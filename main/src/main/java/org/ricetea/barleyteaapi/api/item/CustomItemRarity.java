package org.ricetea.barleyteaapi.api.item;

import io.papermc.paper.inventory.ItemRarity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.ricetea.barleyteaapi.api.helper.ChatColorHelper;
import org.ricetea.barleyteaapi.internal.item.CustomItemRarityImpl;
import org.ricetea.barleyteaapi.internal.item.VanillaCustomItemRarityImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public interface CustomItemRarity {

    @Nonnull
    static CustomItemRarity fromVanilla(@Nonnull ItemRarity rarity) {
        return switch (rarity) {
            case COMMON -> VanillaCustomItemRarityImpl.COMMON;
            case UNCOMMON -> VanillaCustomItemRarityImpl.UNCOMMON;
            case RARE -> VanillaCustomItemRarityImpl.RARE;
            case EPIC -> VanillaCustomItemRarityImpl.EPIC;
        };
    }

    @Nonnull
    static CustomItemRarity fromVanilla(@Nonnull VanillaItemRarity rarity) {
        return rarity.getRarity();
    }

    static CustomItemRarity create(@Nullable CustomItemRarity nextLevelRarity, @Nullable TextColor rarityColor) {
        return create(nextLevelRarity, Style.style(rarityColor));
    }

    static CustomItemRarity create(@Nullable CustomItemRarity nextLevelRarity, @Nullable TextColor rarityColor,
                                @Nullable TextDecoration... rarityDecorations) {
        return create(nextLevelRarity,
                rarityDecorations == null ? Style.style(rarityColor) : Style.style(rarityColor, rarityDecorations));
    }

    @Deprecated
    static CustomItemRarity create(@Nullable CustomItemRarity nextLevelRarity,
                                @Nullable org.bukkit.ChatColor... rarityColorAndStyle) {
        return create(nextLevelRarity, ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    @SuppressWarnings("deprecation")
    static CustomItemRarity create(@Nullable CustomItemRarity nextLevelRarity,
                                @Nullable net.md_5.bungee.api.ChatColor... rarityColorAndStyle) {
        return create(nextLevelRarity, ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    static CustomItemRarity create(@Nullable TextColor rarityColor) {
        return create(null, Style.style(rarityColor));
    }

    static CustomItemRarity create(@Nullable TextColor rarityColor,
                                @Nullable TextDecoration... rarityDecorations) {
        return create(null,
                rarityDecorations == null ? Style.style(rarityColor) : Style.style(rarityColor, rarityDecorations));
    }

    @Deprecated
    static CustomItemRarity create(@Nullable org.bukkit.ChatColor... rarityColorAndStyle) {
        return create(null, ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    @Deprecated
    static CustomItemRarity create(@Nullable net.md_5.bungee.api.ChatColor... rarityColorAndStyle) {
        return create(null, ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    static CustomItemRarity create(@Nullable Style rarityStyle) {
        return create(null, rarityStyle);
    }

    static CustomItemRarity create(@Nullable CustomItemRarity nextLevelRarity, @Nullable Style rarityStyle) {
        return new CustomItemRarityImpl(nextLevelRarity, rarityStyle);
    }

    @Nonnull
    Style getStyle();

    @Nonnull
    default CustomItemRarity upgrade() {
        return this;
    }

    @Nonnull
    default Component apply(@Nonnull Component component) {
        return apply(component, false, false);
    }

    @Nonnull
    Component apply(@Nonnull Component component, boolean isRenamed, boolean force);
}
