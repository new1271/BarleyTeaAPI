package org.ricetea.barleyteaapi.api.item.feature.data;

import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemInventoryFeatureData;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataItemEnchant extends BaseItemInventoryFeatureData<EnchantItemEvent> {

    @Nullable
    private Consumer<ItemStack> afterItemEnchant = null;

    public DataItemEnchant(@Nonnull EnchantItemEvent event) {
        super(event);
    }

    public @Nonnull Player getPlayer() {
        return ObjectUtil.throwWhenNull(event.getEnchanter());
    }

    public @Nonnull Block getBlock() {
        return ObjectUtil.throwWhenNull(event.getEnchantBlock());
    }

    public @Nonnull ItemStack getItemStack() {
        return ObjectUtil.throwWhenNull(event.getItem());
    }

    public int getExpLevelCost() {
        return event.getExpLevelCost();
    }

    public void setExpLevelCost(int level) {
        event.setExpLevelCost(level);
    }

    public @Nonnull Map<Enchantment, Integer> getEnchantsToAdd() {
        return ObjectUtil.throwWhenNull(event.getEnchantsToAdd());
    }

    public @Nonnull Enchantment getEnchantmentHint() {
        return ObjectUtil.throwWhenNull(event.getEnchantmentHint());
    }

    public int getLevelHint() {
        return event.getLevelHint();
    }

    public int whichButton() {
        return event.whichButton();
    }

    @Nullable
    public Consumer<ItemStack> getJobAfterItemEnchant() {
        return afterItemEnchant;
    }

    public void setJobAfterItemEnchant(@Nullable Consumer<ItemStack> jobAfterItemEnchant) {
        afterItemEnchant = jobAfterItemEnchant;
    }
}
