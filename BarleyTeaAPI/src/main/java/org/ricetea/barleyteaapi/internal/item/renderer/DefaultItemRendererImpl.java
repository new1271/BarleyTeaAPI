package org.ricetea.barleyteaapi.internal.item.renderer;

import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemRarity;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurabilityExtra;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemDisplay;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemDisplay;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.api.item.render.ItemRendererRegister;
import org.ricetea.barleyteaapi.internal.bridge.ExcellentEnchantsBridge;
import org.ricetea.barleyteaapi.internal.item.renderer.util.AlternativeItemState;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Box;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class DefaultItemRendererImpl extends AbstractItemRendererImpl {
    private static final @Nonnull Lazy<DefaultItemRendererImpl> _inst = Lazy.create(DefaultItemRendererImpl::new);
    private static final @Nonnull EquipmentSlot[] slots = EquipmentSlot.values();
    private static final @Nonnull Operation[] operations = Operation.values();
    private static final @Nonnull Comparator<Attribute> attributeComparator = (a, b) -> {
        String keyA = a.getKey().getKey();
        String keyB = b.getKey().getKey();
        return keyA.compareTo(keyB);
    };
    private static final double DEFAULT_TOOL_DAMAGE = 1.0;
    private static final double DEFAULT_TOOL_SPEED = 4.0;

    private DefaultItemRendererImpl() {
        super(NamespacedKeyUtil.BarleyTeaAPI("default_item_renderer"));
    }

    @Nonnull
    public static DefaultItemRendererImpl getInstance() {
        return _inst.get();
    }

    public void checkIsRegistered() {
        if (!isRegistered())
            ItemRendererRegister.getInstance().register(this);
    }

    @Override
    @Nonnull
    public ItemStack render(@Nonnull ItemStack itemStack, @Nullable Player player) {

        BarleyTeaAPI apiInstance = BarleyTeaAPI.getInstanceUnsafe();
        ItemRegister register = ItemRegister.getInstanceUnsafe();

        if (apiInstance == null || register == null)
            return itemStack;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return itemStack;

        AlternativeItemState.restore(meta);
        AlternativeItemState.store(meta);

        List<Component> itemLore = meta.lore();
        List<Component> renderLore = new ArrayList<>();

        DataItemType itemType = DataItemType.get(itemStack);

        boolean isTool = itemType.nonNullMap(ItemHelper::materialIsTool, BaseItem::isTool);

        double toolDamage, toolSpeed;
        if (isTool) {
            if (player == null) {
                toolDamage = DEFAULT_TOOL_DAMAGE;
                toolSpeed = DEFAULT_TOOL_SPEED;
            } else {
                AttributeInstance attributeInstance = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                toolDamage = attributeInstance == null ? DEFAULT_TOOL_DAMAGE : attributeInstance.getBaseValue();
                attributeInstance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                toolSpeed = attributeInstance == null ? DEFAULT_TOOL_SPEED : attributeInstance.getBaseValue();
            }
        } else {
            toolDamage = 0;
            toolSpeed = 0;
        }

        if (meta.hasEnchants() && !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
            boolean hasExcellentEnchants = apiInstance.hasExcellentEnchants;
            final List<Component> finalRenderLore = renderLore;
            final Box<Double> toolDamageIncreaseBox = Box.box(0.0);
            meta.getEnchants().forEach((enchantment, boxedLevel) -> {
                if (boxedLevel == null)
                    return;
                final int level = boxedLevel;
                final NamespacedKey key = enchantment.getKey();
                final String enchantName = "enchantment." + key.getNamespace() + "." + key.getKey();
                if (isTool && enchantment.equals(Enchantment.DAMAGE_ALL)) {
                    if (level > 0) {
                        double value = ObjectUtil.letNonNull(toolDamageIncreaseBox.get(), 0.0);
                        value += (1 + (0.5) * (level - 1));
                        toolDamageIncreaseBox.set(value);
                    }
                }
                boolean isExcellentEnchant = hasExcellentEnchants
                        && ExcellentEnchantsBridge.isExcellentEnchant(enchantment);
                TranslatableComponent component;
                TextColor color;
                if (isExcellentEnchant) {
                    component = Component.translatable(enchantName,
                            ExcellentEnchantsBridge.getEnchantmentNameUnsafe(enchantment));
                    color = ObjectUtil.letNonNull(
                            ExcellentEnchantsBridge.getEnchantmentTierColorUnsafe(enchantment),
                            NamedTextColor.GRAY);
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
                finalRenderLore.add(component);
            });
            toolDamage += ObjectUtil.letNonNull(toolDamageIncreaseBox.get(), 0.0);
        }

        if (itemLore != null) {
            renderLore.addAll(itemLore);
        }

        renderLore.add(Component.empty());

        if (!meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
            ArrayList<Component> toolAttributeLore = (isTool ? new ArrayList<>() : null);
            Multimap<Attribute, AttributeModifier> attributeMap = meta.getAttributeModifiers();
            if (attributeMap == null)
                attributeMap = ItemHelper.getDefaultAttributeModifiers(itemStack);
            if (!attributeMap.isEmpty()) {
                ArrayList<Component> slotAttributeLore = new ArrayList<>();
                HashMap<EquipmentSlot, TreeMap<Attribute, double[]>> slotAttributeMap = new HashMap<>();
                final Box<Double> toolDamageIncreaseBox = Box.box(0.0);
                final Box<Double> toolSpeedIncreaseBox = Box.box(0.0);
                attributeMap.asMap().forEach((attribute, modifiers) -> {
                    if (attribute == null)
                        return;
                    Box<Double> increaseBox = switch (attribute) {
                        case GENERIC_ATTACK_DAMAGE -> toolDamageIncreaseBox;
                        case GENERIC_ATTACK_SPEED -> toolSpeedIncreaseBox;
                        default -> null;
                    };
                    modifiers.forEach(modifier -> {
                        double amount = modifier.getAmount();
                        Operation operation = modifier.getOperation();
                        if (amount == 0)
                            return;
                        EquipmentSlot slot = modifier.getSlot();
                        if (isTool && (slot == null || slot.equals(EquipmentSlot.HAND))) {
                            if (operation.equals(AttributeModifier.Operation.ADD_NUMBER)) {
                                if (increaseBox != null) {
                                    increaseBox.set(
                                            ObjectUtil.letNonNull(increaseBox.get(), 0.0) + amount);
                                    return;
                                }
                            }
                        }
                        if (slot == null) {
                            for (EquipmentSlot iteratedSlot : slots) {
                                if (iteratedSlot == null)
                                    continue;
                                TreeMap<Attribute, double[]> hmap = slotAttributeMap.computeIfAbsent(
                                        iteratedSlot,
                                        ignored -> new TreeMap<>(attributeComparator));
                                double[] values = hmap.computeIfAbsent(attribute,
                                        ignored -> new double[operations.length]);
                                values[operation.ordinal()] += amount;
                            }
                        } else {
                            TreeMap<Attribute, double[]> hmap = slotAttributeMap.computeIfAbsent(slot,
                                    ignored -> new TreeMap<>(attributeComparator));
                            double[] values = hmap.computeIfAbsent(attribute,
                                    ignored -> new double[operations.length]);
                            values[operation.ordinal()] += amount;
                        }
                    });
                });
                toolDamage += ObjectUtil.letNonNull(toolDamageIncreaseBox.get(), 0.0);
                toolSpeed += ObjectUtil.letNonNull(toolSpeedIncreaseBox.get(), 0.0);

                for (EquipmentSlot slot : slots) {
                    if (slot == null)
                        continue;
                    TreeMap<Attribute, double[]> attributeOperationMap = slotAttributeMap.get(slot);
                    if (attributeOperationMap == null)
                        continue;
                    String slotStringKey = switch (slot) {
                        case CHEST -> "item.modifiers.chest";
                        case FEET -> "item.modifiers.feet";
                        case HAND -> "item.modifiers.mainhand";
                        case HEAD -> "item.modifiers.head";
                        case LEGS -> "item.modifiers.legs";
                        case OFF_HAND -> "item.modifiers.offhand";
                        default -> "item.modifiers." +
                                slot.toString().toLowerCase().replace("_", "");
                    };
                    slotAttributeLore.add(Component.translatable(slotStringKey)
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false));
                    attributeOperationMap.forEach((attribute, operationValues) -> {
                        final String attributeTranslateKey = "attribute.name." + attribute.getKey().getKey();
                        for (Operation operation : operations) {
                            if (operation != null) {
                                int operationOridinal = operation.ordinal();
                                double value = operationValues[operationOridinal];
                                if (value == 0)
                                    continue;
                                String format = "attribute.modifier." + (value >= 0 ? "plus." : "take.")
                                        + operationOridinal;
                                if (!operation.equals(AttributeModifier.Operation.ADD_NUMBER))
                                    value *= 100;
                                else if (attribute.equals(Attribute.GENERIC_KNOCKBACK_RESISTANCE)) {
                                    value *= 10;
                                }
                                value = Math.round(value * 100.0) / 100.0;
                                slotAttributeLore.add(Component.translatable(format)
                                        .args(Component.text(value),
                                                Component.translatable(attributeTranslateKey))
                                        .color(value > 0 ? NamedTextColor.BLUE : NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false));
                            }
                        }
                    });
                    slotAttributeLore.add(Component.empty());
                }
                renderLore.addAll(slotAttributeLore);
                if (!isTool)
                    renderLore.remove(renderLore.size() - 1);
            }
            if (toolAttributeLore != null) {
                toolDamage = Math.round(toolDamage * 100.0) / 100.0;
                String toolDamageString = Double.toString(toolDamage);
                if (toolDamageString.endsWith(".0"))
                    toolDamageString = toolDamageString.substring(0, toolDamageString.length() - 2);
                toolSpeed = Math.round(toolSpeed * 100.0) / 100.0;
                String toolSpeedString = Double.toString(toolSpeed);
                if (toolSpeedString.endsWith(".0"))
                    toolSpeedString = toolSpeedString.substring(0, toolSpeedString.length() - 2);
                toolAttributeLore.add(Component.space().append(
                                Component.translatable("attribute.modifier.equals.0").args(
                                        Component.text(toolDamageString),
                                        Component.translatable("attribute.name.generic.attack_damage")))
                        .color(NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false));
                toolAttributeLore.add(Component.space().append(
                                Component.translatable("attribute.modifier.equals.0").args(
                                        Component.text(toolSpeedString),
                                        Component.translatable("attribute.name.generic.attack_speed")))
                        .color(NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false));
                renderLore.add(Component.translatable("item.modifiers.mainhand", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false));
                renderLore.addAll(toolAttributeLore);
            }
        }

        Component displayName = meta.displayName();
        BaseItem customItem = itemType.asCustomItem();
        if (customItem != null) {
            boolean isRenamed;
            if (displayName == null) {
                displayName = customItem.getDefaultNameComponent();
                isRenamed = false;
            } else {
                isRenamed = true;
            }

            DataItemRarity rarity = customItem.getRarity();
            if (customItem.isRarityUpgraded(itemStack)) {
                rarity = rarity.upgrade();
            }
            displayName = rarity.apply(displayName, isRenamed, false);

            if (customItem instanceof FeatureItemCustomDurability feature) {
                int maxDura = feature.getMaxDurability(itemStack);
                int dura = maxDura - feature.getDurabilityDamage(itemStack);
                if (dura < maxDura ||
                        feature instanceof FeatureItemCustomDurabilityExtra customDurabilityExtra &&
                                customDurabilityExtra.isAlwaysShowDurabilityHint(itemStack)) {
                    renderLore.add(Component.translatable("item.durability", NamedTextColor.WHITE)
                            .args(Component.text(dura), Component.text(maxDura))
                            .decoration(TextDecoration.ITALIC, false));
                }
            }
            renderLore.add(Component.text(customItem.getKey().toString(), NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false));

            if (customItem instanceof FeatureItemDisplay feature) {
                DataItemDisplay data = new DataItemDisplay(player, itemStack, displayName, renderLore);
                feature.handleItemDisplay(data);
                displayName = data.getDisplayName();
                renderLore = data.getLore();
            }
        }

        meta.displayName(displayName);
        meta.lore(renderLore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
