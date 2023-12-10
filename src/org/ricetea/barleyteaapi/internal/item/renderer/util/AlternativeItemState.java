package org.ricetea.barleyteaapi.internal.item.renderer.util;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.ObjectUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

public final class AlternativeItemState {

    private static final @Nonnull NamespacedKey IsStoredKey = NamespacedKeyUtil.BarleyTeaAPI("is_stored");
    private static final @Nonnull NamespacedKey ItemNameKey = NamespacedKeyUtil.BarleyTeaAPI("item_name");
    private static final @Nonnull NamespacedKey ItemLoreKey = NamespacedKeyUtil.BarleyTeaAPI("item_lore");
    private static final @Nonnull NamespacedKey ItemFlagsKey = NamespacedKeyUtil.BarleyTeaAPI("item_flags");

    @Nonnull
    public static ItemStack store(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta = store(meta);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    @Nonnull
    public static ItemMeta store(@Nonnull ItemMeta meta) {
        meta = storeFlags(storeLore(storeName(meta)));
        meta.getPersistentDataContainer().set(IsStoredKey, PersistentDataType.BOOLEAN, true);
        return meta;
    }

    @Nonnull
    public static ItemStack restore(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta = restore(meta);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    @Nonnull
    public static ItemMeta restore(@Nonnull ItemMeta meta) {
        if (meta.getPersistentDataContainer().getOrDefault(IsStoredKey, PersistentDataType.BOOLEAN, false)) {
            return restoreFlags(restoreLore(restoreName(meta)));
        }
        return meta;
    }

    private static @Nonnull ItemMeta storeName(@Nonnull ItemMeta itemMeta) {
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

    private static @Nonnull ItemMeta storeFlags(@Nonnull ItemMeta itemMeta) {
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        final Set<ItemFlag> flags = itemMeta.getItemFlags();
        int bitFlags = 0;
        for (ItemFlag flag : ItemFlag.values()) {
            if (flags.contains(flag)) {
                bitFlags |= (0b1 << flag.ordinal());
            }
        }
        container.set(ItemFlagsKey, PersistentDataType.INTEGER, bitFlags);
        return itemMeta;
    }

    private static @Nonnull ItemMeta restoreName(@Nonnull ItemMeta itemMeta) {
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        final String name = container.get(ItemNameKey, PersistentDataType.STRING);
        if (name != null) {
            if (name.equals(NamespacedKeyUtil.empty().toString())) {
                itemMeta.displayName(null);
            } else {
                itemMeta.displayName(JSONComponentSerializer.json().deserialize(name));
            }
            container.remove(ItemNameKey);
        }
        return itemMeta;
    }

    private static @Nonnull ItemMeta restoreLore(@Nonnull ItemMeta itemMeta) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        String loreInJSON = container.get(ItemLoreKey, PersistentDataType.STRING);
        if (loreInJSON != null) {
            if (loreInJSON.equals(NamespacedKeyUtil.empty().toString())) {
                itemMeta.lore(null);
            } else {
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
            }
            container.remove(ItemLoreKey);
        }
        return itemMeta;
    }

    private static @Nonnull ItemMeta restoreFlags(@Nonnull ItemMeta itemMeta) {
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        Integer flags = container.get(ItemFlagsKey, PersistentDataType.INTEGER);
        if (flags != null) {
            ItemFlag[] allFlags = ItemFlag.values();
            itemMeta.removeItemFlags(allFlags);
            int unboxedFlags = flags;
            for (ItemFlag flag : allFlags) {
                if (((unboxedFlags >>> flag.ordinal()) & 0b1) == 0b1) {
                    itemMeta.addItemFlags(flag);
                }
            }
            container.remove(ItemFlagsKey);
        }
        return itemMeta;
    }

}
