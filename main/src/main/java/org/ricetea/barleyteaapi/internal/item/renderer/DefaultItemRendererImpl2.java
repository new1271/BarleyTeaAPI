package org.ricetea.barleyteaapi.internal.item.renderer;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
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
import org.ricetea.barleyteaapi.api.persistence.ExtraPersistentDataType;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.*;

@Singleton
@ApiStatus.Internal
public class DefaultItemRendererImpl2 extends AbstractItemRendererImpl {

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

    public DefaultItemRendererImpl2() {
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
            boolean isRenamed;
            if (displayName == null) {
                displayName = ItemHelper.getDefaultNameComponent(itemType);
                isRenamed = false;
            } else {
                isRenamed = true;
            }

            CustomItemRarity rarity = itemType.getRarity();
            if (itemType.isRarityUpgraded(itemStack)) {
                rarity = rarity.upgrade();
            }

            displayName = rarity.apply(displayName, isRenamed, false);

            {
                FeatureItemCustomDurability feature = itemType.getFeature(FeatureItemCustomDurability.class);
                if (feature != null) {
                    INMSItemHelper2 helper = INMSItemHelper2.getInstanceUnsafe();
                    if (helper != null) {
                        int maxDura = feature.getMaxDurability(itemStack);
                        int damage = feature.getDurabilityDamage(itemStack);
                        helper.applyCustomDurabilityBar(meta, damage, maxDura);
                    }
                }
            }

            {
                FeatureItemDisplay feature = itemType.getFeature(FeatureItemDisplay.class);
                if (feature != null) {
                    data = new DataItemDisplay(player, itemStack, displayName, outputLazy.get());
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
            List<Component> output = outputLazy.getUnsafe();
            if (output != null) {
                meta.lore(output);
            }
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
        INMSItemHelper2 helper = INMSItemHelper2.getInstanceUnsafe();
        if (helper != null) {
            helper.restoreCustomDurabilityBar(meta, itemStack.getType().getMaxDurability());
        }
        restore0(meta);
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
}
