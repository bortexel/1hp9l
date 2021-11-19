package ru.bortexel.hardcore.storage;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
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

    public boolean decrementLiveCount(ServerPlayerEntity player) {
        try {
            StoredPlayerData playerData = this.getProvider().getPlayerData(player);
            playerData.setLives(playerData.getLives() - 1);
            this.getProvider().savePlayerData(playerData);
            BortexelHardcore.getInstance().getScoreboardManager().forceUpdate(player);
            return playerData.getLives() >= 0;
        } catch (Exception e) {
            logger.error("Unable to decrement live count for player {}", player.getEntityName(), e);
            return false;
        }
    }

    public boolean canPlay(ServerPlayerEntity player) {
        try {
            StoredPlayerData playerData = this.getProvider().getPlayerData(player);
            return playerData.getLives() >= 0;
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
        } catch (Exception e) {
            logger.error("Unable to grant points to player {}", player.getEntityName(), e);
        }
    }

    public PlayerDataProvider getProvider() {
        return provider;
    }
}
