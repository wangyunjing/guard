package com.wyj.guard.share;

public class Pair<T, E> {
    private final T first;
    private final E second;

    private Pair(T first, E second) {
        this.first = first;
        this.second = second;
    }

    public static <S1, S2> Pair<S1, S2> newPair(S1 first, S2 second) {
        return new Pair<>(first, second);
    }

    public T getFirst() {
        return first;
    }

    public E getSecond() {
        return second;
    }
}

