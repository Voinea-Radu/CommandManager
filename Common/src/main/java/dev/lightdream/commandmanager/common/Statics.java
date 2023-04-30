package dev.lightdream.commandmanager.common;

import lombok.Setter;

public class Statics {


    private static @Setter CommonCommandMain main;


    @SuppressWarnings("unchecked")
    public static <T extends CommonCommandMain> T getMainAs(Class<T> clazz) {
        if (clazz.equals(CommonCommandMain.class)) {
            return (T) main;
        }

        if (clazz.isAssignableFrom(main.getClass())) {
            return (T) main;
        }

        throw new RuntimeException("Tried to get CommandMain object as " + clazz.getName() + " but it was set as " +
                main.getClass().getName());
    }

}
