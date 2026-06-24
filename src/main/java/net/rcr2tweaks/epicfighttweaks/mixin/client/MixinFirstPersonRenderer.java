package net.rcr2tweaks.epicfighttweaks.mixin.client;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.FirstPersonRenderer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

// The production EpicFight jar uses SRG names (m_110458_) for Minecraft method calls like
// RenderType.entityCutoutNoCull, which differ from the Mojmap names we compile against.
// Targeting HumanoidMesh.draw() instead avoids the remapping issue entirely since it is
// an EpicFight method with consistent naming across dev and production jars.
@SuppressWarnings("target")
@Mixin(value = FirstPersonRenderer.class, remap = false)
public class MixinFirstPersonRenderer {
    private static final String DRAW =
        "Lyesman/epicfight/client/mesh/HumanoidMesh;draw(" +
        "Lcom/mojang/blaze3d/vertex/PoseStack;" +
        "Lnet/minecraft/client/renderer/MultiBufferSource;" +
        "Lnet/minecraft/client/renderer/RenderType;" +
        "IFFFFILyesman/epicfight/api/model/Armature;" +
        "[Lyesman/epicfight/api/utils/math/OpenMatrix4f;" +
        ")V";

    // Fires just before mesh.draw() in the attack animation branch (povSettings != null)
    @Inject(method = "render", at = @At(value = "INVOKE", target = DRAW, ordinal = 0), require = 0)
    private void rcr2_hideAllMeshInFirstPersonAttack(
            LocalPlayer entity,
            LocalPlayerPatch localPlayerPatch,
            LivingEntityRenderer<LocalPlayer, PlayerModel<LocalPlayer>> renderer,
            MultiBufferSource buffer,
            PoseStack poseStack,
            int packedLight,
            float partialTicks,
            CallbackInfo ci) {
        HumanoidMesh mesh = ((FirstPersonRenderer)(Object)this).getMeshProvider(localPlayerPatch).get();
        mesh.torso.setHidden(true);
        mesh.jacket.setHidden(true);
        mesh.leftArm.setHidden(true);
        mesh.rightArm.setHidden(true);
        mesh.leftSleeve.setHidden(true);
        mesh.rightSleeve.setHidden(true);
        mesh.leftLeg.setHidden(true);
        mesh.rightLeg.setHidden(true);
        mesh.leftPants.setHidden(true);
        mesh.rightPants.setHidden(true);
    }

    // Fires just before mesh.draw() in the idle first-person branch (povSettings == null)
    @Inject(method = "render", at = @At(value = "INVOKE", target = DRAW, ordinal = 1), require = 0)
    private void rcr2_hideAllMeshInFirstPersonIdle(
            LocalPlayer entity,
            LocalPlayerPatch localPlayerPatch,
            LivingEntityRenderer<LocalPlayer, PlayerModel<LocalPlayer>> renderer,
            MultiBufferSource buffer,
            PoseStack poseStack,
            int packedLight,
            float partialTicks,
            CallbackInfo ci) {
        HumanoidMesh mesh = ((FirstPersonRenderer)(Object)this).getMeshProvider(localPlayerPatch).get();
        mesh.leftArm.setHidden(true);
        mesh.rightArm.setHidden(true);
        mesh.leftSleeve.setHidden(true);
        mesh.rightSleeve.setHidden(true);
    }
}
