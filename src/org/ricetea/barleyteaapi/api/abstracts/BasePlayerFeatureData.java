package org.ricetea.barleyteaapi.api.abstracts;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public abstract class BasePlayerFeatureData<T extends PlayerEvent> extends BaseFeatureData<T> {

    public BasePlayerFeatureData(@Nonnull T event) {
        super(event);
    }

    public @Nonnull Player getPlayer() {
        return ObjectUtil.throwWhenNull(event.getPlayer());
    }

}
