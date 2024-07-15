package com.wsquarepa.playstylesforge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wsquarepa.playstylesforge.core.Mode;
import com.wsquarepa.playstylesforge.core.User;
import com.wsquarepa.playstylesforge.util.StoreManager;
import com.wsquarepa.playstylesforge.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MainCommand {
    private static final HashMap<UUID, Long> confirmations = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("playstyle")
                .then(Commands.literal("set")
                        .then(Commands.argument("mode", StringArgumentType.string())
                                .executes(MainCommand::onSet)))
                .then(Commands.literal("confirm")
                        .executes(MainCommand::onConfirm))
                .executes(MainCommand::onDefault);

        dispatcher.register(builder);
    }

    public static int onDefault(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 1;
        }

        Collection<Mode> modes = Mode.getModes();
        MutableComponent message = Component.literal("Available modes:\nHover over a mode to see its description\nClick on a mode to set it\n\n");

        for (Mode mode : modes) {
            message = message.append("- ")
                    .append(Component.literal(mode.getName())
                            .withStyle(style ->
                                    style.withColor(ChatFormatting.AQUA)
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, mode.getDescription()))
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/playstyle set " + mode.getName()))
                            )
                    )
                    .append("\n");
        }

        source.sendSystemMessage(message);
        return 1;
    }

    public static int onSet(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String modeName = StringArgumentType.getString(context, "mode");
        Mode mode = Mode.valueOf(modeName);

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 1;
        }

        MutableComponent message = Component.literal("Are you sure?\nThis will change your playstyle to " + mode.getName() +
                "\nYou won't be able to change it again for 24 hours.\nClick here to confirm.").withStyle(style ->
                    style.withColor(ChatFormatting.RED)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/playstyle confirm " + modeName))
                );

        confirmations.put(player.getUUID(), System.currentTimeMillis());
        source.sendSystemMessage(message);
        return 1;
    }

    public static int onConfirm(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 1;
        }

        if (!confirmations.containsKey(player.getUUID())) {
            source.sendFailure(Component.literal("Use this command properly."));
            return 1;
        }

        long time = confirmations.get(player.getUUID());
        long timeLeftToConfirm = System.currentTimeMillis() - time;
        if (timeLeftToConfirm > 1000 * 60) {
            source.sendFailure(Component.literal("Confirmation expired."));
            return 1;
        }

        confirmations.remove(player.getUUID());

        User user = StoreManager.getStorage().getUser(player.getUUID());
        if (!user.canChange()) {
            long timeLeft = user.getNextChange() - System.currentTimeMillis();
            String formattedTimeLeft = Util.timeToString(timeLeft);

            source.sendFailure(Component.literal("You can't change your playstyle yet.\nYou can change it again in " + formattedTimeLeft + "."));
            return 1;
        }

        Mode mode = Mode.valueOf(StringArgumentType.getString(context, "mode"));
        if (mode.equals(user.getMode())) {
            source.sendFailure(Component.literal("You are already in this mode."));
            return 1;
        }

        user.setMode(mode);
        source.sendSystemMessage(Component.literal("Your playstyle has been changed to " + mode.getName() + "."));
        return 1;
    }
}
