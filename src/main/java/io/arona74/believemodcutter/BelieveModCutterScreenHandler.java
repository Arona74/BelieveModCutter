package io.arona74.believemodcutter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BelieveModCutterScreenHandler extends ScreenHandler {
    private final ScreenHandlerContext context;
    private final Property selectedRecipe = Property.create();
    private final World world;
    private List<BelieveModCutterRecipe> availableRecipes = new ArrayList<>();
    private ItemStack inputStack = ItemStack.EMPTY;
    private long lastTakeTime;
    
    final Slot inputSlot;
    final Slot outputSlot;
    private Runnable contentsChangedListener = () -> {};
    
    public final SimpleInventory input = new SimpleInventory(1) {
        @Override
        public void markDirty() {
            super.markDirty();
            BelieveModCutterScreenHandler.this.onContentChanged(this);
            BelieveModCutterScreenHandler.this.contentsChangedListener.run();
        }
    };
    
    private final CraftingResultInventory output = new CraftingResultInventory();

    public BelieveModCutterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public BelieveModCutterScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(BelieveModCutterMod.BELIEVEMOD_CUTTER_SCREEN_HANDLER, syncId);
        this.context = context;
        this.world = playerInventory.player.getWorld();
        
        // Input slot
        this.inputSlot = this.addSlot(new Slot(this.input, 0, 20, 33));
        
        // Output slot
        this.outputSlot = this.addSlot(new Slot(this.output, 1, 143, 33) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                stack.onCraft(player.getWorld(), player, stack.getCount());
                BelieveModCutterScreenHandler.this.output.unlockLastRecipe(player, this.getInputStacks());
                ItemStack inputStack = BelieveModCutterScreenHandler.this.inputSlot.takeStack(1);
                if (!inputStack.isEmpty()) {
                    BelieveModCutterScreenHandler.this.populateResult();
                }

                context.run((world, pos) -> {
                    long l = world.getTime();
                    if (BelieveModCutterScreenHandler.this.lastTakeTime != l) {
                        world.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, 
                            SoundCategory.BLOCKS, 1.0f, 1.0f);
                        BelieveModCutterScreenHandler.this.lastTakeTime = l;
                    }
                });
                super.onTakeItem(player, stack);
            }

            private List<ItemStack> getInputStacks() {
                return List.of(BelieveModCutterScreenHandler.this.inputSlot.getStack());
            }
        });

        // Player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Player hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        this.addProperty(this.selectedRecipe);
    }

    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    public List<BelieveModCutterRecipe> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }

    public boolean canCraft() {
        return this.inputSlot.hasStack() && !this.availableRecipes.isEmpty();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, BelieveModCutterMod.BELIEVEMOD_CUTTER);
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
        ItemStack itemStack = this.inputSlot.getStack();
        if (!itemStack.isOf(this.inputStack.getItem())) {
            this.inputStack = itemStack.copy();
            this.updateInput(inventory, itemStack);
        }
    }

    private void updateInput(Inventory input, ItemStack stack) {
        this.availableRecipes.clear();
        this.selectedRecipe.set(-1);
        this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            this.availableRecipes = new ArrayList<>(this.world.getRecipeManager().getAllMatches(BelieveModCutterMod.BELIEVEMOD_CUTTING_RECIPE_TYPE, input, this.world));
        }
    }

    private void populateResult() {
        if (!this.availableRecipes.isEmpty() && this.isInBounds(this.selectedRecipe.get())) {
            BelieveModCutterRecipe recipe = this.availableRecipes.get(this.selectedRecipe.get());
            ItemStack itemStack = recipe.craft(this.input, this.world.getRegistryManager());
            if (itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
                this.output.setLastRecipe(recipe);
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
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        if (id == 0) {
            this.populateResult();
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();
            if (slot == 1) {
                item.onCraft(itemStack2, player.getWorld(), player);
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot2.onQuickTransfer(itemStack2, itemStack);
            } else if (slot == 0) {
                if (!this.insertItem(itemStack2, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.world.getRecipeManager().getFirstMatch(BelieveModCutterMod.BELIEVEMOD_CUTTING_RECIPE_TYPE, new SimpleInventory(itemStack2), this.world).isPresent()) {
                if (!this.insertItem(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot >= 2 && slot < 29) {
                if (!this.insertItem(itemStack2, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot >= 29 && slot < 38 && !this.insertItem(itemStack2, 2, 29, false)) {
                return ItemStack.EMPTY;
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

    public void setContentsChangedListener(Runnable contentsChangedListener) {
        this.contentsChangedListener = contentsChangedListener;
    }
}