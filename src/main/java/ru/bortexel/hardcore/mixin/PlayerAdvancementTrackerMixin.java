package ru.bortexel.hardcore.mixin;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.bortexel.hardcore.storage.PlayerDataManager;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {
    @Shadow public abstract AdvancementProgress getProgress(Advancement advancement);

    @Shadow private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At("TAIL"))
    public void grantCriterion(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> info) {
        if (this.isInsignificantAdvancement(advancement)) return;
        if (!this.getProgress(advancement).isDone()) return;
        if (advancement.getDisplay() == null) return;

        PlayerDataManager playerDataManager = PlayerDataManager.getInstance();
        playerDataManager.grantPoints(owner, (this.getDoneAdvancementCount() / 50 + 1) * 2);
    }

    public int getDoneAdvancementCount() {
        MinecraftServer server = owner.getServer();
        if (server == null) return 0;
        ServerAdvancementLoader advancementLoader = server.getAdvancementLoader();

        int count = 0;
        for (Advancement advancement : advancementLoader.getAdvancements()) {
            if (this.getProgress(advancement).isDone()) count++;
        }

        return count;
    }

    public boolean isInsignificantAdvancement(Advancement advancement) {
        Identifier id = advancement.getId();
        String key = id.getPath();
        return key.contains("recipes/")
                || key.contains("/root")
                || key.contains("technical/");
    }
}
