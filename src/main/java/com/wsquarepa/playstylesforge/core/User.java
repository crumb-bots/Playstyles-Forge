package com.wsquarepa.playstylesforge.core;

import com.wsquarepa.playstylesforge.core.Mode;

import java.util.UUID;

public class User {
    private final UUID uuid;
    private Mode mode;
    private long lastModeChange;

    public User(UUID player) {
        this.uuid = player;
        this.mode = Mode.NORMAL;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        this.lastModeChange = System.currentTimeMillis();
    }

    public long getNextChange() {
        return lastModeChange + 1000 * 60 * 60 * 24;
    }

    public boolean canChange() {
        return System.currentTimeMillis() >= getNextChange();
    }
}
