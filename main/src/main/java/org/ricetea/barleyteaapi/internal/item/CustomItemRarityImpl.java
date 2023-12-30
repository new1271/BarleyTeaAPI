package org.ricetea.barleyteaapi.internal.item;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.helper.ChatColorHelper;
import org.ricetea.barleyteaapi.api.item.CustomItemRarity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@ApiStatus.Internal
public final class CustomItemRarityImpl extends CustomItemRarityBase {

    @Nonnull
    private final Style style;

    @Nonnull
    private final CustomItemRarity nextLevelRarity;

    public CustomItemRarityImpl(@Nullable CustomItemRarity nextLevelRarity, @Nullable TextColor rarityColor) {
        this(nextLevelRarity, Style.style(rarityColor));
    }

    public CustomItemRarityImpl(@Nullable CustomItemRarity nextLevelRarity, @Nullable TextColor rarityColor,
                                @Nullable TextDecoration... rarityDecorations) {
        this(nextLevelRarity,
                rarityDecorations == null ? Style.style(rarityColor) : Style.style(rarityColor, rarityDecorations));
    }

    @Deprecated
    public CustomItemRarityImpl(@Nullable CustomItemRarity nextLevelRarity,
                                @Nullable org.bukkit.ChatColor... rarityColorAndStyle) {
        this(nextLevelRarity, ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    @SuppressWarnings("deprecation")
    public CustomItemRarityImpl(@Nullable CustomItemRarity nextLevelRarity,
                                @Nullable net.md_5.bungee.api.ChatColor... rarityColorAndStyle) {
        this(nextLevelRarity, ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    public CustomItemRarityImpl(@Nullable TextColor rarityColor) {
        this(null, Style.style(rarityColor));
    }

    public CustomItemRarityImpl(@Nullable TextColor rarityColor,
                                @Nullable TextDecoration... rarityDecorations) {
        this(null,
                rarityDecorations == null ? Style.style(rarityColor) : Style.style(rarityColor, rarityDecorations));
    }

    @Deprecated
    public CustomItemRarityImpl(@Nullable org.bukkit.ChatColor... rarityColorAndStyle) {
        this(null, ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    @Deprecated
    public CustomItemRarityImpl(@Nullable net.md_5.bungee.api.ChatColor... rarityColorAndStyle) {
        this(null, ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    public CustomItemRarityImpl(@Nullable Style rarityStyle) {
        this(null, rarityStyle);
    }

    public CustomItemRarityImpl(@Nullable CustomItemRarity nextLevelRarity, @Nullable Style rarityStyle) {
        Style style = rarityStyle == null ? Style.empty() : rarityStyle;
        if (!style.hasDecoration(TextDecoration.ITALIC))
            style = style.decoration(TextDecoration.ITALIC, false);
        this.style = style;
        this.nextLevelRarity = nextLevelRarity == null ? this : nextLevelRarity;
    }

    @Nonnull
    @Override
    public Style getStyle() {
        return style;
    }

    @Nonnull
    @Override
    public CustomItemRarity upgrade() {
        return nextLevelRarity;
    }
}
