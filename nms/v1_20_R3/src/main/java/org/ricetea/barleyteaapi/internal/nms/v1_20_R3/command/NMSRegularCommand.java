package org.ricetea.barleyteaapi.internal.nms.v1_20_R3.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.api.command.Command;
import org.ricetea.barleyteaapi.api.command.CommandRegistrationContext;

import javax.annotation.Nonnull;
import java.util.Locale;

public abstract class NMSRegularCommand implements Command<CommandDispatcher<CommandSourceStack>> {

    @Nonnull
    private final NamespacedKey key;

    @Nonnull
    private final NamespacedKey[] aliases;

    public NMSRegularCommand(@Nonnull NamespacedKey key, @Nonnull NamespacedKey... aliases) {
        this.key = key;
        this.aliases = aliases;
    }

    // Original code is from https://github.com/PaperMC/Velocity/blob/8abc9c80a69158ebae0121fda78b55c865c0abad/proxy/src/main/java/com/velocitypowered/proxy/util/BrigadierUtils.java#L38
    public static <T> LiteralArgumentBuilder<T> buildRedirect(
            final String alias, final LiteralCommandNode<T> destination) {
        // Redirects only work for nodes with children, but break the top argument-less command.
        // Manually adding the root command after setting the redirect doesn't fix it.
        // See https://github.com/Mojang/brigadier/issues/46). Manually clone the node instead.
        LiteralArgumentBuilder<T> builder = LiteralArgumentBuilder
                .<T>literal(alias.toLowerCase(Locale.ENGLISH))
                .requires(destination.getRequirement())
                .forward(
                        destination.getRedirect(), destination.getRedirectModifier(), destination.isFork())
                .executes(destination.getCommand());
        for (CommandNode<T> child : destination.getChildren()) {
            builder.then(child);
        }
        return builder;
    }

    @Override
    @Nonnull
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    @Nonnull
    public NamespacedKey[] getAliases() {
        return aliases;
    }

    public void register(@Nonnull CommandRegistrationContext<CommandDispatcher<CommandSourceStack>> registrationContext) {
        CommandDispatcher<CommandSourceStack> dispatcher = registrationContext.getContext();
        NamespacedKey key = getKey();
        var node = dispatcher.register(prepareCommand(Commands.literal(key.toString())));
        dispatcher.register(buildRedirect(key.getKey(), node));
        for (NamespacedKey alias : getAliases()) {
            dispatcher.register(buildRedirect(alias.toString(), node));
            dispatcher.register(buildRedirect(alias.getKey(), node));
        }
    }

    @Override
    public void unregister(@Nonnull CommandRegistrationContext<CommandDispatcher<CommandSourceStack>> registrationContext) {
        CommandDispatcher<CommandSourceStack> dispatcher = registrationContext.getContext();
        NamespacedKey key = getKey();
        RootCommandNode<CommandSourceStack> root = dispatcher.getRoot();
        root.removeCommand(key.asString());
        root.removeCommand(key.getKey());
        for (NamespacedKey alias : getAliases()) {
            root.removeCommand(alias.toString());
            root.removeCommand(alias.getKey());
        }
    }

    protected abstract LiteralArgumentBuilder<CommandSourceStack> prepareCommand(
            @Nonnull LiteralArgumentBuilder<CommandSourceStack> builder);

    public void updateSuggestions() {
        //Do nothing
    }
}
