package org.ricetea.barleyteaapi.internal.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.recipe.BaseSmithingRecipe;
import org.ricetea.barleyteaapi.internal.nms.helper.NBTItemHelper;
import org.ricetea.barleyteaapi.internal.nms.helper.NBTTagCompoundHelper;
import org.ricetea.utils.ObjectUtil;

import net.kyori.adventure.text.Component;

public final class SmithingHelper {
    @Nullable
    public static ItemStack copyNbt(@Nonnull BaseSmithingRecipe recipe, @Nonnull ItemStack original,
            @Nullable ItemStack result) {
        if (result == null)
            return null;
        Component displayName = BaseItem.getDisplayName(original);
        if (displayName == null) {
            ItemMeta meta = original.getItemMeta();
            if (meta != null) {
                meta.displayName(null);
                original.setItemMeta(meta);
            }
        }
        int damage = ObjectUtil.letNonNull(
                recipe.getOriginal().mapLeftOrRight(
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
        var compound = NBTItemHelper.getNBT(original);
        var compound2 = NBTItemHelper.getNBT(result);
        result = NBTItemHelper.setNBT(result, NBTTagCompoundHelper.merge(compound, compound2));
        if (displayName != null)
            BaseItem.setDisplayName(result, displayName);
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
}
