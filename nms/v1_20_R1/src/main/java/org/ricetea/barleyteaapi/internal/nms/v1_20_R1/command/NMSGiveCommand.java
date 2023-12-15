package org.ricetea.barleyteaapi.internal.nms.v1_20_R1.command;

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
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCommandGive;
import org.ricetea.barleyteaapi.api.item.feature.data.DataCommandGive;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.helper.NMSItemHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.util.MinecraftKeyCombinedIterator;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.util.NMSCommandArgument;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.util.NMSCommandUtil;
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
                                        new CompoundTag()))
                                .then(NMSCommandUtil.argument("count", IntegerArgumentType.integer(1))
                                        .executes(context -> execute(context.getSource(),
                                                NMSCommandArgument.decodePlayerArgumentMultiple(context, "targets"),
                                                NMSCommandArgument.decodeMinecraftKey(context, "item"),
                                                IntegerArgumentType.getInteger(context, "count"),
                                                new CompoundTag()))
                                        .then(NMSCommandUtil.argument("nbt", NMSCommandArgument.selectNBTTag())
                                                .executes(context -> execute(context.getSource(),
                                                        NMSCommandArgument.decodePlayerArgumentMultiple(context,
                                                                "targets"),
                                                        NMSCommandArgument.decodeMinecraftKey(context, "item"),
                                                        IntegerArgumentType.getInteger(context, "count"),
                                                        NMSCommandArgument.decodeNBTTag(context, "nbt")))))));
    }

    private int execute(CommandSourceStack source, Collection<ServerPlayer> targets, ResourceLocation itemKey,
                        int count, CompoundTag nbt) throws CommandSyntaxException {
        try {
            String namespace = itemKey.getNamespace();
            String key = itemKey.getPath();
            NamespacedKey alterItemKey = new NamespacedKey(namespace, key);
            Item nmsItemType;
            BaseItem barleyTeaItemType;
            if (namespace.equalsIgnoreCase(ResourceLocation.DEFAULT_NAMESPACE)) {
                try {
                    nmsItemType = BuiltInRegistries.ITEM.get(itemKey);
                } catch (Exception e) {
                    nmsItemType = null;
                }
                barleyTeaItemType = null;
            } else {
                ItemRegister register = ItemRegister.getInstanceUnsafe();
                if (register == null) {
                    barleyTeaItemType = null;
                    nmsItemType = null;
                } else {
                    barleyTeaItemType = ItemRegister.getInstance().lookup(alterItemKey);
                    nmsItemType = ObjectUtil.safeMap(barleyTeaItemType,
                            type -> CraftMagicNumbers.getItem(type.getMaterialBasedOn()));
                }
            }
            if (nmsItemType == null)
                throw giveFailedMessage.create();
            //int getMaxStackSize() -> l
            int itemMaxStackSize = nmsItemType.getMaxStackSize();
            int itemLimit = itemMaxStackSize * 100;
            ItemStack itemstack = new ItemStack(nmsItemType, count);
            //void setTag(net.minecraft.nbt.CompoundTag) -> c
            itemstack.setTag(nbt);
            if (barleyTeaItemType instanceof FeatureCommandGive commandGiveType) {
                org.bukkit.inventory.ItemStack bukkitStack = itemstack.asBukkitMirror();
                if (barleyTeaItemType.tryRegister(bukkitStack,
                        _itemStack -> _itemStack != null && commandGiveType
                                .handleCommandGive(
                                        new DataCommandGive(_itemStack, nbt.toString())))) {
                    itemstack = Objects.requireNonNull(NMSItemHelper.getNmsItem(bukkitStack));
                } else {
                    throw giveFailedMessage.create();
                }
            }
            if (count > itemLimit) {
                source.sendSystemMessage(Component.translatable("commands.give.failed.toomanyitems",
                        itemLimit, itemstack.getDisplayName()));
                return 0;
            }
            final ItemStack finalItemStack = itemstack;
            //net.minecraft.nbt.CompoundTag getTag() -> v
            CompoundTag nbt1 = itemstack.getTag();
            for (ServerPlayer entityplayer : targets) {
                int totalCount = count;
                while (totalCount > 0) {
                    int stackCount = Math.min(itemMaxStackSize, totalCount);
                    totalCount -= stackCount;
                    ItemStack itemstack1 = new ItemStack(nmsItemType, stackCount);
                    itemstack1.setTag(nbt1);
                    boolean flag = entityplayer.getInventory().add(itemstack1);
                    if (flag && itemstack1.isEmpty()) {
                        itemstack1.setCount(1);
                        ItemEntity entityItem = entityplayer.drop(itemstack1, false, false, false);
                        if (entityItem != null)
                            entityItem.makeFakeItem();
                        entityplayer.level().playSound(null, entityplayer.getX(), entityplayer.getY(), entityplayer.getZ(),
                                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                                ((entityplayer.getRandom().nextFloat() - entityplayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        entityplayer.containerMenu.broadcastChanges();
                        continue;
                    }
                    ItemEntity entityitem = entityplayer.drop(itemstack1, false);
                    if (entityitem != null) {
                        entityitem.setNoPickUpDelay();
                        entityitem.setTarget(entityplayer.getUUID());
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
                                                             SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
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
                                    .listAllKeys(type -> type instanceof FeatureCommandGive).stream()
                                    .map(key -> ResourceLocation.tryBuild(key.getNamespace(), key.getKey())).toList();
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
                            .listAllKeys(type -> type instanceof FeatureCommandGive).stream()
                            .map(key -> ResourceLocation.tryBuild(key.getNamespace(), key.getKey())).toList();
                }
            }
            return new MinecraftKeyCombinedIterator(builtinKeys, customKeys);
        }

        public void updateRegisterList() {
            customKeys = null;
        }
    }
}
