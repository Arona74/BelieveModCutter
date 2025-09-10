package io.arona74.believemodcutter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RecipeGenerator {
    
    public static void generateRecipesFromCSV(String csvFilePath, String outputDirectory) {
        List<RecipeEntry> recipes = readCSV(csvFilePath);
        
        // Create output directory if it doesn't exist
        Path outputPath = Paths.get(outputDirectory);
        try {
            Files.createDirectories(outputPath);
        } catch (IOException e) {
            System.err.println("Error creating output directory: " + e.getMessage());
            return;
        }
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        int successCount = 0;
        
        for (RecipeEntry recipe : recipes) {
            try {
                // Create recipe JSON
                JsonObject recipeJson = createRecipeJson(recipe);
                
                // Generate filename (sanitize the output block name)
                String filename = sanitizeFilename(recipe.outputBlock) + ".json";
                Path recipePath = outputPath.resolve(filename);
                
                // Write to file
                try (FileWriter writer = new FileWriter(recipePath.toFile())) {
                    gson.toJson(recipeJson, writer);
                }
                
                successCount++;
                System.out.println("Generated: " + filename);
                
            } catch (IOException e) {
                System.err.println("Error writing recipe for " + recipe.outputBlock + ": " + e.getMessage());
            }
        }
        
        System.out.println("Successfully generated " + successCount + " recipe files in: " + outputDirectory);
    }
    
    private static List<RecipeEntry> readCSV(String csvFilePath) {
        List<RecipeEntry> recipes = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isHeader = true;
            int lineNumber = 0;
            
            System.out.println("Reading CSV file...");
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                System.out.println("Line " + lineNumber + ": " + line);
                
                if (isHeader) {
                    isHeader = false;
                    System.out.println("Skipping header line");
                    continue; // Skip header
                }
                
                String[] parts = parseCSVLine(line);
                System.out.println("Parsed into " + parts.length + " parts: " + java.util.Arrays.toString(parts));
                
                if (parts.length >= 2) {
                    String outputBlock = parts[0].trim();
                    String inputBlock = parts[1].trim();
                    
                    System.out.println("Output: '" + outputBlock + "', Input: '" + inputBlock + "'");
                    
                    if (!outputBlock.isEmpty() && !inputBlock.isEmpty()) {
                        recipes.add(new RecipeEntry(outputBlock, inputBlock));
                        System.out.println("Added recipe: " + inputBlock + " -> " + outputBlock);
                    } else {
                        System.out.println("Skipping empty entry");
                    }
                } else {
                    System.out.println("Line has less than 2 parts, skipping");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
        
        System.out.println("Total recipes found: " + recipes.size());
        return recipes;
    }
    
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if ((c == ',' || c == ';') && !inQuotes) { // Handle both comma and semicolon
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }
    
    private static JsonObject createRecipeJson(RecipeEntry recipe) {
        JsonObject recipeJson = new JsonObject();
        recipeJson.addProperty("type", "believemodcutter:believemod_cutting");
        
        // Ingredient
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("item", recipe.inputBlock);
        recipeJson.add("ingredient", ingredient);
        
        // Result
        JsonObject result = new JsonObject();
        result.addProperty("item", recipe.outputBlock);
        result.addProperty("count", 1);
        recipeJson.add("result", result);
        
        return recipeJson;
    }
    
    private static String sanitizeFilename(String blockName) {
        // Remove mod namespace and convert to valid filename
        String name = blockName;
        if (name.contains(":")) {
            name = name.substring(name.indexOf(":") + 1);
        }
        
        // Replace invalid characters
        return name.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
    
    // Call this method to generate recipes
    public static void generateBelieveModRecipes() {
        String csvPath = "recipes.csv"; // Your CSV file path
        String outputDir = "src/main/resources/data/believemodcutter/recipes/block_cutting/";
        
        System.out.println("=== Recipe Generator Starting ===");
        System.out.println("Looking for CSV file: " + csvPath);
        System.out.println("Output directory: " + outputDir);
        
        // Check if CSV file exists
        File csvFile = new File(csvPath);
        if (!csvFile.exists()) {
            System.err.println("ERROR: CSV file not found at: " + csvFile.getAbsolutePath());
            System.err.println("Please create recipes.csv in your project root with format:");
            System.err.println("output_block,input_block");
            System.err.println("minecraft:stone_bricks,minecraft:cobblestone");
            return;
        }
        
        generateRecipesFromCSV(csvPath, outputDir);
    }
    
    private static class RecipeEntry {
        final String outputBlock;
        final String inputBlock;
        
        RecipeEntry(String outputBlock, String inputBlock) {
            this.outputBlock = outputBlock;
            this.inputBlock = inputBlock;
        }
    }
}