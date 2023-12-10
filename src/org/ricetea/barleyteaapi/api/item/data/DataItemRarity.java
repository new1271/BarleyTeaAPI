package org.ricetea.barleyteaapi.api.item.data;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.ricetea.barleyteaapi.api.helper.ChatColorHelper;
import org.ricetea.utils.ObjectUtil;

import io.papermc.paper.inventory.ItemRarity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class DataItemRarity {
    @Nonnull
    public static final DataItemRarity EPIC = fromVanillaItemRarity(null, ItemRarity.EPIC);

    @Nonnull
    public static final DataItemRarity RARE = fromVanillaItemRarity(EPIC, ItemRarity.RARE);

    @Nonnull
    public static final DataItemRarity UNCOMMON = fromVanillaItemRarity(RARE, ItemRarity.UNCOMMON);

    @Nonnull
    public static final DataItemRarity COMMON = fromVanillaItemRarity(RARE, ItemRarity.COMMON);

    @Nonnull
    private final Style style;

    @Nullable
    private final DataItemRarity nextLevelRarity;

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

    public DataItemRarity(@Nullable DataItemRarity nextLevelRarity, @Nullable Style rarityStyle) {
        Style style = rarityStyle == null ? Style.empty() : rarityStyle;
        if (!style.hasDecoration(TextDecoration.ITALIC))
            style = style.decoration(TextDecoration.ITALIC, false);
        this.style = style;
        this.nextLevelRarity = nextLevelRarity;
    }

    @Nonnull
    public Component apply(@Nonnull Component component) {
        return apply(component, false, false);
    }

    @Nonnull
    public Component apply(@Nonnull Component component, boolean isRenamed, boolean force) {
        Component result;
        if (force) {
            result = component.style(style);
            if (isRenamed) {
                result = result.decoration(TextDecoration.ITALIC, !style.hasDecoration(TextDecoration.ITALIC));
            }
        } else {
            result = component;
            if (component.color() == null) {
                result = result.color(style.color());
            }
            if (component.decorations().isEmpty()) {
                result = result.decorations(style.decorations())
                        .decoration(TextDecoration.ITALIC, style.hasDecoration(TextDecoration.ITALIC) ^ isRenamed);
            }
        }
        return result;
    }

    @Nonnull
    public DataItemRarity upgrade() {
        return ObjectUtil.letNonNull(nextLevelRarity, this);
    }

    @Nonnull
    public final Style getStyle() {
        return style;
    }

    public final boolean isSimilar(@Nullable DataItemRarity rarity) {
        if (rarity == null)
            return false;
        return isSimilar(rarity.style);
    }

    public final boolean isSimilar(@Nullable Style style) {
        if (style == null)
            return false;
        Style thisStyle = this.style;
        if (thisStyle.isEmpty() && style.isEmpty())
            return true;
        TextColor color = style.color();
        if (Objects.equals(thisStyle.color(), color)) {
            var thisDecorMap = thisStyle.decorations();
            var decorMap = style.decorations();
            for (TextDecoration decoration : TextDecoration.values()) {
                if (decoration.equals(TextDecoration.ITALIC))
                    continue;
                else {
                    if (!Objects.equals(
                            decorMap.getOrDefault(decoration, TextDecoration.State.NOT_SET),
                            thisDecorMap.getOrDefault(decoration, TextDecoration.State.NOT_SET))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Nonnull
    private static DataItemRarity fromVanillaItemRarity(@Nullable DataItemRarity nextLevelRarity,
            @Nonnull ItemRarity rarity) {
        return new DataItemRarity(nextLevelRarity, Style.style(rarity.getColor()));
    }

    @Nullable
    public static final DataItemRarity fromItemRarity(@Nullable ItemRarity rarity) {
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
