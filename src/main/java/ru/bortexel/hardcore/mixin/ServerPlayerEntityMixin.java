package ru.bortexel.hardcore.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.bortexel.hardcore.BortexelHardcore;
import ru.bortexel.hardcore.storage.PlayerDataManager;
import ru.bortexel.hardcore.storage.SQLiteDataStorage;
import ru.bortexel.hardcore.storage.StoredPlayerData;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onDeath(DamageSource source, CallbackInfo ci) {
        PlayerDataManager manager = PlayerDataManager.getInstance();
        manager.consumeLive((ServerPlayerEntity) (Object) this);
    }

    @Inject(method = "getPlayerListName", at = @At("RETURN"), cancellable = true)
    public void getPlayerListName(CallbackInfoReturnable<Text> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        MutableText text = (MutableText) cir.getReturnValue();
        SQLiteDataStorage storage = BortexelHardcore.getInstance().getStorage();
        if (text == null) text = new LiteralText(player.getEntityName());

        try {
            StoredPlayerData playerData = storage.getPlayerData(player);
            cir.setReturnValue(text.append(" ").append(this.styleLiveCount(playerData.getLives())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Text styleLiveCount(int lives) {
        LiteralText text = new LiteralText("" + lives);
        if (lives >= 8) return text.styled(style -> style.withColor(TextColor.fromRgb(0x00b52d)));
        if (lives >= 6) return text.styled(style -> style.withColor(TextColor.fromRgb(0x72c834)));
        if (lives >= 4) return text.styled(style -> style.withColor(TextColor.fromRgb(0xe9db3e)));
        if (lives >= 2) return text.styled(style -> style.withColor(TextColor.fromRgb(0xd0682f)));
        if (lives >= 0) return text.styled(style -> style.withColor(TextColor.fromRgb(0xc12c2e)));
        return text;
    }
}
