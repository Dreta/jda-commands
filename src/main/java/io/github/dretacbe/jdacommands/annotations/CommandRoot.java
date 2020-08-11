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
 * {@link CommandRoot} should be annotated on a class that extends {@link io.github.dretacbe.jdacommands.Command}.
 * This allows you to specify general metadata for a command, such as its name, etc.
 * You can assume that all the subclasses of {@link io.github.dretacbe.jdacommands.Command} will be annotated with {@link CommandRoot}.
 * You can retrieve the metadata specified by this annotation using the methods in {@link io.github.dretacbe.jdacommands.Command}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandRoot {
    String name();

    String description() default "No description provided";
}
