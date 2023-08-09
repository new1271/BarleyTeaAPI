package org.ricetea.barleyteaapi.internal.nms;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.event.entity.CreatureSpawnEvent;

import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.arguments.ArgumentNBTTag;
import net.minecraft.commands.arguments.coordinates.ArgumentVec3;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.phys.Vec3D;

import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureCommandSummon;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;

public final class BarleySummonCommand {
    private static final SimpleCommandExceptionType summonFailedMessage = new SimpleCommandExceptionType(
            (Message) IChatBaseComponent.c("commands.summon.failed"));

    private static final SimpleCommandExceptionType summonFailedWithPositionMessage = new SimpleCommandExceptionType(
            (Message) IChatBaseComponent.c("commands.summon.invalidPosition"));

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void register(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> dispatcher) {
        LiteralCommandNode<CommandListenerWrapper> mainNode = dispatcher
                .register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("summonbarley")
                        .requires(commandlistenerwrapper -> commandlistenerwrapper.c(2)))
                        .then(
                                ((RequiredArgumentBuilder) CommandDispatcher
                                        .a("entity", (ArgumentType) ArgumentMinecraftKeyRegistered.a())
                                        .suggests(BarleySummonEntityProvider.getProvider())
                                        // /summonbarley <entity>
                                        .executes(
                                                commandcontext -> command(
                                                        (CommandListenerWrapper) commandcontext.getSource(),
                                                        ArgumentMinecraftKeyRegistered.e(commandcontext, "entity"),
                                                        ((CommandListenerWrapper) commandcontext.getSource()).d(),
                                                        new NBTTagCompound(), true)))
                                        // /summonbarley <entity> <pos>
                                        .then(((RequiredArgumentBuilder) CommandDispatcher
                                                .a("pos", (ArgumentType) ArgumentVec3.a())
                                                .executes(commandcontext -> command(
                                                        (CommandListenerWrapper) commandcontext.getSource(),
                                                        ArgumentMinecraftKeyRegistered.e(commandcontext, "entity"),
                                                        ArgumentVec3.a(commandcontext, "pos"),
                                                        new NBTTagCompound(), true)))
                                                // /summonbarley <entity> <pos> [nbt]
                                                .then(CommandDispatcher.a("nbt", (ArgumentType) ArgumentNBTTag.a())
                                                        .executes(commandcontext -> command(
                                                                (CommandListenerWrapper) commandcontext.getSource(),
                                                                ArgumentMinecraftKeyRegistered.e(commandcontext,
                                                                        "entity"),
                                                                ArgumentVec3.a(commandcontext, "pos"),
                                                                ArgumentNBTTag.a(commandcontext, "nbt"), false))))));
        dispatcher.register(
                (LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("summon2").redirect(mainNode)));
        dispatcher.register(
                (LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("summonb").redirect(mainNode)));
    }

    private static int command(CommandListenerWrapper source, MinecraftKey entityKey, Vec3D pos, NBTTagCompound nbt,
            boolean initialize) throws CommandSyntaxException {
        BlockPosition blockposition = BlockPosition.a(pos);
        if (!World.k(blockposition))
            throw summonFailedWithPositionMessage.create();
        WorldServer worldserver = source.e();
        String namespace = entityKey.b();
        if (namespace == null || namespace.equalsIgnoreCase(MinecraftKey.c)) {
            NBTTagCompound nbttagcompound1 = nbt.h();
            nbttagcompound1.a("id", entityKey.toString());
            Entity entity = EntityTypes.a(nbttagcompound1, (World) worldserver, loadedEntity -> {
                loadedEntity.b(pos.c, pos.d, pos.e, loadedEntity.dy(), loadedEntity.dA());
                return loadedEntity;
            });
            if (entity == null)
                throw summonFailedMessage.create();
            if (initialize && entity instanceof EntityInsentient)
                ((EntityInsentient) entity).a((WorldAccess) source.e(), source.e().d_(entity.di()),
                        EnumMobSpawn.n,
                        (GroupDataEntity) null, (NBTTagCompound) null);
            if (!worldserver.tryAddFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.COMMAND))
                throw summonFailedWithPositionMessage.create();
            source.a(() -> IChatBaseComponent.a("commands.summon.success", new Object[] { entity.H_() }), true);
        } else {
            EntityRegister register = EntityRegister.getInstance();
            NamespacedKey key = new NamespacedKey(namespace, entityKey.a());
            BaseEntity baseEntity = register.lookupEntityType(key);
            if (baseEntity != null && baseEntity instanceof FeatureCommandSummon) {
                FeatureCommandSummon summonEntity = (FeatureCommandSummon) baseEntity;
                NBTTagCompound nbttagcompound1 = nbt.h();
                nbttagcompound1.a("id", baseEntity.getEntityTypeBasedOn().getKey().toString());
                Entity entity = EntityTypes.a(nbttagcompound1, (World) worldserver, loadedEntity -> {
                    loadedEntity.b(pos.c, pos.d, pos.e, loadedEntity.dy(), loadedEntity.dA());
                    return loadedEntity;
                });
                if (entity == null)
                    throw summonFailedMessage.create();
                org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
                if (bukkitEntity == null)
                    throw summonFailedMessage.create();
                if (initialize && entity instanceof EntityInsentient)
                    ((EntityInsentient) entity).a((WorldAccess) source.e(), source.e().d_(entity.di()),
                            EnumMobSpawn.n,
                            (GroupDataEntity) null, (NBTTagCompound) null);
                if (!worldserver.tryAddFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.COMMAND))
                    throw summonFailedMessage.create();
                if (summonEntity.handleCommandSummon(bukkitEntity, nbt.toString())) {
                    BaseEntity.registerEntity(bukkitEntity, baseEntity);
                } else {
                    bukkitEntity.remove();
                    throw summonFailedMessage.create();
                }
                source.a(() -> IChatBaseComponent.a("commands.summon.success",
                        new Object[] { IChatBaseComponent.c(baseEntity.getNameInTranslateKey()) }), true);
            } else {
                throw summonFailedMessage.create();
            }
        }
        return 1;
    }

    public static void register() {
        CommandDispatcher dispatcher = ((CraftServer) Bukkit.getServer()).getServer().aC();
        register(dispatcher.a());
    }
}
