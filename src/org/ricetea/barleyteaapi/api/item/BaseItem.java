package org.ricetea.barleyteaapi.api.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.item.data.DataItemRarity;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCustomDurability;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;
import org.ricetea.barleyteaapi.util.ObjectUtil;

import net.kyori.adventure.text.Component;

public abstract class BaseItem implements Keyed {
    @Nonnull
    private static final NamespacedKey ItemTagNamespacedKey = NamespacedKeyUtils.BarleyTeaAPI("item_id");
    @Nonnull
    private static final NamespacedKey ItemAlternateDamageNamespacedKey = NamespacedKeyUtils
            .BarleyTeaAPI("item_damage");
    @Nonnull
    private final NamespacedKey key;
    @Nonnull
    private final Material materialBasedOn;
    @Nonnull
    private final DataItemRarity rarity;

    public BaseItem(@Nonnull NamespacedKey key, @Nonnull Material materialBasedOn, @Nonnull DataItemRarity rarity) {
        this.key = key;
        this.materialBasedOn = materialBasedOn;
        this.rarity = rarity;
    }

    @Nonnull
    public final NamespacedKey getKey() {
        return key;
    }

    @Nonnull
    public final String getNameInTranslateKey() {
        return "item." + key.getNamespace() + "." + key.getKey() + ".name";
    }

    @Nonnull
    public String getDefaultName() {
        return getNameInTranslateKey();
    }

    @Nonnull
    public final Material getMaterialBasedOn() {
        return materialBasedOn;
    }

    public final void register(@Nullable ItemStack itemStack) {
        if (itemStack != null && itemStack.hasItemMeta())
            itemStack.getItemMeta().getPersistentDataContainer().set(ItemTagNamespacedKey, PersistentDataType.STRING,
                    key.toString());
    }

    public final boolean isCertainItem(@Nullable ItemStack itemStack) {
        return itemStack != null && itemStack.hasItemMeta()
                && key.toString()
                        .equals(itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(
                                ItemTagNamespacedKey, PersistentDataType.STRING, null));
    }

    public int getDurabilityDamage(@Nullable ItemStack itemStack) {
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

    public void setDurabilityDamage(@Nullable ItemStack itemStack, int damage) {
        if (itemStack == null || !itemStack.hasItemMeta())
            return;
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        FeatureCustomDurability customDurabilityFeature = ObjectUtil.tryCast(this, FeatureCustomDurability.class);
        if (customDurabilityFeature != null || container.has(ItemAlternateDamageNamespacedKey)) {
            container.set(ItemAlternateDamageNamespacedKey, PersistentDataType.INTEGER, damage);
            if (customDurabilityFeature != null && meta instanceof Damageable damageable) {
                if (damage == 0) {
                    damageable.setDamage(0);
                } else {
                    int maxDura = customDurabilityFeature.getMaxDurability(itemStack);
                    int maxDuraVisual = itemStack.getType().getMaxDurability();
                    if (damage == maxDura) {
                        damageable.setDamage(maxDuraVisual);
                    } else {
                        int visualDamage = (int) Math
                                .floor(damage * 1.0 / maxDura * maxDuraVisual);
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
    }

    public static int getDurabilityDamage(@Nullable ItemStack itemStack, @Nullable BaseItem itemType) {
        if (itemType == null) {
            if (itemStack == null || !itemStack.hasItemMeta())
                return 0;
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;
                return damageable.getDamage();
            }
            return 0;
        } else {
            return itemType.getDurabilityDamage(itemStack);
        }
    }

    public static void setDurabilityDamage(@Nullable ItemStack itemStack, @Nullable BaseItem itemType, int damage) {
        if (itemType == null) {
            if (itemStack == null || !itemStack.hasItemMeta())
                return;
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;
                damageable.setDamage(damage);
                itemStack.setItemMeta(damageable);
            }
        } else {
            itemType.setDurabilityDamage(itemStack, damage);
        }
    }

    public static void registerItem(@Nullable ItemStack itemStack, @Nonnull BaseItem itemType) {
        itemType.register(itemStack);
    }

    public static boolean isBarleyTeaItem(@Nullable ItemStack itemStack) {
        return itemStack != null && itemStack.hasItemMeta()
                && itemStack.getItemMeta().getPersistentDataContainer().has(ItemTagNamespacedKey);
    }

    @Nullable
    public static NamespacedKey getItemID(@Nullable ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta())
            return null;
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        String namespacedKeyString = container.getOrDefault(ItemTagNamespacedKey, PersistentDataType.STRING, null);
        return namespacedKeyString == null ? null
                : namespacedKeyString.contains(":") ? NamespacedKey.fromString(namespacedKeyString) : null;
    }

    public static boolean isCertainItem(@Nullable ItemStack itemStack, @Nonnull BaseItem itemType) {
        return itemType.isCertainItem(itemStack);
    }

    @Nonnull
    public static DataItemType getItemType(@Nonnull ItemStack itemStack) {
        NamespacedKey itemTypeID = BaseItem.getItemID(itemStack);
        if (itemTypeID == null) {
            return DataItemType.create(itemStack.getType());
        } else {
            BaseItem baseItem = ItemRegister.getInstance().lookupItemType(itemTypeID);
            if (baseItem == null)
                return DataItemType.create(itemStack.getType());
            else
                return DataItemType.create(baseItem);
        }
    }

    @SuppressWarnings("null")
    @Deprecated
    protected final void setItemName(@Nonnull ItemStack itemStack) {
        setItemName(itemStack, rarity.apply(Component.translatable(getNameInTranslateKey(), getDefaultName())));
    }

    @Deprecated
    protected final void setItemName(@Nonnull ItemStack itemStack, @Nonnull String name) {
        setItemName(itemStack, name, true);
    }

    @SuppressWarnings("null")
    @Deprecated
    protected final void setItemName(@Nonnull ItemStack itemStack, @Nonnull String name,
            boolean isApplyRenamingItalic) {
        setItemName(itemStack, rarity.apply(Component.text(name), isApplyRenamingItalic));
    }

    protected final void setItemName(@Nonnull ItemStack itemStack, @Nonnull Component component) {
        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            meta.displayName(component);
            itemStack.setItemMeta(meta);
        }
    }
}
