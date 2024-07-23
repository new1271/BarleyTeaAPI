package org.ricetea.barleyteaapi.api.internal.nms;

import com.google.common.collect.Multimap;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@ApiStatus.Internal
public interface INMSItemHelper extends IHelper {

    @Nonnull
    static INMSItemHelper getInstance() {
        return Objects.requireNonNull(Bukkit.getServicesManager().load(INMSItemHelper.class));
    }

    @Nullable
    static INMSItemHelper getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(INMSItemHelper.class);
    }

    @Nullable
    Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@Nullable Material material);

    @Nullable
    default ItemStack createItemStackFromShowItem(@Nonnull HoverEvent.ShowItem showItem) {
        Key item = showItem.item();
        int count = showItem.count();
        BinaryTagHolder tagHolder = showItem.nbt();
        String nbt;
        if (tagHolder == null) {
            nbt = "{\"id\":\"" + item + "\", \"count\":\"" + count + "\"}";
        } else {
            nbt = "{\"id\":\"" + item + "\", \"count\":\"" + count + "\", \"nbt\":" + tagHolder.string() + "}";
        }
        return createItemStackFromNbtString(nbt);
    }

    @Nullable
    ItemStack createItemStackFromNbtString(@Nonnull String nbt);

    @Nullable
    String getNMSEquipmentSlotName(@Nullable EquipmentSlot slot);

    default boolean isSuitableForPlayer(@Nonnull EquipmentSlot slot) {
        return isSuitableForEntityType(slot, EntityType.PLAYER);
    }

    default boolean isSuitableForEntityType(@Nonnull EquipmentSlot slot, @Nonnull EntityType entityType) {
        return true;
    }
}
