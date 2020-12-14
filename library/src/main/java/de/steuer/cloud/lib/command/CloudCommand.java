package de.steuer.cloud.lib.command;

import de.steuer.cloud.lib.logging.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public abstract class CloudCommand {

    private final String name;
    private final String usage;
    private final String description;
    private final List<String> aliases;

    protected CloudCommand() {
        final CloudCommandData cloudCommandData = this.getClass().getDeclaredAnnotation(CloudCommandData.class);

        if(cloudCommandData == null)
            throw new CloudCommandNotFoundException("Command \"" + this.getClass().getSimpleName() + "\" cannot be found!");

        this.name = cloudCommandData.name();
        this.usage = cloudCommandData.name() + " " + cloudCommandData.usage();
        this.description = cloudCommandData.description();
        this.aliases = Arrays.asList(cloudCommandData.aliases());
    }

    public abstract void executeCommand(final String commandName, final String... args);

    protected void sendUsage() {
        Logger.getGlobal().info(this.usage);
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
