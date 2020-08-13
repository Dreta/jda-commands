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

import com.google.common.base.Preconditions;
import io.github.dretacbe.jdacommands.annotations.CommandChannel;
import io.github.dretacbe.jdacommands.annotations.CommandPath;
import io.github.dretacbe.jdacommands.annotations.CommandPermissions;
import io.github.dretacbe.jdacommands.annotations.CommandRoot;
import io.github.dretacbe.jdacommands.arguments.Argument;
import io.github.dretacbe.jdacommands.arguments.ArgumentParseException;
import io.github.dretacbe.jdacommands.arguments.types.GreedyStringArgument;
import io.github.dretacbe.jdacommands.arguments.types.LiteralArgument;
import lombok.Data;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a path.
 * <p>
 * There are two parts in a path - the name and the argument. Upon execution, the method that is annotated with
 * {@link CommandPath} and have the same name for the {@link CommandPath#value()} will be invoked with the sender
 * (as a {@link net.dv8tion.jda.api.entities.Member}) and the types of the arguments (see {@link io.github.dretacbe.jdacommands.arguments.types.ArgumentType}).
 */
@Data
public class Path {
    private String name;
    private List<Argument> arguments;
    private Command command;
    private Method method;
    private Function<Member, String> memberPreprocessor;

    public Path(String name, List<Argument> arguments, Command command, Method method) {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(arguments, "arguments");
        Preconditions.checkNotNull(command, "command");
        Preconditions.checkArgument(command.getClass().isAnnotationPresent(CommandRoot.class), "@CommandRoot required");
        Preconditions.checkNotNull(method, "method");
        this.name = name;
        for (Argument argument : arguments) {
            if (argument.getType().getClass() == GreedyStringArgument.class && arguments.get(arguments.size() - 1).getType().getClass() != GreedyStringArgument.class) {
                throw new IllegalArgumentException("GreedyStringArgument must be the last argument if present!");
            }
        }
        this.arguments = arguments;
        this.command = command;
        this.method = method;
    }

    /**
     * Attempt to find method from a path name and a {@link Command} class.
     *
     * @param name  The path name
     * @param clazz The class
     * @return The method, null if not found.
     */
    @Nullable
    public static Method tryFindMethod(String name, Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(CommandPath.class) && method.getAnnotation(CommandPath.class).value().equals(name)) {
                return method;
            }
        }
        return null;
    }

    /**
     * This method parses the argument from a message object and returns a list of arguments to pass into the method.
     *
     * @param message The message object from the {@link net.dv8tion.jda.api.events.message.MessageReceivedEvent}.
     * @return The parsed arguments, including the message object
     * @throws ArgumentParseException When parsing went wrong
     */
    public List<Object> parse(Member member, Message message) throws ArgumentParseException {
        String command = message.getContentRaw().trim();
        if (memberPreprocessor != null) {
            String prep = memberPreprocessor.apply(member);
            if (prep != null) {
                throw new ArgumentParseException(prep);
            }
        }

        if (method.isAnnotationPresent(CommandChannel.class)) {
            CommandChannel annotation = method.getAnnotation(CommandChannel.class);
            if (!Arrays.asList(annotation.channels()).contains(message.getChannelType())) {
                throw new ArgumentParseException(String.format(Command.getOptions().getErrorChannelType(),
                        Arrays.stream(annotation.channels()).map(s -> s.toString().toLowerCase()).collect(Collectors.joining(", "))));
            }
            if (annotation.value().length != 0 && !Arrays.asList(annotation.value()).contains(message.getChannel().getId())) {
                throw new ArgumentParseException(
                        String.format(Command.getOptions().getErrorChannel(),
                                Arrays.stream(annotation.value()).map(s -> Command.getOptions().getGuild().getGuildChannelById(s).getName()).collect(Collectors.joining(", "))));
            }
        }

        if (method.isAnnotationPresent(CommandPermissions.class)) {
            Permission[] permissions = method.getAnnotation(CommandPermissions.class).value();
            Permission[] serverPerms = Arrays.stream(permissions).filter(Permission::isGuild).toArray(Permission[]::new);
            Permission[] channelPerms = Arrays.stream(permissions).filter(Permission::isChannel).toArray(Permission[]::new);
            if (!member.hasPermission(serverPerms)) {
                throw new ArgumentParseException(String.format(Command.getOptions().getErrorServerPermissions(),
                        Arrays.stream(serverPerms).map(Permission::getName).collect(Collectors.joining(", "))));
            }
            if (channelPerms.length != 0 && message.getChannelType() == ChannelType.PRIVATE) {
                throw new ArgumentParseException(Command.getOptions().getErrorDM());
            }
            if (!member.hasPermission((GuildChannel) message.getChannel(), channelPerms)) {
                throw new ArgumentParseException(String.format(Command.getOptions().getErrorChannelPermissions(),
                        Arrays.stream(serverPerms).map(Permission::getName).collect(Collectors.joining(", "))));
            }
        }

        List<Object> result = new ArrayList<>();
        result.add(message);
        String[] args = command.split(" ");
        for (int i = 0; i < arguments.size(); i++) {
            Argument argument = arguments.get(i);
            Object parsed = argument.getType().parse(args, argument.getName(), i + 1);
            if (argument.getType().getClass() == LiteralArgument.class && !method.getAnnotation(CommandPath.class).literals()) {
                continue;
            }
            result.add(parsed);
        }
        return result;
    }
}
