package dev.lightdream.commandmanager.velocity;

import com.velocitypowered.api.command.CommandSource;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.velocity.command.BaseCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = "example", autoRegister = true)
public class ExampleCommand extends BaseCommand {

    @Override
    public void exec(@NotNull CommandSource source, @NotNull List<String> args) {
        sendMessage(source, "ExampleCommand");
    }
}
