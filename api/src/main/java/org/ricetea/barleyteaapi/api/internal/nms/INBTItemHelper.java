package org.ricetea.barleyteaapi.api.internal.nms;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ApiStatus.Internal
public interface INBTItemHelper extends IHelper {
    @Nonnull
    ItemStack copyNbt(@Nonnull ItemStack original, @Nonnull ItemStack result, @Nullable String... tagBlacklist);

    @Nonnull
    ItemStack mergeNbt(@Nonnull ItemStack original, @Nonnull ItemStack result, @Nullable String... tags);

    @Nonnull
    ItemStack setNbt(@Nonnull ItemStack original, @Nonnull String nbt);
}
