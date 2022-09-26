package me.nemo_64.betterinputs.bukkit.command.argument;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.IArgumentType;
import me.lauriichan.laylib.reflection.ClassUtil;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;

public final class ArgumentMapType implements IArgumentType<ArgumentMap> {

    // name|type|value
    // ;
    
    // name|type|value;name|type|value

    private static final int STATE_NAME = 0;
    private static final int STATE_TYPE = 1;
    private static final int STATE_VALUE = 3;

    @Override
    public ArgumentMap parse(Actor<?> actor, String input) throws IllegalArgumentException {
        ArgumentMap map = new ArgumentMap();
        if (input.isBlank()) {
            return map;
        }
        String argumentName = null;
        int argumentType = -1;

        int state = STATE_NAME;
        int start = 0;
        int size = input.length();
        boolean escaped = false;
        StringBuilder buffer = new StringBuilder();
        loop:
        for (int index = 0; index < size; index++) {
            char character = input.charAt(index);
            switch (character) {
            case '\\':
                if (state != STATE_VALUE && state != STATE_NAME) {
                    throw buildWithContext(map.size(), start, index, input, "Can't use escape character at type position");
                }
                escaped = true;
                buffer.append(character);
                continue loop;
            case '|':
                if (escaped) {
                    escaped = false;
                    buffer.setCharAt(buffer.length() - 1, character);
                    continue loop;
                }
                if (state == STATE_NAME) {
                    if (buffer.length() == 0) {
                        throw buildWithContext(map.size(), start, index, input, "Name can't be empty");
                    }
                    argumentName = buffer.toString();
                    buffer = new StringBuilder();
                    state = STATE_TYPE;
                    continue loop;
                }
                if (state == STATE_TYPE) {
                    try {
                        argumentType = Integer.parseInt(buffer.toString());
                    } catch (NumberFormatException nfe) {
                        throw buildWithContext(map.size(), start, index, input, "Couldn't parse type");
                    }
                    if (argumentType < 0 || argumentType > 15) {
                        throw buildWithContext(map.size(), start, index, input, "ArgumentType can't be lower than 0 or higher than 15");
                    }
                    buffer = new StringBuilder();
                    state = STATE_VALUE;
                    continue loop;
                }
                throw buildWithContext(map.size(), start, index, input, "Unescaped '|' character in argument value!");
            case ';':
                if (escaped) {
                    escaped = false;
                    buffer.setCharAt(buffer.length() - 1, character);
                    continue loop;
                }
                if (state != STATE_VALUE) {
                    throw buildWithContext(map.size(), start, index, input,
                        "Can't start new argument without previous argument being complete");
                }
                parseArgument(map, argumentName, argumentType, start, index, input, buffer.toString());
                buffer = new StringBuilder();
                state = STATE_NAME;
                continue loop;
            default:
                if (state == STATE_TYPE && !Character.isDigit(character)) {
                    throw buildWithContext(map.size(), start, index, input, "Only digits allowed at type position");
                }
                escaped = false;
                buffer.append(character);
                continue loop;
            }
        }
        if (state == STATE_NAME && buffer.length() == 0) {
            return map;
        }
        if (state == STATE_VALUE) {
            parseArgument(map, argumentName, argumentType, start, size - 1, input, buffer.toString());
            return map;
        }
        throw buildWithContext(map.size(), start, size - 1, input, "Uncomplete argument");
    }

    private void parseArgument(ArgumentMap map, String name, int type, int start, int index, String input, String buffer) {
        if (type > 7) {
            throw buildWithContext(map.size(), start, index, input, "ArgumentType " + type + " currently not supported!");
        }
        switch (type) {
        case 0:
            if (buffer.isBlank()) {
                throw buildWithContext(map.size(), start, index, input, "String value can't be empty");
            }
            map.set(name, buffer);
            return;
        case 1:
            try {
                map.set(name, Byte.parseByte(buffer));
            } catch (NumberFormatException nfe) {
                throw buildWithContext(map.size(), start, index, input, "Couldn't parse argument byte value");
            }
            return;
        case 2:
            try {
                map.set(name, Short.parseShort(buffer));
            } catch (NumberFormatException nfe) {
                throw buildWithContext(map.size(), start, index, input, "Couldn't parse argument short value");
            }
            return;
        case 3:
            try {
                map.set(name, Integer.parseInt(buffer));
            } catch (NumberFormatException nfe) {
                throw buildWithContext(map.size(), start, index, input, "Couldn't parse argument integer value");
            }
            return;
        case 4:
            try {
                map.set(name, Long.parseLong(buffer));
            } catch (NumberFormatException nfe) {
                throw buildWithContext(map.size(), start, index, input, "Couldn't parse argument long value");
            }
            return;
        case 5:
            try {
                map.set(name, Float.parseFloat(buffer));
            } catch (NumberFormatException nfe) {
                throw buildWithContext(map.size(), start, index, input, "Couldn't parse argument float value");
            }
            return;
        case 6:
            try {
                map.set(name, Double.parseDouble(buffer));
            } catch (NumberFormatException nfe) {
                throw buildWithContext(map.size(), start, index, input, "Couldn't parse argument double value");
            }
            return;
        case 7:
            Class<?> clazz = ClassUtil.findClass(buffer);
            if (clazz == null) {
                throw buildWithContext(map.size(), start, index, input, "Couldn't find class '" + buffer + "'!");
            }
            map.set(name, clazz);
            return;
        }
    }

    private IllegalArgumentException buildWithContext(int argumentId, int start, int index, String string, String reason) {
        StringBuilder builder = new StringBuilder(reason);
        builder.append(" at argument ").append(argumentId + 1).append(" => &l");
        if (start != 0) {
            builder.append("...");
        }
        builder.append("&n").append(string.substring(start, index)).append("&r&c<--- HERE");
        return new IllegalArgumentException(builder.toString());
    }

}
