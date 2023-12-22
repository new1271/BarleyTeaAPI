package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public final class DataEntityExplode extends BaseEntityFeatureData<EntityExplodeEvent> {

    public DataEntityExplode(@Nonnull EntityExplodeEvent event) {
        super(event);
    }

    public @Nonnull List<Block> blockList() {
        return Objects.requireNonNull(event.blockList());
    }

    public @Nonnull Location getLocation() {
        return Objects.requireNonNull(event.getLocation());
    }

    public float getYield() {
        return event.getYield();
    }

    public void setYield(float yield) {
        event.setYield(yield);
    }
}
