package org.ricetea.barleyteaapi.api.item;

import org.ricetea.barleyteaapi.api.internal.item.VanillaCustomItemRarityImpl;

import javax.annotation.Nonnull;

public enum VanillaItemRarity {

    COMMON(VanillaCustomItemRarityImpl.COMMON),
    UNCOMMON(VanillaCustomItemRarityImpl.UNCOMMON),
    RARE(VanillaCustomItemRarityImpl.RARE),
    EPIC(VanillaCustomItemRarityImpl.EPIC);

    @Nonnull
    private final CustomItemRarity rarity;

    VanillaItemRarity(@Nonnull CustomItemRarity rarity) {
        this.rarity = rarity;
    }

    @Nonnull
    public CustomItemRarity getRarity() {
        return rarity;
    }
}
