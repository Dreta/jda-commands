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

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.Guild;

@Builder
@ToString
@EqualsAndHashCode
public class Options {
    /**
     * The prefix before all error messages - will not be included in embeds
     */
    @Builder.Default
    @Getter
    private final String errorPrefix = "Error: ";
    /**
     * The message to send when a command can't be executed in a private channel.
     */
    @Builder.Default
    @Getter
    private final String errorDM = "This command can't be executed in a private channel!";
    /**
     * The message to send when a command must be executed in a specific channel.
     */
    @Builder.Default
    @Getter
    private final String errorChannel = "This command can only be executed in #%s!";
    /**
     * The message to send when a command required server permissions.
     */
    @Builder.Default
    @Getter
    private final String errorServerPermissions = "You need the server permissions %s to run this command!";
    /**
     * The message to send when a command required channel permissions.
     */
    @Builder.Default
    @Getter
    private final String errorChannelPermissions = "You need the channel permissions %s to run this command!";
    /**
     * The message to send when a reflective operation error occurred.
     */
    @Builder.Default
    @Getter
    private final String errorReflective = "An reflective operation error occurred. Please contact the administrators for further information.";
    /**
     * The message to send when no path can be successfully parsed.
     */
    @Builder.Default
    @Getter
    private final String errorUnknown = "Unknown Command!";
    /**
     * The message to send when the command usage is incorrect.
     */
    @Builder.Default
    @Getter
    private final String errorUsage = "Incorrect Usage!";
    /**
     * Whether or not to use embeds to send error messages.
     */
    @Builder.Default
    @Getter
    private final boolean errorEmbed = false;
    /**
     * The title of the embed when {@link #errorEmbed} is true.
     */
    @Builder.Default
    @Getter
    private final String embedTitle = "Error";
    /**
     * The guild of all commands.
     */
    @Getter
    private final Guild guild;
    /**
     * The prefix of all commands.
     */
    @Builder.Default
    @Getter
    private final String prefix = "!";
}
