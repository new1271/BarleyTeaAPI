package org.ricetea.barleyteaapi.internal.item.registration;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.event.ItemsRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsUnregisteredEvent;
import org.ricetea.barleyteaapi.api.internal.item.CustomItemTypeImpl;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldPlayerMove;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemTick;
import org.ricetea.barleyteaapi.api.item.feature.ItemFeature;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.api.localization.LocalizationRegister;
import org.ricetea.barleyteaapi.api.localization.LocalizedMessageFormat;
import org.ricetea.barleyteaapi.internal.base.registration.CustomObjectRegisterBase;
import org.ricetea.barleyteaapi.internal.listener.PlayerMoveListener;
import org.ricetea.barleyteaapi.internal.task.ItemTickTask;
import org.ricetea.barleyteaapi.util.SyncUtil;
import org.ricetea.utils.Constants;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
@ApiStatus.Internal
public final class ItemRegisterImpl extends CustomObjectRegisterBase<CustomItem, ItemFeature> implements ItemRegister {

    @Override
    public void register(@Nullable CustomItem item) {
        if (item == null)
            return;
        registerAll(Set.of(item));
    }

    @Override
    public void registerAll(@Nullable Collection<? extends CustomItem> items) {
        if (items == null)
            return;
        LocalizationRegister localizationRegister = LocalizationRegister.getInstance();
        Map<NamespacedKey, CustomItem> lookupMap = getLookupMap();
        items.forEach(_item -> {
            if (_item == null)
                return;
            CustomItem oldItem = lookupMap.put(_item.getKey(), _item);
            checkFeature(oldItem, true);
            checkFeature(_item, false);
            String translationKey = _item.getTranslationKey();
            LocalizedMessageFormat oldFormat = localizationRegister.lookup(translationKey);
            if (oldFormat != null && oldFormat.getLocales().contains(LocalizedMessageFormat.DEFAULT_LOCALE))
                return;
            LocalizedMessageFormat format = LocalizedMessageFormat.create(translationKey);
            if (oldFormat != null) {
                oldFormat.getLocales().forEach(locale ->
                        format.setFormat(locale, oldFormat.getFormat(locale)));
            }
            format.setFormat(new MessageFormat(_item.getDefaultName()));
            localizationRegister.register(format);
        });
        refreshFeature();
        BarleyTeaAPI apiInst = BarleyTeaAPI.getInstanceUnsafe();
        if (apiInst != null) {
            Logger logger = apiInst.getLogger();
            items.forEach(_item -> {
                if (_item == null)
                    return;
                logger.info(LOGGING_REGISTERED_FORMAT.formatted(_item.getKey(), "item"));
            });
        }
        SyncUtil.callInMainThread(apiInst, () ->
                Bukkit.getPluginManager().callEvent(new ItemsRegisteredEvent(items)));
    }

    private void checkFeature(@Nullable CustomItem item, boolean forRemoval) {
        if (item == null)
            return;
        if (forRemoval)
            unregisterFeatures(item);
        else
            registerFeatures(item);
    }

    private void refreshFeature() {
        if (hasFeature(FeatureItemTick.class)) {
            ItemTickTask.getInstance().start();
        } else {
            ItemTickTask.getInstance().stop();
        }
        if (hasFeature(FeatureItemHoldPlayerMove.class)) {
            PlayerMoveListener.getInstance().tryRegisterEvents();
        } else {
            ObjectUtil.safeCall(PlayerMoveListener.getInstanceUnsafe(), PlayerMoveListener::tryUnregisterEvents);
        }
    }

    @Override
    public void unregister(@Nullable CustomItem item) {
        if (item == null || !getLookupMap().remove(item.getKey(), item))
            return;
        checkFeature(item, true);
        Set<CustomItem> items = Set.of(item);
        CustomItemTypeImpl.removeInstances(items);
        refreshFeature();
        BarleyTeaAPI apiInst = BarleyTeaAPI.getInstanceUnsafe();
        if (apiInst != null) {
            Logger logger = apiInst.getLogger();
            logger.info(LOGGING_UNREGISTERED_FORMAT.formatted(item.getKey()));
        }
        SyncUtil.callInMainThread(apiInst, () ->
                Bukkit.getPluginManager().callEvent(new ItemsUnregisteredEvent(items)));
    }

    @Override
    public void unregisterAll(@Nullable Predicate<? super CustomItem> predicate) {
        if (isEmpty())
            return;
        Map<NamespacedKey, CustomItem> lookupMap = getLookupMap();
        Collection<CustomItem> values = lookupMap.values();
        Stream<CustomItem> stream = values.stream();
        if (predicate != null) {
            if (getCachedSize() >= Constants.MIN_ITERATION_COUNT_FOR_PARALLEL)
                stream = stream.parallel();
            stream = stream.filter(predicate);
        }
        Set<CustomItem> items = stream.collect(Collectors.toUnmodifiableSet());
        items.forEach(_item -> {
            if (_item == null)
                return;
            if (lookupMap.remove(_item.getKey(), _item)) {
                checkFeature(_item, true);
            }
        });
        CustomItemTypeImpl.removeInstances(items);
        refreshFeature();
        BarleyTeaAPI apiInst = BarleyTeaAPI.getInstanceUnsafe();
        if (apiInst != null) {
            Logger logger = apiInst.getLogger();
            items.forEach(_item ->
                    logger.info(LOGGING_UNREGISTERED_FORMAT.formatted(_item.getKey())));
        }
        SyncUtil.callInMainThread(apiInst, () ->
                Bukkit.getPluginManager().callEvent(new ItemsUnregisteredEvent(items)));
    }
}
