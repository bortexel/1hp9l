package ru.bortexel.hardcore.storage;

import net.minecraft.server.network.ServerPlayerEntity;
import ru.ruscalworld.storagelib.exceptions.InvalidModelException;
import ru.ruscalworld.storagelib.exceptions.NotFoundException;
import ru.ruscalworld.storagelib.impl.SQLiteStorage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLiteDataStorage implements PlayerDataProvider {
    private final SQLiteStorage storage;
    private final HashMap<UUID, StoredPlayerData> cache = new HashMap<>();

    public SQLiteDataStorage(String url) {
        this.storage = new SQLiteStorage(url);
    }

    public void initialize() throws SQLException, IOException {
        this.getStorage().registerMigration("players");
        this.getStorage().setup();
    }

    private StoredPlayerData registerPlayer(ServerPlayerEntity player) throws InvalidModelException, SQLException {
        StoredPlayerData playerData = new StoredPlayerData(player.getUuid(), player.getEntityName());
        playerData.setId(this.getStorage().save(playerData));
        this.getCache().put(player.getUuid(), playerData);
        return playerData;
    }

    @Override
    public Optional<StoredPlayerData> getPlayerData(UUID uuid) throws Exception {
        try {
            if (this.getCache().containsKey(uuid)) return Optional.of(this.getCache().get(uuid));
            return Optional.of(this.getStorage().find(StoredPlayerData.class, "uuid", uuid.toString()));
        } catch (NotFoundException exception) {
            return Optional.empty();
        }
    }

    @Override
    public StoredPlayerData getPlayerData(ServerPlayerEntity player) throws Exception {
        Optional<StoredPlayerData> playerData = this.getPlayerData(player.getUuid());
        if (playerData.isPresent()) return playerData.get();
        return this.registerPlayer(player);
    }

    @Override
    public void savePlayerData(StoredPlayerData playerData) {
        this.getCache().put(playerData.getUuid(), playerData);
        CompletableFuture.runAsync(() -> {
            try {
                this.getStorage().save(playerData);
            } catch (InvalidModelException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public SQLiteStorage getStorage() {
        return storage;
    }

    public HashMap<UUID, StoredPlayerData> getCache() {
        return cache;
    }
}
