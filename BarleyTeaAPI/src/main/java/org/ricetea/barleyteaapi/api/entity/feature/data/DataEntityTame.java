package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTameEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataEntityTame extends BaseEntityFeatureData<EntityTameEvent> {

    public DataEntityTame(@Nonnull EntityTameEvent event) {
        super(event);
    }

    public @Nonnull LivingEntity getEntity() {
        return Objects.requireNonNull(event.getEntity());
    }

    public @Nonnull AnimalTamer getOwner() {
        return Objects.requireNonNull(event.getOwner());
    }
}
