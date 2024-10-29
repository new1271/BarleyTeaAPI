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
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
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
import org.ricetea.barleyteaapi.api.internal.nms.INMSItemHelper;
import org.ricetea.barleyteaapi.api.internal.nms.NMSVersion;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.render.ItemRenderer;
import org.ricetea.barleyteaapi.api.item.render.util.ItemRenderHelper;
import org.ricetea.barleyteaapi.internal.connector.patch.ApplyTranslateFallbacksFunction;
import org.ricetea.barleyteaapi.internal.connector.patch.ProtocolLibConnectorPatch;
import org.ricetea.barleyteaapi.util.connector.SoftDependConnector;
import org.ricetea.utils.Box;
import org.ricetea.utils.Cache;
import org.ricetea.utils.ObjectUtil;
import org.ricetea.utils.WithFlag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ApiStatus.Internal
public final class ProtocolLibConnector implements SoftDependConnector {

    private final List<ProtocolLibConnectorPatch> patchList = new ArrayList<>();
    private final ReadWriteLock patchListLock = new ReentrantReadWriteLock();
    private final Cache<Collection<ProtocolLibConnectorPatch>> patchesCache =
            Cache.createThreadSafe(this::getPatchesReal);
    private ProtocolManager protocolManager;
    private ComponentFallbackInjector fallbackInjector;
    private ItemStackPrerenderingInjector prerenderingInjector;

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


    public void addPatch(@Nonnull ProtocolLibConnectorPatch patch) {
        Lock lock = patchListLock.writeLock();
        lock.lock();
        patchList.add(patch);
        patchesCache.reset();
        lock.unlock();
    }

    public void removePatch(@Nonnull ProtocolLibConnectorPatch patch) {
        Lock lock = patchListLock.writeLock();
        lock.lock();
        patchList.remove(patch);
        patchesCache.reset();
        lock.unlock();
    }

    @Nonnull
    public Collection<ProtocolLibConnectorPatch> getPatches() {
        return patchesCache.get();
    }

    @Nonnull
    private Collection<ProtocolLibConnectorPatch> getPatchesReal() {
        Lock lock = patchListLock.readLock();
        lock.lock();
        Collection<ProtocolLibConnectorPatch> result = ImmutableList.copyOf(patchList);
        lock.unlock();
        return result;
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

        @Override
        public void onPacketSending(PacketEvent event) {
            if (event == null)
                return;
            PacketType packetType = event.getPacketType();
            if (packetType == null || packetType.isClient())
                return;
            PacketContainer container = event.getPacket();
            Player player = event.getPlayer();
            if (packetType.equals(PacketType.Play.Server.WINDOW_ITEMS)) {
                onPacketSending_ItemStackList(player, container);
                onPacketSending_ItemStack(player, container);
                return;
            }
            if (packetType.equals(PacketType.Play.Server.OPEN_WINDOW_MERCHANT)) {
                onPacketSending_MerchantRecipeList(player, container);
                return;
            }
            if (packetType.equals(PacketType.Play.Server.SET_SLOT)) {
                onPacketSending_ItemStack(player, container);
                return;
            }
            if (packetType.equals(PacketType.Play.Server.ENTITY_EQUIPMENT)) {
                if (NMSVersion.getCurrent().getVersion() < NMSVersion.v1_20_R4.getVersion())
                    onPacketSending_Equipment(player, container);
                return;
            }
            if (packetType.equals(PacketType.Play.Server.SYSTEM_CHAT) ||
                    packetType.equals(PacketType.Play.Server.DISGUISED_CHAT)) {
                onPacketSending_Component(player, container);
                return;
            }
        }

        private static void onPacketSending_Component(@Nonnull Player player, @Nonnull PacketContainer container) {
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

        private static void onPacketSending_ItemStack(@Nonnull Player player, @Nonnull PacketContainer container) {
            StructureModifier<ItemStack> modifier = container.getItemModifier();
            for (int i = 0, size = modifier.size(); i < size; i++) {
                modifier.modify(i, itemStack -> ObjectUtil.safeMap(ItemHelper.renderUnsafe(itemStack, player), WithFlag::obj));
            }
        }

        private static void onPacketSending_ItemStackList(@Nonnull Player player, @Nonnull PacketContainer container) {
            StructureModifier<List<ItemStack>> modifier = container.getItemListModifier();
            for (int i = 0, size = modifier.size(); i < size; i++) {
                modifier.modify(i, itemStackList -> {
                    itemStackList.replaceAll(itemStack ->
                            ObjectUtil.safeMap(ItemHelper.renderUnsafe(itemStack, player), WithFlag::obj));
                    return itemStackList;
                });
            }
        }

        private static void onPacketSending_Equipment(@Nonnull Player player, @Nonnull PacketContainer container) {
            StructureModifier<List<Pair<ItemSlot, ItemStack>>> modifier = container.getSlotStackPairLists();
            for (int i = 0, size = modifier.size(); i < size; i++) {
                modifier.modify(i, itemStackList -> {
                    itemStackList.forEach(pair -> pair.setSecond(
                            ObjectUtil.safeMap(ItemHelper.renderUnsafe(pair.getSecond(), player), WithFlag::obj)));
                    return itemStackList;
                });
            }
        }

        private static void onPacketSending_MerchantRecipeList(@Nonnull Player player, @Nonnull PacketContainer container) {
            StructureModifier<List<MerchantRecipe>> modifier = container.getMerchantRecipeLists();
            for (int i = 0, size = modifier.size(); i < size; i++) {
                modifier.modify(i, recipeList -> {
                    recipeList
                            .replaceAll(recipe -> {
                                Box<Boolean> flagBox = Box.box(false);
                                List<ItemStack> newIngredients = recipe.getIngredients()
                                        .stream()
                                        .map(itemStack -> {
                                            if (itemStack == null)
                                                return null;
                                            WithFlag<ItemStack> newItem = ItemHelper.render(itemStack, player);
                                            if (newItem.flag()) {
                                                flagBox.set(true);
                                            }
                                            return newItem.obj();
                                        })
                                        .toList();
                                ItemStack oldResult = recipe.getResult();
                                WithFlag<ItemStack> newResult = ItemHelper.render(oldResult, player);
                                if (newResult.flag()) {
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
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            if (event == null)
                return;
            PacketType packetType = event.getPacketType();
            if (packetType == null || packetType.isServer())
                return;
            Player player = event.getPlayer();
            PacketContainer container = event.getPacket();
            if (packetType.equals(PacketType.Play.Client.SET_CREATIVE_SLOT)) {
                onPacketReceiving_ItemStack(player, container);
                return;
            }
        }

        private static void onPacketReceiving_ItemStack(@Nonnull Player player, @Nonnull PacketContainer container) {
            StructureModifier<ItemStack> itemModifier = container.getItemModifier();
            for (int i = 0, size = itemModifier.size(); i < size; i++) {
                itemModifier.modify(i, val -> restoreItem(val, player));
            }
        }

        @Nullable
        private static ItemStack restoreItem(@Nullable ItemStack itemStack, @Nullable Player player) {
            if (itemStack == null)
                return null;
            ItemRenderer renderer = ItemRenderHelper.getLastRenderer(itemStack);
            if (renderer == null && ItemHelper.isCustomItem(itemStack)) {
                renderer = ItemRenderer.getDefault();
            }
            if (renderer != null) {
                itemStack = renderer.restore(itemStack, player);
            }
            if (itemStack.getItemMeta() instanceof BlockStateMeta blockMeta && blockMeta.hasBlockState()) {
                if (blockMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
                    var inventory = shulkerBox.getInventory();
                    for (var iterator = inventory.iterator(); iterator.hasNext(); ) {
                        ItemStack item = iterator.next();
                        iterator.set(restoreItem(item, player));
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
                INMSItemHelper nmsItemHelper = INMSItemHelper.getInstanceUnsafe();
                if (nmsItemHelper == null)
                    return component;
                ItemStack itemStack = nmsItemHelper.createItemStackFromShowItem(showItem);
                if (itemStack == null)
                    return component;
                WithFlag<ItemStack> flag = ItemHelper.render(itemStack, player);
                if (flag.flag()) {
                    itemStack = flag.obj();
                    return itemStack.displayName()
                            .hoverEvent(itemStack.asHoverEvent())
                            .children(component.children());
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

    private class ComponentFallbackInjector extends PacketAdapter {
        private final ApplyTranslateFallbacksFunction applyTranslateFallbacksFunction =
                ConnectorInternals.applyTranslateFallbacks(this::patchTranslateFallbacks);

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
            Player player = event.getPlayer();
            if (packetType.equals(PacketType.Play.Server.WINDOW_ITEMS)) {
                onPacketSending_ItemStackList(player, container);
                onPacketSending_ItemStack(player, container);
                return;
            }
            if (packetType.equals(PacketType.Play.Server.OPEN_WINDOW_MERCHANT)) {
                onPacketSending_MerchantRecipeList(player, container);
                return;
            }
            if (packetType.equals(PacketType.Play.Server.SET_SLOT)) {
                onPacketSending_ItemStack(player, container);
                return;
            }
            if (packetType.equals(PacketType.Play.Server.ENTITY_EQUIPMENT)) {
                if (NMSVersion.getCurrent().getVersion() < NMSVersion.v1_20_R4.getVersion())
                    onPacketSending_Equipment(player, container);
                return;
            }
            if (packetType.equals(PacketType.Play.Server.OPEN_WINDOW) ||
                    packetType.equals(PacketType.Play.Server.SYSTEM_CHAT) ||
                    packetType.equals(PacketType.Play.Server.DISGUISED_CHAT)) {
                onPacketSending_Component(player, container);
                return;
            }
        }

        private void onPacketSending_Component(@Nonnull Player player, @Nonnull PacketContainer container) {
            GlobalTranslator translator = GlobalTranslator.translator();
            Locale locale = player.locale();
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
                                        applyTranslateFallbacksFunction.apply(translator, component, locale))));
                        return rawComponent;
                    });
                }
            } else {
                for (int i = 0; i < size; i++) {
                    modifier.modify(i, component -> applyTranslateFallbacksFunction.apply(translator, component, locale));
                }
            }
        }

        private void onPacketSending_ItemStack(@Nonnull Player player, @Nonnull PacketContainer container) {
            GlobalTranslator translator = GlobalTranslator.translator();
            Locale locale = player.locale();
            StructureModifier<ItemStack> modifier = container.getItemModifier();
            for (int i = 0, size = modifier.size(); i < size; i++) {
                modifier.modify(i, itemStack ->
                        ObjectUtil.safeMap(ConnectorInternals.applyTranslateFallbacks(translator, itemStack,
                                locale, this::patchTranslateFallbacks), WithFlag::obj));
            }
        }

        private void onPacketSending_ItemStackList(@Nonnull Player player, @Nonnull PacketContainer container) {
            GlobalTranslator translator = GlobalTranslator.translator();
            Locale locale = player.locale();
            StructureModifier<List<ItemStack>> modifier = container.getItemListModifier();
            for (int i = 0, size = modifier.size(); i < size; i++) {
                modifier.modify(i, itemStackList -> {
                    itemStackList.replaceAll(itemStack ->
                            ObjectUtil.safeMap(ConnectorInternals.applyTranslateFallbacks(translator, itemStack,
                                    locale, this::patchTranslateFallbacks), WithFlag::obj));
                    return itemStackList;
                });
            }
        }

        private void onPacketSending_Equipment(@Nonnull Player player, @Nonnull PacketContainer container) {
            GlobalTranslator translator = GlobalTranslator.translator();
            Locale locale = player.locale();
            StructureModifier<List<Pair<ItemSlot, ItemStack>>> modifier = container.getSlotStackPairLists();
            for (int i = 0, size = modifier.size(); i < size; i++) {
                modifier.modify(i, itemStackList -> {
                    itemStackList.forEach(pair -> pair.setSecond(ObjectUtil.safeMap(
                            ConnectorInternals.applyTranslateFallbacks(translator, pair.getSecond(),
                                    locale, this::patchTranslateFallbacks), WithFlag::obj)));
                    return itemStackList;
                });
            }
        }

        private void onPacketSending_MerchantRecipeList(@Nonnull Player player, @Nonnull PacketContainer container) {
            GlobalTranslator translator = GlobalTranslator.translator();
            Locale locale = player.locale();
            StructureModifier<List<MerchantRecipe>> modifier = container.getMerchantRecipeLists();
            for (int i = 0, size = modifier.size(); i < size; i++) {
                modifier.modify(i, recipeList -> {
                    recipeList
                            .replaceAll(recipe -> {
                                Box<Boolean> flagBox = Box.box(false);
                                List<ItemStack> newIngredients = recipe.getIngredients()
                                        .stream()
                                        .map(itemStack -> {
                                            WithFlag<ItemStack> newItem =
                                                    ConnectorInternals.applyTranslateFallbacks(translator, itemStack,
                                                            locale, this::patchTranslateFallbacks);
                                            if (newItem != null && newItem.flag())
                                                flagBox.set(true);
                                            return ObjectUtil.safeMap(newItem, WithFlag::obj);
                                        })
                                        .toList();
                                ItemStack oldResult = recipe.getResult();
                                WithFlag<ItemStack> newResult =
                                        ConnectorInternals.applyTranslateFallbacks(translator, oldResult, locale,
                                                this::patchTranslateFallbacks);
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
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            if (event == null)
                return;
            PacketType packetType = event.getPacketType();
            if (packetType == null || packetType.isServer())
                return;
            PacketContainer container = event.getPacket();
            if (packetType.equals(PacketType.Play.Client.SET_CREATIVE_SLOT)) {
                onPacketReceiving_ItemStack(container);
                return;
            }
        }

        private void onPacketReceiving_ItemStack(@Nonnull PacketContainer container) {
            StructureModifier<ItemStack> itemModifier = container.getItemModifier();
            for (int i = 0, size = itemModifier.size(); i < size; i++) {
                itemModifier.modify(i, this::eraseTranslateFallbacks);
            }
        }

        private boolean patchTranslateFallbacks(@Nonnull Translator translator, @Nonnull ItemMeta itemMeta,
                                                @Nonnull Locale locale) {
            boolean result = false;
            for (ProtocolLibConnectorPatch patch : getPatches()) {
                result |= patch.afterApplyTranslateFallbacks(translator, itemMeta, locale, applyTranslateFallbacksFunction);
            }
            return result;
        }

        @Nullable
        private ItemStack eraseTranslateFallbacks(@Nullable ItemStack itemStack) {
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
                            .map(this::eraseTranslateFallbacks)
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
                for (ProtocolLibConnectorPatch patch : ProtocolLibConnector.this.getPatches()){
                    isDirty |= patch.afterEraseTranslateFallbacks(meta, this::eraseTranslateFallbacks);
                }
                if (isDirty)
                    itemStack.setItemMeta(meta);
            }
            return itemStack;
        }

        @Nonnull
        private Component eraseTranslateFallbacks(@Nonnull Component component) {
            Component result;
            if (component instanceof TranslatableComponent translatableComponent) {
                TranslatableComponent translatableResult = translatableComponent.fallback(null);
                translatableResult = translatableResult
                        .args(translatableResult.args()
                                .stream()
                                .map(this::eraseTranslateFallbacks)
                                .toList());
                result = translatableResult;
            } else {
                result = component;
            }
            return result
                    .children(component.children()
                            .stream()
                            .map(this::eraseTranslateFallbacks)
                            .toList());
        }
    }
}
