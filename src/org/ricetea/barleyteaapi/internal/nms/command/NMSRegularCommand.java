package org.ricetea.barleyteaapi.internal.nms.command;

import java.util.Locale;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import net.minecraft.commands.CommandListenerWrapper;

public abstract class NMSRegularCommand implements NMSCommand {

    @Nonnull
    private final NamespacedKey key;

    @Nonnull
    private final NamespacedKey[] aliases;

    public NMSRegularCommand(@Nonnull NamespacedKey key, @Nonnull NamespacedKey... aliases) {
        this.key = key;
        this.aliases = aliases;
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

    @Override
    public void register(@Nonnull CommandDispatcher<CommandListenerWrapper> dispatcher) {
        NamespacedKey key = getKey();
        var node = dispatcher.register(prepareCommand(net.minecraft.commands.CommandDispatcher.a(key.toString())));
        dispatcher.register(buildRedirect(key.getKey(), node));
        for (NamespacedKey alias : getAliases()) {
            dispatcher.register(buildRedirect(alias.toString(), node));
            dispatcher.register(buildRedirect(alias.getKey(), node));
        }
    }

    @Override
    public void unregister(@Nonnull CommandDispatcher<CommandListenerWrapper> dispatcher) {
        NamespacedKey key = getKey();
        RootCommandNode<CommandListenerWrapper> root = dispatcher.getRoot();
        root.removeCommand(key.asString());
        root.removeCommand(key.getKey());
        for (NamespacedKey alias : getAliases()) {
            root.removeCommand(alias.toString());
            root.removeCommand(alias.getKey());
        }
    }

    protected abstract LiteralArgumentBuilder<CommandListenerWrapper> prepareCommand(
            @Nonnull LiteralArgumentBuilder<CommandListenerWrapper> builder);

    public void updateSuggestions() {
        //Do nothing
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
}
