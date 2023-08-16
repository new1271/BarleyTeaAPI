package org.ricetea.barleyteaapi.internal.helper;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseFeatureData;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class ItemFeatureHelper {
    private static final EquipmentSlot[] SLOTS = EquipmentSlot.values();
    private static final EquipmentSlot[] SLOTS_JustHands = new EquipmentSlot[] { EquipmentSlot.HAND,
            EquipmentSlot.OFF_HAND };

    public static <TFeature, TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean forEachEquipmentCancellable(
            @Nullable LivingEntity entity, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment<TEvent, TData> dataConstructor) {
        if (entity != null && event != null) {
            EntityEquipment equipment = entity.getEquipment();
            for (EquipmentSlot slot : SLOTS) {
                if (slot != null) {
                    ItemStack itemStack = equipment.getItem(slot);
                    if (!doFeatureCancellable(itemStack, slot, event, featureClass, featureFunc, dataConstructor)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static <TFeature, TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean forEachHandsCancellable(
            @Nullable LivingEntity entity, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment<TEvent, TData> dataConstructor) {
        if (entity != null && event != null) {
            EntityEquipment equipment = entity.getEquipment();
            for (EquipmentSlot slot : SLOTS_JustHands) {
                if (slot != null) {
                    ItemStack itemStack = equipment.getItem(slot);
                    if (!doFeatureCancellable(itemStack, slot, event, featureClass, featureFunc, dataConstructor)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static <TFeature, TEvent extends Event, TEvent2 extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean forEachEquipmentCancellable(
            @Nullable LivingEntity entity, @Nullable TEvent event, @Nullable TEvent2 event2,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment2<TEvent, TEvent2, TData> dataConstructor) {
        if (entity != null && event != null) {
            EntityEquipment equipment = entity.getEquipment();
            for (EquipmentSlot slot : SLOTS) {
                if (slot != null) {
                    ItemStack itemStack = equipment.getItem(slot);
                    if (!doFeatureCancellable(itemStack, slot, event, event2, featureClass, featureFunc,
                            dataConstructor)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static <TFeature, TEvent extends Event, TEvent2 extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nullable ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable TEvent event,
            @Nullable TEvent2 event2, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment2<TEvent, TEvent2, TData> dataConstructor) {
        ItemRegister register = ItemRegister.getInstanceUnsafe();
        if (register != null && itemStack != null && event != null && equipmentSlot != null) {
            NamespacedKey id = BaseItem.getItemID(itemStack);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(register.lookupItemType(id), featureClass);
                if (feature != null) {
                    boolean result = featureFunc.test(feature,
                            dataConstructor.apply(event, event2, itemStack, equipmentSlot));
                    if (event instanceof Cancellable cancellable) {
                        result &= !cancellable.isCancelled();
                    }
                    return result;
                }
            }
        }
        return true;
    }

    public static <TFeature, TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nullable ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment<TEvent, TData> dataConstructor) {
        ItemRegister register = ItemRegister.getInstanceUnsafe();
        if (register != null && itemStack != null && event != null && equipmentSlot != null) {
            NamespacedKey id = BaseItem.getItemID(itemStack);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(register.lookupItemType(id), featureClass);
                if (feature != null) {
                    boolean result = featureFunc.test(feature, dataConstructor.apply(event, itemStack, equipmentSlot));
                    if (event instanceof Cancellable cancellable) {
                        result &= !cancellable.isCancelled();
                    }
                    return result;
                }
            }
        }
        return true;
    }

    public static <TFeature, TEvent extends Event, TData extends BaseFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nullable ItemStack itemStack, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructor<TEvent, TData> dataConstructor) {
        ItemRegister register = ItemRegister.getInstanceUnsafe();
        if (register != null && itemStack != null && event != null) {
            NamespacedKey id = BaseItem.getItemID(itemStack);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(register.lookupItemType(id), featureClass);
                if (feature != null) {
                    boolean result = featureFunc.test(feature, dataConstructor.apply(event));
                    if (event instanceof Cancellable cancellable) {
                        result &= !cancellable.isCancelled();
                    }
                    return result;
                }
            }
        }
        return true;
    }

    @Nonnull
    public static <TFeature, TEvent extends Event, TData extends BaseFeatureData<TEvent>, TReturn> TReturn doFeatureAndReturn(
            @Nullable ItemStack itemStack, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiFunction<TFeature, TData, TReturn> featureFunc,
            @Nonnull ItemDataConstructor<TEvent, TData> dataConstructor, @Nonnull TReturn defaultValue) {
        ItemRegister register = ItemRegister.getInstanceUnsafe();
        if (register != null && itemStack != null && event != null) {
            NamespacedKey id = BaseItem.getItemID(itemStack);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(register.lookupItemType(id), featureClass);
                if (feature != null) {
                    return ObjectUtil.letNonNull(featureFunc.apply(feature, dataConstructor.apply(event)),
                            defaultValue);
                }
            }
        }
        return defaultValue;
    }

    public static <TFeature, TEvent extends Event, TData extends BaseFeatureData<TEvent>> void doFeature(
            @Nullable ItemStack itemStack, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructor<TEvent, TData> dataConstructor) {
        ItemRegister register = ItemRegister.getInstanceUnsafe();
        if (register != null && itemStack != null && event != null) {
            NamespacedKey id = BaseItem.getItemID(itemStack);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(register.lookupItemType(id), featureClass);
                if (feature != null) {
                    featureFunc.accept(feature, dataConstructor.apply(event));
                }
            }
        }
    }

    @FunctionalInterface
    public interface ItemDataConstructor<T extends Event, R extends BaseFeatureData<T>> {
        @Nonnull
        R apply(@Nonnull T event);
    }

    @FunctionalInterface
    public interface ItemDataConstructorForEquipment<T extends Event, R extends BaseItemHoldEntityFeatureData<T>> {
        @Nonnull
        R apply(@Nonnull T event, @Nonnull ItemStack itemStack, @Nonnull EquipmentSlot equipmentSlot);
    }

    @FunctionalInterface
    public interface ItemDataConstructorForEquipment2<T extends Event, T2 extends Event, R extends BaseItemHoldEntityFeatureData<T>> {
        @Nonnull
        R apply(@Nonnull T event, @Nullable T2 event2, @Nonnull ItemStack itemStack,
                @Nonnull EquipmentSlot equipmentSlot);
    }

    public static ItemStack doItemRepair(@Nullable ItemStack itemStackA, @Nullable ItemStack itemStackB,
            @Nullable ItemStack itemStackResult) {
        BaseItem itemType = ObjectUtil
                .letNonNull(ObjectUtil.mapWhenNonnull(itemStackA, BaseItem::getItemType), DataItemType::empty)
                .getItemTypeForBarleyTeaCustomItem();
        if (itemType == null) {
            if (BaseItem.isBarleyTeaItem(itemStackB)) {
                return null;
            } else {
                return itemStackResult;
            }
        } else {
            if (itemStackA != null && itemStackB != null
                    && itemType.isCertainItem(itemStackB)) {
                if (itemStackResult == null && itemType instanceof FeatureItemGive itemGiveFeature) {
                    itemStackResult = itemGiveFeature.handleItemGive(1);
                }
                if (itemStackResult != null && !itemStackResult.getType().isAir()) {
                    if (itemType instanceof FeatureItemCustomDurability customDurabilityFeature) {
                        int maxDura = customDurabilityFeature.getMaxDurability(itemStackA);
                        int upperItemDamage = customDurabilityFeature.getDurabilityDamage(itemStackA);
                        int lowerItemDamage = customDurabilityFeature.getDurabilityDamage(itemStackB);
                        int newDamage = Math
                                .max(Math.min(upperItemDamage + lowerItemDamage - maxDura - maxDura / 20, maxDura), 0);
                        customDurabilityFeature.setDurabilityDamage(itemStackResult, newDamage);
                    }
                }
                return itemStackResult;
            } else {
                return null;
            }
        }
    }
}
