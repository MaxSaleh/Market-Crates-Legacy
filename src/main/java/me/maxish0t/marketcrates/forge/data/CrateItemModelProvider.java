package me.maxish0t.marketcrates.forge.data;

import me.maxish0t.marketcrates.common.Ref;
import me.maxish0t.marketcrates.forge.core.CrateRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class CrateItemModelProvider extends ItemModelProvider {


    public CrateItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Ref.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (String material : CrateRegistry.woodTypes) {
            // System.out.println(material);
            Item crateItem = CrateRegistry.items.get(material).get();
            ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(crateItem);

            getBuilder(resourceLocation.getPath())
                    .parent(new ModelFile.UncheckedModelFile("block/block"))
                    .texture("particle", "minecraft:block/" + material + "_planks")
                    .texture("material", "minecraft:block/" + material + "_planks")

                    .element()
                    .from(0, 0, 0)
                    .to(16, 16, 16);
        }
    }
}
