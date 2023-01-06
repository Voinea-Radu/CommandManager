package dev.lightdream.commandmanager.common.utils;

import java.util.List;

public class ListUtils {

    public static String listToString(List<String> list, String separator) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
            sb.append(separator);
        }
        return sb.toString();
    }

}
