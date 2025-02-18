package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.damage.DamageSource;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public final class DataItemHoldEntityDamagedByNothingV2 extends DataItemHoldEntityDamagedByNothing {

    public DataItemHoldEntityDamagedByNothingV2(@Nonnull EntityDamageEvent event, @Nonnull ItemStack itemStack,
                                                @Nonnull EquipmentSlot equipmentSlot) {
        super(event, itemStack, equipmentSlot);
    }

    @Nonnull
    public DamageSource getDamageSource() {
        return event.getDamageSource();
    }
}
