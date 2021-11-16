package ru.bortexel.hardcore.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "createPlayer", at = @At("TAIL"))
    public void createPlayer(GameProfile profile, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        this.updateMaxHealth(cir.getReturnValue());
    }

    @Inject(method = "respawnPlayer", at = @At("TAIL"))
    public void respawnPlayer(CallbackInfoReturnable<ServerPlayerEntity> cir) {
        this.updateMaxHealth(cir.getReturnValue());
    }

    private void updateMaxHealth(ServerPlayerEntity player) {
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attribute == null) return;
        attribute.setBaseValue(1);
    }
}
