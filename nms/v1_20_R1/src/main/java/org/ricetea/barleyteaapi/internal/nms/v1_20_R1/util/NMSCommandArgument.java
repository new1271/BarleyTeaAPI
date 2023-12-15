package org.ricetea.barleyteaapi.internal.nms.v1_20_R1.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;

public final class NMSCommandArgument {
    @Nonnull
    public static EntityArgument selectPlayer() {
        return Objects.requireNonNull(EntityArgument.player());
    }

    @Nonnull
    public static EntityArgument selectPlayers() {
        return Objects.requireNonNull(EntityArgument.players());
    }

    @Nonnull
    public static ResourceLocationArgument selectMinecraftKey() {
        return Objects.requireNonNull(ResourceLocationArgument.id());
    }

    @Nonnull
    public static CompoundTagArgument selectNBTTag() {
        return Objects.requireNonNull(CompoundTagArgument.compoundTag());
    }

    @Nonnull
    public static Vec3Argument selectVector3D() {
        return Objects.requireNonNull(Vec3Argument.vec3());
    }

    @Nonnull
    public static ServerPlayer decodePlayerArgumentSingle(CommandContext<CommandSourceStack> context, String name)
            throws CommandSyntaxException {
        return Objects.requireNonNull(EntityArgument.getPlayer(context, name));
    }

    @Nonnull
    public static Collection<ServerPlayer> decodePlayerArgumentMultiple(CommandContext<CommandSourceStack> context, String name)
            throws CommandSyntaxException {
        return Objects.requireNonNull(EntityArgument.getPlayers(context, name));
    }

    @Nonnull
    public static ResourceLocation decodeMinecraftKey(CommandContext<CommandSourceStack> context, String name)
            throws CommandSyntaxException {
        return Objects.requireNonNull(ResourceLocationArgument.getId(context, name));
    }

    @Nonnull
    public static CompoundTag decodeNBTTag(CommandContext<CommandSourceStack> context, String name)
            throws CommandSyntaxException {
        return (CompoundTag) Objects.requireNonNull(CompoundTagArgument.getCompoundTag(context, name));
    }

    @Nonnull
    public static Vec3 decodeVector3D(CommandContext<CommandSourceStack> context, String name)
            throws CommandSyntaxException {
        return Objects.requireNonNull(Vec3Argument.getVec3(context, name));
    }
}
