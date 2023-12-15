package org.ricetea.barleyteaapi.internal.item.renderer;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.api.item.render.ItemRenderer;
import org.ricetea.barleyteaapi.api.item.render.ItemRendererRegister;

public abstract class AbstractItemRendererImpl implements ItemRenderer {
    @Nonnull
    private final NamespacedKey key;

    protected AbstractItemRendererImpl(@Nonnull NamespacedKey key) {
        this.key = key;
    }

    @Override
    public @Nonnull NamespacedKey getKey() {
        return key;
    }

    @Override
    public boolean isRegistered() {
        ItemRendererRegister register = ItemRendererRegister.getInstanceUnsafe();
        return register == null ? false : register.lookup(getKey()) == this;
    }
}
