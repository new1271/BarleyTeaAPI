package org.ricetea.barleyteaapi.internal.listener;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityMove;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMove;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityMove;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityMove;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

public final class EntityMoveListener implements Listener {
    private static final Lazy<EntityMoveListener> inst = Lazy.create(EntityMoveListener::new);

    private EntityMoveListener() {
    }

    @Nonnull
    public static EntityMoveListener getInstance() {
        return inst.get();
    }

    @EventHandler
    public void listenEntityMove(EntityMoveEvent event) {
        if (event == null)
            return;
        if (!EntityFeatureLinker.doFeatureCancellable(event.getEntity(), event, FeatureEntityMove.class,
                FeatureEntityMove::handleEntityMove, DataEntityMove::new))
            event.setCancelled(true);
        if (!(event.getEntity() instanceof Player) || ItemRegister.getInstance().hasAnyRegisteredNeedMovingFeature())
            return;
        if (!ItemFeatureLinker.forEachEquipmentCancellable(event.getEntity(), event, FeatureItemHoldEntityMove.class,
                FeatureItemHoldEntityMove::handleItemHoldEntityEntityMove, DataItemHoldEntityMove::new))
            event.setCancelled(true);
    }
}
