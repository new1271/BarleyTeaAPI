package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureSlimeSplit;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataSlimeSplit;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class SlimeSplitListener implements Listener {
    private static final Lazy<SlimeSplitListener> inst = Lazy.create(SlimeSplitListener::new);

    private SlimeSplitListener() {
    }

    @Nonnull
    public static SlimeSplitListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenSlimeSplit(SlimeSplitEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (!EntityFeatureLinker.doFeatureCancellable(event.getEntity(), event, FeatureSlimeSplit.class,
                FeatureSlimeSplit::handleSlimeSplit, DataSlimeSplit::new)) {
            event.setCancelled(true);
        }
    }
}
