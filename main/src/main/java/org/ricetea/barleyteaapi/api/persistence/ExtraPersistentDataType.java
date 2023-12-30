package org.ricetea.barleyteaapi.api.persistence;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.utils.Constants;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

public class ExtraPersistentDataType {
    public static final PersistentDataType<String, NamespacedKey> NAMESPACED_KEY = new NamespacedKeyDataTypeImpl();
    public static final PersistentDataType<long[], UUID> UUID_LONG_ARRAY = new UUIDLongArrayImpl();

    private static final class NamespacedKeyDataTypeImpl implements PersistentDataType<String, NamespacedKey> {
        @Override
        public @Nonnull Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public @Nonnull Class<NamespacedKey> getComplexType() {
            return NamespacedKey.class;
        }

        @Override
        public @Nonnull String toPrimitive(@Nonnull NamespacedKey complex, @Nonnull PersistentDataAdapterContext context) {
            return complex.asString();
        }

        @Override
        public @Nonnull NamespacedKey fromPrimitive(@Nonnull String primitive, @Nonnull PersistentDataAdapterContext context) {
            return Objects.requireNonNull(NamespacedKey.fromString(primitive));
        }
    }

    private static final class UUIDLongArrayImpl implements PersistentDataType<long[], UUID> {
        @Override
        public @Nonnull Class<long[]> getPrimitiveType() {
            return long[].class;
        }

        @Override
        public @Nonnull Class<UUID> getComplexType() {
            return UUID.class;
        }

        @Override
        public @Nonnull long[] toPrimitive(@Nonnull UUID complex, @Nonnull PersistentDataAdapterContext context) {
            return new long[]{complex.getMostSignificantBits(), complex.getLeastSignificantBits()};
        }

        @Override
        public @Nonnull UUID fromPrimitive(@Nonnull long[] primitive, @Nonnull PersistentDataAdapterContext context) {
            return switch (primitive.length) {
                case 0 -> Constants.EMPTY_UUID;
                case 1 -> new UUID(0, primitive[0]);
                default -> new UUID(primitive[0], primitive[1]);
            };
        }
    }
}
