package org.ricetea.barleyteaapi.api.item;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.item.data.DataItemRarity;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.Translatable;

public abstract class BaseItem implements Keyed, Translatable {
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
        this.isTool = ItemHelper.materialIsTool(materialBasedOn);
        this.lazyType = Lazy.create(() -> DataItemType.create(this));
    }

    @Override
    @Nonnull
    public final NamespacedKey getKey() {
        return key;
    }

    @Override
    @Nonnull
    public final String translationKey(){
        return "item." + key.getNamespace() + "." + key.getKey();
    }

    @Nonnull
    public final String getNameInTranslateKey() {
        return translationKey();
    }

    @Nonnull
    public String getDefaultName() {
        return translationKey();
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

    @Nonnull
    public Component getDefaultNameComponent(){
        return Component.translatable(getNameInTranslateKey(), getDefaultName());
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

    public final void register(@Nullable ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.getPersistentDataContainer().set(DefaultNamespacedKey, PersistentDataType.STRING,
                        key.toString());
                itemStack.setItemMeta(meta);
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
        return ObjectUtil.letNonNull(ObjectUtil.safeMap(itemStack.getItemMeta(), ItemMeta::hasEnchants), false);
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

    public boolean equals(Object obj) {
        if (obj instanceof BaseItem baseItem) {
            return key.equals(baseItem.getKey());
        }
        return super.equals(obj);
    }
}
