package ru.bortexel.hardcore.storage;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bortexel.hardcore.BortexelHardcore;

public class PlayerDataManager {
    private static final Logger logger = LoggerFactory.getLogger("PlayerDataManager");

    private static PlayerDataManager instance;
    private final PlayerDataProvider provider;

    public static PlayerDataManager getInstance() {
        return instance;
    }

    public PlayerDataManager(PlayerDataProvider provider) {
        this.provider = provider;
        instance = this;
    }

    public void consumeLive(ServerPlayerEntity player) {
        PlayerDataManager manager = BortexelHardcore.getInstance().getPlayerDataManager();
        if (!manager.decrementLiveCount(player))
            player.networkHandler.disconnect(new LiteralText("У Вас закончились жизни"));
    }

    public int purchaseLive(ServerPlayerEntity player) {
        try {
            StoredPlayerData playerData = this.getProvider().getPlayerData(player);
            int price = 150 + 100 * playerData.getBoughtLives();
            if (!this.revokePoints(player, playerData, price))
                return price - playerData.getPoints();
            playerData.setLives(playerData.getLives() + 1);
            playerData.setBoughtLives(playerData.getBoughtLives() + 1);
            this.getProvider().savePlayerData(playerData);
            BortexelHardcore.getInstance().getScoreboardManager().forceUpdate(player);
            return 0;
        } catch (Exception e) {
            logger.error("Unable to process live purchase for player {}", player.getEntityName(), e);
            return -1;
        }
    }

    public boolean decrementLiveCount(ServerPlayerEntity player) {
        try {
            StoredPlayerData playerData = this.getProvider().getPlayerData(player);
            playerData.setLives(playerData.getLives() - 1);
            this.getProvider().savePlayerData(playerData);
            BortexelHardcore.getInstance().getScoreboardManager().forceUpdate(player);
            return playerData.getLives() > 0;
        } catch (Exception e) {
            logger.error("Unable to decrement live count for player {}", player.getEntityName(), e);
            return false;
        }
    }

    public boolean canPlay(ServerPlayerEntity player) {
        try {
            StoredPlayerData playerData = this.getProvider().getPlayerData(player);
            return playerData.getLives() > 0;
        } catch (Exception e) {
            logger.error("Unable to verify player {}", player.getEntityName(), e);
            return false;
        }
    }

    public void grantPoints(ServerPlayerEntity player, int points) {
        try {
            StoredPlayerData playerData = this.getProvider().getPlayerData(player);
            playerData.setPoints(playerData.getPoints() + points);
            this.getProvider().savePlayerData(playerData);
            player.sendMessage(this.pointsUpdateMessage(points), true);
            BortexelHardcore.getInstance().getScoreboardManager().forceUpdate(player);
        } catch (Exception e) {
            logger.error("Unable to grant points to player {}", player.getEntityName(), e);
        }
    }

    public boolean revokePoints(ServerPlayerEntity player, StoredPlayerData playerData, int points) {
        try {
            if (!playerData.revokePoints(points)) return false;
            this.getProvider().savePlayerData(playerData);
            player.sendMessage(this.pointsUpdateMessage(-points), true);
            BortexelHardcore.getInstance().getScoreboardManager().forceUpdate(player);
            return true;
        } catch (Exception e) {
            logger.error("Unable to revoke points from player {}", player.getEntityName(), e);
            return false;
        }
    }

    public boolean revokePoints(ServerPlayerEntity player, int points) {
        try {
            StoredPlayerData playerData = this.getProvider().getPlayerData(player);
            return this.revokePoints(player, playerData, points);
        } catch (Exception e) {
            logger.error("Unable to revoke points from player {}", player.getEntityName(), e);
            return false;
        }
    }

    private Text pointsUpdateMessage(int amount) {
        LiteralText signText = new LiteralText(amount > 0 ? " + " : " - ");
        MutableText amountText = new LiteralText(Math.abs(amount) + " ").styled(style -> style.withColor(Formatting.GOLD));
        MutableText pointsText = new LiteralText(System.currentTimeMillis() % 20 == 0 ? "social credit" : "очков")
                .styled(style -> style.withColor(Formatting.WHITE));
        return signText.append(amountText).append(pointsText);
    }

    public PlayerDataProvider getProvider() {
        return provider;
    }
}
