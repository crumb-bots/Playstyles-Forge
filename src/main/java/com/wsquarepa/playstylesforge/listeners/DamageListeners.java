package com.wsquarepa.playstylesforge.listeners;

import com.wsquarepa.playstylesforge.core.Mode;
import com.wsquarepa.playstylesforge.inventory.InventoryStore;
import com.wsquarepa.playstylesforge.inventory.PlayerInventoryRecord;
import com.wsquarepa.playstylesforge.util.StoreManager;
import com.wsquarepa.playstylesforge.util.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

public class DamageListeners {
    private final HashMap<UUID, Long> notifyCooldown = new HashMap<>();

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Mode mode = StoreManager.getStorage().getUser(player.getUUID()).getMode();
            if (mode.equals(Mode.PEACEFUL) || mode.equals(Mode.RETAIN)) {
                NonNullList<ItemStack> armor = NonNullList.withSize(player.getInventory().armor.size(), ItemStack.EMPTY);
                NonNullList<ItemStack> offhand = NonNullList.withSize(player.getInventory().offhand.size(), ItemStack.EMPTY);
                NonNullList<ItemStack> main = NonNullList.withSize(player.getInventory().items.size(), ItemStack.EMPTY);

                for (int i = 0; i < player.getInventory().armor.size(); i++) {
                    armor.set(i, player.getInventory().armor.get(i).copy());
                }
                for (int i = 0; i < player.getInventory().offhand.size(); i++) {
                    offhand.set(i, player.getInventory().offhand.get(i).copy());
                }
                for (int i = 0; i < player.getInventory().items.size(); i++) {
                    main.set(i, player.getInventory().items.get(i).copy());
                }

                InventoryStore.getInstance().addRecord(player.getUUID(), armor, offhand, main);

                player.getInventory().clearContent();
                player.getInventory().setChanged();

                player.sendSystemMessage(Component.literal("Respawn within 5 minutes to restore your inventory."));
            }
        }
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        PlayerInventoryRecord record = InventoryStore.getInstance().getRecord(player.getUUID(), 1000 * 60 * 5);

        if (record != null) {
            record.restore(player);
        }
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        if (event.getTarget() instanceof Player damaged) {
            Mode damagedMode = StoreManager.getStorage().getUser(damaged.getUUID()).getMode();

            if (damagedMode.equals(Mode.PEACEFUL)) {
                event.setCanceled(true);

                Player damaging = event.getEntity();

                if (notifyCooldown.containsKey(damaging.getUUID())) {
                    long lastNotify = notifyCooldown.get(damaging.getUUID());
                    if (System.currentTimeMillis() - lastNotify < 1000 * 5) return;
                }

                notifyCooldown.put(damaging.getUUID(), System.currentTimeMillis());

                damaging.sendSystemMessage(Component.literal("You cannot attack peaceful players."));
            }
        } else {
            Player damaging = event.getEntity();

            Mode damagingMode = StoreManager.getStorage().getUser(damaging.getUUID()).getMode();

            if (damagingMode.equals(Mode.PEACEFUL) && !Util.isPeacefulMob(event.getTarget().getType())) {
                event.setCanceled(true);

                if (notifyCooldown.containsKey(damaging.getUUID())) {
                    long lastNotify = notifyCooldown.get(damaging.getUUID());
                    if (System.currentTimeMillis() - lastNotify < 1000 * 5) return;
                }

                notifyCooldown.put(damaging.getUUID(), System.currentTimeMillis());
                damaging.sendSystemMessage(Component.literal("You are in peaceful mode! You cannot attack other entities."));
            }

            if (damagingMode.equals(Mode.RETAIN) && event.getTarget() instanceof Player) {
                event.setCanceled(true);

                if (notifyCooldown.containsKey(damaging.getUUID())) {
                    long lastNotify = notifyCooldown.get(damaging.getUUID());
                    if (System.currentTimeMillis() - lastNotify < 1000 * 5) return;
                }

                notifyCooldown.put(damaging.getUUID(), System.currentTimeMillis());
                damaging.sendSystemMessage(Component.literal("You are in retain mode! You cannot attack other players."));
            }
        }
    }

    @SubscribeEvent
    public void onDamage(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            Mode mode = StoreManager.getStorage().getUser(player.getUUID()).getMode();
            if (mode.equals(Mode.DIFFICULT)) {
                event.setAmount(event.getAmount() * 1.5f);
            }
        }
    }
}
