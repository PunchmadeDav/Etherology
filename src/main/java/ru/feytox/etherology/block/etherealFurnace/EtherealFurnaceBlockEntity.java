package ru.feytox.etherology.block.etherealFurnace;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.data.ethersource.EtherSources;
import ru.feytox.etherology.magic.ether.EtherCounter;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

import java.util.Collections;
import java.util.List;

import static ru.feytox.etherology.block.etherealFurnace.EtherealFurnace.LIT;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_FURNACE_BLOCK_ENTITY;

public class EtherealFurnaceBlockEntity extends TickableBlockEntity implements EtherStorage, ImplementedInventory, NamedScreenHandlerFactory, EtherCounter, SidedInventory {

    public static final int MAX_FUEL = 8;
    public static final int DEFAULT_COOK_TIME = 20*15;
    private float storedEther;
    // 0 - fuel, 1 - item, 2 - ether
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private int fuel;
    private int cookTime;
    private int totalCookTime;
    private boolean isUpdated;
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return fuel;
                }
                case 1 -> {
                    return cookTime;
                }
                case 2 -> {
                    return totalCookTime;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> fuel = value;
                case 1 -> cookTime = value;
                case 2 -> totalCookTime = value;
            }
        }

        @Override
        public int size() {
            return 3;
        }
    };

    public EtherealFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEREAL_FURNACE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        tickEtherCount(world);
        cookingTick(world, state);
        transferTick(world);

        if (isUpdated) {
            world.getChunkManager().markForUpdate(pos);
            isUpdated = false;
        }
    }

    public void cookingTick(ServerWorld world, BlockState state) {
        if (fuel == 0) tryConsumeFuel();
        boolean isCooking = isCooking();
        boolean stateChanged = false;
        if (isCooking) cookTime++;

        if (!isCooking && isCookingValid()) {
            // старт эфирования
            totalCookTime = DEFAULT_COOK_TIME;
            cookTime = 1;
            stateChanged = true;
            markDirty();
        } else if (isCooking && !isCookingValid()) {
            // остановка эфирования
            totalCookTime = 0;
            stateChanged = true;
            markDirty();
        }

        if (isDegrade()) {
            // уменьшение шкалы после остановки
            cookTime = Math.max(0, cookTime - 2);
            if (cookTime == 0) stateChanged = true;
            markDirty();
        }

        if (isCooking && cookTime >= totalCookTime && totalCookTime != 0 && isEnoughSpace()) {
            // завершение эфирования
            ItemStack consumedItem = getStack(1);
            float etherPoints = EtherSources.getEtherFuel(consumedItem.getItem());
            increment(etherPoints);
            consumedItem.decrement(1);

            cookTime = 0;
            totalCookTime = 0;
            fuel--;
            stateChanged = !isCookingValid();
            updateCount();
            markDirty();
        }

        if (stateChanged) {
            BlockState newState = state.with(LIT, isCooking() || isDegrade());
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
            markDirty();
        }
    }

    @Override
    public void markDirty() {
        isUpdated = true;
        super.markDirty();
    }

    public boolean isDegrade() {
        return totalCookTime == 0 && cookTime > 0;
    }

    public boolean isCooking() {
        return totalCookTime != 0;
    }

    public boolean isCookingValid() {
        return fuel != 0 && EtherSources.isEtherSource(getStack(1).getItem());
    }

    public void tryConsumeFuel() {
        ItemStack fuelStack = getStack(0);
        if (!fuelStack.isOf(Items.BLAZE_POWDER)) return;

        fuel = MAX_FUEL;
        fuelStack.decrement(1);
        markDirty();
    }

    public boolean isEnoughSpace() {
        return EtherSources.getEtherFuel(getStack(0).getItem()) + storedEther <= getMaxEther();
    }

    @Override
    public float getMaxEther() {
        return 64;
    }

    @Override
    public float getStoredEther() {
        return storedEther;
    }

    @Override
    public float getTransferSize() {
        return 1;
    }

    @Override
    public void setStoredEther(float value) {
        storedEther = value;
    }

    @Override
    public boolean isInputSide(Direction side) {
        return false;
    }

    @Nullable
    @Override
    public Direction getOutputSide() {
        return Direction.DOWN;
    }

    @Override
    public BlockPos getStoragePos() {
        return pos;
    }

    @Override
    public void transferTick(ServerWorld world) {
        if (world.getTime() % 10 == 0) transfer(world);
    }

    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putFloat("stored_ether", storedEther);
        nbt.putInt("fuel", fuel);
        nbt.putInt("cook_time", cookTime);
        nbt.putInt("total_cook_time", totalCookTime);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        storedEther = nbt.getFloat("stored_ether");
        inventory.clear();
        Inventories.readNbt(nbt, inventory, registryLookup);
        fuel = nbt.getInt("fuel");
        cookTime = nbt.getInt("cook_time");
        totalCookTime = nbt.getInt("total_cook_time");
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EtherealFurnaceScreenHandler(syncId, inv, this, propertyDelegate);
    }

    @Override
    public float getEtherCount() {
        return storedEther;
    }

    @Override
    public List<Integer> getCounterSlots() {
        return Collections.singletonList(2);
    }

    @Override
    public Inventory getInventoryForCounter() {
        return this;
    }

    @Override
    public void tickEtherCount(ServerWorld world) {
        if (world.getTime() % 5 == 0) updateCount();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        switch (side) {
            case UP -> {
                return new int[]{1};
            }
            case NORTH, SOUTH, EAST, WEST -> {
                return new int[]{0};
            }
        }
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir == null) return false;

        switch (dir) {
            case UP -> {
                return true;
            }
            case NORTH, SOUTH, EAST, WEST -> {
                return stack.isOf(Items.BLAZE_POWDER);
            }
        }
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }
}
