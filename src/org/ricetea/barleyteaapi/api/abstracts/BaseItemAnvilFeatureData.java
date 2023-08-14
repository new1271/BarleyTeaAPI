package org.ricetea.barleyteaapi.api.abstracts;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public abstract class BaseItemAnvilFeatureData extends BaseFeatureData<PrepareAnvilEvent> {

    public BaseItemAnvilFeatureData(@Nonnull PrepareAnvilEvent event) {
        super(event);
    }

    @Nonnull
    public ItemStack getItemStack() {
        return ObjectUtil.throwWhenNull(event.getInventory().getFirstItem());
    }

    @Nonnull
    public String getRenameText() {
        return ObjectUtil.letNonNull(event.getInventory().getRenameText(), "");
    }

    @Nullable
    public ItemStack getResult() {
        return event.getResult();
    }

    public void setResult(@Nullable ItemStack itemStack) {
        event.setResult(itemStack);
    }
}
