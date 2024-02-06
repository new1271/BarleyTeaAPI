package org.ricetea.barleyteaapi.internal.item.registration;

import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.registration.ItemRendererRegister;
import org.ricetea.barleyteaapi.api.item.render.ItemRenderer;
import org.ricetea.barleyteaapi.internal.base.registration.NSKeyedRegisterBase;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.logging.Logger;

@Singleton
@ApiStatus.Internal
public final class ItemRendererRegisterImpl extends NSKeyedRegisterBase<ItemRenderer> implements ItemRendererRegister {

    @Override
    public void register(@Nullable ItemRenderer renderer) {
        if (renderer == null)
            return;
        super.register(renderer);
        BarleyTeaAPI apiInst = BarleyTeaAPI.getInstanceUnsafe();
        if (apiInst != null) {
            Logger logger = apiInst.getLogger();
            logger.info(LOGGING_REGISTERED_FORMAT.formatted(renderer.getKey(), "item renderer"));
        }
    }

    @Override
    public void unregister(@Nullable ItemRenderer renderer) {
        if (renderer == null)
            return;
        super.unregister(renderer);
        BarleyTeaAPI apiInst = BarleyTeaAPI.getInstanceUnsafe();
        if (apiInst != null) {
            Logger logger = apiInst.getLogger();
            logger.info(LOGGING_UNREGISTERED_FORMAT.formatted(renderer.getKey()));
        }
    }
}
