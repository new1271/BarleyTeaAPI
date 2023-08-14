package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseFeatureData;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataItemMend extends BaseFeatureData<PlayerItemMendEvent> {

    public DataItemMend(@Nonnull PlayerItemMendEvent event) {
        super(event);
    }

    public @Nonnull Player getPlayer() {
        return ObjectUtil.throwWhenNull(event.getPlayer());
    }

    public @Nonnull ItemStack getItemStack() {
        return ObjectUtil.throwWhenNull(event.getItem());
    }

    public @Nonnull EquipmentSlot getSlot() {
        return ObjectUtil.throwWhenNull(event.getSlot());
    }

    public @Nonnull ExperienceOrb getExperienceOrb() {
        return ObjectUtil.throwWhenNull(event.getExperienceOrb());
    }

    public int getRepairAmount() {
        return event.getRepairAmount();
    }

    public void setRepairAmount(int amount) {
        event.setRepairAmount(amount);
    }

}
