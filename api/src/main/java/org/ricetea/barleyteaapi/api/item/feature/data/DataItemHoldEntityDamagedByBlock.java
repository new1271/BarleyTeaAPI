package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.block.CustomBlockType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class DataItemHoldEntityDamagedByBlock extends BaseItemHoldEntityFeatureData<EntityDamageByBlockEvent> {
    @Nonnull
    private final Lazy<CustomBlockType> blockType;

    public DataItemHoldEntityDamagedByBlock(@Nonnull EntityDamageByBlockEvent event, @Nonnull ItemStack itemStack,
                                            @Nonnull EquipmentSlot equipmentSlot) {
        super(event, (LivingEntity) event.getEntity(), itemStack, equipmentSlot);
        blockType = Lazy.create(() -> CustomBlockType.get(getDamager()));
    }

    @Nullable
    public Block getDamager() {
        return event.getDamager();
    }

    @Nonnull
    public CustomBlockType getDamagerType() {
        return blockType.get();
    }

    public double getBaseDamage() {
        return event.getDamage(EntityDamageEvent.DamageModifier.BASE);
    }

    public double getDamage(@Nonnull EntityDamageEvent.DamageModifier modifier) {
        return event.getDamage(modifier);
    }

    public double getDamage() {
        return event.getDamage();
    }

    public void setDamage(@Nonnull EntityDamageEvent.DamageModifier modifier, double damage) {
        event.setDamage(modifier, damage);
    }

    public void setDamage(double damage) {
        event.setDamage(damage);
    }

    public void setBaseDamage(double damage) {
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, damage);
    }

    public double getFinalDamage() {
        return event.getFinalDamage();
    }

    @Nonnull
    public EntityDamageEvent.DamageCause getDamageCause() {
        return event.getCause();
    }
}
