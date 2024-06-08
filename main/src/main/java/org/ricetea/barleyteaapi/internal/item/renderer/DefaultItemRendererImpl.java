package org.ricetea.barleyteaapi.internal.item.renderer;

import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.helper.FeatureHelper;
import org.ricetea.barleyteaapi.api.internal.nms.INMSItemHelper;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.CustomItemRarity;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurabilityExtra;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemDisplay;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemDisplay;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.api.item.registration.ItemRendererRegister;
import org.ricetea.barleyteaapi.api.item.registration.ItemSubRendererRegister;
import org.ricetea.barleyteaapi.api.item.render.ItemSubRenderer;
import org.ricetea.barleyteaapi.api.item.render.ItemSubRendererSupportingState;
import org.ricetea.barleyteaapi.api.item.render.util.AlternativeItemState;
import org.ricetea.barleyteaapi.internal.connector.BulitInSoftDepend;
import org.ricetea.barleyteaapi.internal.connector.ExcellentEnchantsConnector;
import org.ricetea.barleyteaapi.internal.connector.GeyserConnector;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.barleyteaapi.util.connector.SoftDependConnector;
import org.ricetea.utils.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Supplier;

@Singleton
@ApiStatus.Internal
public class DefaultItemRendererImpl extends AbstractItemRendererImpl {
    private static final @Nonnull EquipmentSlot[] slots = EquipmentSlot.values();
    private static final @Nonnull Operation[] operations = Operation.values();
    private static final @Nonnull Comparator<Attribute> attributeComparator = (a, b) -> {
        String keyA = a.getKey().getKey();
        String keyB = b.getKey().getKey();
        return keyA.compareTo(keyB);
    };
    private static final @Nonnull Lazy<Style> defaultEnchantTextStyleLazy =
            Lazy.createThreadSafe(() -> Style.style(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false));
    private static final @Nonnull Lazy<Style> cursedEnchantTextStyleLazy =
            Lazy.createThreadSafe(() -> Style.style(NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false));
    private static final double DEFAULT_TOOL_DAMAGE = 1.0;
    private static final double DEFAULT_TOOL_SPEED = 4.0;
    private static final int[] DequeCapacities = new int[]{64, 16, 32, 8};
    private static final List<Supplier<Deque<Component>>> DequeGenerators = Arrays.stream(DequeCapacities)
            .mapToObj(capacity -> (Supplier<Deque<Component>>) (() -> new ArrayDeque<>(capacity)))
            .toList();
    private final ThreadLocal<List<SoftCache<Deque<Component>>>> reusableRenderLoreStack = ThreadLocal.withInitial(() -> {
        int capacity = DequeCapacities.length;
        List<SoftCache<Deque<Component>>> result = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            result.add(SoftCache.create(DequeGenerators.get(i)));
        }
        return Collections.unmodifiableList(result);
    });

    public DefaultItemRendererImpl() {
        super(NamespacedKeyUtil.BarleyTeaAPI("default_item_renderer"));
        ObjectUtil.tryCall(() -> ItemRendererRegister.getInstance().register(this));
    }

    @Nonnull
    @Override
    public ItemSubRendererSupportingState getSubRendererSupportingState() {
        return ItemSubRendererSupportingState.SelfHandled;
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

        meta = AlternativeItemState.store(AlternativeItemState.restore(meta));

        List<SoftCache<Deque<Component>>> renderLoreStackList = reusableRenderLoreStack.get();
        Deque<Component> renderLoreStack = renderLoreStackList.get(0).get();

        CustomItemType itemType = CustomItemType.get(itemStack);

        boolean isTool = itemType.nonNullMap(ItemHelper::materialIsTool, CustomItem::isTool);
        boolean hasEnchants = false;
        double toolDamage = 0, toolSpeed = 0;
        Lazy<Boolean> isBedrockPlayerLazy = Lazy.create(() -> {
            if (player != null && apiInstance.getSoftDependRegister().get(BulitInSoftDepend.Geyser)
                    instanceof GeyserConnector connector) {
                return connector.isBedrockPlayer(player);
            } else {
                return false;
            }
        });

        if (meta.hasEnchants() && !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
            final Map<Enchantment, Integer> enchantmentMap = meta.getEnchants();
            final Queue<Component> enchantLoreStack = renderLoreStackList.get(1).get();
            final Box<Double> toolDamageIncreaseBox = Box.box(0.0);
            final SoftDependConnector excellentEnchantsConnector = apiInstance.getSoftDependRegister()
                    .get(BulitInSoftDepend.ExcellentEnchants);
            boolean isToolFinal = isTool;
            enchantmentMap.forEach((enchantment, boxedLevel) -> {
                if (boxedLevel == null)
                    return;
                final int level = boxedLevel;
                final NamespacedKey key = enchantment.getKey();
                final String enchantName = "enchantment." + key.getNamespace() + "." + key.getKey();
                if (isToolFinal && enchantment.equals(Enchantment.DAMAGE_ALL)) {
                    if (level > 0) {
                        double value = ObjectUtil.letNonNull(toolDamageIncreaseBox.get(), 0.0);
                        value += (1 + (0.5) * (level - 1));
                        toolDamageIncreaseBox.set(value);
                    }
                }
                TranslatableComponent component = Component.translatable(enchantName);
                Style style;
                if (excellentEnchantsConnector instanceof ExcellentEnchantsConnector connector &&
                        connector.isExcellentEnchant(enchantment)) {
                    component = component.fallback(connector.getEnchantmentName(enchantment));
                    style = connector.getEnchantmentTierStyleUnsafe(enchantment, defaultEnchantTextStyleLazy);
                } else {
                    if (isBedrockPlayerLazy.get())
                        return;
                    style = (enchantment.isCursed() ? cursedEnchantTextStyleLazy : defaultEnchantTextStyleLazy).get();
                }
                component = component.style(style);
                if (level != 1 || enchantment.getMaxLevel() != 1) {
                    Component levelComponent;
                    levelComponent = Component.translatable("enchantment.level." + level,
                            Integer.toString(level));
                    component = component
                            .append(Component.space())
                            .append(levelComponent)
                            .style(style);
                }
                enchantLoreStack.offer(component);
            });
            toolDamage += ObjectUtil.letNonNull(toolDamageIncreaseBox.get(), 0.0);
            if (!enchantLoreStack.isEmpty()) {
                renderLoreStack.addAll(enchantLoreStack);
                hasEnchants = true;
                enchantLoreStack.clear();
            }
        }

        if (!meta.hasItemFlag(ItemFlag.HIDE_ITEM_SPECIFICS)) {
            if (meta instanceof EnchantmentStorageMeta esMeta && esMeta.hasStoredEnchants()) {
                if (hasEnchants)
                    renderLoreStack.add(Component.empty());
                final Map<Enchantment, Integer> enchantmentMap = esMeta.getStoredEnchants();
                final Queue<Component> enchantLoreStack = renderLoreStackList.get(1).get();
                final Box<Double> toolDamageIncreaseBox = Box.box(0.0);
                final SoftDependConnector excellentEnchantsConnector = apiInstance.getSoftDependRegister()
                        .get(BulitInSoftDepend.ExcellentEnchants);
                boolean isToolFinal = isTool;
                enchantmentMap.forEach((enchantment, boxedLevel) -> {
                    if (boxedLevel == null)
                        return;
                    final int level = boxedLevel;
                    final NamespacedKey key = enchantment.getKey();
                    final String enchantName = "enchantment." + key.getNamespace() + "." + key.getKey();
                    if (isToolFinal && enchantment.equals(Enchantment.DAMAGE_ALL)) {
                        if (level > 0) {
                            double value = ObjectUtil.letNonNull(toolDamageIncreaseBox.get(), 0.0);
                            value += (1 + (0.5) * (level - 1));
                            toolDamageIncreaseBox.set(value);
                        }
                    }
                    TranslatableComponent component = Component.translatable(enchantName);
                    Style style;
                    if (excellentEnchantsConnector instanceof ExcellentEnchantsConnector connector &&
                            connector.isExcellentEnchant(enchantment)) {
                        component = component.fallback(connector.getEnchantmentName(enchantment));
                        style = connector.getEnchantmentTierStyleUnsafe(enchantment, defaultEnchantTextStyleLazy);
                    } else {
                        if (isBedrockPlayerLazy.get())
                            return;
                        style = (enchantment.isCursed() ? cursedEnchantTextStyleLazy : defaultEnchantTextStyleLazy).get();
                    }
                    component = component.style(style);
                    if (level != 1 || enchantment.getMaxLevel() != 1) {
                        Component levelComponent;
                        levelComponent = Component.translatable("enchantment.level." + level,
                                Integer.toString(level));
                        component = component
                                .append(Component.space())
                                .append(levelComponent)
                                .style(style);
                    }
                    enchantLoreStack.offer(component);
                });
                toolDamage += ObjectUtil.letNonNull(toolDamageIncreaseBox.get(), 0.0);
                renderLoreStack.addAll(enchantLoreStack);
                enchantLoreStack.clear();
            }
        }

        var lore = meta.lore();
        if (lore != null && !lore.isEmpty())
            renderLoreStack.addAll(meta.lore());

        if (!meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
            Multimap<Attribute, AttributeModifier> attributeMap = meta.getAttributeModifiers();
            if (attributeMap == null) {
                attributeMap = ItemHelper.getDefaultAttributeModifiers(itemStack);
            } else if (isTool && !attributeMap.isEmpty()) {
                if (attributeMap.values().parallelStream()
                        .anyMatch(attributeModifier ->
                                !attributeModifier.getName().equals(Constants.DEFAULT_ATTRIBUTE_MODIFIER_NAME))) {
                    isTool = false;
                }
            }
            Queue<Component> toolAttributeLoreStack = (isTool ? renderLoreStackList.get(2).get() : null);
            if (!attributeMap.isEmpty()) {
                Deque<Component> slotAttributeLoreStack = renderLoreStackList.get(3).get();
                HashMap<EquipmentSlot, TreeMap<Attribute, double[]>> slotAttributeMap = new HashMap<>();
                final Box<Double> toolDamageIncreaseBox = Box.box(0.0);
                final Box<Double> toolSpeedIncreaseBox = Box.box(0.0);
                boolean isToolFinal = isTool;
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
                        if (isToolFinal && (slot == null || slot.equals(EquipmentSlot.HAND))) {
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
                INMSItemHelper helper = Bukkit.getServicesManager().load(INMSItemHelper.class);
                for (EquipmentSlot slot : slots) {
                    if (slot == null)
                        continue;
                    TreeMap<Attribute, double[]> attributeOperationMap = slotAttributeMap.get(slot);
                    if (attributeOperationMap == null || attributeOperationMap.isEmpty())
                        continue;
                    String slotStringKey = helper == null ? switch (slot) {
                        case HAND -> "mainhand";
                        case OFF_HAND -> "offhand";
                        case FEET -> "feet";
                        case LEGS -> "legs";
                        case CHEST -> "chest";
                        case HEAD -> "head";
                        default -> slot.name().replace("_", "");
                    } : helper.getNMSEquipmentSlotName(slot);
                    if (slotStringKey == null)
                        continue;
                    slotStringKey = "item.modifiers." + slotStringKey;
                    slotAttributeLoreStack.offer(Component.translatable(slotStringKey)
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
                                boolean isPositive = value >= 0;
                                value = Math.abs(Math.round(value * 100.0) / 100.0);
                                String valueString = Double.toString(value);
                                if (valueString.endsWith(".0"))
                                    valueString = valueString.substring(0, valueString.length() - 2);
                                slotAttributeLoreStack.offer(Component.translatable(format)
                                        .args(Component.text(valueString),
                                                Component.translatable(attributeTranslateKey))
                                        .color(isPositive ? NamedTextColor.BLUE : NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false));
                            }
                        }
                    });
                    slotAttributeLoreStack.offer(Component.empty());
                }
                if (!slotAttributeLoreStack.isEmpty()) {
                    slotAttributeLoreStack.removeLast();
                    renderLoreStack.offer(Component.empty());
                    renderLoreStack.addAll(slotAttributeLoreStack);
                    slotAttributeLoreStack.clear();
                }
            }
            if (toolAttributeLoreStack != null) {
                toolDamage += DEFAULT_TOOL_DAMAGE;
                toolSpeed += DEFAULT_TOOL_SPEED;
                toolDamage = Math.round(toolDamage * 100.0) / 100.0;
                String toolDamageString = Double.toString(toolDamage);
                if (toolDamageString.endsWith(".0"))
                    toolDamageString = toolDamageString.substring(0, toolDamageString.length() - 2);
                toolSpeed = Math.round(toolSpeed * 100.0) / 100.0;
                String toolSpeedString = Double.toString(toolSpeed);
                if (toolSpeedString.endsWith(".0"))
                    toolSpeedString = toolSpeedString.substring(0, toolSpeedString.length() - 2);
                toolAttributeLoreStack.offer(Component.translatable("item.modifiers.mainhand", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false));
                toolAttributeLoreStack.offer(Component.space().append(
                                Component.translatable("attribute.modifier.equals.0").args(
                                        Component.text(toolDamageString),
                                        Component.translatable("attribute.name.generic.attack_damage")))
                        .color(NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false));
                toolAttributeLoreStack.offer(Component.space().append(
                                Component.translatable("attribute.modifier.equals.0").args(
                                        Component.text(toolSpeedString),
                                        Component.translatable("attribute.name.generic.attack_speed")))
                        .color(NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false));
                renderLoreStack.offer(Component.empty());
                renderLoreStack.addAll(toolAttributeLoreStack);
                toolAttributeLoreStack.clear();
            }
        }

        Component displayName = meta.displayName();
        CustomItem customItem = itemType.asCustomItem();
        if (customItem != null) {
            boolean isRenamed;
            if (displayName == null) {
                displayName = ItemHelper.getDefaultNameComponent(itemType);
                isRenamed = false;
            } else {
                isRenamed = true;
            }

            CustomItemRarity rarity = customItem.getRarity();
            if (customItem.isRarityUpgraded(itemStack)) {
                rarity = rarity.upgrade();
            }
            displayName = rarity.apply(displayName, isRenamed, false);

            FeatureItemCustomDurability feature = customItem.getFeature(FeatureItemCustomDurability.class);
            if (feature != null) {
                int maxDura = feature.getMaxDurability(itemStack);
                int dura = maxDura - feature.getDurabilityDamage(itemStack);
                if (dura < maxDura || Boolean.TRUE.equals(
                        ObjectUtil.safeMap(
                                customItem.getFeature(FeatureItemCustomDurabilityExtra.class),
                                _feature -> _feature.isAlwaysShowDurabilityHint(itemStack)
                        ))) {
                    renderLoreStack.offer(Component.translatable("item.durability", NamedTextColor.WHITE)
                            .args(Component.text(dura), Component.text(maxDura))
                            .decoration(TextDecoration.ITALIC, false));
                }
            }

            if (meta.isUnbreakable()) {
                renderLoreStack.offer(Component.translatable("item.unbreakable", NamedTextColor.BLUE)
                        .decoration(TextDecoration.ITALIC, false));
            }

            renderLoreStack.offer(Component.text(customItem.getKey().toString(), NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false));
        }

        List<Component> output = new ArrayList<>(renderLoreStack);

        DataItemDisplay data = null;

        FeatureItemDisplay feature = FeatureHelper.getFeatureUnsafe(customItem, FeatureItemDisplay.class);
        if (feature != null) {
            data = new DataItemDisplay(player, itemStack, displayName, output);
            try {
                feature.handleItemDisplay(data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        ItemSubRendererRegister subRendererRegister = ItemSubRendererRegister.getInstanceUnsafe();
        if (subRendererRegister != null) {
            if (data == null)
                data = new DataItemDisplay(player, itemStack, displayName, output);
            for (ItemSubRenderer subRenderer : subRendererRegister.listAll()) {
                try {
                    subRenderer.render(data);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        renderLoreStack.clear();

        meta.displayName(data != null ? data.getDisplayName() : displayName);
        meta.lore(output);
        if (meta instanceof EnchantmentStorageMeta)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ITEM_SPECIFICS);
        else
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
