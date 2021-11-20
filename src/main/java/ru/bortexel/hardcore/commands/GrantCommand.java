package ru.bortexel.hardcore.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import ru.bortexel.hardcore.BortexelHardcore;
import ru.bortexel.hardcore.storage.SQLiteDataStorage;
import ru.bortexel.hardcore.storage.StoredPlayerData;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GrantCommand {
    private static int executeGrantLives(CommandContext<ServerCommandSource> context) {
        int lives = IntegerArgumentType.getInteger(context, "amount");
        StoredPlayerData playerData = getPlayer(context);
        playerData.setLives(playerData.getLives() + lives);
        BortexelHardcore.getInstance().getStorage().savePlayerData(playerData);
        context.getSource().sendFeedback(new LiteralText("Выдано " + lives + " жизней игроку " + playerData.getPlayerName()), true);
        return 0;
    }

    private static int executeGrantPoints(CommandContext<ServerCommandSource> context) {
        int points = IntegerArgumentType.getInteger(context, "amount");
        StoredPlayerData playerData = getPlayer(context);
        playerData.setPoints(playerData.getPoints() + points);
        BortexelHardcore.getInstance().getStorage().savePlayerData(playerData);
        context.getSource().sendFeedback(new LiteralText("Выдано " + points + " поинтов игроку " + playerData.getPlayerName()), true);
        return 0;
    }

    private static StoredPlayerData getPlayer(CommandContext<ServerCommandSource> context) throws CommandException {
        try {
            SQLiteDataStorage storage = BortexelHardcore.getInstance().getStorage();
            Optional<StoredPlayerData> playerData = storage.getPlayerData(UuidArgumentType.getUuid(context, "uuid"));
            if (playerData.isEmpty()) throw new CommandException(new LiteralText("Неизвестный игрок"));
            return playerData.get();
        } catch (Exception e) {
            throw new CommandException(new LiteralText(e.getMessage()));
        }
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("grant").requires(source -> source.hasPermissionLevel(3))
                .then(literal("lives").then(
                        argument("uuid", UuidArgumentType.uuid()).then(
                                argument("amount", IntegerArgumentType.integer(0)).executes(GrantCommand::executeGrantLives)
                        )
                ))
                .then(literal("points").then(
                        argument("uuid", UuidArgumentType.uuid()).then(
                                argument("amount", IntegerArgumentType.integer(0)).executes(GrantCommand::executeGrantPoints)
                        )
                ))
        );
    }
}
