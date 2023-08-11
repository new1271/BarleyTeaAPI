package org.ricetea.barleyteaapi.api.item.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCustomDurability;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.internal.bridge.ExcellentEnchantsBridge;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class DefaultItemRenderer extends AbstractItemRenderer {

    public DefaultItemRenderer() {
        super(NamespacedKeyUtils.BarleyTeaAPI("default_item_renderer"));
    }

    @SuppressWarnings({ "unchecked", "null" })
    @Override
    public void renderItem(@Nonnull ItemStack itemStack) {
        BarleyTeaAPI apiInstance = BarleyTeaAPI.getInstance();
        if (apiInstance != null) {
            NamespacedKey id = BaseItem.getItemID(itemStack);
            if (id != null) {
                BaseItem itemType = ItemRegister.getInstance().lookupItemType(id);
                ItemMeta meta = itemStack.getItemMeta();
                List<Component> customLores = getItemLore(itemStack);
                boolean isTool = isToolOrWeapon(itemStack);
                double toolDamage = 1;
                double toolSpeed = 4.0;
                List<Component> lores = new ArrayList<Component>();
                if (meta.hasEnchants() && !hasItemFlag(itemStack, ItemFlag.HIDE_ENCHANTS)) {
                    final Entry<Enchantment, Integer>[] enchantments = (Entry<Enchantment, Integer>[]) meta
                            .getEnchants()
                            .entrySet().toArray(Entry<?, ?>[]::new);
                    int length = enchantments.length;
                    boolean hasExcellentEnchants = apiInstance.hasExcellentEnchants;
                    for (int i = length - 1; i >= 0; i--) {
                        Entry<Enchantment, Integer> enchantmentKVPair = enchantments[i];
                        final Enchantment enchantment = enchantmentKVPair.getKey();
                        final NamespacedKey key = enchantment.getKey();
                        final String enchantName = "enchantment." + key.getNamespace() + "." + key.getKey();
                        int level = enchantmentKVPair.getValue();
                        if (enchantment.equals(Enchantment.DAMAGE_ALL)) {
                            if (level > 0)
                                toolDamage += (1 + (0.5) * (level - 1));
                        }
                        boolean isExcellentEnchant = hasExcellentEnchants
                                && ExcellentEnchantsBridge.isExcellentEnchant(enchantment);
                        TranslatableComponent component;
                        TextColor color;
                        if (isExcellentEnchant) {
                            component = Component.translatable(enchantName,
                                    ExcellentEnchantsBridge.getEnchantmentNameUnsafe(enchantment));
                            color = ExcellentEnchantsBridge.getEnchantmentTierColorUnsafe(enchantment);
                        } else {
                            component = Component.translatable(enchantName);
                            color = enchantment.isCursed() ? NamedTextColor.RED : NamedTextColor.GRAY;
                        }
                        component = component.color(color).decoration(TextDecoration.ITALIC, false);
                        if (level != 1 || enchantment.getMaxLevel() != 1) {
                            Component levelComponent;
                            levelComponent = Component.translatable("enchantment.level." + level,
                                    Integer.toString(level));
                            component = component.append(Component.text(" ").append(levelComponent)
                                    .color(color).decoration(TextDecoration.ITALIC, false));
                        }
                        lores.add(component);
                    }
                }
                if (customLores != null) {
                    lores.addAll(customLores);
                }
                lores.add(Component.empty());
                ArrayList<Component> MainHandLore = (isTool ? new ArrayList<>() : null);
                if (meta.hasAttributeModifiers()) {
                    ArrayList<Component> AttributeLore = new ArrayList<>();
                    HashMap<EquipmentSlot, HashMap<Attribute, AttributeModifier>> map = new HashMap<EquipmentSlot, HashMap<Attribute, AttributeModifier>>();
                    for (Entry<Attribute, AttributeModifier> entry : meta.getAttributeModifiers().entries()) {
                        if (entry.getValue().getAmount() == 0)
                            continue;
                        HashMap<Attribute, AttributeModifier> hmap = map.getOrDefault(entry.getValue().getSlot(),
                                new HashMap<Attribute, AttributeModifier>());
                        if (isTool && entry.getValue().getSlot().equals(EquipmentSlot.HAND)) {
                            if (entry.getValue().getOperation().equals(AttributeModifier.Operation.ADD_NUMBER)) {
                                switch (entry.getKey()) {
                                    case GENERIC_ATTACK_DAMAGE:
                                        toolDamage += entry.getValue().getAmount();
                                        continue;
                                    case GENERIC_ATTACK_SPEED:
                                        toolSpeed += entry.getValue().getAmount();
                                        continue;
                                    default:
                                        break;
                                }
                            }
                        }
                        hmap.put(entry.getKey(), entry.getValue());
                        map.put(entry.getValue().getSlot(), hmap);
                    }
                    for (Entry<EquipmentSlot, HashMap<Attribute, AttributeModifier>> entry : map.entrySet()) {
                        String slot;
                        boolean isMainHand = false;
                        switch (entry.getKey()) {
                            case CHEST:
                                slot = "item.modifiers.chest";
                                break;
                            case FEET:
                                slot = "item.modifiers.feet";
                                break;
                            case HAND:
                                slot = "item.modifiers.mainhand";
                                isMainHand = isTool;
                                break;
                            case HEAD:
                                slot = "item.modifiers.head";
                                break;
                            case LEGS:
                                slot = "item.modifiers.legs";
                                break;
                            case OFF_HAND:
                                slot = "item.modifiers.offhand";
                                break;
                            default:
                                continue;
                        }
                        if (!isMainHand)
                            AttributeLore.add(Component.translatable(slot).color(NamedTextColor.GRAY)
                                    .decoration(TextDecoration.ITALIC, false));
                        Object[] entries = entry.getValue().entrySet().toArray();
                        for (int i = 0; i < entries.length; i++) {
                            Entry<Attribute, AttributeModifier> entry2 = (Entry<Attribute, AttributeModifier>) entries[i];
                            final NamespacedKey key = entry2.getKey().getKey();
                            final String attributeKey = "attribute.name." + key.getKey();
                            double value = entry2.getValue().getAmount();
                            String format = "attribute.modifier." + (value >= 0 ? "plus." : "take.")
                                    + entry2.getValue().getOperation().ordinal();
                            if (!entry2.getValue().getOperation().equals(AttributeModifier.Operation.ADD_NUMBER))
                                value *= 100;
                            else if (entry2.getKey().equals(Attribute.GENERIC_KNOCKBACK_RESISTANCE)) {
                                value *= 10;
                            }
                            value = Math.round(value * 100.0) / 100.0;
                            String valueString = Double.toString(value);
                            if (valueString.endsWith(".0"))
                                valueString = valueString.substring(0, valueString.length() - 2);
                            AttributeLore.add(Component.translatable(format).args(Component.text(valueString),
                                    Component.translatable(attributeKey))
                                    .color(value > 0 ? NamedTextColor.BLUE : NamedTextColor.RED)
                                    .decoration(TextDecoration.ITALIC, false));
                        }
                    }
                    lores.addAll(AttributeLore);
                }
                if (isTool) {
                    toolDamage = Math.round(toolDamage * 100.0) / 100.0;
                    String toolDamageString = Double.toString(toolDamage);
                    if (toolDamageString.endsWith(".0"))
                        toolDamageString = toolDamageString.substring(0, toolDamageString.length() - 2);
                    toolSpeed = Math.round(toolSpeed * 100.0) / 100.0;
                    String toolSpeedString = Double.toString(toolSpeed);
                    if (toolSpeedString.endsWith(".0"))
                        toolSpeedString = toolSpeedString.substring(0, toolSpeedString.length() - 2);
                    MainHandLore.add(0, Component.text(" ").append(
                            Component.translatable("attribute.modifier.equals.0").args(
                                    Component.text(toolDamageString),
                                    Component.translatable("attribute.name.generic.attack_damage")))
                            .color(NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false));
                    MainHandLore.add(1, Component.text(" ").append(
                            Component.translatable("attribute.modifier.equals.0").args(
                                    Component.text(toolSpeedString),
                                    Component.translatable("attribute.name.generic.attack_speed")))
                            .color(NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false));
                }
                if (isTool) {
                    lores.add(Component.translatable("item.modifiers.mainhand", NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false));
                    lores.addAll(MainHandLore);
                }
                if (itemType instanceof FeatureCustomDurability) {
                    FeatureCustomDurability customDurability = (FeatureCustomDurability) itemType;
                    int maxDura = customDurability.getMaxDurability(itemStack);
                    int dura = maxDura - customDurability.getDurabilityDamage(itemStack);
                    if (dura < maxDura) {
                        lores.add(Component.translatable("item.durability", NamedTextColor.WHITE)
                                .args(Component.text(dura), Component.text(maxDura))
                                .decoration(TextDecoration.ITALIC, false));
                    }
                }
                lores.add(Component.text(id.toString(), NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false));
                meta.lore(lores);
                itemStack.setItemMeta(meta);
                setLastRenderer(itemStack, this);
            }
        }
    }

    private static boolean isToolOrWeapon(ItemStack item) {
        Material type = item.getType();
        switch (type) {
            case WOODEN_SWORD:
            case STONE_SWORD:
            case GOLDEN_SWORD:
            case IRON_SWORD:
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
            case TRIDENT:
            case WOODEN_AXE:
            case STONE_AXE:
            case GOLDEN_AXE:
            case IRON_AXE:
            case DIAMOND_AXE:
            case NETHERITE_AXE:
            case WOODEN_PICKAXE:
            case STONE_PICKAXE:
            case GOLDEN_PICKAXE:
            case IRON_PICKAXE:
            case DIAMOND_PICKAXE:
            case NETHERITE_PICKAXE:
            case WOODEN_SHOVEL:
            case STONE_SHOVEL:
            case GOLDEN_SHOVEL:
            case IRON_SHOVEL:
            case DIAMOND_SHOVEL:
            case NETHERITE_SHOVEL:
            case WOODEN_HOE:
            case STONE_HOE:
            case GOLDEN_HOE:
            case IRON_HOE:
            case DIAMOND_HOE:
            case NETHERITE_HOE:
                return true;
            default:
                return false;
        }
    }
}
