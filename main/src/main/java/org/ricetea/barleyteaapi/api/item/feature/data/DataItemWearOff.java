package org.ricetea.barleyteaapi.api.item.feature.data;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent.SlotType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BasePlayerFeatureData;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemWearOff extends BasePlayerFeatureData<PlayerArmorChangeEvent> {

    @Nonnull
    private final Lazy<CustomItemType> newItemType;

    public DataItemWearOff(@Nonnull PlayerArmorChangeEvent event) {
        super(event);
        newItemType = Lazy.create(() -> CustomItemType.get(getNewItem()));
    }

    @Nonnull
    public EquipmentSlot getEquipmentSlot() {
        return switch (getSlotType()) {
            case CHEST -> EquipmentSlot.CHEST;
            case FEET -> EquipmentSlot.FEET;
            case HEAD -> EquipmentSlot.HEAD;
            case LEGS -> EquipmentSlot.LEGS;
        };
    }

    @Nonnull
    public SlotType getSlotType() {
        return Objects.requireNonNull(event.getSlotType());
    }

    @Nonnull
    public ItemStack getItem() {
        return Objects.requireNonNull(event.getOldItem());
    }

    @Nonnull
    public ItemStack getNewItem() {
        return event.getNewItem();
    }

    @Nonnull
    public CustomItemType getNewItemType() {
        return newItemType.get();
    }
}
