package dev.lightdream.commandmanager.common.utils;

import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {

    public static String listToString(List<String> list, String separator) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
            sb.append(separator);
        }
        return sb.toString();
    }

    public static List<String> getListThatStartsWith(List<String> list, String prefix) {
        return list.stream().filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase())).collect(Collectors.toList());
    }

}
