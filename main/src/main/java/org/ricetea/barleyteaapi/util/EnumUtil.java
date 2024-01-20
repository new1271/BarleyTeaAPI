package org.ricetea.barleyteaapi.util;

import java.util.Objects;

public class EnumUtil {
    public static <E extends Enum<E>> E maxOrdinal(E a, E b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return a.ordinal() > b.ordinal() ? a : b;
    }

    public static <E extends Enum<E>> E minOrdinal(E a, E b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return a.ordinal() < b.ordinal() ? a : b;
    }
}
