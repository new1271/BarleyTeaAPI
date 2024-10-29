package org.ricetea.barleyteaapi.internal.connector.patch;

import net.kyori.adventure.translation.Translator;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.Locale;

public interface InteractiveChatConnectorPatch {
    boolean afterApplyTranslateFallbacks(@Nonnull Translator translator, @Nonnull ItemMeta itemMeta,
                                         @Nonnull Locale locale, @Nonnull ApplyTranslateFallbacksFunction function);
}
