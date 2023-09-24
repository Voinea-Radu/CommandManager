package dev.lightdream.commandmanager.common.command;

public abstract class CommonCommand extends CommandImpl{
    @Override
    public boolean registerCommand(String alias) {
        return false;
    }
}
