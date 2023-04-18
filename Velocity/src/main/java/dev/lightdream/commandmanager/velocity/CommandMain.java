package dev.lightdream.commandmanager.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.lightdream.commandmanager.common.CommonCommandMain;
import org.jetbrains.annotations.NotNull;

public interface CommandMain extends CommonCommandMain {

    @NotNull ProxyServer getProxy();

}
