package org.ricetea.barleyteaapi.internal.connector;

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
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.i18n.GlobalTranslators;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.render.ItemRenderer;
import org.ricetea.barleyteaapi.api.item.render.util.AlternativeItemState;
import org.ricetea.barleyteaapi.api.item.render.util.ItemRenderHelper;
import org.ricetea.barleyteaapi.internal.nms.INMSItemHelper;
import org.ricetea.barleyteaapi.internal.nms.NMSHelperRegister;
import org.ricetea.barleyteaapi.util.connector.SoftDependConnector;
import org.ricetea.utils.Box;
import org.ricetea.utils.Converters;
import org.ricetea.utils.ObjectUtil;
import org.ricetea.utils.WithFlag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@ApiStatus.Internal
public final class ProtocolLibConnector implements SoftDependConnector {

    private ProtocolManager protocolManager;
    private ComponentFallbackInjector fallbackInjector;
    private ItemStackPrerenderingInjector prerenderingInjector;

    @Nonnull
    @Override
    public String getPluginName() {
        return BulitInSoftDepend.ProtocolLib.getPluginName();
    }

    @Override
    public void onEnable(@Nonnull Plugin plugin) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        ObjectUtil.safeCall(fallbackInjector, protocolManager::removePacketListener);
        ObjectUtil.safeCall(prerenderingInjector, protocolManager::removePacketListener);
        fallbackInjector = new ComponentFallbackInjector();
        prerenderingInjector = new ItemStackPrerenderingInjector();
        ObjectUtil.safeCall(prerenderingInjector, protocolManager::addPacketListener);
        ObjectUtil.safeCall(fallbackInjector, protocolManager::addPacketListener);
        this.protocolManager = protocolManager;
    }

    @Override
    public void onDisable() {
        ObjectUtil.safeCall(fallbackInjector, protocolManager::removePacketListener);
        ObjectUtil.safeCall(prerenderingInjector, protocolManager::removePacketListener);
        fallbackInjector = null;
        prerenderingInjector = null;
    }

    private static class ItemStackPrerenderingInjector extends PacketAdapter {
        public ItemStackPrerenderingInjector() {
            super(BarleyTeaAPI.getInstance(), ListenerPriority.LOWEST,
                    PacketType.Play.Server.WINDOW_ITEMS,
                    PacketType.Play.Server.OPEN_WINDOW_MERCHANT,
                    PacketType.Play.Server.SET_SLOT,
                    PacketType.Play.Server.ENTITY_EQUIPMENT,
                    PacketType.Play.Server.SYSTEM_CHAT,
                    PacketType.Play.Server.DISGUISED_CHAT,
                    PacketType.Play.Client.SET_CREATIVE_SLOT);
        }

        @Nullable
        private static WithFlag<ItemStack> renderItem(@Nullable ItemStack itemStack, @Nonnull Player player) {
            if (itemStack == null)
                return null;
            boolean modified = false;
            ItemRenderer renderer = ItemRenderHelper.getLastRenderer(itemStack);
            if (renderer == null && ItemHelper.isCustomItem(itemStack)) {
                renderer = ItemRenderer.getDefault();
            }
            if (renderer != null) {
                itemStack = renderer.render(itemStack, player);
                modified = true;
            }
            if (itemStack.getItemMeta() instanceof BlockStateMeta blockMeta) {
                boolean modified2 = false;
                if (blockMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
                    var inventory = shulkerBox.getInventory();
                    Box<Boolean> flag = Box.box(false);
                    for (var iterator = inventory.iterator(); iterator.hasNext(); ) {
                        WithFlag<ItemStack> result = renderItem(iterator.next(), player);
                        if (result != null && result.flag()) {
                            flag.set(true);
                            iterator.set(result.obj());
                        }
                    }
                    modified2 = ObjectUtil.letNonNull(flag.get(), false);
                    if (modified2) {
                        blockMeta.setBlockState(shulkerBox);
                    }
                }
                if (modified2) {
                    itemStack.setItemMeta(blockMeta);
                }
                modified |= modified2;
            }
            return new WithFlag<>(itemStack, modified);
        }

        @Nullable
        private static ItemStack restoreItem(@Nullable ItemStack itemStack) {
            if (itemStack == null)
                return null;
            ItemRenderer renderer = ItemRenderHelper.getLastRenderer(itemStack);
            if (renderer == null && ItemHelper.isCustomItem(itemStack)) {
                renderer = ItemRenderer.getDefault();
            }
            if (renderer != null) {
                AlternativeItemState.restore(itemStack);
            }
            if (itemStack.getItemMeta() instanceof BlockStateMeta blockMeta && blockMeta.hasBlockState()) {
                if (blockMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
                    var inventory = shulkerBox.getInventory();
                    for (var iterator = inventory.iterator(); iterator.hasNext(); ) {
                        ItemStack item = iterator.next();
                        iterator.set(restoreItem(item));
                    }
                    blockMeta.setBlockState(shulkerBox);
                }
                itemStack.setItemMeta(blockMeta);
            }
            return itemStack;
        }

        @Nonnull
        private static Component renderHoverEvent(@Nonnull Component component, @Nonnull Player player) {
            HoverEvent<?> hoverEvent = component.hoverEvent();
            if (hoverEvent != null && hoverEvent.value() instanceof ShowItem showItem) {
                BinaryTagHolder nbtHolder = showItem.nbt();
                if (nbtHolder == null)
                    return component;
                String rawNbt = "{\"id\":\"" + showItem.item() + "\", \"Count\":" + showItem.count() + ", \"tag\": "
                        + nbtHolder.string() + "}";
                INMSItemHelper helper = NMSHelperRegister.getHelper(INMSItemHelper.class);
                if (helper != null) {
                    ItemStack itemStack = helper.createItemStackFromNbtString(rawNbt);
                    if (itemStack != null) {
                        var flag = renderItem(itemStack, player);
                        if (flag != null && flag.flag()) {
                            itemStack = flag.obj();
                            return itemStack.displayName()
                                    .hoverEvent(itemStack.asHoverEvent())
                                    .children(component.children());
                        }
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
                            rawComponent.setJson(serializer.serialize(
                                    Objects.requireNonNull(
                                            searchHoverEventAndRender(component, player))));
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
                    modifier.modify(i, itemStack -> ObjectUtil.safeMap(renderItem(itemStack, player), WithFlag::obj));
                }
            }
            if (packetType.equals(PacketType.Play.Server.ENTITY_EQUIPMENT)) {
                StructureModifier<List<Pair<ItemSlot, ItemStack>>> modifier = container.getSlotStackPairLists();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, itemStackList -> {
                        itemStackList.forEach(pair -> pair.setSecond(
                                ObjectUtil.safeMap(renderItem(pair.getSecond(), player), WithFlag::obj)));
                        return itemStackList;
                    });
                }
            } else if (packetType.equals(PacketType.Play.Server.OPEN_WINDOW_MERCHANT)) {
                StructureModifier<List<MerchantRecipe>> modifier = container.getMerchantRecipeLists();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, recipeList -> {
                        recipeList
                                .replaceAll(recipe -> {
                                    Box<Boolean> flagBox = Box.box(false);
                                    List<ItemStack> newIngredients = recipe.getIngredients()
                                            .stream()
                                            .map(itemStack -> {
                                                WithFlag<ItemStack> newItem = renderItem(itemStack, player);
                                                if (newItem != null && newItem.flag()) {
                                                    flagBox.set(true);
                                                }
                                                return ObjectUtil.safeMap(newItem, WithFlag::obj);
                                            })
                                            .toList();
                                    ItemStack oldResult = recipe.getResult();
                                    WithFlag<ItemStack> newResult = renderItem(oldResult, player);
                                    if (newResult != null && newResult.flag()) {
                                        MerchantRecipe newRecipe = new MerchantRecipe(newResult.obj(), recipe.getUses(), recipe.getMaxUses(),
                                                recipe.hasExperienceReward(), recipe.getVillagerExperience(), recipe.getPriceMultiplier(),
                                                recipe.getDemand(), recipe.getSpecialPrice(), recipe.shouldIgnoreDiscounts());
                                        newRecipe.setIngredients(newIngredients);
                                        return newRecipe;
                                    } else {
                                        if (Boolean.TRUE.equals(flagBox.get())) {
                                            recipe.setIngredients(newIngredients);
                                        }
                                    }
                                    return recipe;
                                });
                        return recipeList;
                    });
                }
            } else {
                StructureModifier<List<ItemStack>> modifier = container.getItemListModifier();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, itemStackList -> {
                        itemStackList.replaceAll(itemStack ->
                                ObjectUtil.safeMap(renderItem(itemStack, player), WithFlag::obj));
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
                itemModifier.modify(i, ItemStackPrerenderingInjector::restoreItem);
            }
            StructureModifier<List<ItemStack>> itemListModifier = container.getItemListModifier();
            for (int i = 0, size = itemListModifier.size(); i < size; i++) {
                itemListModifier.modify(i, list -> {
                    list.replaceAll(ItemStackPrerenderingInjector::restoreItem);
                    return list;
                });
            }
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

        @Nullable
        private static WithFlag<ItemStack> applyTranslateFallbacks(@Nonnull Translator translator, @Nullable ItemStack itemStack,
                                                                   @Nonnull Locale locale) {
            if (itemStack == null)
                return null;
            ItemMeta meta = itemStack.getItemMeta();
            boolean isDirty = false;
            if (meta != null) {
                Component displayName = ObjectUtil.tryMapSilently(meta::displayName);
                if (displayName != null) {
                    meta.displayName(applyTranslateFallbacks(translator, displayName, locale));
                    isDirty = true;
                }
                List<Component> lore = ObjectUtil.tryMapSilently(meta::lore);
                if (lore != null) {
                    meta.lore(lore.stream()
                            .map(loreLine -> applyTranslateFallbacks(translator, loreLine, locale))
                            .toList());
                    isDirty = true;
                }
                if (meta instanceof BlockStateMeta blockMeta) {
                    if (blockMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
                        var inventory = shulkerBox.getInventory();
                        Box<Boolean> flag = Box.box(false);
                        for (var iterator = inventory.iterator(); iterator.hasNext(); ) {
                            WithFlag<ItemStack> result = applyTranslateFallbacks(translator, iterator.next(), locale);
                            if (result != null && result.flag()) {
                                flag.set(true);
                                iterator.set(result.obj());
                            }
                        }
                        if (!isDirty)
                            isDirty = ObjectUtil.letNonNull(flag.get(), false);
                        blockMeta.setBlockState(shulkerBox);
                    }
                }
                if (isDirty) {
                    itemStack.setItemMeta(meta);
                }
            }
            return new WithFlag<>(itemStack, isDirty);
        }

        @Nullable
        private static ItemStack eraseTranslateFallbacks(@Nullable ItemStack itemStack) {
            if (itemStack == null)
                return null;
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
                if (itemStack.getItemMeta() instanceof BlockStateMeta blockMeta) {
                    if (blockMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
                        var inventory = shulkerBox.getInventory();
                        for (var iterator = inventory.iterator(); iterator.hasNext(); ) {
                            ItemStack item = iterator.next();
                            iterator.set(eraseTranslateFallbacks(item));
                        }
                        isDirty = true;
                        blockMeta.setBlockState(shulkerBox);
                    }
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
                if (nbtHolder == null)
                    return component;
                String rawNbt = "{\"id\":\"" + showItem.item() + "\", \"Count\":" + showItem.count() + ", \"tag\": "
                        + nbtHolder.string() + "}";
                INMSItemHelper helper = NMSHelperRegister.getHelper(INMSItemHelper.class);
                if (helper != null) {
                    ItemStack itemStack = helper.createItemStackFromNbtString(rawNbt);
                    if (itemStack != null) {
                        var flag = applyTranslateFallbacks(translator, itemStack, locale);
                        if (flag != null && flag.flag()) {
                            itemStack = flag.obj();
                            return itemStack.displayName()
                                    .hoverEvent(itemStack.asHoverEvent())
                                    .children(component.children());
                        }
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
                            rawComponent.setJson(serializer.serialize(
                                    Objects.requireNonNull(
                                            applyTranslateFallbacks(translator, component, locale))));
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
                    modifier.modify(i, itemStack ->
                            ObjectUtil.safeMap(applyTranslateFallbacks(translator, itemStack, locale), WithFlag::obj));
                }
            }
            if (packetType.equals(PacketType.Play.Server.ENTITY_EQUIPMENT)) {
                StructureModifier<List<Pair<ItemSlot, ItemStack>>> modifier = container.getSlotStackPairLists();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, itemStackList -> {
                        itemStackList.forEach(pair -> pair.setSecond(
                                ObjectUtil.safeMap(applyTranslateFallbacks(translator, pair.getSecond(), locale), WithFlag::obj)));
                        return itemStackList;
                    });
                }
            } else if (packetType.equals(PacketType.Play.Server.OPEN_WINDOW_MERCHANT)) {
                StructureModifier<List<MerchantRecipe>> modifier = container.getMerchantRecipeLists();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, recipeList -> {
                        recipeList
                                .replaceAll(recipe -> {
                                    Box<Boolean> flagBox = Box.box(false);
                                    List<ItemStack> newIngredients = recipe.getIngredients()
                                            .stream()
                                            .map(itemStack -> {
                                                WithFlag<ItemStack> newItem = applyTranslateFallbacks(translator, itemStack, locale);
                                                if (newItem != null && newItem.flag())
                                                    flagBox.set(true);
                                                return ObjectUtil.safeMap(newItem, WithFlag::obj);
                                            })
                                            .toList();
                                    ItemStack oldResult = recipe.getResult();
                                    WithFlag<ItemStack> newResult = applyTranslateFallbacks(translator, oldResult, locale);
                                    if (newResult != null && newResult.flag()) {
                                        MerchantRecipe newRecipe = new MerchantRecipe(newResult.obj(), recipe.getUses(), recipe.getMaxUses(),
                                                recipe.hasExperienceReward(), recipe.getVillagerExperience(), recipe.getPriceMultiplier(),
                                                recipe.getDemand(), recipe.getSpecialPrice(), recipe.shouldIgnoreDiscounts());
                                        newRecipe.setIngredients(newIngredients);
                                        return newRecipe;
                                    } else {
                                        if (Boolean.TRUE.equals(flagBox.get())) {
                                            recipe.setIngredients(newIngredients);
                                        }
                                    }
                                    return recipe;
                                });

                        return recipeList;
                    });
                }
            } else {
                StructureModifier<List<ItemStack>> modifier = container.getItemListModifier();
                for (int i = 0, size = modifier.size(); i < size; i++) {
                    modifier.modify(i, itemStackList -> {
                        itemStackList.replaceAll(itemStack ->
                                ObjectUtil.safeMap(applyTranslateFallbacks(translator, itemStack, locale), WithFlag::obj));
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
    }
}
