package com.wyj.guard.context.event;

import java.util.EventObject;

public abstract class ApplicationEvent extends EventObject{

    /** System time when the event happened */
    private final long timestamp;

    public ApplicationEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

}
