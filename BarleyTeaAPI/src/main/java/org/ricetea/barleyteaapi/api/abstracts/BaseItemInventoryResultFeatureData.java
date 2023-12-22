package org.ricetea.barleyteaapi.api.abstracts;

import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public abstract class BaseItemInventoryResultFeatureData<T extends org.bukkit.event.inventory.PrepareInventoryResultEvent>
        extends BaseItemInventoryFeatureData<T> {

    @Nullable
    private DataItemType resultType = null;

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
    public DataItemType getResultType() {
        DataItemType resultType = this.resultType;
        if (resultType == null)
            this.resultType = resultType = ObjectUtil
                    .letNonNull(ObjectUtil.safeMap(getResult(), BaseItem::getItemType), DataItemType.empty());
        return resultType;
    }
}
