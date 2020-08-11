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

package io.github.dretacbe.jdacommands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Each {@link io.github.dretacbe.jdacommands.Command} has multiple paths to it - also known as routes.
 * Each of the paths in the command is a different usage for it. For example, a command might have the
 * following paths:
 * !ban [user: {@link io.github.dretacbe.jdacommands.arguments.types.MemberArgument}] [reason: {@link io.github.dretacbe.jdacommands.arguments.types.StringArgument}]
 * !ban [user: {@link io.github.dretacbe.jdacommands.arguments.types.MemberArgument}] [time: {@link io.github.dretacbe.jdacommands.arguments.types.StringArgument}] [timeunit] [reason: {@link io.github.dretacbe.jdacommands.arguments.types.StringArgument}]
 * Each of the paths in the command will have a method mapped to it - the mapped method must be annotated
 * with {@link CommandPath} with the path name that is the same as specified in INSERT ADDPATH REFERENCE.
 * The method will have all of its arguments passed in directly plus the member that ran the command.
 * e.g. for the first path specified above,
 * {@link net.dv8tion.jda.api.entities.Message} (the message), {@link net.dv8tion.jda.api.entities.Member} (the user) and {@link String} (the reason) will be
 * passed into the method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandPath {
    String value();
}
