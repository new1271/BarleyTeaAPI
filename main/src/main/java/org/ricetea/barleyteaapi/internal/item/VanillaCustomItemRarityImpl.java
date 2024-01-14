package org.ricetea.barleyteaapi.internal.item;

import io.papermc.paper.inventory.ItemRarity;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.item.CustomItemRarity;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@ApiStatus.Internal
public final class VanillaCustomItemRarityImpl extends CustomItemRarityBase {

    @Nonnull
    public static final VanillaCustomItemRarityImpl COMMON, UNCOMMON, RARE, EPIC;

    static {
        EPIC = new VanillaCustomItemRarityImpl(ItemRarity.EPIC);
        RARE = new VanillaCustomItemRarityImpl(ItemRarity.RARE, EPIC);
        UNCOMMON = new VanillaCustomItemRarityImpl(ItemRarity.UNCOMMON, RARE);
        COMMON = new VanillaCustomItemRarityImpl(ItemRarity.COMMON, RARE);
    }

    @Nonnull
    private final ItemRarity rarity;

    @Nonnull
    private final CustomItemRarity nextLevelRarity;

    @Nonnull
    private final Lazy<Style> styleLazy = Lazy.createThreadSafe(() -> Style.style(getRarity().getColor()));

    private VanillaCustomItemRarityImpl(@Nonnull ItemRarity rarity) {
        this(rarity, null);
    }

    private VanillaCustomItemRarityImpl(@Nonnull ItemRarity rarity, @Nullable CustomItemRarity nextLevelRarity) {
        this.rarity = rarity;
        this.nextLevelRarity = nextLevelRarity == null ? this : nextLevelRarity;
    }

    @Nonnull
    public ItemRarity getRarity() {
        return rarity;
    }

    @Nonnull
    @Override
    public CustomItemRarity upgrade() {
        return nextLevelRarity;
    }

    @Nonnull
    @Override
    public Style getStyle() {
        return styleLazy.get();
    }
}
