package ru.feytox.etherology.block.etherealStorage;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.misc.RegistrableBlock;

import java.util.List;

import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_STORAGE_BLOCK_ENTITY;

public class EtherealStorageBlock extends HorizontalFacingBlock implements RegistrableBlock, BlockEntityProvider {

    protected static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final MapCodec<EtherealStorageBlock> CODEC = MapCodec.unit(EtherealStorageBlock::new);

    public EtherealStorageBlock() {
        super(Settings.copy(Blocks.STONE).nonOpaque());
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = (NamedScreenHandlerFactory) world.getBlockEntity(pos);
            if (screenHandlerFactory != null) player.openHandledScreen(screenHandlerFactory);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof EtherealStorageBlockEntity etherStorage) {
                List<ItemStack> stacks = etherStorage.getItems().subList(0, 4);
                for (int i = 0; i < 3; i++) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stacks.get(i));
                }
                etherStorage.clear();
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public String getBlockId() {
        return "ethereal_storage";
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherealStorageBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ETHEREAL_STORAGE_BLOCK_ENTITY) return null;

        return world.isClient ? null : EtherealStorageBlockEntity::serverTicker;
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }
}
