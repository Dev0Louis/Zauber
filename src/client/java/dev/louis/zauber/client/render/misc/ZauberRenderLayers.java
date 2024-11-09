package dev.louis.zauber.client.render.misc;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.louis.zauber.client.render.item.StaffItemRenderer;
import dev.louis.zauber.client.telekinesis.TelekinesisPad;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;

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
            RenderLayer.MultiPhaseParameters.builder().program(POSITION_TEXTURE_PROGRAM).texture(new Texture(TelekinesisPad.TEXTURE, false, false)).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).build(false)
    );

    private static final RenderLayer.MultiPhase DEBUG_TRIANGLES = RenderLayer.of(
            "debug_triangles",
            VertexFormats.POSITION_TEXTURE,
            VertexFormat.DrawMode.TRIANGLES,
            786432,
            false,
            true,
            RenderLayer.MultiPhaseParameters.builder().program(POSITION_TEXTURE_PROGRAM).texture(new Texture(StaffItemRenderer.ENTITY_HOLDING_TEXTURE, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).build(false)
    );

    private static final RenderPhase.ShaderProgram POS_TEX_COLOR_SHADER_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getPositionTexColorProgram);

    private static final RenderLayer.MultiPhase STAFF_SPHERE = RenderLayer.of(
            "spell_sphere",
            VertexFormats.POSITION_TEXTURE_COLOR,
            VertexFormat.DrawMode.TRIANGLES,
            786432,
            false,
            true,
            RenderLayer.MultiPhaseParameters.builder().program(POS_TEX_COLOR_SHADER_PROGRAM).texture(new Texture(StaffItemRenderer.STAFF_SPHERE, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).build(false)
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
                        .texture(new RenderPhase.Texture(StaffItemRenderer.ENTITY_HOLDING_TEXTURE, true, false))
                        .writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING)
                        .depthTest(EQUAL_DEPTH_TEST)
                        .transparency(GLINT_TRANSPARENCY)
                        .texturing(new OffsetTexturing((entity.age + tickDelta) * 0.002f, (entity.age + tickDelta) * 0.002f))
                        .target(ITEM_ENTITY_TARGET)
                        .build(false)
        );
    }

    public static RenderLayer getTelekinesisPad() {
        return TELEKINESIS_PAD;
    }

    public static RenderLayer.MultiPhase getStaffSphere() {
        return STAFF_SPHERE;
    }
}
