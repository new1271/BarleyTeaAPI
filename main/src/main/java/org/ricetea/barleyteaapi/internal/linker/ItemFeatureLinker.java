package org.ricetea.barleyteaapi.internal.linker;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.base.data.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.helper.FeatureHelper;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemSlotFilter;
import org.ricetea.barleyteaapi.api.item.feature.ItemFeature;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemSlotFilter;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.utils.MathHelper;
import org.ricetea.utils.ObjectUtil;
import org.ricetea.utils.ObjectWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

@ApiStatus.Internal
public final class ItemFeatureLinker {

    public static <TFeature extends ItemFeature, TEvent extends Event,
            TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean forEachEquipmentCancellable(
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
            if (slot == null || !ItemHelper.isSuitableForEntityType(slot, entity.getType()))
                continue;
            ItemStack itemStack = equipment.getItem(slot);
            CustomItem itemType = CustomItem.get(itemStack);
            if (itemType == null)
                continue;
            ObjectWrapper<ItemStack> wrappedItemStack = ObjectWrapper.wrap(itemStack);
            boolean result = ObjectUtil.tryMap(() ->
                    doFeatureCancellable(itemType, wrappedItemStack, slot, event, featureClass, featureFunc,
                            dataConstructor), true);
            if (result) {
                if (isCopy && wrappedItemStack.isCalled()) {
                    equipment.setItem(slot, itemStack, true);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public static <TFeature extends ItemFeature, TEvent extends Event, TEvent2 extends Event,
            TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean forEachEquipmentCancellable(
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
            if (slot == null || !ItemHelper.isSuitableForEntityType(slot, entity.getType()))
                continue;
            ItemStack itemStack = equipment.getItem(slot);
            CustomItem itemType = CustomItem.get(itemStack);
            if (itemType == null)
                continue;
            ObjectWrapper<ItemStack> wrappedItemStack = ObjectWrapper.wrap(itemStack);
            boolean result = ObjectUtil.tryMap(() ->
                    doFeatureCancellable(itemType, wrappedItemStack, slot, event,
                            event2, featureClass, featureFunc, dataConstructor), true);
            if (result) {
                if (isCopy && wrappedItemStack.isCalled()) {
                    equipment.setItem(slot, itemStack, true);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public static <TFeature extends ItemFeature, TEvent extends Event,
            TData extends BaseItemHoldEntityFeatureData<TEvent>> void forEachEquipment(
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
            if (slot == null || !ItemHelper.isSuitableForEntityType(slot, entity.getType()))
                continue;
            ItemStack itemStack = equipment.getItem(slot);
            CustomItem itemType = CustomItem.get(itemStack);
            if (itemType == null)
                continue;
            ObjectWrapper<ItemStack> wrappedItemStack = ObjectWrapper.wrap(itemStack);
            ObjectUtil.tryCall(() ->
                    doFeature(itemType, wrappedItemStack, slot,
                            event, featureClass, featureFunc, dataConstructor));
            if (isCopy && wrappedItemStack.isCalled()) {
                equipment.setItem(slot, wrappedItemStack.get(), true);
            }
        }
    }

    public static <TFeature extends ItemFeature, TEvent extends Event, TEvent2 extends Event,
            TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nullable ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable TEvent event,
            @Nullable TEvent2 event2, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment2<TEvent, TEvent2, TData> dataConstructor) {
        if (itemStack == null || event == null || equipmentSlot == null || !ItemRegister.hasRegistered())
            return true;
        CustomItem itemType = CustomItem.get(itemStack);
        if (itemType == null)
            return true;
        return doFeatureCancellable(itemType, ObjectWrapper.wrap(itemStack),
                equipmentSlot, event, event2, featureClass, featureFunc, dataConstructor);
    }

    private static <TFeature extends ItemFeature, TEvent extends Event, TEvent2 extends Event,
            TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nonnull CustomItem itemType, @Nonnull ObjectWrapper<ItemStack> wrappedItemStack,
            @Nonnull EquipmentSlot equipmentSlot, @Nonnull TEvent event,
            @Nullable TEvent2 event2, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment2<TEvent, TEvent2, TData> dataConstructor) {
        TFeature feature = itemType.getFeature(featureClass);
        if (feature == null)
            return true;
        FeatureItemSlotFilter filterFeature = itemType.getFeature(FeatureItemSlotFilter.class);
        if (filterFeature != null) {
            ItemStack itemStack = wrappedItemStack.get();
            if (!filterFeature.handleItemSlotFilter(new DataItemSlotFilter(itemStack, equipmentSlot)))
                return true;
        }
        return ObjectUtil.tryMap(() -> {
            boolean result = featureFunc.test(feature,
                    dataConstructor.apply(event, event2, wrappedItemStack.get(), equipmentSlot));
            if (event instanceof Cancellable cancellable) {
                result &= !cancellable.isCancelled();
            }
            return result;
        }, true);
    }

    public static <TFeature extends ItemFeature, TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nullable ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment<TEvent, TData> dataConstructor) {
        if (itemStack == null || event == null || equipmentSlot == null || !ItemRegister.hasRegistered())
            return true;
        CustomItem itemType = CustomItem.get(itemStack);
        if (itemType == null)
            return true;
        return doFeatureCancellable(itemType, ObjectWrapper.wrap(itemStack), equipmentSlot,
                event, featureClass, featureFunc, dataConstructor);
    }

    private static <TFeature extends ItemFeature, TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nonnull CustomItem itemType, @Nonnull ObjectWrapper<ItemStack> wrappedItemStack,
            @Nonnull EquipmentSlot equipmentSlot, @Nonnull TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment<TEvent, TData> dataConstructor) {
        TFeature feature = itemType.getFeature(featureClass);
        if (feature == null)
            return true;
        FeatureItemSlotFilter filterFeature = itemType.getFeature(FeatureItemSlotFilter.class);
        if (filterFeature != null) {
            ItemStack itemStack = wrappedItemStack.get();
            if (!filterFeature.handleItemSlotFilter(new DataItemSlotFilter(itemStack, equipmentSlot)))
                return true;
        }
        return ObjectUtil.tryMap(() -> {
            boolean result = featureFunc.test(feature,
                    dataConstructor.apply(event, wrappedItemStack.get(), equipmentSlot));
            if (event instanceof Cancellable cancellable) {
                result &= !cancellable.isCancelled();
            }
            return result;
        }, true);
    }

    public static <TFeature extends ItemFeature, TEvent extends Event, TData extends BaseFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nullable ItemStack itemStack, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructor<TEvent, TData> dataConstructor) {
        if (itemStack == null || event == null || !ItemRegister.hasRegistered())
            return true;
        TFeature feature = ObjectUtil.tryMap(CustomItem.get(itemStack), (obj) -> obj.getFeature(featureClass));
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

    public static <TFeature extends ItemFeature, TEvent extends Event, TData extends BaseFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nullable ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructor<TEvent, TData> dataConstructor) {
        if (itemStack == null || event == null || !ItemRegister.hasRegistered())
            return true;
        CustomItem itemType = CustomItem.get(itemStack);
        if (itemType == null)
            return true;
        TFeature feature = itemType.getFeature(featureClass);
        if (feature == null)
            return true;
        if (equipmentSlot != null) {
            FeatureItemSlotFilter filterFeature = itemType.getFeature(FeatureItemSlotFilter.class);
            if (filterFeature != null) {
                if (!filterFeature.handleItemSlotFilter(new DataItemSlotFilter(itemStack, equipmentSlot)))
                    return true;
            }
        }
        return ObjectUtil.tryMap(() -> {
            boolean result = featureFunc.test(feature, dataConstructor.apply(event));
            if (event instanceof Cancellable cancellable) {
                result &= !cancellable.isCancelled();
            }
            return result;
        }, true);
    }

    @Nonnull
    public static <TFeature extends ItemFeature, TEvent extends Event, TData extends BaseFeatureData<TEvent>, TReturn> TReturn doFeatureAndReturn(
            @Nullable ItemStack itemStack, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiFunction<TFeature, TData, TReturn> featureFunc,
            @Nonnull ItemDataConstructor<TEvent, TData> dataConstructor, @Nonnull TReturn defaultValue) {
        if (itemStack == null || event == null || !ItemRegister.hasRegistered())
            return defaultValue;
        TFeature feature = ObjectUtil.tryMap(CustomItem.get(itemStack), (obj) -> obj.getFeature(featureClass));
        if (feature == null)
            return defaultValue;
        return ObjectUtil.tryMap(() -> featureFunc.apply(feature, dataConstructor.apply(event)), defaultValue);
    }

    @Nonnull
    public static <TFeature extends ItemFeature, TEvent extends Event, TData extends BaseFeatureData<TEvent>, TReturn> TReturn doFeatureAndReturn(
            @Nullable ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiFunction<TFeature, TData, TReturn> featureFunc,
            @Nonnull ItemDataConstructor<TEvent, TData> dataConstructor, @Nonnull TReturn defaultValue) {
        if (itemStack == null || event == null || !ItemRegister.hasRegistered())
            return defaultValue;
        CustomItem itemType = CustomItem.get(itemStack);
        if (itemType == null)
            return defaultValue;
        TFeature feature = itemType.getFeature(featureClass);
        if (feature == null)
            return defaultValue;
        if (equipmentSlot != null) {
            FeatureItemSlotFilter filterFeature = itemType.getFeature(FeatureItemSlotFilter.class);
            if (filterFeature != null) {
                if (!filterFeature.handleItemSlotFilter(new DataItemSlotFilter(itemStack, equipmentSlot)))
                    return defaultValue;
            }
        }
        return ObjectUtil.tryMap(() -> featureFunc.apply(feature, dataConstructor.apply(event)), defaultValue);
    }

    public static <TFeature extends ItemFeature, TEvent extends Event, TData extends BaseFeatureData<TEvent>> void doFeature(
            @Nullable ItemStack itemStack, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructor<TEvent, TData> dataConstructor) {
        if (itemStack == null || event == null || !ItemRegister.hasRegistered())
            return;
        TFeature feature = ObjectUtil.tryMap(CustomItem.get(itemStack), (obj) -> obj.getFeature(featureClass));
        if (feature == null)
            return;
        ObjectUtil.tryCall(() -> featureFunc.accept(feature, dataConstructor.apply(event)));
    }

    public static <TFeature extends ItemFeature, TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> void doFeature(
            @Nullable ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment<TEvent, TData> dataConstructor) {
        if (itemStack == null || event == null || equipmentSlot == null || !ItemRegister.hasRegistered())
            return;
        CustomItem itemType = CustomItem.get(itemStack);
        if (itemType == null)
            return;
        doFeature(itemType, ObjectWrapper.wrap(itemStack), equipmentSlot,
                event, featureClass, featureFunc, dataConstructor);
    }

    private static <TFeature extends ItemFeature, TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> void doFeature(
            @Nonnull CustomItem itemType, @Nonnull ObjectWrapper<ItemStack> wrappedItemStack,
            @Nonnull EquipmentSlot equipmentSlot, @Nonnull TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment<TEvent, TData> dataConstructor) {
        TFeature feature = itemType.getFeature(featureClass);
        if (feature == null)
            return;
        FeatureItemSlotFilter filterFeature = itemType.getFeature(FeatureItemSlotFilter.class);
        if (filterFeature != null) {
            ItemStack itemStack = wrappedItemStack.get();
            if (!filterFeature.handleItemSlotFilter(new DataItemSlotFilter(itemStack, equipmentSlot)))
                return;
        }
        ObjectUtil.tryCall(() -> featureFunc.accept(feature,
                dataConstructor.apply(event, wrappedItemStack.get(), equipmentSlot)));
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
                if (itemStackResult == null) {
                    try {
                        itemStackResult = FeatureHelper.mapIfHasFeature(itemType, FeatureItemGive.class, feature -> feature.handleItemGive(1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (itemStackResult != null && !itemStackResult.getType().isAir()) {
                    FeatureItemCustomDurability feature = itemType.getFeature(FeatureItemCustomDurability.class);
                    if (feature != null) {
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
                    } else if (itemStackResult.getItemMeta() instanceof Damageable damageableR &&
                            itemStackA.getItemMeta() instanceof Damageable damageableA &&
                            itemStackB.getItemMeta() instanceof Damageable damageableB) {
                        try {
                            int maxDura = itemStackResult.getType().getMaxDurability();
                            int upperItemDamage = damageableA.getDamage();
                            int lowerItemDamage = damageableB.getDamage();
                            int newDamage = MathHelper.between(
                                    upperItemDamage + lowerItemDamage - maxDura - maxDura / 20
                                    , 0, maxDura);
                            damageableR.setDamage(newDamage);
                            itemStackResult.setItemMeta(damageableR);
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
