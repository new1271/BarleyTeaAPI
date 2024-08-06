package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BaseItemInventoryResultFeatureData;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class DataItemGrindstone extends BaseItemInventoryResultFeatureData<PrepareGrindstoneEvent> {

    @Nonnull
    private final ItemStack original;
    @Nullable
    private final ItemStack addition;
    @Nonnull
    private final Lazy<CustomItemType> originalTypeLazy;
    @Nullable
    private final Lazy<CustomItemType> additionTypeLazy;

    public DataItemGrindstone(@Nonnull PrepareGrindstoneEvent event, @Nonnull ItemStack original, @Nullable ItemStack addition) {
        super(event);
        this.original = original;
        originalTypeLazy = Lazy.create(() -> CustomItemType.get(getOriginal()));
        if ((this.addition = addition) == null) {
            additionTypeLazy = null;
        } else {
            additionTypeLazy = Lazy.create(() -> CustomItemType.get(getAddition()));
        }
    }

    @Nonnull
    public GrindstoneInventory getInventory() {
        return Objects.requireNonNull(event.getInventory());
    }

    @Nonnull
    public ItemStack getOriginal() {
        return original;
    }

    @Nonnull
    public CustomItemType getOriginalType() {
        return originalTypeLazy.get();
    }

    @Nullable
    public ItemStack getAddition() {
        return addition;
    }

    @Nonnull
    public CustomItemType getAdditionType() {
        return ObjectUtil.letNonNull(ObjectUtil.safeMap(additionTypeLazy, Lazy::get), CustomItemType::empty);
    }

    public boolean hasAddition() {
        return addition != null;
    }
}
