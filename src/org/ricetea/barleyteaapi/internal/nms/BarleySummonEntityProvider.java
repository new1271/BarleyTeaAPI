package org.ricetea.barleyteaapi.internal.nms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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

import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

public final class BarleySummonEntityProvider extends CompletionProviders {
    private static final Lazy<SuggestionProvider<CommandListenerWrapper>> provider = new Lazy<>(
            BarleySummonEntityProvider::build);
    private static final Lazy<EntitySuggestionProvider> suggestionProvider = new Lazy<>(EntitySuggestionProvider::new);

    private static SuggestionProvider<CommandListenerWrapper> build() {
        return CompletionProviders.a(
                new MinecraftKey(NamespacedKeyUtils.Namespace,
                        "summonable_entities_" + ThreadLocalRandom.current().nextInt()),
                suggestionProvider.get());
    }

    public static SuggestionProvider<CommandListenerWrapper> getProvider() {
        return provider.get();
    }

    public static void updateRegisterList() {
        EntitySuggestionProvider suggestionProvider = BarleySummonEntityProvider.suggestionProvider.getUnsafe();
        if (suggestionProvider != null)
            suggestionProvider.updateRegisterList();
    }

    private static class EntitySuggestionProvider
            implements SuggestionProvider<ICompletionProvider>, Iterable<MinecraftKey> {
        final ArrayList<MinecraftKey> builtinKeys = new ArrayList<>();
        List<MinecraftKey> customKeys = null;

        public EntitySuggestionProvider() {
            BuiltInRegistries.h.s().filter(EntityTypes::c)
                    .forEach(entity -> builtinKeys.add(EntityTypes.a(entity)));
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ICompletionProvider> dcommandContext,
                SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
            String lowerCasedRemaining = suggestionsBuilder.getRemainingLowerCase();
            if (!lowerCasedRemaining.contains("/")) {
                if (lowerCasedRemaining.isBlank() || lowerCasedRemaining.contains(":")) {
                    return ICompletionProvider.a(this, suggestionsBuilder);
                } else {
                    if (customKeys == null) {
                        customKeys = Arrays.stream(EntityRegister.getInstance().getEntityIDs(null))
                                .map(key -> MinecraftKey.a(key.getNamespace(), key.getKey())).toList();
                    }
                    builtinKeys.stream()
                            .filter(key -> key.a().startsWith(lowerCasedRemaining)
                                    || key.b().startsWith(lowerCasedRemaining))
                            .forEach(key -> suggestionsBuilder.suggest(key.toString()));
                    customKeys.stream()
                            .filter(key -> key.a().startsWith(lowerCasedRemaining)
                                    || key.b().startsWith(lowerCasedRemaining))
                            .forEach(key -> suggestionsBuilder.suggest(key.toString()));
                }
            }
            return suggestionsBuilder.buildFuture();
        }

        public void updateRegisterList() {
            customKeys = null;
        }

        public Iterator<MinecraftKey> iterator() {
            if (customKeys == null) {
                customKeys = Arrays.stream(EntityRegister.getInstance().getEntityIDs(null))
                        .map(key -> MinecraftKey.a(key.getNamespace(), key.getKey())).toList();
            }
            return new IteratorForEntityKey(builtinKeys, customKeys);
        }

        private static class IteratorForEntityKey implements Iterator<MinecraftKey> {

            boolean isInBuiltin = true;
            ArrayList<MinecraftKey> _builtins;
            List<MinecraftKey> _another;
            Iterator<MinecraftKey> currentIterator;

            public IteratorForEntityKey(ArrayList<MinecraftKey> builtins, List<MinecraftKey> another) {
                _builtins = builtins;
                _another = another;
            }

            @Override
            public boolean hasNext() {
                Iterator<MinecraftKey> iterator = currentIterator;
                if (iterator == null)
                    currentIterator = iterator = isInBuiltin ? _builtins.iterator() : _another.iterator();
                boolean result = iterator.hasNext();
                if (!result && isInBuiltin) {
                    isInBuiltin = false;
                    currentIterator = iterator = _another.iterator();
                    result = iterator.hasNext();
                }
                return result;
            }

            @Override
            public MinecraftKey next() {
                Iterator<MinecraftKey> iterator = currentIterator;
                if (iterator == null)
                    currentIterator = iterator = isInBuiltin ? _builtins.iterator() : _another.iterator();
                if (!iterator.hasNext() && isInBuiltin) {
                    isInBuiltin = false;
                    currentIterator = iterator = _another.iterator();
                }
                return iterator.next();
            }
        }
    }
}
