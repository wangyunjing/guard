package com.wyj.guard.share;

public interface Closeable {

    boolean virtualClose();

    boolean physicalClose();

    boolean selfClose();

}
