package org.ricetea.barleyteaapi.internal.nms.v1_21_R1.util;

import net.minecraft.resources.ResourceLocation;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class MinecraftKeyCombinedIterator implements Iterator<ResourceLocation> {

    boolean isInBuiltin = true;
    @Nonnull
    List<ResourceLocation> _builtins;
    @Nonnull
    List<ResourceLocation> _another;
    Iterator<ResourceLocation> currentIterator;

    public MinecraftKeyCombinedIterator(@Nonnull List<ResourceLocation> builtins, @Nullable List<ResourceLocation> another) {
        _builtins = builtins;
        _another = ObjectUtil.letNonNull(another, Collections::emptyList);
    }

    @Override
    public boolean hasNext() {
        Iterator<ResourceLocation> iterator = currentIterator;
        if (iterator == null)
            currentIterator = iterator = isInBuiltin ? _builtins.iterator() : _another.iterator();
        boolean result = iterator.hasNext();
        if (!result && isInBuiltin) {
            isInBuiltin = false;
            currentIterator = iterator = _another.iterator();
            result = iterator.hasNext();
        }
        return result;
    }

    @Override
    public ResourceLocation next() {
        Iterator<ResourceLocation> iterator = currentIterator;
        if (iterator == null)
            currentIterator = iterator = isInBuiltin ? _builtins.iterator() : _another.iterator();
        if (!iterator.hasNext() && isInBuiltin) {
            isInBuiltin = false;
            currentIterator = iterator = _another.iterator();
        }
        return iterator.next();
    }
}
