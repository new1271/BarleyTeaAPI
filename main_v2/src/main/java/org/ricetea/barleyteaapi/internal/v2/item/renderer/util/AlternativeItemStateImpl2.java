package org.ricetea.barleyteaapi.internal.v2.item.renderer.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.ricetea.barleyteaapi.api.item.render.util.AlternativeItemState;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public final class AlternativeItemStateImpl2 implements AlternativeItemState {

    private static final @Nonnull NamespacedKey IsStoredKey = NamespacedKeyUtil.BarleyTeaAPI("is_stored");
    private static final @Nonnull NamespacedKey ItemNameKey = NamespacedKeyUtil.BarleyTeaAPI("item_name");
    private static final @Nonnull NamespacedKey ItemNameNewKey = NamespacedKeyUtil.BarleyTeaAPI("item_name_new");
    private static final @Nonnull NamespacedKey ItemLoreKey = NamespacedKeyUtil.BarleyTeaAPI("item_lore");

    @Nonnull
    @Override
    public ItemMeta store(@Nonnull ItemMeta meta) {
        ItemMeta result = storeLore(storeCustomName(storeItemName(meta)));
        result.getPersistentDataContainer().set(IsStoredKey, PersistentDataType.BOOLEAN, true);
        return result;
    }

    @Nonnull
    @Override
    public ItemMeta restore(@Nonnull ItemMeta meta) {
        ItemMeta result;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.getOrDefault(IsStoredKey, PersistentDataType.BOOLEAN, false)) {
            result = restoreLore(restoreCustomName(restoreItemName(meta)));
            container.remove(IsStoredKey);
        } else {
            result = meta;
        }
        return result;
    }

    private static @Nonnull ItemMeta storeCustomName(@Nonnull ItemMeta itemMeta) {
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        final Component displayName = itemMeta.displayName();
        if (displayName == null) {
            container.remove(ItemNameKey);
        } else {
            container.set(ItemNameKey, PersistentDataType.STRING,
                    JSONComponentSerializer.json().serialize(displayName));
        }
        return itemMeta;
    }

    private static @Nonnull ItemMeta storeItemName(@Nonnull ItemMeta itemMeta) {
        if (!itemMeta.hasItemName())
            return itemMeta;
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        final Component itemName = itemMeta.itemName();
        container.set(ItemNameNewKey, PersistentDataType.STRING,
                JSONComponentSerializer.json().serialize(itemName));
        return itemMeta;
    }

    @SuppressWarnings("unchecked")
    private static @Nonnull ItemMeta storeLore(@Nonnull ItemMeta itemMeta) {
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        final List<Component> lore = itemMeta.lore();
        if (lore == null) {
            container.remove(ItemLoreKey);
        } else {
            final JSONComponentSerializer serializer = JSONComponentSerializer.json();
            final JSONArray array = new JSONArray();
            lore.forEach(
                    component -> array.add(serializer.serialize(ObjectUtil.letNonNull(component, Component::empty))));
            container.set(ItemLoreKey, PersistentDataType.STRING, array.toString());
        }
        return itemMeta;
    }

    private static @Nonnull ItemMeta restoreCustomName(@Nonnull ItemMeta itemMeta) {
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        final String name = container.get(ItemNameKey, PersistentDataType.STRING);
        if (name == null)
            itemMeta.displayName(null);
        else {
            itemMeta.displayName(JSONComponentSerializer.json().deserialize(name));
            container.remove(ItemNameKey);
        }
        return itemMeta;
    }

    private static @Nonnull ItemMeta restoreItemName(@Nonnull ItemMeta itemMeta) {
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        final String name = container.get(ItemNameNewKey, PersistentDataType.STRING);
        itemMeta.itemName(name == null ? null : JSONComponentSerializer.json().deserialize(name));
        container.remove(ItemNameNewKey);
        return itemMeta;
    }

    private static @Nonnull ItemMeta restoreLore(@Nonnull ItemMeta itemMeta) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        String loreInJSON = container.get(ItemLoreKey, PersistentDataType.STRING);
        if (loreInJSON == null)
            itemMeta.lore(null);
        else {
            try {
                JSONArray array = (JSONArray) JSONValue.parse(loreInJSON);
                int length = array.size();
                Component[] components = new Component[length];
                JSONComponentSerializer serializer = JSONComponentSerializer.json();
                for (int i = 0; i < length; i++) {
                    try {
                        components[i] = serializer.deserialize(array.get(i).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                itemMeta.lore(Arrays.asList(components));
            } catch (Exception e) {
                e.printStackTrace();
            }
            container.remove(ItemLoreKey);
        }
        return itemMeta;
    }
}
