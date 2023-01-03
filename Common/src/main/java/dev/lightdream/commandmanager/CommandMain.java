package dev.lightdream.commandmanager;

import dev.lightdream.commandmanager.command.CommandProvider;
import dev.lightdream.commandmanager.command.CommonCommand;
import dev.lightdream.commandmanager.dto.CommandLang;

import java.util.ArrayList;
import java.util.List;

public interface CommandMain extends CommandProvider {

    String getPackageName();

    CommandLang getLang();


}
