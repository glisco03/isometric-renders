package com.glisco.isometricrenders.property;

import com.glisco.isometricrenders.render.Renderable;
import com.glisco.isometricrenders.screen.IsometricUI;
import com.glisco.isometricrenders.screen.RenderScreen;
import com.glisco.isometricrenders.util.ClientRenderCallback;
import com.glisco.isometricrenders.util.Translate;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4fStack;

public class DefaultPropertyBundle implements PropertyBundle {

    private static final DefaultPropertyBundle INSTANCE = new DefaultPropertyBundle();

    public final IntProperty scale = IntProperty.of(100, 0, 500);
    public final IntProperty rotation = IntProperty.of(135, 0, 360).withRollover();
    public final IntProperty slant = IntProperty.of(30, -90, 90);
    public final IntProperty lightAngle = IntProperty.of(-45, -45, 45);

    public final IntProperty xOffset = IntProperty.of(0, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);
    public final IntProperty yOffset = IntProperty.of(0, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);

    public final IntProperty rotationSpeed = IntProperty.of(0, 0, 100);
    protected float rotationOffset = 0;
    protected boolean rotationOffsetUpdated = false;

    public DefaultPropertyBundle() {
        ClientRenderCallback.EVENT.register(client -> {
            this.rotationOffsetUpdated = false;
        });
    }

    @Override
    public void buildGuiControls(Renderable<?> renderable, FlowLayout container) {
        IsometricUI.sectionHeader(container, "transform_options", false);

        IsometricUI.intControl(container, scale, "scale", 10);
        IsometricUI.intControl(container, rotation, "rotation", 45);
        IsometricUI.intControl(container, slant, "slant", 30);
        IsometricUI.intControl(container, lightAngle, "light_angle", 15);
        IsometricUI.intControl(container, rotationSpeed, "rotation_speed", 5);

        // -------

        IsometricUI.sectionHeader(container, "presets", true);

        try (var builder = IsometricUI.row(container)) {
            builder.row.child(Components.button(Translate.gui("dimetric"), (ButtonComponent button) -> {
                this.rotation.setToDefault();
                this.slant.set(30);
            }).horizontalSizing(Sizing.fixed(60)).margins(Insets.right(5)));

            builder.row.child(Components.button(Translate.gui("isometric"), (ButtonComponent button) -> {
                this.rotation.setToDefault();
                this.slant.set(36);
            }).horizontalSizing(Sizing.fixed(60)));
        }
    }

    @Override
    public void applyToViewMatrix(Matrix4fStack modelViewStack) {
        final float scale = this.scale.get() / 100f;
        modelViewStack.scale(scale, scale, scale);

        modelViewStack.translate(this.xOffset.get() / 26000f, this.yOffset.get() / -26000f, 0);

        modelViewStack.rotate(RotationAxis.POSITIVE_X.rotationDegrees(this.slant.get()));
        modelViewStack.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(this.rotation.get()));

        this.updateAndApplyRotationOffset(modelViewStack);
    }

    public float getRotationOffset() {
        return this.rotationOffset;
    }

    public void setRotationOffset(int offset) {
        this.rotationOffset = offset;
        this.rotationOffsetUpdated = true;
    }

    protected void updateAndApplyRotationOffset(Matrix4fStack modelViewStack) {
        if (rotationSpeed.get() != 0) {
            if (!this.rotationOffsetUpdated) {
                rotationOffset += MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration() * rotationSpeed.get() * .1f;
                this.rotationOffsetUpdated = true;
            }
            modelViewStack.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(rotationOffset));
        } else {
            rotationOffset = 0;
        }
    }

    public static DefaultPropertyBundle get() {
        return INSTANCE;
    }
}
