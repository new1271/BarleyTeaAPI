package org.ricetea.barleyteaapi.internal.nms.v1_20_R1.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataCommandSummon;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.util.MinecraftKeyCombinedIterator;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.util.NMSCommandArgument;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.util.NMSCommandUtil;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class NMSSummonCommand extends NMSRegularCommand {
    @Nonnull
    private static final SimpleCommandExceptionType summonFailedMessage = new SimpleCommandExceptionType(
            Component.translatable("commands.summon.failed"));

    @Nonnull
    private static final SimpleCommandExceptionType summonFailedWithPositionMessage = new SimpleCommandExceptionType(
            Component.translatable("commands.summon.invalidPosition"));

    @Nonnull
    private final Lazy<SuggestionProviderImpl> suggestionProvider = Lazy.create(SuggestionProviderImpl::new);

    public NMSSummonCommand() {
        super(NamespacedKeyUtil.BarleyTeaAPI("summonbarley"), NamespacedKeyUtil.BarleyTeaAPI("summon2"));
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> prepareCommand(
            @Nonnull LiteralArgumentBuilder<CommandSourceStack> builder) {
        return builder.requires(NMSCommandUtil::needOp)
                .then(NMSCommandUtil.argument("entity", NMSCommandArgument.selectMinecraftKey())
                        .suggests(suggestionProvider.get())
                        .executes(context -> execute(context.getSource(),
                                NMSCommandArgument.decodeMinecraftKey(context, "entity"),
                                context.getSource().getPosition(), new CompoundTag(), true))
                        .then(NMSCommandUtil.argument("pos", NMSCommandArgument.selectVector3D())
                                .executes(context -> execute(context.getSource(),
                                        NMSCommandArgument.decodeMinecraftKey(context, "entity"),
                                        NMSCommandArgument.decodeVector3D(context, "pos"),
                                        new CompoundTag(), true))
                                .then(NMSCommandUtil.argument("nbt", NMSCommandArgument.selectNBTTag())
                                        .executes(context -> execute(context.getSource(),
                                                NMSCommandArgument.decodeMinecraftKey(context, "entity"),
                                                NMSCommandArgument.decodeVector3D(context, "pos"),
                                                NMSCommandArgument.decodeNBTTag(context, "nbt"),
                                                false)))));
    }

    private int execute(CommandSourceStack source, ResourceLocation entityKey, Vec3 pos, CompoundTag nbt,
                        boolean initialize) throws CommandSyntaxException {
        try {
            BlockPos blockposition = BlockPos.containing(pos);
            if (!Level.isInSpawnableBounds(blockposition))
                throw summonFailedWithPositionMessage.create();
            ServerLevel worldserver = source.getLevel();
            String namespace = entityKey.getNamespace();
            if (namespace.equalsIgnoreCase(ResourceLocation.DEFAULT_NAMESPACE)) {
                CompoundTag CompoundTag1 = nbt.copy();
                CompoundTag1.putString("id", entityKey.toString());
                Entity entity = EntityType.loadEntityRecursive(CompoundTag1, worldserver, loadedEntity -> {
                    loadedEntity.moveTo(pos.x, pos.y, pos.z, loadedEntity.getYRot(), loadedEntity.getXRot());
                    loadedEntity.spawnReason = CreatureSpawnEvent.SpawnReason.COMMAND;
                    return loadedEntity;
                });
                if (entity == null)
                    throw summonFailedMessage.create();
                if (initialize && entity instanceof Mob mob)
                    mob.finalizeSpawn(source.getLevel(), source.getLevel().getCurrentDifficultyAt(entity.blockPosition()),
                            MobSpawnType.COMMAND, null, null);
                if (!worldserver.tryAddFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.COMMAND))
                    throw summonFailedWithPositionMessage.create();
                source.sendSuccess(() ->
                        Component.translatable("commands.summon.success", entity.getDisplayName()), true);
                return 1;
            } else {
                EntityRegister register = EntityRegister.getInstanceUnsafe();
                if (register != null) {
                    NamespacedKey key = new NamespacedKey(namespace, entityKey.getPath());
                    CustomEntity entityType = register.lookup(key);
                    if (entityType instanceof FeatureCommandSummon feature) {
                        CompoundTag CompoundTag1 = nbt.copy();
                        CompoundTag1.putString("id", entityType.getOriginalType().getKey().toString());
                        Entity entity = EntityType.loadEntityRecursive(CompoundTag1, worldserver, loadedEntity -> {
                            loadedEntity.moveTo(pos.x, pos.y, pos.z, loadedEntity.getYRot(), loadedEntity.getXRot());
                            loadedEntity.spawnReason = CreatureSpawnEvent.SpawnReason.COMMAND;
                            return loadedEntity;
                        });
                        if (entity == null)
                            throw summonFailedMessage.create();
                        if (initialize && entity instanceof Mob mob)
                            mob.finalizeSpawn(source.getLevel(), source.getLevel().getCurrentDifficultyAt(entity.blockPosition()),
                                    MobSpawnType.COMMAND, null, null);
                        if (!worldserver.tryAddFreshEntityWithPassengers(entity,
                                CreatureSpawnEvent.SpawnReason.COMMAND))
                            throw summonFailedMessage.create();
                        org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
                        if (EntityHelper.tryRegister(entityType, bukkitEntity,
                                _entity -> _entity != null
                                        && feature.handleCommandSummon(
                                        new DataCommandSummon(_entity, nbt.toString())))) {
                            EntityFeatureLinker.loadEntity(entityType, bukkitEntity, false);
                        } else {
                            bukkitEntity.remove();
                            throw summonFailedMessage.create();
                        }
                        source.sendSuccess(() -> Component.translatable("commands.summon.success",
                                        bukkitEntity.customName() == null
                                                ? Component.translatable(entityType.getTranslationKey(),
                                                entityType.getDefaultName())
                                                : entity.getDisplayName()),
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
            implements SuggestionProvider<CommandSourceStack>, Iterable<ResourceLocation> {

        @Nonnull
        private final List<ResourceLocation> builtinKeys;
        @Nullable
        private List<ResourceLocation> customKeys;

        public SuggestionProviderImpl() {
            DefaultedRegistry<EntityType<?>> entityRegistries = BuiltInRegistries.ENTITY_TYPE;
            builtinKeys = ObjectUtil.letNonNull(
                    entityRegistries.stream()
                            .filter(EntityType::canSummon)
                            .map(entityRegistries::getKey)
                            .toList(),
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
                        EntityRegister register = EntityRegister.getInstanceUnsafe();
                        if (register != null) {
                            this.customKeys = customKeys = register
                                    .listAll(type -> type instanceof FeatureCommandSummon)
                                    .stream()
                                    .map(CustomEntity::getKey)
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
                EntityRegister register = EntityRegister.getInstanceUnsafe();
                if (register != null) {
                    this.customKeys = customKeys = register
                            .listAll(type -> type instanceof FeatureCommandSummon)
                            .stream()
                            .map(CustomEntity::getKey)
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
