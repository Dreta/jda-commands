# jda-commands
JDA-Commands is a wrapper around JDA to offer fast command creation.

## Usage

The following gateway intents are required:
 * `GatewayIntent.GUILD_MESSAGES`
 * `GatewayIntent.DIRECT_MESSAGES`

You might also want `GatewayIntent.GUILD_MEMBERS` if you intend to use `MembersArgument`.

You need to add `new CommandListener()` listener to your `JDA` before the commands will start working.

I will need to initialize jda-commands after that. Example:
```java
Command.init(
    Options.builder()
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
    value = {"channel snowflake", "channel snowflake 2"},  // Optional
    channels = {ChannelType.TEXT}  // Optional
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
    public static void me(Message message) {  // Can also be static if desired.
        message.getTextChannel().sendMessage(message.getMember().getAsMention() + ", pong!").queue();
    }

    @CommandPath("you")
    public static void you(Message message, Member who) {
        message.getTextChannel().sendMessage(who.getAsMention() + ", pong!").queue();
    }
}
```

The method must be always static - the command should NOT have anything that depends on their instance.

After done creating the commands, I will need to `new` them. Keep in mind that `new`ing the commands must be done after `init`ing the API.

```java
new PingCommand();
```

Now if I run `!ping`, `@Dreta#6665, pong!` will be sent.

If I run `!ping @aberdeener#0001`, `@aberdeener#0001, pong!` will be sent.

This might be way too overkill for a small command, however it will keep your code more maintainable if the command becomes more complex.

## Advanced Usage

I can also use multiple classes for one command, which might be better when I have a giant command that has a lot of sub commands. For example:
```java
// We will use the ping command shown in the previous example and move the "you"
// path into a separate class.

public class PingYou {
    @CommandPath("you")
    public static void you(Message message, Member who) {
        message.getTextChannel().sendMessage(who.getAsMention() + ", pong!").queue();
    }
}

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
        addPath("you", PingYou.class, Collections.singletonList(
            new Argument("who", new MemberArgument())
        ));
    }

    @CommandPath("me")
    public static void me(Message message) {  // Can also be static if desired.
        message.getTextChannel().sendMessage(message.getMember().getAsMention() + ", pong!").queue();
    }
}
```

As you can see, I can specify the exact class to find methods in when adding paths to the command. Try making the path "me" into a separate class as well!

I won't need to `new` to other class, which is `PingYou` in this case.

For a runnable bot you can play around with, go to [Dretacbe](https://github.com/Dretacbe)/**[jda-commands-example](https://github.com/Dretacbe/jda-commands-example)**

## Compiling

Just so you know, jda-commands uses Maven 3 for that.

```bash
mvn clean install
```
