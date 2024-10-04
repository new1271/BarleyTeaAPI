package org.ricetea.barleyteaapi.internal.v2.connector.handler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.Translator;
import org.bukkit.inventory.meta.ItemMeta;
import org.ricetea.barleyteaapi.internal.connector.patch.ApplyTranslateFallbacksFunction;
import org.ricetea.barleyteaapi.internal.connector.patch.EraseTranslateFallbacksFunction;
import org.ricetea.barleyteaapi.internal.connector.patch.ProtocolLibConnectorPatch;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import java.util.Locale;

public final class ProtocolLibConnectorPatchImpl implements ProtocolLibConnectorPatch {

    @Override
    public boolean afterApplyTranslateFallbacks(@Nonnull Translator translator, @Nonnull ItemMeta itemMeta,
                                                @Nonnull Locale locale, @Nonnull ApplyTranslateFallbacksFunction function) {
        if (!itemMeta.hasItemName())
            return false;
        Component itemName = ObjectUtil.tryMapSilently(itemMeta::itemName);
        if (itemName == null)
            return false;
        itemMeta.itemName(function.apply(translator, itemName, locale));
        return true;
    }

    @Override
    public boolean afterEraseTranslateFallbacks(@Nonnull ItemMeta itemMeta, @Nonnull EraseTranslateFallbacksFunction function) {

        if (!itemMeta.hasItemName())
            return false;
        Component itemName = ObjectUtil.tryMapSilently(itemMeta::itemName);
        if (itemName == null)
            return false;
        itemMeta.itemName(function.apply(itemName));
        return true;
    }
}
