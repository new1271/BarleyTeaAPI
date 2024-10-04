package org.ricetea.barleyteaapi.internal.connector.patch;

import net.kyori.adventure.text.Component;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface EraseTranslateFallbacksFunction {
    @Nonnull
    Component apply(@Nonnull Component component);
}
