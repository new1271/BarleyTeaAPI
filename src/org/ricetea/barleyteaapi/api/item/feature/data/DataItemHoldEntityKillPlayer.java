package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemHoldEntityFeatureData;

public final class DataItemHoldEntityKillPlayer extends BaseItemHoldEntityFeatureData<PlayerDeathEvent> {
    @SuppressWarnings("null")
    public DataItemHoldEntityKillPlayer(@Nonnull PlayerDeathEvent event,
            @Nonnull EntityDamageByEntityEvent lastDamageCauseByEntityEvent, @Nonnull ItemStack itemStack,
            @Nonnull EquipmentSlot equipmentSlot) {
        super(event, (LivingEntity) lastDamageCauseByEntityEvent.getDamager(), itemStack, equipmentSlot);
    }

    @SuppressWarnings("null")
    @Nonnull
    public Player getDecedent() {
        return event.getEntity();
    }
}
