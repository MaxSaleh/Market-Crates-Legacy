package com.lfaoanl.marketcrates.common.blocks;

import com.lfaoanl.marketcrates.common.ItemOrientation;
import com.lfaoanl.marketcrates.common.blocks.states.CrateType;
import com.lfaoanl.marketcrates.common.blocks.states.CrateTypeProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public abstract class AbstractCrateBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    private static final Properties properties = Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final CrateTypeProperty TYPE = CrateTypeProperty.create("type");

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final Item inclinable = Items.STICK;

    private NonNullList<ItemOrientation> oldItems;

    public AbstractCrateBlock() {
        super(properties);

        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, CrateType.DEFAULT)
                .setValue(WATERLOGGED, false));


    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(TYPE);
        builder.add(WATERLOGGED);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }

        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }


    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    /**
     * Opacity 1 to fix the lighting on the block below
     *
     * @param state
     * @param worldIn
     * @param pos
     * @return
     */
    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean isWaterLogged = fluidstate.getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, isWaterLogged);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        boolean horizontal = false;

        if (state.getValue(FACING).getAxis() == Direction.Axis.X) {
            horizontal = true;
        }

        return state.getValue(TYPE).getShape(horizontal);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return super.getDrops(state, builder);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            // System.out.println("onReplaced");
            BlockEntity tileentity = world.getBlockEntity(pos);

            if (tileentity instanceof AbstractCrateBlockEntity) {
                if (state.getValue(TYPE).isDouble()) {
                    Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), getBlockItem());
                }
                Containers.dropContents(world, pos, (AbstractCrateBlockEntity) tileentity);
//                worldIn.updateComparatorOutputLevel(pos, this); TODO

                this.oldItems = ((AbstractCrateBlockEntity) world.getBlockEntity(pos)).getItems();
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    public ItemStack getBlockItem() {
        return new ItemStack(Item.byBlock(this));
    }

    //@Override
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
        if (state.getValue(TYPE).isDouble()) {
            for (int i = 0; i < 12; i++) {
                Containers.dropItemStack((Level) world, pos.getX(), pos.getY(), pos.getZ(), oldItems.get(i).getItemStack());
            }
            oldItems = null;
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {

        CrateType crateType = state.getValue(TYPE);
        ItemStack heldItem = player.getMainHandItem();
        if (!crateType.isDouble() && player.isCreative() && heldItem.getItem() == inclinable) {
            world.setBlockAndUpdate(pos, state.setValue(TYPE, crateType.opposite()));
            return InteractionResult.CONSUME;
        }

        if (!world.isClientSide) {

            openGui(state, world, player, pos);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.SUCCESS;
    }

    protected abstract void openGui(BlockState state, Level world, Player player, BlockPos pos);

    @Override
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {
        CrateType crateType = state.getValue(TYPE);
        ItemStack heldItem = player.getMainHandItem();

        if (!player.isCreative() && !crateType.isDouble() && hasProperItem(crateType, heldItem)) {

            SoundEvent blockSound = SoundEvents.WOOD_BREAK;

            // Checks to see the current state and switches it
            if (crateType == CrateType.DEFAULT) {
                // Crate becomes inclined
                heldItem.setCount(heldItem.getCount() - 1);
                blockSound = SoundEvents.WOOD_PLACE;
            } else {
                // Crate becomes default
                player.drop(new ItemStack(inclinable), false);
            }

            playSound(world, pos, player, blockSound);
            world.setBlockAndUpdate(pos, state.setValue(TYPE, crateType.opposite()));
        }
    }

    public void playSound(Level world, BlockPos pos, Player player, SoundEvent blockSound) {
        world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), blockSound, SoundSource.BLOCKS, 0.8f, 1.5f);
    }

    /**
     * Checks to see if the user has te proper item to turn an inclined crate back to a normal crate
     *
     * @param heldItem
     * @return
     */
    private boolean hasProperTool(ItemStack heldItem) {
        return heldItem.getItem() instanceof AxeItem;
    }

    /**
     * Checks to see if the user is holding the inclinable item (stick)
     * Or when the crate is already inclined if it is holding the hasProperTool
     *
     * @param type
     * @param heldItem
     * @return
     */
    private boolean hasProperItem(CrateType type, ItemStack heldItem) {
        if (type == CrateType.INCLINED) {
            return hasProperTool(heldItem);
        }
        return heldItem.getItem() == inclinable && heldItem.getCount() >= 1;
    }

    /**
     * Override de BaseBlockEntity's method which renders the model INVISIBLE
     *
     * @param blockState
     * @return
     */
    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }


}