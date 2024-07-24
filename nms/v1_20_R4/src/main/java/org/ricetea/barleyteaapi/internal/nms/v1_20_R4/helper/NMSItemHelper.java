package org.ricetea.barleyteaapi.internal.nms.v1_20_R4.helper;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

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
            net.minecraft.world.item.Item item = CraftMagicNumbers.getItem(material);
            if (item != null) {
                var map = item.components().get(DataComponents.ATTRIBUTE_MODIFIERS);
                if (map == null)
                    return null;
                Multimap<Attribute, AttributeModifier> result = LinkedHashMultimap.create();
                for (net.minecraft.world.entity.EquipmentSlot rawSlot : net.minecraft.world.entity.EquipmentSlot.values()) {
                    map.forEach(rawSlot, (attribute, attributeModifier) -> {
                        Attribute bukkitAttribute = CraftAttribute.minecraftToBukkit(attribute.value());
                        EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(rawSlot);
                        AttributeModifier bukkitAttributeModifier = CraftAttributeInstance
                                .convert(attributeModifier, group);
                        result.put(bukkitAttribute, bukkitAttributeModifier);
                    });
                }
                return result;
            }
        }
        return null;
    }

    @Nullable
    public static org.bukkit.inventory.ItemStack createItemStackFromNbtString(@Nonnull String nbt) {
        CompoundTag parsedTag;
        try {
            parsedTag = TagParser.parseTag(nbt);
        } catch (CommandSyntaxException e) {
            return null;
        }
        if (!(parsedTag.get("id") instanceof StringTag idTag))
            return null;
        Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(idTag.getAsString()));
        int count;
        if (parsedTag.get("id") instanceof IntTag countTag) {
            count = Math.max(countTag.getAsInt(), 1);
        } else {
            count = 1;
        }
        ItemStack result = new ItemStack(item, count);
        if (parsedTag.get("components") instanceof CompoundTag componentsTag){
            DataComponentMap map = NBTItemHelper.toComponentMap(componentsTag);
            if (map != null) {
                result.applyComponents(map);
            }
        }
        return result.asBukkitMirror();
    }
}
