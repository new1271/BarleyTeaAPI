package org.ricetea.barleyteaapi.api.item.render;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface ItemRenderer extends Keyed {

    @Nonnull
    static ItemRenderer getDefault() {
        return Objects.requireNonNull(Bukkit.getServicesManager().load(ItemRenderer.class));
    }

    boolean isRegistered();

    @Nonnull
    ItemSubRendererSupportingState getSubRendererSupportingState();

    @Nonnull
    default ItemStack render(@Nonnull ItemStack itemStack) {
        return render(itemStack, null);
    }

    @Nonnull
    ItemStack render(@Nonnull ItemStack itemStack, @Nullable Player player);
}
