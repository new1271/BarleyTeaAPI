package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemAnvilFeatureData;

import javax.annotation.Nonnull;

public final class DataItemAnvilRename extends BaseItemAnvilFeatureData {

    public DataItemAnvilRename(@Nonnull PrepareAnvilEvent event) {
        super(event);
    }

}
