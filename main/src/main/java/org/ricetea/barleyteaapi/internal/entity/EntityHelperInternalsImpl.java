package org.ricetea.barleyteaapi.internal.entity;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.internal.entity.EntityHelperInternals;
import org.ricetea.utils.Box;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.Reference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

public final class EntityHelperInternalsImpl implements EntityHelperInternals {

    private final Map<Entity, Object> map = new WeakHashMap<>();
    private final ReadWriteLock locker = new ReentrantReadWriteLock();

    @Nullable
    @Override
    public Box<NamespacedKey> getCachedEntityID(@Nonnull Entity entity) {
        Lock lock = locker.readLock();
        Object value;
        try {
            if (lock.tryLock(850, TimeUnit.NANOSECONDS)) {
                value = map.get(entity);
            } else {
                value = null;
            }
        } catch (InterruptedException e) {
            value = null;
        } catch (Exception e) {
            value = null;
            BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
            if (api == null)
                Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
            else
                api.getLogger().log(Level.SEVERE, e.getMessage(), e);
        } finally {
            ObjectUtil.tryCallSilently(lock, Lock::unlock);
        }
        if (value == null)
            return null;
        return Box.box(ObjectUtil.tryCast(value, NamespacedKey.class));
    }

    @Override
    public void setCachedEntityID(@Nonnull Reference<Entity> entityRef, @Nullable NamespacedKey key) {
        Lock lock = locker.writeLock();
        try {
            if (lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                Entity entity = entityRef.get();
                if (entity != null) {
                    map.put(entity, key == null ? this : key);
                }
            }
        } catch (Exception e) {
            BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
            if (api == null)
                Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
            else
                api.getLogger().log(Level.SEVERE, e.getMessage(), e);
        } finally {
            ObjectUtil.tryCallSilently(lock, Lock::unlock);
        }
    }

    @Override
    public void removeCachedEntity(@Nonnull Reference<Entity> entityRef) {
        Lock lock = locker.writeLock();
        try {
            if (lock.tryLock(1000, TimeUnit.MILLISECONDS)){
                Entity entity = entityRef.get();
                if (entity != null) {
                    map.remove(entity);
                }
            }
        } catch (Exception e) {
            BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
            if (api == null)
                Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
            else
                api.getLogger().log(Level.SEVERE, e.getMessage(), e);
        } finally {
            ObjectUtil.tryCallSilently(lock, Lock::unlock);
        }
    }
}
