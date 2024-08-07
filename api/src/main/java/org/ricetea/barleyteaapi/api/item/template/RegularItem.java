package org.ricetea.barleyteaapi.api.item.template;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.item.CustomItemRarity;
import org.ricetea.barleyteaapi.api.item.VanillaItemRarity;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCommandGive;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemSlotFilter;
import org.ricetea.barleyteaapi.api.item.feature.data.DataCommandGive;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemSlotFilter;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;

public abstract class RegularItem extends DefaultItem
        implements FeatureCommandGive, FeatureItemGive, FeatureItemSlotFilter {

    @Nonnull
    private static final NamespacedKey ItemAlternateDamageNamespacedKey = NamespacedKeyUtil
            .BarleyTeaAPI("item_damage");
    @Nonnull
    private static final EquipmentSlot[] SpecialDefaultSlotsForDualWeldItem =
            new EquipmentSlot[]{
                    EquipmentSlot.HAND, EquipmentSlot.OFF_HAND
            };

    @Nullable
    private final EquipmentSlot[] defaultSlots;

    public RegularItem(@Nonnull NamespacedKey key, @Nonnull Material originalType, @Nonnull VanillaItemRarity rarity) {
        super(key, originalType, rarity.getRarity());
        defaultSlots = getDefaultSlots(originalType);
    }

    public RegularItem(@Nonnull NamespacedKey key, @Nonnull Material originalType, @Nonnull CustomItemRarity rarity) {
        super(key, originalType, rarity);
        defaultSlots = getDefaultSlots(originalType);
    }

    private static EquipmentSlot[] getDefaultSlots(@Nonnull Material originalType) {
        return switch (originalType) {
            case SHIELD, BOW -> SpecialDefaultSlotsForDualWeldItem;
            default -> (originalType.isBlock() || originalType.isInteractable() || originalType.isEdible()) ?
                    SpecialDefaultSlotsForDualWeldItem : null;
        };
    }

@Override
@Nullable
public ItemStack handleItemGive(int count) {
    if (count > 0) {
        ItemStack itemStack = new ItemStack(getOriginalType(), count);
        if (ItemHelper.tryRegister(this, itemStack, this::handleItemGive)) {
            return itemStack;
        }
    }
    return null;
}

@Override
public boolean handleCommandGive(@Nonnull DataCommandGive data) {
    ItemStack itemStack = data.getItemStack();
    return handleItemGive(itemStack);
}

@Override
public boolean handleItemSlotFilter(@Nonnull DataItemSlotFilter data) {
    EquipmentSlotComparator comparator = EquipmentSlotComparator.getInstance();
    EquipmentSlot equipmentSlot = data.getEquipmentSlot();
    EquipmentSlot[] defaultSlots = this.defaultSlots;
    int length = defaultSlots == null ? 0 : defaultSlots.length;
    return switch (length) {
        case 0 -> comparator.compare(equipmentSlot, getOriginalType().getEquipmentSlot()) == 0;
        case 1 -> comparator.compare(equipmentSlot, defaultSlots[0]) == 0;
        default -> {
            if (length < 4) {
                for (int i = 0; i < length; i++) {
                    if (comparator.compare(equipmentSlot, defaultSlots[i]) == 0)
                        yield true;
                }
                yield false;
            } else {
                int findingIndex = Arrays.binarySearch(defaultSlots, equipmentSlot, comparator);
                yield findingIndex >= 0 && findingIndex < length;
            }
        }
    };
}

protected abstract boolean handleItemGive(@Nonnull ItemStack itemStack);

    public int getDurabilityDamage(@Nonnull ItemStack itemStack) {
        return getDurabilityDamage0(itemStack);
    }

    public void setDurabilityDamage(@Nonnull ItemStack itemStack, int damage) {
        setDurabilityDamage0(itemStack, damage);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return;
        FeatureItemCustomDurability feature = this.getFeature(FeatureItemCustomDurability.class);
        if (feature != null) {
            if (meta instanceof Damageable damageable) {
                if (damage == 0) {
                    damageable.setDamage(0);
                } else {
                    int maxDura = feature.getMaxDurability(itemStack);
                    int maxDuraVisual = itemStack.getType().getMaxDurability();
                    if (damage == maxDura) {
                        damageable.setDamage(maxDuraVisual);
                    } else {
                        int visualDamage = (int) Math
                                .round(damage * 1.0 / maxDura * maxDuraVisual);
                        if (visualDamage <= 0 && damage > 0) {
                            visualDamage = 1;
                        } else if (visualDamage >= maxDuraVisual && damage < maxDura) {
                            visualDamage = maxDuraVisual - 1;
                        }
                        damageable.setDamage(visualDamage);
                    }
                }
                itemStack.setItemMeta(damageable);
            }
        }
    }

    protected int getDurabilityDamage0(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return 0;
        FeatureItemCustomDurability feature = this.getFeature(FeatureItemCustomDurability.class);
        if (feature != null)
            return meta.getPersistentDataContainer().getOrDefault(ItemAlternateDamageNamespacedKey,
                    PersistentDataType.INTEGER,
                    0);
        else if (meta instanceof Damageable damageable)
            return damageable.getDamage();
        return 0;
    }

    protected void setDurabilityDamage0(@Nonnull ItemStack itemStack, int damage) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return;
        FeatureItemCustomDurability feature = this.getFeature(FeatureItemCustomDurability.class);
        if (feature != null)
            meta.getPersistentDataContainer().set(ItemAlternateDamageNamespacedKey, PersistentDataType.INTEGER, damage);
        else if (meta instanceof Damageable damageable)
            damageable.setDamage(damage);
        itemStack.setItemMeta(meta);
    }

private static class EquipmentSlotComparator implements Comparator<EquipmentSlot> {

    private static final Lazy<EquipmentSlotComparator> _inst = Lazy.create(EquipmentSlotComparator::new);

    private EquipmentSlotComparator() {
    }

    @Nonnull
    public static EquipmentSlotComparator getInstance() {
        return _inst.get();
    }

    @Override
    public int compare(@Nullable EquipmentSlot o1, @Nullable EquipmentSlot o2) {
        if (o1 == null)
            o1 = EquipmentSlot.HAND;
        if (o2 == null)
            o2 = EquipmentSlot.HAND;
        return o1 == o2 ? 0 : (o1.ordinal() - o2.ordinal());
    }
    }
}
