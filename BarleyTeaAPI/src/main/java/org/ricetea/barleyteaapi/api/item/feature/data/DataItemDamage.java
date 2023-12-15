package org.ricetea.barleyteaapi.api.item.feature.data;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BasePlayerFeatureData;

public final class DataItemDamage extends BasePlayerFeatureData<PlayerItemDamageEvent> {

    public DataItemDamage(@Nonnull PlayerItemDamageEvent event) {
        super(event);
    }

    public @Nonnull Player getPlayer() {
        return Objects.requireNonNull(event.getPlayer());
    }

    public @Nonnull ItemStack getItemStack() {
        return Objects.requireNonNull(event.getItem());
    }

    public int getDamage() {
        return event.getDamage();
    }

    public int getOriginalDamage() {
        return event.getOriginalDamage();
    }

    public void setDamage(int damage) {
        event.setDamage(damage);
    }
}
