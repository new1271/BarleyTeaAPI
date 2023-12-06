package org.ricetea.barleyteaapi.internal.nms.util;

import javax.annotation.Nonnull;

import com.mojang.brigadier.arguments.ArgumentType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;

public final class NMSCommandUtil {

    public static boolean needOp(@Nonnull CommandListenerWrapper wrapper) {
        return wrapper.c(2);
    }

    @Nonnull
    public static <T> com.mojang.brigadier.builder.RequiredArgumentBuilder<net.minecraft.commands.CommandListenerWrapper, T> argument(
            @Nonnull String name, ArgumentType<T> argumentType) {
        return net.minecraft.commands.CommandDispatcher.a(name, argumentType);
    }

    @Nonnull
    public static com.mojang.brigadier.builder.LiteralArgumentBuilder<net.minecraft.commands.CommandListenerWrapper> branch(
            @Nonnull String name) {
        return net.minecraft.commands.CommandDispatcher.a(name);
    }

    public static void sendMessage(@Nonnull CommandListenerWrapper source, @Nonnull IChatBaseComponent component) {
        source.a(component);
    }

    public static void sendMessage(@Nonnull CommandListenerWrapper source, @Nonnull Component component) {
        source.getBukkitSender().sendMessage(component);
    }

    public static void sendErrorMessage(@Nonnull CommandListenerWrapper source,
            @Nonnull IChatBaseComponent component, boolean withStyle) {
        source.sendFailure(component, withStyle);
    }

    public static void sendErrorMessage(@Nonnull CommandListenerWrapper source, @Nonnull Component component,
            boolean withStyle) {
        if (withStyle) {
            component = component.style(Style.style(NamedTextColor.RED));
        }
        sendMessage(source, component);
    }
}
