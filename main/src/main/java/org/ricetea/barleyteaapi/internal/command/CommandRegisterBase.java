package org.ricetea.barleyteaapi.internal.command;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.command.Command;
import org.ricetea.barleyteaapi.api.command.CommandRegister;
import org.ricetea.barleyteaapi.internal.base.registration.NSKeyedRegisterBase;
import org.ricetea.utils.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApiStatus.Internal
public abstract class CommandRegisterBase<T> extends NSKeyedRegisterBase<Command<T>> implements CommandRegister<T> {

    @Nonnull
    protected static final String LOGGING_REGISTERED_FORMAT = "registered /%1$s as %2$s";

    @Nonnull
    protected static final String LOGGING_UNREGISTERED_FORMAT = "unregistered /%s";

    @Nonnull
    private static final ConcurrentHashMap<Class<?>, CommandRegister<?>> registerLookupTable = new ConcurrentHashMap<>();

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
        Command<T> oldCommand = getLookupMap().put(command.getKey(), command);
        if (oldCommand != null)
            unregister0(oldCommand);
        register0(command);
        BarleyTeaAPI apiInst = BarleyTeaAPI.getInstanceUnsafe();
        if (apiInst != null) {
            Logger logger = apiInst.getLogger();
            logger.info(LOGGING_REGISTERED_FORMAT.formatted(command.getKey(), "command"));
        }
    }

    @Override
    public void unregister(@Nullable Command<T> command) {
        if (command == null || !getLookupMap().remove(command.getKey(), command))
            return;
        unregister0(command);
        BarleyTeaAPI apiInst = BarleyTeaAPI.getInstanceUnsafe();
        if (apiInst != null) {
            Logger logger = apiInst.getLogger();
            logger.info(LOGGING_UNREGISTERED_FORMAT.formatted(command.getKey()));
        }
    }

    protected abstract void register0(@Nonnull Command<T> command);

    protected abstract void unregister0(@Nonnull Command<T> command);

    @Override
    public void unregisterAll(@Nullable Predicate<Command<T>> predicate) {
        Map<NamespacedKey, Command<T>> lookupMap = getLookupMap();
        Collection<Command<T>> values = lookupMap.values();
        Stream<Command<T>> stream = values.stream();
        if (predicate != null) {
            if (getCachedSize() >= Constants.MIN_ITERATION_COUNT_FOR_PARALLEL)
                stream = stream.parallel();
            stream = stream.filter(predicate);
        }
        Set<Command<T>> commands = stream.collect(Collectors.toUnmodifiableSet());
        commands.forEach(this::unregister0);
        BarleyTeaAPI apiInst = BarleyTeaAPI.getInstanceUnsafe();
        if (apiInst != null) {
            Logger logger = apiInst.getLogger();
            commands.forEach(command ->
                    logger.info(LOGGING_UNREGISTERED_FORMAT.formatted(command.getKey())));
        }
    }
}
