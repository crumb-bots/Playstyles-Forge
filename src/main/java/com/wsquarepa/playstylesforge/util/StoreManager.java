package com.wsquarepa.playstylesforge.util;

import com.google.gson.Gson;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class StoreManager {
    private static StoreManager INSTANCE;
    private static Storage storage;
    private MinecraftServer server;

    private StoreManager() {

    }

    public static StoreManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StoreManager();
        }
        return INSTANCE;
    }

    public static Storage getStorage() {
        return storage;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
        load();
    }

    public void load() {
        File configFile = new File(server.getServerDirectory(), "config/playstyle/storage.json");

        if (!configFile.exists()) {
            storage = new Storage();
            save();
        }

        try {
            String json = Files.readString(configFile.toPath());

            Gson gson = new Gson();
            storage = gson.fromJson(json, Storage.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        File configFile = new File(server.getServerDirectory(), "config/playstyle/storage.json");

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
        }

        Gson gson = new Gson();
        String json = gson.toJson(storage);

        try {
            FileWriter writer = new FileWriter(configFile);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
