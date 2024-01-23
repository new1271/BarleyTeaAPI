package org.ricetea.barleyteaapi.internal.base.registration;

import org.ricetea.barleyteaapi.api.base.registration.StringKeyedRegister;
import org.ricetea.utils.Constants;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class StringKeyedRegisterBase<T> implements StringKeyedRegister<T> {

    @Nonnull
    protected static final String LOGGING_REGISTERED_FORMAT = "registered %1$s as %2$s";

    @Nonnull
    protected static final String LOGGING_UNREGISTERED_FORMAT = "unregistered %s";

    @Nonnull
    private final Map<String, T> lookupMap;

    private int cachedSize;

    protected StringKeyedRegisterBase() {
        lookupMap = createLookupMap();
        cachedSize = 0;
    }

    @Nonnull
    protected Map<String, T> createLookupMap() {
        return new ConcurrentHashMap<>();
    }

    @Nonnull
    protected Map<String, T> getLookupMap() {
        return lookupMap;
    }

    /**
     * Refresh Cached map size<br>
     * Cached map size will be used to optimising searching function like {@link #listAll(Predicate)} or {@link #listAllKeys(Predicate)}
     */
    protected void refreshCachedSize() {
        cachedSize = lookupMap.size();
    }

    /**
     * Get Cached map size<br>
     * Cached map size can be used to optimising searching function without getting real size
     */
    protected int getCachedSize() {
        return cachedSize;
    }

    /**
     * Get key from item<br>
     * Cached map size can be used to optimising searching function without getting real size
     */
    @Nonnull
    protected abstract String getKeyFromItem(@Nonnull T item);

    @Override
    public void register(@Nullable T item) {
        if (item == null)
            return;
        lookupMap.put(getKeyFromItem(item), item);
        refreshCachedSize();
    }

    @Override
    public void registerAll(@Nullable Collection<T> items) {
        if (items == null)
            return;
        items.forEach(item -> ObjectUtil.safeCall(item, (_item) -> lookupMap.put(getKeyFromItem(_item), _item)));
        refreshCachedSize();
    }

    @Override
    public void unregister(@Nullable String key) { //Replaced to better implementation
        if (key == null)
            return;
        lookupMap.remove(key);
        refreshCachedSize();
    }

    @Override
    public void unregister(@Nullable T item) {
        if (item == null)
            return;
        lookupMap.remove(getKeyFromItem(item), item);
        refreshCachedSize();
    }

    @Override
    public void unregisterAll(@Nullable Predicate<T> predicate) {
        if (predicate == null)
            lookupMap.clear();
        else
            lookupMap.values().removeIf(predicate);
        refreshCachedSize();
    }

    @Override
    public boolean isEmpty() { //Replaced to better implementation
        return lookupMap.isEmpty();
    }

    @Nullable
    @Override
    public T lookup(@Nullable String key) {
        if (key == null)
            return null;
        return lookupMap.get(key);
    }

    @Nonnull
    @Override
    public Collection<String> listAllKeys(@Nullable Predicate<String> predicate) {
        if (isEmpty())
            return Collections.emptySet();
        Set<String> keySet = lookupMap.keySet();
        if (predicate == null)
            return Set.copyOf(keySet);
        Stream<String> stream = keySet.stream();
        if (cachedSize >= Constants.MIN_ITERATION_COUNT_FOR_PARALLEL) {
            stream = stream.parallel();
        }
        return stream.filter(predicate).collect(Collectors.toUnmodifiableSet());
    }

    @Nonnull
    @Override
    public Set<T> listAll(@Nullable Predicate<T> predicate) {
        if (isEmpty())
            return Collections.emptySet();
        Collection<T> values = lookupMap.values();
        if (predicate == null)
            return Set.copyOf(values);
        Stream<T> stream = values.stream();
        if (cachedSize >= Constants.MIN_ITERATION_COUNT_FOR_PARALLEL) {
            stream = stream.parallel();
        }
        return stream.filter(predicate).collect(Collectors.toUnmodifiableSet());
    }

    @Nullable
    @Override
    public String findFirstKey(@Nullable Predicate<String> predicate) {
        if (isEmpty())
            return null;
        Stream<String> stream = lookupMap.keySet().stream();
        return ObjectUtil.letNonNull(
                        ObjectUtil.safeMap(predicate, stream::filter),
                        stream
                )
                .findFirst()
                .orElse(null);
    }

    @Nullable
    @Override
    public T findFirst(@Nullable Predicate<T> predicate) {
        if (isEmpty())
            return null;
        Stream<T> stream = lookupMap.values().stream();
        return ObjectUtil.letNonNull(
                        ObjectUtil.safeMap(predicate, stream::filter),
                        stream
                )
                .findFirst()
                .orElse(null);
    }
}
