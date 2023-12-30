package org.ricetea.barleyteaapi.internal.linker;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.base.data.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.utils.MathHelper;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

@ApiStatus.Internal
public final class ItemFeatureLinker {

    public static <TFeature, TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean forEachEquipmentCancellable(
            @Nullable LivingEntity entity, @Nullable TEvent event, @Nonnull EquipmentSlot[] slots,
            @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment<TEvent, TData> dataConstructor) {
        if (entity == null || event == null || !ItemRegister.hasRegistered())
            return true;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null)
            return true;
        boolean isCopy = !(equipment instanceof PlayerInventory);
        for (EquipmentSlot slot : slots) {
            if (slot != null) {
                ItemStack itemStack = equipment.getItem(slot);
                boolean result = ObjectUtil.tryMap(() ->
                        doFeatureCancellable(itemStack, slot, event, featureClass, featureFunc,
                                dataConstructor), true);
                if (result) {
                    if (isCopy) {
                        equipment.setItem(slot, itemStack, true);
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public static <TFeature, TEvent extends Event, TEvent2 extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean forEachEquipmentCancellable(
            @Nullable LivingEntity entity, @Nullable TEvent event,
            @Nullable TEvent2 event2, @Nonnull EquipmentSlot[] slots,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment2<TEvent, TEvent2, TData> dataConstructor) {
        if (entity == null || event == null || !ItemRegister.hasRegistered())
            return true;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null)
            return true;
        boolean isCopy = !(equipment instanceof PlayerInventory);
        for (EquipmentSlot slot : slots) {
            if (slot != null) {
                ItemStack itemStack = equipment.getItem(slot);
                boolean result = ObjectUtil.tryMap(() ->
                        doFeatureCancellable(itemStack, slot, event, event2, featureClass, featureFunc,
                                dataConstructor), true);
                if (result) {
                    if (isCopy) {
                        equipment.setItem(slot, itemStack, true);
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public static <TFeature, TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> void forEachEquipment(
            @Nullable LivingEntity entity, @Nullable TEvent event, @Nonnull EquipmentSlot[] slots,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment<TEvent, TData> dataConstructor) {
        if (entity == null || event == null || !ItemRegister.hasRegistered())
            return;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null)
            return;
        boolean isCopy = !(equipment instanceof PlayerInventory);
        for (EquipmentSlot slot : slots) {
            if (slot != null) {
                ItemStack itemStack = equipment.getItem(slot);
                ObjectUtil.tryCall(() ->
                        doFeature(itemStack, slot, event, featureClass, featureFunc,
                                dataConstructor));
                if (isCopy) {
                    equipment.setItem(slot, itemStack, true);
                }
            }
        }
    }

    public static <TFeature, TEvent extends Event, TEvent2 extends Event,
            TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nullable ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable TEvent event,
            @Nullable TEvent2 event2, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment2<TEvent, TEvent2, TData> dataConstructor) {
        if (itemStack == null || event == null || equipmentSlot == null || !ItemRegister.hasRegistered())
            return true;
        TFeature feature = ObjectUtil.tryCast(CustomItem.get(itemStack), featureClass);
        if (feature == null)
            return true;
        return ObjectUtil.tryMap(() -> {
            boolean result = featureFunc.test(feature,
                    dataConstructor.apply(event, event2, itemStack, equipmentSlot));
            if (event instanceof Cancellable cancellable) {
                result &= !cancellable.isCancelled();
            }
            return result;
        }, true);
    }

    public static <TFeature, TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nullable ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment<TEvent, TData> dataConstructor) {
        if (itemStack == null || event == null || equipmentSlot == null || !ItemRegister.hasRegistered())
            return true;
        TFeature feature = ObjectUtil.tryCast(CustomItem.get(itemStack), featureClass);
        if (feature == null)
            return true;
        return ObjectUtil.tryMap(() -> {
            boolean result = featureFunc.test(feature,
                    dataConstructor.apply(event, itemStack, equipmentSlot));
            if (event instanceof Cancellable cancellable) {
                result &= !cancellable.isCancelled();
            }
            return result;
        }, true);
    }

    public static <TFeature, TEvent extends Event, TData extends BaseFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nullable ItemStack itemStack, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructor<TEvent, TData> dataConstructor) {
        if (itemStack == null || event == null || !ItemRegister.hasRegistered())
            return true;
        TFeature feature = ObjectUtil.tryCast(CustomItem.get(itemStack), featureClass);
        if (feature == null)
            return true;
        return ObjectUtil.tryMap(() -> {
            boolean result = featureFunc.test(feature, dataConstructor.apply(event));
            if (event instanceof Cancellable cancellable) {
                result &= !cancellable.isCancelled();
            }
            return result;
        }, true);
    }

    @Nonnull
    public static <TFeature, TEvent extends Event, TData extends BaseFeatureData<TEvent>, TReturn> TReturn doFeatureAndReturn(
            @Nullable ItemStack itemStack, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiFunction<TFeature, TData, TReturn> featureFunc,
            @Nonnull ItemDataConstructor<TEvent, TData> dataConstructor, @Nonnull TReturn defaultValue) {
        if (itemStack == null || event == null || !ItemRegister.hasRegistered())
            return defaultValue;
        TFeature feature = ObjectUtil.tryCast(CustomItem.get(itemStack), featureClass);
        if (feature == null)
            return defaultValue;
        return ObjectUtil.tryMap(() -> featureFunc.apply(feature, dataConstructor.apply(event)), defaultValue);
    }

    public static <TFeature, TEvent extends Event, TData extends BaseFeatureData<TEvent>> void doFeature(
            @Nullable ItemStack itemStack, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructor<TEvent, TData> dataConstructor) {
        if (itemStack == null || event == null || !ItemRegister.hasRegistered())
            return;
        TFeature feature = ObjectUtil.tryCast(CustomItem.get(itemStack), featureClass);
        if (feature == null)
            return;
        ObjectUtil.tryCall(() -> featureFunc.accept(feature, dataConstructor.apply(event)));
    }

    public static <TFeature, TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> void doFeature(
            @Nullable ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment<TEvent, TData> dataConstructor) {
        if (itemStack == null || event == null || equipmentSlot == null || !ItemRegister.hasRegistered())
            return;
        TFeature feature = ObjectUtil.tryCast(CustomItem.get(itemStack), featureClass);
        if (feature == null)
            return;
        ObjectUtil.tryCall(() -> featureFunc.accept(feature, dataConstructor.apply(event, itemStack, equipmentSlot)));
    }

    public static ItemStack doItemRepair(@Nullable ItemStack itemStackA, @Nullable ItemStack itemStackB,
                                         @Nullable ItemStack itemStackResult) {
        CustomItem itemType = CustomItem.get(itemStackA);
        if (itemType == null) {
            if (ItemHelper.isCustomItem(itemStackB)) {
                return null;
            } else {
                return itemStackResult;
            }
        } else {
            if (ItemHelper.isCertainItem(itemType, itemStackB)) {
                if (itemStackResult == null && itemType instanceof FeatureItemGive itemGiveFeature) {
                    try {
                        itemStackResult = itemGiveFeature.handleItemGive(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (itemStackResult != null && !itemStackResult.getType().isAir()) {
                    if (itemType instanceof FeatureItemCustomDurability feature) {
                        try {
                            int maxDura = feature.getMaxDurability(itemStackA);
                            int upperItemDamage = feature.getDurabilityDamage(itemStackA);
                            int lowerItemDamage = feature.getDurabilityDamage(itemStackB);
                            int newDamage = MathHelper.between(
                                    upperItemDamage + lowerItemDamage - maxDura - maxDura / 20
                                    , 0, maxDura);
                            feature.setDurabilityDamage(itemStackResult, newDamage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return itemStackResult;
            } else {
                return null;
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
}
