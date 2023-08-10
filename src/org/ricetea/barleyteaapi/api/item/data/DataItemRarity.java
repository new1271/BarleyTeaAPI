package org.ricetea.barleyteaapi.api.item.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.ricetea.barleyteaapi.api.helper.ChatColorHelper;

import io.papermc.paper.inventory.ItemRarity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class DataItemRarity {

    @Nonnull
    public static final DataItemRarity COMMON = new DataItemRarity(NamedTextColor.WHITE);

    @Nonnull
    public static final DataItemRarity UNCOMMON = new DataItemRarity(NamedTextColor.YELLOW);

    @Nonnull
    public static final DataItemRarity RARE = new DataItemRarity(NamedTextColor.AQUA);

    @Nonnull
    public static final DataItemRarity EPIC = new DataItemRarity(NamedTextColor.LIGHT_PURPLE);

    @Nonnull
    final Style style;

    public DataItemRarity(@Nullable TextColor rarityColor) {
        this(Style.style(rarityColor));
    }

    public DataItemRarity(@Nullable TextColor rarityColor, @Nullable TextDecoration... rarityDecorations) {
        this(rarityDecorations == null ? Style.style(rarityColor) : Style.style(rarityColor, rarityDecorations));
    }

    @Deprecated
    public DataItemRarity(@Nullable org.bukkit.ChatColor... rarityColorAndStyle) {
        this(ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    public DataItemRarity(@Nullable net.md_5.bungee.api.ChatColor... rarityColorAndStyle) {
        this(ChatColorHelper.toKyoriStyle(rarityColorAndStyle));
    }

    @SuppressWarnings("null")
    public DataItemRarity(@Nullable Style rarityStyle) {
        style = rarityStyle == null ? Style.empty() : rarityStyle;
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
