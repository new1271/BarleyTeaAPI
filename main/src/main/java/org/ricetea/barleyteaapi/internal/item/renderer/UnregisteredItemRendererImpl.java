package org.ricetea.barleyteaapi.internal.item.renderer;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.render.ItemSubRendererSupportingState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ApiStatus.Internal
public final class UnregisteredItemRendererImpl extends AbstractItemRendererImpl {

    public UnregisteredItemRendererImpl(@Nonnull NamespacedKey key) {
        super(key);
    }

    @Override
    public boolean isRegistered() {
        return false;
    }

    @Nonnull
    @Override
    public ItemSubRendererSupportingState getSubRendererSupportingState() {
        return ItemSubRendererSupportingState.APIHandled;
    }

    @Override
    @Nonnull
    public ItemStack render(@Nonnull ItemStack itemStack, @Nullable Player player) {
        showWarning();
        return itemStack;
    }

    private void showWarning() {
        BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
        if (api != null) {
            api.getLogger().warning(getKey() + " isn't registered as a valid item renderer!");
        }
    }
}
