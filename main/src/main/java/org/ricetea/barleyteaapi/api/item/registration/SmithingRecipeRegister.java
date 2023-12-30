package org.ricetea.barleyteaapi.api.item.registration;

import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.item.recipe.BaseSmithingRecipe;
import org.ricetea.barleyteaapi.internal.item.registration.SmithingRecipeRegisterImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public interface SmithingRecipeRegister extends IRegister<BaseSmithingRecipe> {

    @Nonnull
    static SmithingRecipeRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return SmithingRecipeRegisterImpl.getInstance();
    }

    @Nullable
    static SmithingRecipeRegister getInstanceUnsafe() {
        BarleyTeaAPI.checkPluginUsable();
        return SmithingRecipeRegisterImpl.getInstanceUnsafe();
    }

    static boolean hasRegistered() {
        return ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), SmithingRecipeRegister::hasAnyRegistered), false);
    }
}
