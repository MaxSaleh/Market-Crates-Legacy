package com.lfaoanl.marketcrates.common.blocks;

import com.lfaoanl.marketcrates.common.ItemOrientation;
import com.lfaoanl.marketcrates.common.MarketCrates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractCrateBlockEntity extends BaseContainerBlockEntity {

    protected NonNullList<ItemOrientation> stacks = NonNullList.withSize(12, ItemOrientation.EMPTY);

    private boolean isDouble = false;

    protected AbstractCrateBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }


    public NonNullList<ItemOrientation> getItems() {
        return stacks;
    }

    public boolean isDoubleCrate() {
        if (getBlockState().isAir()) {
            return this.isDouble;
        }
        // Store items to be able to retreive it after the block is broken
        this.isDouble = getBlockState().getValue(AbstractCrateBlock.TYPE).isDouble();

        return this.isDouble;
    }

    public void setItem(int index, ItemStack stack) {
        this.getItems().set(index, new ItemOrientation(stack));
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    public boolean isEmpty() {
        return this.getItems().stream().allMatch(ItemOrientation::isEmpty);
    }

    public ItemStack getItem(int index) {
        // TODO increase size of `stacks` and check if crateIsDouble to allow the supplied index
        return this.getItems().get(index).getItemStack();
    }

    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = ContainerHelper.removeItem(ItemOrientation.toItemStack(stacks), index, count);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }
        return itemstack;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sendContents();
    }

    public ItemStack removeItemNoUpdate(int index) {
        ItemOrientation orientation = index >= 0 && index < stacks.size() ? stacks.set(index, ItemOrientation.EMPTY) : ItemOrientation.EMPTY;

        return orientation.getItemStack();
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.crate");
    }

    @Override
    public int getContainerSize() {
//        if (isDoubleCrate()) {
        return 12;
//        }
//        return 6;
    }

    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    public void clearContent() {
        this.getItems().clear();
    }

    public void load(CompoundTag nbt) {
        super.load(nbt);

        this.stacks = loadFromNbt(nbt);
    }

    private NonNullList<ItemOrientation> loadFromNbt(CompoundTag nbt) {
        NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, items);
        return ItemOrientation.toItemOrientation(items);
    }

    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, ItemOrientation.toItemStack(this.stacks));
    }

    public void receiveContents(NonNullList<ItemStack> stacks) {
        MarketCrates.LOGGER.debug("Received packets");
        MarketCrates.LOGGER.debug(stacks.get(0));

        this.stacks = ItemOrientation.toItemOrientation(stacks);
    }

    public abstract void sendContents();

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = super.getUpdateTag();

        ContainerHelper.saveAllItems(updateTag, ItemOrientation.toItemStack(stacks));

        return updateTag;
    }

}
