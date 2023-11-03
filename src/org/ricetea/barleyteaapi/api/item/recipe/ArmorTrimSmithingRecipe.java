package org.ricetea.barleyteaapi.api.item.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.internal.helper.MaterialHelper;

public class ArmorTrimSmithingRecipe extends BaseSmithingRecipe {

    public ArmorTrimSmithingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original) {
        this(key, original, original);
    }

    public ArmorTrimSmithingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original,
            @Nonnull DataItemType result) {
        super(key, original, result);
    }

    @Nonnull
    public DataItemType getTemplateAsExample() {
        return DataItemType.get(Tag.ITEMS_TRIM_TEMPLATES.getValues().iterator().next());
    }

    @Nonnull
    public DataItemType getAdditionAsExample() {
        return DataItemType.get(Tag.ITEMS_TRIM_MATERIALS.getValues().iterator().next());
    }

    @Override
    public boolean filterAdditionType(@Nonnull DataItemType additionType) {
        Material type = additionType.left();
        return type != null && Tag.ITEMS_TRIM_MATERIALS.isTagged(type);
    }

    @Override
    public boolean filterTemplateType(@Nonnull DataItemType templateType) {
        Material type = templateType.left();
        return type != null && Tag.ITEMS_TRIM_TEMPLATES.isTagged(type);
    }

    @Override
    public ItemStack apply(@Nonnull ItemStack original, @Nonnull ItemStack template, @Nonnull ItemStack material) {
        ItemStack result = super.apply(original, template, material);
        if (result != null && result.getItemMeta() instanceof ArmorMeta armorMeta) {
            armorMeta.setTrim(getArmorTrim(template, material));
            result.setItemMeta(armorMeta);
        }
        return result;
    }

    private static ArmorTrim getArmorTrim(@Nonnull ItemStack template, @Nonnull ItemStack material) {
        TrimMaterial trimMaterial = getTrimMaterial(material.getType());
        if (trimMaterial != null) {
            TrimPattern trimPattern = getTrimPattern(template.getType());
            if (trimPattern != null) {
                return new ArmorTrim(trimMaterial, trimPattern);
            }
        }
        return null;
    }

    @Nullable
    public static TrimMaterial getTrimMaterial(@Nullable Material material) {
        if (material == null)
            return null;
        switch (material) {
            case QUARTZ:
                return TrimMaterial.QUARTZ;
            case IRON_INGOT:
                return TrimMaterial.IRON;
            case NETHERITE_INGOT:
                return TrimMaterial.NETHERITE;
            case REDSTONE:
                return TrimMaterial.REDSTONE;
            case COPPER_INGOT:
                return TrimMaterial.COPPER;
            case GOLD_INGOT:
                return TrimMaterial.GOLD;
            case EMERALD:
                return TrimMaterial.EMERALD;
            case DIAMOND:
                return TrimMaterial.DIAMOND;
            case LAPIS_LAZULI:
                return TrimMaterial.LAPIS;
            case AMETHYST_SHARD:
                return TrimMaterial.AMETHYST;
            default:
                return null;
        }
    }

    @Nullable
    public static TrimPattern getTrimPattern(@Nullable Material material) {
        if (material == null)
            return null;
        switch (material) {
            case COAST_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.COAST;
            case DUNE_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.DUNE;
            case EYE_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.EYE;
            case HOST_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.HOST;
            case RAISER_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.RAISER;
            case RIB_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.RIB;
            case SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.SENTRY;
            case SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.SHAPER;
            case SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.SILENCE;
            case SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.SNOUT;
            case SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.SPIRE;
            case TIDE_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.TIDE;
            case VEX_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.VEX;
            case WARD_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.WARD;
            case WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.WAYFINDER;
            case WILD_ARMOR_TRIM_SMITHING_TEMPLATE:
                return TrimPattern.WILD;
            default:
                return MaterialHelper.getTrimPatternByMaterial(material);
        }
    }

    @Nonnull
    public SmithingTransformRecipe toBukkitRecipe(NamespacedKey key) {
        return new SmithingTransformRecipe(key, new ItemStack(getResult().getMaterialBasedOn()),
                new MaterialChoice(Tag.ITEMS_TRIM_TEMPLATES), new MaterialChoice(getOriginal().getMaterialBasedOn()),
                new MaterialChoice(Tag.ITEMS_TRIM_MATERIALS), true);
    }
}
