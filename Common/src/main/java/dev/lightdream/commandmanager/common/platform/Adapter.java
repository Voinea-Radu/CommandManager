package dev.lightdream.commandmanager.common.platform;

import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.command.IPlatformCommand;

public interface Adapter
        <NativePlayer extends NativeCommandSender, NativeConsole extends NativeCommandSender, NativeCommandSender> {

    PlatformPlayer convertPlayer(NativePlayer nativePLayer);

    PlatformConsole convertConsole(NativeConsole nativeConsole);

    default PlatformCommandSender convertCommandSender(NativeCommandSender nativeCommandSender) {
        if (getNativePlayerClass().isInstance(nativeCommandSender)) {
            //noinspection unchecked
            return convertPlayer((NativePlayer) nativeCommandSender);
        }

        if (getNativeConsoleClass().isInstance(nativeCommandSender)) {
            //noinspection unchecked
            return convertConsole((NativeConsole) nativeCommandSender);
        }

        throw createConversionError(nativeCommandSender, PlatformCommandSender.class);
    }

    IPlatformCommand convertCommand(CommonCommand command);

    default RuntimeException createConversionError(Class<?> fromClass, Class<?> toClass) {
        return new RuntimeException("Can not convert from " + fromClass.getName() + " to " + toClass.getName());
    }

    default RuntimeException createConversionError(Object fromObject, Class<?> toClass) {
        return createConversionError(fromObject.getClass(), toClass);
    }

    Class<NativePlayer> getNativePlayerClass();

    Class<NativeConsole> getNativeConsoleClass();
}
