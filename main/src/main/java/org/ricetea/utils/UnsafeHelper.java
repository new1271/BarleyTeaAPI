package org.ricetea.utils;

import sun.misc.Unsafe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Objects;

public class UnsafeHelper {
    @Nullable
    private static Unsafe unsafeInst; //cannot use Lazy here, because Lazy need unsafe memory fence!

    static {
        Field field;
        try {
            field = Unsafe.class.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            field = null;
        }
        if (field != null) {
            field.setAccessible(true);
            try {
                if (field.get(null) instanceof Unsafe unsafe) {
                    unsafeInst = unsafe;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Nonnull
    public static Unsafe getUnsafe() {
        return Objects.requireNonNull(unsafeInst);
    }

    @Nullable
    public static Unsafe getUnsafeUnsafely() {
        return unsafeInst;
    }
}
