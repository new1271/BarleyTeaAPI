package org.ricetea.barleyteaapi.internal.v2.item.renderer;

import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.internal.nms.INMSItemHelper2;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.CustomItemRarity;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemDisplay;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemDisplay;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.registration.ItemRendererRegister;
import org.ricetea.barleyteaapi.api.item.registration.ItemSubRendererRegister;
import org.ricetea.barleyteaapi.api.item.render.ItemSubRenderer;
import org.ricetea.barleyteaapi.api.item.render.ItemSubRendererSupportingState;
import org.ricetea.barleyteaapi.api.item.render.util.AlternativeItemState;
import org.ricetea.barleyteaapi.api.localization.LocalizationRegister;
import org.ricetea.barleyteaapi.api.localization.LocalizedMessageFormat;
import org.ricetea.barleyteaapi.api.persistence.ExtraPersistentDataType;
import org.ricetea.barleyteaapi.internal.item.renderer.AbstractItemRendererImpl;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;
import org.ricetea.utils.SoftCache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Singleton
@ApiStatus.Internal
public class DefaultItemRendererImpl2 extends AbstractItemRendererImpl {

    @Nonnull
    public static final String ALTERNATE_ITEM_ID_FORMAT = "barleyteaapi.message.alternate_item_id_format";

    @Nonnull
    private static final NamespacedKey[] alternateAttributeKeys = new NamespacedKey[]{
            NamespacedKey.minecraft("default_attack_damage_real_key"),
            NamespacedKey.minecraft("default_attack_speed_real_key"),
    };

    @Nonnull
    private static final Attribute[] targetAttributes = new Attribute[]{
            Attribute.GENERIC_ATTACK_DAMAGE, Attribute.GENERIC_ATTACK_SPEED
    };

    @Nonnull
    private static final NamespacedKey[] defaultAttributeKeys = new NamespacedKey[]{
            NamespacedKey.minecraft("base_attack_damage"),
            NamespacedKey.minecraft("base_attack_speed")
    };

    @Nonnull
    private static final ThreadLocal<SoftCache<StringBuilder>> localBuilderCache =
            ThreadLocal.withInitial(() -> SoftCache.create(StringBuilder::new));

    @Nonnull
    private static final Map<Locale, Map<String, String>> literalNamespaceMap = new ConcurrentHashMap<>();

    public DefaultItemRendererImpl2() {
        super(NamespacedKeyUtil.BarleyTeaAPI("default_item_renderer"));
        ObjectUtil.tryCall(() -> ItemRendererRegister.getInstance().register(this));
        LocalizedMessageFormat format = LocalizedMessageFormat.create(ALTERNATE_ITEM_ID_FORMAT);
        format.setFormat(new MessageFormat("[{0}/{1}]"));
        LocalizationRegister.getInstance().register(format);
    }

    @Nonnull
    @Override
    public ItemSubRendererSupportingState getSubRendererSupportingState() {
        return ItemSubRendererSupportingState.SelfHandled;
    }

    @Override
    @Nonnull
    public ItemStack render(@Nonnull ItemStack itemStack, @Nullable Player player) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return itemStack;

        CustomItemType customitemType = CustomItemType.get(itemStack);

        AlternativeItemState itemState = AlternativeItemState.getInstanceUnsafe();
        if (itemState != null)
            meta = itemState.store(itemState.restore(meta));

        render0(customitemType, meta);

        DataItemDisplay data = null;
        Component displayName = meta.displayName();

        List<Component> lore = meta.lore();
        Lazy<List<Component>> outputLazy = Lazy.create(() -> lore == null ? new ArrayList<>() : new ArrayList<>(lore));

        CustomItem itemType = customitemType.asCustomItem();
        if (itemType != null) {
            List<Component> output = outputLazy.get();

            Component defaultNameComponent = ItemHelper.getDefaultNameComponent(itemType);

            boolean isRenamed = displayName != null;

            CustomItemRarity rarity = itemType.getRarity();
            if (itemType.isRarityUpgraded(itemStack)) {
                rarity = rarity.upgrade();
            }

            meta.itemName(rarity.apply(defaultNameComponent));

            if (displayName != null)
                displayName = rarity.apply(displayName, isRenamed, false);

            NamespacedKey itemTypeKey = itemType.getKey();
            String namespace = itemTypeKey.getNamespace();
            output.add(Component.empty());
            output.add(Component.translatable(ALTERNATE_ITEM_ID_FORMAT)
                    .color(NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .arguments(
                            Component.translatable("namespace." + namespace,
                                    getFallbackNamespaceString(player, namespace)),
                            defaultNameComponent
                    ));

            {
                FeatureItemCustomDurability feature = itemType.getFeature(FeatureItemCustomDurability.class);
                if (feature != null) {
                    INMSItemHelper2 helper = INMSItemHelper2.getInstanceUnsafe();
                    if (helper != null) {
                        int maxDura = feature.getMaxDurability(itemStack);
                        if (maxDura > 0) {
                            int damage = feature.getDurabilityDamage(itemStack);
                            if (meta instanceof Damageable damageable && itemStack.getType().getMaxDurability() > 0)
                                helper.applyCustomDurabilityBar(damageable, damage, maxDura);
                            else {
                                itemStack.setItemMeta(meta);
                                itemStack = helper.applyCustomDurabilityBarSpecial(itemStack, damage, maxDura);
                                meta = itemStack.getItemMeta();
                                if (meta == null)
                                    return itemStack;
                            }
                        }
                    }
                }
            }

            {
                FeatureItemDisplay feature = itemType.getFeature(FeatureItemDisplay.class);
                if (feature != null) {
                    data = new DataItemDisplay(player, itemStack, displayName, output);
                    ObjectUtil.tryCall(data, feature::handleItemDisplay);
                }
            }
        }

        ItemSubRendererRegister subRendererRegister = ItemSubRendererRegister.getInstanceUnsafe();
        if (subRendererRegister != null) {
            if (data == null)
                data = new DataItemDisplay(player, itemStack, displayName, outputLazy.get());
            for (ItemSubRenderer subRenderer : subRendererRegister.listAll()) {
                if (subRenderer == null)
                    continue;
                ObjectUtil.tryCall(data, subRenderer::render);
            }
        }

        if (data != null) {
            displayName = data.getDisplayName();
        }
        List<Component> output = outputLazy.getUnsafe();
        if (output != null) {
            meta.lore(output);
        }
        meta.displayName(displayName);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @SuppressWarnings("UnstableApiUsage")
    private void render0(@Nonnull CustomItemType itemType, @Nonnull ItemMeta meta) {
        CustomItem customItem = itemType.asCustomItem();
        if (customItem != null && customItem.isTool()) {
            Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();
            if (modifiers == null)
                return;
            PersistentDataContainer container = meta.getPersistentDataContainer();
            for (int i = 0, length = targetAttributes.length; i < length; i++) {
                Attribute attribute = targetAttributes[i];
                Collection<AttributeModifier> modifierCollection = modifiers.get(attribute);
                if (modifierCollection.isEmpty())
                    continue;
                AttributeModifier modifier = modifierCollection.stream()
                        .filter(Objects::nonNull)
                        .filter(val -> EquipmentSlotGroup.MAINHAND.equals(val.getSlotGroup()))
                        .filter(val -> AttributeModifier.Operation.ADD_NUMBER.equals(val.getOperation()))
                        .findAny()
                        .orElse(null);
                if (modifier == null)
                    continue;
                NamespacedKey modifierKey = modifier.getKey();
                NamespacedKey defaultKey = defaultAttributeKeys[i];
                if (Objects.equals(defaultKey, modifierKey))
                    continue;
                NamespacedKey alterStoreKey = alternateAttributeKeys[i];
                try {
                    container.set(alterStoreKey, ExtraPersistentDataType.NAMESPACED_KEY, modifierKey);
                } catch (Exception ignored) {
                    container.remove(alterStoreKey);
                }
                meta.removeAttributeModifier(attribute, modifier);
                meta.addAttributeModifier(attribute,
                        new AttributeModifier(defaultKey,
                                modifier.getAmount(), AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlotGroup.MAINHAND)
                );
            }
        }
    }

    @Nonnull
    @Override
    public ItemStack restore(@Nonnull ItemStack itemStack, @Nullable Player player) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return itemStack;
        AlternativeItemState itemState = AlternativeItemState.getInstanceUnsafe();
        if (itemState != null)
            meta = itemState.restore(meta);
        restore0(meta);
        INMSItemHelper2 helper = INMSItemHelper2.getInstanceUnsafe();
        if (helper != null) {
            int maxDura = itemStack.getType().getMaxDurability();
            if (helper.isNeedSpecialRestore(meta)) {
                itemStack.setItemMeta(meta);
                return helper.restoreCustomDurabilityBarSpecial(itemStack);
            } else if (meta instanceof Damageable damageable) {
                helper.restoreCustomDurabilityBar(damageable, maxDura);
            }
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @SuppressWarnings("UnstableApiUsage")
    private void restore0(@Nonnull ItemMeta meta) {
        var modifiers = meta.getAttributeModifiers();
        if (modifiers == null || modifiers.isEmpty())
            return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        for (int i = 0, length = targetAttributes.length; i < length; i++) {
            NamespacedKey altKey = alternateAttributeKeys[i];

            NamespacedKey restoreKey;
            try {
                restoreKey = container.get(altKey, ExtraPersistentDataType.NAMESPACED_KEY);
            } catch (Exception ignored) {
                continue;
            }

            if (restoreKey == null)
                continue;

            Attribute attribute = targetAttributes[i];
            Collection<AttributeModifier> modifierCollection = modifiers.get(attribute);
            if (modifierCollection.isEmpty())
                continue;
            AttributeModifier modifier = modifierCollection.stream()
                    .filter(Objects::nonNull)
                    .filter(val -> restoreKey.equals(val.getKey()))
                    .filter(val -> EquipmentSlotGroup.MAINHAND.equals(val.getSlotGroup()))
                    .filter(val -> AttributeModifier.Operation.ADD_NUMBER.equals(val.getOperation()))
                    .findAny()
                    .orElse(null);
            if (modifier == null)
                continue;
            meta.removeAttributeModifier(attribute, modifier);
            meta.addAttributeModifier(attribute,
                    new AttributeModifier(restoreKey,
                            modifier.getAmount(), AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.MAINHAND)
            );

            ObjectUtil.tryCallSilently(altKey, container::remove);
        }
    }

    @Nonnull
    private static String getFallbackNamespaceString(@Nullable Player player, @Nonnull String namespace) {
        if (namespace.isEmpty())
            return "";
        Locale locale = ObjectUtil.letNonNull(ObjectUtil.tryMapSilently(player, Player::locale), Locale::getDefault);
        Map<String, String> map = literalNamespaceMap.computeIfAbsent(locale, val -> new ConcurrentHashMap<>());
        return map.computeIfAbsent(namespace, val -> getFallbackNamespaceStringNoCached(locale, val));
    }

    @Nonnull
    private static String getFallbackNamespaceStringNoCached(@Nonnull Locale locale, @Nonnull String namespace) {
        if (namespace.isEmpty())
            return "";
        MessageFormat format = GlobalTranslator.translator().translate("namespace." + namespace, locale);
        if (format != null)
            return format.toPattern();
        if (namespace.contains("_")) {
            String[] paths = namespace.split(Pattern.quote("_"));
            StringBuilder builder = localBuilderCache.get().get();
            for (String path : paths) {
                builder.append(normalizeString(path));
            }
            String result = builder.toString();
            builder.setLength(0);
            return result;
        } else {
            return normalizeString(namespace);
        }
    }

    @Nonnull
    private static String normalizeString(@Nonnull String str) {
        char c = str.charAt(0);
        if (Character.isLowerCase(c)) {
            c = Character.toUpperCase(c);
            if (str.length() > 1)
                return c + str.substring(1);
            return Character.toString(c);
        }
        return str;
    }
}
