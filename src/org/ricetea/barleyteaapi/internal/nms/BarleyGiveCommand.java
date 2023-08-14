package org.ricetea.barleyteaapi.internal.nms;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.arguments.ArgumentNBTTag;
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

import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCommandGive;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.api.item.render.AbstractItemRenderer;
import org.ricetea.barleyteaapi.internal.nms.helper.NMSItemHelper;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class BarleyGiveCommand {
	private static final SimpleCommandExceptionType giveFailedMessage = new SimpleCommandExceptionType(
			(Message) IChatBaseComponent.c("command.failed"));

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void register(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> dispatcher) {
		LiteralCommandNode<CommandListenerWrapper> mainNode = dispatcher
				.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("givebarley")
						.requires(commandlistenerwrapper -> commandlistenerwrapper.c(2)))
						.then(
								CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).then(
										((RequiredArgumentBuilder) CommandDispatcher
												.a("item", (ArgumentType) ArgumentMinecraftKeyRegistered.a())
												.suggests(BarleyGiveItemProvider.getProvider())
												// /givebarley <targets> <item>
												.executes(
														commandcontext -> command(
																(CommandListenerWrapper) commandcontext.getSource(),
																ArgumentEntity.f(commandcontext, "targets"),
																ArgumentMinecraftKeyRegistered.e(commandcontext,
																		"item"),
																1,
																new NBTTagCompound())))
												// /givebarley <targets> <item> <count>
												.then(((RequiredArgumentBuilder) CommandDispatcher
														.a("count", (ArgumentType) IntegerArgumentType.integer(1))
														.executes(commandcontext -> command(
																(CommandListenerWrapper) commandcontext.getSource(),
																ArgumentEntity.f(commandcontext, "targets"),
																ArgumentMinecraftKeyRegistered.e(commandcontext,
																		"item"),
																IntegerArgumentType.getInteger(commandcontext, "count"),
																new NBTTagCompound())))
														// /givebarley <targets> <item> <count> [nbt]
														.then(CommandDispatcher
																.a("nbt", (ArgumentType) ArgumentNBTTag.a())
																.executes(commandcontext -> command(
																		(CommandListenerWrapper) commandcontext
																				.getSource(),
																		ArgumentEntity.f(commandcontext, "targets"),
																		ArgumentMinecraftKeyRegistered.e(commandcontext,
																				"item"),
																		IntegerArgumentType.getInteger(commandcontext,
																				"count"),
																		ArgumentNBTTag.a(commandcontext, "nbt"))))))));
		dispatcher.register(
				(LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("give2")
						.requires(commandlistenerwrapper -> commandlistenerwrapper.c(2)).redirect(mainNode)));
		dispatcher.register(
				(LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("giveb")
						.requires(commandlistenerwrapper -> commandlistenerwrapper.c(2)).redirect(mainNode)));
	}

	private static int command(CommandListenerWrapper source, Collection<EntityPlayer> targets, MinecraftKey itemKey,
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
				barleyTeaItemType = ItemRegister.getInstance().lookupItemType(alterItemKey);
				nmsItemType = ObjectUtil.mapWhenNonnull(barleyTeaItemType,
						type -> CraftMagicNumbers.getItem(type.getMaterialBasedOn()));
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
				BaseItem.registerItem(bukkitStack, barleyTeaItemType);
				if (commandGiveType.handleCommandGive(ObjectUtil.throwWhenNull(bukkitStack), nbt.toString())) {
					AbstractItemRenderer.renderItem(bukkitStack);
					itemstack = ObjectUtil.throwWhenNull(NMSItemHelper.getNmsItem(bukkitStack));
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

	public static void register() {
		CommandDispatcher dispatcher = ((CraftServer) Bukkit.getServer()).getServer().aC();
		register(dispatcher.a());
	}

	public static void unregister() {
		CommandDispatcher dispatcher = ((CraftServer) Bukkit.getServer()).getServer().aC();
		var root = dispatcher.a().getRoot();
		root.removeCommand("givebarley");
		root.removeCommand("give2");
		root.removeCommand("giveb");
	}
}
