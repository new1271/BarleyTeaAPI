package org.ricetea.barleyteaapi.internal.helper;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.recipe.BaseSmithingRecipe;
import org.ricetea.barleyteaapi.internal.nms.INBTItemHelper;
import org.ricetea.barleyteaapi.internal.nms.NMSHelperRegister;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SmithingHelper {
    @Nullable
    public static ItemStack copyNbt(@Nonnull BaseSmithingRecipe recipe, @Nonnull ItemStack original,
                                    @Nullable ItemStack result) {
        if (result == null)
            return null;
        int damage = ObjectUtil.letNonNull(
                recipe.getOriginal().map(
                        left -> {
                            if (original.getItemMeta() instanceof Damageable meta) {
                                return meta.getDamage();
                            }
                            return 0;
                        },
                        right -> {
                            if (right instanceof FeatureItemCustomDurability feature) {
                                return feature.getDurabilityDamage(original);
                            }
                            return 0;
                        }),
                0);
        INBTItemHelper helper = NMSHelperRegister.getHelper(INBTItemHelper.class);
        if (helper != null) {
            result = helper.copyNbt(result, original);
            if (damage > 0) {
                DataItemType resultType = recipe.getResult();
                if (resultType.isVanilla()) {
                    if (result.getItemMeta() instanceof Damageable meta) {
                        meta.setDamage(damage);
                        result.setItemMeta(meta);
                    }
                } else if (resultType.asCustomItem() instanceof FeatureItemCustomDurability feature) {
                    feature.setDurabilityDamage(result, damage);
                }
            }
            return result;
        }
        return null;
    }
}
