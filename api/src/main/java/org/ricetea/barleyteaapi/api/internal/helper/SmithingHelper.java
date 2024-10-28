package org.ricetea.barleyteaapi.api.internal.helper;

import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.ricetea.barleyteaapi.api.helper.FeatureHelper;
import org.ricetea.barleyteaapi.api.internal.nms.INBTItemHelper;
import org.ricetea.barleyteaapi.api.internal.nms.NMSVersion;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.recipe.BaseSmithingRecipe;
import org.ricetea.utils.Box;
import org.ricetea.utils.Constants;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public final class SmithingHelper {
    @Nullable
    public static ItemStack copyNbt(@Nonnull BaseSmithingRecipe recipe, @Nonnull ItemStack original,
                                    @Nullable ItemStack result) {
        if (result == null)
            return null;
        int damage = ObjectUtil.letNonNull(
                recipe.getOriginal().map(
                        left ->
                                ObjectUtil.safeMap(
                                        ObjectUtil.tryCast(original.getItemMeta(), Damageable.class),
                                        Damageable::getDamage
                                ),
                        right ->
                                FeatureHelper.mapIfHasFeature(
                                        right,
                                        FeatureItemCustomDurability.class,
                                        feature -> feature.getDurabilityDamage(original)
                                )
                ),
                0);
        INBTItemHelper helper = Bukkit.getServicesManager().load(INBTItemHelper.class);
        boolean isNewerThan1_20_R3 = NMSVersion.getCurrent().getVersion() > NMSVersion.v1_20_R3.getVersion();
        {
            ItemMeta originalMeta = original.getItemMeta();
            ItemMeta resultMeta = result.getItemMeta();
            if (isNewerThan1_20_R3) {
                copyAttributes(originalMeta, resultMeta);
            } else {
                copyAttributesLegacy(originalMeta, resultMeta);
            }
            copyEnchants(originalMeta, resultMeta);
            result.setItemMeta(resultMeta);
        }
        if (helper != null) {
            if (isNewerThan1_20_R3) {
                result = helper.copyNbt(original, result,
                        "minecraft:custom_data.PublicBukkitValues", "minecraft:attribute_modifiers",
                        "minecraft:enchantments", "minecraft:damage", "minecraft:custom_model_data");
                result = helper.mergeNbt(original, result, "minecraft:custom_data.PublicBukkitValues");
            } else {
                result = helper.copyNbt(original, result,
                        "PublicBukkitValues", "AttributeModifiers", "Enchantments",
                        "Damage", "CustomModelData");
                result = helper.mergeNbt(original, result, "PublicBukkitValues");
            }
        }
        if (damage > 0) {
            CustomItemType resultType = recipe.getResult();
            if (resultType.isMaterial()) {
                if (result.getItemMeta() instanceof Damageable meta) {
                    meta.setDamage(damage);
                    result.setItemMeta(meta);
                }
            } else {
                ItemStack finalResult = result;
                FeatureHelper.callIfHasFeature(
                        resultType.asCustomItem(),
                        FeatureItemCustomDurability.class,
                        feature -> feature.setDurabilityDamage(finalResult, damage)
                );
            }
        }
        return result;
    }

    private static void copyAttributesLegacy(@Nullable ItemMeta original, @Nullable ItemMeta result) {
        if (original == null || result == null)
            return;
        Multimap<Attribute, AttributeModifier> attributes = original.getAttributeModifiers();
        if (attributes == null)
            return;
        Box<Boolean> flagBox = Box.box(false);
        attributes.forEach((attribute, attributeModifier) -> {
            if (attributeModifier.getName().equals(Constants.DEFAULT_ATTRIBUTE_MODIFIER_NAME))
                return;
            if (Boolean.FALSE.equals(flagBox.exchange(true))) {
                for (Attribute _attribute : Attribute.values())
                    result.removeAttributeModifier(_attribute);
            }
            result.addAttributeModifier(attribute, attributeModifier);
        });
    }

    private static void copyAttributes(@Nullable ItemMeta original, @Nullable ItemMeta result) {
        if (original == null || result == null)
            return;
        Multimap<Attribute, AttributeModifier> attributes = original.getAttributeModifiers();
        if (attributes == null)
            return;
        Box<Boolean> flagBox = Box.box(false);
        attributes.forEach((attribute, attributeModifier) -> {
            String modName = attributeModifier.getName();
            if (modName.startsWith(Constants.DEFAULT_ATTRIBUTE_MODIFIER_KEY_HEADER) ||
                    modName.startsWith("base_"))
                return;
            if (Boolean.FALSE.equals(flagBox.exchange(true))) {
                for (Attribute _attribute : Attribute.values())
                    result.removeAttributeModifier(_attribute);
            }
            result.addAttributeModifier(attribute, attributeModifier);
        });
    }

    private static void copyEnchants(@Nullable ItemMeta original, @Nullable ItemMeta result) {
        if (original == null || result == null)
            return;
        Map<Enchantment, Integer> enchants = original.getEnchants();
        enchants.forEach((enchantment, level) -> {
            if (level == null)
                return;
            int unboxedLevel = level;
            if (result.getEnchantLevel(enchantment) < unboxedLevel) {
                result.addEnchant(enchantment, unboxedLevel, true);
            }
        });
    }
}
