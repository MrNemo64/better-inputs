package me.nemo_64.better_inputs;

/**
 * Represents the sender of an input process and allows to send messages to it. <br>
 * It is recommended that implementations have a {@link #hashCode()} function
 * @param <R> Type of the data containing information about the sent message. In most implementations will be {@link
 * java.lang.Void} (when no information about the sent message is needed) or {@link java.lang.Boolean} (if the message
 * can fail to be sent).
 */
public interface InputProcessSender<R> {

    /**
     * Sends a message to the sender of the input process. This is the most basic way to send a message, if more complex
     * ways to send messages are needed the implementations have to provide them
     * @param message Message to be sent
     * @return Depending on the implementation of the sender, a value containing information about the sent message
     */
    R sendMessage(String message);

}