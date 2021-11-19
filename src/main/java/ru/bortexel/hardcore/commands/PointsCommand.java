package ru.bortexel.hardcore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import ru.bortexel.hardcore.BortexelHardcore;
import ru.bortexel.hardcore.storage.SQLiteDataStorage;
import ru.bortexel.hardcore.storage.StoredPlayerData;

public class PointsCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            SQLiteDataStorage storage = BortexelHardcore.getInstance().getStorage();
            StoredPlayerData playerData = storage.getPlayerData(player);

            LiteralText prefix = new LiteralText("У Вас сейчас ");
            MutableText amountText = new LiteralText("" + playerData.getPoints()).formatted(Formatting.GOLD);
            LiteralText pointsText = new LiteralText(" очков");
            context.getSource().sendFeedback(prefix.append(amountText).append(pointsText), false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommandException(new LiteralText("Не удалось проверить количество очков"));
        }
        return 0;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("points").executes(new PointsCommand()));
    }
}
