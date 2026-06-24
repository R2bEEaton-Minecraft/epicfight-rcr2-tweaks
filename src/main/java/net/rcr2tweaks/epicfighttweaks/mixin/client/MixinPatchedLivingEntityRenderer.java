package net.rcr2tweaks.epicfighttweaks.mixin.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.ModList;
import net.rcr2tweaks.epicfighttweaks.compat.MermodTailPatchedLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;

@Mixin(value = PatchedLivingEntityRenderer.class, remap = false)
public class MixinPatchedLivingEntityRenderer {

    // EpicFight's RenderOriginalModelLayer uses "Root" as the joint name, but the humanoid
    // armature has no "Root" joint — causing a silent NPE that prevents any unknown vanilla
    // layer (including Mermod's TailRenderLayer) from rendering. Override the entry here
    // with our layer that correctly uses "Torso".
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "initLayerLast", at = @At("RETURN"), require = 0)
    private void rcr2_fixMermodTailLayer(
            EntityRendererProvider.Context ctx, EntityType<?> entityType,
            CallbackInfoReturnable<PatchedLivingEntityRenderer> cir) {

        if (!ModList.get().isLoaded("mermod")) return;
        try {
            Class<?> tailLayerClass = Class.forName("io.github.thatpreston.mermod.client.render.TailRenderLayer");
            ((PatchedLivingEntityRenderer) (Object) this).addPatchedLayerAlways(tailLayerClass, new MermodTailPatchedLayer());
        } catch (ClassNotFoundException ignored) {}
    }
}
