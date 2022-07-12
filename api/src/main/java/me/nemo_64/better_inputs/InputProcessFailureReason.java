package me.nemo_64.better_inputs;

public interface InputProcessFailureReason {

    InputProcessFailureReason UNKNOWN = () -> "The input process failed but the reason is unknown";
    InputProcessFailureReason CANCELLED = () -> "The input process was cancelled";
    InputProcessFailureReason UNQUEUED = () -> "The input process was removed from the queue before it could start";
    InputProcessFailureReason UNABLE_TO_START = () -> "The input process could not be started";

    String description();

}