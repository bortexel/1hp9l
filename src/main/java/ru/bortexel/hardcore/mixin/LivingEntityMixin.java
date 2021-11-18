package ru.bortexel.hardcore.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.bortexel.hardcore.storage.PlayerDataManager;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "tryUseTotem", at = @At("TAIL"))
    public void tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ServerPlayerEntity player) PlayerDataManager.getInstance().consumeLive(player);
    }
}
