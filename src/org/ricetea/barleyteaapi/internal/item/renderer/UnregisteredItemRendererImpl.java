package org.ricetea.barleyteaapi.internal.item.renderer;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.BarleyTeaAPI;

public final class UnregisteredItemRendererImpl extends AbstractItemRendererImpl {

    public UnregisteredItemRendererImpl(@Nonnull NamespacedKey key) {
        super(key);
    }

    @Override
    public boolean isRegistered() {
        return false;
    }

    @Override
    @Nonnull
    public ItemStack render(@Nonnull ItemStack itemStack, @Nonnull Player player) {
        showWarning();
        return itemStack;
    }

    private void showWarning() {
        BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
        if (api != null) {
            api.getLogger().warning(getKey().toString() + " isn't registered as a valid item renderer!");
        }
    }
}
