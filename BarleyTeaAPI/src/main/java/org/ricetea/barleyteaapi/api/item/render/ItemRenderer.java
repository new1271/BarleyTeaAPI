package org.ricetea.barleyteaapi.api.item.render;

import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.internal.item.renderer.DefaultItemRendererImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ItemRenderer extends Keyed {

    boolean isRegistered();

    @Nonnull
    default ItemStack render(@Nonnull ItemStack itemStack) {
        return render(itemStack, null);
    }

    @Nonnull
    ItemStack render(@Nonnull ItemStack itemStack, @Nullable Player player);

    @Nonnull
    static ItemRenderer getDefault() {
        return DefaultItemRendererImpl.getInstance();
    }
}
