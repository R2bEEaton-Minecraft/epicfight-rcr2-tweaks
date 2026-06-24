package net.rcr2tweaks.epicfighttweaks.mixin.client;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.rcr2tweaks.epicfighttweaks.compat.MermodCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

@Mixin(value = PPlayerRenderer.class, remap = false)
public class MixinPPlayerRenderer {

    // When the player has a Mermod tail, hide EpicFight's own leg mesh parts.
    // Mermod's vanilla-model mixin (onSetModelProperties) targets PlayerModel.leftLeg etc.,
    // but EpicFight renders its own HumanoidMesh — those flags have no effect.
    @Inject(
        method = "prepareModel(Lyesman/epicfight/client/mesh/HumanoidMesh;" +
                 "Lnet/minecraft/client/player/AbstractClientPlayer;" +
                 "Lyesman/epicfight/client/world/capabilites/entitypatch/player/AbstractClientPlayerPatch;" +
                 "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;)V",
        at = @At("HEAD"), require = 0
    )
    private void rcr2_hideLegMeshForMermod(
            HumanoidMesh mesh,
            AbstractClientPlayer entity,
            AbstractClientPlayerPatch<AbstractClientPlayer> patch,
            PlayerRenderer renderer,
            CallbackInfo ci) {

        if (!MermodCompat.shouldHideLeg(entity)) return;
        mesh.leftLeg.setHidden(true);
        mesh.rightLeg.setHidden(true);
        mesh.leftPants.setHidden(true);
        mesh.rightPants.setHidden(true);
    }
}
