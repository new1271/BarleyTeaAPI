package org.ricetea.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.IntUnaryOperator;
import java.util.regex.Pattern;

public class Constants {
    @Nonnull
    public static final UUID EMPTY_UUID = new UUID(0, 0);
    @Nonnull
    public static final NamespacedKey EMPTY_NAMESPACED_KEY = NamespacedKey.minecraft("empty");
    @Nonnull
    public static final String DEFAULT_ATTRIBUTE_MODIFIER_NAME = "default modifiers";
    @Nonnull
    public static final String DEFAULT_ATTRIBUTE_MODIFIER_KEY_HEADER = "default_";
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
    public static final int MIN_ITERATION_COUNT_FOR_PARALLEL_VERY_SMALL_OPERATION = MIN_ITERATION_COUNT_FOR_PARALLEL * 10;

    @Nonnull
    public static NamespacedKey getDefaultAttributeModifierKey(@Nonnull Attribute attribute, @Nullable EquipmentSlot slot) {
        NamespacedKey original = attribute.getKey();
        String value = original.value();
        if (value.startsWith("generic.")) {
            value = value.substring("generic.".length());
        }
        if (EquipmentSlot.HAND.equals(slot))
            return new NamespacedKey(original.getNamespace(), DEFAULT_ATTRIBUTE_MODIFIER_KEY_HEADER + value);
        return new NamespacedKey(original.getNamespace(), DEFAULT_ATTRIBUTE_MODIFIER_KEY_HEADER +
                (slot == null ? "all" : slot.toString().toLowerCase()) + "_" + value);
    }
}
