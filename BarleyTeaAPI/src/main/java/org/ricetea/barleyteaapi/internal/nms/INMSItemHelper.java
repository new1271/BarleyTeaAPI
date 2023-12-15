package org.ricetea.barleyteaapi.internal.nms;

import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface INMSItemHelper extends IHelper {
    @Nullable
    Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@Nullable Material material);

    @Nullable
    ItemStack createItemStackFromNbtString(@Nonnull String nbt);
}
