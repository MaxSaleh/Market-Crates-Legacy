package com.lfaoanl.marketcrates.common.gui;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class BaseCrateContainer extends AbstractContainerMenu {


    protected final Container inventory;
    private final int size;

    public BaseCrateContainer(int id, Inventory playerInventory, Container inventory, int size, MenuType<? extends BaseCrateContainer> containerType, boolean isDouble) {
        super(containerType, id);

        if (isDouble) {
            // Left
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 2; ++j) {
                    this.addSlot(new Slot(inventory, i + j * 3, 35 + j * 18, 17 + i * 18));
                }
            }

            // Right
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 2; ++j) {
                    this.addSlot(new Slot(inventory, 6 + i + j * 3, 107 + j * 18, 17 + i * 18));
                }
            }
        } else {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 2; ++j) {
                    this.addSlot(new Slot(inventory, i + j * 3, 71 + j * 18, 17 + i * 18));
                }
            }
        }

        this.size = size;

        checkContainerSize(inventory, size);
        this.inventory = inventory;
        inventory.startOpen(playerInventory.player);

        drawUserInventory(playerInventory);

    }

    private void drawContainerSlots() {
        //TODO dynamically calculate x values with an offset
//        for (int i = 0; i < 3; ++i) {
//            for (int j = 0; j < 2; ++j) {
//                this.addSlot(new Slot(inventory, i + j * 3, 71 + j * 18, 17 + i * 18));
//            }
//        }
    }

    private void drawUserInventory(Inventory playerInventory) {
        // Player inventory
        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        // Hotbar
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean stillValid(Player playerIn) {
        return this.inventory.stillValid(playerIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (i < size ? !this.moveItemStackTo(itemStack2, size, 36 + size, true) : !this.moveItemStackTo(itemStack2, 0, size, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack2);
        }
        return itemStack;
    }

    /**
     * Called when the container is closed.
     */
    public void removed(Player playerIn) {
        super.removed(playerIn);
        this.inventory.stopOpen(playerIn);
    }

}
