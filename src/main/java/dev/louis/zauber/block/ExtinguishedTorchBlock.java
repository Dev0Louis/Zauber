package dev.louis.zauber.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractTorchBlock;

public class ExtinguishedTorchBlock extends AbstractTorchBlock {
    public static final MapCodec<ExtinguishedTorchBlock> CODEC = createCodec(ExtinguishedTorchBlock::new);

    protected ExtinguishedTorchBlock(Settings settings) {
        super(settings);
    }

    public MapCodec<? extends ExtinguishedTorchBlock> getCodec() {
        return CODEC;
    }
}
