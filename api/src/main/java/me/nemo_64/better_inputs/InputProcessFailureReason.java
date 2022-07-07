package me.nemo_64.better_inputs;

public interface InputProcessFailureReason {

    InputProcessFailureReason UNKNOWN = () -> "The input process failed but the reason is unknown";
    InputProcessFailureReason CANCELLED = () -> "The input process was cancelled";
    InputProcessFailureReason UNABLE_TO_START = () -> "The input process could not be started";

    String description();

}