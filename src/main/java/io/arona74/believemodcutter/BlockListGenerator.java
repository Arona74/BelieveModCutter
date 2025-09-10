package io.arona74.believemodcutter;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockListGenerator {
    
    public static void generateBlockCSV(String modId, String outputPath) {
        List<BlockInfo> blocks = new ArrayList<>();
        
        // Collect all blocks from the specified mod
        for (Block block : Registries.BLOCK) {
            Identifier id = Registries.BLOCK.getId(block);
            if (id.getNamespace().equals(modId)) {
                blocks.add(new BlockInfo(
                    id.toString(),
                    id.getPath(),
                    block.getClass().getSimpleName(),
                    block.getHardness(),
                    block.getBlastResistance()
                ));
            }
        }
        
        // Write to CSV
        try {
            writeCSV(blocks, outputPath);
            System.out.println("Generated CSV with " + blocks.size() + " blocks from mod: " + modId);
            System.out.println("File saved to: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
        }
    }
    
    private static void writeCSV(List<BlockInfo> blocks, String outputPath) throws IOException {
        try (FileWriter writer = new FileWriter(outputPath)) {
            // Write header
            writer.append("Registry_Name,Block_Name,Block_Class,Hardness,Blast_Resistance\n");
            
            // Write block data
            for (BlockInfo block : blocks) {
                writer.append(escapeCSV(block.registryName))
                      .append(",")
                      .append(escapeCSV(block.blockName))
                      .append(",")
                      .append(escapeCSV(block.blockClass))
                      .append(",")
                      .append(String.valueOf(block.hardness))
                      .append(",")
                      .append(String.valueOf(block.blastResistance))
                      .append("\n");
            }
        }
    }
    
    private static String escapeCSV(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    // Call this from your mod initialization or create a command
    public static void generateBelieveModBlocks() {
        String outputPath = "believemod_blocks.csv";
        generateBlockCSV("believemod", outputPath);
    }
    
    private static class BlockInfo {
        final String registryName;
        final String blockName;
        final String blockClass;
        final float hardness;
        final float blastResistance;
        
        BlockInfo(String registryName, String blockName, String blockClass, float hardness, float blastResistance) {
            this.registryName = registryName;
            this.blockName = blockName;
            this.blockClass = blockClass;
            this.hardness = hardness;
            this.blastResistance = blastResistance;
        }
    }
}