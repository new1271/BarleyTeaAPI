package org.ricetea.barleyteaapi.api.internal.additional;

public interface IAdditionalPartEntryPoint {

    void onEnable();

    void applyPatchs();

    void onDisable();
}
