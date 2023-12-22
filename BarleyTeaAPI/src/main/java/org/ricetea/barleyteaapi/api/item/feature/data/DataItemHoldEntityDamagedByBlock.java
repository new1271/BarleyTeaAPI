package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.block.data.DataBlockType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class DataItemHoldEntityDamagedByBlock extends BaseItemHoldEntityFeatureData<EntityDamageByBlockEvent> {
    @Nonnull
    private final Lazy<DataBlockType> blockType;

    public DataItemHoldEntityDamagedByBlock(@Nonnull EntityDamageByBlockEvent event, @Nonnull ItemStack itemStack,
                                            @Nonnull EquipmentSlot equipmentSlot) {
        super(event, (LivingEntity) event.getEntity(), itemStack, equipmentSlot);
        blockType = Lazy.create(() -> DataBlockType.get(getDamager()));
    }

    @Nullable
    public Block getDamager() {
        return event.getDamager();
    }

    @Nonnull
    public DataBlockType getDamagerType() {
        return blockType.get();
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
