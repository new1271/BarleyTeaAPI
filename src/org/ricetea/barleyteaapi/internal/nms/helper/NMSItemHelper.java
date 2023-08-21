package org.ricetea.barleyteaapi.internal.nms.helper;

import javax.annotation.Nullable;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_20_R1.attribute.CraftAttributeMap;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public final class NMSItemHelper {
    @Nullable
    public static net.minecraft.world.item.ItemStack getNmsItem(@Nullable org.bukkit.inventory.ItemStack itemStack) {
        if (itemStack == null)
            return null;
        return itemStack instanceof CraftItemStack craftItemStack ? craftItemStack.handle
                : CraftItemStack.asNMSCopy(itemStack);
    }

    @Nullable
    public static Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(
            @Nullable org.bukkit.inventory.ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = getNmsItem(itemStack);
        if (nmsItemStack != null) {
            // net.minecraft.world.item.Item getItem() -> d
            net.minecraft.world.item.Item item = nmsItemStack.d();
            Multimap<Attribute, AttributeModifier> result = LinkedHashMultimap.create();
            //com.google.common.collect.Multimap getDefaultAttributeModifiers(net.minecraft.world.entity.EquipmentSlot) -> a
            /*
            * net.minecraft.world.entity.EquipmentSlot -> bfo:
            net.minecraft.world.entity.EquipmentSlot MAINHAND -> a
            net.minecraft.world.entity.EquipmentSlot OFFHAND -> b
            net.minecraft.world.entity.EquipmentSlot FEET -> c
            net.minecraft.world.entity.EquipmentSlot LEGS -> d
            net.minecraft.world.entity.EquipmentSlot CHEST -> e
            net.minecraft.world.entity.EquipmentSlot HEAD -> f
            net.minecraft.world.entity.EquipmentSlot$Type type -> g
            */
            for (net.minecraft.world.entity.EnumItemSlot rawSlot : net.minecraft.world.entity.EnumItemSlot.values()) {
                EquipmentSlot slot = getEquipmentSlot(rawSlot);
                var map = item.a(rawSlot);
                /*
                 * net.minecraft.world.entity.ai.attributes.Attributes -> bhg:
                net.minecraft.world.entity.ai.attributes.Attribute MAX_HEALTH -> a
                net.minecraft.world.entity.ai.attributes.Attribute FOLLOW_RANGE -> b
                net.minecraft.world.entity.ai.attributes.Attribute KNOCKBACK_RESISTANCE -> c
                net.minecraft.world.entity.ai.attributes.Attribute MOVEMENT_SPEED -> d
                net.minecraft.world.entity.ai.attributes.Attribute FLYING_SPEED -> e
                net.minecraft.world.entity.ai.attributes.Attribute ATTACK_DAMAGE -> f
                net.minecraft.world.entity.ai.attributes.Attribute ATTACK_KNOCKBACK -> g
                net.minecraft.world.entity.ai.attributes.Attribute ATTACK_SPEED -> h
                net.minecraft.world.entity.ai.attributes.Attribute ARMOR -> i
                net.minecraft.world.entity.ai.attributes.Attribute ARMOR_TOUGHNESS -> j
                net.minecraft.world.entity.ai.attributes.Attribute LUCK -> k
                net.minecraft.world.entity.ai.attributes.Attribute SPAWN_REINFORCEMENTS_CHANCE -> l
                net.minecraft.world.entity.ai.attributes.Attribute JUMP_STRENGTH -> m
                 */
                map.asMap().forEach((attribute, attributeModifiers) -> {
                    Attribute bukkitAttribute = getAttribute(attribute);
                    attributeModifiers.forEach((attributeModifier) -> {
                        AttributeModifier bukkitAttributeModifier = new AttributeModifier(attributeModifier.a(),
                                attributeModifier.b(), attributeModifier.d(),
                                getAttributeModifierOperation(attributeModifier.c()), slot);
                        result.put(bukkitAttribute, bukkitAttributeModifier);
                    });
                });
            }
            return result;
        }
        return null;
    }

    public static Attribute getAttribute(AttributeBase attribute) {
        /*
         * net.minecraft.world.entity.ai.attributes.Attributes -> bhg:
         net.minecraft.world.entity.ai.attributes.Attribute MAX_HEALTH -> a
         net.minecraft.world.entity.ai.attributes.Attribute FOLLOW_RANGE -> b
         net.minecraft.world.entity.ai.attributes.Attribute KNOCKBACK_RESISTANCE -> c
         net.minecraft.world.entity.ai.attributes.Attribute MOVEMENT_SPEED -> d
         net.minecraft.world.entity.ai.attributes.Attribute FLYING_SPEED -> e
         net.minecraft.world.entity.ai.attributes.Attribute ATTACK_DAMAGE -> f
         net.minecraft.world.entity.ai.attributes.Attribute ATTACK_KNOCKBACK -> g
         net.minecraft.world.entity.ai.attributes.Attribute ATTACK_SPEED -> h
         net.minecraft.world.entity.ai.attributes.Attribute ARMOR -> i
         net.minecraft.world.entity.ai.attributes.Attribute ARMOR_TOUGHNESS -> j
         net.minecraft.world.entity.ai.attributes.Attribute LUCK -> k
         net.minecraft.world.entity.ai.attributes.Attribute SPAWN_REINFORCEMENTS_CHANCE -> l
         net.minecraft.world.entity.ai.attributes.Attribute JUMP_STRENGTH -> m
         */
        if (attribute.equals(GenericAttributes.a)) {
            return Attribute.GENERIC_MAX_HEALTH;
        } else if (attribute.equals(GenericAttributes.b)) {
            return Attribute.GENERIC_FOLLOW_RANGE;
        } else if (attribute.equals(GenericAttributes.c)) {
            return Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        } else if (attribute.equals(GenericAttributes.d)) {
            return Attribute.GENERIC_MOVEMENT_SPEED;
        } else if (attribute.equals(GenericAttributes.e)) {
            return Attribute.GENERIC_FLYING_SPEED;
        } else if (attribute.equals(GenericAttributes.f)) {
            return Attribute.GENERIC_ATTACK_DAMAGE;
        } else if (attribute.equals(GenericAttributes.g)) {
            return Attribute.GENERIC_ATTACK_KNOCKBACK;
        } else if (attribute.equals(GenericAttributes.h)) {
            return Attribute.GENERIC_ATTACK_SPEED;
        } else if (attribute.equals(GenericAttributes.i)) {
            return Attribute.GENERIC_ARMOR;
        } else if (attribute.equals(GenericAttributes.j)) {
            return Attribute.GENERIC_ARMOR_TOUGHNESS;
        } else if (attribute.equals(GenericAttributes.k)) {
            return Attribute.GENERIC_LUCK;
        } else if (attribute.equals(GenericAttributes.l)) {
            return Attribute.ZOMBIE_SPAWN_REINFORCEMENTS;
        } else if (attribute.equals(GenericAttributes.m)) {
            return Attribute.HORSE_JUMP_STRENGTH;
        }
        return CraftAttributeMap.fromMinecraft(attribute.c());
    }

    public static org.bukkit.attribute.AttributeModifier.Operation getAttributeModifierOperation(Operation operation) {
        switch (operation) {
            case a:
                return org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER;
            case b:
                return org.bukkit.attribute.AttributeModifier.Operation.ADD_SCALAR;
            case c:
                return org.bukkit.attribute.AttributeModifier.Operation.MULTIPLY_SCALAR_1;
        }
        return null;
    }

    public static EquipmentSlot getEquipmentSlot(net.minecraft.world.entity.EnumItemSlot slot) {
        /*
        * net.minecraft.world.entity.EquipmentSlot -> bfo:
        net.minecraft.world.entity.EquipmentSlot MAINHAND -> a
        net.minecraft.world.entity.EquipmentSlot OFFHAND -> b
        net.minecraft.world.entity.EquipmentSlot FEET -> c
        net.minecraft.world.entity.EquipmentSlot LEGS -> d
        net.minecraft.world.entity.EquipmentSlot CHEST -> e
        net.minecraft.world.entity.EquipmentSlot HEAD -> f
        net.minecraft.world.entity.EquipmentSlot$Type type -> g
        */
        switch (slot) {
            case a:
                return EquipmentSlot.HAND;
            case b:
                return EquipmentSlot.OFF_HAND;
            case c:
                return EquipmentSlot.FEET;
            case d:
                return EquipmentSlot.LEGS;
            case e:
                return EquipmentSlot.CHEST;
            case f:
                return EquipmentSlot.HEAD;
        }
        return null;
    }
}
