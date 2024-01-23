package org.ricetea.barleyteaapi.internal.connector;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.localization.LocalizationRegister;
import org.ricetea.barleyteaapi.api.localization.LocalizedMessageFormat;
import org.ricetea.barleyteaapi.util.connector.SoftDependConnector;
import org.ricetea.utils.ObjectUtil;
import org.ricetea.utils.StringHelper;
import su.nightexpress.excellentenchants.enchantment.impl.ExcellentEnchant;
import su.nightexpress.excellentenchants.enchantment.registry.EnchantRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class ExcellentEnchantsConnector implements SoftDependConnector {

    private final Map<String, Style> colorPrefixDictionary = new HashMap<>();

    private final Set<LocalizedMessageFormat> formatSet = Collections.newSetFromMap(new WeakHashMap<>());

    @Override
    public void onEnable(@Nonnull Plugin plugin) {
        LocalizationRegister register = LocalizationRegister.getInstance();
        for (Entry<NamespacedKey, ExcellentEnchant> enchKVPair : EnchantRegistry.REGISTRY_MAP.entrySet()) {
            NamespacedKey key = enchKVPair.getKey();
            LocalizedMessageFormat format = LocalizedMessageFormat.create(
                    StringHelper.joinWithoutNull(".", "enchantment", key.getNamespace(), key.getKey()));
            format.setFormat(new MessageFormat(enchKVPair.getValue().getDisplayName()));
            formatSet.add(format);
            register.register(format);
        }
    }

    @Override
    public void onDisable() {
        LocalizationRegister register = LocalizationRegister.getInstanceUnsafe();
        if (register == null)
            return;
        register.unregisterAll(formatSet::contains);
        formatSet.clear();
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
