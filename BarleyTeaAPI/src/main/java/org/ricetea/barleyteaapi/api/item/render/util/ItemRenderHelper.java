package org.ricetea.barleyteaapi.api.item.render.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.item.render.ItemRenderer;
import org.ricetea.barleyteaapi.api.item.render.ItemRendererRegister;
import org.ricetea.barleyteaapi.internal.item.renderer.UnregisteredItemRendererImpl;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ItemRenderHelper {

    private static final @Nonnull NamespacedKey lastRenderingKey = NamespacedKeyUtil.BarleyTeaAPI("last_renderer");

    @Nullable
    public static ItemRenderer getLastRenderer(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            return getLastRenderer(meta);
        }
        return null;
    }

    @Nullable
    public static ItemRenderer getLastRenderer(@Nonnull ItemMeta meta) {
        ItemRendererRegister register = ItemRendererRegister.getInstance();
        NamespacedKey key = ObjectUtil.safeMap(
                meta.getPersistentDataContainer().get(lastRenderingKey, PersistentDataType.STRING),
                NamespacedKey::fromString);
        if (key == null)
            return null;
        ItemRenderer renderer = register.lookup(key);
        return renderer == null ? new UnregisteredItemRendererImpl(key) : renderer;
    }

    public static void setLastRenderer(@Nonnull ItemStack itemStack, @Nonnull ItemRenderer renderer) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            setLastRenderer(meta, renderer);
            itemStack.setItemMeta(meta);
        }
    }

    public static void setLastRenderer(@Nonnull ItemMeta meta, @Nonnull ItemRenderer renderer) {
        meta.getPersistentDataContainer().set(lastRenderingKey, PersistentDataType.STRING,
                renderer.getKey().toString());
    }

}
