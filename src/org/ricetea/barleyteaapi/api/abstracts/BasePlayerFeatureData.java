package org.ricetea.barleyteaapi.api.abstracts;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

public abstract class BasePlayerFeatureData<T extends PlayerEvent> extends BaseFeatureData<T> {

    public BasePlayerFeatureData(@Nonnull T event) {
        super(event);
    }

    public @Nonnull Player getPlayer() {
        return Objects.requireNonNull(event.getPlayer());
    }

}
