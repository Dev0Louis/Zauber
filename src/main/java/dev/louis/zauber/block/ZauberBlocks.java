package dev.louis.zauber.block;

import dev.louis.zauber.Zauber;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ZauberBlocks {

    public static final Block SPELL_TABLE = Registry.register(
            Registries.BLOCK, new Identifier(Zauber.MOD_ID, "spell_table"),

            new SpellTableBlock(
                    FabricBlockSettings.create()
                            .mapColor(MapColor.BLACK)
                            .instrument(Instrument.BASEDRUM)
                            .requiresTool()
                            .luminance(SpellTableBlock::getLightLevel)
                            .strength(5.0F, 1200.0F)
                            .sounds(BlockSoundGroup.DEEPSLATE_BRICKS)
                            .ticksRandomly()
                            .luminance(SpellTableBlock::getLightLevel)
            )
    );
    public static void init() {
    }
}
