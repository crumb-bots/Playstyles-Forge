package com.wsquarepa.playstylesforge.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;

public class Util {
    private Util() {
    }

    public static <T> NonNullList<T> cloneNonNullList(NonNullList<T> list) {
        NonNullList<T> clone = NonNullList.create();
        clone.addAll(list);
        return clone;
    }

    public static String timeToString(long time) {
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        StringBuilder builder = new StringBuilder();

        if (days > 0) {
            builder.append(days).append(" days");
        }

        if (hours > 0) {
            if (days > 0) {
                builder.append(" ");
            }

            builder.append(hours).append(" hours");
        }

        if (minutes > 0) {
            if (days > 0 || hours > 0) {
                builder.append(" ");
            }

            builder.append(minutes).append(" minutes");
        }

        if (seconds > 0) {
            if (days > 0 || hours > 0 || minutes > 0) {
                builder.append(" ");
            }

            builder.append(seconds).append(" seconds");
        }

        return builder.toString();
    }

    public static boolean isPeacefulMob(EntityType<?> type) {
        return type == EntityType.ARMOR_STAND ||
                type == EntityType.BOAT ||
                type == EntityType.CHEST_BOAT ||
                type == EntityType.MINECART ||
                type == EntityType.CHEST_MINECART ||
                type == EntityType.COMMAND_BLOCK_MINECART ||
                type == EntityType.FURNACE_MINECART ||
                type == EntityType.HOPPER_MINECART ||
                type == EntityType.SPAWNER_MINECART ||
                type == EntityType.TNT_MINECART ||
                type == EntityType.ITEM_FRAME ||
                type == EntityType.LEASH_KNOT ||
                type == EntityType.PAINTING ||
                type == EntityType.FIREBALL ||
                type == EntityType.SHULKER_BULLET ||
                type == EntityType.GLOW_ITEM_FRAME;
    }
}
