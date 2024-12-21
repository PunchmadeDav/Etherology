package ru.feytox.etherology.util.misc;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter @Setter
public abstract class TickableBlockEntity extends BlockEntity {

    @Nullable
    private ClientSideObject clientTickableBlock;

    public TickableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {}

    // TODO: 28.01.2024 replace block's getTicker to this
    public static <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockEntityType<T> providedType, BlockEntityType<?> type) {
        if (providedType != type) return null;
        return world.isClient ? TickableBlockEntity::clientTicker : TickableBlockEntity::serverTicker;
    }

    public static void clientTicker(World world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof TickableBlockEntity be) {
            EtherProxy.getInstance().tryTickBlockEntity(be, world, blockPos, state);
        }
    }

    public Optional<Class<? extends TickableBlockEntity>> getClientTickerProvider() {
        return Optional.empty();
    }

    public static void serverTicker(World world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof TickableBlockEntity be) {
            be.serverTick((ServerWorld) world, blockPos, state);
        }
    }

    public void syncData(ServerWorld world) {
        markDirty();
        world.getChunkManager().markForUpdate(pos);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
