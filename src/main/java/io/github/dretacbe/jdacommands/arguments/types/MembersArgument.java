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

package io.github.dretacbe.jdacommands.arguments.types;

import io.github.dretacbe.jdacommands.Command;
import io.github.dretacbe.jdacommands.arguments.ArgumentParseException;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Represents some members that can be either @username#discriminator or @everyone and @here.
 */
public class MembersArgument implements ArgumentType<Collection<Member>> {
    @Override
    public Collection<Member> parse(String[] args, String name, int start) throws ArgumentParseException {
        if (args[start].equals("@everyone")) {
            return Command.getOptions().getGuild().getMembers();
        } else if (args[start].equals("@here")) {
            return Command.getOptions().getGuild().getMembers().stream().filter(member -> member.getOnlineStatus() == OnlineStatus.ONLINE).collect(Collectors.toList());
        } else {
            String snowflake = args[start].replaceFirst("<@", "").replace(">", "");
            Member member = Command.getOptions().getGuild().getMemberById(snowflake);
            if (member == null) {
                // In this case the user must have directly mentioned a member (Sending <@channel snowflake>)
                throw new ArgumentParseException("Argument " + name + " refers to a non-existent member!");
            }
            return Collections.singletonList(member);
        }
    }
}
