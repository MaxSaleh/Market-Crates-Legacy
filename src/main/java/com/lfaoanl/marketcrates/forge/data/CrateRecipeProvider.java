package com.lfaoanl.marketcrates.forge.data;

import com.lfaoanl.marketcrates.common.Ref;
import com.lfaoanl.marketcrates.forge.core.CrateRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class CrateRecipeProvider extends RecipeProvider {

    private final Blocks facadeBlocks = new Blocks();

    public CrateRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

        for (String material : CrateRegistry.woodTypes) {
            RegistryObject<Block> registry = CrateRegistry.blocks.get(material);
            Block block = registry.get();

            ShapedRecipeBuilder.shaped(block)
                    .pattern("FFF")
                    .pattern("SSS")
                    .define('F', getBlock(material, "fence"))
                    .define('S', getBlock(material, "slab"))
                    .group(Ref.MODID)
                    .unlockedBy("has_wood_slab", has(getBlock(material, "slab")))
                    .save(consumer, resourceLocation(registry.getId()));

        }

    }


    protected ResourceLocation resourceLocation(ResourceLocation location) {
        return new ResourceLocation(location.getNamespace(), location.getPath());
    }

    private Block getBlock(String material, String name) {
        // System.out.println(material + " - " + name);
        String finalName = String.format("%s_%s", material.toUpperCase(), name.toUpperCase());
        // System.out.println(finalName);
        try {
            return (Block) Blocks.class.getDeclaredField(finalName).get(facadeBlocks);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
