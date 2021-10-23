package de.imfactions.util.Command;

import com.google.common.collect.Lists;

import java.util.List;

public class FlagList {
    private final List<String> flags;

    public FlagList(String... flags) {
        this.flags = Lists.newArrayList(flags);
    }

    public List<String> getFlags() {
        return flags;
    }

    public void addFlag(String flag) {
        flags.add(flag);
    }

    public boolean contains(String flag) {
        return flags.contains(flag);
    }

    public int size() {
        return flags.size();
    }

    /**
     * Durchsucht ein Array von Argumenten nach den gegebenen Flags und
     * gibt eine neue FlagList mit den gefundenen Flags zurück, sowie die übrigen Argumente
     *
     * @param args Argumente die nach Flags durchsucht werden sollen
     * @return Ein FilterResult mit einer neuen FlagList und den restlichen Argumenten
     * @throws FlagNotFoundException wenn eine Flag in den Argumenten gefunden wird, die nicht bekannt ist
     */
    public FilterResult filterFlags(String[] args) throws FlagNotFoundException {
        FlagList found = new FlagList();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-") && !args[i].startsWith("--")) {
                if (flags.contains(args[i].substring(1))) {
                    found.addFlag(args[i].substring(1));
                    args[i] = null;
                } else throw new FlagNotFoundException(flags, args[i].substring(1));
            }
        }
        String[] leftover = new String[args.length - found.size()];
        int k = 0;
        for (String arg : args) {
            if (arg != null) {
                leftover[k] = arg;
                k++;
            }
        }
        return new FilterResult(found, leftover);
    }

    public static class FilterResult {
        private final FlagList flags;
        private final String[] args;

        public FilterResult(FlagList flags, String[] args) {
            this.flags = flags;
            this.args = args;
        }

        public FlagList getFlags() {
            return flags;
        }

        public String[] getArgs() {
            return args;
        }
    }

    public static FilterResult generate(IMCommand command, String[] args) throws FlagNotFoundException {
        if (command.flags().length > 0) {
            FlagList flags = new FlagList(command.flags());
            return flags.filterFlags(args);
        }
        return null;
    }
}

