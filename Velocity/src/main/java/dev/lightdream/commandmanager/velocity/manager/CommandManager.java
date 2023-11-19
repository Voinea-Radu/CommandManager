package dev.lightdream.commandmanager.velocity.manager;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.dto.CommandLang;
import dev.lightdream.commandmanager.common.manager.CommonCommandManager;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.reflections.Reflections;

import java.util.Set;

@Getter
@Accessors(chain = true, fluent = true)
public class CommandManager extends CommonCommandManager {

    private final ProxyServer proxy;

    @lombok.Builder(builderClassName = "Builder")
    public CommandManager(Reflections reflections, CommandLang lang,
                          Set<Class<? extends ICommonCommand>> commandClasses, String basePermission,
                          boolean autoRegister, ProxyServer proxy) {
        super(reflections, lang, commandClasses, basePermission, autoRegister);

        this.proxy = proxy;
    }

    public static CommandManager instance(){
        return (CommandManager) instance;
    }

    @SuppressWarnings("unused")
    public static Builder builder() {
        return applyDefaults(new Builder());
    }

    public static class Builder extends CommonBuilder {
        public Builder() {
            super();
        }
    }

}
