package net.herospvp.blacklist.database;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class Store {

    @Getter
    private static final Map<String, String> storedPlayers = new HashMap<>();

    public static void add(String playerName, String reason) {
        storedPlayers.put(playerName, reason);
    }

    public static void remove(String playerName) {
        storedPlayers.remove(playerName);
    }

    public static boolean containsKey(String playerName) {
        return storedPlayers.containsKey(playerName);
    }

}
