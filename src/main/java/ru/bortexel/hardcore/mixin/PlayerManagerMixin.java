package ru.bortexel.hardcore.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.bortexel.hardcore.BortexelHardcore;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("HEAD"), cancellable = true)
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        BortexelHardcore bortexel = BortexelHardcore.getInstance();
        if (!bortexel.getManager().canPlay(player)) {
            connection.disconnect(new LiteralText("У Вас закончились жизни"));
            ci.cancel();
        }
    }

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
