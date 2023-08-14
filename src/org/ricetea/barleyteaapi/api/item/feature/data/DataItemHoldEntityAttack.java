package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataItemHoldEntityAttack extends BaseItemHoldEntityFeatureData<EntityDamageByEntityEvent> {

    @Nonnull
    private final Lazy<DataEntityType> damageeType;

    @SuppressWarnings("null")
    public DataItemHoldEntityAttack(@Nonnull EntityDamageByEntityEvent event, @Nonnull ItemStack itemStack,
            @Nonnull EquipmentSlot equipmentSlot) {
        super(event, (LivingEntity) event.getDamager(), itemStack, equipmentSlot);
        damageeType = new Lazy<>(() -> BaseEntity.getEntityType(getDamagee()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getDamagee() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getDamageeType() {
        return damageeType.get();
    }

    public double getDamage() {
        return event.getDamage();
    }

    public void setDamage(double damage) {
        event.setDamage(damage);
    }

    public double getFinalDamage() {
        return event.getFinalDamage();
    }

    @SuppressWarnings("null")
    @Nonnull
    public EntityDamageEvent.DamageCause getDamageCause() {
        return event.getCause();
    }
}