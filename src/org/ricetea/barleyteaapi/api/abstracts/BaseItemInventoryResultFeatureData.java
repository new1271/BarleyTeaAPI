package org.ricetea.barleyteaapi.api.abstracts;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.utils.ObjectUtil;

@SuppressWarnings("deprecation")
public abstract class BaseItemInventoryResultFeatureData<T extends org.bukkit.event.inventory.PrepareInventoryResultEvent>
        extends BaseItemInventoryFeatureData<T> {

    @Nullable
    private DataItemType resultType = null;

    public BaseItemInventoryResultFeatureData(@Nonnull T event) {
        super(event);
    }

    @Nonnull
    public Inventory getInventory() {
        return Objects.requireNonNull(event.getInventory());
    }

    @Nullable
    public ItemStack getResult() {
        return event.getResult();
    }

    @Nonnull
    public DataItemType getResultType() {
        DataItemType resultType = this.resultType;
        if (resultType == null)
            this.resultType = resultType = ObjectUtil
                    .letNonNull(ObjectUtil.mapWhenNonnull(getResult(), BaseItem::getItemType), DataItemType.empty());
        return resultType;
    }

    public void setResult(@Nullable ItemStack itemStack) {
        event.setResult(itemStack);
        resultType = null;
    }
}
