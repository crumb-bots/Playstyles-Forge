package com.wsquarepa.playstylesforge.listeners;

import com.wsquarepa.playstylesforge.core.Mode;
import com.wsquarepa.playstylesforge.util.StoreManager;
import com.wsquarepa.playstylesforge.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

public class DamageListeners {
    private final HashMap<UUID, Long> notifyCooldown = new HashMap<>();

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            Mode mode = StoreManager.getStorage().getUser(player.getUUID()).getMode();
            if (mode.equals(Mode.PEACEFUL) || mode.equals(Mode.RETAIN)) {
                event.setCanceled(true);
                player.respawn();
            }
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
