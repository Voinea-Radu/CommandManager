package dev.lightdream.commandmanager.common.platform;

public interface IPlatformObject {

     Object getNative();

     Adapter<?, ?, ?> getAdapter();

}
