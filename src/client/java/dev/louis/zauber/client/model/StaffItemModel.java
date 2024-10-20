// Made with Blockbench 4.9.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package dev.louis.zauber.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class StaffItemModel extends Model {
	public static final Identifier TEXTURE = Identifier.of("zauber", "textures/item/staff_in_hand.png");
	private final ModelPart root;

	public StaffItemModel(ModelPart root) {
		super(RenderLayer::getEntitySolid);
		this.root = root;
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -9.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F))
				.uv(7, 7).cuboid(-1.0F, -18.0F, -1.0F, 2.0F, 8.0F, 1.0F, new Dilation(0.0F))
				.uv(13, 3).cuboid(-1.0F, -16.0F, 0.0F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 13).cuboid(0.0F, -19.0F, 0.0F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F))
				.uv(8, 3).cuboid(0.0F, -21.0F, -1.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
				.uv(12, 16).cuboid(-1.0F, -21.0F, 0.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
				.uv(8, 0).cuboid(-1.0F, -22.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
				.uv(16, 9).cuboid(-1.0F, -21.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(15, 14).cuboid(0.0F, -10.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
				.uv(6, 0).cuboid(-1.0F, -10.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(4, 15).cuboid(-1.0F, -27.0F, -1.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
				.uv(13, 10).cuboid(0.0F, -27.0F, -1.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
				.uv(17, 6).cuboid(-1.0F, -24.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(17, 3).cuboid(0.0F, -25.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(8, 16).cuboid(-1.0F, -30.0F, 0.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 8).cuboid(0.0F, -30.0F, -1.0F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F))
				.uv(16, 0).cuboid(-1.0F, -30.0F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		root.render(matrices, vertices, light, overlay, color);
	}
}