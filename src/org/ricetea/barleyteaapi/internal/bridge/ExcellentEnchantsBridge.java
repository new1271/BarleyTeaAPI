package org.ricetea.barleyteaapi.internal.bridge;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.ricetea.barleyteaapi.api.helper.ChatColorHelper;
import org.ricetea.barleyteaapi.api.i18n.GlobalTranslators;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.ObjectUtil;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.translation.TranslationRegistry;
import su.nightexpress.excellentenchants.enchantment.EnchantRegistry;
import su.nightexpress.excellentenchants.enchantment.impl.ExcellentEnchant;

public class ExcellentEnchantsBridge {

    private static TranslationRegistry reg;

    @Nullable
    public static String getEnchantmentName(@Nullable Enchantment enchantment) {
        return ObjectUtil.mapWhenNonnull(ObjectUtil.tryCast(enchantment, ExcellentEnchant.class),
                ExcellentEnchant::getDisplayName);
    }

    @SuppressWarnings("null")
    @Nonnull
    public static String getEnchantmentNameUnsafe(@Nonnull Enchantment enchantment) {
        return ((ExcellentEnchant) enchantment).getDisplayName();
    }

    @Nullable
    public static TextColor getEnchantmentTierColorUnsafe(@Nonnull Enchantment enchantment) {
        return ObjectUtil.mapWhenNonnull(
                (Style) ChatColorHelper.toKyoriStyle(((ExcellentEnchant) enchantment).getTier().getColor()),
                Style::color);
    }

    public static boolean isExcellentEnchant(@Nullable Enchantment enchantment) {
        return enchantment instanceof ExcellentEnchant;
    }

    public static void registerTranslations() {
        reg = TranslationRegistry
                .create(NamespacedKeyUtil.BarleyTeaAPI("excellent_enchant_translation"));
        for (Entry<NamespacedKey, ExcellentEnchant> enchKVPair : EnchantRegistry.REGISTRY_MAP.entrySet()) {
            NamespacedKey key = enchKVPair.getKey();
            reg.register("enchantment." + key.getNamespace() + "." + key.getKey(), Locale.getDefault(),
                    new MessageFormat(enchKVPair.getValue().getDisplayName()));
        }
        GlobalTranslators translators = GlobalTranslators.getInstance();
        translators.addServerTranslationSource(reg);
        translators.addRenderTranslationSource(reg);
    }

    public static void unregisterTranslations() {
        GlobalTranslators translators = GlobalTranslators.getInstance();
        translators.removeServerTranslationSource(reg);
        translators.removeRenderTranslationSource(reg);
    }
}
