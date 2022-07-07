package me.nemo_64.better_inputs;

public enum InputProcessPriority implements Comparable<InputProcessPriority> {

    LOW() {
        @Override
        public int priority() {
            return -1;
        }
    },
    NORMAL() {
        @Override
        public int priority() {
            return 0;
        }
    },
    HIGH() {
        @Override
        public int priority() {
            return 1;
        }
    };

    public abstract int priority();

}
