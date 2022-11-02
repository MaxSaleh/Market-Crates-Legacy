package com.lfaoanl.marketcrates.forge.core;

import com.lfaoanl.marketcrates.common.Ref;
import com.lfaoanl.marketcrates.common.items.CrateItem;
import com.lfaoanl.marketcrates.forge.blocks.CrateBlock;
import com.lfaoanl.marketcrates.forge.blocks.CrateBlockEntity;
import com.lfaoanl.marketcrates.forge.gui.CrateContainer;
import com.lfaoanl.marketcrates.forge.gui.CrateDoubleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

public class CrateRegistry {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Ref.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ref.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ref.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Ref.MODID);


    public static final RegistryObject<MenuType<CrateContainer>> CONTAINER_CRATE = registerContainer("crate", (IContainerFactory<CrateContainer>) (windowId, playerInventory, data) -> new CrateContainer(windowId, playerInventory));

    public static final RegistryObject<MenuType<CrateDoubleContainer>> CONTAINER_CRATE_DOUBLE = registerContainer("crate_double", (IContainerFactory<CrateDoubleContainer>) (windowId, playerInv, data) -> new CrateDoubleContainer(windowId, playerInv));


    public static String[] woodTypes = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "crimson", "warped"};

    public static final RegistryObject<BlockEntityType<CrateBlockEntity>> CRATE_TILE = TILES.register("crate", () -> BlockEntityType.Builder.of(CrateBlockEntity::new, CrateRegistry.validCrates()).build(null));

    public static final HashMap<String, RegistryObject<Item>> items = new HashMap<>();
    public static final HashMap<String, RegistryObject<Block>> blocks = new HashMap<>();


    public static void init() {

        for (String type : woodTypes) {
            // System.out.println("Register: " + type);
            RegistryObject<Block> block = BLOCKS.register(type + "_crate", CrateBlock::new);
            RegistryObject<Item> item = ITEMS.register(type + "_crate", () -> new CrateItem(block.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));

            blocks.put(type, block);
            items.put(type, item);
        }

        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

    }

    private static Block[] validCrates() {
        Block[] b = new Block[woodTypes.length];

        for (int i = 0; i < woodTypes.length; i++) {
            String type = woodTypes[i];
            b[i] = blocks.get(type).get();
        }

        return b;
    }

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerContainer(String key, MenuType.MenuSupplier<T> supplier)
    {
        MenuType<T> type = new MenuType<>(supplier);
        return CONTAINERS.register(key, () -> type);
    }

}
