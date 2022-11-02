package me.maxish0t.marketcrates.forge.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public class CrateDataGenerator {

    public static void init(GatherDataEvent event) {


        DataGenerator generator = event.getGenerator();

        if (event.includeServer()) {
            // Recipes
            generator.addProvider(true, new CrateRecipeProvider(generator));
            // LootTable
            generator.addProvider(true, new CrateLootTableProvider(generator));
        }
        if (event.includeClient()) {
            // BlockStates
            // System.out.println("LFAOANL: Data generator");
            generator.addProvider(true, new CrateBlockStates(generator, event.getExistingFileHelper()));

            // Items
            generator.addProvider(true, new CrateItemModelProvider(generator, event.getExistingFileHelper()));
        }
    }
}
