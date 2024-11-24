package com.glisco.isometricrenders.mixin.access;

import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.channels.WritableByteChannel;

@Mixin(NativeImage.class)
public interface NativeImageInvoker {

    @Invoker("write")
    boolean isometric$write(WritableByteChannel channel);

}
