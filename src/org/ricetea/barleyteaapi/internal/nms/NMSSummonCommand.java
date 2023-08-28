package org.ricetea.barleyteaapi.internal.nms;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.CreatureSpawnEvent;

import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.arguments.ArgumentNBTTag;
import net.minecraft.commands.arguments.coordinates.ArgumentVec3;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
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
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class NMSSummonCommand implements NMSBaseCommand {
    private static final SimpleCommandExceptionType summonFailedMessage = new SimpleCommandExceptionType(
            (Message) IChatBaseComponent.c("commands.summon.failed"));

    private static final SimpleCommandExceptionType summonFailedWithPositionMessage = new SimpleCommandExceptionType(
            (Message) IChatBaseComponent.c("commands.summon.invalidPosition"));

    private SuggestionProvider suggestionProvider;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void register(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> dispatcher) {
        LiteralCommandNode<CommandListenerWrapper> mainNode = dispatcher
                .register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("summonbarley")
                        .requires(commandlistenerwrapper -> commandlistenerwrapper.c(2)))
                        .then(
                                ((RequiredArgumentBuilder) CommandDispatcher
                                        .a("entity", (ArgumentType) ArgumentMinecraftKeyRegistered.a())
                                        .suggests(suggestionProvider = new SuggestionProvider())
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
                (LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("summon2")
                        .requires(commandlistenerwrapper -> commandlistenerwrapper.c(2)).redirect(mainNode)));
        dispatcher.register(
                (LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("summonb")
                        .requires(commandlistenerwrapper -> commandlistenerwrapper.c(2)).redirect(mainNode)));
    }

    private int command(CommandListenerWrapper source, MinecraftKey entityKey, Vec3D pos, NBTTagCompound nbt,
            boolean initialize) throws CommandSyntaxException {
        try {
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
                return 1;
            } else {
                EntityRegister register = EntityRegister.getInstanceUnsafe();
                if (register != null) {
                    NamespacedKey key = new NamespacedKey(namespace, entityKey.a());
                    BaseEntity baseEntity = register.lookup(key);
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
                        if (!worldserver.tryAddFreshEntityWithPassengers(entity,
                                CreatureSpawnEvent.SpawnReason.COMMAND))
                            throw summonFailedMessage.create();
                        if (!baseEntity.tryRegister(bukkitEntity,
                                _entity -> _entity != null
                                        && summonEntity.handleCommandSummon(_entity, nbt.toString()))) {
                            bukkitEntity.remove();
                            throw summonFailedMessage.create();
                        }
                        source.a(() -> IChatBaseComponent.a("commands.summon.success",
                                new Object[] { IChatBaseComponent.a(baseEntity.getNameInTranslateKey(),
                                        baseEntity.getDefaultName()) }),
                                true);
                        return 1;
                    }
                }
            }
            throw summonFailedMessage.create();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void unregister(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> dispatcher) {
        var root = dispatcher.getRoot();
        root.removeCommand("summonbarley");
        root.removeCommand("summon2");
        root.removeCommand("summonb");
    }

    public void update() {
        ObjectUtil.callWhenNonnull(suggestionProvider, SuggestionProvider::updateRegisterList);
    }

    public static class SuggestionProvider
            implements com.mojang.brigadier.suggestion.SuggestionProvider<ICompletionProvider>, Iterable<MinecraftKey> {

        @Nonnull
        private final List<MinecraftKey> builtinKeys;
        @Nullable
        private List<MinecraftKey> customKeys;

        public SuggestionProvider() {
            RegistryBlocks<EntityTypes<?>> entityRegistries = BuiltInRegistries.h;
            builtinKeys = ObjectUtil.letNonNull(
                    entityRegistries.s().filter(EntityTypes::c).map(entityType -> entityRegistries.b(entityType))
                            .toList(),
                    Collections::emptyList);
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ICompletionProvider> provider,
                SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
            String lowerCasedRemaining = suggestionsBuilder.getRemainingLowerCase();
            if (!lowerCasedRemaining.contains("/")) {
                if (lowerCasedRemaining.isBlank() || lowerCasedRemaining.contains(":")) {
                    return ICompletionProvider.a(this, suggestionsBuilder);
                } else {
                    List<MinecraftKey> customKeys = this.customKeys;
                    if (customKeys == null) {
                        EntityRegister register = EntityRegister.getInstanceUnsafe();
                        if (register != null) {
                            this.customKeys = customKeys = register
                                    .listAllKeys(type -> type instanceof FeatureCommandSummon).stream()
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
                EntityRegister register = EntityRegister.getInstanceUnsafe();
                if (register != null) {
                    this.customKeys = customKeys = register
                            .listAllKeys(type -> type instanceof FeatureCommandSummon).stream()
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
