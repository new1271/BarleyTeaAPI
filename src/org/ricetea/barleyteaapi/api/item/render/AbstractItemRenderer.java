package org.ricetea.barleyteaapi.api.item.render;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.ObjectUtil;

import net.kyori.adventure.text.Component;

public abstract class AbstractItemRenderer implements Keyed {

    private static final @Nonnull NamespacedKey lastRenderingKey = NamespacedKeyUtil.BarleyTeaAPI("last_renderer");
    private static @Nullable AbstractItemRenderer _inst;
    private final NamespacedKey key;

    @Nonnull
    public static AbstractItemRenderer getDefault() {
        AbstractItemRenderer inst = _inst;
        if (inst == null) {
            _inst = inst = DefaultItemRenderer.getInstance();
        }
        return inst;
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
                return ItemRendererRegister.getInstance()
                        .lookup(Objects.requireNonNull(NamespacedKey.fromString(resultString)));
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

    protected abstract void render(@Nonnull ItemStack itemStack);

    protected abstract void beforeFirstRender(@Nonnull ItemStack itemStack);

    protected abstract @Nullable List<Component> getItemLore(@Nonnull ItemMeta itemMeta);

    protected abstract void setItemLore(@Nonnull ItemMeta itemMeta, @Nullable List<? extends Component> lore);

    protected abstract void addItemFlags(@Nonnull ItemMeta itemMeta, @Nonnull ItemFlag... flags);

    protected abstract void addItemFlags(@Nonnull ItemMeta itemMeta, @Nonnull Set<ItemFlag> flags);

    protected abstract void removeItemFlags(@Nonnull ItemMeta itemMeta, @Nonnull ItemFlag... flags);

    protected abstract void removeItemFlags(@Nonnull ItemMeta itemMeta, @Nonnull Set<ItemFlag> flags);

    protected abstract boolean hasItemFlag(@Nonnull ItemMeta itemMeta, @Nonnull ItemFlag flag);

    protected abstract @Nullable Set<ItemFlag> getItemFlags(@Nonnull ItemMeta itemMeta);

    public static void renderItem(@Nullable ItemStack itemStack) {
        if (itemStack != null) {
            if (BaseItem.isBarleyTeaItem(itemStack)) {
                AbstractItemRenderer renderer = getLastRenderer(itemStack);
                if (renderer == null) {
                    renderer = getDefault();
                    renderer.beforeFirstRender(itemStack);
                }
                renderer.render(itemStack);
            }
        }
    }

    public static @Nullable List<Component> getItemLore(@Nullable ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (BaseItem.isBarleyTeaItem(itemStack)) {
                    AbstractItemRenderer renderer = getLastRenderer(itemStack);
                    if (renderer == null) {
                        renderer = getDefault();
                    }
                    return renderer.getItemLore(itemMeta);
                }
                return itemMeta.lore();
            }
        }
        return null;
    }

    public static void setItemLore(@Nullable ItemStack itemStack, @Nullable List<? extends Component> lore) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (BaseItem.isBarleyTeaItem(itemStack)) {
                    AbstractItemRenderer renderer = getLastRenderer(itemStack);
                    if (renderer == null) {
                        renderer = getDefault();
                    }
                    renderer.setItemLore(itemMeta, lore);
                    itemStack.setItemMeta(itemMeta);
                    renderer.render(itemStack);
                } else {
                    itemMeta.lore(lore);
                    itemStack.setItemMeta(itemMeta);
                }
            }
        }
    }

    public static void addItemFlags(@Nullable ItemStack itemStack, @Nullable ItemFlag... flags) {
        if (itemStack != null && flags != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (BaseItem.isBarleyTeaItem(itemStack)) {
                    AbstractItemRenderer renderer = getLastRenderer(itemStack);
                    if (renderer == null) {
                        renderer = getDefault();
                    }
                    renderer.addItemFlags(itemMeta, flags);
                    itemStack.setItemMeta(itemMeta);
                    renderer.render(itemStack);
                } else {
                    itemMeta.addItemFlags(flags);
                    itemStack.setItemMeta(itemMeta);
                }
            }
        }
    }

    public static void addItemFlags(@Nullable ItemStack itemStack, @Nullable Set<ItemFlag> flags) {
        if (itemStack != null && flags != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (BaseItem.isBarleyTeaItem(itemStack)) {
                    AbstractItemRenderer renderer = getLastRenderer(itemStack);
                    if (renderer == null) {
                        renderer = getDefault();
                    }
                    renderer.addItemFlags(itemMeta, flags);
                    itemStack.setItemMeta(itemMeta);
                    renderer.render(itemStack);
                } else {
                    itemMeta.addItemFlags(flags.toArray(ItemFlag[]::new));
                    itemStack.setItemMeta(itemMeta);
                }
            }
        }
    }

    public static void removeItemFlags(@Nullable ItemStack itemStack, @Nullable ItemFlag... flags) {
        if (itemStack != null && flags != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (BaseItem.isBarleyTeaItem(itemStack)) {
                    AbstractItemRenderer renderer = getLastRenderer(itemStack);
                    if (renderer == null) {
                        renderer = getDefault();
                    }
                    renderer.removeItemFlags(itemMeta, flags);
                    itemStack.setItemMeta(itemMeta);
                    renderer.render(itemStack);
                } else {
                    itemMeta.removeItemFlags(flags);
                    itemStack.setItemMeta(itemMeta);
                }
            }
        }
    }

    public static void removeItemFlags(@Nullable ItemStack itemStack, @Nullable Set<ItemFlag> flags) {
        if (itemStack != null && flags != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (BaseItem.isBarleyTeaItem(itemStack)) {
                    AbstractItemRenderer renderer = getLastRenderer(itemStack);
                    if (renderer == null) {
                        renderer = getDefault();
                    }
                    renderer.removeItemFlags(itemMeta, flags);
                    itemStack.setItemMeta(itemMeta);
                    renderer.render(itemStack);
                } else {
                    itemMeta.removeItemFlags(flags.toArray(ItemFlag[]::new));
                    itemStack.setItemMeta(itemMeta);
                }
            }
        }
    }

    public static boolean hasItemFlag(@Nullable ItemStack itemStack, @Nullable ItemFlag flag) {
        if (itemStack != null && flag != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (BaseItem.isBarleyTeaItem(itemStack)) {
                    AbstractItemRenderer renderer = getLastRenderer(itemStack);
                    if (renderer == null) {
                        renderer = getDefault();
                    }
                    return renderer.hasItemFlag(itemMeta, flag);
                } else {
                    return itemMeta.hasItemFlag(flag);
                }
            }
        }
        return false;
    }

    @SuppressWarnings("null")
    @Nonnull
    public static Set<ItemFlag> getItemFlags(@Nullable ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (BaseItem.isBarleyTeaItem(itemStack)) {
                    AbstractItemRenderer renderer = getLastRenderer(itemStack);
                    if (renderer == null) {
                        renderer = getDefault();
                    }
                    return ObjectUtil.letNonNull(renderer.getItemFlags(itemMeta), Collections::emptySet);
                } else {
                    return ObjectUtil.letNonNull(itemMeta.getItemFlags(), Collections::emptySet);
                }
            }
        }
        return Collections.emptySet();
    }
}
