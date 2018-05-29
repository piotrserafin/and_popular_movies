package com.piotrserafin.popularmovies.utils;

import com.piotrserafin.popularmovies.ui.activities.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

    private final Map<String, Command> commands;
    private static CommandFactory instance = null;

    private CommandFactory() {
        commands = new HashMap<>();
    }

    public void addCommand(@MainActivity.sortType final String sortType, final Command command) {
        commands.put(sortType, command);
    }

    public void execute(@MainActivity.sortType final String sortType) {
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

