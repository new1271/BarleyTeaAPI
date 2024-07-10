package org.ricetea.utils.fast;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.ricetea.utils.Constants;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public final class FastStringToObjectMap<V> implements Map<String, V> {

    private final boolean isAsciiString;
    @Nonnull
    private final Class<V> clazz;
    @Nonnull
    private final Long2ObjectMap<Collection<InnerStruct<V>>> innerMap;
    @Nonnull
    private final Lazy<MutableKeySet> mutableKeySetLazy;
    @Nonnull
    private final Lazy<MutableValueCollection> mutableValueCollectionLazy;
    @Nonnull
    private final Lazy<MutableEntrySet> mutableEntrySetLazy;
    @Nonnegative
    private int size;

    public FastStringToObjectMap(@Nonnull Class<V> clazz) {
        this(clazz, false);
    }

    public FastStringToObjectMap(@Nonnull Class<V> clazz, boolean isAsciiString) {
        this.isAsciiString = isAsciiString;
        innerMap = new Long2ObjectArrayMap<>();
        this.clazz = clazz;
        mutableKeySetLazy = Lazy.create(MutableKeySet::new);
        mutableValueCollectionLazy = Lazy.create(MutableValueCollection::new);
        mutableEntrySetLazy = Lazy.create(MutableEntrySet::new);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String _key)
            return containsKey0(_key);
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        V castedObj = ObjectUtil.tryCast(value, clazz);
        if (castedObj == null)
            return false;
        return containsValue0(castedObj);
    }

    @Override
    public V get(Object key) {
        if (!(key instanceof String _key))
            return null;
        long hash = getFastStringHash(_key);
        var list = innerMap.get(hash);
        if (list == null)
            return null;
        int length = list.size();
        if (length < Constants.MIN_ITERATION_COUNT_FOR_PARALLEL) {
            for (InnerStruct<V> struct : list) {
                if (fullStringCompare0(_key, struct.key))
                    return struct.value;
            }
        } else {
            return list.stream()
                    .filter(val -> fullStringCompare0(_key, val.key))
                    .findAny()
                    .map(InnerStruct::getValue)
                    .orElse(null);
        }
        return null;
    }

    @Nullable
    @Override
    public V put(String key, V value) {
        if (key == null)
            return null;
        long hash = getFastStringHash(key);
        innerMap.compute(hash, (val, _list) -> {
            if (_list == null) {
                _list = new ArrayList<>();
                _list.add(new InnerStruct<>(key, value));
                size++;
            }
            for (InnerStruct<V> struct : _list) {
                if (fullStringCompare0(key, struct.key)) {
                    struct.value = value;
                    return _list;
                }
            }
            _list.add(new InnerStruct<>(key, value));
            size++;
            return _list;
        });
        return null;
    }

    @Override
    public V remove(Object key) {
        if (!(key instanceof String _key))
            return null;
        long hash = getFastStringHash(_key);
        var list = innerMap.get(hash);
        if (list == null)
            return null;
        InnerStruct<V> target = null;
        for (InnerStruct<V> struct : list) {
            if (fullStringCompare0(_key, struct.key)) {
                target = struct;
                break;
            }
        }
        if (target == null)
            return null;
        list.remove(target);
        size--;
        if (list.isEmpty())
            innerMap.remove(hash, list);
        return target.value;
    }

    @Override
    public void putAll(@Nonnull Map<? extends String, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        innerMap.clear();
    }

    @Nonnull
    @Override
    public Set<String> keySet() {
        return mutableKeySetLazy.get();
    }

    @Nonnull
    @Override
    public Collection<V> values() {
        return mutableValueCollectionLazy.get();
    }

    @Nonnull
    @Override
    public Set<Entry<String, V>> entrySet() {
        return mutableEntrySetLazy.get();
    }

    private boolean containsKey0(@Nonnull String key) {
        long hash = getFastStringHash(key);
        var list = innerMap.get(hash);
        if (list == null)
            return false;
        int length = list.size();
        if (length > Constants.MIN_ITERATION_COUNT_FOR_PARALLEL) {
            return list.parallelStream()
                    .anyMatch(val -> fullStringCompare0(key, val.key));
        } else {
            for (InnerStruct<V> struct : list) {
                if (key.equals(struct.key)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsValue0(@Nonnull V value) {
        var values = innerMap.values();
        Stream<Collection<InnerStruct<V>>> stream;
        if (size > Constants.MIN_ITERATION_COUNT_FOR_PARALLEL) {
            stream = values.parallelStream();
        } else {
            stream = values.stream();
        }
        return stream.flatMap(Collection::stream)
                .anyMatch(val -> value.equals(val.value));
    }

    private long getFastStringHash(@Nullable String key) {
        if (key == null)
            return 0;
        return isAsciiString ? getFastStringHashAscii(key) : getFastStringHashUni(key);
    }

    private long getFastStringHashUni(@Nonnull String key) {
        int length = key.length();
        if (length < 1)
            return 0;
        long first, last2, last;
        switch (length) {
            case 1 -> {
                first = key.charAt(0);
                last2 = 0;
                last = 0;
            }
            case 2 -> {
                first = key.charAt(0);
                last2 = 0;
                last = key.charAt(1);
            }
            case 3 -> {
                first = key.charAt(0);
                last2 = key.charAt(1);
                last = key.charAt(2);
            }
            default -> {
                first = key.charAt(0);
                last2 = key.charAt(length - 2);
                last = key.charAt(length - 1);
            }
        }
        return first << 48 | last2 << 32 | last << 16 | (length & 0xFF);
    }

    private long getFastStringHashAscii(@Nonnull String key) {
        int length = key.length();
        if (length < 1)
            return 0;
        long f1, f2, l1, l2, l3, l4;
        switch (length) {
            case 1 -> {
                f1 = key.charAt(0);
                f2 = 0;
                l1 = 0;
                l2 = 0;
                l3 = 0;
                l4 = 0;
            }
            case 2 -> {
                f1 = key.charAt(0);
                f2 = 0;
                l1 = 0;
                l2 = 0;
                l3 = 0;
                l4 = key.charAt(1);
            }
            case 3 -> {
                f1 = key.charAt(0);
                f2 = key.charAt(1);
                l1 = 0;
                l2 = 0;
                l3 = 0;
                l4 = key.charAt(2);
            }
            case 4 -> {
                f1 = key.charAt(0);
                f2 = key.charAt(1);
                l1 = 0;
                l2 = 0;
                l3 = key.charAt(2);
                l4 = key.charAt(3);
            }
            case 5 -> {
                f1 = key.charAt(0);
                f2 = key.charAt(1);
                l1 = 0;
                l2 = key.charAt(2);
                l3 = key.charAt(3);
                l4 = key.charAt(4);
            }
            case 6 -> {
                f1 = key.charAt(0);
                f2 = key.charAt(1);
                l1 = key.charAt(2);
                l2 = key.charAt(3);
                l3 = key.charAt(4);
                l4 = key.charAt(5);
            }
            default -> {
                f1 = key.charAt(0);
                f2 = key.charAt(1);
                l1 = key.charAt(length - 4);
                l2 = key.charAt(length - 3);
                l3 = key.charAt(length - 2);
                l4 = key.charAt(length - 1);
            }
        }
        return f1 << 56 | f2 << 48 | l1 << 40 | l2 << 32 | l3 << 24 | l4 << 16 | (length & 0xFF);
    }

    @SuppressWarnings("unused")
    private boolean fullStringCompare(@Nullable String a, @Nullable String b) {
        if (a == null)
            return b == null;
        return fullStringCompare0(a, b);
    }

    @SuppressWarnings("StringEquality")
    private boolean fullStringCompare0(@Nonnull String a, @Nullable String b) {
        if (a == b)
            return true;
        if (b == null)
            return false;
        int length = a.length();
        if (length != b.length())
            return false;
        for (int i = 0; i < length; i++) {
            if (a.charAt(i) != b.charAt(i))
                return false;
        }
        return true;
    }

    private boolean removeValueUnsafe(@Nullable Object value) {
        V castedObj = ObjectUtil.tryCast(value, clazz);
        if (castedObj == null)
            return false;
        return removeValue(castedObj);
    }

    private boolean removeValue(@Nullable V value) {
        return innerMap.values().removeIf(list ->
                list.removeIf(struct -> Objects.equals(value, struct.value)) && list.isEmpty()
        );
    }

    private static final class InnerStruct<V> implements Entry<String, V> {
        @Nonnull
        public String key;

        @Nullable
        public V value;

        public InnerStruct(@Nonnull String key, @Nullable V value) {
            this.key = key;
            this.value = value;
        }

        @Nonnull
        public String getKey() {
            return key;
        }

        @Nullable
        public V getValue() {
            return value;
        }

        public V setValue(@Nullable V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

    private class MutableKeySet implements Set<String> {

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return FastStringToObjectMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof String _key)
                return FastStringToObjectMap.this.containsKey0(_key);
            return false;
        }

        @Override
        public Stream<String> stream() {
            return FastStringToObjectMap.this.innerMap
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .map(InnerStruct::getKey);
        }

        @Override
        public Stream<String> parallelStream() {
            return stream().parallel();
        }

        @Nonnull
        @Override
        public Iterator<String> iterator() {
            return stream().iterator();
        }

        @SuppressWarnings("SimplifyStreamApiCallChains")
        @Nonnull
        @Override
        public Object[] toArray() {
            return stream().toArray();
        }

        @SuppressWarnings("unchecked")
        @Nonnull
        @Override
        public <T> T[] toArray(@Nonnull T[] a) {
            return (T[]) toArray();
        }

        @Override
        public boolean add(String s) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof String _key)
                return FastStringToObjectMap.this.remove(_key) != null;
            return false;
        }

        @SuppressWarnings("SuspiciousMethodCalls")
        @Override
        public boolean containsAll(@Nonnull Collection<?> c) {
            FastStringToObjectMap<V> map = FastStringToObjectMap.this;
            return c.stream().allMatch(map::containsKey);
        }

        @Override
        public boolean addAll(@Nonnull Collection<? extends String> c) {
            return false;
        }

        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            return false;
        }

        @SuppressWarnings("SuspiciousMethodCalls")
        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            FastStringToObjectMap<V> map = FastStringToObjectMap.this;
            return c.stream().allMatch(val -> map.remove(val) != null);
        }

        @Override
        public void clear() {
            FastStringToObjectMap.this.clear();
        }
    }

    private class MutableValueCollection implements Collection<V> {

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return FastStringToObjectMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return FastStringToObjectMap.this.containsValue(o);
        }

        @Override
        public Stream<V> stream() {
            return FastStringToObjectMap.this.innerMap
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .map(InnerStruct::getValue);
        }

        @Override
        public Stream<V> parallelStream() {
            return stream().parallel();
        }

        @Nonnull
        @Override
        public Iterator<V> iterator() {
            return stream().iterator();
        }

        @SuppressWarnings("SimplifyStreamApiCallChains")
        @Nonnull
        @Override
        public Object[] toArray() {
            return stream().toArray();
        }

        @SuppressWarnings("unchecked")
        @Nonnull
        @Override
        public <T> T[] toArray(@Nonnull T[] a) {
            return (T[]) toArray();
        }

        @Override
        public boolean add(V s) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            V castedObj = ObjectUtil.tryCast(o, clazz);
            if (castedObj == null)
                return false;
            return FastStringToObjectMap.this.removeValue(castedObj);
        }

        @SuppressWarnings("SuspiciousMethodCalls")
        @Override
        public boolean containsAll(@Nonnull Collection<?> c) {
            FastStringToObjectMap<V> map = FastStringToObjectMap.this;
            return c.stream().allMatch(map::containsValue);
        }

        @Override
        public boolean addAll(@Nonnull Collection<? extends V> c) {
            return false;
        }

        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            return false;
        }

        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            FastStringToObjectMap<V> map = FastStringToObjectMap.this;
            return c.stream().allMatch(map::removeValueUnsafe);
        }

        @Override
        public void clear() {
            FastStringToObjectMap.this.clear();
        }
    }

    private class MutableEntrySet implements Set<Entry<String, V>> {

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return FastStringToObjectMap.this.isEmpty();
        }

        @Override
        public Stream<Entry<String, V>> stream() {
            return FastStringToObjectMap.this.innerMap
                    .values()
                    .stream()
                    .flatMap(Collection::stream);
        }

        @Override
        public Stream<Entry<String, V>> parallelStream() {
            return stream().parallel();
        }

        @Override
        public boolean add(Entry<String, V> s) {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Entry<?, ?> entry))
                return false;
            V value = FastStringToObjectMap.this.get(entry.getKey());
            if (value == null)
                return false;
            return Objects.equals(entry.getValue(), value);
        }

        @Nonnull
        @Override
        public Iterator<Entry<String, V>> iterator() {
            return stream().iterator();
        }

        @SuppressWarnings("SimplifyStreamApiCallChains")
        @Nonnull
        @Override
        public Object[] toArray() {
            return stream().toArray();
        }

        @SuppressWarnings("unchecked")
        @Nonnull
        @Override
        public <T> T[] toArray(@Nonnull T[] a) {
            return (T[]) toArray();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Entry<?, ?> entry))
                return false;
            return FastStringToObjectMap.this.remove(entry.getKey(), entry.getValue());
        }

        @Override
        public boolean containsAll(@Nonnull Collection<?> c) {
            return c.stream().allMatch(this::contains);
        }

        @Override
        public boolean addAll(@Nonnull Collection<? extends Entry<String, V>> c) {
            return false;
        }

        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            return false;
        }

        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            return c.stream().allMatch(this::remove);
        }

        @Override
        public void clear() {
            FastStringToObjectMap.this.clear();
        }
    }
}
