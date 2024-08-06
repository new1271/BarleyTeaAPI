package org.ricetea.barleyteaapi.internal.item.renderer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.internal.nms.INMSItemHelper2;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.CustomItemRarity;
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
    private static final NamespacedKey[] alternateAttributeNameKeys = new NamespacedKey[]{
            NamespacedKey.minecraft("default_attack_damage_real_name"),
            NamespacedKey.minecraft("default_attack_speed_real_name"),
    };
    @Nonnull
    private static final NamespacedKey[] alternateAttributeIDKeys = new NamespacedKey[]{
            NamespacedKey.minecraft("default_attack_damage_real_id"),
            NamespacedKey.minecraft("default_attack_speed_real_id"),
    };

    @Nonnull
    private static final Attribute[] targetAttributes = new Attribute[]{
            Attribute.GENERIC_ATTACK_DAMAGE, Attribute.GENERIC_ATTACK_SPEED
    };

    @Nonnull
    private static final String[] defaultAttributeNames = new String[]{
            "minecraft:default_attack_damage", "minecraft:default_attack_speed"
    };

    @Nonnull
    private static final ThreadLocal<SoftCache<StringBuilder>> localBuilderCache =
            ThreadLocal.withInitial(() -> SoftCache.create(StringBuilder::new));

    @Nonnull
    private static final Map<String, String> literalNamespaceMap = new ConcurrentHashMap<>();

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
        meta = AlternativeItemState.store(AlternativeItemState.restore(meta));
        render0(meta);

        DataItemDisplay data = null;
        Component displayName = meta.displayName();

        List<Component> lore = meta.lore();
        Lazy<List<Component>> outputLazy = Lazy.create(() -> lore == null ? new ArrayList<>() : new ArrayList<>(lore));

        CustomItem itemType = CustomItem.get(itemStack);
        if (itemType != null) {
            List<Component> output = outputLazy.get();

            Component defaultNameComponent = ItemHelper.getDefaultNameComponent(itemType);

            boolean isRenamed;
            if (displayName == null) {
                displayName = defaultNameComponent;
                isRenamed = false;
            } else {
                isRenamed = true;
            }

            CustomItemRarity rarity = itemType.getRarity();
            if (itemType.isRarityUpgraded(itemStack)) {
                rarity = rarity.upgrade();
            }

            displayName = rarity.apply(displayName, isRenamed, false);

            NamespacedKey itemTypeKey = itemType.getKey();
            String namespace = itemTypeKey.getNamespace();
            output.add(Component.empty());
            output.add(Component.translatable(ALTERNATE_ITEM_ID_FORMAT)
                    .color(NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .args(
                            Component.translatable("namespace." + namespace, getFallbackNamespaceString(namespace)),
                            defaultNameComponent
                    ));

            {
                FeatureItemCustomDurability feature = itemType.getFeature(FeatureItemCustomDurability.class);
                if (feature != null) {
                    INMSItemHelper2 helper = INMSItemHelper2.getInstanceUnsafe();
                    if (helper != null) {
                        int maxDura = feature.getMaxDurability(itemStack);
                        int damage = feature.getDurabilityDamage(itemStack);
                        if (meta instanceof Damageable damageable)
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

    private void render0(@Nonnull ItemMeta meta) {
        var modifiers = meta.getAttributeModifiers();
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
                    .filter(val -> EquipmentSlot.HAND.equals(val.getSlot()))
                    .filter(val -> AttributeModifier.Operation.ADD_NUMBER.equals(val.getOperation()))
                    .findAny()
                    .orElse(null);
            if (modifier == null)
                continue;
            NamespacedKey nameKey = alternateAttributeNameKeys[i];
            NamespacedKey idKey = alternateAttributeIDKeys[i];
            try {
                container.set(nameKey, PersistentDataType.STRING, modifier.getName());
                container.set(idKey, ExtraPersistentDataType.UUID_LONG_ARRAY, modifier.getUniqueId());
            } catch (Exception ignored) {
                container.remove(nameKey);
                container.remove(idKey);
            }
            meta.removeAttributeModifier(attribute, modifier);
            meta.addAttributeModifier(attribute,
                    new AttributeModifier(modifier.getUniqueId(), defaultAttributeNames[i],
                            modifier.getAmount(), AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlot.HAND)
            );
        }
    }

    @Nonnull
    @Override
    public ItemStack restore(@Nonnull ItemStack itemStack, @Nullable Player player) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return itemStack;
        meta = AlternativeItemState.restore(meta);
        restore0(meta);
        INMSItemHelper2 helper = INMSItemHelper2.getInstanceUnsafe();
        if (helper != null) {
            if (helper.isNeedSpecialRestore(itemStack)) {
                itemStack.setItemMeta(meta);
                return helper.restoreCustomDurabilityBarSpecial(itemStack);
            } else if (meta instanceof Damageable damageable) {
                helper.restoreCustomDurabilityBar(damageable, itemStack.getType().getMaxDurability());
            }
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private void restore0(@Nonnull ItemMeta meta) {
        var modifiers = meta.getAttributeModifiers();
        if (modifiers == null || modifiers.isEmpty())
            return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        for (int i = 0, length = targetAttributes.length; i < length; i++) {
            NamespacedKey nameKey = alternateAttributeNameKeys[i];
            NamespacedKey idKey = alternateAttributeIDKeys[i];

            String restoreName;
            UUID restoreUUID;
            try {
                restoreName = container.get(nameKey, PersistentDataType.STRING);
                restoreUUID = container.get(idKey, ExtraPersistentDataType.UUID_LONG_ARRAY);
            } catch (Exception ignored) {
                continue;
            }

            if (restoreName == null || restoreUUID == null)
                continue;

            String storeName = defaultAttributeNames[i];
            Attribute attribute = targetAttributes[i];
            Collection<AttributeModifier> modifierCollection = modifiers.get(attribute);
            if (modifierCollection.isEmpty())
                continue;
            AttributeModifier modifier = modifierCollection.stream()
                    .filter(Objects::nonNull)
                    .filter(val -> storeName.equals(val.getName()))
                    .filter(val -> restoreUUID.equals(val.getUniqueId()))
                    .filter(val -> EquipmentSlot.HAND.equals(val.getSlot()))
                    .filter(val -> AttributeModifier.Operation.ADD_NUMBER.equals(val.getOperation()))
                    .findAny()
                    .orElse(null);
            if (modifier == null)
                continue;
            meta.removeAttributeModifier(attribute, modifier);
            meta.addAttributeModifier(attribute,
                    new AttributeModifier(modifier.getUniqueId(), restoreName,
                            modifier.getAmount(), AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlot.HAND)
            );

            ObjectUtil.tryCallSilently(nameKey, container::remove);
            ObjectUtil.tryCallSilently(idKey, container::remove);
        }
    }

    @Nonnull
    private static String getFallbackNamespaceString(@Nonnull String namespace) {
        return literalNamespaceMap.computeIfAbsent(namespace, DefaultItemRendererImpl2::getFallbackNamespaceStringNoCached);
    }

    @Nonnull
    private static String getFallbackNamespaceStringNoCached(@Nonnull String namespace) {
        if (namespace.isEmpty())
            return "";
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
