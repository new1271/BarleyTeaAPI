package org.ricetea.barleyteaapi.internal.item;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.ApiStatus;
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
