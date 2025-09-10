package io.arona74.believemodcutter;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class BelieveModCutterRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final String group;
    private final Ingredient ingredient;
    private final ItemStack result;

    public BelieveModCutterRecipe(Identifier id, String group, Ingredient ingredient, ItemStack result) {
        this.id = id;
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return this.ingredient.test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.result;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BelieveModCutterMod.BELIEVEMOD_CUTTING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BelieveModCutterMod.BELIEVEMOD_CUTTING_RECIPE_TYPE;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(BelieveModCutterMod.BELIEVEMOD_CUTTER);
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public static class Serializer implements RecipeSerializer<BelieveModCutterRecipe> {
        
        @Override
        public BelieveModCutterRecipe read(Identifier id, JsonObject json) {
            String group = JsonHelper.getString(json, "group", "");
            
            Ingredient ingredient;
            if (JsonHelper.hasArray(json, "ingredient")) {
                ingredient = Ingredient.fromJson(JsonHelper.getArray(json, "ingredient"));
            } else {
                ingredient = Ingredient.fromJson(JsonHelper.getObject(json, "ingredient"));
            }
            
            String resultId = JsonHelper.getString(JsonHelper.getObject(json, "result"), "item");
            int count = JsonHelper.getInt(JsonHelper.getObject(json, "result"), "count", 1);
            ItemStack result = new ItemStack(Registries.ITEM.get(new Identifier(resultId)), count);
            
            return new BelieveModCutterRecipe(id, group, ingredient, result);
        }

        @Override
        public BelieveModCutterRecipe read(Identifier id, PacketByteBuf buf) {
            String group = buf.readString();
            Ingredient ingredient = Ingredient.fromPacket(buf);
            ItemStack result = buf.readItemStack();
            return new BelieveModCutterRecipe(id, group, ingredient, result);
        }

        @Override
        public void write(PacketByteBuf buf, BelieveModCutterRecipe recipe) {
            buf.writeString(recipe.group);
            recipe.ingredient.write(buf);
            buf.writeItemStack(recipe.result);
        }
    }
}