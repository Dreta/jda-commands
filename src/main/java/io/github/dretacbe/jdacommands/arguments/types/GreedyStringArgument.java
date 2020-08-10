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

import java.util.Arrays;

/**
 * Represents a string argument that extends until the end of the message.
 * Must not have trailing arguments.
 */
public class GreedyStringArgument implements ArgumentType<String> {
    @Override
    public String parse(String[] args, String name, int start) {
        return String.join(" ", Arrays.copyOfRange(args, start, args.length - 1));
    }
}
