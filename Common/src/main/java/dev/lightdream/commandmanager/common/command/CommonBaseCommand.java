package dev.lightdream.commandmanager.common.command;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommonBaseCommand extends CommonCommandImpl{

    @Override
    public boolean registerCommand(String alias) {
        return getMain().getAdapter().convertCommand(this).registerCommand(alias);
    }

    @Override
    public void sendMessage(Object user, String message) {
        getMain().getAdapter().convertCommand(this).sendMessage(user, message);
    }

    @Override
    public boolean checkPermission(Object user, String permission) {
        return getMain().getAdapter().convertCommand(this).checkPermission(user, permission);
    }
}
