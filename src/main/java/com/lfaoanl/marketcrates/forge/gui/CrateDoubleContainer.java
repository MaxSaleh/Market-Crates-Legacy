package com.lfaoanl.marketcrates.forge.gui;

import com.lfaoanl.marketcrates.common.gui.BaseCrateContainer;
import com.lfaoanl.marketcrates.forge.core.CrateRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class CrateDoubleContainer extends BaseCrateContainer {

    private static final MenuType<CrateDoubleContainer> CONTAINER_REGISTRY = CrateRegistry.CONTAINER_CRATE_DOUBLE.get();

    public CrateDoubleContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(12));

    }

    public CrateDoubleContainer(int id, Inventory playerInventory, Container inventory) {
        super(id, playerInventory, inventory, 12, CONTAINER_REGISTRY, true);
    }
}