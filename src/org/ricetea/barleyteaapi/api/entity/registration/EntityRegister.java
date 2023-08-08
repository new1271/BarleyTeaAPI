package org.ricetea.barleyteaapi.api.entity.registration;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.util.Lazy;

public final class EntityRegister implements IRegister<BaseEntity> {
    @Nonnull
    private static final Lazy<EntityRegister> inst = new Lazy<>(EntityRegister::new);

    @Nonnull
    private final Hashtable<NamespacedKey, BaseEntity> lookupTable = new Hashtable<>();

    public static EntityRegister getInstance() {
        return inst.get();
    }

    public void register(@Nonnull BaseEntity entity) {
        lookupTable.put(entity.getKey(), entity);
    }

    public void unregister(@Nonnull BaseEntity entity) {
        lookupTable.remove(entity.getKey());
    }

    @Nullable
    public BaseEntity lookupEntity(@Nonnull NamespacedKey key) {
        return lookupTable.get(key);
    }

    @Nonnull
    public NamespacedKey[] getEntityIDs(@Nullable Predicate<BaseEntity> filter) {
        NamespacedKey[] result;
        if (filter == null)
            result = lookupTable.keySet().toArray(NamespacedKey[]::new);
        else
            result = lookupTable.entrySet().stream().filter(new EntityFilter(filter)).map(e -> e.getKey())
                    .toArray(NamespacedKey[]::new);
        return result != null ? result : new NamespacedKey[0];
    }

    private static class EntityFilter implements Predicate<Map.Entry<NamespacedKey, BaseEntity>> {

        @Nonnull
        Predicate<BaseEntity> filter;

        public EntityFilter(@Nonnull Predicate<BaseEntity> filter) {
            this.filter = filter;
        }

        @Override
        public boolean test(Entry<NamespacedKey, BaseEntity> t) {
            return filter.test(t.getValue());
        }

    }
}
