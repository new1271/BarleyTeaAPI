package org.ricetea.barleyteaapi.internal.listener;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityMove;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMove;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityMove;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityMove;
import org.ricetea.barleyteaapi.internal.item.registration.ItemRegisterImpl;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.utils.Constants;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class EntityMoveListener implements Listener {
    private static final Lazy<EntityMoveListener> inst = Lazy.create(EntityMoveListener::new);

    private EntityMoveListener() {
    }

    @Nonnull
    public static EntityMoveListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntityMove(EntityMoveEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (!EntityFeatureLinker.doFeatureCancellable(event.getEntity(), event, FeatureEntityMove.class,
                FeatureEntityMove::handleEntityMove, DataEntityMove::new))
            event.setCancelled(true);
        if (!(event.getEntity() instanceof Player))
            return;
        ItemRegisterImpl register = ItemRegisterImpl.getInstanceUnsafe();
        if (register == null || !register.hasAnyRegisteredNeedMovingFeature())
            return;
        if (!ItemFeatureLinker.forEachEquipmentCancellable(event.getEntity(), event,
                Constants.ALL_SLOTS, FeatureItemHoldEntityMove.class,
                FeatureItemHoldEntityMove::handleItemHoldEntityEntityMove, DataItemHoldEntityMove::new))
            event.setCancelled(true);
    }
}
