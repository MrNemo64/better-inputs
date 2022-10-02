package me.nemo_64.betterinputs.api.platform;

public interface IPlatformActor<H> {

    /**
     * Checks if the actor is valid
     * 
     * @return {@code true} if the actor is valid otherwise {@code false}
     */
    default boolean isAvailable() {
        return true;
    }

    /**
     * Gets the platform handle of this actor
     * 
     * @return the platform handle
     */
    H getHandle();

    /**
     * Tries to cast this actor to the specified handle type
     * 
     * @param  <P>   the provided handle type
     * @param  clazz the handle type
     * 
     * @return       the same actor casted to the specified handle type or an
     *                   {@code me.nemo_64.betterinputs.api.platform.InvalidActor}
     */
    <P> IPlatformActor<P> as(Class<P> clazz);

    /**
     * Sends a message to the actor
     * 
     * @param message the message to be sent
     */
    void sendMessage(String message);

}
