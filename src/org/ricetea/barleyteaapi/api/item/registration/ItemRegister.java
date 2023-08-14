package org.ricetea.barleyteaapi.api.item.registration;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCommandGive;
import org.ricetea.barleyteaapi.internal.nms.BarleyGiveItemProvider;
import org.ricetea.barleyteaapi.util.Lazy;

public final class ItemRegister implements IRegister<BaseItem> {
    @Nonnull
    private static final Lazy<ItemRegister> inst = new Lazy<>(ItemRegister::new);

    @Nonnull
    private final Hashtable<NamespacedKey, BaseItem> lookupTable = new Hashtable<>();

    private ItemRegister() {
    }

    @Nonnull
    public static ItemRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return inst.get();
    }

    public void register(@Nonnull BaseItem item) {
        lookupTable.put(item.getKey(), item);
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstance();
            if (inst != null) {
                Logger logger = inst.getLogger();
                logger.info("registered " + item.getKey().toString() + " as item!");
                if (item instanceof FeatureCommandGive) {
                    BarleyGiveItemProvider.updateRegisterList();
                }
            }
        }
    }

    public void unregister(@Nonnull BaseItem item) {
        lookupTable.remove(item.getKey());
        BarleyTeaAPI inst = BarleyTeaAPI.getInstance();
        if (inst != null) {
            if (item instanceof FeatureCommandGive) {
                BarleyGiveItemProvider.updateRegisterList();
            }
        }
    }

    @Nullable
    public BaseItem lookupItemType(@Nonnull NamespacedKey key) {
        return lookupTable.get(key);
    }

    public boolean hasItemType(@Nonnull NamespacedKey key) {
        return lookupTable.containsKey(key);
    }

    public boolean hasAnyRegisteredItem() {
        return lookupTable.size() > 0;
    }

    @Nonnull
    public NamespacedKey[] getItemIDs(@Nullable Predicate<BaseItem> filter) {
        NamespacedKey[] result;
        if (filter == null)
            result = lookupTable.keySet().toArray(NamespacedKey[]::new);
        else
            result = lookupTable.entrySet().stream().filter(new ItemFilter(filter)).map(e -> e.getKey())
                    .toArray(NamespacedKey[]::new);
        return result != null ? result : new NamespacedKey[0];
    }

    @Nonnull
    public BaseItem[] getItemTypes(@Nullable Predicate<BaseItem> filter) {
        BaseItem[] result;
        if (filter == null)
            result = lookupTable.values().toArray(BaseItem[]::new);
        else
            result = lookupTable.values().stream().filter(filter).toArray(BaseItem[]::new);
        return result != null ? result : new BaseItem[0];
    }

    private static class ItemFilter implements Predicate<Map.Entry<NamespacedKey, BaseItem>> {

        @Nonnull
        Predicate<BaseItem> filter;

        public ItemFilter(@Nonnull Predicate<BaseItem> filter) {
            this.filter = filter;
        }

        @Override
        public boolean test(Entry<NamespacedKey, BaseItem> t) {
            return filter.test(t.getValue());
        }

    }
}
