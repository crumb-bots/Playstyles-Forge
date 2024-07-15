package com.wsquarepa.playstylesforge.util;

import com.wsquarepa.playstylesforge.core.User;

import java.util.HashMap;
import java.util.UUID;

public class Storage {
    private final HashMap<UUID, User> players;

    public Storage() {
        this.players = new HashMap<>();
    }

    public User getUser(UUID uuid) {
        players.putIfAbsent(uuid, new User(uuid));
        return players.get(uuid);
    }
}
