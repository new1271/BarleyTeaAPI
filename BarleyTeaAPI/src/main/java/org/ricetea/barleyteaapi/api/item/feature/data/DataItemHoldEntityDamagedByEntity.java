package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

public final class DataItemHoldEntityDamagedByEntity extends BaseItemHoldEntityFeatureData<EntityDamageByEntityEvent> {

    @Nonnull
    private final Lazy<DataEntityType> damagerType;

    public DataItemHoldEntityDamagedByEntity(@Nonnull EntityDamageByEntityEvent event, @Nonnull ItemStack itemStack,
                                             @Nonnull EquipmentSlot equipmentSlot) {
        super(event, (LivingEntity) event.getEntity(), itemStack, equipmentSlot);
        damagerType = Lazy.create(() -> BaseEntity.getEntityType(getDamager()));
    }

    @Nonnull
    public Entity getDamager() {
        return event.getDamager();
    }

    @Nonnull
    public DataEntityType getDamagerType() {
        return damagerType.get();
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

    @Nonnull
    public EntityDamageEvent.DamageCause getDamageCause() {
        return event.getCause();
    }
}
