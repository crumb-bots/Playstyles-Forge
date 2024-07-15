package com.wsquarepa.playstylesforge.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class InventoryStore {
    private static InventoryStore instance;

    private InventoryStore() {
    }

    public static InventoryStore getInstance() {
        if (instance == null) {
            instance = new InventoryStore();
        }

        return instance;
    }

    HashMap<UUID, PlayerInventoryRecord> records = new HashMap<>();

    public void addRecord(UUID player, NonNullList<ItemStack> armor, NonNullList<ItemStack> offhand, NonNullList<ItemStack> main) {
        records.put(player, new PlayerInventoryRecord(player, System.currentTimeMillis(), armor, offhand, main));
    }

    public PlayerInventoryRecord getRecord(UUID player, long maxTime) {
        PlayerInventoryRecord record = records.get(player);

        if (record == null) {
            return null;
        }

        if (System.currentTimeMillis() - record.time() > maxTime) {
            records.remove(player);
            return null;
        }

        return record;
    }
}
