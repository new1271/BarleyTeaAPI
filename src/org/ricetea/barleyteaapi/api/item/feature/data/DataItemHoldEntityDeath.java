package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataItemHoldEntityDeath extends BaseItemFeatureData<EntityDeathEvent> {
    @Nullable
    private final Entity killer;

    @Nullable
    private final Lazy<DataEntityType> killerType;

    @Nonnull
    private final Lazy<DataEntityType> decedentType;

    @SuppressWarnings("null")
    public DataItemHoldEntityDeath(@Nonnull EntityDeathEvent event,
            @Nullable EntityDamageByEntityEvent lastDamageCauseByEntityEvent, @Nonnull ItemStack itemStack,
            @Nonnull EquipmentSlot equipmentSlot) {
        super(event, event.getEntity(), itemStack, equipmentSlot);
        decedentType = new Lazy<>(() -> BaseEntity.getEntityType(getDecedent()));
        killer = ObjectUtil.mapWhenNonnull(lastDamageCauseByEntityEvent, EntityDamageByEntityEvent::getDamager);
        killerType = ObjectUtil.mapWhenNonnull(killer, killer -> new Lazy<>(() -> BaseEntity.getEntityType(killer)));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getDecedent() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getDecedentType() {
        return decedentType.get();
    }

    @Nullable
    public Entity getKiller() {
        return killer;
    }

    @Nullable
    public DataEntityType getKillerType() {
        Lazy<DataEntityType> killerType = this.killerType;
        if (killerType == null)
            return null;
        else
            return killerType.get();
    }

    public boolean hasKiller() {
        return killer != null;
    }
}
