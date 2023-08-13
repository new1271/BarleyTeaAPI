package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataItemHoldEntityKillEntity extends BaseItemFeatureData<EntityDeathEvent> {
    @Nonnull
    private final Lazy<DataEntityType> decedentType;

    @SuppressWarnings("null")
    public DataItemHoldEntityKillEntity(@Nonnull EntityDeathEvent event,
            @Nonnull EntityDamageByEntityEvent lastDamageCauseByEntityEvent, @Nonnull ItemStack itemStack,
            @Nonnull EquipmentSlot equipmentSlot) {
        super(event, (LivingEntity) lastDamageCauseByEntityEvent.getDamager(), itemStack, equipmentSlot);
        decedentType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
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
}
