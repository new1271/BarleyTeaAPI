package org.ricetea.barleyteaapi.internal.bridge;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map.Entry;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.ricetea.barleyteaapi.api.i18n.GlobalTranslators;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.translation.TranslationRegistry;
import su.nightexpress.excellentenchants.enchantment.EnchantRegistry;
import su.nightexpress.excellentenchants.enchantment.impl.ExcellentEnchant;

public class ExcellentEnchantsBridge {
    public static String getEnchantmentName(Enchantment enchantment) {
        if (enchantment instanceof ExcellentEnchant) {
            return ((ExcellentEnchant) enchantment).getDisplayName();
        }
        return null;
    }

    public static String getEnchantmentNameUnsafe(Enchantment enchantment) {
        return ((ExcellentEnchant) enchantment).getDisplayName();
    }

    public static TextColor getEnchantmentTierColorUnsafe(Enchantment enchantment) {
        return TextColor.color(((ExcellentEnchant) enchantment).getTier().getColor().getColor().getRGB());
    }

    public static boolean isExcellentEnchant(Enchantment enchantment) {
        return enchantment instanceof ExcellentEnchant;
    }

    private static TranslationRegistry reg;

    public static void registerTranslations() {
        reg = TranslationRegistry
                .create(NamespacedKeyUtils.BarleyTeaAPI("excellent_enchant_translation"));
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
