package org.ricetea.barleyteaapi.api.item;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
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
import org.ricetea.barleyteaapi.api.item.render.AbstractItemRenderer;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public abstract class BaseItem implements Keyed {
    @Nonnull
    private static final NamespacedKey DefaultNamespacedKey = NamespacedKeyUtil.BarleyTeaAPI("item_id");
    @Nonnull
    private static final HashMap<NamespacedKey, Function<String, NamespacedKey>> FallbackNamespacedKeys = new HashMap<>();
    @Nonnull
    private final NamespacedKey key;
    @Nonnull
    private final Material materialBasedOn;
    @Nonnull
    private final DataItemRarity rarity;
    @Nonnull
    private final Lazy<DataItemType> lazyType;
    private final boolean isTool;

    @SuppressWarnings("deprecation")
    public BaseItem(@Nonnull NamespacedKey key, @Nonnull Material materialBasedOn, @Nonnull DataItemRarity rarity) {
        this.key = key;
        this.materialBasedOn = materialBasedOn;
        this.rarity = rarity;
        this.isTool = materialIsTool(materialBasedOn);
        this.lazyType = Lazy.create(() -> DataItemType.create(this));
    }

    @Nonnull
    public final NamespacedKey getKey() {
        return key;
    }

    @Nonnull
    public final String getNameInTranslateKey() {
        return "item." + key.getNamespace() + "." + key.getKey();
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

    @Nonnull
    public final DataItemRarity getRarity() {
        return rarity;
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

    protected final void setToolAttackDamage(@Nullable ItemMeta itemMeta, double attackDamage) {
        setDefaultAttribute(itemMeta, Attribute.GENERIC_ATTACK_DAMAGE, attackDamage - 1.0, Operation.ADD_NUMBER,
                EquipmentSlot.HAND);
    }

    protected final void setToolAttackSpeed(@Nullable ItemStack itemStack, double attackSpeed) {
        setDefaultAttribute(itemStack, Attribute.GENERIC_ATTACK_SPEED, attackSpeed - 4.0, Operation.ADD_NUMBER,
                EquipmentSlot.HAND);
    }

    protected final void setToolAttackSpeed(@Nullable ItemMeta itemMeta, double attackSpeed) {
        setDefaultAttribute(itemMeta, Attribute.GENERIC_ATTACK_SPEED, attackSpeed - 4.0, Operation.ADD_NUMBER,
                EquipmentSlot.HAND);
    }

    protected final void setDefaultAttribute(@Nullable ItemStack itemStack, @Nullable Attribute attribute,
            double amount, @Nullable Operation operation, @Nullable EquipmentSlot equipmentSlot) {
        if (itemStack != null && attribute != null && operation != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                setDefaultAttribute(meta, attribute, amount, operation, equipmentSlot);
                itemStack.setItemMeta(meta);
            }
        }
    }

    protected final void setDefaultAttribute(@Nullable ItemMeta itemMeta, @Nullable Attribute attribute,
            double amount, @Nullable Operation operation, @Nullable EquipmentSlot equipmentSlot) {
        if (itemMeta == null || attribute == null || operation == null)
            return;
        if (getItemID(itemMeta) != null) {
            itemMeta.removeAttributeModifier(attribute);
            itemMeta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(),
                    "default modifiers", amount, operation, equipmentSlot));
        }
    }

    public static void addFallbackNamespacedKey(@Nullable NamespacedKey key) {
        addFallbackNamespacedKey(key, null);
    }

    public static void addFallbackNamespacedKey(@Nullable NamespacedKey key,
            @Nullable Function<String, NamespacedKey> converter) {
        if (key != null && !FallbackNamespacedKeys.containsKey(key)) {
            FallbackNamespacedKeys.put(key, converter == null ? NamespacedKey::fromString : converter);
        }
    }

    public static void removeFallbackNamespacedKey(@Nullable NamespacedKey key) {
        FallbackNamespacedKeys.remove(key);
    }

    @Nonnull
    public final Component getDefaultNameComponent() {
        return getDefaultNameComponent(true);
    }

    @Nonnull
    public final Component getDefaultNameComponent(boolean isRarityStyled) {
        Component component = Objects.requireNonNull(Component.translatable(getNameInTranslateKey(), getDefaultName()));
        if (isRarityStyled)
            component = rarity.apply(component);
        return component;
    }

    public final boolean isDefaultNameComponent(@Nullable ItemStack itemStack) {
        if (itemStack == null)
            return false;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            return isDefaultNameComponent(meta.displayName());
        }
        return false;
    }

    public final boolean isDefaultNameComponent(@Nullable Component component) {
        if (component instanceof TranslatableComponent translatableComponent)
            return translatableComponent.key().equals(getNameInTranslateKey());
        return false;
    }

    @Nullable
    public static Component getDisplayName(@Nonnull ItemStack itemStack) {
        return getItemType(itemStack).mapLeftOrRight(
                left -> ObjectUtil.mapWhenNonnull(itemStack.getItemMeta(), ItemMeta::displayName),
                right -> {
                    Component displayName = ObjectUtil.mapWhenNonnull(itemStack.getItemMeta(), ItemMeta::displayName);
                    if (displayName != null) {
                        if (right.isDefaultNameComponent(itemStack)) {
                            return null;
                        } else {
                            DataItemRarity originalRarity = right.getRarity();
                            DataItemRarity rarity = right.isRarityUpgraded(itemStack) ? originalRarity.upgrade()
                                    : originalRarity;
                            if (!rarity.isSimilar(displayName.style()) &&
                                    (originalRarity == rarity || !originalRarity.isSimilar(displayName.style()))) {
                                displayName = displayName.style(Style.empty());
                            }
                        }
                    }
                    return displayName;
                });
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
                && key.toString().equals(
                        itemStack.getItemMeta().getPersistentDataContainer()
                                .get(DefaultNamespacedKey, PersistentDataType.STRING));
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
        NamespacedKey result;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        String namespacedKeyString = container.get(DefaultNamespacedKey, PersistentDataType.STRING);
        if (namespacedKeyString == null) {
            result = null;
            if (!FallbackNamespacedKeys.isEmpty()) {
                for (var iterator = FallbackNamespacedKeys.entrySet().iterator(); iterator.hasNext();) {
                    var entry = iterator.next();
                    NamespacedKey key = entry.getKey();
                    if (key != null) {
                        namespacedKeyString = container.get(key, PersistentDataType.STRING);
                        if (namespacedKeyString != null) {
                            Function<String, NamespacedKey> function = entry.getValue();
                            result = function == null ? NamespacedKey.fromString(namespacedKeyString)
                                    : function.apply(namespacedKeyString);
                            break;
                        }
                    }
                }
            }
        } else {
            result = NamespacedKey.fromString(namespacedKeyString);
        }
        return result;
    }

    public static boolean isCertainItem(@Nullable ItemStack itemStack, @Nonnull BaseItem itemType) {
        return itemType.isCertainItem(itemStack);
    }

    @Nonnull
    public static DataItemType getItemType(@Nonnull ItemStack itemStack) {
        return DataItemType.get(itemStack);
    }

    protected final void setItemName(@Nonnull ItemStack itemStack) {
        setItemName(itemStack, isRarityUpgraded(itemStack));
    }

    protected final void setItemName(@Nonnull ItemStack itemStack, boolean isUpgraded) {
        setItemName(itemStack, getDefaultNameComponent(), isUpgraded, false);
    }

    @Deprecated
    protected final void setItemName(@Nonnull ItemStack itemStack, @Nonnull String displayName) {
        setItemName(itemStack, displayName, isRarityUpgraded(itemStack), true);
    }

    @Deprecated
    protected final void setItemName(@Nonnull ItemStack itemStack, @Nonnull String displayName,
            boolean isUpgraded, boolean needApplyRenamingItalic) {
        setItemName(itemStack, Objects.requireNonNull(Component.text(displayName)), isUpgraded,
                needApplyRenamingItalic);
    }

    protected final void setItemName(@Nonnull ItemStack itemStack, @Nonnull Component displayName) {
        setItemName(itemStack, displayName, isRarityUpgraded(itemStack), true);
    }

    protected final void setItemName(@Nonnull ItemStack itemStack, @Nonnull Component displayName,
            boolean isUpgraded, boolean needApplyRenamingItalic) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (displayName.color() == null) {
                DataItemRarity rarity = this.rarity;
                if (isUpgraded)
                    rarity = rarity.upgrade();
                displayName = rarity.apply(displayName, needApplyRenamingItalic);
            }
            meta.displayName(displayName);
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

    public static void setDisplayName(@Nonnull ItemStack itemStack, @Nullable String displayName) {
        if (displayName == null || displayName.isBlank())
            setAsDefaultName(itemStack);
        else {
            getItemType(itemStack).processLeftOrRight(m -> {
                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null) {
                    meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(displayName));
                    itemStack.setItemMeta(meta);
                }
            }, baseItem -> {
                baseItem.setItemName(itemStack, displayName);
            });
        }
    }

    public static void setDisplayName(@Nonnull ItemStack itemStack, @Nullable Component displayName) {
        if (displayName == null)
            setAsDefaultName(itemStack);
        else {
            getItemType(itemStack).processLeftOrRight(m -> {
                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null) {
                    meta.displayName(displayName);
                    itemStack.setItemMeta(meta);
                }
            }, baseItem -> {
                baseItem.setItemName(itemStack, displayName);
            });
        }
    }
}
