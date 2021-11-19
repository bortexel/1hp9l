package ru.bortexel.hardcore.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bortexel.hardcore.storage.PlayerDataManager;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onDeath(DamageSource source, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof net.minecraft.server.network.ServerPlayerEntity player) {
            PlayerDataManager manager = PlayerDataManager.getInstance();
            manager.consumeLive(player);
        }
    }
}
