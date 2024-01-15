package org.ricetea.barleyteaapi.api.item.render;

import org.bukkit.Keyed;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemDisplay;

import javax.annotation.Nonnull;

public interface ItemSubRenderer extends Keyed {
    void render(@Nonnull DataItemDisplay data);
}
