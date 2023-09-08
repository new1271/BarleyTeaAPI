package org.ricetea.barleyteaapi.api.entity;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.helper.ChatColorHelper;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public abstract class BaseEntity implements Keyed {
    @Nonnull
    private static final NamespacedKey DefaultNamespacedKey = NamespacedKeyUtils.BarleyTeaAPI("entity_id");
    @Nonnull
    private static final HashMap<NamespacedKey, Function<String, NamespacedKey>> FallbackNamespacedKeys = new HashMap<>();
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
        return "entity." + key.getNamespace() + "." + key.getKey();
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
            entity.getPersistentDataContainer().set(DefaultNamespacedKey,
                    PersistentDataType.STRING, key.toString());
    }

    public final <T extends Entity> void register(@Nullable T entity,
            @Nullable Consumer<T> afterEntityRegistered) {
        if (entity != null) {
            entity.getPersistentDataContainer().set(DefaultNamespacedKey,
                    PersistentDataType.STRING, key.toString());
            if (afterEntityRegistered != null) {
                afterEntityRegistered.accept(entity);
            }
        }
    }

    public final <T extends Entity> boolean tryRegister(@Nullable T entity,
            @Nullable Predicate<T> afterEntityRegistered) {
        if (entity != null) {
            PersistentDataContainer container = entity.getPersistentDataContainer();
            String previousID = container.getOrDefault(DefaultNamespacedKey, PersistentDataType.STRING, null);
            container.set(DefaultNamespacedKey, PersistentDataType.STRING, key.toString());
            if (afterEntityRegistered != null) {
                if (!afterEntityRegistered.test(entity)) {
                    if (!entity.isDead())
                        if (previousID == null)
                            container.remove(DefaultNamespacedKey);
                        else
                            container.set(DefaultNamespacedKey, PersistentDataType.STRING, previousID);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public final boolean isCertainEntity(@Nullable Entity entity) {
        return entity != null
                && key.toString().equals(entity.getPersistentDataContainer().getOrDefault(DefaultNamespacedKey,
                        PersistentDataType.STRING, null));
    }

    public static void registerEntity(@Nullable Entity entity, @Nonnull BaseEntity entityType) {
        entityType.register(entity);
    }

    public static boolean isBarleyTeaEntity(@Nullable Entity entity) {
        return entity != null && getEntityID(entity) != null;
    }

    @Nullable
    public static NamespacedKey getEntityID(@Nullable Entity entity) {
        if (entity == null)
            return null;
        NamespacedKey result;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        String namespacedKeyString = container.getOrDefault(DefaultNamespacedKey, PersistentDataType.STRING, null);
        if (namespacedKeyString == null) {
            result = null;
            if (!FallbackNamespacedKeys.isEmpty()) {
                for (var iterator = FallbackNamespacedKeys.entrySet().iterator(); iterator.hasNext();) {
                    var entry = iterator.next();
                    NamespacedKey key = entry.getKey();
                    if (key != null) {
                        namespacedKeyString = container.getOrDefault(key, PersistentDataType.STRING, null);
                        if (namespacedKeyString != null) {
                            Function<String, NamespacedKey> function = entry.getValue();
                            result = function == null ? NamespacedKey.fromString(namespacedKeyString)
                                    : function.apply(namespacedKeyString);
                            break;
                        }
                    }
                }
            }
        } else {
            result = NamespacedKey.fromString(namespacedKeyString);
        }
        return result;
    }

    public static void addFallbackNamespacedKey(@Nullable NamespacedKey key) {
        addFallbackNamespacedKey(key, null);
    }

    public static void addFallbackNamespacedKey(@Nullable NamespacedKey key,
            @Nullable Function<String, NamespacedKey> converter) {
        if (key != null && !FallbackNamespacedKeys.containsKey(key)) {
            FallbackNamespacedKeys.put(key, converter == null ? NamespacedKey::fromString : converter);
        }
    }

    public static void removeFallbackNamespacedKey(@Nullable NamespacedKey key) {
        FallbackNamespacedKeys.remove(key);
    }

    public static boolean isCertainEntity(@Nullable Entity entity, @Nonnull BaseEntity entityType) {
        return entityType.isCertainEntity(entity);
    }

    @Nonnull
    public static DataEntityType getEntityType(@Nonnull Entity entity) {
        NamespacedKey entityTypeID = BaseEntity.getEntityID(entity);
        if (entityTypeID == null) {
            return DataEntityType.create(entity.getType());
        } else {
            EntityRegister register = EntityRegister.getInstanceUnsafe();
            if (register == null) {
                return DataEntityType.create(entity.getType());
            } else {
                BaseEntity baseEntity = register.lookup(entityTypeID);
                if (baseEntity == null)
                    return DataEntityType.create(entity.getType());
                else
                    return DataEntityType.create(baseEntity);
            }
        }
    }

    @Nonnull
    public final Component getDefaultNameComponent() {
        return Objects.requireNonNull(Component.translatable(getNameInTranslateKey(), getDefaultName()));
    }

    @Deprecated
    protected final void setEntityName(@Nonnull Entity entity, @Nullable org.bukkit.ChatColor... colorAndStyles) {
        setEntityName(entity, ChatColorHelper.toKyoriStyle(colorAndStyles));
    }

    @Deprecated
    protected final void setEntityName(@Nonnull Entity entity, @Nonnull String name,
            @Nullable org.bukkit.ChatColor... colorAndStyles) {
        setEntityName(entity, name, ChatColorHelper.toKyoriStyle(colorAndStyles));
    }

    protected final void setEntityName(@Nonnull Entity entity,
            @Nullable net.md_5.bungee.api.ChatColor... colorAndStyles) {
        setEntityName(entity, ChatColorHelper.toKyoriStyle(colorAndStyles));
    }

    protected final void setEntityName(@Nonnull Entity entity, @Nonnull String name,
            @Nullable net.md_5.bungee.api.ChatColor... colorAndStyles) {
        setEntityName(entity, name, ChatColorHelper.toKyoriStyle(colorAndStyles));
    }

    protected final void setEntityName(@Nonnull Entity entity) {
        setEntityName(entity, getDefaultNameComponent());
    }

    protected final void setEntityName(@Nonnull Entity entity, @Nullable TextColor color,
            @Nullable TextDecoration... decoration) {
        setEntityName(entity, Style.style(color, decoration));
    }

    protected final void setEntityName(@Nonnull Entity entity, @Nullable Style style) {
        setEntityName(entity, style == null ? getDefaultNameComponent()
                : Objects.requireNonNull(getDefaultNameComponent().style(style)));
    }

    protected final void setEntityName(@Nonnull Entity entity, @Nonnull String name) {
        setEntityName(entity, name, null, (TextDecoration[]) null);
    }

    protected final void setEntityName(@Nonnull Entity entity, @Nonnull String name, @Nullable TextColor color,
            @Nullable TextDecoration... decorations) {
        setEntityName(entity, name, decorations == null ? Style.style(color) : Style.style(color, decorations));
    }

    protected final void setEntityName(@Nonnull Entity entity, @Nonnull String name, @Nullable Style style) {
        setEntityName(entity,
                Objects.requireNonNull(style == null ? Component.text(name) : Component.text(name, style)));
    }

    protected final void setEntityName(@Nonnull Entity entity, @Nonnull Component component) {
        entity.customName(component);
    }

    @Nullable
    protected AttributeInstance getAttribute(@Nullable Entity entity, @Nullable Attribute attribute) {
        if (entity instanceof LivingEntity livingEntity && attribute != null)
            return livingEntity.getAttribute(attribute);
        return null;
    }

    protected void setAttribute(@Nullable Entity entity, @Nullable Attribute attribute, double baseValue) {
        AttributeInstance attributeInstance = getAttribute(entity, attribute);
        if (attributeInstance != null)
            attributeInstance.setBaseValue(baseValue);
    }

    public boolean equals(Object obj) {
        if (obj instanceof BaseEntity baseEntity) {
            return key.equals(baseEntity.getKey());
        }
        return super.equals(obj);
    }
}
