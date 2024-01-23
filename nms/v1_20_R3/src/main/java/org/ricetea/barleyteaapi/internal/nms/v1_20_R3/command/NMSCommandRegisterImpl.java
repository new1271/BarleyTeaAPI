package org.ricetea.barleyteaapi.internal.nms.v1_20_R3.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.ricetea.barleyteaapi.api.command.Command;
import org.ricetea.barleyteaapi.api.command.CommandRegistrationContext;
import org.ricetea.barleyteaapi.internal.command.CommandRegisterBase;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class NMSCommandRegisterImpl extends CommandRegisterBase<CommandDispatcher<CommandSourceStack>> {

    private final @Nonnull CommandRegistrationContextImpl registrationContext = new CommandRegistrationContextImpl();

    @Override
    protected void register0(@Nonnull Command<CommandDispatcher<CommandSourceStack>> command) {
        command.register(registrationContext);
    }

    @Override
    protected void unregister0(@Nonnull Command<CommandDispatcher<CommandSourceStack>> command) {
        command.unregister(registrationContext);
    }

    private static class CommandRegistrationContextImpl
            implements CommandRegistrationContext<CommandDispatcher<CommandSourceStack>> {

        private final Lazy<CommandDispatcher<CommandSourceStack>> contextLazy =
                Lazy.createThreadSafe(() ->
                        Objects.requireNonNull(
                                ((CraftServer) Bukkit.getServer()).getServer().
                                        vanillaCommandDispatcher.getDispatcher()));

        @Nonnull
        @Override
        public CommandDispatcher<CommandSourceStack> getContext() {
            return contextLazy.get();
        }
    }
}
