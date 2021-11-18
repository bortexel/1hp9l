package ru.bortexel.hardcore;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.loader.api.FabricLoader;
import ru.bortexel.hardcore.listeners.DeathListener;
import ru.bortexel.hardcore.storage.PlayerDataManager;
import ru.bortexel.hardcore.storage.SQLiteDataStorage;

import java.nio.file.Path;

public final class BortexelHardcore implements DedicatedServerModInitializer {
    private static BortexelHardcore instance;
    private SQLiteDataStorage storage;
    private PlayerDataManager manager;

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

        ServerPlayerEvents.ALLOW_DEATH.register(new DeathListener());

        this.setManager(new PlayerDataManager(this.getStorage()));
        instance = this;
    }

    public SQLiteDataStorage getStorage() {
        return storage;
    }

    private void setStorage(SQLiteDataStorage storage) {
        this.storage = storage;
    }

    public PlayerDataManager getManager() {
        return manager;
    }

    private void setManager(PlayerDataManager manager) {
        this.manager = manager;
    }
}
