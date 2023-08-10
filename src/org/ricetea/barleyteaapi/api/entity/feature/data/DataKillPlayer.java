package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BarleyTeaEntityType;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.util.Lazy;

import net.kyori.adventure.text.Component;

public final class DataKillPlayer extends DataEntityBase<PlayerDeathEvent> {
    @Nonnull
    private final Entity killer;

    @Nonnull
    private final Lazy<BarleyTeaEntityType> killerType;

    @SuppressWarnings("null")
    public DataKillPlayer(@Nonnull PlayerDeathEvent event,
            @Nonnull EntityDamageByEntityEvent lastDamageCauseByEntityEvent) {
        super(event);
        killer = lastDamageCauseByEntityEvent.getDamager();
        killerType = new Lazy<>(() -> BaseEntity.getEntityType(killer));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Player getDecedent() {
        return event.getEntity();
    }

    @Nonnull
    public Entity getKiller() {
        return killer;
    }

    @Nonnull
    public BarleyTeaEntityType getKillerType() {
        return killerType.get();
    }

    @Nullable
    public Component deathMessage() {
        return event.deathMessage();
    }

    public void deathMessage(@Nullable Component component) {
        event.deathMessage(component);
    }
}
