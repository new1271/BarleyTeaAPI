package org.ricetea.barleyteaapi.api.item.render;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.ObjectUtil;

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

    @Override
    @Nullable
    public AbstractItemRenderer lookup(@Nonnull NamespacedKey key) {
        return lookupOrDefault(key, null);
    }

    @Nullable
    public AbstractItemRenderer lookupOrDefault(@Nullable NamespacedKey rendererKey,
            @Nullable AbstractItemRenderer defaultRenderer) {
        if (rendererKey == null)
            return defaultRenderer;
        AbstractItemRenderer renderer = lookupTable.get(rendererKey);
        if (renderer == null)
            return new InvalidItemRenderer(rendererKey);
        else
            return renderer;
    }

    @Override
    public boolean has(@Nonnull NamespacedKey key) {
        return lookupTable.containsKey(key);
    }

    @Override
    public boolean hasAnyRegistered() {
        return lookupTable.size() > 0;
    }

    @Override
    @Nonnull
    public Collection<AbstractItemRenderer> listAll() {
        return ObjectUtil.letNonNull(Collections.unmodifiableCollection(lookupTable.values()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<AbstractItemRenderer> listAll(@Nullable Predicate<AbstractItemRenderer> predicate) {
        return predicate == null ? listAll()
                : ObjectUtil.letNonNull(
                        lookupTable.values().stream().filter(predicate).collect(Collectors.toUnmodifiableList()),
                        Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<NamespacedKey> listAllKeys() {
        return ObjectUtil.letNonNull(Collections.unmodifiableCollection(lookupTable.keySet()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<NamespacedKey> listAllKeys(@Nullable Predicate<AbstractItemRenderer> predicate) {
        return predicate == null ? listAllKeys()
                : ObjectUtil.letNonNull(
                        lookupTable.entrySet().stream().filter(new Filter<>(predicate)).map(new Mapper<>())
                                .collect(Collectors.toUnmodifiableList()),
                        Collections::emptySet);
    }

    @Override
    @Nullable
    public AbstractItemRenderer findFirst(@Nullable Predicate<AbstractItemRenderer> predicate) {
        var stream = lookupTable.values().stream();
        if (predicate != null)
            stream = stream.filter(predicate);
        return stream.findFirst().orElse(null);
    }

    @Override
    @Nullable
    public NamespacedKey findFirstKey(@Nullable Predicate<AbstractItemRenderer> predicate) {
        var stream = lookupTable.entrySet().stream();
        if (predicate != null)
            stream = stream.filter(new Filter<>(predicate));
        return stream.map(new Mapper<>()).findFirst().orElse(null);
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
