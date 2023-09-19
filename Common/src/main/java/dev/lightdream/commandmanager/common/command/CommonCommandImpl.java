package dev.lightdream.commandmanager.common.command;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class CommonCommandImpl implements ICommonCommand {

    private CommonCommandMain main;
    private Command commandAnnotation;
    private ICommonCommand parentCommand;
    private List<ICommonCommand> subCommands;
    private @Getter boolean enabled = true;

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public void enable() {
        this.enabled = true;
    }


}
