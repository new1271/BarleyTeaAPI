package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;

import net.kyori.adventure.text.Component;

public final class DataKillPlayer extends BaseEntityFeatureData<PlayerDeathEvent> {
    public DataKillPlayer(@Nonnull PlayerDeathEvent event,
            @Nonnull EntityDamageByEntityEvent lastDamageCauseByEntityEvent) {
        super(event, lastDamageCauseByEntityEvent.getDamager());
    }

    @SuppressWarnings("null")
    @Nonnull
    public Player getDecedent() {
        return event.getEntity();
    }

    @Nullable
    public Component deathMessage() {
        return event.deathMessage();
    }

    public void deathMessage(@Nullable Component component) {
        event.deathMessage(component);
    }
}
