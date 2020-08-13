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

import io.github.dretacbe.jdacommands.annotations.CommandChannel;
import io.github.dretacbe.jdacommands.annotations.CommandPermissions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This listener must be added to your {@link net.dv8tion.jda.api.JDA} object so commands will work.
 */
public class CommandListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot()) {
            return;
        }
        Member member = e.getMember();
        boolean ran = false;

        for (Command command : Command.getCommands()) {
            List<String> aliases = new ArrayList<>(Arrays.asList(command.getAliases()));
            aliases.add(command.getName());

            for (String alias : aliases) {
                // If this is the command to execute
                if (e.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(Command.getOptions().getPrefix() + alias)) {
                    ran = true;  // Do not show the unknown command message

                    if (command.getMemberPreprocessor() != null) {  // Preprocess member
                        String prep = command.getMemberPreprocessor().apply(member);
                        if (prep != null) {
                            Command.sendError(e.getMessage().getTextChannel(), prep).queue();
                            return;
                        }
                    }

                    if (getClass().isAnnotationPresent(CommandChannel.class)) {
                        CommandChannel annotation = getClass().getAnnotation(CommandChannel.class);
                        if (!annotation.allowDM() && e.getMessage().getChannelType() == ChannelType.PRIVATE) {  // Check for DM
                            Command.sendError(e.getMessage().getTextChannel(), Command.getOptions().getErrorDM()).queue();
                            return;
                        }
                        if (!annotation.value().isEmpty() && !e.getMessage().getChannel().getId().equals(annotation.value())) {
                            Command.sendError(e.getMessage().getTextChannel(),  // Check for channel
                                    String.format(Command.getOptions().getErrorChannel(), Command.getOptions().getGuild().getGuildChannelById(annotation.value()).getName())).queue();
                            return;
                        }
                    }

                    if (getClass().isAnnotationPresent(CommandPermissions.class)) {
                        Permission[] permissions = getClass().getAnnotation(CommandPermissions.class).value();
                        Permission[] serverPerms = Arrays.stream(permissions).filter(Permission::isGuild).toArray(Permission[]::new);
                        Permission[] channelPerms = Arrays.stream(permissions).filter(Permission::isChannel).toArray(Permission[]::new);
                        if (!member.hasPermission(serverPerms)) {  // Check for server permissions
                            Command.sendError(e.getMessage().getTextChannel(), String.format(Command.getOptions().getErrorServerPermissions(),
                                    Arrays.stream(serverPerms).map(Permission::getName).collect(Collectors.joining(", ")))).queue();
                            return;
                        }
                        if (channelPerms.length != 0 && e.getMessage().getChannelType() == ChannelType.PRIVATE) {
                            Command.sendError(e.getMessage().getTextChannel(), Command.getOptions().getErrorDM()).queue();
                            return;  // Channel permissions require guild channel
                        }
                        if (!member.hasPermission((GuildChannel) e.getMessage().getChannel(), channelPerms)) {  // Check for channel permissions
                            Command.sendError(e.getMessage().getTextChannel(), String.format(Command.getOptions().getErrorChannelPermissions(),
                                    Arrays.stream(channelPerms).map(Permission::getName).collect(Collectors.joining(", ")))).queue();
                            return;
                        }
                    }

                    Exception lastError = null;
                    for (Path path : command.getPaths().values()) {
                        if (e.getMessage().getContentRaw().trim().split(" ").length - 1 > path.getArguments().size()) {
                            continue;  // We always match the one with the most arguments
                        }
                        try {
                            List<Object> result = path.parse(member, e.getMessage());
                            path.getMethod().setAccessible(true);
                            path.getMethod().invoke(null, result.toArray());
                            return;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            lastError = ex;
                        }
                    }
                    if (lastError == null) {
                        Command.sendError(e.getMessage().getTextChannel(), Command.getOptions().getErrorUsage()).queue();
                    } else {
                        Command.sendError(e.getMessage().getTextChannel(), lastError.getMessage()).queue();
                    }
                    break;
                }
            }
        }

        if (!ran && e.getMessage().getContentRaw().startsWith(Command.getOptions().getPrefix()) && Command.getOptions().isUnknownCommand()) {
            Command.sendError(e.getMessage().getTextChannel(), Command.getOptions().getErrorUnknown()).queue();
        }
    }
}
