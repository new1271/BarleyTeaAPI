package org.ricetea.barleyteaapi.internal.nms.v1_20_R4.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.internal.nms.INBTItemHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R4.helper.NBTItemHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R4.helper.NBTTagCompoundHelper;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class NBTItemHelperImpl implements INBTItemHelper {
    private static final NBTItemHelperImpl _inst = new NBTItemHelperImpl();

    @Nonnull
    public static NBTItemHelperImpl getInstance() {
        return _inst;
    }

    @Nonnull
    @Override
    public ItemStack copyNbt(@Nonnull ItemStack original, @Nonnull ItemStack result,
                             @Nullable String... tagBlacklist) {
        var componentMapOfOriginal = NBTItemHelper.getComponentMap(original);
        var componentMapOfResult = NBTItemHelper.getComponentMap(result);
        if (tagBlacklist != null && tagBlacklist.length > 0) {
            var builder = DataComponentMap.builder()
                    .addAll(componentMapOfOriginal);
            Registry<DataComponentType<?>> registry = BuiltInRegistries.DATA_COMPONENT_TYPE;
            boolean isDirty = false;
            DataComponentMap finalComponentMapOfOriginal = componentMapOfOriginal;
            Lazy<CompoundTag> customDataLazy = Lazy.create(() -> {
                CustomData data = finalComponentMapOfOriginal.get(DataComponents.CUSTOM_DATA);
                if (data == null)
                    return new CompoundTag();
                return data.copyTag();
            });
            for (String tag : tagBlacklist) {
                String[] paths = tag.split("\\.");
                int pathLength = paths.length;
                if (pathLength == 0)
                    continue;
                String headerPath = paths[0];
                DataComponentType<?> type;
                ResourceLocation location = ResourceLocation.tryParse(headerPath);
                if (location == null) {
                    continue;
                }
                type = registry.get(location);
                if (type == null) {
                    continue;
                }
                if (pathLength == 1) {
                    isDirty = true;
                    builder.set(type, null);
                } else if (Objects.equals(type, DataComponents.CUSTOM_DATA)) {
                    CompoundTag originalNode = customDataLazy.get();
                    if (originalNode.isEmpty())
                        continue;
                    CompoundTag currentCompoundNode = originalNode;
                    for (int i = 1, length = paths.length; i < length; i++) {
                        if (currentCompoundNode == null)
                            break;
                        String path = paths[i];
                        if (i == length - 1) {
                            currentCompoundNode.remove(path);
                            isDirty = true;
                            break;
                        }
                        currentCompoundNode = ObjectUtil.tryCast(currentCompoundNode.get(path), CompoundTag.class);
                    }
                }
            }
            if (isDirty) {
                CompoundTag tag = customDataLazy.getUnsafe();
                if (tag != null)
                    builder.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                componentMapOfOriginal = builder.build();
            }
        }
        return NBTItemHelper.setComponentMap(result,
                NBTTagCompoundHelper.merge(componentMapOfResult, componentMapOfOriginal));
    }

    @Nonnull
    @Override
    public ItemStack mergeNbt(@Nonnull ItemStack original, @Nonnull ItemStack result, @Nullable String... tags) {
        var componentMapOfOriginal = NBTItemHelper.getComponentMap(original);
        var componentMapOfResult = NBTItemHelper.getComponentMap(result);
        if (tags != null && tags.length > 0) {
            return NBTItemHelper.setComponentMap(
                    result,
                    DataComponentMap.composite(
                            componentMapOfResult,
                            new FilterOnlyDataComponentMap(
                                    componentMapOfOriginal,
                                    componentMapOfResult.get(DataComponents.CUSTOM_DATA),
                                    tags)
                    ));
        }
        return result;
    }

    @Nonnull
    @Override
    public ItemStack setNbt(@Nonnull ItemStack original, @Nonnull String nbt) {
        DataComponentMap map;
        try {
            map = NBTItemHelper.toComponentMap(TagParser.parseTag(nbt));
        } catch (CommandSyntaxException ignored) {
            return original;
        }
        return NBTItemHelper.setComponentMap(original, map);
    }

    private static class FilterOnlyDataComponentMap implements DataComponentMap {

        @Nonnull
        private final DataComponentMap map;

        @Nonnull
        private final Set<ResourceLocation> headerTags;

        @Nonnull
        private final Set<String> customDataTags;
        @Nullable
        private final CustomData targetData;

        public FilterOnlyDataComponentMap(@Nonnull DataComponentMap map, @Nullable CustomData targetData, @Nonnull String[] tags) {
            this.map = map;
            this.targetData = targetData;
            Lazy<Set<ResourceLocation>> headers = Lazy.create(HashSet::new);
            Lazy<Set<String>> customDataTags = Lazy.create(HashSet::new);
            ResourceLocation customDataResourceLocation = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(DataComponents.CUSTOM_DATA);
            for (String tag : tags) {
                int indexOf = tag.indexOf('.');
                String header, value;
                if (indexOf < 0) {
                    header = tag;
                    value = null;
                } else {
                    header = tag.substring(0, indexOf);
                    value = tag.substring(indexOf + 1);
                }
                ResourceLocation loc = ResourceLocation.tryParse(header);
                if (loc == null)
                    continue;
                if (customDataResourceLocation != null && value != null &&
                        Objects.equals(loc, customDataResourceLocation)) {
                    customDataTags.get().add(value);
                } else {
                    headers.get().add(loc);
                }
            }
            headerTags = ObjectUtil.letNonNull(
                    ObjectUtil.safeMap(headers.getUnsafe(), Collections::unmodifiableSet),
                    Collections::emptySet
            );
            this.customDataTags = ObjectUtil.letNonNull(
                    ObjectUtil.safeMap(customDataTags.getUnsafe(), Collections::unmodifiableSet),
                    Collections::emptySet
            );
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T get(@Nonnull DataComponentType<? extends T> type) {
            ResourceLocation location = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type);
            if (location != null && !headerTags.contains(location))
                return null;
            T result = map.get(type);
            if (result instanceof CustomData data && !customDataTags.isEmpty()) {
                final CompoundTag originalNode = data.copyTag();
                final CompoundTag targetNode = ObjectUtil.letNonNull(
                        ObjectUtil.safeMap(targetData, CustomData::copyTag),
                        CompoundTag::new
                );
                for (String tag : customDataTags) {
                    CompoundTag currentOriginalNode = originalNode;
                    CompoundTag currentTargetNode = targetNode;
                    String[] paths = tag.split(Pattern.quote("."));
                    for (int i = 0, length = paths.length; i < length; i++) {
                        if (currentOriginalNode == null || currentTargetNode == null)
                            break;
                        String path = paths[i];
                        if (i == length - 1) {
                            merge(currentTargetNode, currentOriginalNode);
                            break;
                        } else if (currentOriginalNode.contains(path)) {
                            currentOriginalNode = ObjectUtil.tryCast(currentOriginalNode.get(path), CompoundTag.class);
                            currentTargetNode = ObjectUtil.tryCast(currentTargetNode.get(path), CompoundTag.class);
                        } else {
                            currentOriginalNode = null;
                            currentTargetNode = null;
                        }
                    }
                }
                result = (T) CustomData.of(targetNode);
            }
            return result;
        }

        @Override
        @Nonnull
        public Set<DataComponentType<?>> keySet() {
            Set<DataComponentType<?>> result = map.keySet();
            if (result.isEmpty() || headerTags.isEmpty())
                return result;
            var registry = BuiltInRegistries.DATA_COMPONENT_TYPE;
            return result.stream()
                    .filter(val -> {
                        ResourceLocation location = registry.getKey(val);
                        return location == null || !headerTags.contains(location);
                    }).collect(Collectors.toUnmodifiableSet());
        }

        private void merge(@Nonnull CompoundTag node, @Nonnull CompoundTag node2) {
            for (String tagKey : node2.getAllKeys()) {
                Tag tag = node.get(tagKey);
                if (tag == null) {
                    node.put(tagKey, Objects.requireNonNull(node2.get(tagKey)));
                } else if (tag.getId() == 10 && tag instanceof CompoundTag compoundTag &&
                        node2.contains(tagKey, 10)) { //Compound Type
                    merge(compoundTag, node2.getCompound(tagKey));
                }
            }
        }
    }
}
