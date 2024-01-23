package org.ricetea.barleyteaapi.internal.nms.v1_20_R2.helper;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_20_R2.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_20_R2.attribute.CraftAttribute;
import org.bukkit.craftbukkit.v1_20_R2.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftMagicNumbers;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
                for (net.minecraft.world.entity.EquipmentSlot rawSlot : net.minecraft.world.entity.EquipmentSlot.values()) {
                    EquipmentSlot slot = CraftEquipmentSlot.getSlot(rawSlot);
                    var map = item.getDefaultAttributeModifiers(rawSlot);
                    map.forEach((attribute, attributeModifier) -> {
                        Attribute bukkitAttribute = CraftAttribute.minecraftToBukkit(attribute);
                        AttributeModifier bukkitAttributeModifier = CraftAttributeInstance
                                .convert(attributeModifier, slot);
                        result.put(bukkitAttribute, bukkitAttributeModifier);
                    });
                }
                return result;
            }
        }
        return null;
    }

    @Nullable
    public static ItemStack createItemStackFromNbtString(@Nonnull String nbt) {
        try {
            return net.minecraft.world.item.ItemStack.of(TagParser.parseTag(nbt)).asBukkitMirror();
        } catch (CommandSyntaxException e) {
            return null;
        }
    }
}
