package org.ricetea.barleyteaapi.api.base.registration;

import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.api.item.recipe.BaseRecipe;

import javax.annotation.Nonnull;
import java.util.Collection;

public interface RecipeKeyedRegister<T extends BaseRecipe> extends NSKeyedRegister<T> {
    Collection<T> listAllAssociatedWithDummyRecipe(@Nonnull NamespacedKey dummyKey);
}
