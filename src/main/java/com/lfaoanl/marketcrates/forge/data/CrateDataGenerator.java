package com.lfaoanl.marketcrates.forge.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class CrateDataGenerator {

    public static void init(GatherDataEvent event) {


        DataGenerator generator = event.getGenerator();

        if (event.includeServer()) {
            // Recipes
            generator.addProvider(new CrateRecipeProvider(generator));
            // LootTable
            generator.addProvider(new CrateLootTableProvider(generator));
        }
        if (event.includeClient()) {
            // BlockStates
            // System.out.println("LFAOANL: Data generator");
            generator.addProvider(new CrateBlockStates(generator, event.getExistingFileHelper()));

            // Items
            generator.addProvider(new CrateItemModelProvider(generator, event.getExistingFileHelper()));
        }
    }
}
