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

import net.dv8tion.jda.api.entities.ChannelType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If you annotate any subclass of {@link io.github.dretacbe.jdacommands.Command} or a command path method
 * with {@link CommandChannel}, then the command will only be able to be executed in that specified channel.
 * <p>
 * You can also specify {@link #channels()} to set the allowed channel types for this command.
 * <p>
 * Only guild channels are supported as of the moment.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CommandChannel {
    /**
     * The snowflake of the channels
     *
     * @return The snowflakes
     */
    String[] value() default {""};

    /**
     * The channels that the command can be executed in
     *
     * @return The channels (type)
     */
    ChannelType[] channels() default {ChannelType.TEXT, ChannelType.PRIVATE};
}
