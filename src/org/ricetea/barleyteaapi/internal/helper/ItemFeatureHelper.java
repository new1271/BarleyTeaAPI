package org.ricetea.barleyteaapi.internal.helper;

import java.util.function.BiPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class ItemFeatureHelper {
    private static final EquipmentSlot[] SLOTS = EquipmentSlot.values();

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
        if (itemStack != null && event != null && equipmentSlot != null) {
            NamespacedKey id = BaseItem.getItemID(itemStack);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(ItemRegister.getInstance().lookupItemType(id), featureClass);
                if (feature != null) {
                    return featureFunc.test(feature, dataConstructor.apply(event, event2, itemStack, equipmentSlot));
                }
            }
        }
        return true;
    }

    public static <TFeature, TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nullable ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull ItemDataConstructorForEquipment<TEvent, TData> dataConstructor) {
        if (itemStack != null && event != null && equipmentSlot != null) {
            NamespacedKey id = BaseItem.getItemID(itemStack);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(ItemRegister.getInstance().lookupItemType(id), featureClass);
                if (feature != null) {
                    return featureFunc.test(feature, dataConstructor.apply(event, itemStack, equipmentSlot));
                }
            }
        }
        return true;
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
                if (itemStackResult != null) {
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
