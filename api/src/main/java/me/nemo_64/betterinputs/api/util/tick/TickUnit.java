package me.nemo_64.betterinputs.api.util.tick;

public enum TickUnit {

    TICK(TickUnit.TICK_SCALE),
    SECOND(TickUnit.SECOND_SCALE),
    MINUTE(TickUnit.MINUTE_SCALE),
    HOUR(TickUnit.HOUR_SCALE),
    DAY(TickUnit.DAY_SCALE),
    WEEK(TickUnit.WEEK_SCALE);

    private static final long TICK_SCALE = 1L;
    private static final long SECOND_SCALE = TICK_SCALE * 20L;
    private static final long MINUTE_SCALE = SECOND_SCALE * 60L;
    private static final long HOUR_SCALE = MINUTE_SCALE * 60L;
    private static final long DAY_SCALE = HOUR_SCALE * 24L;
    private static final long WEEK_SCALE = DAY_SCALE * 7L;

    private final long scale;

    private TickUnit(long scale) {
        this.scale = scale;
    }

    private long convert(long scale, long amount) {
        long selfScale = this.scale;
        if (selfScale == scale) {
            return amount;
        }
        if (selfScale < scale) {
            return amount / (scale / selfScale);
        }
        long scalar = (selfScale / scale);
        long max = Long.MAX_VALUE / scalar;
        if (amount > max) {
            return Long.MAX_VALUE;
        }
        if (amount < -max) {
            return Long.MIN_VALUE;
        }
        return amount * (selfScale / scale);
    }

    public long toTicks(long amount) {
        return convert(TICK_SCALE, amount);
    }

    public long toSeconds(long amount) {
        return convert(SECOND_SCALE, amount);
    }

    public long toMinutes(long amount) {
        return convert(MINUTE_SCALE, amount);
    }

    public long toHours(long amount) {
        return convert(HOUR_SCALE, amount);
    }

    public long toDays(long amount) {
        return convert(DAY_SCALE, amount);
    }

    public long toWeeks(long amount) {
        return convert(WEEK_SCALE, amount);
    }

}
