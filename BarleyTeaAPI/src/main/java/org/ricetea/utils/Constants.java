package org.ricetea.utils;

import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.IntUnaryOperator;

public class Constants {
    @Nonnull
    public static final UUID EMPTY_UUID = new UUID(0, 0);
    @Nonnull
    public static final IntUnaryOperator IncreaseOperator = i -> i + 1;
    @Nonnull
    public static final IntUnaryOperator DecreaseOperator = i -> i - 1;
    @Nonnull
    public static final EquipmentSlot[] ALL_SLOTS = EquipmentSlot.values();
    @Nonnull
    public static final EquipmentSlot[] HAND_SLOTS = new EquipmentSlot[]
            {EquipmentSlot.HAND, EquipmentSlot.OFF_HAND};
    @Nonnull
    public static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]
            {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
}
