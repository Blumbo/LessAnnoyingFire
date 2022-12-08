package net.blumbo.lessannoyingfire.mixin;

import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {

    @ModifyArg(method = "renderFireOverlay", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;vertex(Lnet/minecraft/util/math/Matrix4f;FFF)Lnet/minecraft/client/render/VertexConsumer;"))
    private static float lowerFire(float y) {
        return y - 0.25f;
    }

    @ModifyArg(method = "renderFireOverlay", index = 3, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;color(FFFF)Lnet/minecraft/client/render/VertexConsumer;"))
    private static float reduceOpacity(float opacity) {
        return opacity - 0.1f;
    }

}
