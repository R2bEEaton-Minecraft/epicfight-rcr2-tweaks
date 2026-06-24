package net.rcr2tweaks.epicfighttweaks.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.layer.PatchedLayer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

/**
 * Renders the Mermod tail layer using the "Torso" joint as an attachment point.
 *
 * EpicFight's default RenderOriginalModelLayer hardcodes "Root" as the joint name,
 * but HumanoidArmature has no "Root" joint — causing a silent NPE. This layer uses
 * "Torso" instead so the tail follows the player's hips correctly.
 */
public class MermodTailPatchedLayer extends PatchedLayer<
        AbstractClientPlayer,
        AbstractClientPlayerPatch<AbstractClientPlayer>,
        PlayerModel<AbstractClientPlayer>,
        RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> {

    @Override
    protected void renderLayer(
            AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch,
            AbstractClientPlayer entity,
            RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> layer,
            PoseStack poseStack,
            MultiBufferSource buf,
            int packedLight,
            OpenMatrix4f[] poses,
            float bob, float yRot, float xRot, float partialTicks) {

        if (layer == null || poses == null) return;

        Armature armature = entitypatch.getArmature();
        Joint torsoJoint = armature.searchJointByName("Torso");
        if (torsoJoint == null) return;

        int id = torsoJoint.getId();
        if (id < 0 || id >= poses.length || poses[id] == null) return;

        float walkPos   = entity.walkAnimation.position(partialTicks);
        float walkSpeed = entity.walkAnimation.speed(partialTicks);
        float ageInTicks = bob; // PatchedLayer passes ageInTicks as the "bob" slot

        poseStack.pushPose();
        MathUtils.mulStack(poseStack, poses[id]);
        // Torso bone matrix carries rotation only; add a manual lift to hip height.
        // Adjust this value if the tail appears too high or too low in game.
        poseStack.translate(0.0, 0.75, 0.0);
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        layer.render(poseStack, buf, packedLight, entity,
                walkPos, walkSpeed, partialTicks, ageInTicks, yRot, xRot);
        poseStack.popPose();
    }
}
