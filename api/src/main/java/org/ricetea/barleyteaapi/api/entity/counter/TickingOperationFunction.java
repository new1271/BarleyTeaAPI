package org.ricetea.barleyteaapi.api.entity.counter;

@FunctionalInterface
public interface TickingOperationFunction {
    int doOperation(int source);
}
