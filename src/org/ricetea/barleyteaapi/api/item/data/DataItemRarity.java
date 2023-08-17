package org.ricetea.barleyteaapi.api.item.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.ricetea.barleyteaapi.api.helper.ChatColorHelper;
import org.ricetea.barleyteaapi.util.ObjectUtil;

import io.papermc.paper.inventory.ItemRarity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class DataItemRarity {
    @Nonnull
    public static final DataItemRarity EPIC = new DataItemRarity(NamedTextColor.LIGHT_PURPLE);

    @Nonnull
    public static final DataItemRarity RARE = new DataItemRarity(EPIC, NamedTextColor.AQUA);

    @Nonnull
    public static final DataItemRarity UNCOMMON = new DataItemRarity(RARE, NamedTextColor.YELLOW);

    @Nonnull
    public static final DataItemRarity COMMON = new DataItemRarity(UNCOMMON, NamedTextColor.WHITE);

    @Nonnull
    final Style style;

    @Nullable
    final DataItemRarity nextLevelRarity;

    public DataItemRarity(@Nullable DataItemRarity nextLevelRarity, @Nullable TextColor rarityColor) {
        this(nextLevelRarity, Style.style(rarityColor));
    }

    public DataItemRarity(@Nullable DataItemRarity nextLevelRarity, @Nullable TextColor rarityColor,
            @Nullable TextDecoration... rarityDecorations) {
        this(nextLevelRarity,
                rarityDecorations == null ? Style.style(rarityColor) : Style.style(rarityColor, rarityDecorations));
    }

    @Deprecated
    public DataItemRarity(@Nullable DataItemRarity nextLevelRarity,
            @Nullable org.bukkit.ChatColor... rarityColorAndStyle) {
        this(nextLevelRarity, ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    public DataItemRarity(@Nullable DataItemRarity nextLevelRarity,
            @Nullable net.md_5.bungee.api.ChatColor... rarityColorAndStyle) {
        this(nextLevelRarity, ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    public DataItemRarity(@Nullable TextColor rarityColor) {
        this(null, Style.style(rarityColor));
    }

    public DataItemRarity(@Nullable TextColor rarityColor,
            @Nullable TextDecoration... rarityDecorations) {
        this(null,
                rarityDecorations == null ? Style.style(rarityColor) : Style.style(rarityColor, rarityDecorations));
    }

    @Deprecated
    public DataItemRarity(@Nullable org.bukkit.ChatColor... rarityColorAndStyle) {
        this(null, ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    public DataItemRarity(@Nullable net.md_5.bungee.api.ChatColor... rarityColorAndStyle) {
        this(null, ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    public DataItemRarity(@Nullable Style rarityStyle) {
        this(null, rarityStyle);
    }

    @SuppressWarnings("null")
    public DataItemRarity(@Nullable DataItemRarity nextLevelRarity, @Nullable Style rarityStyle) {
        Style style = rarityStyle == null ? Style.empty() : rarityStyle;
        if (!style.hasDecoration(TextDecoration.ITALIC))
            style = style.decoration(TextDecoration.ITALIC, false);
        this.style = style;
        this.nextLevelRarity = nextLevelRarity;
    }

    @Nonnull
    public Component apply(@Nonnull Component component) {
        return apply(component, false);
    }

    @SuppressWarnings("null")
    @Nonnull
    public Component apply(@Nonnull Component component, boolean isRenamedItem) {
        Component result = component.style(style);
        if (isRenamedItem) {
            result = result.decoration(TextDecoration.ITALIC,
                    style.hasDecoration(TextDecoration.ITALIC) ? TextDecoration.State.FALSE
                            : TextDecoration.State.TRUE);
        }
        return result;
    }

    @Nonnull
    public DataItemRarity upgrade() {
        return ObjectUtil.letNonNull(nextLevelRarity, this);
    }

    @Nullable
    public static final DataItemRarity fromPaperItemRarity(@Nullable ItemRarity rarity) {
        if (rarity == null)
            return null;
        switch (rarity) {
            case COMMON:
                return COMMON;
            case EPIC:
                return EPIC;
            case RARE:
                return RARE;
            case UNCOMMON:
                return UNCOMMON;
            default:
                return null;
        }
    }
}
