package org.ricetea.barleyteaapi.api.item.template;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ricetea.barleyteaapi.api.helper.TranslationKeyHelper;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.CustomItemRarity;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;

import javax.annotation.Nonnull;

public class DefaultItem implements CustomItem {

    @Nonnull
    private final NamespacedKey key;

    @Nonnull
    private final String translationKey;

    @Nonnull
    private final Material originalType;

    @Nonnull
    private final CustomItemRarity rarity;

    private final boolean isTool;

    public DefaultItem(@Nonnull NamespacedKey key, @Nonnull Material originalType,
                       @Nonnull CustomItemRarity rarity) {
        this(key, originalType, rarity, ItemHelper.materialIsTool(originalType));
    }

    public DefaultItem(@Nonnull NamespacedKey key, @Nonnull Material originalType,
                       @Nonnull CustomItemRarity rarity, boolean isTool) {
        this.key = key;
        this.translationKey = TranslationKeyHelper.getTranslationKey("item", key);
        this.originalType = originalType;
        this.rarity = rarity;
        this.isTool = isTool;
    }

    @Nonnull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Nonnull
    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Nonnull
    @Override
    public Material getOriginalType() {
        return originalType;
    }

    @Nonnull
    @Override
    public CustomItemRarity getRarity() {
        return rarity;
    }

    @Override
    public boolean isTool() {
        return isTool;
    }

    @Override
    public boolean isRarityUpgraded(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && meta.hasEnchants();
    }
}
