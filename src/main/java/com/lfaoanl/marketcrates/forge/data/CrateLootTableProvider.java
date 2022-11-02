package com.lfaoanl.marketcrates.forge.data;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lfaoanl.marketcrates.common.Ref;
import com.lfaoanl.marketcrates.forge.core.CrateRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class CrateLootTableProvider implements DataProvider {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    protected final Map<ResourceLocation, LootTable> lootTables = new HashMap<>();

    private final DataGenerator generator;

    protected CrateLootTableProvider(DataGenerator generator) {
        this.generator = generator;
    }


    protected void createLootTables() {
        for (String material : CrateRegistry.woodTypes) {
            Block block = CrateRegistry.blocks.get(material).get();

            registerSelfDrop(block);
        }
    }


    @Override
    public void run(HashCache cache) {
        lootTables.clear();
        Path outFolder = generator.getOutputFolder();

        createLootTables();

        ValidationContext validator = new ValidationContext(LootContextParamSets.ALL_PARAMS, function -> null, lootTables::get);
        lootTables.forEach((name, table) -> LootTables.validate(validator, name, table));
        Multimap<String, String> problems = validator.getProblems();
        if (!problems.isEmpty()) {
            problems.forEach((name, table) -> System.out.println("Found validation problem in " + name + ": " + table));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        } else {
            lootTables.forEach((name, table) -> {
                Path out = getPath(outFolder, name);

                try {
                    DataProvider.save(GSON, cache, LootTables.serialize(table), out);
                } catch (IOException e) {
                    // System.out.println("Couldn't save loot table " + out);
                    // System.out.println(Arrays.toString(e.getStackTrace()));
                }
            });
        }
    }

    @Override
    public String getName() {
        return "Loot Tables: " + Ref.MODID;
    }

    protected LootPool.Builder createLootPoolBuilder() {
        return LootPool.lootPool().when(ExplosionCondition.survivesExplosion());
    }

    protected void register(Block block, LootPool.Builder... pools) {
        LootTable.Builder builder = LootTable.lootTable();
        for (LootPool.Builder pool : pools) {
            builder.withPool(pool);
        }
        register(block, builder);
    }

    protected void registerSelfDrop(Block block) {
        register(block, singleItem(block));
    }

    private Path getPath(Path outFolder, ResourceLocation name) {
        return outFolder.resolve("data/" + name.getNamespace() + "/loot_tables/" + name.getPath() + ".json");
    }

    private void register(Block block, LootTable.Builder table) {
        register(block.getRegistryName(), table);
    }

    private void register(ResourceLocation registryName, LootTable.Builder table) {
        if (lootTables.put(toTableLoc(registryName), table.setParamSet(LootContextParamSets.BLOCK).build()) != null) {
            throw new IllegalStateException("Duplicate loot table: " + table);
        }
    }

    private LootPool.Builder singleItem(ItemLike in) {
        return createLootPoolBuilder().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(in));
    }

    private ResourceLocation toTableLoc(ResourceLocation registryName) {
        return new ResourceLocation(registryName.getNamespace(), "blocks/" + registryName.getPath());
    }
}