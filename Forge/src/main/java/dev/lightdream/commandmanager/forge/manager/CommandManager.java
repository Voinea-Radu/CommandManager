package dev.lightdream.commandmanager.forge.manager;

import com.mojang.brigadier.CommandDispatcher;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.dto.CommandLang;
import dev.lightdream.commandmanager.common.manager.CommonCommandManager;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.commands.CommandSourceStack;
import org.reflections.Reflections;

import java.util.Set;

@Getter
@Accessors(chain = true, fluent = true)
public class CommandManager extends CommonCommandManager {

    private final CommandDispatcher<CommandSourceStack> dispatcher;

    @lombok.Builder(builderClassName = "Builder")
    public CommandManager(Reflections reflections, CommandLang lang,
                          Set<Class<? extends ICommonCommand>> commandClasses, String basePermission,
                          boolean autoRegister, CommandDispatcher<CommandSourceStack> dispatcher) {
        super(reflections, lang, commandClasses, basePermission, autoRegister);

        this.dispatcher = dispatcher;
    }

    public static CommandManager instance(){
        return (CommandManager) instance;
    }

    @SuppressWarnings("unused")
    public static Builder builder(){
        return applyDefaults(new Builder());
    }

    public static class Builder extends CommonBuilder{
        public Builder(){
            super();
        }
    }

}
