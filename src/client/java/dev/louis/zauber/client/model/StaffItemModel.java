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
		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(4, 0).cuboid(3.5F, -32.0F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
				.uv(8, 0).cuboid(-0.5F, -33.0F, -0.5F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(8, 2).cuboid(-0.5F, -28.0F, -0.5F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -27.0F, -0.5F, 1.0F, 25.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		root.render(matrices, vertices, light, overlay, color);
	}
}