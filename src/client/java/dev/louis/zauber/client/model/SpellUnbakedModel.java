package dev.louis.zauber.client.model;

import dev.louis.zauber.spell.type.SpellType;

import dev.louis.zauber.Zauber;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpellUnbakedModel implements UnbakedModel {

    public SpellUnbakedModel() {
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        var list = Zauber.Spells.ZAUBER_SPELLS.stream().map(SpellType::getId).map(identifier -> identifier.withPrefixedPath("item/").withSuffixedPath("_spell_book")).collect(Collectors.toList());
        list.add(Identifier.ofVanilla("item/generated"));
        return list;
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {

    }

    @Nullable
    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings modelBakeSettings) {
        Map<String, BakedModel> map = new HashMap<>();

        Zauber.Spells.ZAUBER_SPELLS.forEach(spellType -> {
            var id = spellType.getId();
            map.put(id.toString(), baker.bake(id.withPrefixedPath("item/").withSuffixedPath("_spell_book"), modelBakeSettings));
        });
        var baked = baker.bake(Identifier.ofVanilla("item/generated"), modelBakeSettings);
        return new SpellBookBakedModel(map, baked.getTransformation());
    }
}
