package org.ricetea.barleyteaapi.api.item;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.item.data.DataItemRarity;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.api.item.render.AbstractItemRenderer;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;
import org.ricetea.barleyteaapi.util.ObjectUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public abstract class BaseItem implements Keyed {
    @Nonnull
    private static final NamespacedKey DefaultNamespacedKey = NamespacedKeyUtils.BarleyTeaAPI("item_id");
    @Nonnull
    private static final Set<NamespacedKey> FallbackNamespacedKeys = new HashSet<>();
    @Nonnull
    private final NamespacedKey key;
    @Nonnull
    private final Material materialBasedOn;
    @Nonnull
    private final DataItemRarity rarity;
    @Nonnull
    private final Lazy<DataItemType> lazyType;
    private final boolean isTool;

    public BaseItem(@Nonnull NamespacedKey key, @Nonnull Material materialBasedOn, @Nonnull DataItemRarity rarity) {
        this.key = key;
        this.materialBasedOn = materialBasedOn;
        this.rarity = rarity;
        this.isTool = materialIsTool(materialBasedOn);
        this.lazyType = new Lazy<>(() -> DataItemType.create(this));
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

    @Nonnull
    public final DataItemType getType() {
        return lazyType.get();
    }

    public boolean isTool() {
        return isTool;
    }

    protected static boolean materialIsTool(Material material) {
        Material type = material;
        switch (type) {
            case WOODEN_SWORD:
            case STONE_SWORD:
            case GOLDEN_SWORD:
            case IRON_SWORD:
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
            case TRIDENT:
            case WOODEN_AXE:
            case STONE_AXE:
            case GOLDEN_AXE:
            case IRON_AXE:
            case DIAMOND_AXE:
            case NETHERITE_AXE:
            case WOODEN_PICKAXE:
            case STONE_PICKAXE:
            case GOLDEN_PICKAXE:
            case IRON_PICKAXE:
            case DIAMOND_PICKAXE:
            case NETHERITE_PICKAXE:
            case WOODEN_SHOVEL:
            case STONE_SHOVEL:
            case GOLDEN_SHOVEL:
            case IRON_SHOVEL:
            case DIAMOND_SHOVEL:
            case NETHERITE_SHOVEL:
            case WOODEN_HOE:
            case STONE_HOE:
            case GOLDEN_HOE:
            case IRON_HOE:
            case DIAMOND_HOE:
            case NETHERITE_HOE:
                return true;
            default:
                return false;
        }
    }

    protected final void setToolAttackDamage(@Nullable ItemStack itemStack, double attackDamage) {
        setDefaultAttribute(itemStack, Attribute.GENERIC_ATTACK_DAMAGE, attackDamage - 1.0, Operation.ADD_NUMBER,
                EquipmentSlot.HAND);
    }

    protected final void setToolAttackSpeed(@Nullable ItemStack itemStack, double attackSpeed) {
        setDefaultAttribute(itemStack, Attribute.GENERIC_ATTACK_SPEED, attackSpeed - 4.0, Operation.ADD_NUMBER,
                EquipmentSlot.HAND);
    }

    protected final void setDefaultAttribute(@Nullable ItemStack itemStack, @Nullable Attribute attribute,
            double amount, @Nullable Operation operation, @Nullable EquipmentSlot equipmentSlot) {
        if (itemStack != null && attribute != null && operation != null && isBarleyTeaItem(itemStack)) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.removeAttributeModifier(attribute);
                meta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(),
                        "default modifiers", amount, operation, equipmentSlot));
                itemStack.setItemMeta(meta);
            }
        }
    }

    public static void addFallbackNamespacedKey(@Nullable NamespacedKey key) {
        if (key != null && !FallbackNamespacedKeys.contains(key)) {
            FallbackNamespacedKeys.add(key);
        }
    }

    public static void removeFallbackNamespacedKey(@Nullable NamespacedKey key) {
        FallbackNamespacedKeys.remove(key);
    }

    @Nonnull
    public final TranslatableComponent getDefaultNameComponent(){
        return Objects.requireNonNull(Component.translatable(getNameInTranslateKey(), getDefaultName()));
    }

    public final void register(@Nullable ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.getPersistentDataContainer().set(DefaultNamespacedKey, PersistentDataType.STRING,
                        key.toString());
                itemStack.setItemMeta(meta);
                AbstractItemRenderer.renderItem(itemStack);
            }
        }
    }

    public final void register(@Nullable ItemStack itemStack,
            @Nullable Consumer<ItemStack> afterItemStackRegistered) {
        if (itemStack != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.getPersistentDataContainer().set(DefaultNamespacedKey, PersistentDataType.STRING,
                        key.toString());
                itemStack.setItemMeta(meta);
                if (afterItemStackRegistered != null) {
                    afterItemStackRegistered.accept(itemStack);
                }
                AbstractItemRenderer.renderItem(itemStack);
            }
        }
    }

    public final boolean tryRegister(@Nullable ItemStack itemStack,
            @Nullable Predicate<ItemStack> afterItemStackRegistered) {
        if (itemStack != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.getPersistentDataContainer().set(DefaultNamespacedKey, PersistentDataType.STRING,
                        key.toString());
                itemStack.setItemMeta(meta);
                if (afterItemStackRegistered != null && !afterItemStackRegistered.test(itemStack)) {
                    return false;
                }
                AbstractItemRenderer.renderItem(itemStack);
                return true;
            }
        }
        return false;
    }

    public final boolean isCertainItem(@Nullable ItemStack itemStack) {
        return itemStack != null && itemStack.hasItemMeta()
                && key.toString()
                        .equals(itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(
                                DefaultNamespacedKey, PersistentDataType.STRING, null));
    }

    public boolean isRarityUpgraded(@Nonnull ItemStack itemStack) {
        return ObjectUtil.letNonNull(ObjectUtil.mapWhenNonnull(itemStack.getItemMeta(), ItemMeta::hasEnchants), false);
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
        } else if (itemType instanceof FeatureItemCustomDurability customDurabilityFeature) {
            return customDurabilityFeature.getDurabilityDamage(itemStack);
        }
        return 0;
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
        } else if (itemType instanceof FeatureItemCustomDurability customDurabilityFeature) {
            customDurabilityFeature.setDurabilityDamage(itemStack, damage);
        }
    }

    public static void registerItem(@Nullable ItemStack itemStack, @Nonnull BaseItem itemType) {
        itemType.register(itemStack);
    }

    public static boolean isBarleyTeaItem(@Nullable ItemStack itemStack) {
        return itemStack != null && itemStack.hasItemMeta()
                && getItemID(itemStack.getItemMeta()) != null;
    }

    @Nullable
    public static NamespacedKey getItemID(@Nullable ItemStack itemStack) {
        if (itemStack == null)
            return null;
        return getItemID(itemStack.getItemMeta());
    }

    @Nullable
    public static NamespacedKey getItemID(@Nullable ItemMeta itemMeta) {
        if (itemMeta == null)
            return null;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        String namespacedKeyString = container.getOrDefault(DefaultNamespacedKey, PersistentDataType.STRING, null);
        if (namespacedKeyString == null && !FallbackNamespacedKeys.isEmpty()) {
            for (var iterator = FallbackNamespacedKeys.iterator(); iterator.hasNext() && namespacedKeyString == null;) {
                NamespacedKey key = iterator.next();
                if (key != null)
                    namespacedKeyString = container.getOrDefault(key, PersistentDataType.STRING, null);
            }
        }
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
            ItemRegister register = ItemRegister.getInstanceUnsafe();
            if (register == null) {
                return DataItemType.create(itemStack.getType());
            } else {
                BaseItem baseItem = ItemRegister.getInstance().lookup(itemTypeID);
                if (baseItem == null)
                    return DataItemType.create(itemStack.getType());
                else
                    return DataItemType.create(baseItem);
            }
        }
    }

    protected final void setItemName(@Nonnull ItemStack itemStack) {
        setItemName(itemStack, isRarityUpgraded(itemStack));
    }

    protected final void setItemName(@Nonnull ItemStack itemStack, boolean isUpgraded) {
        DataItemRarity rarity = this.rarity;
        if (isUpgraded)
            rarity = rarity.upgrade();
        setItemName(itemStack, rarity.apply(getDefaultNameComponent()));
    }

    @Deprecated
    protected final void setItemName(@Nonnull ItemStack itemStack, @Nonnull String name) {
        setItemName(itemStack, name, true, isRarityUpgraded(itemStack));
    }

    @SuppressWarnings("null")
    @Deprecated
    protected final void setItemName(@Nonnull ItemStack itemStack, @Nonnull String name,
            boolean isApplyRenamingItalic, boolean isUpgraded) {
        DataItemRarity rarity = this.rarity;
        if (isUpgraded)
            rarity = rarity.upgrade();
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
