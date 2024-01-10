package org.ricetea.utils;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.function.Supplier;

public class SupplierUtil {

    @Nonnull
    public static <T> Supplier<T> fromConstuctor(@Nonnull Constructor<T> constructor, Object... initArgs) {
        return () -> {
            try {
                return constructor.newInstance(initArgs);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    @Nonnull
    public static <T> Supplier<T> fromConstuctor(@Nonnull Class<T> clazz, Object... initArgs) {
        return () -> {
            Constructor<T> constructor;
            try {
                constructor = clazz.getConstructor(initArgs == null ? null : Arrays.stream(initArgs)
                        .map(Object::getClass)
                        .toArray(Class<?>[]::new));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            try {
                return constructor.newInstance(initArgs);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    @Nonnull
    public static <T> Supplier<T> fromConstuctor(@Nonnull Class<T> clazz, boolean setAccessableIfRequired, Object... initArgs) {
        return () -> {
            Constructor<T> constructor;
            try {
                constructor = clazz.getDeclaredConstructor(initArgs == null ? null : Arrays.stream(initArgs)
                        .map(Object::getClass)
                        .toArray(Class<?>[]::new));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            try {
                return constructor.newInstance(initArgs);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }
}
