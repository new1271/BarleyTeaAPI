package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemAnvilFeatureData;

public final class DataItemAnvilRename extends BaseItemAnvilFeatureData {

    public DataItemAnvilRename(@Nonnull PrepareAnvilEvent event) {
        super(event);
    }

}
