package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureSlimeSplit;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataSlimeSplit;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.Lazy;

public final class SlimeSplitListener implements Listener {
    private static final Lazy<SlimeSplitListener> inst = new Lazy<>(SlimeSplitListener::new);

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
        NamespacedKey id = BaseEntity.getEntityID(event.getEntity());
        if (id != null) {
            BaseEntity entity = EntityRegister.getInstance().lookupEntityType(id);
            if (entity instanceof FeatureSlimeSplit slimeSplitEntity) {
                boolean cancelled = !slimeSplitEntity.handleSlimeSplit(new DataSlimeSplit(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

    }
}
