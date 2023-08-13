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
import org.ricetea.barleyteaapi.api.item.render.AbstractItemRenderer;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

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
        if (itemStack != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.getPersistentDataContainer().set(ItemTagNamespacedKey, PersistentDataType.STRING,
                        key.toString());
                itemStack.setItemMeta(meta);
            }
        }
    }

    public final boolean isCertainItem(@Nullable ItemStack itemStack) {
        return itemStack != null && itemStack.hasItemMeta()
                && key.toString()
                        .equals(itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(
                                ItemTagNamespacedKey, PersistentDataType.STRING, null));
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
        if (this instanceof FeatureCustomDurability customDurabilityFeature) {
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

    public static int getDurabilityDamage(@Nullable ItemStack itemStack, @Nullable BaseItem itemType) {
        if (itemStack == null)
            return 0;
        if (itemType == null) {
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
        if (itemStack == null)
            return;
        if (itemType == null) {
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
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.displayName(component);
            itemStack.setItemMeta(meta);
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof BaseItem baseItem) {
            return key.equals(baseItem.getKey());
        }
        return super.equals(obj);
    }

    public static void setAsDefaultName(@Nonnull ItemStack itemStack) {
        getItemType(itemStack).processLeftOrRight(m -> {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.displayName(null);
                itemStack.setItemMeta(meta);
            }
        }, baseItem -> {
            baseItem.setItemName(itemStack);
        });
    }

    public static void setName(@Nonnull ItemStack itemStack, @Nullable String name) {
        if (name == null || name.isBlank())
            setAsDefaultName(itemStack);
        else {
            getItemType(itemStack).processLeftOrRight(m -> {
                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null) {
                    meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
                    itemStack.setItemMeta(meta);
                }
            }, baseItem -> {
                baseItem.setItemName(itemStack, name);
            });
        }
    }
}
