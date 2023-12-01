package org.ricetea.barleyteaapi.internal.nms.command;

import javax.annotation.Nonnull;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandListenerWrapper;

public interface NMSCommand extends Keyed {
    void register(@Nonnull CommandDispatcher<CommandListenerWrapper> dispatcher);

    void unregister(@Nonnull CommandDispatcher<CommandListenerWrapper> dispatcher);

    @Nonnull
    NamespacedKey[] getAliases();
}
