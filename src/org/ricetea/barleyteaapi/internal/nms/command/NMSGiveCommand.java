package org.ricetea.barleyteaapi.internal.nms.command;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCommandGive;
import org.ricetea.barleyteaapi.api.item.feature.data.DataCommandGive;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.internal.nms.helper.NMSItemHelper;
import org.ricetea.barleyteaapi.internal.nms.util.*;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class NMSGiveCommand extends NMSRegularCommand {
	@Nonnull
	private static final SimpleCommandExceptionType giveFailedMessage = new SimpleCommandExceptionType(
			(Message) IChatBaseComponent.c("command.failed"));

	@Nonnull
	private static final Lazy<SuggestionProviderImpl> suggestionProvider = Lazy.create(SuggestionProviderImpl::new);

	public NMSGiveCommand() {
		super(NamespacedKeyUtil.BarleyTeaAPI("givebarley"), NamespacedKeyUtil.BarleyTeaAPI("give2"));
	}

	@Override
	public LiteralArgumentBuilder<CommandListenerWrapper> prepareCommand(
			@Nonnull LiteralArgumentBuilder<CommandListenerWrapper> builder) {
		return builder.requires(NMSCommandUtil::needOp)
				.then(NMSCommandUtil.argument("targets", NMSCommandArgument.selectPlayers())
						.then(NMSCommandUtil.argument("item", NMSCommandArgument.selectMinecraftKey())
								.suggests(suggestionProvider.get())
								.executes(context -> execute(context.getSource(),
										NMSCommandArgument.decodePlayerArgumentMultiple(context, "targets"),
										NMSCommandArgument.decodeMinecraftKey(context, "item"), 1,
										new NBTTagCompound()))
								.then(NMSCommandUtil.argument("count", IntegerArgumentType.integer(1))
										.executes(context -> execute(context.getSource(),
												NMSCommandArgument.decodePlayerArgumentMultiple(context, "targets"),
												NMSCommandArgument.decodeMinecraftKey(context, "item"),
												IntegerArgumentType.getInteger(context, "count"),
												new NBTTagCompound()))
										.then(NMSCommandUtil.argument("nbt", NMSCommandArgument.selectNBTTag())
												.executes(context -> execute(context.getSource(),
														NMSCommandArgument.decodePlayerArgumentMultiple(context,
																"targets"),
														NMSCommandArgument.decodeMinecraftKey(context, "item"),
														IntegerArgumentType.getInteger(context, "count"),
														NMSCommandArgument.decodeNBTTag(context, "nbt")))))));
	}

	private int execute(CommandListenerWrapper source, Collection<EntityPlayer> targets, MinecraftKey itemKey,
			int count, NBTTagCompound nbt) throws CommandSyntaxException {
		try {
			String namespace = itemKey.b();
			String key = itemKey.a();
			NamespacedKey alterItemKey = new NamespacedKey(namespace, key);
			Item nmsItemType;
			BaseItem barleyTeaItemType;
			if (namespace == null || namespace.equalsIgnoreCase(MinecraftKey.c)) {
				try {
					nmsItemType = (Item) BuiltInRegistries.i.a(itemKey);
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
			int itemMaxStackSize = nmsItemType.l();
			int itemLimit = itemMaxStackSize * 100;
			ItemStack itemstack = new ItemStack(nmsItemType, count);
			//void setTag(net.minecraft.nbt.CompoundTag) -> c
			itemstack.c(nbt);
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
				source.b((IChatBaseComponent) IChatBaseComponent.a("commands.give.failed.toomanyitems",
						new Object[] { Integer.valueOf(itemLimit), itemstack.J() }));
				return 0;
			}
			final ItemStack finalItemStack = itemstack;
			//net.minecraft.nbt.CompoundTag getTag() -> v
			NBTTagCompound nbt1 = itemstack.v();
			for (Iterator<EntityPlayer> iterator = targets.iterator(); iterator.hasNext();) {
				EntityPlayer entityplayer = iterator.next();
				int totalCount = count;
				while (totalCount > 0) {
					int stackCount = Math.min(itemMaxStackSize, totalCount);
					totalCount -= stackCount;
					ItemStack itemstack1 = new ItemStack(nmsItemType, stackCount);
					itemstack1.c(nbt1);
					boolean flag = entityplayer.fN().e(itemstack1);
					if (flag && itemstack1.b()) {
						itemstack1.f(1);
						EntityItem entityItem = entityplayer.drop(itemstack1, false, false, false);
						if (entityItem != null)
							entityItem.w();
						entityplayer.dI().a((EntityHuman) null, entityplayer.dn(), entityplayer.dp(), entityplayer.dt(),
								SoundEffects.ma, SoundCategory.h, 0.2F,
								((entityplayer.ec().i() - entityplayer.ec().i()) * 0.7F + 1.0F) * 2.0F);
						((EntityHuman) entityplayer).bR.d();
						continue;
					}
					EntityItem entityitem = entityplayer.a(itemstack1, false);
					if (entityitem != null) {
						entityitem.p();
						entityitem.b(entityplayer.ct());
					}
				}
			}
			if (targets.size() == 1) {
				source.a(() -> IChatBaseComponent.a("commands.give.success.single",
						new Object[] { Integer.valueOf(count),
								finalItemStack.J(), ((EntityPlayer) targets.iterator().next()).H_() }),
						true);
			} else {
				source.a(
						() -> IChatBaseComponent.a("commands.give.success.multiple",
								new Object[] { Integer.valueOf(count), finalItemStack.J(),
										Integer.valueOf(targets.size()) }),
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
			implements SuggestionProvider<CommandListenerWrapper>, Iterable<MinecraftKey> {

		@Nonnull
		private final List<MinecraftKey> builtinKeys;
		@Nullable
		private List<MinecraftKey> customKeys;

		public SuggestionProviderImpl() {
			RegistryBlocks<Item> itemRegistries = BuiltInRegistries.i;
			builtinKeys = ObjectUtil.letNonNull(
					itemRegistries.s().map(itemType -> itemRegistries.b(itemType)).toList(),
					Collections::emptyList);
		}

		@Override
		public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandListenerWrapper> provider,
				SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
			String lowerCasedRemaining = suggestionsBuilder.getRemainingLowerCase();
			if (!lowerCasedRemaining.contains("/")) {
				if (lowerCasedRemaining.isBlank() || lowerCasedRemaining.contains(":")) {
					return ICompletionProvider.a(this, suggestionsBuilder);
				} else {
					List<MinecraftKey> customKeys = this.customKeys;
					if (customKeys == null) {
						ItemRegister register = ItemRegister.getInstanceUnsafe();
						if (register != null) {
							this.customKeys = customKeys = register
									.listAllKeys(type -> type instanceof FeatureCommandGive).stream()
									.map(key -> MinecraftKey.a(key.getNamespace(), key.getKey())).toList();
						}
					}
					builtinKeys.stream()
							.filter(key -> key.a().startsWith(lowerCasedRemaining)
									|| key.b().startsWith(lowerCasedRemaining))
							.forEach(key -> suggestionsBuilder.suggest(key.toString()));
					if (customKeys != null) {
						customKeys.stream()
								.filter(key -> key.a().startsWith(lowerCasedRemaining)
										|| key.b().startsWith(lowerCasedRemaining))
								.forEach(key -> suggestionsBuilder.suggest(key.toString()));
					}
				}
			}
			return suggestionsBuilder.buildFuture();
		}

		@Override
		public Iterator<MinecraftKey> iterator() {
			List<MinecraftKey> customKeys = this.customKeys;
			if (customKeys == null) {
				ItemRegister register = ItemRegister.getInstanceUnsafe();
				if (register != null) {
					this.customKeys = customKeys = register
							.listAllKeys(type -> type instanceof FeatureCommandGive).stream()
							.map(key -> MinecraftKey.a(key.getNamespace(), key.getKey())).toList();
				}
			}
			return new MinecraftKeyCombinedIterator(builtinKeys, customKeys);
		}

		public void updateRegisterList() {
			customKeys = null;
		}
	}
}
