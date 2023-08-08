package org.ricetea.barleyteaapi.internal.nms;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.synchronization.CompletionProviders;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.EntityTypes;

import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.api.entity.feature.ICommandSummon;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

public final class BarleySummonEntityProvider extends CompletionProviders {
    private static SuggestionProvider<CommandListenerWrapper> provider;

    public static SuggestionProvider<CommandListenerWrapper> getProvider() {
        if (provider == null) {
            provider = CompletionProviders.a(
                    new MinecraftKey(NamespacedKeyUtils.Namespace,
                            "summonable_entities_" + ThreadLocalRandom.current().nextInt()),
                    new EntitySuggestionProvider());
        }
        return provider;
    }

    private static class EntitySuggestionProvider implements SuggestionProvider<ICompletionProvider> {
        ArrayList<MinecraftKey> entities = new ArrayList<>();

        public EntitySuggestionProvider() {
            BuiltInRegistries.h.s().filter(EntityTypes::c).forEach(entity -> entities.add(EntityTypes.a(entity)));
            for (NamespacedKey namespacedKey : EntityRegister.getInstance()
                    .getEntityIDs(e -> e instanceof ICommandSummon)) {
                entities.add(MinecraftKey.a(namespacedKey.getNamespace(), namespacedKey.getKey()));
            }
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ICompletionProvider> dcommandContext,
                SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
            String lowerCasedRemaining = suggestionsBuilder.getRemainingLowerCase();
            if (!lowerCasedRemaining.contains("/")) {
                if (lowerCasedRemaining.isBlank() || lowerCasedRemaining.contains(":")) {
                    return ICompletionProvider.a(entities, suggestionsBuilder);
                } else {
                    entities.stream()
                            .filter(key -> key.a().startsWith(lowerCasedRemaining)
                                    || key.b().startsWith(lowerCasedRemaining))
                            .forEach(key -> suggestionsBuilder.suggest(key.toString()));
                }
            }
            return suggestionsBuilder.buildFuture();
        }

    }
}
