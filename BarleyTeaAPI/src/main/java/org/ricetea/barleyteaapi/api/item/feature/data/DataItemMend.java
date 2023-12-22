package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseFeatureData;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemMend extends BaseFeatureData<PlayerItemMendEvent> {

    public DataItemMend(@Nonnull PlayerItemMendEvent event) {
        super(event);
    }

    public @Nonnull Player getPlayer() {
        return Objects.requireNonNull(event.getPlayer());
    }

    public @Nonnull ItemStack getItemStack() {
        return Objects.requireNonNull(event.getItem());
    }

    public @Nonnull EquipmentSlot getSlot() {
        return Objects.requireNonNull(event.getSlot());
    }

    public @Nonnull ExperienceOrb getExperienceOrb() {
        return Objects.requireNonNull(event.getExperienceOrb());
    }

    public int getRepairAmount() {
        return event.getRepairAmount();
    }

    public void setRepairAmount(int amount) {
        event.setRepairAmount(amount);
    }

}
