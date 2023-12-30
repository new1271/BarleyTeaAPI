package org.ricetea.barleyteaapi.api.item.registration;

import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCraftingRecipe;
import org.ricetea.barleyteaapi.internal.item.registration.CraftingRecipeRegisterImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public interface CraftingRecipeRegister extends IRegister<BaseCraftingRecipe> {

    @Nonnull
    static CraftingRecipeRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return CraftingRecipeRegisterImpl.getInstance();
    }

    @Nullable
    static CraftingRecipeRegister getInstanceUnsafe() {
        BarleyTeaAPI.checkPluginUsable();
        return CraftingRecipeRegisterImpl.getInstanceUnsafe();
    }

    static boolean hasRegistered() {
        return ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), CraftingRecipeRegister::hasAnyRegistered), false);
    }
}
