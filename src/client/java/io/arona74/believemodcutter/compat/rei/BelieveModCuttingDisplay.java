package io.arona74.believemodcutter.compat.rei;

import io.arona74.believemodcutter.BelieveModCutterRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class BelieveModCuttingDisplay implements Display {
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;

    public BelieveModCuttingDisplay(BelieveModCutterRecipe recipe) {
        this.inputs = List.of(EntryIngredients.ofIngredient(recipe.getIngredient()));
        this.outputs = List.of(EntryIngredients.of(recipe.getOutput(MinecraftClient.getInstance().world.getRegistryManager())));
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return BelieveModCutterREIPlugin.BELIEVEMOD_CUTTING;
    }
}