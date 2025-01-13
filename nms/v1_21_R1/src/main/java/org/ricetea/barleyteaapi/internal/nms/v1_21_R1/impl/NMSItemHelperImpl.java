package org.ricetea.barleyteaapi.internal.nms.v1_21_R1.impl;

import com.google.common.collect.Multimap;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.GsonDataComponentValue;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.internal.nms.INMSItemHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_21_R1.helper.NBTItemHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_21_R1.helper.NMSItemHelper;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;
import org.ricetea.utils.SoftCache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class NMSItemHelperImpl implements INMSItemHelper {
    private static final Lazy<NMSItemHelperImpl> _inst = Lazy.create(NMSItemHelperImpl::new);
    private final ThreadLocal<SoftCache<StringBuilder>> localBuilder = ThreadLocal.withInitial(() ->
            SoftCache.create(StringBuilder::new));

    private NMSItemHelperImpl() {
    }

    @Nonnull
    public static NMSItemHelperImpl getInstance() {
        return _inst.get();
    }

    @Nullable
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@Nullable Material material) {
        return NMSItemHelper.getDefaultAttributeModifiers(material);
    }


    @Nullable
    @Override
    public ItemStack createItemStackFromShowItem(@Nonnull HoverEvent.ShowItem showItem) {
        Key key = showItem.item();
        Item item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(key.namespace(), key.value()))
                .orElse(null);
        if (item == null)
            return null;
        if (item instanceof AirItem)
            return new ItemStack(Material.AIR);
        net.minecraft.world.item.ItemStack nmsItemStack = new net.minecraft.world.item.ItemStack(item, showItem.count());
        String nbt = ObjectUtil.safeMap(showItem.dataComponents(), this::serializeDataComponents);
        if (nbt != null) {
            try {
                ObjectUtil.safeCall(NBTItemHelper.toComponentMap(TagParser.parseTag(nbt)), nmsItemStack::applyComponents);
            } catch (Exception ignored) {
            }
        }
        return nmsItemStack.asBukkitMirror();
    }

    @Nullable
    private String serializeDataComponents(@Nullable Map<Key, DataComponentValue> map) {
        if (map == null || map.isEmpty())
            return null;
        StringBuilder builder = localBuilder.get().get();
        builder.append("{");
        for (var entry : map.entrySet()) {
            DataComponentValue value = entry.getValue();
            switch (value) {
                case BinaryTagHolder holder -> {
                    builder.append('"');
                    builder.append(entry.getKey());
                    builder.append('"');
                    builder.append(':');
                    builder.append(holder.string());
                    builder.append(',');
                }
                case DataComponentValue.TagSerializable serializable -> {
                    builder.append('"');
                    builder.append(entry.getKey());
                    builder.append('"');
                    builder.append(':');
                    builder.append(serializable.asBinaryTag().string());
                    builder.append(',');
                }
                case GsonDataComponentValue gson -> {
                    builder.append('"');
                    builder.append(entry.getKey());
                    builder.append('"');
                    builder.append(':');
                    builder.append(gson.element());
                    builder.append(',');
                }
                default -> {
                }
            }
        }
        builder.append("}");
        String nbt = builder.toString();
        builder.setLength(0);
        return nbt;
    }

    @Nullable
    @Override
    public String getNMSEquipmentSlotName(@Nullable EquipmentSlot slot) {
        if (slot == null)
            return null;
        return CraftEquipmentSlot.getNMS(slot).getName();
    }

    @Override
    public boolean materialIsTool(@Nonnull Material material) {
        return switch (material) {
            case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD,
                    DIAMOND_SWORD, NETHERITE_SWORD, TRIDENT, WOODEN_AXE,
                    STONE_AXE, GOLDEN_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE,
                    WOODEN_PICKAXE, STONE_PICKAXE, GOLDEN_PICKAXE, IRON_PICKAXE,
                    DIAMOND_PICKAXE, NETHERITE_PICKAXE, WOODEN_SHOVEL, STONE_SHOVEL,
                    GOLDEN_SHOVEL, IRON_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL,
                    WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE,
                    MACE -> true;
            default -> false;
        };
    }

    @Override
    public boolean isSuitableForEntityType(@Nonnull EquipmentSlot slot, @Nonnull EntityType entityType) {
        return switch (slot) {
            case HAND, OFF_HAND, HEAD, CHEST, LEGS, FEET -> true;
            case BODY -> switch (entityType) {
                case HORSE, SKELETON_HORSE, ZOMBIE_HORSE, MULE, DONKEY,
                        WOLF -> true;
                default -> false;
            };
            default -> false;
        };
    }

    public void addItemFlags2(@Nonnull ItemStack itemStack, @Nullable ItemFlag... flags) {
        if (flags == null)
            return;
        for (ItemFlag flag : flags) {
            if (flag == null)
                continue;
            addItemFlag(itemStack, flag);
        }
    }

    public void removeItemFlags2(@Nonnull ItemStack itemStack, @Nullable ItemFlag... flags) {
        if (flags == null)
            return;
        for (ItemFlag flag : flags) {
            if (flag == null)
                continue;
            removeItemFlag(itemStack, flag);
        }
    }

    @Nonnull
    public Set<ItemFlag> getItemFlags2(@Nonnull ItemStack itemStack) {
        Lazy<Set<ItemFlag>> resultLazy = Lazy.create(() -> EnumSet.noneOf(ItemFlag.class));
        for (ItemFlag flag : ItemFlag.values()) {
            if (hasItemFlag2(itemStack, flag))
                resultLazy.get().add(flag);
        }
        return ObjectUtil.letNonNull(
                ObjectUtil.safeMap(resultLazy.getUnsafe(), Collections::unmodifiableSet),
                Collections::emptySet
        );
    }

    public boolean hasItemFlag2(@Nonnull ItemStack itemStack, @Nonnull ItemFlag flag) {
        if (!(itemStack instanceof CraftItemStack craftItemStack))
            return false;
        DataComponentType<?> type = switch (flag) {
            case HIDE_ATTRIBUTES -> DataComponents.ATTRIBUTE_MODIFIERS;
            case HIDE_ENCHANTS -> DataComponents.ENCHANTMENTS;
            case HIDE_STORED_ENCHANTS -> DataComponents.STORED_ENCHANTMENTS;
            case HIDE_UNBREAKABLE -> DataComponents.UNBREAKABLE;
            case HIDE_PLACED_ON -> DataComponents.CAN_PLACE_ON;
            case HIDE_DESTROYS -> DataComponents.CAN_BREAK;
            case HIDE_ARMOR_TRIM -> DataComponents.TRIM;
            case HIDE_DYE -> DataComponents.DYED_COLOR;
            case HIDE_ADDITIONAL_TOOLTIP -> DataComponents.HIDE_ADDITIONAL_TOOLTIP;
            default -> null;
        };
        if (type == null)
            return false;
        net.minecraft.world.item.ItemStack nmsItemStack = craftItemStack.handle;
        DataComponentPatch patchMap = nmsItemStack.getComponentsPatch();
        Optional<?> optional = patchMap.get(type);
        return switch (flag) {
            case HIDE_ATTRIBUTES -> {
                ItemAttributeModifiers oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        ItemAttributeModifiers.class
                );
                yield oldObj != null && !oldObj.showInTooltip();
            }
            case HIDE_ENCHANTS, HIDE_STORED_ENCHANTS -> {
                ItemEnchantments oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        ItemEnchantments.class
                );
                yield oldObj != null && !oldObj.showInTooltip;
            }
            case HIDE_UNBREAKABLE -> {
                Unbreakable oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        Unbreakable.class
                );
                yield oldObj != null && !oldObj.showInTooltip();
            }
            case HIDE_PLACED_ON, HIDE_DESTROYS -> {
                AdventureModePredicate oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        AdventureModePredicate.class
                );
                yield oldObj != null && !oldObj.showInTooltip();
            }
            case HIDE_ARMOR_TRIM -> {
                ArmorTrim oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        ArmorTrim.class
                );
                yield oldObj != null && !oldObj.showInTooltip;
            }
            case HIDE_DYE -> {
                DyedItemColor oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        DyedItemColor.class
                );
                yield oldObj != null && !oldObj.showInTooltip();
            }
            case HIDE_ADDITIONAL_TOOLTIP -> {
                Unit oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        Unit.class
                );
                yield oldObj == null;
            }
            default -> false;
        };
    }

    private void addItemFlag(@Nonnull ItemStack itemStack, @Nonnull ItemFlag flag) {
        if (!(itemStack instanceof CraftItemStack craftItemStack))
            return;
        DataComponentType<?> type = switch (flag) {
            case HIDE_ATTRIBUTES -> DataComponents.ATTRIBUTE_MODIFIERS;
            case HIDE_ENCHANTS -> DataComponents.ENCHANTMENTS;
            case HIDE_STORED_ENCHANTS -> DataComponents.STORED_ENCHANTMENTS;
            case HIDE_UNBREAKABLE -> DataComponents.UNBREAKABLE;
            case HIDE_PLACED_ON -> DataComponents.CAN_PLACE_ON;
            case HIDE_DESTROYS -> DataComponents.CAN_BREAK;
            case HIDE_ARMOR_TRIM -> DataComponents.TRIM;
            case HIDE_DYE -> DataComponents.DYED_COLOR;
            case HIDE_ADDITIONAL_TOOLTIP -> DataComponents.HIDE_ADDITIONAL_TOOLTIP;
            default -> null;
        };
        if (type == null)
            return;
        net.minecraft.world.item.ItemStack nmsItemStack = craftItemStack.handle;
        DataComponentPatch patchMap = nmsItemStack.getComponentsPatch();
        Optional<?> optional = patchMap.get(type);
        Object newObj = switch (flag) {
            case HIDE_ATTRIBUTES -> {
                ItemAttributeModifiers oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        ItemAttributeModifiers.class
                );
                if (oldObj == null)
                    yield ItemAttributeModifiers.EMPTY.withTooltip(false);
                if (oldObj.showInTooltip())
                    yield oldObj.withTooltip(false);
                yield null;
            }
            case HIDE_ENCHANTS, HIDE_STORED_ENCHANTS -> {
                ItemEnchantments oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        ItemEnchantments.class
                );
                if (oldObj == null)
                    yield ItemEnchantments.EMPTY.withTooltip(false);
                if (oldObj.showInTooltip)
                    yield oldObj.withTooltip(false);
                yield null;
            }
            case HIDE_UNBREAKABLE -> {
                Unbreakable oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        Unbreakable.class
                );
                if (oldObj == null || !oldObj.showInTooltip())
                    yield null;
                yield oldObj.withTooltip(false);
            }
            case HIDE_PLACED_ON, HIDE_DESTROYS -> {
                AdventureModePredicate oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        AdventureModePredicate.class
                );
                if (oldObj == null || !oldObj.showInTooltip())
                    yield null;
                yield oldObj.withTooltip(false);
            }
            case HIDE_ARMOR_TRIM -> {
                ArmorTrim oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        ArmorTrim.class
                );
                if (oldObj == null || !oldObj.showInTooltip)
                    yield null;
                yield oldObj.withTooltip(false);
            }
            case HIDE_DYE -> {
                DyedItemColor oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        DyedItemColor.class
                );
                if (oldObj == null || !oldObj.showInTooltip())
                    yield null;
                yield oldObj.withTooltip(false);
            }
            case HIDE_ADDITIONAL_TOOLTIP -> {
                Unit oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        Unit.class
                );
                yield oldObj == null ? Unit.INSTANCE : null;
            }
            default -> null;
        };
        if (newObj != null) {
            DataComponentPatch.Builder builder = DataComponentPatch
                    .builder()
                    .set(TypedDataComponent.createUnchecked(type, newObj));
            nmsItemStack.applyComponents(builder.build());
        }
    }

    private void removeItemFlag(@Nonnull ItemStack itemStack, @Nonnull ItemFlag flag) {
        if (!(itemStack instanceof CraftItemStack craftItemStack))
            return;
        DataComponentType<?> type = switch (flag) {
            case HIDE_ATTRIBUTES -> DataComponents.ATTRIBUTE_MODIFIERS;
            case HIDE_ENCHANTS -> DataComponents.ENCHANTMENTS;
            case HIDE_STORED_ENCHANTS -> DataComponents.STORED_ENCHANTMENTS;
            case HIDE_UNBREAKABLE -> DataComponents.UNBREAKABLE;
            case HIDE_PLACED_ON -> DataComponents.CAN_PLACE_ON;
            case HIDE_DESTROYS -> DataComponents.CAN_BREAK;
            case HIDE_ARMOR_TRIM -> DataComponents.TRIM;
            case HIDE_DYE -> DataComponents.DYED_COLOR;
            case HIDE_ADDITIONAL_TOOLTIP -> DataComponents.HIDE_ADDITIONAL_TOOLTIP;
            default -> null;
        };
        if (type == null)
            return;
        net.minecraft.world.item.ItemStack nmsItemStack = craftItemStack.handle;
        DataComponentPatch patchMap = nmsItemStack.getComponentsPatch();
        Optional<?> optional = patchMap.get(type);
        boolean removed = false;
        Object newObj = switch (flag) {
            case HIDE_ATTRIBUTES -> {
                ItemAttributeModifiers oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        ItemAttributeModifiers.class
                );
                if (oldObj != null) {
                    if (oldObj.modifiers().isEmpty())
                        removed = true;
                    else if (!oldObj.showInTooltip())
                        yield oldObj.withTooltip(true);
                }
                yield null;
            }
            case HIDE_ENCHANTS, HIDE_STORED_ENCHANTS -> {
                ItemEnchantments oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        ItemEnchantments.class
                );
                if (oldObj != null) {
                    if (oldObj.entrySet().isEmpty())
                        removed = true;
                    else if (!oldObj.showInTooltip)
                        yield oldObj.withTooltip(true);
                }
                yield null;
            }
            case HIDE_UNBREAKABLE -> {
                Unbreakable oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        Unbreakable.class
                );
                if (oldObj != null && !oldObj.showInTooltip())
                    yield oldObj.withTooltip(true);
                yield null;
            }
            case HIDE_PLACED_ON, HIDE_DESTROYS -> {
                AdventureModePredicate oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        AdventureModePredicate.class
                );
                if (oldObj != null && !oldObj.showInTooltip())
                    yield oldObj.withTooltip(true);
                yield null;
            }
            case HIDE_ARMOR_TRIM -> {
                ArmorTrim oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        ArmorTrim.class
                );
                if (oldObj != null && !oldObj.showInTooltip)
                    yield oldObj.withTooltip(true);
                yield null;
            }
            case HIDE_DYE -> {
                DyedItemColor oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        DyedItemColor.class
                );
                if (oldObj != null && !oldObj.showInTooltip())
                    yield oldObj.withTooltip(true);
                yield null;
            }
            case HIDE_ADDITIONAL_TOOLTIP -> {
                Unit oldObj = ObjectUtil.tryCast(
                        ObjectUtil.safeMap(optional, val -> val.orElse(null)),
                        Unit.class
                );
                if (oldObj != null)
                    removed = true;
                yield null;
            }
            default -> null;
        };
        if (newObj != null || removed) {
            DataComponentPatch.Builder builder = DataComponentPatch.builder();
            if (newObj == null)
                builder.remove(type);
            else
                builder.set(TypedDataComponent.createUnchecked(type, newObj));
            nmsItemStack.applyComponents(builder.build());
        }
    }
}