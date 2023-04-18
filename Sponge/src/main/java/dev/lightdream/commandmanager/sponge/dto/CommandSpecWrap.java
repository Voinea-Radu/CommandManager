package dev.lightdream.commandmanager.sponge.dto;

import org.spongepowered.api.command.spec.CommandSpec;

public class CommandSpecWrap {

    public CommandSpec.Builder spec;
    public boolean onlyForPlayers;
    public boolean onlyForConsole;

    public CommandSpecWrap(CommandSpec.Builder spec, boolean onlyForPlayers, boolean onlyForConsole) {
        this.spec = spec;
        this.onlyForPlayers = onlyForPlayers;
        this.onlyForConsole = onlyForConsole;
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("unused")
    public CommandSpec getSpec() {
        return spec.build();
    }

    public static class Builder {
        private CommandSpec.Builder spec;
        private boolean onlyForPlayers;
        private boolean onlyForConsole;

        public Builder() {
            this.spec = CommandSpec.builder();
        }

        @SuppressWarnings("unused")
        public Builder onlyForPlayers(boolean onlyForPlayers) {
            this.onlyForPlayers = onlyForPlayers;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder onlyForConsole(boolean onlyForConsole) {
            this.onlyForConsole = onlyForConsole;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder spec(CommandSpec.Builder spec) {
            this.spec = spec;
            return this;
        }

        public CommandSpecWrap build() {
            return new CommandSpecWrap(spec, onlyForPlayers, onlyForConsole);
        }

    }

}