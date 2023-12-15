package org.ricetea.barleyteaapi.internal.nms;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface INBTItemHelper extends IHelper {
    @Nonnull
    ItemStack copyNbt(@Nonnull ItemStack original, @Nonnull ItemStack itemStackCopying);
}
