package org.ricetea.barleyteaapi.api.item.render;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.util.Lazy;

import net.kyori.adventure.text.Component;

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
        lookupTable.put(renderer.getKey(), renderer);
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstance();
            if (inst != null) {
                Logger logger = inst.getLogger();
                logger.info("registered " + renderer.getKey().toString() + " as item renderer!");
            }
        }
    }

    @Override
    public void unregister(@Nonnull AbstractItemRenderer renderer) {
        lookupTable.remove(renderer.getKey());
    }

    @Nullable
    public AbstractItemRenderer getRendererOrDefault(@Nullable NamespacedKey rendererKey) {
        return getRendererOrDefault(rendererKey, null);
    }

    @Nullable
    public AbstractItemRenderer getRendererOrDefault(@Nullable NamespacedKey rendererKey,
            @Nullable AbstractItemRenderer defaultRenderer) {
        if (rendererKey == null)
            return defaultRenderer;
        AbstractItemRenderer renderer = lookupTable.get(rendererKey);
        if (renderer == null)
            return new InvalidItemRenderer(rendererKey);
        else
            return renderer;
    }

    public static class InvalidItemRenderer extends AbstractItemRenderer {

        public InvalidItemRenderer(@Nonnull NamespacedKey key) {
            super(key);
        }

        @Override
        public void render(@Nonnull ItemStack itemStack) {
            throw new UnsupportedOperationException(getKey().toString() + " is isn't a registered ItemRenderer!");
        }

        @Override
        protected void beforeFirstRender(@Nonnull ItemStack itemStack) {
            throw new UnsupportedOperationException(getKey().toString() + " is isn't a registered ItemRenderer!");
        }

        @Override
        protected @Nullable List<Component> getItemLore(@Nonnull ItemMeta itemMeta) {
            throw new UnsupportedOperationException(getKey().toString() + " is isn't a registered ItemRenderer!");
        }

        @Override
        protected void setItemLore(@Nonnull ItemMeta itemMeta, @Nullable List<? extends Component> lore) {
            throw new UnsupportedOperationException(getKey().toString() + " is isn't a registered ItemRenderer!");
        }

        @Override
        protected void addItemFlags(@Nonnull ItemMeta itemMeta, @Nonnull ItemFlag... flags) {
            throw new UnsupportedOperationException(getKey().toString() + " is isn't a registered ItemRenderer!");
        }

        @Override
        protected void addItemFlags(@Nonnull ItemMeta itemMeta, @Nonnull Set<ItemFlag> flags) {
            throw new UnsupportedOperationException(getKey().toString() + " is isn't a registered ItemRenderer!");
        }

        @Override
        protected void removeItemFlags(@Nonnull ItemMeta itemMeta, @Nonnull ItemFlag... flags) {
            throw new UnsupportedOperationException(getKey().toString() + " is isn't a registered ItemRenderer!");
        }

        @Override
        protected void removeItemFlags(@Nonnull ItemMeta itemMeta, @Nonnull Set<ItemFlag> flags) {
            throw new UnsupportedOperationException(getKey().toString() + " is isn't a registered ItemRenderer!");
        }

        @Override
        protected boolean hasItemFlag(@Nonnull ItemMeta itemMeta, @Nonnull ItemFlag flag) {
            throw new UnsupportedOperationException(getKey().toString() + " is isn't a registered ItemRenderer!");
        }

        @Override
        protected @Nullable Set<ItemFlag> getItemFlags(@Nonnull ItemMeta itemMeta) {
            throw new UnsupportedOperationException(getKey().toString() + " is isn't a registered ItemRenderer!");
        }
    }
}
