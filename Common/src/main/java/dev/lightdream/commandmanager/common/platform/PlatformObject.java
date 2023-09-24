package dev.lightdream.commandmanager.common.platform;

public abstract class PlatformObject {

    protected final Object nativeObject;
    protected final Adapter<?, ?, ?> adapter;

    public PlatformObject(Object nativeObject, Adapter<?, ?, ?> adapter) {
        this.nativeObject = nativeObject;
        this.adapter = adapter;
    }

    public abstract Object getNative();

    public abstract Adapter<?, ?, ?> getAdapter();

}
