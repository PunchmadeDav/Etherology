package ru.feytox.etherology.block.forestLantern;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import ru.feytox.etherology.util.misc.RegistrableBlock;

import java.util.EnumMap;
import java.util.Map;

/**
 * @see WallTorchBlock
 */
public class ForestLanternBlock extends HorizontalFacingBlock implements RegistrableBlock {

    private static final Map<Direction, VoxelShape> SHAPES;

    public ForestLanternBlock() {
        super(Settings.copy(Blocks.BROWN_MUSHROOM_BLOCK).luminance(value -> 8).postProcess((a, b, c) -> true).emissiveLighting((a, b, c) -> true));
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.get(FACING));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState();
        WorldView worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction[] directions = ctx.getPlacementDirections();

        for (Direction direction : directions) {
            if (!direction.getAxis().isHorizontal()) continue;

            state = state.with(FACING, direction.getOpposite());
            if (state.canPlaceAt(worldView, blockPos)) return state;
        }

        return null;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isSideSolidFullSquare(world, blockPos, direction);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : state;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public String getBlockId() {
        return "forest_lantern";
    }

    static {
        SHAPES = new EnumMap<>(ImmutableMap.of(
                Direction.NORTH, Block.createCuboidShape(2, 4, 4, 14, 16, 16),
                Direction.SOUTH, Block.createCuboidShape(2, 4, 0, 14, 16, 12),
                Direction.WEST, Block.createCuboidShape(4, 4, 2, 16, 16, 14),
                Direction.EAST, Block.createCuboidShape(0, 4, 2, 12, 16, 14)
        ));
    }
}