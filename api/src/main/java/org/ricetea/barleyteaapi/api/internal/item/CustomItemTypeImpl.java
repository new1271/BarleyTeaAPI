package org.ricetea.barleyteaapi.api.internal.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.utils.Either;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Immutable
@ApiStatus.Internal
public class CustomItemTypeImpl extends Either<Material, CustomItem> implements CustomItemType {
    @Nonnull
    private static final Map<Material, CustomItemType> vanillaMaterialMap = Collections.synchronizedMap(new EnumMap<>(Material.class));
    @Nonnull
    private static final ConcurrentHashMap<CustomItem, CustomItemType> customItemMap = new ConcurrentHashMap<>();

    private CustomItemTypeImpl(@Nullable Material left) {
        super(left, null);
    }

    private CustomItemTypeImpl(@Nullable CustomItem right) {
        super(null, right);
    }

    @Nonnull
    public static CustomItemType get(@Nonnull Material material) {
        return vanillaMaterialMap.computeIfAbsent(material, CustomItemTypeImpl::new);
    }

    @Nonnull
    public static CustomItemType get(@Nonnull CustomItem CustomItem) {
        return customItemMap.computeIfAbsent(CustomItem, CustomItemTypeImpl::new);
    }

    @Nonnull
    public static CustomItemType get(@Nonnull NamespacedKey key) {
        ItemRegister register = ItemRegister.getInstanceUnsafe();
        if (register != null) {
            CustomItem itemType = register.lookup(key);
            if (itemType != null)
                return get(itemType);
        }
        Optional<Material> materialOptional = Arrays.stream(Material.values())
                .filter(material -> material.getKey().equals(key))
                .findAny();
        return materialOptional.map(CustomItemType::get).orElseGet(CustomItemType::empty);
    }

    @Nonnull
    public static CustomItemType get(@Nonnull ItemStack itemStack) {
        CustomItem itemType = CustomItem.get(itemStack);
        return itemType == null ? get(itemStack.getType()) : get(itemType);
    }

    @ApiStatus.Internal
    public static void removeInstances(@Nonnull Collection<CustomItem> blocks) {
        blocks.forEach(customItemMap::remove);
    }

    @Nullable
    @Override
    public Material asMaterial() {
        return left();
    }

    @Nullable
    @Override
    public CustomItem asCustomItem() {
        return right();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean equals(Object another) {
        if (another instanceof CustomItemType itemType)
            return EQUALS.test(this, itemType);
        return super.equals(another);
    }

    @Nonnull
    @Override
    public String toString() {
        return "CustomItemType{" + getKey() + "}";
    }
}
