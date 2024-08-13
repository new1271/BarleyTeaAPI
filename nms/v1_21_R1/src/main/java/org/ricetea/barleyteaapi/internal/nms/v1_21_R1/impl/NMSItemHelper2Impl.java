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
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class NMSItemHelper2Impl implements INMSItemHelper2 {
    private static final Lazy<NMSItemHelper2Impl> _inst = Lazy.create(NMSItemHelper2Impl::new);
    private static final NamespacedKey OriginalItemDamageKey = NamespacedKeyUtil.BarleyTeaAPI("original_item_damage");
    private static final NamespacedKey CustomDurabilityBarRestorePhaseKey = NamespacedKeyUtil.BarleyTeaAPI("custom_durability_bar_restore_phase");
    private static final NamespacedKey OldMaxStackSizeKey = NamespacedKeyUtil.BarleyTeaAPI("old_max_stack_size");
    private static final NamespacedKey OldDamageKey = NamespacedKeyUtil.BarleyTeaAPI("old_damage");
    private static final NamespacedKey OldMaxDamageKey = NamespacedKeyUtil.BarleyTeaAPI("old_max_damage");

    private NMSItemHelper2Impl() {
    }

    @Nonnull
    public static NMSItemHelper2Impl getInstance() {
        return _inst.get();
    }

    private static void setValueOrRemove(@Nonnull PersistentDataContainer container,
                                         @Nonnull NamespacedKey namespacedKey, int val) {
        try {
            container.set(namespacedKey, PersistentDataType.INTEGER, val);
        } catch (Exception ignored) {
            ObjectUtil.tryCallSilently(namespacedKey, container::remove);
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
        ObjectUtil.tryCallSilently(namespacedKey, container::remove);
        return result;
    }

    @SuppressWarnings("SameParameterValue")
    private static int getValueAndRemove(@Nonnull PersistentDataContainer container,
                                         @Nonnull NamespacedKey namespacedKey, int defaultValue) {
        int result;
        try {
            result = container.getOrDefault(namespacedKey, PersistentDataType.INTEGER, defaultValue);
        } catch (Exception ignored) {
            result = defaultValue;
        }
        ObjectUtil.tryCallSilently(namespacedKey, container::remove);
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
        Lazy<DataComponentPatch.Builder> patchBuilderLazy = Lazy.create(DataComponentPatch::builder);

        boolean modifiedMaxStackSize = false, modifiedMaxDamage = false, modifiedDamage = false;

        Integer maxStackSizeRaw = map.get(DataComponents.MAX_STACK_SIZE);
        Integer maxDamageRaw = map.get(DataComponents.MAX_DAMAGE);
        Integer damageRaw = map.get(DataComponents.DAMAGE);

        if (maxStackSizeRaw == null || maxStackSizeRaw != 1) {
            patchBuilderLazy.get().set(DataComponents.MAX_STACK_SIZE, 1);
            modifiedMaxStackSize = true;
        }

        if (maxDamageRaw == null || maxDamageRaw != maxDurability) {
            patchBuilderLazy.get().set(DataComponents.MAX_DAMAGE, maxDurability);
            modifiedMaxDamage = true;
        }

        if (damageRaw == null || damageRaw != damage) {
            patchBuilderLazy.get().set(DataComponents.DAMAGE, damage);
            modifiedDamage = true;
        }

        DataComponentPatch.Builder patchBuilder = patchBuilderLazy.getUnsafe();

        if (patchBuilder == null)
            return itemStack;

        nmsItemStack.applyComponents(patchBuilder.build());

        itemStack = nmsItemStack.asBukkitMirror();

        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();

            int phase = 0b000;

            if (modifiedMaxStackSize) {
                if (maxStackSizeRaw == null)
                    phase |= 0b100;
                else {
                    setValueOrRemove(container, OldMaxStackSizeKey, maxStackSizeRaw);
                }
            }

            if (modifiedMaxDamage) {
                if (maxDamageRaw == null)
                    phase |= 0b010;
                else {
                    setValueOrRemove(container, OldMaxDamageKey, maxDamageRaw);
                }
            }

            if (modifiedDamage) {
                if (damageRaw == null)
                    phase |= 0b001;
                else {
                    setValueOrRemove(container, OldDamageKey, damageRaw);
                }
            }

            setValueOrRemove(container, CustomDurabilityBarRestorePhaseKey, phase);

            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    @Override
    public void restoreCustomDurabilityBar(@Nonnull Damageable itemMeta, int maxDurability) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        Integer damage;
        try {
            damage = container.get(OriginalItemDamageKey, PersistentDataType.INTEGER);
        } catch (Exception ignored) {
            return;
        }
        if (damage == null)
            return;
        itemMeta.setDamage(damage);
        itemMeta.setMaxDamage(maxDurability);
        ObjectUtil.tryCallSilently(OriginalItemDamageKey, container::remove);
    }

    @Override
    public boolean isNeedSpecialRestore(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        try {
            return meta.getPersistentDataContainer().getOrDefault(CustomDurabilityBarRestorePhaseKey,
                    PersistentDataType.INTEGER, 0) > 0;
        } catch (Exception ignored) {
            return false;
        }
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

    @Nonnull
    @Override
    public ItemStack restoreCustomDurabilityBarSpecial(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return itemStack;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        int restorePhase = getValueAndRemove(container,
                CustomDurabilityBarRestorePhaseKey, 0);
        if (restorePhase <= 0)
            return itemStack;

        Lazy<DataComponentPatch.Builder> patchBuilderLazy = Lazy.create(DataComponentPatch::builder);

        if ((restorePhase & 0b100) == 0) { //restore minecraft:max_stack_size
            Integer val = getValueAndRemove(container, OldMaxStackSizeKey);
            if (val != null) {
                patchBuilderLazy.get().set(DataComponents.MAX_STACK_SIZE, val);
            }
        } else {
            patchBuilderLazy.get().remove(DataComponents.MAX_STACK_SIZE);
        }

        if ((restorePhase & 0b010) == 0) { //restore minecraft:max_damage
            Integer val = getValueAndRemove(container, OldMaxDamageKey);
            if (val != null) {
                patchBuilderLazy.get().set(DataComponents.MAX_DAMAGE, val);
            }
        } else {
            patchBuilderLazy.get().remove(DataComponents.MAX_DAMAGE);
        }

        if ((restorePhase & 0b001) == 0) { //restore minecraft:damage
            Integer val = getValueAndRemove(container, OldDamageKey);
            if (val != null) {
                patchBuilderLazy.get().set(DataComponents.DAMAGE, val);
            }
        } else {
            patchBuilderLazy.get().remove(DataComponents.DAMAGE);
        }

        itemStack.setItemMeta(meta);

        DataComponentPatch.Builder patchBuilder = patchBuilderLazy.getUnsafe();

        if (patchBuilder == null)
            return itemStack;

        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.unwrap(itemStack);
        nmsItemStack.applyComponents(patchBuilder.build());
        itemStack = nmsItemStack.asBukkitMirror();

        return itemStack;
    }
}

