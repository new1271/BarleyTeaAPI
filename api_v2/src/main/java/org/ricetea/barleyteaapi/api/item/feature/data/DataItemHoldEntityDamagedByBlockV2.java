package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.damage.DamageSource;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public final class DataItemHoldEntityDamagedByBlockV2 extends DataItemHoldEntityDamagedByBlock {

    public DataItemHoldEntityDamagedByBlockV2(@Nonnull EntityDamageByBlockEvent event, @Nonnull ItemStack itemStack,
                                              @Nonnull EquipmentSlot equipmentSlot) {
        super(event, itemStack, equipmentSlot);
    }

    @Nonnull
    public DamageSource getDamageSource() {
        return event.getDamageSource();
    }
}
