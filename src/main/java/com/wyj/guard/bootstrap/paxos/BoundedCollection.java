package com.wyj.guard.bootstrap.paxos;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class BoundedCollection<E> {

    private final int capacity;

    private List<E> data = new LinkedList<>();

    public BoundedCollection(int capacity) {
        this.capacity = capacity;
    }

    public void add(E e) {
        if (capacity < 0 || data.size() < capacity) {
            data.add(e);
            return;
        }
        data.remove(0);
        data.add(e);
    }

    public int getCapacity() {
        return capacity;
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public E get(int idx) {
        if (idx < 0 || idx >= data.size()) {
            return null;
        }
        return data.get(idx);
    }

    public E get(Predicate<E> predicate) {
        return data.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    public E getFirst() {
        if (data.isEmpty()) {
            return null;
        }
        return data.get(0);
    }

    public E getLast() {
        if (data.isEmpty()) {
            return null;
        }
        return data.get(data.size() - 1);
    }

    public Iterator<E> iterator() {
        return data.iterator();
    }
}
