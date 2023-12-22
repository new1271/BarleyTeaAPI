package org.ricetea.barleyteaapi.internal.nms;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

public final class NMSHelperRegister {

    private static final ConcurrentHashMap<Class<? extends IHelper>, IHelper> helperMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends IHelper> T getHelper(@Nonnull Class<T> clazz) {
        return (T) helperMap.get(clazz);
    }

    public static <T extends IHelper> void setHelper(@Nullable T helper, @Nonnull Class<T> clazz) {
        if (helper == null)
            helperMap.remove(clazz);
        else
            helperMap.put(clazz, helper);
    }
}
