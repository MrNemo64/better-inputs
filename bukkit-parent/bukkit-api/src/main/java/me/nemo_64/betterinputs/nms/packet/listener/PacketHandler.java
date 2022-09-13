package me.nemo_64.betterinputs.nms.packet.listener;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Target;

@Target(METHOD)
public @interface PacketHandler {

    /**
     * This value describes if the packet handler should receive already cancelled
     * packets or not
     * 
     * @return if the handler receives cancelled packets
     */
    boolean value() default false;

}
