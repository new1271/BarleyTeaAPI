package org.ricetea.barleyteaapi.internal.nms;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Hashtable;

public final class NMSHelperRegister {

    private static final Hashtable<Class<? extends IHelper>, IHelper> helperMap = new Hashtable<>();

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends IHelper> T getHelper(@Nonnull Class<T> clazz) {
        return (T) helperMap.get(clazz);
    }

    public static <T extends IHelper> void setHelper(@Nullable T helper, @Nonnull Class<T> clazz) {
        helperMap.put(clazz, helper);
    }
}
