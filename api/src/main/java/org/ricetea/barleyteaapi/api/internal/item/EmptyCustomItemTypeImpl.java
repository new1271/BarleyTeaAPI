package org.ricetea.barleyteaapi.api.internal.item;

import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.CustomItemType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.inject.Singleton;
import java.util.function.Consumer;
import java.util.function.Function;

@Singleton
@Immutable
@ApiStatus.Internal
public class EmptyCustomItemTypeImpl implements CustomItemType {

    @Nonnull
    private static final EmptyCustomItemTypeImpl _inst = new EmptyCustomItemTypeImpl();

    private EmptyCustomItemTypeImpl() {
    }

    @Nonnull
    public static EmptyCustomItemTypeImpl getInstance() {
        return _inst;
    }

    @Nonnull
    @Override
    public Material asMaterial() {
        return Material.AIR;
    }

    @Nullable
    @Override
    public CustomItem asCustomItem() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Nullable
    @Override
    public <T> T map(@Nonnull Function<Material, T> materialFunction, @Nonnull Function<CustomItem, T> customBlockFunction) {
        return materialFunction.apply(Material.AIR);
    }

    @Override
    public void call(@Nonnull Consumer<Material> materialConsumer, @Nonnull Consumer<CustomItem> customBlockConsumer) {
        materialConsumer.accept(Material.AIR);
    }

    @Override
    public boolean equals(Object another) {
        if (another instanceof CustomItemType itemType)
            return itemType.isEmpty();
        return super.equals(another);
    }

    @Nonnull
    @Override
    public String toString() {
        return "CustomItemType{" + getKey() + "}";
    }
}
