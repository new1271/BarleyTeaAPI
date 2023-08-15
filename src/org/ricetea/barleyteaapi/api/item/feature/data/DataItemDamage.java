package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BasePlayerFeatureData;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataItemDamage extends BasePlayerFeatureData<PlayerItemDamageEvent> {

    public DataItemDamage(@Nonnull PlayerItemDamageEvent event) {
        super(event);
    }

    public @Nonnull Player getPlayer() {
        return ObjectUtil.throwWhenNull(event.getPlayer());
    }

    public @Nonnull ItemStack getItemStack() {
        return ObjectUtil.throwWhenNull(event.getItem());
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
