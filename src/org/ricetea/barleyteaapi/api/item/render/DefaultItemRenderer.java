package org.ricetea.barleyteaapi.api.item.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.internal.bridge.ExcellentEnchantsBridge;
import org.ricetea.barleyteaapi.internal.nms.helper.NMSItemHelper;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;
import org.ricetea.barleyteaapi.util.ObjectUtil;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

public class DefaultItemRenderer extends AbstractItemRenderer {
    private static final @Nonnull NamespacedKey ItemLoreKey = NamespacedKeyUtils.BarleyTeaAPI("item_lore");
    private static final @Nonnull String AlternateItemFlagStoreKeyHeader = "item_flag_";
    private static final @Nonnull Lazy<DefaultItemRenderer> _inst = new Lazy<>(DefaultItemRenderer::new);
    private static final @Nonnull EquipmentSlot[] slots = EquipmentSlot.values();

    private DefaultItemRenderer() {
        super(NamespacedKeyUtils.BarleyTeaAPI("default_item_renderer"));
        ItemRendererRegister.getInstance().register(this);
    }

    @Nonnull
    public static DefaultItemRenderer getInstance() {
        return _inst.get();
    }

    @SuppressWarnings({ "unchecked", "null" })
    @Override
    public void render(@Nonnull ItemStack itemStack) {
        BarleyTeaAPI apiInstance = BarleyTeaAPI.getInstance();
        if (apiInstance != null) {
            NamespacedKey id = BaseItem.getItemID(itemStack);
            if (id != null) {
                BaseItem itemType = ItemRegister.getInstance().lookupItemType(id);
                if (itemType != null) {
                    ItemMeta meta = itemStack.getItemMeta();
                    List<Component> customLores = getItemLore(itemStack);
                    boolean isTool = itemType.isTool();
                    double toolDamage = 1;
                    double toolSpeed = 4.0;
                    List<Component> lores = new ArrayList<Component>();
                    if (meta.hasEnchants() && !hasItemFlag(meta, ItemFlag.HIDE_ENCHANTS)) {
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
                            lores.add(component);
                        }
                    }
                    if (customLores != null) {
                        lores.addAll(customLores);
                    }
                    lores.add(Component.empty());
                    if (!hasItemFlag(meta, ItemFlag.HIDE_ATTRIBUTES)) {
                        ArrayList<Component> MainHandLore = (isTool ? new ArrayList<>() : null);
                        {
                            Multimap<Attribute, AttributeModifier> attrbuteMap = meta.hasAttributeModifiers()
                                    ? meta.getAttributeModifiers()
                                    : NMSItemHelper.getDefaultAttributeModifiers(itemStack);
                            if (attrbuteMap != null) {
                                ArrayList<Component> AttributeLore = new ArrayList<>();
                                HashMap<EquipmentSlot, Multimap<Attribute, AttributeModifier>> map = new HashMap<>();
                                for (Entry<Attribute, AttributeModifier> entry : attrbuteMap.entries()) {
                                    Attribute attribute = entry.getKey();
                                    AttributeModifier modifier = entry.getValue();
                                    if (attribute == null || modifier.getAmount() == 0)
                                        continue;
                                    EquipmentSlot slot = modifier.getSlot();
                                    if (isTool && (slot == null || slot.equals(EquipmentSlot.HAND))) {
                                        if (modifier.getOperation()
                                                .equals(AttributeModifier.Operation.ADD_NUMBER)) {
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
                                    if (slot == null) {
                                        for (EquipmentSlot iteratedSlot : slots) {
                                            if (iteratedSlot == null)
                                                continue;
                                            Multimap<Attribute, AttributeModifier> hmap = map.get(iteratedSlot);
                                            if (hmap == null) {
                                                hmap = LinkedHashMultimap.create();
                                                map.put(iteratedSlot, hmap);
                                            }
                                            hmap.put(attribute, modifier);
                                        }
                                    } else {
                                        Multimap<Attribute, AttributeModifier> hmap = map.get(slot);
                                        if (hmap == null) {
                                            hmap = LinkedHashMultimap.create();
                                            map.put(slot, hmap);
                                        }
                                        hmap.put(attribute, modifier);
                                    }
                                }
                                for (EquipmentSlot slot : slots) {
                                    if (slot == null)
                                        continue;
                                    String slotStringKey;
                                    switch (slot) {
                                        case CHEST:
                                            slotStringKey = "item.modifiers.chest";
                                            break;
                                        case FEET:
                                            slotStringKey = "item.modifiers.feet";
                                            break;
                                        case HAND:
                                            slotStringKey = "item.modifiers.mainhand";
                                            break;
                                        case HEAD:
                                            slotStringKey = "item.modifiers.head";
                                            break;
                                        case LEGS:
                                            slotStringKey = "item.modifiers.legs";
                                            break;
                                        case OFF_HAND:
                                            slotStringKey = "item.modifiers.offhand";
                                            break;
                                        default:
                                            continue;
                                    }
                                    Multimap<Attribute, AttributeModifier> mapValue = map.get(slot);
                                    AttributeLore.add(Component.translatable(slotStringKey).color(NamedTextColor.GRAY)
                                            .decoration(TextDecoration.ITALIC, false));
                                    for (Entry<Attribute, AttributeModifier> entry2 : mapValue.entries()) {
                                        final NamespacedKey key = entry2.getKey().getKey();
                                        final String attributeKey = "attribute.name." + key.getKey();
                                        double value = entry2.getValue().getAmount();
                                        String format = "attribute.modifier." + (value >= 0 ? "plus." : "take.")
                                                + entry2.getValue().getOperation().ordinal();
                                        if (!entry2.getValue().getOperation()
                                                .equals(AttributeModifier.Operation.ADD_NUMBER))
                                            value *= 100;
                                        else if (entry2.getKey().equals(Attribute.GENERIC_KNOCKBACK_RESISTANCE)) {
                                            value *= 10;
                                        }
                                        value = Math.round(value * 100.0) / 100.0;
                                        String valueString = Double.toString(value);
                                        if (valueString.endsWith(".0"))
                                            valueString = valueString.substring(0, valueString.length() - 2);
                                        AttributeLore.add(Component.translatable(format)
                                                .args(Component.text(valueString),
                                                        Component.translatable(attributeKey))
                                                .color(value > 0 ? NamedTextColor.BLUE : NamedTextColor.RED)
                                                .decoration(TextDecoration.ITALIC, false));
                                    }
                                    AttributeLore.add(Component.empty());
                                }
                                lores.addAll(AttributeLore);
                                if (!isTool)
                                    lores.remove(lores.size() - 1);
                            }
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
                            MainHandLore.add(Component.text(" ").append(
                                    Component.translatable("attribute.modifier.equals.0").args(
                                            Component.text(toolDamageString),
                                            Component.translatable("attribute.name.generic.attack_damage")))
                                    .color(NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false));
                            MainHandLore.add(Component.text(" ").append(
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
                    }
                    if (itemType instanceof FeatureItemCustomDurability) {
                        FeatureItemCustomDurability customDurability = (FeatureItemCustomDurability) itemType;
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
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
                    itemStack.setItemMeta(meta);
                }
            }
        }
    }

    protected void beforeFirstRender(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            setItemLore(meta, meta.lore());
            addItemFlags(meta, meta.getItemFlags());
        }
        setLastRenderer(itemStack, this);
    }

    public @Nullable List<Component> getItemLore(@Nonnull ItemMeta itemMeta) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        String loreInJSON = container.getOrDefault(ItemLoreKey, PersistentDataType.STRING, null);
        if (loreInJSON == null)
            return null;
        else {
            try {
                JSONArray array = (JSONArray) JSONValue.parse(loreInJSON);
                int length = array.size();
                Component[] components = new Component[length];
                JSONComponentSerializer serializer = JSONComponentSerializer.json();
                for (int i = 0; i < length; i++) {
                    try {
                        components[i] = serializer.deserialize(array.get(i).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return Arrays.asList(components);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void setItemLore(@Nonnull ItemMeta itemMeta, @Nullable List<? extends Component> lores) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (lores == null) {
            container.remove(ItemLoreKey);
        } else {
            JSONComponentSerializer serializer = JSONComponentSerializer.json();
            int length = lores.size();
            JSONArray array = new JSONArray();
            for (int i = 0; i < length; i++) {
                Component lore = lores.get(i);
                if (lore == null)
                    lore = Component.empty();
                array.add(serializer.serialize(lore));
            }
            container.set(ItemLoreKey, PersistentDataType.STRING, array.toString());
        }
    }

    @Nullable
    public Set<ItemFlag> getItemFlags(@Nonnull ItemMeta itemMeta) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        ItemFlag[] values = ItemFlag.values();
        int length = values.length;
        ArrayList<ItemFlag> flagList = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            ItemFlag flag = values[i];
            if (container.getOrDefault(
                    NamespacedKeyUtils
                            .BarleyTeaAPI(AlternateItemFlagStoreKeyHeader + flag.toString().toLowerCase()),
                    PersistentDataType.BOOLEAN, false) == true) {
                flagList.add(flag);
            }
        }
        return Set.copyOf(flagList);
    }

    public boolean hasItemFlag(@Nonnull ItemMeta itemMeta, @Nullable ItemFlag flag) {
        if (flag == null)
            return false;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.getOrDefault(
                NamespacedKeyUtils
                        .BarleyTeaAPI(AlternateItemFlagStoreKeyHeader + flag.toString().toLowerCase()),
                PersistentDataType.BOOLEAN, false) == true;
    }

    public void addItemFlags(@Nonnull ItemMeta itemMeta, @Nullable Set<ItemFlag> flags) {
        if (flags == null)
            return;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        for (ItemFlag flag : flags) {
            container.set(
                    NamespacedKeyUtils
                            .BarleyTeaAPI(AlternateItemFlagStoreKeyHeader + flag.toString().toLowerCase()),
                    PersistentDataType.BOOLEAN, true);
        }
    }

    public void addItemFlags(@Nonnull ItemMeta itemMeta, @Nullable ItemFlag... flags) {
        if (flags == null)
            return;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        for (ItemFlag flag : flags) {
            container.set(
                    NamespacedKeyUtils
                            .BarleyTeaAPI(AlternateItemFlagStoreKeyHeader + flag.toString().toLowerCase()),
                    PersistentDataType.BOOLEAN, true);
        }
    }

    public void removeItemFlags(@Nonnull ItemMeta itemMeta, @Nullable Set<ItemFlag> flags) {
        if (flags == null)
            return;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        for (ItemFlag flag : flags) {
            container.remove(
                    NamespacedKeyUtils
                            .BarleyTeaAPI(AlternateItemFlagStoreKeyHeader + flag.toString().toLowerCase()));
        }
    }

    public void removeItemFlags(@Nonnull ItemMeta itemMeta, @Nullable ItemFlag... flags) {
        if (flags == null)
            return;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        for (ItemFlag flag : flags) {
            container.remove(
                    NamespacedKeyUtils
                            .BarleyTeaAPI(AlternateItemFlagStoreKeyHeader + flag.toString().toLowerCase()));
        }
    }
}
