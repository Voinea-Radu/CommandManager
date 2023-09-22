package dev.lightdream.commandmanager.spigot.command;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class SpigotCommonCommandImpl extends org.bukkit.command.Command implements ICommonCommand {

    private CommonCommandMain main;
    private Command commandAnnotation;
    private ICommonCommand parentCommand;
    private List<ICommonCommand> subCommands = new ArrayList<>();
    private @Getter boolean enabled = true;

    protected SpigotCommonCommandImpl() {
        super("");
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public void enable() {
        this.enabled = true;
    }

}
