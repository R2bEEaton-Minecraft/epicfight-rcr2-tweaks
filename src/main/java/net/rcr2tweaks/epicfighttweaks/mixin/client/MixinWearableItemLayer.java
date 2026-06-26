package net.rcr2tweaks.epicfighttweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.rcr2tweaks.epicfighttweaks.compat.MermodCompat;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.layer.WearableItemLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = WearableItemLayer.class, remap = false)
public class MixinWearableItemLayer {
    // Target the concrete override (HumanoidArmorLayer 3rd param), not the bridge (RenderLayer 3rd param).
    // Without a full descriptor, Mixin finds two renderLayer overloads in 20.14.17 and throws
    // MixinApplyError when WearableItemLayer is first classloaded (i.e. on world entry).
    @Inject(
        method = "renderLayer(Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I[Lyesman/epicfight/api/utils/math/OpenMatrix4f;FFFF)V",
        at = @At("HEAD"), cancellable = true, require = 0
    )
    private <E extends LivingEntity, T extends LivingEntityPatch<E>> void rcr2_suppressArmorInFirstPerson(
            T entitypatch, E entityliving, HumanoidArmorLayer<E, ?, ?> vanillaLayer,
            PoseStack poseStack, MultiBufferSource buf, int packedLight,
            OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks,
            CallbackInfo ci) {
        if (entitypatch.isFirstPerson()) {
            ci.cancel();
        }
    }

    @Redirect(
        method = "renderLayer(Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I[Lyesman/epicfight/api/utils/math/OpenMatrix4f;FFFF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"),
        require = 0
    )
    private ItemStack rcr2_hideEpicFightLegArmorForMermodTail(LivingEntity entity, EquipmentSlot slot) {
        if ((slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET)
                && entity instanceof Player player
                && MermodCompat.shouldHideLeg(player)) {
            return ItemStack.EMPTY;
        }
        return entity.getItemBySlot(slot);
    }
}
