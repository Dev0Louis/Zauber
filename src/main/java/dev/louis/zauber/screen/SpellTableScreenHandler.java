package dev.louis.zauber.screen;

import com.google.common.collect.Lists;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.recipe.SpellRecipe;
import dev.louis.zauber.recipe.ZauberRecipes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static dev.louis.zauber.block.SpellTableBlock.MAX_CHARGE;
import static dev.louis.zauber.block.SpellTableBlock.MIN_CHARGE;

public class SpellTableScreenHandler extends ScreenHandler{
    private final ScreenHandlerContext context;
    private final Property selectedRecipe = Property.create();
    private final World world;
    private final Property charge;
    private List<RecipeEntry<SpellRecipe>> availableRecipes = Lists.newArrayList();
    private ItemStack inputStack = ItemStack.EMPTY;
    long lastTakeTime;
    final Slot bookInputSlot;
    final Slot heartInputSlot;
    final Slot outputSlot;
    Runnable contentsChangedListener = () -> {};
    public final Inventory input = new SimpleInventory(2){
        @Override
        public void markDirty() {
            super.markDirty();
            SpellTableScreenHandler.this.onContentChanged(this);
            SpellTableScreenHandler.this.contentsChangedListener.run();
        }
    };
    final CraftingResultInventory output = new CraftingResultInventory();

    public SpellTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, Property.create(), ScreenHandlerContext.EMPTY);
    }

    public SpellTableScreenHandler(int syncId, PlayerInventory playerInventory, Property charge, final ScreenHandlerContext context) {
        super(ZauberRecipes.SPELL_TABLE, syncId);
        int i;
        this.context = context;
        this.charge = charge;
        this.world = playerInventory.player.getWorld();
        this.bookInputSlot = this.addSlot(new Slot(this.input, 0, 20, 33));
        this.heartInputSlot = this.addSlot(new Slot(this.input, 1, 20, 53));
        this.outputSlot = this.addSlot(new Slot(this.output, 1, 143, 33){

            @Override
            public void onQuickTransfer(ItemStack newItem, ItemStack original) {
                modifyCharge(-original.getCount());
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                if(!modifyCharge(-stack.getCount())) {
                    if(player instanceof ServerPlayerEntity p)p.networkHandler.disconnect(Text.of("Please Contact Zauber Mod Dev"));
                    return;
                }

                stack.onCraftByPlayer(player.getWorld(), player, stack.getCount());
                SpellTableScreenHandler.this.output.unlockLastRecipe(player, this.getInputStacks());
                ItemStack itemStack = SpellTableScreenHandler.this.bookInputSlot.takeStack(1);
                SpellTableScreenHandler.this.heartInputSlot.takeStack(1);
                if (!itemStack.isEmpty()) {
                    SpellTableScreenHandler.this.populateResult();
                }
                SpellTableScreenHandler.this.context.run((world, pos) -> {
                    long l = world.getTime();
                    if (SpellTableScreenHandler.this.lastTakeTime != l) {
                        world.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        SpellTableScreenHandler.this.lastTakeTime = l;
                    }
                });
                super.onTakeItem(player, stack);
            }
            private List<ItemStack> getInputStacks() {
                return List.of(SpellTableScreenHandler.this.bookInputSlot.getStack());
            }
        });
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
        this.addProperty(this.selectedRecipe);
        this.addProperty(this.charge);
    }

    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    public List<RecipeEntry<SpellRecipe>> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }

    public boolean canCraft() {
        return (this.bookInputSlot.hasStack() && !this.availableRecipes.isEmpty());
    }

    public boolean hasCharge() {
        return charge.get() > MIN_CHARGE;
    }

    public int getCharge() {
        return charge.get();
    }

    public boolean modifyCharge(int charge) {
        int newCharge = this.charge.get() + charge;
        if(newCharge < MIN_CHARGE) {
            this.charge.set(MIN_CHARGE);
            return false;
        }
        if(newCharge > MAX_CHARGE) {
            this.charge.set(MAX_CHARGE);
            return false;
        }
        if(newCharge == MIN_CHARGE) this.availableRecipes.clear();

        this.charge.set(newCharge);
        return true;
    }

    public boolean isChargeValid(int charge) {
        return charge >= MIN_CHARGE && charge <= MAX_CHARGE;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return SpellTableScreenHandler.canUse(this.context, player, ZauberBlocks.SPELL_TABLE);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (this.isInBounds(id)) {
            this.selectedRecipe.set(id);
            this.populateResult();
        }
        return true;
    }

    private boolean isInBounds(int id) {
        return id >= 0 && id < this.availableRecipes.size();
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        ItemStack itemStack = this.bookInputSlot.getStack();
        if (inventory == this.input) {
            this.inputStack = itemStack.copy();
            this.updateInput(inventory, itemStack);
        }
    }

    private void updateInput(Inventory input, ItemStack stack) {
        this.availableRecipes.clear();
        this.selectedRecipe.set(-1);
        this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
        if (!stack.isEmpty() && hasCharge()) {
            this.availableRecipes = this.world.getRecipeManager().listAllOfType(ZauberRecipes.SPELL_RECIPE).stream()
                    .filter(recipe -> recipe.value().matches(input, world))
                    .sorted(Comparator.comparing(RecipeEntry::id))
                    .collect(Collectors.toList());
        }
    }

    void populateResult() {
        if (!this.availableRecipes.isEmpty() && this.isInBounds(this.selectedRecipe.get())) {
            RecipeEntry<SpellRecipe> spellRecipe = this.availableRecipes.get(this.selectedRecipe.get());
            ItemStack itemStack = spellRecipe.value().craft(this.input, this.world.getRegistryManager());
            if (itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
                this.output.setLastRecipe(spellRecipe);
                this.outputSlot.setStackNoCallbacks(itemStack);
            } else {
                this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
            }
        } else {
            this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return ZauberRecipes.SPELL_TABLE;
    }

    public void setContentsChangedListener(Runnable contentsChangedListener) {
        this.contentsChangedListener = contentsChangedListener;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.output && super.canInsertIntoSlot(stack, slot);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();
            if (slot == 2) {
                if(!isChargeValid(this.charge.get()-itemStack2.getCount()))return ItemStack.EMPTY;
                item.onCraftByPlayer(itemStack2, player.getWorld(), player);
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot2.onQuickTransfer(itemStack2, itemStack);
            } else {
                //WARNING AHEAD!!!
                //DEAMONS MAY JUMP OUT OF THIS CODE AND ATTACK YOU THROUGH YOUR COMPUTER SCREEN PROCEED WITH CAUTION!
                boolean moveIntoSpellTableSlots = slot == 0 || slot == 1 ? !this.insertItem(itemStack2, 3, 39, false) : (this.world.getRecipeManager().getFirstMatch(ZauberRecipes.SPELL_RECIPE, new SimpleInventory(itemStack2, this.heartInputSlot.getStack()), this.world).isPresent() || this.world.getRecipeManager().getFirstMatch(ZauberRecipes.SPELL_RECIPE, new SimpleInventory(this.bookInputSlot.getStack(), itemStack2), this.world).isPresent());
                boolean b;
                if (moveIntoSpellTableSlots) {
                    b = !this.insertItem(itemStack2, 0, 2, false);
                } else {
                    b = (slot >= 3 && slot < 30 ? !this.insertItem(itemStack2, 30, 39, false) : slot >= 30 && slot < 39 && !this.insertItem(itemStack2, 3, 30, false));
                }
                if (b) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            }
            slot2.markDirty();
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot2.onTakeItem(player, itemStack2);
            this.sendContentUpdates();
        }
        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.output.removeStack(1);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }
}
