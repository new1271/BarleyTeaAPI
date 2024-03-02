package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.*;

public final class CachedSet<T> implements Set<T> {
    private final Class<T> clazz;
    private T[] _array;
    private HashSet<T> _set;

    public CachedSet(Class<T> clazz) {
        _set = new HashSet<>();
        this.clazz = clazz;
    }

    public CachedSet(Class<T> clazz, int initialCapacity) {
        _set = new HashSet<>(initialCapacity);
        this.clazz = clazz;
    }

    public CachedSet(Class<T> clazz, Collection<? extends T> collection) {
        _set = new HashSet<>(collection);
        this.clazz = clazz;
    }

    @Override
    public int size() {
        if (_set != null)
            return _set.size();
        if (_array != null)
            return _array.length;
        return 0;
    }

    @Override
    public boolean isEmpty() {
        if (_set != null)
            return _set.isEmpty();
        if (_array != null)
            return _array.length <= 0;
        return true;
    }

    @Override
    public boolean contains(Object o) {
        if (_set != null)
            return _set.contains(o);
        if (_array != null) {
            T[] array = _array;
            for (int i = 0, length = _array.length; i < length; i++) {
                T item = array[i];
                if (item == null) {
                    if (o == null)
                        return true;
                } else if (item.equals(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        if (_set != null)
            return _set.iterator();
        if (_array != null)
            return new Iterator<>() {
                final T[] array = _array;
                int index = -1;

                @Override
                public boolean hasNext() {
                    return index < array.length - 1;
                }

                @Override
                public T next() {
                    if (hasNext())
                        return array[++index];
                    else
                        return null;
                }

            };
        return Collections.emptyIterator();
    }

    @Nonnull
    @Override
    public Object[] toArray() {
        return ObjectUtil.letNonNull(setToArray(), EmptyArrays::emptyArray);
    }

    @Nonnull
    @Override
    public <L> L[] toArray(@Nonnull L[] a) {
        Set<T> set = _set;
        if (set != null && set.isEmpty()) {
            return EmptyArrays.emptyArray();
        } else {
            T[] array = _array;
            if (array != null && array.length == 0)
                return EmptyArrays.emptyArray();
        }
        return arrayToSet().toArray(a);
    }

    @Nullable
    public T[] toArrayCasted() {
        return setToArray();
    }

    @Override
    public boolean add(T e) {
        HashSet<T> set = _set;
        if (set == null)
            set = arrayToSet();
        return set.add(e);
    }

    @Override
    public boolean remove(Object o) {
        HashSet<T> set = _set;
        if (set == null)
            set = arrayToSet();
        if (set.isEmpty())
            return false;
        else
            return set.remove(o);
    }

    @Override
    public boolean containsAll(@Nullable Collection<?> c) {
        if (c == null)
            return false;
        if (_set != null)
            return _set.containsAll(c);
        if (_array != null) {
            T[] array = _array;
            for (var iterator = c.iterator(); iterator.hasNext(); ) {
                Object comparedItem;
                boolean flag = false;
                for (int i = 0, length = _array.length; i < length; i++) {
                    T item = array[i];
                    comparedItem = iterator.next();
                    if (item == null) {
                        if (comparedItem == null) {
                            flag = true;
                            break;
                        }
                    } else if (item.equals(comparedItem)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(@Nullable Collection<? extends T> c) {
        if (c == null)
            return false;
        HashSet<T> set = _set;
        if (set == null)
            set = arrayToSet();
        return set.addAll(c);
    }

    @Override
    public boolean removeAll(@Nullable Collection<?> c) {
        if (c == null)
            return false;
        HashSet<T> set = _set;
        if (set == null)
            set = arrayToSet();
        if (set.isEmpty())
            return false;
        else
            return set.removeAll(c);
    }

    @Override
    public boolean retainAll(@Nullable Collection<?> c) {
        if (c == null)
            return false;
        HashSet<T> set = _set;
        if (set == null)
            set = arrayToSet();
        if (set.isEmpty())
            return false;
        else
            return set.retainAll(c);
    }

    @Override
    public void clear() {
        if (_set != null)
            _set.clear();
        if (_array != null)
            _array = null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private T[] setToArray() {
        if (_array == null) {
            if (_set != null) {
                int count = _set.size();
                if (count == 0) {
                    _array = EmptyArrays.emptyArray();
                } else {
                    _array = (T[]) Array.newInstance(clazz, count);
                    Iterator<T> iterator = _set.iterator();
                    for (int i = 0; i < count && iterator.hasNext(); i++) {
                        _array[i] = iterator.next();
                    }
                }
                _set = null;
            }
        }
        return _array;
    }

    @Nonnull
    private HashSet<T> arrayToSet() {
        HashSet<T> set = _set;
        if (set == null) {
            if (_array == null || _array.length == 0) {
                set = new HashSet<>(0);
            } else {
                set = new HashSet<>(Arrays.asList(_array));
                _array = null;
            }
        }
        return _set = set;
    }
}
