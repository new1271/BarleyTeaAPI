package org.ricetea.barleyteaapi.api.item.render;

import javax.annotation.Nonnull;

import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.internal.item.renderer.DefaultItemRendererImpl;

public interface ItemRenderer extends Keyed {

    boolean isRegistered();

    @Nonnull
    ItemStack render(@Nonnull ItemStack itemStack, @Nonnull Player player);

    @Nonnull
    public static ItemRenderer getDefault() {
        return DefaultItemRendererImpl.getInstance();
    }
}
