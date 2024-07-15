package com.wsquarepa.playstylesforge.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public record PlayerInventoryRecord(UUID player, long time, NonNullList<ItemStack> armor, NonNullList<ItemStack> offhand, NonNullList<ItemStack> main) {
    public void restore(Player player) {
        player.getInventory().armor.clear();
        player.getInventory().offhand.clear();
        player.getInventory().items.clear();

        for (int i = 0; i < armor.size(); i++) {
            player.getInventory().armor.set(i, armor.get(i).copy());
        }

        for (int i = 0; i < offhand.size(); i++) {
            player.getInventory().offhand.set(i, offhand.get(i).copy());
        }

        for (int i = 0; i < main.size(); i++) {
            player.getInventory().items.set(i, main.get(i).copy());
        }

        player.getInventory().setChanged();
    }
}
