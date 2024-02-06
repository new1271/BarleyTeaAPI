package org.ricetea.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.IntUnaryOperator;

public class Constants {
    @Nonnull
    public static final UUID EMPTY_UUID = new UUID(0, 0);
    @Nonnull
    public static final NamespacedKey EMPTY_NAMESPACED_KEY = NamespacedKey.minecraft("empty");
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
    public static final int MIN_ITERATION_COUNT_FOR_PARALLEL = Math.max(Runtime.getRuntime().availableProcessors() * 5, 8);
}
