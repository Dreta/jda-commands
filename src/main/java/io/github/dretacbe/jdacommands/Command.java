/*
 * JDA-Commands allows you to easily create commands in JDA.
 * Copyright (C) 2020 Dreta https://dretacbe.github.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.dretacbe.jdacommands;

import io.github.dretacbe.jdacommands.annotations.CommandAliases;
import io.github.dretacbe.jdacommands.annotations.CommandChannel;
import io.github.dretacbe.jdacommands.annotations.CommandPermissions;
import io.github.dretacbe.jdacommands.annotations.CommandRoot;
import io.github.dretacbe.jdacommands.arguments.Argument;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * Represents a command - this is the basis of JDA-Commands.
 * All of the commands must be annotated with {@link io.github.dretacbe.jdacommands.annotations.CommandRoot}.
 * You must add {@link CommandListener} as an event listener to your {@link net.dv8tion.jda.api.JDABuilder} so the commands will work.
 * After initializing the {@link net.dv8tion.jda.api.JDA} object, you must use {@link #init(Options)} to
 * initialize the API. A non-initialized API might throw exceptions.
 * The {@link net.dv8tion.jda.api.JDA} object MUST have the gateway intent {@link net.dv8tion.jda.api.requests.GatewayIntent#GUILD_MESSAGES}
 * and optionally {@link net.dv8tion.jda.api.requests.GatewayIntent#DIRECT_MESSAGES}
 * The method must be static at all times to force good code design.
 * <p>
 * For an example, see README.md.
 *
 * @implNote All the annotations from the class overrides the annotations from individual methods.
 * The permissions, however, will be combined.
 * There can only be ONE path that matches a condition at ONE time. So there cannot be two exact same
 * paths with only the argument-type specific options different. Only one will be used.
 */
@ToString
@EqualsAndHashCode
public abstract class Command {
    @Getter
    private static final List<Command> commands = new ArrayList<>();
    @Getter
    private static Guild guild;
    @Getter
    private static Options options;
    @Getter
    private final Map<String, Path> paths = new HashMap<>();
    @Getter
    private Function<Member, String> memberPreprocessor;

    public Command() {
        commands.add(this);
    }

    public static MessageAction sendError(TextChannel channel, String message) {
        if (getOptions().isErrorEmbed()) {
            return channel.sendMessage(new MessageEmbed("", getOptions().getEmbedTitle(), message, EmbedType.RICH,
                    OffsetDateTime.now(), 0xff0000, null, null, null, null, null, null, Collections.emptyList()));
        } else {
            return channel.sendMessage(getOptions().getErrorPrefix() + message);
        }
    }

    /**
     * Initialize the entirety of JDA-Command.
     *
     * @param options The options
     */
    public static void init(Options options) {
        guild = options.getGuild();
        Command.options = options;
    }

    /**
     * Add a path to the command.
     *
     * @param path      The path name - must be the same with {@link io.github.dretacbe.jdacommands.annotations.CommandPath#value}.
     * @param arguments The arguments of the path.
     */
    public void addPath(String path, List<Argument> arguments) {
        if (paths.containsKey(path)) {
            throw new IllegalStateException("Duplicate path name");
        }
        paths.put(path, new Path(path, arguments, this, Path.tryFindMethod(path, getClass())));
    }

    /**
     * Add a path to the command.
     *
     * @param path      The path name - must be the same with {@link io.github.dretacbe.jdacommands.annotations.CommandPath#value}.
     * @param clazz     The class name of where to find the method. This can be used for spanning commands over multiple classes.
     * @param arguments The arguments of the path.
     */
    public void addPath(String path, Class<? extends Command> clazz, List<Argument> arguments) {
        if (paths.containsKey(path)) {
            throw new IllegalStateException("Duplicate path name");
        }
        paths.put(path, new Path(path, arguments, this, Path.tryFindMethod(path, clazz)));
    }

    /**
     * Preprocess the member before executing a command.
     * This allows you to filter exact members dynamically that are able to run this command.
     * If the member should be denied to run this command, return a string with a friendly
     * error message. Return {@code null} otherwise.
     *
     * @param function The member preprocessor
     */
    public void setMemberPreprocessor(Function<Member, String> function) {
        this.memberPreprocessor = function;
    }

    /**
     * The same as {@link #setMemberPreprocessor(Function)}, however this is only effective for a single path.
     *
     * @param path     The path name
     * @param function The member preprocessor
     */
    public void setMemberPreprocessor(String path, Function<Member, String> function) {
        paths.get(path).setMemberPreprocessor(function);
    }

    /**
     * Gets the name of the command from the {@link CommandRoot} annotation.
     *
     * @return Name
     */
    @NotNull
    public String getName() {
        if (!getClass().isAnnotationPresent(CommandRoot.class)) {
            throw new IllegalStateException("A Command class must be annotated with @CommandRoot!");
        }
        return getClass().getAnnotation(CommandRoot.class).name();
    }

    /**
     * Gets the description of the command from the {@link CommandRoot} annotation.
     *
     * @return Description
     */
    @NotNull
    public String getDescription() {
        if (!getClass().isAnnotationPresent(CommandRoot.class)) {
            throw new IllegalStateException("A Command class must be annotated with @CommandRoot!");
        }
        return getClass().getAnnotation(CommandRoot.class).description();
    }

    /**
     * Get the aliases of the command from the {@link CommandAliases} annotation.
     *
     * @return Aliases
     */
    @NotNull
    public String[] getAliases() {
        if (getClass().isAnnotationPresent(CommandAliases.class)) {
            return getClass().getAnnotation(CommandAliases.class).value();
        }
        return new String[0];
    }

    /**
     * Get the permissions of the command from the {@link CommandPermissions} annotation.
     *
     * @return Permissions
     */
    @NotNull
    public Permission[] getPermissions() {
        if (getClass().isAnnotationPresent(CommandPermissions.class)) {
            return getClass().getAnnotation(CommandPermissions.class).value();
        }
        return new Permission[0];
    }

    /**
     * Get the channel of the command from the {@link CommandChannel} annotation.
     *
     * @return Channel
     * @implNote The channel represents the channel that is annotated on the CLASS, instead of individual methods!
     */
    @Nullable
    public GuildChannel getChannel() {
        if (getClass().isAnnotationPresent(CommandChannel.class)) {
            return getGuild().getGuildChannelById(getClass().getAnnotation(CommandChannel.class).value());
        }
        return null;
    }

    /**
     * Get whether this command/path is allowed in the DM from the {@link CommandChannel} annotation.
     *
     * @return Allow DM
     */
    public boolean isAllowDM() {
        if (getClass().isAnnotationPresent(CommandChannel.class)) {
            return getClass().getAnnotation(CommandChannel.class).allowDM();
        }
        return false;
    }
}
