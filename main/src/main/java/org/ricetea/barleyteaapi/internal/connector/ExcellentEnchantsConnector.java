package org.ricetea.barleyteaapi.internal.connector;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.translation.TranslationRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.i18n.GlobalTranslators;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.barleyteaapi.util.connector.SoftDependConnector;
import org.ricetea.utils.ObjectUtil;
import su.nightexpress.excellentenchants.enchantment.impl.ExcellentEnchant;
import su.nightexpress.excellentenchants.enchantment.registry.EnchantRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class ExcellentEnchantsConnector implements SoftDependConnector {

    private TranslationRegistry reg;

    private final Map<String, Style> colorPrefixDictionary = new HashMap<>();

    @Nonnull
    @Override
    public String getPluginName() {
        return BulitInSoftDepend.ExcellentEnchants.getPluginName();
    }

    @Override
    public void onEnable(@Nonnull Plugin plugin) {
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

    @Override
    public void onDisable() {
        GlobalTranslators translators = GlobalTranslators.getInstance();
        translators.removeServerTranslationSource(reg);
        translators.removeRenderTranslationSource(reg);
    }

    @Nullable
    public String getEnchantmentName(@Nullable Enchantment enchantment) {
        return ObjectUtil.safeMap(ObjectUtil.tryCast(enchantment, ExcellentEnchant.class),
                ExcellentEnchant::getDisplayName);
    }

    @Nonnull
    public String getEnchantmentNameUnsafe(@Nonnull Enchantment enchantment) {
        return ((ExcellentEnchant) enchantment).getDisplayName();
    }

    @Nonnull
    public Style getEnchantmentTierStyleUnsafe(@Nonnull Enchantment enchantment, @Nonnull Supplier<Style> defaultStyleSupplier) {
        Style result = null;
        if (enchantment instanceof ExcellentEnchant excellentEnchant) {
            String colorPrefix = excellentEnchant.getTier().getColor();
            result = colorPrefixDictionary.computeIfAbsent(colorPrefix,
                    this::getStyleFromLegacyFormattingCodes);
        }
        return ObjectUtil.letNonNull(result, defaultStyleSupplier);
    }

    @Nullable
    private Style getStyleFromLegacyFormattingCodes(@Nullable String code) {
        if (code == null)
            return null;
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        try {
            return serializer.deserialize(code + "test")
                    .style()
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        } catch (Exception ignored) {
            return null;
        }
    }

    public boolean isExcellentEnchant(@Nullable Enchantment enchantment) {
        return enchantment instanceof ExcellentEnchant;
    }
}
