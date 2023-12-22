package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.*;

public final class CachedList<T> implements List<T> {
    private T[] _array;
    private ArrayList<T> _list;
    private final Class<T> clazz;

    public CachedList(Class<T> clazz) {
        _list = new ArrayList<>();
        this.clazz = clazz;
    }

    public CachedList(Class<T> clazz, int initialCapacity) {
        _list = new ArrayList<>(initialCapacity);
        this.clazz = clazz;
    }

    public CachedList(Class<T> clazz, Collection<? extends T> collection) {
        _list = new ArrayList<>(collection);
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

    @Override
    public Iterator<T> iterator() {
        if (_list != null)
            return _list.iterator();
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

    @Override
    public Object[] toArray() {
        Object[] result = listToArray();
        return Objects.requireNonNullElseGet(result, () -> new Object[0]);
    }

    @Nonnull
    @Override
    public <L> L[] toArray(@Nonnull L[] a) {
        return arrayToList().toArray(a);
    }

    @Nullable
    public T[] toArrayCasted() {
        return listToArray();
    }

    @Override
    public boolean add(T e) {
        ArrayList<T> list = _list;
        if (list == null)
            list = arrayToList();
        return list.add(e);
    }

    @Override
    public boolean remove(Object o) {
        ArrayList<T> list = _list;
        if (list == null)
            list = arrayToList();
        if (list.isEmpty())
            return false;
        else
            return list.remove(o);
    }

    @Override
    public boolean containsAll(@Nullable Collection<?> c) {
        if (c == null)
            return false;
        if (_list != null)
            return _list.containsAll(c);
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
        ArrayList<T> list = _list;
        if (list == null)
            list = arrayToList();
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index,@Nullable  Collection<? extends T> c) {
        if (c == null)
            return false;
        ArrayList<T> list = _list;
        if (list == null)
            list = arrayToList();
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(@Nullable Collection<?> c) {
        if (c == null)
            return false;
        ArrayList<T> list = _list;
        if (list == null)
            list = arrayToList();
        if (list.isEmpty())
            return false;
        else
            return list.removeAll(c);
    }

    @Override
    public boolean retainAll(@Nullable Collection<?> c) {
        if (c == null)
            return false;
        ArrayList<T> list = _list;
        if (list == null)
            list = arrayToList();
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
    @Override
    public T get(int index) {
        if (_list != null)
            return _list.get(index);
        if (_array != null)
            return _array[index];
        return null;
    }

    @Nullable
    @Override
    public T set(int index, T element) {
        ArrayList<T> list = _list;
        if (list == null)
            list = arrayToList();
        if (list.isEmpty())
            return null;
        else
            return list.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        ArrayList<T> list = _list;
        if (list == null)
            list = arrayToList();
        list.add(index, element);
    }

    @Nullable
    @Override
    public T remove(int index) {
        ArrayList<T> list = _list;
        if (list == null)
            list = arrayToList();
        if (list.isEmpty())
            return null;
        else
            return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        if (_list != null)
            return _list.indexOf(o);
        if (_array != null) {
            T[] array = _array;
            for (int i = 0, length = _array.length; i < length; i++) {
                T item = array[i];
                if (item == null) {
                    if (o == null)
                        return i;
                } else if (item.equals(o)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (_list != null)
            return _list.lastIndexOf(o);
        if (_array != null) {
            T[] array = _array;
            for (int i = _array.length - 1; i >= 0; i--) {
                T item = array[i];
                if (item == null) {
                    if (o == null)
                        return i;
                } else if (item.equals(o)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Nonnull
    @Override
    public ListIterator<T> listIterator() {
        ArrayList<T> list = _list;
        if (list == null)
            list = arrayToList();
        return list.listIterator();
    }

    @Nonnull
    @Override
    public ListIterator<T> listIterator(int index) {
        ArrayList<T> list = _list;
        if (list == null)
            list = arrayToList();
        return list.listIterator(index);
    }

    @Nonnull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        ArrayList<T> list = _list;
        if (list == null)
            list = arrayToList();
        return list.subList(fromIndex, toIndex);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private T[] listToArray() {
        if (_array == null) {
            if (_list != null) {
                int count = _list.size();
                _array = (T[]) Array.newInstance(clazz, count);
                for (int i = 0; i < count; i++) {
                    _array[i] = _list.get(i);
                }
                _list = null;
            }
        }
        return _array;
    }

    @Nonnull
    private ArrayList<T> arrayToList() {
        ArrayList<T> list = _list;
        if (list == null) {
            if (_array == null) {
                list = new ArrayList<>(0);
            } else {
                list = new ArrayList<>(Arrays.asList(_array));
                _array = null;
            }
        }
        return _list = list;
    }
}
