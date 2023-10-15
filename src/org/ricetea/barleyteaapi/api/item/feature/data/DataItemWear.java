package org.ricetea.barleyteaapi.api.item.feature.data;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BasePlayerFeatureData;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent.SlotType;

public final class DataItemWear extends BasePlayerFeatureData<PlayerArmorChangeEvent> {

    @Nonnull
    private final Lazy<DataItemType> oldItemType;

    public DataItemWear(@Nonnull PlayerArmorChangeEvent event) {
        super(event);
        oldItemType = Lazy.create(() -> ObjectUtil
                .letNonNull(ObjectUtil.mapWhenNonnull(getOldItem(), BaseItem::getItemType), DataItemType::empty));
    }

    @Nonnull
    public EquipmentSlot getEquipmentSlot() {
        switch (getSlotType()){
            case CHEST:
                return EquipmentSlot.CHEST;
            case FEET:
                return EquipmentSlot.FEET;
            case HEAD:
                return EquipmentSlot.HEAD;
            case LEGS:
                return EquipmentSlot.LEGS;
            default:
                return Objects.requireNonNull(null);
        }
    }

    @Nonnull
    public SlotType getSlotType() {
        return Objects.requireNonNull(event.getSlotType());
    }

    @Nullable
    public ItemStack getOldItem() {
        return event.getOldItem();
    }

    @Nonnull
    public DataItemType getOldItemType(){
        return oldItemType.get();
    }

    @Nonnull
    public ItemStack getItem() {
        return Objects.requireNonNull(event.getNewItem());
    }
}
