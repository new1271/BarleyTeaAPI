package org.ricetea.barleyteaapi.internal.nms;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.synchronization.CompletionProviders;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.Item;

import org.ricetea.barleyteaapi.api.item.feature.FeatureCommandGive;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class BarleyGiveItemProvider extends CompletionProviders {
    private static final Lazy<SuggestionProvider<CommandListenerWrapper>> provider = new Lazy<>(
            BarleyGiveItemProvider::build);
    private static final Lazy<ItemSuggestionProvider> suggestionProvider = new Lazy<>(ItemSuggestionProvider::new);

    private static SuggestionProvider<CommandListenerWrapper> build() {
        return CompletionProviders.a(
                new MinecraftKey(NamespacedKeyUtils.Namespace,
                        "givable_items_" + ThreadLocalRandom.current().nextInt()),
                suggestionProvider.get());
    }

    public static SuggestionProvider<CommandListenerWrapper> getProvider() {
        return provider.get();
    }

    public static void updateRegisterList() {
        ItemSuggestionProvider suggestionProvider = BarleyGiveItemProvider.suggestionProvider.getUnsafe();
        if (suggestionProvider != null)
            suggestionProvider.updateRegisterList();
    }

    private static class ItemSuggestionProvider
            implements SuggestionProvider<ICompletionProvider>, Iterable<MinecraftKey> {
        @Nonnull
        final List<MinecraftKey> builtinKeys;
        @Nullable
        List<MinecraftKey> customKeys = null;

        public ItemSuggestionProvider() {
            RegistryBlocks<Item> itemRegistries = BuiltInRegistries.i;
            builtinKeys = ObjectUtil.letNonNull(
                    itemRegistries.s().map(itemType -> itemRegistries.b(itemType)).toList(),
                    Collections::emptyList);
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ICompletionProvider> dcommandContext,
                SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
            String lowerCasedRemaining = suggestionsBuilder.getRemainingLowerCase();
            if (!lowerCasedRemaining.contains("/")) {
                if (lowerCasedRemaining.isBlank() || lowerCasedRemaining.contains(":")) {
                    return ICompletionProvider.a(this, suggestionsBuilder);
                } else {
                    List<MinecraftKey> customKeys = this.customKeys;
                    if (customKeys == null) {
                        ItemRegister register = ItemRegister.getInstanceUnsafe();
                        if (register != null) {
                            this.customKeys = customKeys = Arrays
                                    .stream(register.getItemIDs(type -> type instanceof FeatureCommandGive))
                                    .map(key -> MinecraftKey.a(key.getNamespace(), key.getKey())).toList();
                        }
                    }
                    builtinKeys.stream()
                            .filter(key -> key.a().startsWith(lowerCasedRemaining)
                                    || key.b().startsWith(lowerCasedRemaining))
                            .forEach(key -> suggestionsBuilder.suggest(key.toString()));
                    if (customKeys != null) {
                        customKeys.stream()
                                .filter(key -> key.a().startsWith(lowerCasedRemaining)
                                        || key.b().startsWith(lowerCasedRemaining))
                                .forEach(key -> suggestionsBuilder.suggest(key.toString()));
                    }
                }
            }
            return suggestionsBuilder.buildFuture();
        }

        public void updateRegisterList() {
            customKeys = null;
        }

        public Iterator<MinecraftKey> iterator() {
            List<MinecraftKey> customKeys = this.customKeys;
            if (customKeys == null) {
                ItemRegister register = ItemRegister.getInstanceUnsafe();
                if (register != null) {
                    this.customKeys = customKeys = Arrays
                            .stream(register.getItemIDs(type -> type instanceof FeatureCommandGive))
                            .map(key -> MinecraftKey.a(key.getNamespace(), key.getKey())).toList();
                }
            }
            return new IteratorForItemKey(builtinKeys, ObjectUtil.letNonNull(customKeys, Collections::emptyList));
        }

        private static class IteratorForItemKey implements Iterator<MinecraftKey> {

            boolean isInBuiltin = true;
            @Nonnull
            List<MinecraftKey> _builtins;
            @Nonnull
            List<MinecraftKey> _another;
            Iterator<MinecraftKey> currentIterator;

            public IteratorForItemKey(@Nonnull List<MinecraftKey> builtins, @Nonnull List<MinecraftKey> another) {
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
