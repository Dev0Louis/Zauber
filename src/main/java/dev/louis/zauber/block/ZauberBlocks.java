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
            )
    );
    /*public static final Block RITUAL_STONE = Registry.register(
            Registries.BLOCK, new Identifier(Zauber.MOD_ID, "ritual_stone"),
            new RitualStoneBlock(
                    FabricBlockSettings.create()
                            .mapColor(MapColor.IRON_GRAY)
                            .requiresTool()
                            .strength(3.5F)
                            .sounds(BlockSoundGroup.LODESTONE)
                            .pistonBehavior(PistonBehavior.BLOCK)
            )
    );
    public static final Block ITEM_SACRIFICER = Registry.register(
            Registries.BLOCK, new Identifier(Zauber.MOD_ID, "item_sacrificer"),
            new ItemSacrificerBlock(
                    FabricBlockSettings.create()
                            .mapColor(MapColor.IRON_GRAY)
                            .requiresTool()
                            .strength(3.5F)
                            .sounds(BlockSoundGroup.LODESTONE)
                            .pistonBehavior(PistonBehavior.BLOCK)
            )
    );*/

    public static void init() {
        //Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Zauber.MOD_ID, "ritual_block"), RitualStoneBlockEntity.TYPE);
        //Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Zauber.MOD_ID, "item_sacrificer"), ItemSacrificerBlockEntity.TYPE);
    }
}
