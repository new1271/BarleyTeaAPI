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
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
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
        Material material = Arrays.stream(Material.values())
                .parallel()
                .filter(val -> Objects.equals(item, val.getKey()))
                .findAny().orElse(Material.AIR);

        ItemStack result = new ItemStack(material, count);

        String nbt = ObjectUtil.safeMap(showItem.nbt(), BinaryTagHolder::string);
        if (nbt != null) {
            INBTItemHelper helper = Bukkit.getServicesManager().load(INBTItemHelper.class);
            if (helper != null) {
                helper.setNbt(result, nbt);
            }
        }
        return result;
    }

    @Nullable
    String getNMSEquipmentSlotName(@Nullable EquipmentSlot slot);

    default boolean isSuitableForPlayer(@Nonnull EquipmentSlot slot) {
        return isSuitableForEntityType(slot, EntityType.PLAYER);
    }

    default boolean isSuitableForEntityType(@Nonnull EquipmentSlot slot, @Nonnull EntityType entityType) {
        return true;
    }
}
