package org.ricetea.barleyteaapi.api.entity.counter;

public final class IncreaseFunctions {
    public static final TickingOperationFunction Increase = source -> source + 1;
    public static final TickingOperationFunction Decrease = source -> source - 1;
}
