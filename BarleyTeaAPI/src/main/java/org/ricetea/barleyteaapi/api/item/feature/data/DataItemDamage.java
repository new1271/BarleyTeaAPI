package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BasePlayerFeatureData;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemDamage extends BasePlayerFeatureData<PlayerItemDamageEvent> {

    public DataItemDamage(@Nonnull PlayerItemDamageEvent event) {
        super(event);
    }

    public @Nonnull ItemStack getItemStack() {
        return Objects.requireNonNull(event.getItem());
    }

    public int getDamage() {
        return event.getDamage();
    }

    public void setDamage(int damage) {
        event.setDamage(damage);
    }

    public int getOriginalDamage() {
        return event.getOriginalDamage();
    }
}
