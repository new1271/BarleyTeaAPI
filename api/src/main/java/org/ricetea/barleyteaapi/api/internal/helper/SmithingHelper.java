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
        {
            ItemMeta originalMeta = original.getItemMeta();
            ItemMeta resultMeta = result.getItemMeta();
            copyAttributes(originalMeta, resultMeta);
            copyEnchants(originalMeta, resultMeta);
            result.setItemMeta(resultMeta);
        }
        if (helper != null) {
            NMSVersion nmsVersion = NMSVersion.getCurrent();
            if (nmsVersion.getVersion() < NMSVersion.v1_20_R4.getVersion()) {
                result = helper.copyNbt(original, result,
                        "PublicBukkitValues", "AttributeModifiers", "Enchantments",
                        "Damage", "CustomModelData");
                result = helper.mergeNbt(original, result, "PublicBukkitValues");
            }
            else{
                result = helper.copyNbt(original, result,
                        "custom_data.PublicBukkitValues", "attribute_modifiers", "enchantments",
                        "damage", "custom_model_data");
                result = helper.mergeNbt(original, result, "custom_data.PublicBukkitValues");
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

    private static void copyAttributes(@Nullable ItemMeta original, @Nullable ItemMeta result) {
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
