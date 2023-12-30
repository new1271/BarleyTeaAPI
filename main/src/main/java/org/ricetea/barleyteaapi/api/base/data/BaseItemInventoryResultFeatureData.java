package org.ricetea.barleyteaapi.api.base.data;

import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.CustomItemType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public abstract class BaseItemInventoryResultFeatureData<T extends org.bukkit.event.inventory.PrepareInventoryResultEvent>
        extends BaseItemInventoryFeatureData<T> {

    @Nullable
    private CustomItemType resultType = null;

    public BaseItemInventoryResultFeatureData(@Nonnull T event) {
        super(event);
    }

    @Nullable
    public ItemStack getResult() {
        return event.getResult();
    }

    public void setResult(@Nullable ItemStack itemStack) {
        event.setResult(itemStack);
        resultType = null;
    }

    @Nonnull
    public CustomItemType getResultType() {
        CustomItemType resultType = this.resultType;
        if (resultType == null)
            this.resultType = resultType = CustomItemType.get(getResult());
        return resultType;
    }
}
