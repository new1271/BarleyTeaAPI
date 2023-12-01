package org.ricetea.barleyteaapi.internal.nms.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.arguments.ArgumentNBTTag;
import net.minecraft.commands.arguments.coordinates.ArgumentVec3;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.phys.Vec3D;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nonnull;

public final class NMSCommandArgument {
    @Nonnull
    public static ArgumentEntity selectPlayer() {
        return Objects.requireNonNull(ArgumentEntity.c());
    }

    @Nonnull
    public static ArgumentEntity selectPlayers() {
        return Objects.requireNonNull(ArgumentEntity.d());
    }

    @Nonnull
    public static ArgumentMinecraftKeyRegistered selectMinecraftKey() {
        return Objects.requireNonNull(ArgumentMinecraftKeyRegistered.a());
    }

    @Nonnull
    public static ArgumentNBTTag selectNBTTag() {
        return Objects.requireNonNull(ArgumentNBTTag.a());
    }

    @Nonnull
    public static ArgumentVec3 selectVector3D() {
        return Objects.requireNonNull(ArgumentVec3.a());
    }

    @Nonnull
    public static EntityPlayer decodePlayerArgumentSingle(CommandContext<CommandListenerWrapper> context, String name)
            throws CommandSyntaxException {
        return Objects.requireNonNull(ArgumentEntity.e(context, name));
    }

    @Nonnull
    public static Collection<EntityPlayer> decodePlayerArgumentMultiple(CommandContext<CommandListenerWrapper> context,
            String name) throws CommandSyntaxException {
        return Objects.requireNonNull(ArgumentEntity.d(context, name));
    }

    @Nonnull
    public static MinecraftKey decodeMinecraftKey(CommandContext<CommandListenerWrapper> context,
            String name) throws CommandSyntaxException {
        return Objects.requireNonNull(ArgumentMinecraftKeyRegistered.e(context, name));
    }

    @Nonnull
    public static NBTTagCompound decodeNBTTag(CommandContext<CommandListenerWrapper> context,
            String name) throws CommandSyntaxException {
        return Objects.requireNonNull(ArgumentNBTTag.a(context, name));
    }

    @Nonnull
    public static Vec3D decodeVector3D(CommandContext<CommandListenerWrapper> context,
            String name) throws CommandSyntaxException {
        return Objects.requireNonNull(ArgumentVec3.a(context, name));
    }
}
