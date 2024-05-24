package dev.louis.zauber.block;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.block.entity.DarknessAccumulatorBlockEntity;
import dev.louis.zauber.block.entity.ItemSacrificerBlockEntity;
import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class ZauberBlocks {

    public static final Block SPELL_TABLE = register(
            "spell_table",
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
    public static final Block RITUAL_STONE = register(
            "ritual_stone",
            new RitualStoneBlock(
                    FabricBlockSettings.create()
                            .mapColor(MapColor.IRON_GRAY)
                            .requiresTool()
                            .strength(3.5F)
                            .sounds(BlockSoundGroup.LODESTONE)
                            .pistonBehavior(PistonBehavior.BLOCK)
            )
    );
    public static final Block ITEM_SACRIFICER = register(
            "item_sacrificer",
            new ItemSacrificerBlock(
                    FabricBlockSettings.create()
                            .mapColor(MapColor.IRON_GRAY)
                            .requiresTool()
                            .strength(3.5F)
                            .sounds(BlockSoundGroup.LODESTONE)
                            .pistonBehavior(PistonBehavior.BLOCK)
            )
    );
    public static final Block MANA_CAULDRON = register(
            "mana_cauldron",
            new ManaCauldron(
                    FabricBlockSettings.create()
                            .mapColor(MapColor.IRON_GRAY)
                            .requiresTool()
                            .strength(3.5F)
                            .sounds(BlockSoundGroup.LODESTONE)
                            .pistonBehavior(PistonBehavior.BLOCK)
            )
    );
    public static final Block EXTINGUISHED_TORCH = register(
            "extinguished_torch",
            new ExtinguishedTorchBlock(
                    AbstractBlock.Settings.create().noCollision().breakInstantly().sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY)
            )
    );
    public static final Block EXTINGUISHED_WALL_TORCH = register(
            "extinguished_wall_torch",
            new ExtinguishedWallTorchBlock(
                    AbstractBlock.Settings.create()
                            .noCollision()
                            .breakInstantly()
                            .sounds(BlockSoundGroup.WOOD)
                            .dropsLike(EXTINGUISHED_TORCH)
                            .pistonBehavior(PistonBehavior.DESTROY)
            )
    );
    public static final Block DARKNESS_ACCUMULATOR = register(
            "darkness_accumulator",
            new DarknessAccumulatorBlock(
                    AbstractBlock.Settings.create()
                            .requiresTool()
                            .ticksRandomly()
                            .strength(3.5F)
                            .sounds(BlockSoundGroup.GLASS)
                            .nonOpaque()
            )
    );
    /*public static final Block DARKSTONE = register(
            "darkstone",
            new ClientOptionalBlock(
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.BLACK)
                            .instrument(Instrument.PLING)
                            .strength(0.3F)
                            .sounds(BlockSoundGroup.GLASS)
                            .solidBlock(Blocks::never),
                    Blocks.TUFF
            )
    );*/
    public static final Block TRAPPING_BED = register("trapping_bed", createTrappingBedBlock(DyeColor.BLACK));

    private static Block createTrappingBedBlock(DyeColor color) {
        return new TrappingBedBlock(
                color,
                AbstractBlock.Settings.create()
                        .mapColor(state -> state.get(BedBlock.PART) == BedPart.FOOT ? color.getMapColor() : MapColor.WHITE_GRAY)
                        .sounds(BlockSoundGroup.WOOD)
                        .strength(0.2F)
                        .nonOpaque()
                        .burnable()
                        .pistonBehavior(PistonBehavior.DESTROY)
        );
    }

    public static Block register(String id, Block block) {
        return Registry.register(
                Registries.BLOCK,
                Identifier.of(Zauber.MOD_ID, id),
                block
        );
    }

    public static void init() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Zauber.MOD_ID, "ritual_block"), RitualStoneBlockEntity.TYPE);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Zauber.MOD_ID, "item_sacrificer"), ItemSacrificerBlockEntity.TYPE);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Zauber.MOD_ID, "darkness_accumulator"), DarknessAccumulatorBlockEntity.TYPE);
        //Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Zauber.MOD_ID, "mana_storage"), ManaStorageBlockEntity.TYPE);
    }
}
