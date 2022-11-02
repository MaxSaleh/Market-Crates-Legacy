package com.lfaoanl.marketcrates.common.items;

import com.lfaoanl.marketcrates.common.blocks.AbstractCrateBlock;
import com.lfaoanl.marketcrates.common.blocks.AbstractCrateBlockEntity;
import com.lfaoanl.marketcrates.common.blocks.states.CrateType;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CrateItem extends BlockItem {

    public CrateItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();

        // If item is same as BlockItem of targetted CrateBlock
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof AbstractCrateBlock && ((AbstractCrateBlock) block).getBlockItem().getItem().equals(this)) {

            // If the TileEntity is not already a double crate
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof AbstractCrateBlockEntity && !((AbstractCrateBlockEntity) tileEntity).isDoubleCrate()) {
                BlockState newState = tileEntity.getBlockState().setValue(AbstractCrateBlock.TYPE, CrateType.DOUBLE);
                Player player = context.getPlayer();

                world.setBlock(pos, newState, 2);
                ((AbstractCrateBlock) tileEntity.getBlockState().getBlock()).playSound(world, pos, player, SoundEvents.WOOD_PLACE);

                if (!player.isCreative()) {
                    context.getItemInHand().setCount(context.getItemInHand().getCount() - 1);
                }

                // SUCCES when client && CONSUME when server
                return InteractionResult.sidedSuccess(world.isClientSide());
            }
        }

        return super.useOn(context);
    }
}
