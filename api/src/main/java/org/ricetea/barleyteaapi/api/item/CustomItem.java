package org.ricetea.barleyteaapi.api.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.CustomObject;
import org.ricetea.barleyteaapi.api.item.feature.ItemFeature;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public interface CustomItem extends CustomObject<ItemFeature> {

    @Nullable
    static CustomItem get(@Nullable ItemStack itemStack) {
        if (itemStack == null || ItemHelper.isEmpty(itemStack))
            return null;
        else {
            ItemRegister register = ItemRegister.getInstanceUnsafe();
            if (register == null)
                return null;
            return register.lookup(ItemHelper.getItemID(itemStack));
        }
    }

    @Nonnull
    Material getOriginalType();

    @Nonnull
    CustomItemRarity getRarity();

    boolean isTool();

    boolean isRarityUpgraded(@Nonnull ItemStack itemStack);

    @Nonnull
    default CustomItemType getType() {
        return CustomItemType.get(this);
    }

    @Nullable
    default <T extends ItemFeature> T getFeature(@Nonnull Class<T> featureClass) {
        return ObjectUtil.tryCast(this, featureClass);
    }

    @Nonnull
    default Collection<Class<? extends ItemFeature>> getFeatures() {
        Class<?>[] interfaces = getClass().getInterfaces();
        ArrayList<Class<? extends ItemFeature>> result = new ArrayList<>(interfaces.length);
        for (Class<?> _interface : interfaces) {
            try {
                Class<? extends ItemFeature> castedInterface = _interface.asSubclass(ItemFeature.class);
                result.add(castedInterface);
            } catch (Exception ignored) {

            }
        }
        return result.stream().collect(Collectors.toUnmodifiableSet());
    }
}
