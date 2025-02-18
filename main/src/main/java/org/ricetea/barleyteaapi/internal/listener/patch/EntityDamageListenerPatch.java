package org.ricetea.barleyteaapi.internal.listener.patch;

import org.bukkit.event.Event;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.base.data.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker.ItemDataConstructorForEquipment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public interface EntityDamageListenerPatch {
    @Nullable
    <TEvent extends Event, TData extends BaseFeatureData<TEvent>> Function<TEvent, TData> getEntityEventDataFactoryOrNull(
            @Nonnull Class<TData> dataClazz);

    @Nullable
    <TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>>
    ItemDataConstructorForEquipment<TEvent, TData> getItemHoldEntityEventDataFactoryOrNull(
            @Nonnull Class<TData> dataClazz);
}
