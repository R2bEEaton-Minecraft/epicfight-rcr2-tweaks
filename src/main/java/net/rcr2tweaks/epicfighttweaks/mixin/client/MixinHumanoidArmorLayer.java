package net.rcr2tweaks.epicfighttweaks.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.rcr2tweaks.epicfighttweaks.compat.MermodCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class MixinHumanoidArmorLayer {

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private <T extends LivingEntity> void rcr2_hideLegArmorForMermodTail(
            PoseStack poseStack, MultiBufferSource buffer, T entity,
            EquipmentSlot slot, int packedLight, HumanoidModel<?> model,
            CallbackInfo ci) {
        if (slot != EquipmentSlot.LEGS && slot != EquipmentSlot.FEET) return;
        if (entity instanceof Player player && MermodCompat.shouldHideLeg(player)) {
            ci.cancel();
        }
    }
}
