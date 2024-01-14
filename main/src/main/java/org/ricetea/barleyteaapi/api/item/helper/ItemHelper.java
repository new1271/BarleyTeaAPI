package org.ricetea.barleyteaapi.api.item.helper;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Unmodifiable;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.persistence.ExtraPersistentDataType;
import org.ricetea.barleyteaapi.internal.nms.INMSItemHelper;
import org.ricetea.barleyteaapi.internal.nms.NMSHelperRegister;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Cache;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ItemHelper {
    @Nonnull
    public static final NamespacedKey DefaultNamespacedKey = NamespacedKeyUtil.BarleyTeaAPI("item_id");

    @Nonnull
    private static final Multimap<Attribute, AttributeModifier> EMPTY_MAP = Objects
            .requireNonNull(ImmutableMultimap.of());

    @Nonnull
    private static final HashMap<Material, Multimap<Attribute, AttributeModifier>> defaultModifiers = new HashMap<>();

    @Nonnull
    private static final HashMap<NamespacedKey, Function<String, NamespacedKey>> FallbackNamespacedKeys = new HashMap<>();

    @Nonnull
    private static final Cache<Set<NamespacedKey>> FallbackNamespacedKeyCache =
            Cache.createThreadSafe(() -> Collections.unmodifiableSet(FallbackNamespacedKeys.keySet()));

    @Nonnull
    public static Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@Nullable Material material) {
        if (material == null)
            return EMPTY_MAP;
        return defaultModifiers.computeIfAbsent(material, ItemHelper::getDefaultAttributeModifiers_NoCache);
    }

    private static Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers_NoCache(@Nonnull Material material) {
        Multimap<Attribute, AttributeModifier> map;
        INMSItemHelper itemHelper = NMSHelperRegister.getHelper(INMSItemHelper.class);
        if (itemHelper == null) {
            map = material.getDefaultAttributeModifiers(material.getEquipmentSlot());
        } else {
            map = itemHelper.getDefaultAttributeModifiers(material);
        }
        return ObjectUtil.letNonNull(ObjectUtil.safeMap(map, ImmutableMultimap::copyOf), ImmutableMultimap::of);
    }

    @Nonnull
    public static Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@Nullable ItemStack itemStack) {
        return itemStack == null ? EMPTY_MAP : getDefaultAttributeModifiers(itemStack.getType());
    }

    @Nonnull
    public static ItemStack getSingletonClone(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return new ItemStack(Material.AIR);
        } else if (itemStack.getType().isAir()) {
            return itemStack;
        } else {
            ItemStack result = itemStack.clone();
            if (result.getAmount() > 1)
                result.setAmount(1);
            return result;
        }
    }

    public static void setToolAttackDamage(@Nullable ItemStack itemStack, double attackDamage) {
        setDefaultAttribute(itemStack, Attribute.GENERIC_ATTACK_DAMAGE, attackDamage - 1.0, Operation.ADD_NUMBER,
                EquipmentSlot.HAND);
    }

    public static void setToolAttackDamage(@Nullable ItemMeta itemMeta, double attackDamage) {
        setDefaultAttribute(itemMeta, Attribute.GENERIC_ATTACK_DAMAGE, attackDamage - 1.0, Operation.ADD_NUMBER,
                EquipmentSlot.HAND);
    }

    public static void setToolAttackSpeed(@Nullable ItemStack itemStack, double attackSpeed) {
        setDefaultAttribute(itemStack, Attribute.GENERIC_ATTACK_SPEED, attackSpeed - 4.0, Operation.ADD_NUMBER,
                EquipmentSlot.HAND);
    }

    public static void setToolAttackSpeed(@Nullable ItemMeta itemMeta, double attackSpeed) {
        setDefaultAttribute(itemMeta, Attribute.GENERIC_ATTACK_SPEED, attackSpeed - 4.0, Operation.ADD_NUMBER,
                EquipmentSlot.HAND);
    }

    public static void setDefaultAttribute(@Nullable ItemStack itemStack, @Nullable Attribute attribute,
                                           double amount, @Nullable Operation operation, @Nullable EquipmentSlot equipmentSlot) {
        if (itemStack != null && attribute != null && operation != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                setDefaultAttribute(meta, attribute, amount, operation, equipmentSlot);
                itemStack.setItemMeta(meta);
            }
        }
    }

    public static void setDefaultAttribute(@Nullable ItemMeta itemMeta, @Nullable Attribute attribute,
                                           double amount, @Nullable Operation operation, @Nullable EquipmentSlot equipmentSlot) {
        if (itemMeta == null || attribute == null || operation == null)
            return;
        itemMeta.removeAttributeModifier(attribute);
        itemMeta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(),
                "default modifiers", amount, operation, equipmentSlot));
    }

    public static int getDurabilityDamage(@Nonnull CustomItemType itemType, @Nullable ItemStack itemStack) {
        if (itemStack == null)
            return 0;
        return itemType.nonNullMap(ignored -> {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof Damageable damageable) {
                return damageable.getDamage();
            }
            return 0;
        }, type -> {
            if (type instanceof FeatureItemCustomDurability feature)
                return feature.getDurabilityDamage(itemStack);
            return 0;
        });
    }

    public static void setDurabilityDamage(@Nonnull CustomItemType itemType,
                                           @Nullable ItemStack itemStack, int damage) {
        if (itemStack == null)
            return;
        itemType.call(ignored -> {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(damage);
                itemStack.setItemMeta(damageable);
            }
        }, type -> {
            if (type instanceof FeatureItemCustomDurability feature)
                feature.setDurabilityDamage(itemStack, damage);
        });
    }

    public static boolean materialIsTool(Material material) {
        return switch (material) {
            case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD,
                    DIAMOND_SWORD, NETHERITE_SWORD, TRIDENT, WOODEN_AXE,
                    STONE_AXE, GOLDEN_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE,
                    WOODEN_PICKAXE, STONE_PICKAXE, GOLDEN_PICKAXE, IRON_PICKAXE,
                    DIAMOND_PICKAXE, NETHERITE_PICKAXE, WOODEN_SHOVEL, STONE_SHOVEL,
                    GOLDEN_SHOVEL, IRON_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL,
                    WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> true;
            default -> false;
        };
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

    @Nonnull
    @Unmodifiable
    public static Set<NamespacedKey> getFallbackNamespacedKeys() {
        return FallbackNamespacedKeyCache.get();
    }

    public static boolean isCustomItem(@Nullable ItemStack itemStack) {
        return getItemID(itemStack) != null;
    }

    @Nullable
    public static NamespacedKey getItemID(@Nullable ItemStack itemStack) {
        if (itemStack == null)
            return null;
        return getItemID(itemStack.getItemMeta());
    }

    @Nullable
    public static NamespacedKey getItemID(@Nullable ItemMeta itemMeta) {
        if (itemMeta == null)
            return null;
        NamespacedKey result;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
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
        return result;
    }

    public static boolean isCertainItem(@Nonnull CustomItem itemType, @Nullable ItemStack itemStack) {
        return itemStack != null && itemType.getKey().equals(getItemID(itemStack));
    }

    @Nonnull
    public static Component getDefaultNameComponent(@Nonnull CustomItemType itemType) {
        return itemType.nonNullMap(ItemHelper::getDefaultNameComponent, ItemHelper::getDefaultNameComponent);
    }

    @Nonnull
    public static Component getDefaultNameComponent(@Nonnull CustomItem itemType) {
        return Component.translatable(itemType.getTranslationKey());
    }

    @Nonnull
    public static Component getDefaultNameComponent(@Nonnull Material itemType) {
        return Component.translatable(Objects.requireNonNull(itemType.getItemTranslationKey()));
    }

    public static void register(@Nullable CustomItem itemType, @Nullable ItemStack itemStack) {
        register(itemType, itemStack, null);
    }

    public static void register(@Nullable CustomItem itemType, @Nullable ItemStack itemStack,
                                @Nullable Consumer<ItemStack> afterItemStackRegistered) {
        if (itemType != null && itemStack != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.getPersistentDataContainer().set(DefaultNamespacedKey, ExtraPersistentDataType.NAMESPACED_KEY,
                        itemType.getKey());
                itemStack.setItemMeta(meta);
                if (afterItemStackRegistered != null) {
                    afterItemStackRegistered.accept(itemStack);
                }
            }
        }
    }

    public static boolean tryRegister(@Nullable CustomItem itemType, @Nullable ItemStack itemStack,
                                      @Nullable Predicate<ItemStack> afterItemStackRegistered) {
        if (itemType != null && itemStack != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey oldKey = getItemID(meta);
                container.set(DefaultNamespacedKey, ExtraPersistentDataType.NAMESPACED_KEY,
                        itemType.getKey());
                itemStack.setItemMeta(meta);
                if (afterItemStackRegistered != null && !afterItemStackRegistered.test(itemStack)) {
                    meta = itemStack.getItemMeta();
                    if (meta != null) {
                        container = meta.getPersistentDataContainer();
                        if (oldKey == null)
                            container.remove(DefaultNamespacedKey);
                        else
                            container.set(DefaultNamespacedKey, ExtraPersistentDataType.NAMESPACED_KEY, oldKey);
                        itemStack.setItemMeta(meta);
                    }
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}
