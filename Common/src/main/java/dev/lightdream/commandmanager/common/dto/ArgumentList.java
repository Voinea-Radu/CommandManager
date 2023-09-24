package dev.lightdream.commandmanager.common.dto;

import dev.lightdream.commandmanager.common.command.CommonCommand;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ArgumentList {

    private List<String> arguments;
    private CommonCommand command;

    public String get(int index) {
        return arguments.get(index);
    }

    @SuppressWarnings("unused")
    public String get(String id) {
        int index = command.getArguments().indexOf(id);
        return get(index);
    }
}
