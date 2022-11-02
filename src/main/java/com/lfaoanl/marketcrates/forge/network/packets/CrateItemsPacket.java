package com.lfaoanl.marketcrates.forge.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class CrateItemsPacket {

    public final NonNullList<ItemStack> items;
    private final BlockPos position;

    public CrateItemsPacket(BlockPos position, NonNullList<ItemStack> items) {
        this.items = items;
        this.position = position;
    }

    public static void encode(CrateItemsPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.position.getX());
        buf.writeInt(msg.position.getY());
        buf.writeInt(msg.position.getZ());

        buf.writeInt(msg.items.size());

        for (ItemStack stack : msg.items) {
            buf.writeItem(stack);
        }
    }

    public BlockPos getPosition() {
        return position;
    }

    public static CrateItemsPacket decode(FriendlyByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        BlockPos blockPos = new BlockPos(x, y, z);

        int size = buf.readInt();
        NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);

        for (int i = 0; i < size; i++) {
            ItemStack itemStack = buf.readItem();
            items.set(i, itemStack);
        }

        return new CrateItemsPacket(blockPos, items);
    }
}
