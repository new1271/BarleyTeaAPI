package org.ricetea.barleyteaapi.api.item.template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemRarity;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCommandGive;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.barleyteaapi.api.item.feature.data.DataCommandGive;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;

public abstract class RegularItem extends BaseItem
        implements FeatureCommandGive, FeatureItemGive {
    @Nonnull
    private static final NamespacedKey ItemAlternateDamageNamespacedKey = NamespacedKeyUtil
            .BarleyTeaAPI("item_damage");

    public RegularItem(@Nonnull NamespacedKey key, @Nonnull Material materialBasedOn, @Nonnull DataItemRarity rarity) {
        super(key, materialBasedOn, rarity);
    }

    public int getDurabilityDamage(@Nonnull ItemStack itemStack) {
        return getDurabilityDamage0(itemStack);
    }

    public void setDurabilityDamage(@Nonnull ItemStack itemStack, int damage) {
        setDurabilityDamage0(itemStack, damage);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return;
        if (this instanceof FeatureItemCustomDurability customDurabilityFeature) {
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
                itemStack.setItemMeta(damageable);
            }
        }
    }

    protected int getDurabilityDamage0(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return 0;
        if (this instanceof FeatureItemCustomDurability)
            return meta.getPersistentDataContainer().getOrDefault(ItemAlternateDamageNamespacedKey,
                    PersistentDataType.INTEGER,
                    0);
        else if (meta instanceof Damageable damageable)
            return damageable.getDamage();
        return 0;
    }

    protected void setDurabilityDamage0(@Nonnull ItemStack itemStack, int damage) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return;
        if (this instanceof FeatureItemCustomDurability)
            meta.getPersistentDataContainer().set(ItemAlternateDamageNamespacedKey, PersistentDataType.INTEGER, damage);
        else if (meta instanceof Damageable damageable)
            damageable.setDamage(damage);
        itemStack.setItemMeta(meta);
    }

    @Nullable
    public ItemStack handleItemGive(int count) {
        if (count > 0) {
            ItemStack itemStack = new ItemStack(getMaterialBasedOn(), count);
            if (tryRegister(itemStack, this::handleItemGive)) {
                return itemStack;
            }
        }
        return null;
    }

    protected abstract boolean handleItemGive(@Nonnull ItemStack itemStack);

    public boolean handleCommandGive(@Nonnull DataCommandGive data) {
        ItemStack itemStack = data.getItemStack();
        if (handleItemGive(itemStack)) {
            return true;
        }
        return false;
    }
}
