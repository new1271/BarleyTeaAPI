package org.ricetea.barleyteaapi.api.base.data;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class BasePlayerFeatureData<T extends PlayerEvent> extends BaseFeatureData<T> {

    public BasePlayerFeatureData(@Nonnull T event) {
        super(event);
    }

    public @Nonnull Player getPlayer() {
        return Objects.requireNonNull(event.getPlayer());
    }

}
