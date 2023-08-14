package org.ricetea.barleyteaapi.api.item.template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemRarity;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCommandGive;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.barleyteaapi.api.item.render.AbstractItemRenderer;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

public abstract class RegularItem extends BaseItem
        implements FeatureCommandGive, FeatureItemGive {
    @Nonnull
    private static final NamespacedKey ItemAlternateDamageNamespacedKey = NamespacedKeyUtils
            .BarleyTeaAPI("item_damage");

    public RegularItem(@Nonnull NamespacedKey key, @Nonnull Material materialBasedOn, @Nonnull DataItemRarity rarity) {
        super(key, materialBasedOn, rarity);
    }

    public int getDurabilityDamage(@Nonnull ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta())
            return 0;
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(ItemAlternateDamageNamespacedKey)) {
            Integer damage = container.get(ItemAlternateDamageNamespacedKey, PersistentDataType.INTEGER);
            if (damage == null) {
                container.set(ItemAlternateDamageNamespacedKey, PersistentDataType.INTEGER, 0);
                damage = 0;
            }
            return damage;
        } else if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            return damageable.getDamage();
        }
        return 0;
    }

    public void setDurabilityDamage(@Nonnull ItemStack itemStack, int damage) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (this instanceof FeatureItemCustomDurability customDurabilityFeature) {
            container.set(ItemAlternateDamageNamespacedKey, PersistentDataType.INTEGER, damage);
            if (meta instanceof Damageable damageable) {
                if (damage == 0) {
                    damageable.setDamage(0);
                } else {
                    int maxDura = customDurabilityFeature.getMaxDurability(itemStack);
                    int maxDuraVisual = itemStack.getType().getMaxDurability();
                    if (damage == maxDura) {
                        damageable.setDamage(maxDuraVisual);
                    } else {
                        int visualDamage = (int) Math
                                .round(damage * 1.0 / maxDura * maxDuraVisual);
                        if (visualDamage <= 0 && damage > 0) {
                            visualDamage = 1;
                        } else if (visualDamage >= maxDuraVisual && damage < maxDura) {
                            visualDamage = maxDuraVisual - 1;
                        }
                        damageable.setDamage(visualDamage);
                    }
                }
            }
        } else if (meta instanceof Damageable damageable) {
            damageable.setDamage(damage);
        } else {
            return;
        }
        itemStack.setItemMeta(meta);
        AbstractItemRenderer.renderItem(itemStack);
    }

    @Nonnull
    public ItemStack handleItemGive(int count) {
        if (count > 0) {
            ItemStack itemStack = new ItemStack(getMaterialBasedOn(), count);
            BaseItem.registerItem(itemStack, this);
            if (handleItemGive(itemStack)) {
                AbstractItemRenderer.renderItem(itemStack);
                ItemMeta meta = itemStack.getItemMeta();
                if (meta == null || !meta.hasDisplayName())
                    setItemName(itemStack);
                return itemStack;
            }
        }
        return new ItemStack(Material.AIR);
    }

    protected abstract boolean handleItemGive(ItemStack itemStack);

    public boolean handleCommandGive(@Nonnull ItemStack itemStackGived, @Nullable String nbt) {
        if (handleItemGive(itemStackGived)) {
            ItemMeta meta = itemStackGived.getItemMeta();
            if (meta == null || !meta.hasDisplayName())
                setItemName(itemStackGived);
            return true;
        }
        return false;
    }
}
