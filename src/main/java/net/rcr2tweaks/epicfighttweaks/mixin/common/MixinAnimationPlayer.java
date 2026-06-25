package net.rcr2tweaks.epicfighttweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.main.EpicFightSharedConstants;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = AnimationPlayer.class, remap = false)
public class MixinAnimationPlayer {
    @Shadow protected float elapsedTime;

    @Inject(method = "tick", at = @At("RETURN"))
    private void rcr2_speedUpAnticipation(LivingEntityPatch<?> entitypatch, CallbackInfo ci) {
        var accessor = ((AnimationPlayer)(Object)this).getAnimation();
        if (accessor == null) return;
        Object raw = accessor.get();
        if (!(raw instanceof AttackAnimation attackAnim)) return;
        AttackAnimation.Phase phase = attackAnim.getPhaseByTime(this.elapsedTime);
        if (phase == null) return;
        // Speed up the pre-contact window (windup/anticipation before the hit).
        // Most EF animations have preDelay == antic, so we can't use that window;
        // instead we advance time throughout the entire pre-contact range.
        if (this.elapsedTime < phase.contact) {
            float bonus = EpicFightSharedConstants.A_TICK * 0.15f;
            this.elapsedTime = Math.min(this.elapsedTime + bonus, phase.contact - EpicFightSharedConstants.A_TICK * 0.05f);
        }
    }
}
