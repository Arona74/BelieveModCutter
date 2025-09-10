package io.arona74.believemodcutter;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BelieveModCutterMod implements ModInitializer {
    public static final String MOD_ID = "believemodcutter";
   
    // Block and Item
    public static final Block BELIEVEMOD_CUTTER = Registry.register(
        Registries.BLOCK,
        new Identifier(MOD_ID, "believemod_cutter"),
        new BelieveModCutterBlock(FabricBlockSettings.copyOf(Blocks.STONE)
            .strength(3.5f)
            .sounds(BlockSoundGroup.STONE)
            .requiresTool())
    );
   
    public static final BlockItem BELIEVEMOD_CUTTER_ITEM = Registry.register(
        Registries.ITEM,
        new Identifier(MOD_ID, "believemod_cutter"),
        new BlockItem(BELIEVEMOD_CUTTER, new FabricItemSettings())
    );
   
    // Recipe Type
    public static final RecipeType<BelieveModCutterRecipe> BELIEVEMOD_CUTTING_RECIPE_TYPE =
        Registry.register(Registries.RECIPE_TYPE, new Identifier(MOD_ID, "believemod_cutting"),
        new RecipeType<BelieveModCutterRecipe>() {
            public String toString() {
                return "believemod_cutting";
            }
        });
   
    // Recipe Serializer
    public static final RecipeSerializer<BelieveModCutterRecipe> BELIEVEMOD_CUTTING_RECIPE_SERIALIZER =
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(MOD_ID, "believemod_cutting"),
        new BelieveModCutterRecipe.Serializer());
   
    // Screen Handler
    public static final ScreenHandlerType<BelieveModCutterScreenHandler> BELIEVEMOD_CUTTER_SCREEN_HANDLER =
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, "believemod_cutter"),
        new ScreenHandlerType<>((syncId, inventory) -> new BelieveModCutterScreenHandler(syncId, inventory), null));
   
    @Override
    public void onInitialize() {
        // Add to creative inventory
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.add(BELIEVEMOD_CUTTER_ITEM);
        });
        
        // Generate BelieveMod blocks CSV (comment out after first run) FOR DEV ONLY
        // BlockListGenerator.generateBelieveModBlocks();
        
        // Generate recipes from CSV (comment out after first run) FOR DEV ONLY
        // RecipeGenerator.generateBelieveModRecipes();
    }
   
    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}