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

import io.github.dretacbe.jdacommands.arguments.ArgumentParseException;

public class BooleanArgument implements ArgumentType<Boolean> {
    public Boolean parse(String[] args, String name, int start) throws ArgumentParseException {
        String s = args[start].toLowerCase();
        if (!s.equals("true") && !s.equals("yes") && !s.equals("false") && !s.equals("no")) {  // Just in case somebody is stupid enough not to know what true/false is
            throw new ArgumentParseException("Argument " + name + " isn't a boolean value (true/false)!");
        }
        return s.equals("true") || s.equals("yes");
    }
}
