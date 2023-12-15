package org.ricetea.barleyteaapi.api.item.helper;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.internal.nms.helper.NMSItemHelper;
import org.ricetea.utils.ObjectUtil;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

public final class ItemHelper {
    private ItemHelper() {
        //Do nothing
    }

    @Nonnull
    private static final Multimap<Attribute, AttributeModifier> EMPTY_MAP = Objects
            .requireNonNull(ImmutableMultimap.of());

    @Nonnull
    private static final HashMap<Material, Multimap<Attribute, AttributeModifier>> defaultModifiers = new HashMap<>();

    @Nonnull
    public static Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@Nullable Material material) {
        if (material != null) {
            Multimap<Attribute, AttributeModifier> map = defaultModifiers.get(material);
            if (map == null) {
                map = NMSItemHelper.getDefaultAttributeModifiers(material);
                map = map == null ? EMPTY_MAP
                        : ObjectUtil.letNonNull(ImmutableMultimap.copyOf(map), ImmutableMultimap::of);
                defaultModifiers.put(material, map);
            }
            return map;
        }
        return EMPTY_MAP;
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
    
    public static int getDurabilityDamage(@Nullable ItemStack itemStack, @Nullable BaseItem itemType) {
        if (itemStack == null)
            return 0;
        if (itemType == null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;
                return damageable.getDamage();
            }
            return 0;
        } else if (itemType instanceof FeatureItemCustomDurability customDurabilityFeature) {
            return customDurabilityFeature.getDurabilityDamage(itemStack);
        }
        return 0;
    }

    public static void setDurabilityDamage(@Nullable ItemStack itemStack, @Nullable BaseItem itemType, int damage) {
        if (itemStack == null)
            return;
        if (itemType == null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;
                damageable.setDamage(damage);
                itemStack.setItemMeta(damageable);
            }
        } else if (itemType instanceof FeatureItemCustomDurability customDurabilityFeature) {
            customDurabilityFeature.setDurabilityDamage(itemStack, damage);
        }
    }

    public static boolean materialIsTool(Material material) {
        Material type = material;
        switch (type) {
            case WOODEN_SWORD:
            case STONE_SWORD:
            case GOLDEN_SWORD:
            case IRON_SWORD:
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
            case TRIDENT:
            case WOODEN_AXE:
            case STONE_AXE:
            case GOLDEN_AXE:
            case IRON_AXE:
            case DIAMOND_AXE:
            case NETHERITE_AXE:
            case WOODEN_PICKAXE:
            case STONE_PICKAXE:
            case GOLDEN_PICKAXE:
            case IRON_PICKAXE:
            case DIAMOND_PICKAXE:
            case NETHERITE_PICKAXE:
            case WOODEN_SHOVEL:
            case STONE_SHOVEL:
            case GOLDEN_SHOVEL:
            case IRON_SHOVEL:
            case DIAMOND_SHOVEL:
            case NETHERITE_SHOVEL:
            case WOODEN_HOE:
            case STONE_HOE:
            case GOLDEN_HOE:
            case IRON_HOE:
            case DIAMOND_HOE:
            case NETHERITE_HOE:
                return true;
            default:
                return false;
        }
    }
}
