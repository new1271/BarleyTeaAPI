package org.ricetea.barleyteaapi.api.base.data;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

public abstract class BaseItemHoldEntityFeatureData<T extends Event> extends BaseFeatureData<T> {

    @Nonnull
    private final LivingEntity holderEntity;

    @Nonnull
    private final Lazy<CustomEntityType> holderEntityType;

    @Nonnull
    private final ItemStack itemStack;

    @Nonnull
    private final EquipmentSlot equipmentSlot;

    public BaseItemHoldEntityFeatureData(@Nonnull T event, @Nonnull LivingEntity holderEntity,
                                         @Nonnull ItemStack itemStack,
                                         @Nonnull EquipmentSlot equipmentSlot) {
        super(event);
        this.holderEntity = holderEntity;
        this.holderEntityType = Lazy.create(() -> CustomEntityType.get(getHolderEntity()));
        this.itemStack = itemStack;
        this.equipmentSlot = equipmentSlot;
    }

    @Nonnull
    public LivingEntity getHolderEntity() {
        return holderEntity;
    }

    @Nonnull
    public final CustomEntityType getHolderEntityType() {
        return holderEntityType.get();
    }

    @Nonnull
    public final ItemStack getItemStack() {
        return itemStack;
    }

    @Nonnull
    public final EquipmentSlot getEquipmentSlot() {
        return equipmentSlot;
    }
}
