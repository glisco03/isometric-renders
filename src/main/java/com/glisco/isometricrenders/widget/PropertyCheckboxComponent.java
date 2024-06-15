package com.glisco.isometricrenders.widget;

import com.glisco.isometricrenders.property.Property;
import io.wispforest.owo.ui.component.CheckboxComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

public class PropertyCheckboxComponent extends CheckboxComponent {

    private final Property<Boolean> property;

    public PropertyCheckboxComponent(Text message, Property<Boolean> property) {
        super(message);

        this.property = property;
        this.checked(this.property.get());
    }

    @Override
    public void onPress() {
        super.onPress();
        property.set(this.isChecked());
    }
}
