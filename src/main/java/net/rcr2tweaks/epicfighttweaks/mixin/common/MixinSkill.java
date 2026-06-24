package net.rcr2tweaks.epicfighttweaks.mixin.common;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = Skill.class, remap = false)
public class MixinSkill {
    @Inject(method = "isExecutableState", at = @At("RETURN"), cancellable = true)
    private void rcr2_blockInvalidStates(PlayerPatch<?> executor, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        Player player = executor.getOriginal();
        if (player.isSwimming()) { cir.setReturnValue(false); return; }
        if (player.getAbilities().flying) { cir.setReturnValue(false); }
    }
}
