package ru.bortexel.hardcore;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import ru.bortexel.hardcore.listeners.DeathListener;
import ru.bortexel.hardcore.storage.PlayerDataManager;
import ru.bortexel.hardcore.storage.SQLiteDataStorage;

import java.nio.file.Path;

public final class BortexelHardcore implements DedicatedServerModInitializer {
    private static BortexelHardcore instance;
    private SQLiteDataStorage storage;
    private ScoreboardManager scoreboardManager;
    private PlayerDataManager playerDataManager;
    private MinecraftServer server;

    public static BortexelHardcore getInstance() {
        return instance;
    }

    @Override
    public void onInitializeServer() {
        try {
            Path path = FabricLoader.getInstance().getGameDir().resolve("mods/bortexel/1hp9l.db");
            SQLiteDataStorage storage = new SQLiteDataStorage("jdbc:sqlite:" + path);
            storage.initialize();
            this.setStorage(storage);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ScoreboardManager scoreboardManager = new ScoreboardManager(server.getScoreboard());
            scoreboardManager.init();
            this.setScoreboardManager(scoreboardManager);
        });

        ServerPlayerEvents.ALLOW_DEATH.register(new DeathListener());

        this.setPlayerDataManager(new PlayerDataManager(this.getStorage()));
        instance = this;
    }

    public SQLiteDataStorage getStorage() {
        return storage;
    }

    private void setStorage(SQLiteDataStorage storage) {
        this.storage = storage;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    private void setPlayerDataManager(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public MinecraftServer getServer() {
        return server;
    }

    private void setServer(MinecraftServer server) {
        this.server = server;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    private void setScoreboardManager(ScoreboardManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }
}
