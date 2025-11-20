package com.landmaster.divisionsigil.mixin;

import com.landmaster.divisionsigil.Config;
import com.landmaster.divisionsigil.DivisionSigil;
import com.landmaster.divisionsigil.item.BuildersWandItem;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    private ClientLevel level;

    @Shadow
    private static void renderShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha) {}

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "renderHitOutline", shift = At.Shift.AFTER))
    public void injectRenderLevel(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera,
                                  GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix,
                                  CallbackInfo info, @Local VertexConsumer vertexConsumer, @Local HitResult hitResult, @Local PoseStack poseStack) {
        if (camera.getEntity() instanceof Player player && player.getWeaponItem().is(DivisionSigil.BUILDERS_WAND)) {
            for (var pos: BuildersWandItem.computeBlockPlacements(player, (BlockHitResult) hitResult, Config.BUILDERS_WAND_MAX_BLOCKS.getAsInt())) {
                renderShape(poseStack, vertexConsumer, Shapes.block(),
                        pos.getX() - camera.getPosition().x,
                        pos.getY() - camera.getPosition().y,
                        pos.getZ() - camera.getPosition().z,
                        1, 1, 1, 1);
            }
        }
    }
}
