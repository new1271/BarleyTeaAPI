package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

public final class DataItemHoldEntityAttack extends BaseItemHoldEntityFeatureData<EntityDamageByEntityEvent> {

    @Nonnull
    private final Lazy<CustomEntityType> damageeType;

    public DataItemHoldEntityAttack(@Nonnull EntityDamageByEntityEvent event, @Nonnull ItemStack itemStack,
                                    @Nonnull EquipmentSlot equipmentSlot) {
        super(event, (LivingEntity) event.getDamager(), itemStack, equipmentSlot);
        damageeType = Lazy.create(() -> CustomEntityType.get(getDamagee()));
    }

    @Nonnull
    public Entity getDamagee() {
        return event.getEntity();
    }

    @Nonnull
    public CustomEntityType getDamageeType() {
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

    @Nonnull
    public EntityDamageEvent.DamageCause getDamageCause() {
        return event.getCause();
    }
}
