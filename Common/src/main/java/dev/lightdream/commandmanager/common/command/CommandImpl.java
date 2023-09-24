package dev.lightdream.commandmanager.common.command;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class CommandImpl implements ICommand {

    private CommonCommandMain main;
    private Command commandAnnotation;
    private ICommand parentCommand;
    private List<ICommand> subCommands = new ArrayList<>();
    private boolean enabled = true;

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public void enable() {
        this.enabled = true;
    }

}
