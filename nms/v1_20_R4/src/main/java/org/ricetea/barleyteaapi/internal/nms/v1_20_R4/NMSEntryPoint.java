package org.ricetea.barleyteaapi.internal.nms.v1_20_R4;

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
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicesManager;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.command.CommandRegister;
import org.ricetea.barleyteaapi.api.event.EntitiesRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.EntitiesUnregisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsUnregisteredEvent;
import org.ricetea.barleyteaapi.api.internal.nms.*;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R4.command.NMSCommandRegisterImpl;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R4.command.NMSGiveCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R4.command.NMSRegularCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R4.command.NMSSummonCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R4.helper.NBTItemHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R4.helper.NBTTagCompoundHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R4.helper.NMSEntityHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R4.impl.NMSItemHelper2Impl;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R4.impl.NMSItemHelperImpl;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class NMSEntryPoint implements Listener, INMSEntryPoint {

    @Nullable
    private NMSRegularCommand summonCommand, giveCommand;

    @Nonnull
    private final BarleyTeaAPI apiInst;

    public NMSEntryPoint(@Nonnull BarleyTeaAPI apiInst) {
        this.apiInst = apiInst;
    }

    @Override
    public NMSVersion getNMSVersion() {
        return NMSVersion.v1_20_R4;
    }

    public void onEnable() {
        Logger logger = apiInst.getLogger();
        logger.info("[NMS] registering command register...");
        NMSCommandRegisterImpl commandRegister = new NMSCommandRegisterImpl();
        CommandRegister.setInstance(commandRegister, NMSCommandRegisterImpl.class);
        logger.info("[NMS] registering '/givebarley' command...");
        commandRegister.register(giveCommand = new NMSGiveCommand());
        logger.info("[NMS] registering '/summonbarley' command...");
        commandRegister.register(summonCommand = new NMSSummonCommand());
        logger.info("[NMS] registering nms implementation...");
        loadNmsImplementations();
        Bukkit.getPluginManager().registerEvents(this, apiInst);
    }

    private void loadNmsImplementations() {
        ServicesManager servicesManager = Bukkit.getServicesManager();
        apiInst.loadApiImplementation(servicesManager, NMSEntityHelper::damage, INMSEntityHelper.class);
        apiInst.loadApiImplementation(servicesManager, NMSItemHelperImpl.getInstance(), INMSItemHelper.class);
        apiInst.loadApiImplementation(servicesManager, NMSItemHelper2Impl.getInstance(), INMSItemHelper2.class);
        apiInst.loadApiImplementation(servicesManager, new INBTItemHelper() {
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
                    for (String tag : tagBlacklist) {
                        String[] paths = tag.split("\\.");
                        int pathLength = paths.length;
                        if (pathLength == 0)
                            continue;
                        String headerPath = paths[0];
                        DataComponentType<?> type;
                        try {
                            ResourceLocation location = new ResourceLocation(headerPath);
                            type = registry.get(location);
                        } catch (Exception ex) {
                            continue;
                        }
                        if (type == null)
                            continue;
                        if (pathLength == 1) {
                            builder.set(type, null);
                        } else if (Objects.equals(type, DataComponents.CUSTOM_DATA)) {
                            CustomData data = componentMapOfOriginal.get(DataComponents.CUSTOM_DATA);
                            if (data != null) {
                                final CompoundTag originalNode = data.copyTag();
                                CompoundTag currentCompoundNode = originalNode;
                                for (int i = 0, length = paths.length; i < length; i++) {
                                    if (currentCompoundNode == null)
                                        break;
                                    String path = paths[i];
                                    if (i == length - 1) {
                                        currentCompoundNode.remove(path);
                                        break;
                                    } else if (currentCompoundNode.contains(path)) {
                                        currentCompoundNode = ObjectUtil.tryCast(currentCompoundNode.get(path), CompoundTag.class);
                                    } else {
                                        currentCompoundNode = null;
                                    }
                                }
                                builder.set(DataComponents.CUSTOM_DATA, CustomData.of(originalNode));
                            }
                        }
                    }
                }
                return NBTItemHelper.setComponentMap(result, NBTTagCompoundHelper.merge(componentMapOfResult, componentMapOfOriginal));
            }

            @Nonnull
            @Override
            public ItemStack mergeNbt(@Nonnull ItemStack original, @Nonnull ItemStack result, @Nullable String... tags) {
                var componentMapOfOriginal = NBTItemHelper.getComponentMap(original);
                var componentMapOfResult = NBTItemHelper.getComponentMap(result);
                if (tags != null && tags.length > 0) {
                    var builder = DataComponentMap.builder()
                            .addAll(componentMapOfResult)
                            .addAll(new FilterOnlyDataComponentMap(
                                    componentMapOfOriginal,
                                    componentMapOfResult.get(DataComponents.CUSTOM_DATA),
                                    tags));
                    return NBTItemHelper.setComponentMap(result, builder.build());
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

        }, INBTItemHelper.class);
    }

    public void onDisable() {
        Logger logger = apiInst.getLogger();
        logger.info("unregistering command register...");
        NMSCommandRegisterImpl commandRegister = CommandRegister.getInstanceUnsafe(NMSCommandRegisterImpl.class);
        if (commandRegister != null) {
            commandRegister.unregisterAll();
            CommandRegister.setInstance(null, NMSCommandRegisterImpl.class);
        }
    }

    @EventHandler
    public void listenEntitiesRegistered(EntitiesRegisteredEvent event) {
        if (event == null)
            return;
        summonCommand.updateSuggestions();
    }

    @EventHandler
    public void listenEntitiesUnregistered(EntitiesUnregisteredEvent event) {
        if (event == null)
            return;
        summonCommand.updateSuggestions();
    }

    @EventHandler
    public void listenItemsRegistered(ItemsRegisteredEvent event) {
        if (event == null)
            return;
        giveCommand.updateSuggestions();
    }

    @EventHandler
    public void listenItemsUnregistered(ItemsUnregisteredEvent event) {
        if (event == null)
            return;
        giveCommand.updateSuggestions();
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
                if (customDataResourceLocation != null && value != null &&
                        header.equalsIgnoreCase(customDataResourceLocation.getPath())) {
                    customDataTags.get().add(value);
                } else {
                    try {
                        headers.get().add(new ResourceLocation(header));
                    } catch (Exception ignored) {
                    }
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
            if (location == null || headerTags.contains(location))
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
