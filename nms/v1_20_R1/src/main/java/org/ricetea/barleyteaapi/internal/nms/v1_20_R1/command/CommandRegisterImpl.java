package org.ricetea.barleyteaapi.internal.nms.v1_20_R1.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.ricetea.barleyteaapi.api.command.Command;
import org.ricetea.barleyteaapi.api.command.CommandRegister;
import org.ricetea.barleyteaapi.api.command.CommandRegistrationContext;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class CommandRegisterImpl extends CommandRegister<CommandDispatcher<CommandSourceStack>> {

    private static class CommandRegistrationContextImpl
            extends CommandRegistrationContext<CommandDispatcher<CommandSourceStack>> {

        private final Lazy<CommandDispatcher<CommandSourceStack>> contextLazy =
                Lazy.createInThreadSafe(() ->
                        Objects.requireNonNull(
                                ((CraftServer) Bukkit.getServer()).getServer().
                                        vanillaCommandDispatcher.getDispatcher()));

        @Nonnull
        @Override
        public CommandDispatcher<CommandSourceStack> getContext() {
            return contextLazy.get();
        }
    }

    private final @Nonnull CommandRegistrationContextImpl registrationContext = new CommandRegistrationContextImpl();

    @Override
    protected void register0(@Nonnull Command<CommandDispatcher<CommandSourceStack>> command) {
        command.register(registrationContext);
    }

    @Override
    protected void unregister0(@Nonnull Command<CommandDispatcher<CommandSourceStack>> command) {
        command.unregister(registrationContext);
    }
}