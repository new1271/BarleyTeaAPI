package org.ricetea.barleyteaapi.internal.nms.v1_20_R4.helper;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class NBTItemHelper {
    @Nonnull
    public static DataComponentMap getComponentMap(@Nonnull org.bukkit.inventory.ItemStack itemStack) {
        return getComponentMap(Objects.requireNonNull(CraftItemStack.asNMSCopy(itemStack)));
    }

    @Nonnull
    public static DataComponentMap getComponentMap(@Nonnull net.minecraft.world.item.ItemStack itemStack) {
        // net.minecraft.nbt.CompoundTag getTag() -> u
        return Objects.requireNonNull(itemStack.getComponents());
    }

    @Nonnull
    public static net.minecraft.world.item.ItemStack setComponentMap(@Nonnull net.minecraft.world.item.ItemStack itemStack,
                                                                     DataComponentMap map) {
        // void setTag(net.minecraft.nbt.CompoundTag) -> c
        itemStack.applyComponents(map);
        return itemStack;
    }

    @Nonnull
    public static org.bukkit.inventory.ItemStack setComponentMap(@Nonnull org.bukkit.inventory.ItemStack itemStack,
                                                                 DataComponentMap map) {
        return Objects.requireNonNull(setComponentMap(Objects.requireNonNull(CraftItemStack.asNMSCopy(itemStack)), map).getBukkitStack());
    }

    @Nonnull
    public static org.bukkit.inventory.ItemStack castBukkitItemStack(@Nonnull net.minecraft.world.item.ItemStack stack) {
        return Objects.requireNonNull(CraftItemStack.asCraftMirror(stack));
    }

    @Nullable
    public static DataComponentMap toComponentMap(@Nullable CompoundTag tag) {
        if (tag == null)
            return null;
        if (tag.isEmpty())
            return DataComponentMap.EMPTY;
        Map<DataComponentType<?>, Object> map = new HashMap<>();
        DataComponentType<CustomData> customDataType = DataComponents.CUSTOM_DATA;
        Lazy<CompoundTag> customNbtTagLazy = Lazy.create(CompoundTag::new);
        var registry = BuiltInRegistries.DATA_COMPONENT_TYPE;
        for (String key : tag.getAllKeys()) {
            Tag value = tag.get(key);
            if (value == null)
                continue;
            ResourceLocation location = ObjectUtil.tryMapSilently(key, ResourceLocation::new);
            if (location == null) {
                customNbtTagLazy.get().put(key, value);
                continue;
            }
            DataComponentType<?> type = registry.get(location);
            if (type == null) {
                customNbtTagLazy.get().put(key, value);
                continue;
            }
            if (type == customDataType && value instanceof CompoundTag compound) {
                customNbtTagLazy.get().merge(compound);
                continue;
            }
            Codec<?> codec = type.codec();
            if (codec == null)
                continue;
            Pair<?, Tag> decodedValue = ObjectUtil.tryMapSilently(() -> codec.decode(NbtOps.INSTANCE, value).getOrThrow());
            if (decodedValue != null)
                map.put(type, decodedValue.getFirst());
        }
        CompoundTag customNbtTag = customNbtTagLazy.getUnsafe();
        if (customNbtTag != null) {
            map.put(customDataType, CustomData.of(customNbtTag));
        }
        return new ResultMap(map);
    }

    private record ResultMap(@Nonnull Map<DataComponentType<?>, Object> map) implements DataComponentMap {

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T get(@Nonnull DataComponentType<? extends T> type) {
            return (T) map.get(type);
        }

        @Nonnull
        @Override
        public Set<DataComponentType<?>> keySet() {
            return map.keySet();
        }
    }
}
