package dev.louis.zauber.client.render.misc;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.client.telekinesis.TelekinesisPad;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;

import java.util.OptionalDouble;

import static net.minecraft.client.render.RenderPhase.*;

public class ZauberRenderLayers {

    public static final RenderPhase.ColorLogic TRANSLUCENT_ON = new RenderPhase.ColorLogic("or_reverse", () -> {
        RenderSystem.disableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR);
        //RenderSystem.logicOp(GlStateManager.LogicOp.A/);
    }, RenderSystem::disableColorLogicOp);

    public static final RenderPhase.ColorLogic TRANSLUCENT_OFF = new RenderPhase.ColorLogic("or_reverse", () -> {
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.NOOP);
        //RenderSystem.logicOp(GlStateManager.LogicOp.A/);
    }, RenderSystem::disableColorLogicOp);

    private static final RenderLayer TELEKINESIS_PAD = RenderLayer.of(
            "telekinesis_pad",
            VertexFormats.POSITION_TEXTURE,
            VertexFormat.DrawMode.QUADS,
            786432,
            RenderLayer.MultiPhaseParameters.builder().program(POSITION_TEXTURE_PROGRAM).texture(new Texture(TelekinesisPad.TEXTURE, TriState.FALSE, false)).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).build(false)
    );




    public static final RenderLayer.MultiPhase LINES = RenderLayer.of(
            "lines",
            VertexFormats.LINES,
            VertexFormat.DrawMode.LINES,
            1536,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(LINES_PROGRAM)
                    .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(5)))
                    .layering(VIEW_OFFSET_Z_LAYERING)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .target(ITEM_ENTITY_TARGET)
                    .writeMaskState(ALL_MASK)
                    .cull(DISABLE_CULLING)
                    .build(false)
    );

    public static RenderLayer getTelekinesisPad() {
        return TELEKINESIS_PAD;
    }
    public static RenderLayer getBrrrrrrrr(Entity entity, float tickDelta) {
        return RenderLayer.of(
                "telekinesis_overlay",
                VertexFormats.POSITION_TEXTURE,
                VertexFormat.DrawMode.QUADS,
                1536,
                false,
                true,
                RenderLayer.MultiPhaseParameters.builder()
                        .program(TRANSLUCENT_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(Identifier.of(Zauber.MOD_ID, "textures/test.png"), TriState.TRUE, false))
                        .writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING)
                        .depthTest(EQUAL_DEPTH_TEST)
                        .transparency(GLINT_TRANSPARENCY)
                        .texturing(new OffsetTexturing((entity.age + tickDelta) * 0.002f, (entity.age + tickDelta) * 0.002f))
                        .target(ITEM_ENTITY_TARGET)
                        .build(false)
        );
    }

}
