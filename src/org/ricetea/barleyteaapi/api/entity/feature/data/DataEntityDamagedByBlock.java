package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.block.data.DataBlockType;
import org.ricetea.utils.Lazy;

public final class DataEntityDamagedByBlock extends BaseEntityFeatureData<EntityDamageByBlockEvent> {
    @Nonnull
    private final Lazy<DataBlockType> blockType;

    public DataEntityDamagedByBlock(@Nonnull EntityDamageByBlockEvent event) {
        super(event);
        blockType = Lazy.create(() -> BaseBlock.getBlockType(getDamager()));
    }

    @SuppressWarnings("null")
    @Nonnull
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

    @SuppressWarnings("null")
    @Nonnull
    public EntityDamageEvent.DamageCause getDamageCause() {
        return event.getCause();
    }
}
