package org.ricetea.barleyteaapi.internal.entity;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.internal.entity.EntityHelperInternals;
import org.ricetea.utils.Box;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class EntityHelperInternalsImpl implements EntityHelperInternals {

    private final Map<Entity, Object> map = new ConcurrentHashMap<>();

    @Nullable
    @Override
    public Box<NamespacedKey> getCachedEntityID(@Nonnull Entity entity) {
        Object value = map.get(entity);
        if (value == null)
            return null;
        return Box.box(ObjectUtil.tryCast(value, NamespacedKey.class));
    }

    @Override
    public void setCachedEntityID(@Nonnull Entity entity, @Nullable NamespacedKey key) {
        map.put(entity, key == null ? this : key);
    }

    @Override
    public void removeCachedEntity(@Nonnull Entity entity) {
        map.remove(entity);
    }
}
