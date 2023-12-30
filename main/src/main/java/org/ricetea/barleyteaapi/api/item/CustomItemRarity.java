package org.ricetea.barleyteaapi.api.item;

import io.papermc.paper.inventory.ItemRarity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.ricetea.barleyteaapi.internal.item.VanillaCustomItemRarityImpl;

import javax.annotation.Nonnull;
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
