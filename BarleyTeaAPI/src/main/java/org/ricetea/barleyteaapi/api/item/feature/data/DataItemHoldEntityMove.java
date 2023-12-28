package org.ricetea.barleyteaapi.api.item.feature.data;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BaseItemHoldEntityFeatureData;

import javax.annotation.Nonnull;

public final class DataItemHoldEntityMove extends BaseItemHoldEntityFeatureData<EntityMoveEvent> {

    public DataItemHoldEntityMove(@Nonnull EntityMoveEvent event, @Nonnull ItemStack itemStack,
                                  @Nonnull EquipmentSlot equipmentSlot) {
        super(event, event.getEntity(), itemStack, equipmentSlot);
    }

    @Nonnull
    public Location getFrom() {
        return event.getFrom();
    }

    public void setFrom(@Nonnull Location from) {
        event.setFrom(from);
    }

    @Nonnull
    public Location getTo() {
        return event.getTo();
    }

    public void setTo(@Nonnull Location to) {
        event.setTo(to);
    }

    public boolean hasChangedPosition() {
        return event.hasChangedPosition();
    }

    public boolean hasExplicitlyChangedPosition() {
        return event.hasExplicitlyChangedPosition();
    }

    public boolean hasChangedBlock() {
        return event.hasChangedBlock();
    }

    public boolean hasExplicitlyChangedBlock() {
        return event.hasExplicitlyChangedBlock();
    }

    public boolean hasChangedOrientation() {
        return event.hasChangedOrientation();
    }
}
