package org.ricetea.barleyteaapi.api.item.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

public abstract class AbstractItemRenderer implements Keyed {

    private static @Nullable AbstractItemRenderer _inst;
    private static final @Nonnull Lazy<AbstractItemRenderer> _defaultInst = new Lazy<>(DefaultItemRenderer::new);
    private static final @Nonnull NamespacedKey lastRenderingKey = NamespacedKeyUtils.BarleyTeaAPI("last_renderer");
    private static final @Nonnull NamespacedKey ItemLoreKey = NamespacedKeyUtils.BarleyTeaAPI("item_lore");
    private static final @Nonnull String AlternateItemFlagStoreKeyHeader = "item_flag_";
    private final NamespacedKey key;

    @Nonnull
    public static AbstractItemRenderer getDefault() {
        AbstractItemRenderer inst = _inst;
        return inst == null ? _defaultInst.get() : inst;
    }

    public static void setDefault(@Nullable AbstractItemRenderer renderer) {
        _inst = renderer;
    }

    @Nullable
    public static AbstractItemRenderer getLastRenderer(@Nullable ItemStack itemStack) {
        if (itemStack != null && itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            String resultString = meta.getPersistentDataContainer().getOrDefault(lastRenderingKey,
                    PersistentDataType.STRING,
                    null);
            if (resultString != null && resultString.contains(":")) {
                return ItemRendererRegister.getInstance().getRenderer(NamespacedKey.fromString(resultString));
            }
        }
        return null;
    }

    public static void setLastRenderer(@Nullable ItemStack itemStack, @Nullable AbstractItemRenderer renderer) {
        if (itemStack != null && itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            if (renderer == null) {
                meta.getPersistentDataContainer().remove(lastRenderingKey);
            } else {
                meta.getPersistentDataContainer().set(lastRenderingKey, PersistentDataType.STRING,
                        renderer.getKey().toString());
            }
            itemStack.setItemMeta(meta);
        }
    }

    public AbstractItemRenderer(@Nonnull NamespacedKey key) {
        this.key = key;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    public abstract void renderItem(@Nonnull ItemStack itemStack);

    public void convertBackToNormalItem(@Nonnull ItemStack itemStack) {
        convertBackToNormalItem(itemStack, getLastRenderer(itemStack));
    }

    public void convertBackToNormalItem(@Nonnull ItemStack itemStack, @Nullable AbstractItemRenderer lastRenderer) {
        if (lastRenderer == this) {
            if (itemStack.hasItemMeta()) {
                ItemMeta meta = itemStack.getItemMeta();
                List<Component> customLores = getItemLore(itemStack);
                ItemFlag[] flags = getItemFlags(itemStack);
                if (customLores != null)
                    meta.lore(customLores);
                meta.removeItemFlags(ItemFlag.values());
                if (flags != null && flags.length > 0)
                    meta.addItemFlags(flags);
                itemStack.setItemMeta(meta);
            }
        } else if (lastRenderer != null) {
            lastRenderer.convertBackToNormalItem(itemStack, lastRenderer);
        }
    }

    public @Nullable List<Component> getItemLore(@Nonnull ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.getOrDefault(lastRenderingKey, PersistentDataType.STRING, null) != null) {
                String loreInJSON = container.getOrDefault(ItemLoreKey, PersistentDataType.STRING, null);
                if (loreInJSON == null)
                    return null;
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
                        return Arrays.asList(components);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return itemStack.lore();
    }

    @SuppressWarnings("unchecked")
    public void setItemLore(@Nonnull ItemStack itemStack, @Nullable List<Component> lores) {
        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.getOrDefault(lastRenderingKey, PersistentDataType.STRING, null) != null) {
                if (lores == null) {
                    container.remove(ItemLoreKey);
                } else {
                    JSONComponentSerializer serializer = JSONComponentSerializer.json();
                    int length = lores.size();
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < length; i++) {
                        Component lore = lores.get(i);
                        if (lore == null)
                            lore = Component.empty();
                        array.add(serializer.serialize(lore));
                    }
                    container.set(ItemLoreKey, PersistentDataType.STRING, array.toString());
                }
                itemStack.setItemMeta(meta);
                return;
            }
        }
        itemStack.lore(lores);
    }

    @Nullable
    public ItemFlag[] getItemFlags(@Nonnull ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.getOrDefault(lastRenderingKey, PersistentDataType.STRING, null) != null) {
                ItemFlag[] values = ItemFlag.values();
                int length = values.length;
                ArrayList<ItemFlag> flagList = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    ItemFlag flag = values[i];
                    if (container.getOrDefault(
                            NamespacedKeyUtils
                                    .BarleyTeaAPI(AlternateItemFlagStoreKeyHeader + flag.toString().toLowerCase()),
                            PersistentDataType.BOOLEAN, false) == true) {
                        flagList.add(flag);
                    }
                }
                return flagList.toArray(ItemFlag[]::new);
            }
        }
        return itemStack.getItemFlags().toArray(ItemFlag[]::new);
    }

    public boolean hasItemFlag(@Nonnull ItemStack itemStack, @Nullable ItemFlag flag) {
        if (flag == null)
            return false;
        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.getOrDefault(lastRenderingKey, PersistentDataType.STRING, null) != null) {
                return container.getOrDefault(
                        NamespacedKeyUtils
                                .BarleyTeaAPI(AlternateItemFlagStoreKeyHeader + flag.toString().toLowerCase()),
                        PersistentDataType.BOOLEAN, false) == true;
            }
        }
        return itemStack.hasItemFlag(flag);
    }

    public void addItemFlags(@Nonnull ItemStack itemStack, @Nullable ItemFlag... flags) {
        if (flags == null)
            return;
        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.getOrDefault(lastRenderingKey, PersistentDataType.STRING, null) != null) {
                for (ItemFlag flag : flags) {
                    container.set(
                            NamespacedKeyUtils
                                    .BarleyTeaAPI(AlternateItemFlagStoreKeyHeader + flag.toString().toLowerCase()),
                            PersistentDataType.BOOLEAN, true);
                }
                itemStack.setItemMeta(meta);
                return;
            }
        }
        itemStack.addItemFlags(flags);
    }

    public void removeItemFlags(@Nonnull ItemStack itemStack, @Nullable ItemFlag... flags) {
        if (flags == null)
            return;
        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.getOrDefault(lastRenderingKey, PersistentDataType.STRING, null) != null) {
                for (ItemFlag flag : flags) {
                    container.remove(
                            NamespacedKeyUtils
                                    .BarleyTeaAPI(AlternateItemFlagStoreKeyHeader + flag.toString().toLowerCase()));
                }
                itemStack.setItemMeta(meta);
                return;
            }
        }
        itemStack.removeItemFlags(flags);
    }
}
