package de.imfactions.util.Command;


import de.imfactions.IMFactions;

import java.lang.reflect.Method;
import java.util.Arrays;

public abstract class GlobalCommand<S> {
    private final IMFactions factions;
    private final CommandSubMap<S> commandMap;
    private final Class playerClass;

    public GlobalCommand(IMFactions factions, IMCommand command, Object function, Method method, Class playerClass) {
        this.factions = factions;
        this.commandMap = new CommandSubMap<>(new CommandProcessor<>(factions, this, command, function, method));
        this.playerClass = playerClass;
    }

    public void addSubCommand(IMCommand command, Object function, Method method) {
        commandMap.putCommand(new CommandProcessor<>(factions, this, command, function, method), Arrays.copyOfRange(command.parent(), 1, command.parent().length));
    }

    public void process(S sender, String[] args) {
        commandMap.run(sender, args);
    }

    public Class getPlayerClass() {
        return playerClass;
    }

    public abstract boolean checkPermission(S sender, String permission);
}

