package org.ricetea.barleyteaapi.api.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

public abstract class BaseEntity implements Keyed {
    @Nonnull
    private static final NamespacedKey EntityTagNamespacedKey = NamespacedKeyUtils.BarleyTeaAPI("entity_id");
    @Nonnull
    private final NamespacedKey key;
    @Nonnull
    private final EntityType entityTypeBasedOn;

    public BaseEntity(@Nonnull NamespacedKey key, @Nonnull EntityType entityTypeBasedOn) {
        this.key = key;
        this.entityTypeBasedOn = entityTypeBasedOn;
    }

    @Nonnull
    public final NamespacedKey getKey() {
        return key;
    }

    @Nonnull
    public final String getNameInTranslateKey() {
        return "entity." + key.getNamespace() + "." + key.getKey() + ".name";
    }

    @Nonnull
    public String getDefaultName() {
        return getNameInTranslateKey();
    }

    @Nonnull
    public final EntityType getEntityTypeBasedOn() {
        return entityTypeBasedOn;
    }

    public final void register(@Nullable Entity entity) {
        if (entity != null)
            entity.getPersistentDataContainer().set(EntityTagNamespacedKey,
                    PersistentDataType.STRING, key.toString());
    }

    public final boolean isCertainEntity(@Nullable Entity entity) {
        return entity != null
                && key.toString().equals(entity.getPersistentDataContainer().getOrDefault(EntityTagNamespacedKey,
                        PersistentDataType.STRING, null));
    }

    public static void registerEntity(@Nullable Entity entity, @Nonnull BaseEntity entityType) {
        entityType.register(entity);
    }

    public static boolean isEntity(@Nullable Entity entity) {
        return entity != null && entity.getPersistentDataContainer().has(EntityTagNamespacedKey);
    }

    @Nullable
    public static NamespacedKey getEntityID(@Nullable Entity entity) {
        if (entity == null)
            return null;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        String namespacedKeyString = container.getOrDefault(EntityTagNamespacedKey, PersistentDataType.STRING, null);
        return namespacedKeyString == null ? null
                : namespacedKeyString.contains(":") ? NamespacedKey.fromString(namespacedKeyString) : null;
    }

    public static boolean isCertainEntity(@Nullable Entity entity, @Nonnull BaseEntity entityType) {
        return entityType.isCertainEntity(entity);
    }

    @Nonnull
    public static BarleyTeaEntityType getEntityType(Entity entity) {
        NamespacedKey entityTypeID = BaseEntity.getEntityID(entity);
        if (entityTypeID == null) {
            return BarleyTeaEntityType.create(entity.getType());
        } else {
            BaseEntity baseEntity = EntityRegister.getInstance().lookupEntityType(entityTypeID);
            if (baseEntity == null)
                return BarleyTeaEntityType.create(entity.getType());
            else
                return BarleyTeaEntityType.create(baseEntity);
        }
    }
}
