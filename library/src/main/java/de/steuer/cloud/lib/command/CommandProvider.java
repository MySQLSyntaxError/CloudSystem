package de.steuer.cloud.lib.command;

import de.steuer.cloud.lib.logging.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class CommandProvider {

    private final List<CloudCommand> cloudCommands = new ArrayList<>();

    public void registerCommand(final CloudCommand cloudCommand) {
        for(final CloudCommand command : cloudCommands) {
            if(cloudCommand.getName().equalsIgnoreCase(command.getName()))
                Logger.getGlobal().warning("The name \"" + cloudCommand.getName() + "\" already exists!");

            if(command.getAliases().contains(cloudCommand.getName()))
                Logger.getGlobal().warning("The name \"" + cloudCommand.getName() + "\" is already an alias of \"" + command.getName() + "\"!");

            for(final String alias : cloudCommand.getAliases()) {
                if(command.getAliases().contains(alias)) {
                    Logger.getGlobal().warning("The alias \"" + alias + "\" is already an alias of \"" + command.getName() + "\"!");
                    break;
                }
            }
        }

        cloudCommands.add(cloudCommand);
    }

    public String onCommand(final String message) {
        final String commandName = message.split(" ")[0];
        final CloudCommand command = getCommand(commandName);

        if(command == null)
            return commandName;

        final List<String> arguments = new ArrayList<>();

        for(final String argument : message.substring(commandName.length()).split(" ")) {
            if(argument.equalsIgnoreCase("") || argument.equalsIgnoreCase(" "))
                continue;

            arguments.add(argument);
        }

        command.executeCommand(commandName, arguments.toArray(new String[arguments.size()]));

        return null;
    }

    public CloudCommand getCommand(final String name) {
        for(final CloudCommand command : cloudCommands) {
            if(command.getName().equalsIgnoreCase(name))
                return command;

            for(final String alias : command.getAliases()) {
                if(alias.equalsIgnoreCase(name))
                    return command;
            }
        }

        return null;
    }

    public boolean commandExists(final String commandName) {
        return getCommand(commandName) != null;
    }

    public boolean commandExists(final CloudCommand command) {
        return getCommand(command.getName()) != null;
    }

    public void consoleInput() {
        try {
            String input;

            while((input = Logger.getGlobal().getReader().readLine()) != null) {
                if(!input.equalsIgnoreCase("")) {
                    final String commandName = this.onCommand(input);

                    if(commandName != null)
                        Logger.getGlobal().error("Command \"" + commandName + "\" wasnt found!");
                }
            }
        } catch (IOException e) {
            Logger.getGlobal().error(e.getMessage(), e);
        }
    }
}
