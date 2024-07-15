package com.wsquarepa.playstylesforge;

import com.wsquarepa.playstylesforge.commands.MainCommand;
import com.wsquarepa.playstylesforge.core.Mode;
import com.wsquarepa.playstylesforge.core.User;
import com.wsquarepa.playstylesforge.listeners.DamageListeners;
import com.wsquarepa.playstylesforge.listeners.EntityListeners;
import com.wsquarepa.playstylesforge.util.StoreManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

@Mod("playstyle")
public class Playstyle {

    public Playstyle() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Register event listeners
        MinecraftForge.EVENT_BUS.register(new DamageListeners());
        MinecraftForge.EVENT_BUS.register(new EntityListeners());
    }

    @SubscribeEvent
    public void registerCommandsEvent(RegisterCommandsEvent event) {
        MainCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event) {
        StoreManager.getInstance().setServer(ServerLifecycleHooks.getCurrentServer());

        // Schedule saving storage
        MinecraftForge.EVENT_BUS.addListener(this::onServerTick);
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        StoreManager.getInstance().save();
    }

    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            StoreManager.getInstance().save();
            ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(player -> {
                User user = StoreManager.getStorage().getUser(UUID.fromString(player.getStringUUID()));

                if (user.getMode().equals(Mode.PEACEFUL)) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.GLOWING, 20 * 60, 0, true, false));
                }
            });
        }
    }
}
