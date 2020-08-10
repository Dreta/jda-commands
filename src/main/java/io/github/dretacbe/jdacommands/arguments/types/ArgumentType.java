package io.github.dretacbe.jdacommands.arguments.types;

import io.github.dretacbe.jdacommands.arguments.ArgumentParseException;

/**
 * Represents an argument type that can be parsed into a friendly object.
 *
 * @param <O> The type of the object to parse into
 */
public interface ArgumentType<O> {
    O parse(String[] args, String name, int start) throws ArgumentParseException;
}
