package org.ricetea.barleyteaapi.internal.nms.v1_20_R4.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.internal.nms.INMSItemHelper2;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;

public final class NMSItemHelper2Impl implements INMSItemHelper2 {
    private static final Lazy<NMSItemHelper2Impl> _inst = Lazy.create(NMSItemHelper2Impl::new);
    private static final NamespacedKey OriginalItemDamageKey = NamespacedKeyUtil.BarleyTeaAPI("original_item_damage");

    private NMSItemHelper2Impl() {
    }

    @Nonnull
    public static NMSItemHelper2Impl getInstance() {
        return _inst.get();
    }
    
    @Override
    public void applyCustomDurabilityBar(@Nonnull ItemMeta itemMeta, int damage, int maxDurability) {
        if (itemMeta instanceof Damageable damageable) {
            PersistentDataContainer container = damageable.getPersistentDataContainer();
            try {
                container.set(OriginalItemDamageKey, PersistentDataType.INTEGER, damage);
            } catch (Exception ignored) {
                container.remove(OriginalItemDamageKey);
                return;
            }
            damageable.setMaxDamage(maxDurability);
            damageable.setDamage(damage);
        }
    }

    @Override
    public void restoreCustomDurabilityBar(@Nonnull ItemMeta itemMeta, int maxDurability) {
        if (itemMeta instanceof Damageable damageable) {
            PersistentDataContainer container = damageable.getPersistentDataContainer();
            Integer damage;
            try {
                damage = container.get(OriginalItemDamageKey, PersistentDataType.INTEGER);
            } catch (Exception ignored) {
                return;
            }
            if (damage == null)
                return;
            damageable.setDamage(damage);
            damageable.setMaxDamage(maxDurability);
            ObjectUtil.tryCallSilently(OriginalItemDamageKey, container::remove);
        }
    }
}
