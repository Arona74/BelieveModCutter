package io.arona74.believemodcutter.compat.rei;

import io.arona74.believemodcutter.BelieveModCutterMod;
import io.arona74.believemodcutter.BelieveModCutterRecipe;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BelieveModCutterREIPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<BelieveModCuttingDisplay> BELIEVEMOD_CUTTING = 
        CategoryIdentifier.of(new Identifier(BelieveModCutterMod.MOD_ID, "believemod_cutting"));

    @Override
    public void registerCategories(CategoryRegistry registry) {
        System.out.println("REI: Registering BelieveMod Cutting category");
        registry.add(new BelieveModCuttingCategory());
        registry.addWorkstations(BELIEVEMOD_CUTTING, EntryStacks.of(BelieveModCutterMod.BELIEVEMOD_CUTTER_ITEM));
        System.out.println("REI: Category and workstation registered");
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        System.out.println("REI: Registering displays");
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            // Get all recipes of our type directly
            Collection<BelieveModCutterRecipe> recipeCollection = client.world.getRecipeManager()
                .listAllOfType(BelieveModCutterMod.BELIEVEMOD_CUTTING_RECIPE_TYPE);
            
            List<BelieveModCutterRecipe> recipes = new ArrayList<>(recipeCollection);
            
            System.out.println("REI: Found " + recipes.size() + " BelieveMod cutting recipes");
            
            for (BelieveModCutterRecipe recipe : recipes) {
                registry.add(new BelieveModCuttingDisplay(recipe));
                System.out.println("REI: Added recipe: " + recipe.getId());
            }
        } else {
            System.out.println("REI: Client world is null, cannot register displays");
        }
    }
}