package org.ricetea.barleyteaapi.api.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.CustomObject;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CustomItem extends CustomObject {

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
}
