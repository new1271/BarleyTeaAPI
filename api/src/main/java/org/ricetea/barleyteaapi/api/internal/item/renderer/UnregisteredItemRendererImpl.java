package org.ricetea.barleyteaapi.api.internal.item.renderer;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.item.render.ItemRenderer;
import org.ricetea.barleyteaapi.api.item.render.ItemSubRendererSupportingState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ApiStatus.Internal
public final class UnregisteredItemRendererImpl implements ItemRenderer {

    private final NamespacedKey key;

    public UnregisteredItemRendererImpl(@Nonnull NamespacedKey key) {
        this.key = key;
    }

    @Override
    public @Nonnull NamespacedKey getKey() {
        return key;
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

    @Nonnull
    @Override
    public ItemStack restore(@Nonnull ItemStack itemStack, @Nullable Player player) {
        showWarning();
        return itemStack;
    }

    private void showWarning() {
        new Exception(getKey() + " isn't registered as a valid item renderer!").printStackTrace();
    }
}
