package org.ricetea.barleyteaapi.internal.block;

import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.block.CustomBlockType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.function.Consumer;
import java.util.function.Function;

@Immutable
@ApiStatus.Internal
public class EmptyCustomBlockTypeImpl implements CustomBlockType {

    @Nonnull
    private static final Lazy<EmptyCustomBlockTypeImpl> _inst = Lazy.createInThreadSafe(EmptyCustomBlockTypeImpl::new);

    private EmptyCustomBlockTypeImpl() {
    }

    @Nonnull
    public static EmptyCustomBlockTypeImpl getInstance() {
        return _inst.get();
    }

    @Nonnull
    @Override
    public Material asMaterial() {
        return Material.AIR;
    }

    @Nullable
    @Override
    public CustomBlock asCustomBlock() {
        return null;
    }

    @Nullable
    @Override
    public <T> T map(@Nonnull Function<Material, T> materialFunction, @Nonnull Function<CustomBlock, T> customBlockFunction) {
        return materialFunction.apply(Material.AIR);
    }

    @Override
    public void call(@Nonnull Consumer<Material> materialConsumer, @Nonnull Consumer<CustomBlock> customBlockConsumer) {
        materialConsumer.accept(Material.AIR);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
