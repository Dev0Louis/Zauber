package dev.louis.zauber.ritual.spell;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.block.entity.ItemSacrificerBlockEntity;
import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.helper.EffectHelper;
import dev.louis.zauber.item.SpellBookItem;
import dev.louis.zauber.item.ZauberItems;
import dev.louis.zauber.ritual.Ritual;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class HailSpellRitual extends Ritual {
    private final BlockPos itemSacrificerPos;
    private final Position connectionPos;

    public HailSpellRitual(World world, RitualStoneBlockEntity ritualStoneBlockEntity, BlockPos itemSacrificerPos) {
        super(world, ritualStoneBlockEntity);
        this.itemSacrificerPos = itemSacrificerPos;
        this.connectionPos = itemSacrificerPos.toCenterPos().add(0, 1, 0);
    }

    @Override
    public void tick() {
        if(age % 5 == 0) {
            world.playSound(null, this.pos, SoundEvents.ENTITY_ARROW_HIT, SoundCategory.PLAYERS, 1, -4);
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void finish() {
        world.getBlockEntity(itemSacrificerPos, ItemSacrificerBlockEntity.TYPE).ifPresent(itemSacrificer -> {
            EffectHelper.playBreakItemEffect(itemSacrificer);
            itemSacrificer.setStoredStack(ItemStack.EMPTY);

            ritualStoneBlockEntity.setStoredStack(SpellBookItem.createSpellBook(Zauber.Spells.HAIL_STORM));
        });
    }

    @Override
    public boolean shouldStop() {
        return age > 40;
    }

    @Override
    public Stream<Position> getConnections() {
        return Stream.of(connectionPos);
    }

    public static Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        var ritualItemStack = ritualStoneBlockEntity.getStoredStack();

        var optionalItemSacrificer = ritualStoneBlockEntity.getItemSacrificers().filter(itemSacrificer -> itemSacrificer.getStoredStack().isOf(ZauberItems.HEART_OF_THE_ICE)).findAny();
        if(!SpellBookItem.getSpellType(ritualItemStack).map(spellType -> spellType.equals(Zauber.Spells.ICE)).orElse(false) || optionalItemSacrificer.isEmpty()) return null;
        return new HailSpellRitual(world, ritualStoneBlockEntity, optionalItemSacrificer.get().getPos());
    }
}
