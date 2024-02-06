package org.ricetea.barleyteaapi.api.item.registration;

import org.bukkit.Bukkit;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.base.registration.RecipeKeyedRegister;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCookingRecipe;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public interface CookingRecipeRegister extends RecipeKeyedRegister<BaseCookingRecipe> {

    @Nonnull
    static CookingRecipeRegister getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static CookingRecipeRegister getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(CookingRecipeRegister.class);
    }

    static boolean hasRegistered() {
        return !ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), IRegister::isEmpty), true);
    }
}
