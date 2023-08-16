package org.ricetea.barleyteaapi.internal.nms;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandListenerWrapper;

public interface NMSBaseCommand {
    void register(CommandDispatcher<CommandListenerWrapper> dispatcher);

    void unregister(CommandDispatcher<CommandListenerWrapper> dispatcher);

    void update();
}
