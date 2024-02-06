package org.ricetea.barleyteaapi.internal.localization;

import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.localization.LocalizationRegister;
import org.ricetea.barleyteaapi.api.localization.LocalizedMessageFormat;
import org.ricetea.barleyteaapi.internal.base.registration.StringKeyedRegisterBase;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class LocalizationRegisterImpl extends StringKeyedRegisterBase<LocalizedMessageFormat> implements LocalizationRegister {

    @Nonnull
    private final TranslationRegistry registry;

    public LocalizationRegisterImpl() {
        registry = TranslationRegistry.create(NamespacedKeyUtil.BarleyTeaAPI("translations"));
        GlobalTranslator.translator().addSource(registry);
    }

    @Nonnull
    @Override
    protected String getKeyFromItem(@Nonnull LocalizedMessageFormat item) {
        return item.getTranslationKey();
    }

    @Override
    public void register(@Nullable LocalizedMessageFormat format) {
        if (format == null)
            return;
        String key = getKeyFromItem(format);
        if (getLookupMap().put(key, format) != null) {
            registry.unregister(key);
        }
        format.getLocales().forEach(locale ->
                registry.register(key, locale, format.getFormat(locale)));
    }

    @Override
    public void unregister(@Nullable LocalizedMessageFormat format) {
        if (format == null)
            return;
        String key = getKeyFromItem(format);
        if (getLookupMap().put(key, format) == null)
            return;
        registry.unregister(key);
    }
}
