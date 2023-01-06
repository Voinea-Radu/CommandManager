package dev.lightdream.commandmanager.common;

import dev.lightdream.commandmanager.common.command.CommandProvider;
import dev.lightdream.commandmanager.common.dto.CommandLang;
import dev.lightdream.reflections.Mapper;
import org.jetbrains.annotations.NotNull;


public interface CommandMain {

    CommandLang getLang();

    @NotNull Mapper getMapper();

}
