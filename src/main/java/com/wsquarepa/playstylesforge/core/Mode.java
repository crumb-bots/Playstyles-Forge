package com.wsquarepa.playstylesforge.core;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Arrays;
import java.util.Collection;

public enum Mode {
    DIFFICULT (
            Component.literal("DIFFICULT")
                    .append("\nFor the ones who want an extra challenge")
                    .append("\n\n- Receive 1.5x more damage")
    ),
    NORMAL (
            Component.literal("NORMAL")
                    .append("\nFor the ones who want to play the game as it is")
                    .append("\n\nThis playstyle changes nothing. ")
    ),
    RETAIN (
            Component.literal("RETAIN")
                    .append("\nFor the ones who want to keep their items on death")
                    .append("\n\n- Keep your items on death (to non-players)")
                    .append("\n- Keep your experience on death (to non-players)")
                    .append("\n- Cannot attack other players, but other players can attack you")
    ),
    PEACEFUL (
            Component.literal("PEACEFUL")
                    .append("\nFor the ones who like peaceful mode")
                    .append("\n\n- Monsters won't target you")
                    .append("\n- Keep your items on death")
                    .append("\n- Keep your experience on death")
                    .append("\n- PvP & PvE disabled")
                    .append("\n- Always glowing")
    );


    public final MutableComponent description;

    Mode(MutableComponent desc) {
        this.description = desc;
    }

    public static Collection<String> getNames() {
        return Arrays.stream(Mode.values()).map(Enum::name).toList();
    }

    public static Collection<Mode> getModes() {
        return Arrays.stream(Mode.values()).toList();
    }

    public String getName() {
        return name();
    }

    public MutableComponent getDescription() {
        return description;
    }
}
