package com.glisco.isometricrenders.util;

import com.glisco.isometricrenders.render.Renderable;
import com.glisco.isometricrenders.render.RenderableDispatcher;

import static com.glisco.isometricrenders.property.GlobalProperties.exportResolution;

public class InstantRenderer {
    public static void render(Renderable<?> renderable){
        ImageIO.save(
                RenderableDispatcher.drawIntoImage(renderable, 0, exportResolution),
                renderable.exportPath()
        );
    }
}
