package xyz.aikoyori.bigfan.mixin;

import amymialee.whipdashing.entities.HookEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.aikoyori.bigfan.entities.FanEntity;
import xyz.aikoyori.bigfan.entities.FanWindEntity;

@Mixin(HookEntity.class)
public class WhipdashingHook {
    @Inject(method = "isHeavy",at=@At("HEAD"),cancellable = true)
    void mewhenthehookwhipdash(Entity entity, LivingEntity owner, CallbackInfoReturnable<Boolean> cir){
        if ((entity instanceof FanEntity fan && fan.isLocked()) || entity instanceof FanWindEntity)
            cir.setReturnValue(true);
    }
}
