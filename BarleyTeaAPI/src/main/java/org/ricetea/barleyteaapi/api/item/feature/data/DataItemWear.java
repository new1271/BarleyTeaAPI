package org.ricetea.barleyteaapi.api.item.feature.data;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent.SlotType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BasePlayerFeatureData;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemWear extends BasePlayerFeatureData<PlayerArmorChangeEvent> {

    @Nonnull
    private final Lazy<DataItemType> oldItemType;

    public DataItemWear(@Nonnull PlayerArmorChangeEvent event) {
        super(event);
        oldItemType = Lazy.create(() -> ObjectUtil
                .letNonNull(ObjectUtil.safeMap(getOldItem(), BaseItem::getItemType), DataItemType::empty));
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
    public ItemStack getOldItem() {
        return event.getOldItem();
    }

    @Nonnull
    public DataItemType getOldItemType() {
        return oldItemType.get();
    }

    @Nonnull
    public ItemStack getItem() {
        return Objects.requireNonNull(event.getNewItem());
    }
}
