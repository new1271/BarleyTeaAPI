package org.ricetea.barleyteaapi.internal.nms.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.CreatureSpawnEvent;

import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
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
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityLoad;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTick;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataCommandSummon;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.internal.nms.util.*;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

public final class NMSSummonCommand extends NMSRegularCommand {
    @Nonnull
    private static final SimpleCommandExceptionType summonFailedMessage = new SimpleCommandExceptionType(
            (Message) IChatBaseComponent.c("commands.summon.failed"));

    @Nonnull
    private static final SimpleCommandExceptionType summonFailedWithPositionMessage = new SimpleCommandExceptionType(
            (Message) IChatBaseComponent.c("commands.summon.invalidPosition"));

    @Nonnull
    private final Lazy<SuggestionProviderImpl> suggestionProvider = Lazy.create(SuggestionProviderImpl::new);

    public NMSSummonCommand() {
        super(NamespacedKeyUtil.BarleyTeaAPI("summonbarley"), NamespacedKeyUtil.BarleyTeaAPI("summon2"));
    }

    @Override
    public LiteralArgumentBuilder<CommandListenerWrapper> prepareCommand(
            @Nonnull LiteralArgumentBuilder<CommandListenerWrapper> builder) {
        return builder.requires(NMSCommandUtil::needOp)
                .then(NMSCommandUtil.argument("entity", NMSCommandArgument.selectMinecraftKey())
                        .suggests(suggestionProvider.get())
                        .executes(context -> execute(context.getSource(),
                                NMSCommandArgument.decodeMinecraftKey(context, "entity"),
                                context.getSource().d(), new NBTTagCompound(), true))
                        .then(NMSCommandUtil.argument("pos", NMSCommandArgument.selectVector3D())
                                .executes(context -> execute(context.getSource(),
                                        NMSCommandArgument.decodeMinecraftKey(context, "entity"),
                                        NMSCommandArgument.decodeVector3D(context, "pos"),
                                        new NBTTagCompound(), true))
                                .then(NMSCommandUtil.argument("nbt", NMSCommandArgument.selectNBTTag())
                                        .executes(context -> execute(context.getSource(),
                                                NMSCommandArgument.decodeMinecraftKey(context, "entity"),
                                                NMSCommandArgument.decodeVector3D(context, "pos"),
                                                NMSCommandArgument.decodeNBTTag(context, "nbt"),
                                                false)))));
    }

    private int execute(CommandListenerWrapper source, MinecraftKey entityKey, Vec3D pos, NBTTagCompound nbt,
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
                        if (initialize && entity instanceof EntityInsentient)
                            ((EntityInsentient) entity).a((WorldAccess) source.e(), source.e().d_(entity.di()),
                                    EnumMobSpawn.n,
                                    (GroupDataEntity) null, (NBTTagCompound) null);
                        if (!worldserver.tryAddFreshEntityWithPassengers(entity,
                                CreatureSpawnEvent.SpawnReason.COMMAND))
                            throw summonFailedMessage.create();
                        org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
                        if (bukkitEntity == null)
                            throw summonFailedMessage.create();
                        if (baseEntity.tryRegister(bukkitEntity,
                                _entity -> _entity != null
                                        && summonEntity.handleCommandSummon(
                                                new DataCommandSummon(_entity, nbt.toString())))) {
                            if (baseEntity instanceof FeatureEntityLoad feature && !bukkitEntity.isDead()) {
                                feature.handleEntityLoaded(bukkitEntity);
                            }
                            if (baseEntity instanceof FeatureEntityTick) {
                                EntityTickTask.getInstance().addEntity(bukkitEntity);
                            }
                        } else {
                            bukkitEntity.remove();
                            throw summonFailedMessage.create();
                        }
                        source.a(() -> IChatBaseComponent.a("commands.summon.success",
                                new Object[] {
                                        bukkitEntity.customName() == null
                                                ? IChatBaseComponent.a(baseEntity.getNameInTranslateKey(),
                                                        baseEntity.getDefaultName())
                                                : entity.H_() }),
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

    @Override
    public void updateSuggestions() {
        ObjectUtil.safeCall(suggestionProvider.get(), SuggestionProviderImpl::updateRegisterList);
    }

    public static class SuggestionProviderImpl
            implements SuggestionProvider<CommandListenerWrapper>, Iterable<MinecraftKey> {

        @Nonnull
        private final List<MinecraftKey> builtinKeys;
        @Nullable
        private List<MinecraftKey> customKeys;

        public SuggestionProviderImpl() {
            RegistryBlocks<EntityTypes<?>> entityRegistries = BuiltInRegistries.h;
            builtinKeys = ObjectUtil.letNonNull(
                    entityRegistries.s().filter(EntityTypes::c).map(entityType -> entityRegistries.b(entityType))
                            .toList(),
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
