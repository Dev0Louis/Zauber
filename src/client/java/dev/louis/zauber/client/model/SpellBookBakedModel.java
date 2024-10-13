package dev.louis.zauber.client.model;

import dev.louis.zauber.component.item.ZauberDataComponentTypes;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SpellBookBakedModel implements FabricBakedModel, BakedModel {
    private final Map<String, BakedModel> modelMap;
    private final ModelTransformation modelTransformation;

    public SpellBookBakedModel(Map<String, BakedModel> modelMap, ModelTransformation modelTransformation) {
        this.modelMap = modelMap;
        this.modelTransformation = modelTransformation;
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        var component = stack.get(ZauberDataComponentTypes.STORED_SPELL_TYPE);
        if (component != null) {
            var bakedModel = modelMap.get(component.spellType().getIdAsString());
            if (bakedModel != null) {
                bakedModel.emitItemQuads(stack, randomSupplier, context);
            }
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return true;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return null;
    }

    @Override
    public ModelTransformation getTransformation() {
        return modelTransformation;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }
}
