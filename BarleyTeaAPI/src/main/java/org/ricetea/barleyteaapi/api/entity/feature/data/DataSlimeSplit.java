package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.event.entity.SlimeSplitEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;

import javax.annotation.Nonnull;

public final class DataSlimeSplit extends BaseEntityFeatureData<SlimeSplitEvent> {
    public DataSlimeSplit(@Nonnull SlimeSplitEvent event) {
        super(event);
    }

    public int getCount() {
        return event.getCount();
    }

    public void setCount(int count) {
        event.setCount(count);
    }
}
