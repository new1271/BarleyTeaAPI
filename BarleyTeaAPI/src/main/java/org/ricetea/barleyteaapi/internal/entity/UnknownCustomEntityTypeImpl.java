package org.ricetea.barleyteaapi.internal.entity;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.function.Consumer;
import java.util.function.Function;

@Immutable
@ApiStatus.Internal
public class UnknownCustomEntityTypeImpl implements CustomEntityType {

    @Nonnull
    private static final Lazy<UnknownCustomEntityTypeImpl> _inst = Lazy.createInThreadSafe(UnknownCustomEntityTypeImpl::new);

    private UnknownCustomEntityTypeImpl() {
    }

    @Nonnull
    public static UnknownCustomEntityTypeImpl getInstance() {
        return _inst.get();
    }

    @Nonnull
    @Override
    public EntityType asEntityType() {
        return EntityType.UNKNOWN;
    }

    @Nullable
    @Override
    public CustomEntity asCustomEntity() {
        return null;
    }

    @Nullable
    @Override
    public <T> T map(@Nonnull Function<EntityType, T> materialFunction, @Nonnull Function<CustomEntity, T> customBlockFunction) {
        return materialFunction.apply(EntityType.UNKNOWN);
    }

    @Override
    public void call(@Nonnull Consumer<EntityType> materialConsumer, @Nonnull Consumer<CustomEntity> customBlockConsumer) {
        materialConsumer.accept(EntityType.UNKNOWN);
    }
}
