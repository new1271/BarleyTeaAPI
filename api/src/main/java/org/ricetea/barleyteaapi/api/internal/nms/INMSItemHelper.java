package org.ricetea.barleyteaapi.api.internal.nms;

import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ApiStatus.Internal
public interface INMSItemHelper extends IHelper {
    @Nullable
    Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@Nullable Material material);

    @Nullable
    ItemStack createItemStackFromNbtString(@Nonnull String nbt);

    @Nullable
    String getNMSEquipmentSlotName(@Nullable EquipmentSlot slot);
}
