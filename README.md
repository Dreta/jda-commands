# jda-commands
JDA-Commands is a wrapper around JDA to offer fast command creation.

## Usage

The following gateway intents are required:
 * GUILD_MESSAGES
 * DIRECT_MESSAGES

You need to add `new CommandListener()` listener to your `JDA` before the commands will start working.

I will need to initialize jda-commands after that. Example:
```java
Command.init(
    new Options.OptionsBuilder()
        .prefix("!")
        .guild(some guild object)
        .build()
);
```

Now I can start creating commands.
```java
// For more information please see the source code

@CommandRoot(  // Mandatory
    name = "ping",  // Mandatory
    description = "Ping ping ping!"  // Optional
)
@CommandChannel(  // Optional
    value = "channel snowflake",  // Optional
    allowDM = true  // Optional
)
@CommandPermissions({Permission.MESSAGE_READ, Permission.MESSAGE_WRITE})  // Optional
@CommandAliases("pong")  // Optional
public class PingCommand extends Command {
    public PingCommand() {
        addPath("me", Collections.emptyList());
        addPath("you", Collections.singletonList(
            new Argument("who", new MemberArgument())
        ));
    }

    @CommandPath("me")
    public void me(Message message) {
        message.getTextChannel().sendMessage(message.getMember().getAsMention() + ", pong!");
    }

    @CommandPath("you")
    public void you(Message message, Member who) {
        message.getTextChannel().sendMessage(who.getAsMention() + ", pong!");
    }
}
```

Now if I run `!ping`, `@Dreta#6665, pong!` will be sent.

If I run `!ping @aberdeener#0001`, `@aberdeener#0001, pong!` will be sent.

This might be way too overkill for a small command, however it will keep your code more maintainable if the command becomes more complex.
