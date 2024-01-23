package org.ricetea.barleyteaapi.api.item.registration;

import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.base.registration.NSKeyedRegister;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCraftingRecipe;
import org.ricetea.barleyteaapi.internal.item.registration.CraftingRecipeRegisterImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public interface CraftingRecipeRegister extends NSKeyedRegister<BaseCraftingRecipe> {

    @Nonnull
    static CraftingRecipeRegister getInstance() {
        return CraftingRecipeRegisterImpl.getInstance();
    }

    @Nullable
    static CraftingRecipeRegister getInstanceUnsafe() {
        return CraftingRecipeRegisterImpl.getInstanceUnsafe();
    }

    static boolean hasRegistered() {
        return !ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), IRegister::isEmpty), true);
    }
}
