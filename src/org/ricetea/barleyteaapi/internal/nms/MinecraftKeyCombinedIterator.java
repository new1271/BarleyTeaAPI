package org.ricetea.barleyteaapi.internal.nms;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.ricetea.utils.ObjectUtil;

import net.minecraft.resources.MinecraftKey;

public class MinecraftKeyCombinedIterator implements Iterator<MinecraftKey> {

    boolean isInBuiltin = true;
    @Nonnull
    List<MinecraftKey> _builtins;
    @Nonnull
    List<MinecraftKey> _another;
    Iterator<MinecraftKey> currentIterator;

    public MinecraftKeyCombinedIterator(@Nonnull List<MinecraftKey> builtins, @Nullable List<MinecraftKey> another) {
        _builtins = builtins;
        _another = ObjectUtil.letNonNull(another, Collections::emptyList);
    }

    @Override
    public boolean hasNext() {
        Iterator<MinecraftKey> iterator = currentIterator;
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
    public MinecraftKey next() {
        Iterator<MinecraftKey> iterator = currentIterator;
        if (iterator == null)
            currentIterator = iterator = isInBuiltin ? _builtins.iterator() : _another.iterator();
        if (!iterator.hasNext() && isInBuiltin) {
            isInBuiltin = false;
            currentIterator = iterator = _another.iterator();
        }
        return iterator.next();
    }
}
