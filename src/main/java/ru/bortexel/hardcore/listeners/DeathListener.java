package ru.bortexel.hardcore.listeners;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import ru.bortexel.hardcore.storage.PlayerDataManager;

public class DeathListener implements ServerPlayerEvents.AllowDeath {
    @Override
    public boolean allowDeath(ServerPlayerEntity player, DamageSource damageSource, float damageAmount) {
        PlayerDataManager manager = PlayerDataManager.getInstance();
        manager.consumeLive(player);
        return false;
    }
}
