package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.damage.DamageSource;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public final class DataItemHoldEntityAttackV2 extends DataItemHoldEntityAttack {

    public DataItemHoldEntityAttackV2(@Nonnull EntityDamageByEntityEvent event, @Nonnull ItemStack itemStack,
                                      @Nonnull EquipmentSlot equipmentSlot) {
        super(event, itemStack, equipmentSlot);
    }

    @Nonnull
    public DamageSource getDamageSource() {
        return event.getDamageSource();
    }
}
