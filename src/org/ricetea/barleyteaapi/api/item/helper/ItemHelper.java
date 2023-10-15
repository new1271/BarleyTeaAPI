package org.ricetea.barleyteaapi.api.item.helper;

import java.util.HashMap;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
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
}
