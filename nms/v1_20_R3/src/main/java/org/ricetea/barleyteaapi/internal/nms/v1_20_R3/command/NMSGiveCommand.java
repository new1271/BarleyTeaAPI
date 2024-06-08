package org.ricetea.barleyteaapi.internal.nms.v1_20_R3.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers;
import org.ricetea.barleyteaapi.api.helper.FeatureHelper;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCommandGive;
import org.ricetea.barleyteaapi.api.item.feature.data.DataCommandGive;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R3.helper.NMSItemHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R3.util.MinecraftKeyCombinedIterator;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R3.util.NMSCommandArgument;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R3.util.NMSCommandUtil;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class NMSGiveCommand extends NMSRegularCommand {
    @Nonnull
    private static final SimpleCommandExceptionType giveFailedMessage = new SimpleCommandExceptionType(
            Component.translatable("command.failed"));

    @Nonnull
    private static final Lazy<SuggestionProviderImpl> suggestionProvider = Lazy.create(SuggestionProviderImpl::new);

    public NMSGiveCommand() {
        super(NamespacedKeyUtil.BarleyTeaAPI("givebarley"), NamespacedKeyUtil.BarleyTeaAPI("give2"));
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> prepareCommand(
            @Nonnull LiteralArgumentBuilder<CommandSourceStack> builder) {
        return builder.requires(NMSCommandUtil::needOp)
                .then(NMSCommandUtil.argument("targets", NMSCommandArgument.selectPlayers())
                        .then(NMSCommandUtil.argument("item", NMSCommandArgument.selectMinecraftKey())
                                .suggests(suggestionProvider.get())
                                .executes(context -> execute(context.getSource(),
                                        NMSCommandArgument.decodePlayerArgumentMultiple(context, "targets"),
                                        NMSCommandArgument.decodeMinecraftKey(context, "item"), 1,
                                        null))
                                .then(NMSCommandUtil.argument("count", IntegerArgumentType.integer(1))
                                        .executes(context -> execute(context.getSource(),
                                                NMSCommandArgument.decodePlayerArgumentMultiple(context, "targets"),
                                                NMSCommandArgument.decodeMinecraftKey(context, "item"),
                                                IntegerArgumentType.getInteger(context, "count"),
                                                null))
                                        .then(NMSCommandUtil.argument("nbt", NMSCommandArgument.selectNBTTag())
                                                .executes(context -> execute(context.getSource(),
                                                        NMSCommandArgument.decodePlayerArgumentMultiple(context,
                                                                "targets"),
                                                        NMSCommandArgument.decodeMinecraftKey(context, "item"),
                                                        IntegerArgumentType.getInteger(context, "count"),
                                                        NMSCommandArgument.decodeNBTTag(context, "nbt")))))));
    }

    private int execute(@Nonnull CommandSourceStack source, @Nonnull Collection<ServerPlayer> targets, @Nonnull ResourceLocation itemKey,
                        int count, @Nullable CompoundTag nbt) throws CommandSyntaxException {
        try {
            String namespace = itemKey.getNamespace();
            String key = itemKey.getPath();
            NamespacedKey alterItemKey = new NamespacedKey(namespace, key);
            Item nmsItemType = null;
            CustomItem customItemType = null;
            if (namespace.equalsIgnoreCase(ResourceLocation.DEFAULT_NAMESPACE)) {
                try {
                    nmsItemType = BuiltInRegistries.ITEM.get(itemKey);
                } catch (Exception ignored) {
                }
            }
            boolean fuzzySearching;
            if (nmsItemType != null && nmsItemType.equals(Items.AIR)) {
                fuzzySearching = true;
                nmsItemType = null;
            } else {
                fuzzySearching = false;
            }
            if (nmsItemType == null) {
                ItemRegister register = ItemRegister.getInstanceUnsafe();
                if (register != null) {
                    if (fuzzySearching) {
                        String fuzzyKey = itemKey.getPath();
                        customItemType = register.findFirst(itemType ->
                                itemType.getKey().getKey().equalsIgnoreCase(fuzzyKey));
                    } else
                        customItemType = register.lookup(alterItemKey);
                    nmsItemType = ObjectUtil.safeMap(customItemType,
                            type -> CraftMagicNumbers.getItem(type.getOriginalType()));
                }
            }
            if (nmsItemType == null)
                throw giveFailedMessage.create();
            int itemMaxStackSize = nmsItemType.getMaxStackSize();
            int itemLimit = itemMaxStackSize * 100;
            ItemStack itemstack = new ItemStack(nmsItemType, Math.min(count, itemMaxStackSize));
            if (nbt != null && !nbt.isEmpty()) {
                itemstack.setTag(nbt);
            }
            if (count > itemLimit) {
                source.sendSystemMessage(Component.translatable("commands.give.failed.toomanyitems",
                        itemLimit, itemstack.getDisplayName()));
                return 0;
            }
            FeatureCommandGive feature = FeatureHelper.getFeatureUnsafe(customItemType, FeatureCommandGive.class);
            if (feature != null) {
                org.bukkit.inventory.ItemStack bukkitStack = itemstack.asBukkitMirror();
                if (ItemHelper.tryRegister(customItemType, bukkitStack,
                        _itemStack -> _itemStack != null && feature.handleCommandGive(
                                        new DataCommandGive(_itemStack, ObjectUtil.safeMap(nbt, CompoundTag::toString))))) {
                    itemstack = Objects.requireNonNull(NMSItemHelper.getNmsItem(bukkitStack));
                } else {
                    throw giveFailedMessage.create();
                }
            }
            final ItemStack finalItemStack = itemstack;
            final CompoundTag finalNbt = itemstack.getTag();
            for (ServerPlayer target : targets) {
                for (int totalCount = count; totalCount > 0; ) {
                    int stackCount = Math.min(itemMaxStackSize, totalCount);
                    totalCount -= stackCount;
                    ItemStack seperatedItemStack = new ItemStack(nmsItemType, stackCount);
                    if (finalNbt != null && !finalNbt.isEmpty()) {
                        seperatedItemStack.setTag(finalNbt);
                    }
                    boolean isFull = target.getInventory().add(seperatedItemStack);
                    if (isFull && seperatedItemStack.isEmpty()) {
                        seperatedItemStack.setCount(1);
                        ObjectUtil.safeCall(target.drop(seperatedItemStack, false, false, false),
                                ItemEntity::makeFakeItem);
                        target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f,
                                ((target.getRandom().nextFloat() - target.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                        target.containerMenu.broadcastChanges();
                        continue;
                    }
                    ItemEntity droppedItem = target.drop(seperatedItemStack, false);
                    if (droppedItem != null) {
                        droppedItem.setNoPickUpDelay();
                        droppedItem.setTarget(target.getUUID());
                    }
                }
            }
            if (targets.size() == 1) {
                source.sendSuccess(() -> Component.translatable("commands.give.success.single",
                                count, finalItemStack.getDisplayName(), targets.iterator().next().getDisplayName()),
                        true);
            } else {
                source.sendSuccess(
                        () -> Component.translatable("commands.give.success.multiple",
                                count, finalItemStack.getDisplayName(), targets.size()),
                        true);
            }
            return targets.size();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void updateSuggestions() {
        ObjectUtil.safeCall(suggestionProvider.get(), SuggestionProviderImpl::updateRegisterList);
    }

    private static class SuggestionProviderImpl
            implements SuggestionProvider<CommandSourceStack>, Iterable<ResourceLocation> {

        @Nonnull
        private final List<ResourceLocation> builtinKeys;
        @Nullable
        private List<ResourceLocation> customKeys;

        public SuggestionProviderImpl() {
            DefaultedRegistry<Item> itemRegistries = BuiltInRegistries.ITEM;
            builtinKeys = ObjectUtil.letNonNull(
                    itemRegistries.stream().map(itemRegistries::getKey).toList(),
                    Collections::emptyList);
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> provider,
                                                             SuggestionsBuilder suggestionsBuilder) {
            String lowerCasedRemaining = suggestionsBuilder.getRemainingLowerCase();
            if (!lowerCasedRemaining.contains("/")) {
                if (lowerCasedRemaining.isBlank() || lowerCasedRemaining.contains(":")) {
                    return SharedSuggestionProvider.suggestResource(this, suggestionsBuilder);
                } else {
                    List<ResourceLocation> customKeys = this.customKeys;
                    if (customKeys == null) {
                        ItemRegister register = ItemRegister.getInstanceUnsafe();
                        if (register != null) {
                            this.customKeys = customKeys = register
                                    .listAll(type -> FeatureHelper.hasFeature(type, FeatureCommandGive.class))
                                    .stream()
                                    .map(CustomItem::getKey)
                                    .map(key -> ResourceLocation.tryBuild(key.getNamespace(), key.getKey()))
                                    .toList();
                        }
                    }
                    builtinKeys.stream()
                            .filter(key -> key.getNamespace().startsWith(lowerCasedRemaining)
                                    || key.getPath().startsWith(lowerCasedRemaining))
                            .forEach(key -> suggestionsBuilder.suggest(key.toString()));
                    if (customKeys != null) {
                        customKeys.stream()
                                .filter(key -> key.getNamespace().startsWith(lowerCasedRemaining)
                                        || key.getPath().startsWith(lowerCasedRemaining))
                                .forEach(key -> suggestionsBuilder.suggest(key.toString()));
                    }
                }
            }
            return suggestionsBuilder.buildFuture();
        }

        @Nonnull
        @Override
        public Iterator<ResourceLocation> iterator() {
            List<ResourceLocation> customKeys = this.customKeys;
            if (customKeys == null) {
                ItemRegister register = ItemRegister.getInstanceUnsafe();
                if (register != null) {
                    this.customKeys = customKeys = register
                            .listAll(type -> FeatureHelper.hasFeature(type, FeatureCommandGive.class))
                            .stream()
                            .map(CustomItem::getKey)
                            .map(key -> ResourceLocation.tryBuild(key.getNamespace(), key.getKey()))
                            .toList();
                }
            }
            return new MinecraftKeyCombinedIterator(builtinKeys, customKeys);
        }

        public void updateRegisterList() {
            customKeys = null;
        }
    }
}
