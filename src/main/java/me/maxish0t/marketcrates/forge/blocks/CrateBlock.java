package me.maxish0t.marketcrates.forge.blocks;

import me.maxish0t.marketcrates.common.blocks.AbstractCrateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

public class CrateBlock extends AbstractCrateBlock {

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(pos, state);
    }

    @Override
    protected void openGui(BlockState state, Level world, Player player, BlockPos pos) {
        // FORGE open inventory
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof CrateBlockEntity) {
            NetworkHooks.openScreen((ServerPlayer) player, (CrateBlockEntity) tile, tile.getBlockPos());
        }

    }
}
