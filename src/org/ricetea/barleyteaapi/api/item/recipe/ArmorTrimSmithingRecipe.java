package org.ricetea.barleyteaapi.api.item.recipe;

import java.util.Set;
import java.util.stream.Collectors;

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
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.internal.helper.MaterialHelper;
import org.ricetea.barleyteaapi.internal.helper.SmithingHelper;
import org.ricetea.utils.Lazy;

public class ArmorTrimSmithingRecipe extends BaseSmithingRecipe {

    private static final Lazy<Set<DataItemType>> templateSetLazy = Lazy.createInThreadSafe(() -> {
        return Tag.ITEMS_TRIM_TEMPLATES.getValues()
                .stream()
                .map(DataItemType::get)
                .collect(Collectors.toSet());
    });

    private static final Lazy<Set<DataItemType>> additionSetLazy = Lazy.createInThreadSafe(() -> {
        return Tag.ITEMS_TRIM_MATERIALS.getValues()
                .stream()
                .map(DataItemType::get)
                .collect(Collectors.toSet());
    });

    private final boolean copyNbt;

    public ArmorTrimSmithingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original) {
        this(key, original, original);
    }

    public ArmorTrimSmithingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original,
            @Nonnull DataItemType result) {
        this(key, original, result, true);
    }

    public ArmorTrimSmithingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original,
            @Nonnull DataItemType result, boolean copyNbt) {
        super(key, original, result);
        this.copyNbt = copyNbt;
    }

    @Nonnull
    @Override
    public Set<DataItemType> getTemplates() {
        return templateSetLazy.get();
    }

    @Nonnull
    @Override
    public Set<DataItemType> getAdditions() {
        return additionSetLazy.get();
    }

    @Override
    public ItemStack apply(@Nonnull ItemStack original, @Nonnull ItemStack template, @Nonnull ItemStack material) {
        ItemStack result;
        if (copyNbt) {
            if (getOriginal() == getResult()) {
                result = original.clone();
            } else {
                result = SmithingHelper.copyNbt(this, original, super.apply(original, template, material));
            }
        } else {
            result = super.apply(original, template, material);
        }
        if (result != null && result.getItemMeta() instanceof ArmorMeta armorMeta) {
            armorMeta.setTrim(getArmorTrim(BaseItem.getItemType(template), BaseItem.getItemType(material)));
            result.setItemMeta(armorMeta);
        }
        return result;
    }

    private ArmorTrim getArmorTrim(@Nonnull DataItemType templateType, @Nonnull DataItemType materialType) {
        TrimMaterial trimMaterial = getTrimMaterial(materialType);
        if (trimMaterial != null) {
            TrimPattern trimPattern = getTrimPattern(templateType);
            if (trimPattern != null) {
                return new ArmorTrim(trimMaterial, trimPattern);
            }
        }
        return null;
    }

    @Nullable
    protected TrimMaterial getTrimMaterial(@Nullable DataItemType itemType) {
        if (itemType == null)
            return null;
        Material material = itemType.asMaterial();
        if (material == null) {
            return null;
        } else {
            return switch (material) {
                case QUARTZ -> TrimMaterial.QUARTZ;
                case IRON_INGOT -> TrimMaterial.IRON;
                case NETHERITE_INGOT -> TrimMaterial.NETHERITE;
                case REDSTONE -> TrimMaterial.REDSTONE;
                case COPPER_INGOT -> TrimMaterial.COPPER;
                case GOLD_INGOT -> TrimMaterial.GOLD;
                case EMERALD -> TrimMaterial.EMERALD;
                case DIAMOND -> TrimMaterial.DIAMOND;
                case LAPIS_LAZULI -> TrimMaterial.LAPIS;
                case AMETHYST_SHARD -> TrimMaterial.AMETHYST;
                default -> null;
            };
        }
    }

    @Nullable
    protected TrimPattern getTrimPattern(@Nullable DataItemType itemType) {
        if (itemType == null)
            return null;
        Material material = itemType.asMaterial();
        if (material == null) {
            return null;
        } else {
            return switch (material) {
                case COAST_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.COAST;
                case DUNE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.DUNE;
                case EYE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.EYE;
                case HOST_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.HOST;
                case RAISER_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.RAISER;
                case RIB_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.RIB;
                case SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SENTRY;
                case SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SHAPER;
                case SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SILENCE;
                case SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SNOUT;
                case SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SPIRE;
                case TIDE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.TIDE;
                case VEX_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.VEX;
                case WARD_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.WARD;
                case WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.WAYFINDER;
                case WILD_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.WILD;
                default -> MaterialHelper.getTrimPatternByMaterial(material);
            };
        }
    }

    @Nonnull
    public SmithingTransformRecipe toBukkitRecipe(@Nonnull NamespacedKey key) {
        return new SmithingTransformRecipe(key, new ItemStack(getResult().getMaterialBasedOn()),
                new MaterialChoice(Tag.ITEMS_TRIM_TEMPLATES), new MaterialChoice(getOriginal().getMaterialBasedOn()),
                new MaterialChoice(Tag.ITEMS_TRIM_MATERIALS), true);
    }
}
