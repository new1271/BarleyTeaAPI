package org.ricetea.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CachedSet<T> implements Set<T> {
    private T[] _array;
    private HashSet<T> _list;
    private final Class<T> clazz;

    public CachedSet(Class<T> clazz) {
        _list = new HashSet<>();
        this.clazz = clazz;
    }

    public CachedSet(Class<T> clazz, int initialCapacity) {
        _list = new HashSet<>(initialCapacity);
        this.clazz = clazz;
    }

    public CachedSet(Class<T> clazz, Collection<? extends T> collection) {
        _list = new HashSet<>(collection);
        this.clazz = clazz;
    }

    @Override
    public int size() {
        if (_list != null)
            return _list.size();
        if (_array != null)
            return _array.length;
        return 0;
    }

    @Override
    public boolean isEmpty() {
        if (_list != null)
            return _list.isEmpty();
        if (_array != null)
            return _array.length <= 0;
        return true;
    }

    @Override
    public boolean contains(Object o) {
        if (_list != null)
            return _list.contains(o);
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

    @Nullable
    @Override
    public Iterator<T> iterator() {
        if (_list != null)
            return _list.iterator();
        if (_array != null)
            return new Iterator<T>() {
                T[] array = _array;
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

    @Nullable
    @Override
    public Object[] toArray() {
        return listToArray();
    }

    @Override
    public <L> L[] toArray(L[] a) {
        return arrayToSet().toArray(a);
    }

    @Nullable
    public T[] toArrayCasted() {
        return listToArray();
    }

    @Override
    public boolean add(T e) {
        HashSet<T> list = _list;
        if (list == null)
            list = arrayToSet();
        return list.add(e);
    }

    @Override
    public boolean remove(Object o) {
        HashSet<T> list = _list;
        if (list == null)
            list = arrayToSet();
        if (list.isEmpty())
            return false;
        else
            return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c == null)
            return false;
        if (_list != null)
            return _list.containsAll(c);
        if (_array != null) {
            T[] array = _array;
            for (var iterator = c.iterator(); iterator.hasNext();) {
                Object comparedItem = iterator.next();
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
    public boolean addAll(Collection<? extends T> c) {
        HashSet<T> list = _list;
        if (list == null)
            list = arrayToSet();
        return list.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        HashSet<T> list = _list;
        if (list == null)
            list = arrayToSet();
        if (list.isEmpty())
            return false;
        else
            return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        HashSet<T> list = _list;
        if (list == null)
            list = arrayToSet();
        if (list.isEmpty())
            return false;
        else
            return list.retainAll(c);
    }

    @Override
    public void clear() {
        if (_list != null)
            _list.clear();
        if (_array != null)
            _array = null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private T[] listToArray() {
        if (_array == null) {
            if (_list != null) {
                int count = _list.size();
                _array = (T[]) Array.newInstance(clazz, count);
                Iterator<T> iterator = _list.iterator();
                for (int i = 0; i < count && iterator.hasNext(); i++) {
                    _array[i] = iterator.next();
                }
                _list = null;
            }
        }
        return _array;
    }

    @Nonnull
    private HashSet<T> arrayToSet() {
        HashSet<T> list = _list;
        if (list == null) {
            if (_array == null) {
                list = new HashSet<T>(0);
            } else {
                list = new HashSet<T>(Arrays.asList(_array));
                _array = null;
            }
        }
        return _list = list;
    }
}
