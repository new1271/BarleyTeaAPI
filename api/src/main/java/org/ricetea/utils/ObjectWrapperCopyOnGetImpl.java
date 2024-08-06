package org.ricetea.utils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

class ObjectWrapperCopyOnGetImpl<T> implements ObjectWrapper<T> {
    private boolean needCopy;
    @Nonnull
    private T obj;
    @Nonnull
    private final Function<T, T> cloningFunction;

    @SuppressWarnings("unchecked")
    ObjectWrapperCopyOnGetImpl(@Nonnull T obj) {
        this(obj, getCloneFunction((Class<? extends T>) obj.getClass()));
    }

    ObjectWrapperCopyOnGetImpl(@Nonnull T obj, @Nonnull Function<T, T> cloningFunction) {
        this.obj = obj;
        this.cloningFunction = cloningFunction;
        needCopy = true;
    }

    private static <T> Function<T, T> getCloneFunction(@Nonnull Class<? extends T> clazz) {
        try {
            Method method = clazz.getMethod("clone");
            return (obj) -> {
                try {
                    Object rawObj = method.invoke(obj);
                    return ObjectUtil.letNonNull(ObjectUtil.tryCast(rawObj, clazz), obj);
                } catch (Exception e) {
                    return obj;
                }
            };
        } catch (Exception ignored) {

        }
        return Function.identity();
    }

    @Nonnull
    @Override
    public T get() {
        T obj = this.obj;
        if (needCopy) {
            this.obj = obj = Objects.requireNonNull(cloningFunction.apply(obj));
            needCopy = false;
        }
        return obj;
    }

    @Override
    public boolean isCalled() {
        return !needCopy;
    }
}
