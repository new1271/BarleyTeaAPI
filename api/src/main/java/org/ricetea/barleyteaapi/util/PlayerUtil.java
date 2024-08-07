package org.ricetea.barleyteaapi.util;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ricetea.utils.SoftCache;

import javax.annotation.Nonnull;
import java.util.Collection;

public class PlayerUtil {
    private static final SoftCache<Collection<? extends Player>> playerCache =
            SoftCache.createThreadSafe(() -> ImmutableList.copyOf(Bukkit.getOnlinePlayers()));

    @Nonnull
    public static Collection<? extends Player> getOnlinePlayerSnapshot() {
        return playerCache.get();
    }

    public static void updateOnlinePlayerSnapshot() {
        playerCache.reset();
    }

}
