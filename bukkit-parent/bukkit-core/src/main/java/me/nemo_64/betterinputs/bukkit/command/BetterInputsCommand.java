package me.nemo_64.betterinputs.bukkit.command;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.CommandManager;
import me.lauriichan.laylib.command.Node;
import me.lauriichan.laylib.command.NodeAction;
import me.lauriichan.laylib.command.NodeArgument;
import me.lauriichan.laylib.command.NodeCommand;
import me.lauriichan.laylib.command.annotation.Action;
import me.lauriichan.laylib.command.annotation.Param;
import me.lauriichan.laylib.command.annotation.Argument;
import me.lauriichan.laylib.command.annotation.Command;
import me.lauriichan.laylib.command.annotation.Description;
import me.lauriichan.laylib.command.util.Triple;
import me.lauriichan.laylib.localization.Key;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.reflection.ClassUtil;
import me.nemo_64.betterinputs.api.input.InputFactory;
import me.nemo_64.betterinputs.api.input.modifier.TimeoutModifier;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.api.util.argument.NotEnoughArgumentsException;
import me.nemo_64.betterinputs.api.util.tick.TickUnit;
import me.nemo_64.betterinputs.bukkit.BetterInputsBukkit;
import me.nemo_64.betterinputs.bukkit.command.model.InputKey;

@Command(name = "betterinputs", aliases = {
    "binputs",
    "bi"
}, description = "command.description.betterinputs.parent")
public final class BetterInputsCommand {

    /*
     * Test input
     */

    @Action("test")
    public void test(BetterInputsBukkit api, Actor<?> actor, @Argument(name = "input type", index = 1) InputKey key,
        @Argument(name = "arguments", optional = true, index = 2) ArgumentMap map) {
        Optional<InputFactory<?, ?>> factory = api.getInputFactory(key.namespacedKey());
        if (factory.isEmpty()) {
            actor.sendMessage("Unknown input '" + key.namespacedKey() + "'!");
            return;
        }
        try {
            api.createInput(factory.get().getInputType()).type(key.namespacedKey()).actor(actor.getHandle())
                .exceptionHandler((exception) -> {
                    actor.sendMessage("Something went wrong: '" + exception.getMessage() + "'!");
                }).cancelListener((provider, reason) -> {
                    actor.sendMessage("Action cancelled: '" + reason + "'");
                }).provide().withModifierExceptionHandler((modifier, exception) -> {
                    actor.sendMessage("Something went wrong with the '" + ClassUtil.getClassName(modifier.getClass()) + "' modifier: '"
                        + exception.getMessage() + "'!");
                }).withModifier(new TimeoutModifier<>(3, TickUnit.MINUTE)).asFuture().thenAccept(value -> {
                    actor.sendMessage("Input complete: '" + Objects.toString(value) + "'");
                });
        } catch (IllegalArgumentException | NotEnoughArgumentsException exp) {
            actor.sendMessage("Something went wrong while creating the input: '" + exp.getMessage() + "'!");
        }
    }

    @Action("test timed")
    public void testTimed(BetterInputsBukkit api, Actor<?> actor, @Argument(name = "input type", index = 1) InputKey key,
        @Argument(name = "arguments", optional = true, index = 4) ArgumentMap map, @Argument(name = "time unit", index = 2, params = {
            @Param(name = "type", classValue = TickUnit.class, type = 7)
        }) TickUnit unit,
        @Argument(name = "time amount", index = 3, params = {
            @Param(name = "minimum", intValue = 1, type = 3)
        }) int amount) {
        Optional<InputFactory<?, ?>> factory = api.getInputFactory(key.namespacedKey());
        if (factory.isEmpty()) {
            actor.sendMessage("Unknown input '" + key.namespacedKey() + "'!");
            return;
        }
        try {
            api.createInput(factory.get().getInputType()).type(key.namespacedKey()).actor(actor.getHandle())
                .exceptionHandler((exception) -> {
                    actor.sendMessage("Something went wrong: '" + exception.getMessage() + "'!");
                }).cancelListener((provider, reason) -> {
                    actor.sendMessage("Action cancelled: '" + reason + "'");
                }).provide().withModifierExceptionHandler((modifier, exception) -> {
                    actor.sendMessage("Something went wrong with the '" + ClassUtil.getClassName(modifier.getClass()) + "' modifier: '"
                        + exception.getMessage() + "'!");
                }).withModifier(new TimeoutModifier<>(amount, unit)).asFuture().thenAccept(value -> {
                    actor.sendMessage("Input complete: '" + Objects.toString(value) + "'");
                });
        } catch (IllegalArgumentException | NotEnoughArgumentsException exp) {
            actor.sendMessage("Something went wrong while creating the input: '" + exp.getMessage() + "'!");
        }
    }

    /*
     * Debug command
     */

    @Action("debug")
    public void debug(ISimpleLogger logger, Actor<?> actor) {
        if (logger.isDebug()) {
            logger.setDebug(false);
            actor.sendMessage("Disabled debug");
            return;
        }
        logger.setDebug(true);
        actor.sendMessage("Enabled debug");
    }

    /*
     * Help command
     */

    @Action("?")
    @Action("help")
    @Description("command.description.betterinputs.help")
    public void help(CommandManager commandManager, Actor<?> actor, @Argument(name = "command") String command) {
        Triple<NodeCommand, Node, String> triple = commandManager.findNode(command);
        if (triple == null) {
            actor.sendTranslatedMessage("command.help.command.none", Key.of("command", command));
            return;
        }
        Node node = triple.getB();
        NodeAction action = node.getAction();
        if (action == null) {
            if (!node.hasChildren()) {
                actor.sendTranslatedMessage("command.help.command.empty", Key.of("command", command),
                    Key.of("description", "$#" + triple.getA().getDescription()));
                return;
            }
            actor.sendTranslatedMessage("command.help.command.tree", Key.of("command", triple.getC()),
                Key.of("description", "$#" + triple.getA().getDescription()), Key.of("tree", generateTree(actor, node.getNames())));
            return;
        }
        StringBuilder argumentBuilder = new StringBuilder(actor.getTranslatedMessageAsString("command.help.argument.format.header"));
        List<NodeArgument> argumentList = action.getArguments();
        boolean found = false;
        for (int index = 0; index < argumentList.size(); index++) {
            NodeArgument argument = argumentList.get(index);
            if (argument.isProvided()) {
                continue;
            }
            found = true;
            argumentBuilder.append('\n');
            String type = ClassUtil.getClassName(argument.getArgumentType());
            if (argument.isOptional()) {
                argumentBuilder.append(actor.getTranslatedMessageAsString("command.help.argument.format.optional",
                    Key.of("name", argument.getName()), Key.of("type", type)));
                continue;
            }
            argumentBuilder.append(actor.getTranslatedMessageAsString("command.help.argument.format.required",
                Key.of("name", argument.getName()), Key.of("type", type)));
        }
        String arguments = found ? argumentBuilder.toString() : actor.getTranslatedMessageAsString("command.help.argument.no-arguments");
        if (node.hasChildren()) {
            actor.sendTranslatedMessage("command.help.command.tree-executable", Key.of("command", triple.getC()),
                Key.of("description", "$#" + action.getDescription()), Key.of("arguments", arguments),
                Key.of("tree", generateTree(actor, node.getNames())));
            return;
        }
        actor.sendTranslatedMessage("command.help.command.executable", Key.of("command", triple.getC()),
            Key.of("description", "$#" + action.getDescription()), Key.of("arguments", arguments));
    }

    private String generateTree(Actor<?> actor, String[] names) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < names.length; index++) {
            builder.append(actor.getTranslatedMessageAsString("command.help.tree.format", Key.of("name", names[index])));
            if (index + 1 != names.length) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

}
