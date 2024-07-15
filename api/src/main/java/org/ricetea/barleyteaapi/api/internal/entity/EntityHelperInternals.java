package org.ricetea.barleyteaapi.api.internal.entity;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.task.TaskOption;
import org.ricetea.barleyteaapi.api.task.TaskService;
import org.ricetea.utils.Box;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;

public interface EntityHelperInternals {

    @Nonnull
    static EntityHelperInternals getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static EntityHelperInternals getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(EntityHelperInternals.class);
    }

    @Nullable
    Box<NamespacedKey> getCachedEntityID(@Nonnull Entity entity);

    default void setCachedEntityID(@Nonnull Entity entity, @Nullable NamespacedKey key) {
        TaskService service = TaskService.getInstanceUnsafe();
        WeakReference<Entity> entityRef = new WeakReference<>(entity);
        if (service == null)
            setCachedEntityID(entityRef, key);
        else {
            service.runTask(() ->
                            setCachedEntityID(entityRef, key),
                    TaskOption.LongRunning
            );
        }
    }

    default void removeCachedEntity(@Nonnull Entity entity) {
        TaskService service = TaskService.getInstanceUnsafe();
        WeakReference<Entity> entityRef = new WeakReference<>(entity);
        if (service == null)
            removeCachedEntity(entityRef);
        else {
            service.runTask(() ->
                            removeCachedEntity(entityRef),
                    TaskOption.LongRunning
            );
        }
    }

    void setCachedEntityID(@Nonnull Reference<Entity> entity, @Nullable NamespacedKey key);

    void removeCachedEntity(@Nonnull Reference<Entity> entity);
}
