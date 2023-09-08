package org.ricetea.barleyteaapi.internal.nms.helper;

import javax.annotation.Nullable;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_20_R1.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_20_R1.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.v1_20_R1.attribute.CraftAttributeMap;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.EquipmentSlot;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;

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
            @Nullable org.bukkit.Material material) {
        if (material != null) {
            // net.minecraft.world.item.Item getItem() -> d
            net.minecraft.world.item.Item item = CraftMagicNumbers.getItem(material);
            if (item != null) {
                Multimap<Attribute, AttributeModifier> result = LinkedHashMultimap.create();
                //com.google.common.collect.Multimap getDefaultAttributeModifiers(net.minecraft.world.entity.EquipmentSlot) -> a
                for (net.minecraft.world.entity.EnumItemSlot rawSlot : net.minecraft.world.entity.EnumItemSlot
                        .values()) {
                    EquipmentSlot slot = CraftEquipmentSlot.getSlot(rawSlot);
                    var map = item.a(rawSlot);
                    map.forEach((attribute, attributeModifier) -> {
                        MinecraftKey attributeKey = BuiltInRegistries.v.b(attribute);
                        if (attributeKey != null) {
                            Attribute bukkitAttribute = CraftAttributeMap.fromMinecraft(attributeKey.toString());
                            AttributeModifier bukkitAttributeModifier = CraftAttributeInstance
                                    .convert(attributeModifier, slot);
                            result.put(bukkitAttribute, bukkitAttributeModifier);
                        }
                    });
                }
                return result;
            }
        }
        return null;
    }
}
