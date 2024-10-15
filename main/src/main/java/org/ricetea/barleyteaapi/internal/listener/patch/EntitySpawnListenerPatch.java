package org.ricetea.barleyteaapi.internal.listener.patch;

import org.bukkit.event.entity.EntitySpawnEvent;

import javax.annotation.Nonnull;

public interface EntitySpawnListenerPatch {
    boolean listenEntitySpawnInOtherCase(@Nonnull EntitySpawnEvent event);
}
