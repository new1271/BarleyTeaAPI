package org.ricetea.barleyteaapi.api.internal.nms;

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMaps;
import org.bukkit.Bukkit;
import org.checkerframework.checker.index.qual.Positive;

import java.util.Arrays;
import java.util.function.IntSupplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public enum NMSVersion implements IntSupplier {
    LowerBound(762, false),
    v1_20_R1(763, true),
    v1_20_R2(764, true),
    v1_20_R3(765, true),
    v1_20_R4(766, true),
    v1_21_R1(767, true),
    UpperBound(768, false),
    ;

    @Positive
    final int version;
    final boolean valid;
    static final Int2ObjectSortedMap<NMSVersion> map = Int2ObjectSortedMaps.unmodifiable(
            (Int2ObjectSortedMap<? extends NMSVersion>) Arrays.stream(values())
                    .collect(Collectors.toMap(
                            NMSVersion::getVersion,
                            UnaryOperator.identity(),
                            (a, b) -> a,
                            Int2ObjectAVLTreeMap::new))
    );

    NMSVersion(@Positive int version, boolean valid) {
        this.version = version;
        this.valid = valid;
    }

    @Positive
    @Override
    public final int getAsInt() {
        return version;
    }

    @Positive
    public final int getVersion() {
        return version;
    }

    @Positive
    public final int version() {
        return version;
    }

    public final boolean isValid() {
        return valid;
    }

    @SuppressWarnings("deprecation")
    public static NMSVersion getCurrent() {
        int version = Bukkit.getUnsafe().getProtocolVersion();
        if (version <= NMSVersion.LowerBound.getVersion())
            return NMSVersion.LowerBound;
        if (version >= NMSVersion.UpperBound.getVersion())
            return NMSVersion.UpperBound;
        return map.get(version);
    }
}
