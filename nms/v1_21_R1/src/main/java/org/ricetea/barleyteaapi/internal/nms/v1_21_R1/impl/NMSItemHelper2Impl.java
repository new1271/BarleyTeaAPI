package org.ricetea.barleyteaapi.internal.nms.v1_21_R1.impl;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.internal.nms.INMSItemHelper2;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Constants;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class NMSItemHelper2Impl implements INMSItemHelper2 {
    private static final NMSItemHelper2Impl _inst = new NMSItemHelper2Impl();
    private static final NamespacedKey OriginalItemDamageKey = NamespacedKeyUtil.BarleyTeaAPI("original_item_damage");
    private static final NamespacedKey OldMaxStackSizeKey = NamespacedKeyUtil.BarleyTeaAPI("old_max_stack_size");
    private static final NamespacedKey OldDamageKey = NamespacedKeyUtil.BarleyTeaAPI("old_damage");
    private static final NamespacedKey OldMaxDamageKey = NamespacedKeyUtil.BarleyTeaAPI("old_max_damage");

    private NMSItemHelper2Impl() {
    }

    @Nonnull
    public static NMSItemHelper2Impl getInstance() {
        return _inst;
    }

    @Nonnull
    @Override
    public RecipeChoice getEmptyRecipeChoice() {
        return RecipeChoice.empty();
    }

    @SuppressWarnings("UnstableApiUsage")
    @Nonnull
    @Override
    public AttributeModifier getDefaultAttributeModifier(@Nonnull Attribute attribute, double amount,
                                                         @Nonnull AttributeModifier.Operation operation,
                                                         @Nullable EquipmentSlot equipmentSlot) {
        return new AttributeModifier(Constants.getDefaultAttributeModifierKey(attribute, equipmentSlot),
                amount, operation,
                ObjectUtil.letNonNull(ObjectUtil.safeMap(equipmentSlot, EquipmentSlot::getGroup),
                        EquipmentSlotGroup.ANY));
    }

    private static void setValueOrRemove(@Nonnull PersistentDataContainer container,
                                         @Nonnull NamespacedKey namespacedKey, int val) {
        try {
            container.set(namespacedKey, PersistentDataType.INTEGER, val);
        } catch (Exception ignored) {
            ObjectUtil.tryCall(namespacedKey, container::remove);
        }
    }

    private static Integer getValueAndRemove(@Nonnull PersistentDataContainer container,
                                             @Nonnull NamespacedKey namespacedKey) {
        Integer result;
        try {
            result = container.get(namespacedKey, PersistentDataType.INTEGER);
        } catch (Exception ignored) {
            result = null;
        }
        ObjectUtil.tryCall(namespacedKey, container::remove);
        return result;
    }

    @Override
    public void applyCustomDurabilityBar(@Nonnull Damageable itemMeta, int damage, int maxDurability) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        try {
            container.set(OriginalItemDamageKey, PersistentDataType.INTEGER, damage);
        } catch (Exception ignored) {
            container.remove(OriginalItemDamageKey);
            return;
        }
        itemMeta.setMaxDamage(maxDurability);
        itemMeta.setDamage(damage);
    }

    @Nonnull
    @Override
    public ItemStack applyCustomDurabilityBarSpecial(@Nonnull ItemStack itemStack, int damage, int maxDurability) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.unwrap(itemStack);

        DataComponentMap map = nmsItemStack.getComponents();
        DataComponentPatch.Builder patchBuilder = DataComponentPatch.builder();

        Integer maxStackSizeRaw = map.get(DataComponents.MAX_STACK_SIZE);
        Integer maxDamageRaw = map.get(DataComponents.MAX_DAMAGE);
        Integer damageRaw = map.get(DataComponents.DAMAGE);

        patchBuilder.set(DataComponents.MAX_STACK_SIZE, 1);
        patchBuilder.set(DataComponents.MAX_DAMAGE, maxDurability);
        patchBuilder.set(DataComponents.DAMAGE, damage);

        nmsItemStack.applyComponents(patchBuilder.build());

        itemStack = nmsItemStack.asBukkitMirror();
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null)
            return itemStack;

        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (maxStackSizeRaw != null) {
            setValueOrRemove(container, OldMaxStackSizeKey, maxStackSizeRaw);
        }

        if (maxDamageRaw != null) {
            setValueOrRemove(container, OldMaxDamageKey, maxDamageRaw);
        }

        if (damageRaw != null) {
            setValueOrRemove(container, OldDamageKey, damageRaw);
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    @Override
    public void restoreCustomDurabilityBar(@Nonnull Damageable itemMeta, int maxDurability) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        Integer damage = getValueAndRemove(container, OriginalItemDamageKey);
        if (damage == null)
            return;
        itemMeta.setDamage(damage);
        itemMeta.setMaxDamage(maxDurability);
    }

    @Override
    public boolean isNeedSpecialRestore(@Nonnull ItemMeta itemMeta) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(OldMaxStackSizeKey) || container.has(OldMaxDamageKey) || container.has(OldDamageKey);
    }

    @Nonnull
    @Override
    public ItemStack restoreCustomDurabilityBarSpecial(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return itemStack;

        PersistentDataContainer container = meta.getPersistentDataContainer();

        DataComponentPatch.Builder patchBuilder = DataComponentPatch.builder();

        Integer val = getValueAndRemove(container, OldMaxStackSizeKey);
        if (val == null) { //restore minecraft:max_stack_size
            patchBuilder.remove(DataComponents.MAX_STACK_SIZE);
        } else {
            patchBuilder.set(DataComponents.MAX_STACK_SIZE, val);
        }

        val = getValueAndRemove(container, OldMaxDamageKey);
        if (val == null) { //restore minecraft:max_damage
            patchBuilder.remove(DataComponents.MAX_DAMAGE);
        } else {
            patchBuilder.set(DataComponents.MAX_DAMAGE, val);
        }

        val = getValueAndRemove(container, OldDamageKey);
        if (val == null) { //restore minecraft:damage
            patchBuilder.remove(DataComponents.DAMAGE);
        } else {
            patchBuilder.set(DataComponents.DAMAGE, val);
        }

        itemStack.setItemMeta(meta);

        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.unwrap(itemStack);
        nmsItemStack.applyComponents(patchBuilder.build());
        itemStack = nmsItemStack.asBukkitMirror();

        return itemStack;
    }
}

