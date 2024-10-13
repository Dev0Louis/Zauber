package dev.louis.zauber.client.render;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.client.extension.BlockRenderManagerExtension;
import dev.louis.zauber.client.model.StaffItemModel;
import dev.louis.zauber.extension.PlayerEntityExtension;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

public class StaffItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, SimpleSynchronousResourceReloadListener {

    public static final Identifier ID = Identifier.of(Zauber.MOD_ID, "staff_renderer");
    public static final ModelIdentifier STAFF_IN_HAND = ModelIdentifier.ofInventoryVariant(Identifier.of(Zauber.MOD_ID, "staff_in_hand"));
    public static final ModelIdentifier STAFF = ModelIdentifier.ofInventoryVariant(Identifier.of(Zauber.MOD_ID, "staff"));
    private final EntityModelLayer staffModelLayer;
    private ItemRenderer itemRenderer;
    private StaffItemModel modelStaff;
    private BakedModel inventoryModel;

    private static BlockState state = Blocks.BEDROCK.getDefaultState();

    public StaffItemRenderer(EntityModelLayer staffModelLayer) {
        this.staffModelLayer = staffModelLayer;
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        boolean renderAsItem = mode == ModelTransformationMode.GUI
                || mode == ModelTransformationMode.GROUND
                || mode == ModelTransformationMode.FIXED;
        if (renderAsItem) {
            itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, this.inventoryModel);
        } else {
            if (UnsafeItemRendererContext.IN_STAFF_RENDERING.get() == null) {
                var unsafeEntity = UnsafeItemRendererContext.RENDERER_ENTITY.get();
                if (unsafeEntity instanceof PlayerEntity player) {
                    var extension = (PlayerEntityExtension) player;
                    extension.zauber$getTelekinesisAffected().ifPresent(entity -> {
                        UnsafeItemRendererContext.IN_STAFF_RENDERING.set(Unit.INSTANCE);
                        renderEntity(matrices, vertexConsumers, light, entity);
                        UnsafeItemRendererContext.IN_STAFF_RENDERING.remove();
                    });
                }
            }

            /*extension.getStaffTargetedEntity().ifPresentOrElse(
                    entity  -> renderEntity(matrices, vertexConsumers, light, entity),
                    ()      -> extension.getStaffTargetedBlock().ifPresent(cachedBlockPosition -> renerBlock(matrices, vertexConsumers, light, overlay, cachedBlockPosition))
            );*/

            matrices.push();
            matrices.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(
                    vertexConsumers, this.modelStaff.getLayer(StaffItemModel.TEXTURE), false, stack.hasGlint()
            );


            this.modelStaff.render(matrices, vertexConsumer, light, overlay);
            matrices.pop();
        }
    }

    private static void renerBlock(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CachedBlockPosition cBlockPos) {
        var world = MinecraftClient.getInstance().world;
        var pos = cBlockPos.getBlockPos();
        var state = cBlockPos.getBlockState();
        var hardness = state.getHardness(world, pos);
        var swingSpeed = Math.min(hardness == -1 ? 16 : hardness, 16);

        matrices.push();
        var scale = 0.25f /2;
        matrices.translate(0, scale, 0);
        matrices.scale(scale, scale, scale);
        matrices.translate(-0.5, 1.25f + Math.sin(MinecraftClient.getInstance().world.getTime() / (2 + (3 * swingSpeed))) / 4 , -0.5);
        ((BlockRenderManagerExtension) MinecraftClient.getInstance().getBlockRenderManager()).zauber$renderWorldBlockAsEntity(
                world,
                pos,
                state,
                matrices,
                vertexConsumers,
                light,
                overlay
        );
        matrices.pop();
    }

    private static void renderEntity(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Entity entity) {
        matrices.push();
        var scale = (1 / entity.getHeight()) / 4;
        matrices.scale(scale, scale, scale);
        matrices.translate(0, entity.getHeight(), 0);
        float prevBodyYaw = 0;
        float prevPrevBodyYaw = 0;
        float prevPrevHeadYaw = 0;
        float prevHeadYaw = 0;
        final boolean bl = entity instanceof LivingEntity;
        if (bl) {
            var livingEntity = (LivingEntity) entity;
            prevBodyYaw = livingEntity.bodyYaw;
            prevPrevBodyYaw = livingEntity.prevBodyYaw;
            prevPrevHeadYaw = livingEntity.prevHeadYaw;
            prevHeadYaw = livingEntity.headYaw;

            livingEntity.bodyYaw = (float) (Math.sin(MinecraftClient.getInstance().world.getTime() / 15f) * 10f) + -20;
            livingEntity.prevBodyYaw = (float) (Math.sin(MinecraftClient.getInstance().world.getTime() / 15f) * 10f) + -20;
            livingEntity.prevHeadYaw = (float) (Math.sin(MinecraftClient.getInstance().world.getTime() / 15f) * 10f) + -20;
            livingEntity.headYaw = (float) (Math.sin(MinecraftClient.getInstance().world.getTime() / 15f) * 10f) + -20;

        }
        MinecraftClient.getInstance().getEntityRenderDispatcher().render(
                entity,
                0,
                0,
                0,
                0,
                0,
                matrices,
                vertexConsumers,
                light
        );
        if (bl) {
            var livingEntity = (LivingEntity) entity;
            livingEntity.bodyYaw = prevBodyYaw;
            livingEntity.prevBodyYaw = prevPrevBodyYaw;
            livingEntity.prevHeadYaw = prevPrevHeadYaw;
            livingEntity.headYaw = prevHeadYaw;
        }

        matrices.pop();
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        MinecraftClient client = MinecraftClient.getInstance();
        this.modelStaff = new StaffItemModel(client.getEntityModelLoader().getModelPart(this.staffModelLayer));
        this.itemRenderer = client.getItemRenderer();
        this.inventoryModel = client.getBakedModelManager().getModel(STAFF);
    }
}
