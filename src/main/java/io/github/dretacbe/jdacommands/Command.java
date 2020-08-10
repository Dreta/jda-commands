package io.github.dretacbe.jdacommands;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

public class Command {
    @Getter
    private static Guild guild;
    @Getter
    private static String prefix;

    public static void init(Guild guild, String prefix) {
        if (Command.guild != null) {
            throw new IllegalStateException("Already initialized");
        }
        Command.guild = guild;
        Command.prefix = prefix;
    }
}
