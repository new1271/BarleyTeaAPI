package org.ricetea.barleyteaapi.api.item.render;

import java.util.Hashtable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.util.Lazy;

public final class ItemRendererRegister implements IRegister<AbstractItemRenderer> {
    @Nonnull
    private static final Lazy<ItemRendererRegister> inst = new Lazy<>(ItemRendererRegister::new);

    @Nonnull
    private final Hashtable<NamespacedKey, AbstractItemRenderer> lookupTable = new Hashtable<>();

    private ItemRendererRegister() {
    }

    @Nonnull
    public static ItemRendererRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return inst.get();
    }

    @Override
    public void register(@Nonnull AbstractItemRenderer renderer) {
        BarleyTeaAPI.checkPluginUsable();
        lookupTable.put(renderer.getKey(), renderer);
    }

    @Override
    public void unregister(@Nonnull AbstractItemRenderer renderer) {
        lookupTable.remove(renderer.getKey());
    }

    @Nullable
    public AbstractItemRenderer getRenderer(@Nullable NamespacedKey rendererKey) {
        return rendererKey == null ? AbstractItemRenderer.getDefault() : lookupTable.get(rendererKey);
    }
}
