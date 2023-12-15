package org.ricetea.barleyteaapi.internal.bridge;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.translation.Translator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.i18n.GlobalTranslators;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.render.ItemRenderer;
import org.ricetea.barleyteaapi.api.item.render.util.ItemRenderUtil;
import org.ricetea.barleyteaapi.internal.item.renderer.util.AlternativeItemState;
import org.ricetea.barleyteaapi.internal.nms.INMSItemHelper;
import org.ricetea.barleyteaapi.internal.nms.NMSHelperRegister;
import org.ricetea.utils.Converters;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class ProtocolLibBridge {

    private static ProtocolManager protocolManager;
    private static ComponentFallbackInjector fallbackInjector;
    private static ItemStackPrerenderingInjector prerenderingInjector;

    public static void enable() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        ObjectUtil.safeCall(fallbackInjector, protocolManager::removePacketListener);
        ObjectUtil.safeCall(prerenderingInjector, protocolManager::removePacketListener);
        fallbackInjector = new ComponentFallbackInjector();
        prerenderingInjector = new ItemStackPrerenderingInjector();
        ObjectUtil.safeCall(prerenderingInjector, protocolManager::addPacketListener);
        ObjectUtil.safeCall(fallbackInjector, protocolManager::addPacketListener);
        ProtocolLibBridge.protocolManager = protocolManager;
    }

    public static void disable() {
        ObjectUtil.safeCall(fallbackInjector, protocolManager::removePacketListener);
        ObjectUtil.safeCall(prerenderingInjector, protocolManager::removePacketListener);
        fallbackInjector = null;
        prerenderingInjector = null;
    }

    private static class ItemStackPrerenderingInjector extends PacketAdapter {
        public ItemStackPrerenderingInjector() {
            super(BarleyTeaAPI.getInstance(), ListenerPriority.HIGHEST,
                    PacketType.Play.Server.WINDOW_ITEMS,
                    PacketType.Play.Server.OPEN_WINDOW_MERCHANT,
                    PacketType.Play.Server.SET_SLOT,
                    PacketType.Play.Server.ENTITY_EQUIPMENT,
                    PacketType.Play.Server.SYSTEM_CHAT,
                    PacketType.Play.Server.DISGUISED_CHAT,
                    PacketType.Play.Client.SET_CREATIVE_SLOT);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            if (event == null)
                return;
            PacketType packetType = event.getPacketType();
            if (packetType == null || packetType.isClient())
                return;
            PacketContainer container = event.getPacket();
            Player player = event.getPlayer();
            {
                StructureModifier<Component> modifier = container.getSpecificModifier(Component.class);
                int size = modifier.size();
                if (size <= 0) {
                    StructureModifier<WrappedChatComponent> legacyModifier = container.getChatComponents();
                    JSONComponentSerializer serializer = JSONComponentSerializer.json();
                    size = legacyModifier.size();
                    for (int i = 0; i < size; i++) {
                        legacyModifier.modify(i, rawComponent -> {
                            Component component = serializer.deserialize(rawComponent.getJson());
                            if (component != null) {
                                rawComponent.setJson(serializer.serialize(
                                        Objects.requireNonNull(
                                                searchHoverEventAndRender(component, player))));
                            }
                            return rawComponent;
                        });
                    }
                } else {
                    for (int i = 0; i < size; i++) {
                        modifier.modify(i, component -> searchHoverEventAndRender(component, player));
                    }
                }
            }
            {
                StructureModifier<ItemStack> modifier = container.getItemModifier();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, itemStack -> renderItem(itemStack, player));
                }
            }
            if (packetType.equals(PacketType.Play.Server.ENTITY_EQUIPMENT)) {
                StructureModifier<List<Pair<ItemSlot, ItemStack>>> modifier = container.getSlotStackPairLists();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, itemStackList -> {
                        itemStackList.forEach(pair -> pair
                                .setSecond(renderItem(pair.getSecond(), player)));
                        return itemStackList;
                    });
                }
            } else if (packetType.equals(PacketType.Play.Server.OPEN_WINDOW_MERCHANT)) {
                StructureModifier<List<MerchantRecipe>> modifier = container.getMerchantRecipeLists();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, recipeList -> {
                        recipeList
                                .replaceAll(recipe -> {
                                    recipe.setIngredients(
                                            recipe.getIngredients()
                                                    .stream()
                                                    .map(itemStack -> renderItem(itemStack, player))
                                                    .toList());
                                    return recipe;
                                });
                        return recipeList;
                    });
                }
            } else {
                StructureModifier<List<ItemStack>> modifier = container.getItemListModifier();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, itemStackList -> {
                        itemStackList
                                .replaceAll(itemStack -> renderItem(itemStack, player));
                        return itemStackList;
                    });
                }
            }

        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            if (event == null)
                return;
            PacketType packetType = event.getPacketType();
            if (packetType == null || packetType.isServer())
                return;
            PacketContainer container = event.getPacket();
            StructureModifier<ItemStack> itemModifier = container.getItemModifier();
            for (int i = 0, size = itemModifier.size(); i < size; i++) {
                itemModifier.modify(i, AlternativeItemState::restore);
            }
        }

        @Nonnull
        private static ItemStack renderItem(@Nonnull ItemStack itemStack, @Nonnull Player player) {
            ItemRenderer renderer = ItemRenderUtil.getLastRenderer(itemStack);
            if (renderer == null && BaseItem.isBarleyTeaItem(itemStack)) {
                renderer = ItemRenderer.getDefault();
            }
            if (renderer != null)
                itemStack = renderer.render(itemStack, player);
            return itemStack;
        }

        @Nonnull
        private static Component renderHoverEvent(@Nonnull Component component, @Nonnull Player player) {
            HoverEvent<?> hoverEvent = component.hoverEvent();
            if (hoverEvent != null && hoverEvent.value() instanceof ShowItem showItem) {
                BinaryTagHolder nbtHolder = showItem.nbt();
                String rawNbt;
                if (nbtHolder == null)
                    rawNbt = "{\"id\":\"" + showItem.item() + "\", \"Count\":\"" + showItem.count() + "\"}";
                else {
                    rawNbt = "{\"id\":\"" + showItem.item() + "\", \"Count\":" + showItem.count() + ", \"tag\": "
                            + nbtHolder.string() + "}";
                }
                INMSItemHelper helper = NMSHelperRegister.getHelper(INMSItemHelper.class);
                if (helper != null) {
                    ItemStack itemStack = helper.createItemStackFromNbtString(rawNbt);
                    if (itemStack != null) {
                        itemStack = renderItem(itemStack, player);
                        return itemStack.displayName().hoverEvent(itemStack.asHoverEvent());
                    }
                }
            }
            return component;
        }

        @Nullable
        private static Component searchHoverEventAndRender(@Nullable Component component, @Nonnull Player player) {
            if (component == null)
                return null;
            Component result = component;
            HoverEvent<?> hoverEvent = result.hoverEvent();
            if (hoverEvent != null) {
                result = renderHoverEvent(result, player);
            }
            if (result instanceof TranslatableComponent translatable) {
                result = translatable.args(translatable.args()
                        .stream()
                        .map(arg -> searchHoverEventAndRender(arg, player))
                        .toList());
            }
            return result
                    .children(result.children()
                            .stream()
                            .map(arg -> searchHoverEventAndRender(arg, player))
                            .toList());
        }
    }

    private static class ComponentFallbackInjector extends PacketAdapter {

        public ComponentFallbackInjector() {
            super(BarleyTeaAPI.getInstance(), ListenerPriority.HIGHEST,
                    PacketType.Play.Server.OPEN_WINDOW,
                    PacketType.Play.Server.WINDOW_ITEMS,
                    PacketType.Play.Server.OPEN_WINDOW_MERCHANT,
                    PacketType.Play.Server.SET_SLOT,
                    PacketType.Play.Server.ENTITY_EQUIPMENT,
                    PacketType.Play.Server.SYSTEM_CHAT,
                    PacketType.Play.Server.DISGUISED_CHAT,
                    PacketType.Play.Client.SET_CREATIVE_SLOT);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            if (event == null)
                return;
            PacketType packetType = event.getPacketType();
            if (packetType == null || packetType.isClient())
                return;
            PacketContainer container = event.getPacket();
            Locale locale = event.getPlayer().locale();
            Translator translator = GlobalTranslators.getInstance().getServerTranslator();
            {
                StructureModifier<Component> modifier = container.getSpecificModifier(Component.class);
                int size = modifier.size();
                if (size <= 0) {
                    StructureModifier<WrappedChatComponent> legacyModifier = container.getChatComponents();
                    JSONComponentSerializer serializer = JSONComponentSerializer.json();
                    size = legacyModifier.size();
                    for (int i = 0; i < size; i++) {
                        legacyModifier.modify(i, rawComponent -> {
                            Component component = serializer.deserialize(rawComponent.getJson());
                            if (component != null) {
                                rawComponent.setJson(serializer.serialize(
                                        Objects.requireNonNull(
                                                applyTranslateFallbacks(translator, component, locale))));
                            }
                            return rawComponent;
                        });
                    }
                } else {
                    for (int i = 0; i < size; i++) {
                        modifier.modify(i, component -> applyTranslateFallbacks(translator, component, locale));
                    }
                }
            }
            {
                StructureModifier<ItemStack> modifier = container.getItemModifier();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, itemStack -> applyTranslateFallbacks(translator, itemStack, locale));
                }
            }
            if (packetType.equals(PacketType.Play.Server.ENTITY_EQUIPMENT)) {
                StructureModifier<List<Pair<ItemSlot, ItemStack>>> modifier = container.getSlotStackPairLists();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, itemStackList -> {
                        itemStackList.forEach(pair -> pair
                                .setSecond(applyTranslateFallbacks(translator, pair.getSecond(), locale)));
                        return itemStackList;
                    });
                }
            } else if (packetType.equals(PacketType.Play.Server.OPEN_WINDOW_MERCHANT)) {
                StructureModifier<List<MerchantRecipe>> modifier = container.getMerchantRecipeLists();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, recipeList -> {
                        recipeList
                                .replaceAll(recipe -> {
                                    recipe.setIngredients(
                                            recipe.getIngredients()
                                                    .stream()
                                                    .map(itemstack -> applyTranslateFallbacks(translator, itemstack,
                                                            locale))
                                                    .toList());
                                    return recipe;
                                });
                        return recipeList;
                    });
                }
            } else {
                StructureModifier<List<ItemStack>> modifier = container.getItemListModifier();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, itemStackList -> {
                        itemStackList
                                .replaceAll(itemStack -> applyTranslateFallbacks(translator, itemStack, locale));
                        return itemStackList;
                    });
                }
            }

        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            if (event == null)
                return;
            PacketType packetType = event.getPacketType();
            if (packetType == null || packetType.isServer())
                return;
            PacketContainer container = event.getPacket();
            StructureModifier<ItemStack> itemModifier = container.getItemModifier();
            for (int i = 0, size = itemModifier.size(); i < size; i++) {
                itemModifier.modify(i, ComponentFallbackInjector::eraseTranslateFallbacks);
            }
        }

        @Nonnull
        private static ItemStack applyTranslateFallbacks(@Nonnull Translator translator, @Nonnull ItemStack itemStack,
                                                         @Nonnull Locale locale) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                boolean isDirty = false;
                Component displayName = meta.displayName();
                if (displayName != null) {
                    meta.displayName(applyTranslateFallbacks(translator, displayName, locale));
                    isDirty = true;
                }
                List<Component> lore = meta.lore();
                if (lore != null) {
                    meta.lore(lore.stream()
                            .map(loreLine -> applyTranslateFallbacks(translator, loreLine, locale))
                            .toList());
                    isDirty = true;
                }
                if (isDirty)
                    itemStack.setItemMeta(meta);
            }
            return itemStack;
        }

        @Nonnull
        private static ItemStack eraseTranslateFallbacks(@Nonnull ItemStack itemStack) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                boolean isDirty = false;
                Component displayName = meta.displayName();
                if (displayName != null) {
                    meta.displayName(eraseTranslateFallbacks(displayName));
                    isDirty = true;
                }
                List<Component> lore = meta.lore();
                if (lore != null) {
                    meta.lore(lore.stream()
                            .map(ComponentFallbackInjector::eraseTranslateFallbacks)
                            .toList());
                    isDirty = true;
                }
                if (isDirty)
                    itemStack.setItemMeta(meta);
            }
            return itemStack;
        }

        @Nullable
        private static Component applyTranslateFallbacks(@Nonnull Translator translator, @Nullable Component component,
                                                         @Nonnull Locale locale) {
            if (component == null)
                return null;
            Component result;
            if (component instanceof TranslatableComponent translatableComponent) {
                TranslatableComponent translatableResult = translatableComponent;
                if (translatableComponent.fallback() == null) {
                    MessageFormat format = translator.translate(translatableComponent.key(), locale);
                    if (format != null)
                        translatableResult = translatableResult.fallback(Converters.toStringFormat(format));
                }
                translatableResult = translatableResult
                        .args(translatableResult.args()
                                .stream()
                                .map(arg -> applyTranslateFallbacks(translator, arg, locale))
                                .toList());
                result = translatableResult;
            } else {
                result = component;
            }
            HoverEvent<?> hoverEvent = result.hoverEvent();
            if (hoverEvent != null) {
                result = processChatMessageHoverEvents(translator, result, locale);
            }
            return result
                    .children(component.children()
                            .stream()
                            .map(arg -> applyTranslateFallbacks(translator, arg, locale))
                            .toList());
        }

        @Nonnull
        private static Component processChatMessageHoverEvents(@Nonnull Translator translator,
                                                               @Nonnull Component component, @Nonnull Locale locale) {
            HoverEvent<?> hoverEvent = component.hoverEvent();
            if (hoverEvent != null && hoverEvent.value() instanceof ShowItem showItem) {
                BinaryTagHolder nbtHolder = showItem.nbt();
                String rawNbt;
                if (nbtHolder == null)
                    rawNbt = "{\"id\":\"" + showItem.item() + "\", \"Count\":\"" + showItem.count() + "\"}";
                else {
                    rawNbt = "{\"id\":\"" + showItem.item() + "\", \"Count\":" + showItem.count() + ", \"tag\": "
                            + nbtHolder.string() + "}";
                }
                INMSItemHelper helper = NMSHelperRegister.getHelper(INMSItemHelper.class);
                if (helper != null) {
                    ItemStack itemStack = helper.createItemStackFromNbtString(rawNbt);
                    if (itemStack != null) {
                        return component.hoverEvent(applyTranslateFallbacks(translator, itemStack, locale).asHoverEvent());
                    }
                }
            }
            return component;
        }

        @Nonnull
        private static Component eraseTranslateFallbacks(@Nonnull Component component) {
            Component result;
            if (component instanceof TranslatableComponent translatableComponent) {
                TranslatableComponent translatableResult = translatableComponent.fallback(null);
                translatableResult = translatableResult
                        .args(translatableResult.args()
                                .stream()
                                .map(ComponentFallbackInjector::eraseTranslateFallbacks)
                                .toList());
                result = translatableResult;
            } else {
                result = component;
            }
            return result
                    .children(component.children()
                            .stream()
                            .map(ComponentFallbackInjector::eraseTranslateFallbacks)
                            .toList());
        }
    }
}
