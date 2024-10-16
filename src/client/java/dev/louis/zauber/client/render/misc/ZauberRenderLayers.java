package dev.louis.zauber.client.render.misc;

import dev.louis.zauber.client.render.item.StaffItemRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import static net.minecraft.client.render.RenderPhase.*;

public class ZauberRenderLayers {
    private static final RenderLayer.MultiPhase DEBUG_TRIANGLES = RenderLayer.of(
            "debug_triangles",
            VertexFormats.POSITION_TEXTURE,
            VertexFormat.DrawMode.TRIANGLES,
            786432,
            false,
            true,
            RenderLayer.MultiPhaseParameters.builder().program(POSITION_TEXTURE_PROGRAM).texture(new Texture(StaffItemRenderer.ENTITY_HOLDING_TEXTURE, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).build(false)
    );

    public static RenderLayer getBrrrrrrrr() {
        return DEBUG_TRIANGLES;
    }
}
