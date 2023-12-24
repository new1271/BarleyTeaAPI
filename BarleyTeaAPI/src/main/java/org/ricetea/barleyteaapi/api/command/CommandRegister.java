package org.ricetea.barleyteaapi.api.command;

import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Logger;

public abstract class CommandRegister<T> implements IRegister<Command<T>> {

    private static final ConcurrentHashMap<Class<?>, CommandRegister<?>> registerLookupTable = new ConcurrentHashMap<>();

    @Nonnull
    private final ConcurrentHashMap<NamespacedKey, Command<T>> lookupTable = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <C, T extends CommandRegister<C>> T getInstance(@Nonnull Class<T> clazz) {
        return (T) Objects.requireNonNull(registerLookupTable.get(clazz));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <C, T extends CommandRegister<C>> T getInstanceUnsafe(@Nonnull Class<T> clazz) {
        return (T) registerLookupTable.get(clazz);
    }

    public static <C, T extends CommandRegister<C>> void setInstance(@Nullable T register, @Nonnull Class<T> clazz) {
        if (register == null)
            registerLookupTable.remove(clazz);
        else
            registerLookupTable.put(clazz, register);
    }


    @Override
    public void register(@Nullable Command<T> command) {
        if (command == null)
            return;
        Command<T> oldCommand = lookupTable.put(command.getKey(), command);
        if (oldCommand != null)
            unregister0(oldCommand);
        register0(command);
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                logger.info("registered /" + command.getKey() + " as command!");
            }
        }
    }

    @Override
    public void unregister(@Nullable Command<T> command) {
        if (command == null || !lookupTable.remove(command.getKey(), command))
            return;
        unregister0(command);
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                logger.info("unregistered /" + command.getKey());
            }
        }
    }

    protected abstract void register0(@Nonnull Command<T> command);

    protected abstract void unregister0(@Nonnull Command<T> command);

    @Override
    public void unregisterAll() {
        var keySet = Collections.unmodifiableSet(lookupTable.keySet());
        var values = Collections.unmodifiableCollection(lookupTable.values());
        lookupTable.clear();
        values.forEach(this::unregister0);
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                for (var key : keySet) {
                    logger.info("unregistered /" + key);
                }
            }
        }
    }

    @Override
    public void unregisterAll(@Nullable Predicate<Command<T>> predicate) {
        if (predicate == null)
            unregisterAll();
        else
            listAll(predicate).forEach(this::unregister);
    }

    @Nullable
    @Override
    public Command<T> lookup(@Nullable NamespacedKey key) {
        if (key == null)
            return null;
        return lookupTable.get(key);
    }

    @Override
    public boolean has(@Nullable NamespacedKey key) {
        if (key == null)
            return false;
        return lookupTable.containsKey(key);
    }

    @Override
    public boolean hasAnyRegistered() {
        return !lookupTable.isEmpty();
    }

    @Nonnull
    @Override
    public Collection<Command<T>> listAll() {
        return lookupTable.values()
                .stream()
                .toList();
    }

    @Nonnull
    @Override
    public Collection<Command<T>> listAll(@Nullable Predicate<Command<T>> predicate) {
        if (predicate == null)
            return listAll();
        return lookupTable.values()
                .stream()
                .filter(predicate)
                .toList();
    }

    @Nonnull
    @Override
    public Collection<NamespacedKey> listAllKeys() {
        return Collections.unmodifiableSet(lookupTable.keySet());
    }

    @Nonnull
    @Override
    public Collection<NamespacedKey> listAllKeys(@Nullable Predicate<Command<T>> predicate) {
        if (predicate == null)
            return listAllKeys();
        return lookupTable.entrySet()
                .stream()
                .filter(new Filter<>(predicate))
                .map(Map.Entry::getKey)
                .toList();
    }

    @Nullable
    @Override
    public Command<T> findFirst(@Nullable Predicate<Command<T>> predicate) {
        if (predicate == null)
            return null;
        return lookupTable.values()
                .stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    @Nullable
    @Override
    public NamespacedKey findFirstKey(@Nullable Predicate<Command<T>> predicate) {
        if (predicate == null)
            return null;
        return lookupTable.entrySet()
                .stream()
                .filter(new Filter<>(predicate))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
