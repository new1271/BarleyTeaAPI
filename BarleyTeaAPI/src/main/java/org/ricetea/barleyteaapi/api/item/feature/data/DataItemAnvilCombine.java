package org.ricetea.barleyteaapi.api.item.feature.data;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemAnvilFeatureData;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.utils.Lazy;

public final class DataItemAnvilCombine extends BaseItemAnvilFeatureData {

    @Nonnull
    private final Lazy<DataItemType> combinedType;

    public DataItemAnvilCombine(@Nonnull PrepareAnvilEvent event) {
        super(event);
        combinedType = Lazy.create(() -> BaseItem.getItemType(getItemStackCombined()));
    }

    @Nonnull
    public ItemStack getItemStackCombined() {
        return Objects.requireNonNull(event.getInventory().getSecondItem());
    }

    @Nonnull
    public DataItemType getItemStackCombinedType() {
        return combinedType.get();
    }
}
