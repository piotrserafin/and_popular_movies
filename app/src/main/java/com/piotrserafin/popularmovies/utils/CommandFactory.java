package com.piotrserafin.popularmovies.utils;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

    private final Map<MovieSortType, Command> commands;
    private static CommandFactory instance = null;

    private CommandFactory() {
        commands = new HashMap<>();
    }

    public void addCommand(final MovieSortType sortType, final Command command) {
        commands.put(sortType, command);
    }

    public void execute(final MovieSortType sortType) {
        if (commands.containsKey(sortType)) {
            commands.get(sortType).execute();
        }
    }

    public static CommandFactory getInstance() {
        if (instance == null) {
            instance = new CommandFactory();
        }
        return instance;
    }
}

