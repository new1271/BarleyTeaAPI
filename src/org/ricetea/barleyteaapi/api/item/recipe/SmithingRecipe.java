package org.ricetea.barleyteaapi.api.item.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.internal.nms.helper.NBTItemHelper;
import org.ricetea.barleyteaapi.internal.nms.helper.NBTTagCompoundHelper;
import org.ricetea.utils.ObjectUtil;

import net.kyori.adventure.text.Component;

public class SmithingRecipe extends BaseSmithingRecipe {

    private final boolean copyNbt;

    @Nonnull
    private final DataItemType template, addition;

    public SmithingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original, @Nonnull DataItemType template,
            @Nonnull DataItemType addition, @Nonnull DataItemType result) {
        this(key, original, template, addition, result, true);
    }

    public SmithingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original, @Nonnull DataItemType template,
            @Nonnull DataItemType addition, @Nonnull DataItemType result, boolean copyNbt) {
        super(key, original, result);
        this.template = template;
        this.addition = addition;
        this.copyNbt = copyNbt;
    }

    @Nonnull
    public DataItemType getTemplateAsExample() {
        return template;
    }

    @Nonnull
    public DataItemType getAdditionAsExample() {
        return addition;
    }

    @Override
    public boolean filterAdditionType(@Nonnull DataItemType additionType) {
        return addition.equals(additionType);
    }

    @Override
    public boolean filterTemplateType(@Nonnull DataItemType templateType) {
        return template.equals(templateType);
    }

    @Nonnull
    public SmithingTransformRecipe toBukkitRecipe(NamespacedKey key) {
        return new SmithingTransformRecipe(key, new ItemStack(getResult().getMaterialBasedOn()),
                new MaterialChoice(getTemplateAsExample().getMaterialBasedOn()), 
                new MaterialChoice(getOriginal().getMaterialBasedOn()),
                new MaterialChoice(getAdditionAsExample().getMaterialBasedOn()), copyNbt);
    }

    @Nullable
    @Override
    public ItemStack apply(@Nonnull ItemStack original, @Nonnull ItemStack template, @Nonnull ItemStack addition) {
        ItemStack result = super.apply(original, template, addition);
        if (result != null && copyNbt) {
            Component displayName = BaseItem.getDisplayName(original);
            if (displayName == null) {
                ItemMeta meta = original.getItemMeta();
                if (meta != null) {
                    meta.displayName(null);
                    original.setItemMeta(meta);
                }
            }
            int damage = ObjectUtil.letNonNull(
                    getOriginal().mapLeftOrRight(
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
                DataItemType resultType = getResult();
                if (resultType.isVanilla()) {
                    if (result.getItemMeta() instanceof Damageable meta) {
                        meta.setDamage(damage);
                        result.setItemMeta(meta);
                    }
                } else if (resultType.asCustomItem() instanceof FeatureItemCustomDurability feature) {
                    feature.setDurabilityDamage(result, damage);
                }
            }
        }
        return result;
    }
}
