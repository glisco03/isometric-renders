package com.glisco.isometricrenders.mixin;

import com.glisco.isometricrenders.property.GlobalProperties;
import com.glisco.isometricrenders.screen.RenderScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderTickCounter.Dynamic.class)
public class RenderTickCounterMixin {

    @Shadow private long prevTimeMillis;

    @ModifyVariable(method = "beginRenderTick(JZ)I", index = 1, argsOnly = true, at = @At("HEAD"))
    public long test(long value) {
        return MinecraftClient.getInstance().currentScreen instanceof RenderScreen rs && rs.remainingAnimationFrames > 0
                ? this.prevTimeMillis + (1000L / GlobalProperties.exportFramerate) : value;
    }

}
