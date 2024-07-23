package org.ricetea.barleyteaapi.internal.nms.v1_20_R4.util;

import com.mojang.brigadier.arguments.ArgumentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import javax.annotation.Nonnull;

public final class NMSCommandUtil {

    public static boolean needOp(@Nonnull CommandSourceStack wrapper) {
        return wrapper.hasPermission(2);
    }

    @Nonnull
    public static <T> com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, T> argument(
            @Nonnull String name, ArgumentType<T> argumentType) {
        return Commands.argument(name, argumentType);
    }

    @Nonnull
    public static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> branch(
            @Nonnull String name) {
        return Commands.literal(name);
    }

    public static void sendMessage(@Nonnull CommandSourceStack source, @Nonnull net.minecraft.network.chat.Component component) {
        source.sendSystemMessage(component);
    }

    public static void sendMessage(@Nonnull CommandSourceStack source, @Nonnull Component component) {
        source.getBukkitSender().sendMessage(component);
    }

    public static void sendErrorMessage(@Nonnull CommandSourceStack source,
                                        @Nonnull net.minecraft.network.chat.Component component, boolean withStyle) {
        source.sendFailure(component, withStyle);
    }

    public static void sendErrorMessage(@Nonnull CommandSourceStack source, @Nonnull Component component,
                                        boolean withStyle) {
        if (withStyle) {
            component = component.style(Style.style(NamedTextColor.RED));
        }
        sendMessage(source, component);
    }
}
