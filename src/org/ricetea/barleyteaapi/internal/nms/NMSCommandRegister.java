package org.ricetea.barleyteaapi.internal.nms;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.ObjectUtil;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandListenerWrapper;

public final class NMSCommandRegister {
    private final static Lazy<NMSCommandRegister> _inst = new Lazy<>(NMSCommandRegister::new);

    @Nonnull
    private final CommandDispatcher<CommandListenerWrapper> dispatcher;

    private NMSCommandRegister() {
        dispatcher = ObjectUtil.throwWhenNull(((CraftServer) Bukkit.getServer()).getServer().aC().a());
    }

    @Nonnull
    public static NMSCommandRegister getInstance() {
        return _inst.get();
    }

    public void register(NMSBaseCommand command) {
        command.register(dispatcher);
    }

    public void unregister(NMSBaseCommand command) {
        command.register(dispatcher);
    }

    public void registerAll() {

    }
}
