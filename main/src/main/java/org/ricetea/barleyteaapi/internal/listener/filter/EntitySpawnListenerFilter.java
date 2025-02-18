package org.ricetea.barleyteaapi.internal.listener.filter;

import org.bukkit.event.entity.EntitySpawnEvent;

import javax.annotation.Nonnull;

public interface EntitySpawnListenerFilter {
    boolean listenEntitySpawnFirst(@Nonnull EntitySpawnEvent event);
}
