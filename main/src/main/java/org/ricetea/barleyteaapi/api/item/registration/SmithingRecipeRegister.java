package org.ricetea.barleyteaapi.api.item.registration;

import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.base.registration.NSKeyedRegister;
import org.ricetea.barleyteaapi.api.item.recipe.BaseSmithingRecipe;
import org.ricetea.barleyteaapi.internal.item.registration.SmithingRecipeRegisterImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public interface SmithingRecipeRegister extends NSKeyedRegister<BaseSmithingRecipe> {

    @Nonnull
    static SmithingRecipeRegister getInstance() {
        return SmithingRecipeRegisterImpl.getInstance();
    }

    @Nullable
    static SmithingRecipeRegister getInstanceUnsafe() {
        return SmithingRecipeRegisterImpl.getInstanceUnsafe();
    }

    static boolean hasRegistered() {
        return !ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), IRegister::isEmpty), true);
    }
}
