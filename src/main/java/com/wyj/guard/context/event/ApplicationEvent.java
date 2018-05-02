package com.wyj.guard.context.event;

public abstract class ApplicationEvent<T>{

    private final T source;

    /** System time when the event happened */
    private final long timestamp;

    public ApplicationEvent(T source) {
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }

    public final T getSource() {
        return this.source;
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

}
