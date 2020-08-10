package io.github.dretacbe.jdacommands.arguments.types;

import io.github.dretacbe.jdacommands.arguments.ArgumentParseException;

public interface ArgumentType<O> {
    O parse(String[] args, String name, int start) throws ArgumentParseException;
}
