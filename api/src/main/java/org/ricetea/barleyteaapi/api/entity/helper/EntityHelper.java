package org.ricetea.barleyteaapi.api.entity.helper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Unmodifiable;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.barleyteaapi.api.helper.ChatColorHelper;
import org.ricetea.barleyteaapi.api.internal.entity.EntityHelperInternals;
import org.ricetea.barleyteaapi.api.internal.nms.INMSEntityHelper;
import org.ricetea.barleyteaapi.api.task.TaskOption;
import org.ricetea.barleyteaapi.api.task.TaskService;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Cache;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class EntityHelper {

    @Nonnull
    public static final NamespacedKey DefaultNamespacedKey = NamespacedKeyUtil.BarleyTeaAPI("entity_id");
    @Nonnull
    private static final ConcurrentHashMap<NamespacedKey, Function<String, NamespacedKey>> FallbackNamespacedKeys = new ConcurrentHashMap<>();
    @Nonnull
    private static final Cache<Set<NamespacedKey>> FallbackNamespacedKeyCache =
            Cache.createThreadSafe(() -> Collections.unmodifiableSet(FallbackNamespacedKeys.keySet()));


    public static void addFallbackNamespacedKey(@Nullable NamespacedKey key) {
        addFallbackNamespacedKey(key, null);
    }

    public static void addFallbackNamespacedKey(@Nullable NamespacedKey key,
                                                @Nullable Function<String, NamespacedKey> converter) {
        if (key != null) {
            FallbackNamespacedKeys.putIfAbsent(key, converter == null ? NamespacedKey::fromString : converter);
            FallbackNamespacedKeyCache.reset();
        }
    }

    public static void removeFallbackNamespacedKey(@Nullable NamespacedKey key) {
        if (key != null) {
            FallbackNamespacedKeys.remove(key);
            FallbackNamespacedKeyCache.reset();
        }
    }

    @Nonnull
    @Unmodifiable
    public static Set<NamespacedKey> getFallbackNamespacedKeys() {
        return FallbackNamespacedKeyCache.get();
    }

    @Nullable
    public static NamespacedKey getEntityID(@Nullable Entity entity) {
        return getEntityID(entity, true);
    }

    @Nullable
    public static NamespacedKey getEntityID(@Nullable Entity entity, boolean cached) {
        if (entity == null)
            return null;
        EntityHelperInternals internals = EntityHelperInternals.getInstanceUnsafe();
        if (internals != null && cached) {
            var box = internals.getCachedEntityID(entity);
            if (box != null) {
                return box.get();
            }
        }
        NamespacedKey result;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        String namespacedKeyString = container.get(DefaultNamespacedKey, PersistentDataType.STRING);
        if (namespacedKeyString == null) {
            result = null;
            if (!FallbackNamespacedKeys.isEmpty()) {
                for (Map.Entry<NamespacedKey, Function<String, NamespacedKey>> entry : FallbackNamespacedKeys.entrySet()) {
                    NamespacedKey key = entry.getKey();
                    if (key != null) {
                        namespacedKeyString = container.get(key, PersistentDataType.STRING);
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
        if (internals != null) {
            internals.setCachedEntityID(entity, result);
        }
        return result;
    }

    public static boolean isCustomEntity(@Nullable Entity entity) {
        return entity != null && getEntityID(entity) != null;
    }

    public static boolean isCertainEntity(@Nullable CustomEntity entityType, @Nullable Entity entity) {
        return entityType != null && entity != null && entityType.getKey().equals(getEntityID(entity));
    }

    public static void register(@Nullable CustomEntity entityType, @Nullable Entity entity) {
        register(entityType, entity, null);
    }

    public static <T extends Entity> void register(@Nullable CustomEntity entityType, @Nullable T entity,
                                                   @Nullable Consumer<T> afterEntityRegistered) {
        if (entityType != null && entity != null) {
            NamespacedKey key = entityType.getKey();
            entity.getPersistentDataContainer().set(DefaultNamespacedKey, PersistentDataType.STRING, key.toString());
            if (afterEntityRegistered != null) {
                afterEntityRegistered.accept(entity);
            }
            EntityHelperInternals internals = EntityHelperInternals.getInstanceUnsafe();
            if (internals != null) {
                internals.setCachedEntityID(entity, key);
            }
        }
    }

    public static <T extends Entity> boolean tryRegister(@Nullable CustomEntity entityType, @Nullable T entity,
                                                         @Nullable Predicate<T> afterEntityRegistered) {
        if (entityType != null && entity != null) {
            PersistentDataContainer container = entity.getPersistentDataContainer();
            String previousID = container.get(DefaultNamespacedKey, PersistentDataType.STRING);
            NamespacedKey key = entityType.getKey();
            entity.getPersistentDataContainer().set(DefaultNamespacedKey, PersistentDataType.STRING, key.toString());
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
            EntityHelperInternals internals = EntityHelperInternals.getInstanceUnsafe();
            if (internals != null) {
                internals.setCachedEntityID(entity, key);
            }
            return true;
        }
        return false;
    }

    @Nonnull
    public static Component getDefaultNameComponent(@Nonnull CustomEntityType entityType) {
        return entityType.nonNullMap(EntityHelper::getDefaultNameComponent, EntityHelper::getDefaultNameComponent);
    }

    @Nonnull
    public static Component getDefaultNameComponent(@Nonnull CustomEntity entityType) {
        return Component.translatable(entityType.getTranslationKey());
    }

    @Nonnull
    public static Component getDefaultNameComponent(@Nonnull EntityType entityType) {
        return Component.translatable(entityType.translationKey());
    }

    @Deprecated
    public static void setEntityName(@Nonnull CustomEntity entityType, @Nonnull Entity entity, @Nullable org.bukkit.ChatColor... colorAndStyles) {
        setEntityName(entityType, entity, ChatColorHelper.toKyoriStyle(colorAndStyles));
    }

    @Deprecated
    public static void setEntityName(@Nonnull CustomEntity entityType, @Nonnull Entity entity,
                                     @Nullable net.md_5.bungee.api.ChatColor... colorAndStyles) {
        setEntityName(entityType, entity, ChatColorHelper.toKyoriStyle(colorAndStyles));
    }

    public static void setEntityName(@Nonnull CustomEntity entityType, @Nonnull Entity entity) {
        setEntityName(entity, getDefaultNameComponent(entityType));
    }

    public static void setEntityName(@Nonnull CustomEntity entityType, @Nonnull Entity entity,
                                     @Nullable TextColor color, @Nullable TextDecoration... decoration) {
        if (decoration == null)
            setEntityName(entityType, entity, Style.style(color));
        else
            setEntityName(entityType, entity, Style.style(color, decoration));
    }

    public static void setEntityName(@Nonnull CustomEntity entityType, @Nonnull Entity entity, @Nullable Style style) {
        setEntityName(entity, style == null ? getDefaultNameComponent(entityType)
                : Objects.requireNonNull(getDefaultNameComponent(entityType).style(style)));
    }

    @Deprecated
    public static void setEntityName(@Nonnull Entity entity, @Nonnull String name,
                                     @Nullable org.bukkit.ChatColor... colorAndStyles) {
        setEntityName(entity, name, ChatColorHelper.toKyoriStyle(colorAndStyles));
    }

    @Deprecated
    public static void setEntityName(@Nonnull Entity entity, @Nonnull String name,
                                     @Nullable net.md_5.bungee.api.ChatColor... colorAndStyles) {
        setEntityName(entity, name, ChatColorHelper.toKyoriStyle(colorAndStyles));
    }

    public static void setEntityName(@Nonnull Entity entity, @Nonnull String name) {
        setEntityName(entity, name, null, (TextDecoration[]) null);
    }

    public static void setEntityName(@Nonnull Entity entity, @Nonnull String name, @Nullable TextColor color,
                                     @Nullable TextDecoration... decorations) {
        setEntityName(entity, name, decorations == null ? Style.style(color) : Style.style(color, decorations));
    }

    public static void setEntityName(@Nonnull Entity entity, @Nonnull String name, @Nullable Style style) {
        setEntityName(entity,
                Objects.requireNonNull(style == null ? Component.text(name) : Component.text(name, style)));
    }

    public static void setEntityName(@Nonnull Entity entity, @Nonnull Component component) {
        entity.customName(component);
    }

    @Nullable
    public static AttributeInstance getAttribute(@Nullable Entity entity, @Nullable Attribute attribute) {
        if (entity instanceof LivingEntity livingEntity && attribute != null)
            return livingEntity.getAttribute(attribute);
        return null;
    }

    public static void setAttribute(@Nullable Entity entity, @Nullable Attribute attribute, double baseValue) {
        AttributeInstance attributeInstance = getAttribute(entity, attribute);
        if (attributeInstance != null)
            attributeInstance.setBaseValue(baseValue);
    }

    public static double getBaseMaxHealth(@Nullable Entity entity) {
        if (entity instanceof LivingEntity) {
            AttributeInstance attributeInstance = getAttribute(entity, Attribute.GENERIC_MAX_HEALTH);
            if (attributeInstance != null)
                return attributeInstance.getBaseValue();
        }
        return 0.0;
    }

    public static double getMaxHealth(@Nullable Entity entity) {
        if (entity instanceof LivingEntity) {
            AttributeInstance attributeInstance = getAttribute(entity, Attribute.GENERIC_MAX_HEALTH);
            if (attributeInstance != null)
                return attributeInstance.getValue();
        }
        return 0.0;
    }

    public static double getHealth(@Nullable Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity.getHealth();
        }
        return 0.0;
    }

    public static void setHealth(@Nullable Entity entity, double health) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setHealth(Math.max(Math.min(health, getMaxHealth(livingEntity)), 0));
        }
    }

    public static void setAsMaxHealth(@Nullable Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setHealth(getMaxHealth(livingEntity));
        }
    }

    public static boolean regenHealth(@Nullable Entity entity, double value) {
        return regenHealth(entity, value, EntityRegainHealthEvent.RegainReason.REGEN);
    }

    public static boolean regenHealth(@Nullable Entity entity, double value, boolean shouldPassEvent) {
        return regenHealth(entity, value, shouldPassEvent ? EntityRegainHealthEvent.RegainReason.REGEN : null);
    }

    public static boolean regenHealth(@Nullable Entity entity, double value, @Nullable EntityRegainHealthEvent.RegainReason reason) {
        if (entity instanceof LivingEntity livingEntity) {
            if (reason != null) {
                EntityRegainHealthEvent event = new EntityRegainHealthEvent(entity, value, reason);
                event.setCancelled(false);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled())
                    return false;
                else
                    value = event.getAmount();
            }
            livingEntity.setHealth(Math.max(Math.min(livingEntity.getHealth() + value, getMaxHealth(livingEntity)), 0));
            return true;
        }
        return false;
    }

    @Nullable
    public static Entity getProjectileShooterEntity(@Nullable Projectile projectile) {
        return ObjectUtil.tryCast(ObjectUtil.safeMap(projectile, Projectile::getShooter), Entity.class);
    }

    public static boolean isPlayer(@Nullable CustomEntityType entityType) {
        if (entityType == null)
            return false;
        EntityType type = entityType.asEntityType();
        if (type == null)
            return false;
        else {
            return type.equals(EntityType.PLAYER);
        }
    }

    public static boolean damage(@Nullable Entity damagee, @Nullable DamageCause damageCause, float damage) {
        return damage(damagee, null, damageCause, damage);
    }

    public static boolean damage(@Nullable Entity damagee, @Nullable Entity damager, @Nullable DamageCause damageCause,
                                 float damage) {
        if (damagee == null)
            return false;
        INMSEntityHelper helper = Bukkit.getServicesManager().load(INMSEntityHelper.class);
        if (helper == null)
            return false;
        return helper.damage(damagee, damager, damageCause, damage, true);
    }

    public static boolean damageWithDifficultyScaling(@Nullable Entity damagee, @Nullable DamageCause damageCause,
                                                      float damage) {
        return damageWithDifficultyScaling(damagee, null, damageCause, damage);
    }

    public static boolean damageWithDifficultyScaling(@Nullable Entity damagee, @Nullable Entity damager,
                                                      @Nullable DamageCause damageCause, float damage) {
        if (damagee == null)
            return false;
        INMSEntityHelper helper = Bukkit.getServicesManager().load(INMSEntityHelper.class);
        if (helper == null)
            return false;
        return helper.damage(damagee, damager, damageCause, damage, false);
    }
}
