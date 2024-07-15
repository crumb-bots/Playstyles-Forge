package com.wsquarepa.playstylesforge.listeners;

import com.wsquarepa.playstylesforge.core.Mode;
import com.wsquarepa.playstylesforge.util.StoreManager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityListeners {

    @SubscribeEvent
    public void onEntityTarget(LivingChangeTargetEvent event) {
        if (event.getNewTarget() instanceof Player player) {
            if (StoreManager.getStorage().getUser(player.getUUID()).getMode().equals(Mode.PEACEFUL)) {
                event.setCanceled(true);
            }
        }
    }
}
