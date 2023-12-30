package org.ricetea.barleyteaapi.api.item.registration;

import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCookingRecipe;
import org.ricetea.barleyteaapi.internal.item.registration.CookingRecipeRegisterImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public interface CookingRecipeRegister extends IRegister<BaseCookingRecipe> {

    @Nonnull
    static CookingRecipeRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return CookingRecipeRegisterImpl.getInstance();
    }

    @Nullable
    static CookingRecipeRegister getInstanceUnsafe() {
        BarleyTeaAPI.checkPluginUsable();
        return CookingRecipeRegisterImpl.getInstanceUnsafe();
    }

    static boolean hasRegistered() {
        return ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), CookingRecipeRegister::hasAnyRegistered), false);
    }
}
