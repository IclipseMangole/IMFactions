package de.imfactions.util.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandSubMap<S> {

    private Map<String, Entry> subcommands;

    public CommandSubMap() {
        subcommands = new HashMap<>();
    }

    public CommandSubMap(CommandProcessor<S> command) {
        this();
        subcommands.put("", new Entry(command));
    }

    public void putCommand(CommandProcessor<S> command, String... parents) {
        putCommand(command, 0, parents);
    }

    private void putCommand(CommandProcessor<S> command, int state, String[] parents) {
        if (state == parents.length) {
            Entry entry = subcommands.get(command.getCommand().name());
            if (entry == null) {
                subcommands.put(command.getCommand().name(), new Entry(command));
            } else {
                entry.setDefaultAction(command);
            }
        } else {
            String currentLevel = parents[state].toLowerCase();
            Entry entry = subcommands.get(currentLevel);
            if (entry == null) {
                subcommands.put(currentLevel, new Entry(command, state + 1, parents));
            } else {
                if (entry.getSubcommands() == null) {
                    entry.setSubcommands(new CommandSubMap<>());
                }
                entry.getSubcommands().putCommand(command, state + 1, parents);
            }
        }
    }

    public void run(S sender, String... args) {
        if (!run(sender, 0, args)) {
            subcommands.get("").getDefaultAction().prepareProcess(sender, 0, args);
        }
    }

    private boolean run(S sender, int state, String[] args) {
        if (state >= args.length) {
            return false;
        }
        String currentLevel = args[state].toLowerCase();
        Entry entry = subcommands.get(currentLevel);
        if (entry == null) {
            entry = subcommands.get("%");
        }
        return entry != null && entry.run(sender, state + 1, args);
    }

    private class Entry {
        private CommandProcessor<S> defaultAction;
        private CommandSubMap<S> subcommands;

        public Entry(CommandProcessor<S> defaultAction) {
            this.defaultAction = defaultAction;
        }

        public Entry(CommandProcessor<S> command, int state, String[] parents) {
            subcommands = new CommandSubMap<>(command);
            subcommands.putCommand(command, state, parents);
        }

        private boolean run(S sender, int state, String[] args) {
            if (subcommands != null && subcommands.run(sender, state, args)) {
                return true;
            }
            if (defaultAction != null) {
                defaultAction.prepareProcess(sender, state, args);
                return true;
            }
            return false;
        }

        public CommandProcessor<S> getDefaultAction() {
            return defaultAction;
        }

        public CommandSubMap<S> getSubcommands() {
            return subcommands;
        }

        public void setSubcommands(CommandSubMap<S> subcommands) {
            this.subcommands = subcommands;
        }

        public void setDefaultAction(CommandProcessor<S> defaultAction) {
            this.defaultAction = defaultAction;
        }
    }
}

