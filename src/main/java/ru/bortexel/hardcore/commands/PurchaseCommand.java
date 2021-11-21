package ru.bortexel.hardcore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import ru.bortexel.hardcore.storage.PlayerDataManager;

import static net.minecraft.server.command.CommandManager.literal;

public class PurchaseCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        PlayerDataManager playerDataManager = PlayerDataManager.getInstance();
        int result = playerDataManager.purchaseLive(player);
        if (result == 0) {
            context.getSource().sendFeedback(new LiteralText("Вы успешно приобрели одну жизнь"), true);
        } else if (result < 0) {
            throw new CommandException(new LiteralText("Не удалось приобрести жизни"));
        } else throw new CommandException(new LiteralText("Для покупки жизни необходимо ещё " + result + " очков"));
        return 0;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("purchase").then(
                literal("live").executes(new PurchaseCommand())
        ));
    }
}
