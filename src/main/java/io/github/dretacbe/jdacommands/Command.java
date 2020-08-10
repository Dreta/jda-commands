package io.github.dretacbe.jdacommands;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Represents a command - this is the basis of JDA-Commands.
 * More documentation will be added soon.
 */
public class Command {
    @Getter
    private static Guild guild;
    @Getter
    private static String prefix;

    /**
     * Initialize the entirety of JDA-Command.
     *
     * @param guild The guild to handle commands in
     */
    public static void init(Guild guild) {
        init(guild, "!");
    }

    /**
     * Initialize the entirety of JDA-Command.
     *
     * @param guild  The guild to handle commands in
     * @param prefix The prefix for the command, for example !.
     */
    public static void init(Guild guild, String prefix) {
        if (Command.guild != null) {
            throw new IllegalStateException("Already initialized");
        }
        Command.guild = guild;
        Command.prefix = prefix;
    }
}
