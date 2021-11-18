package ru.bortexel.hardcore.storage;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;
import java.util.UUID;

public interface PlayerDataProvider {
    Optional<StoredPlayerData> getPlayerData(UUID uuid) throws Exception;
    StoredPlayerData getPlayerData(ServerPlayerEntity player) throws Exception;
    void savePlayerData(StoredPlayerData playerData) throws Exception;
}
