package ru.feytox.etherology.block.jewelryTable;

import io.wispforest.owo.util.ImplementedInventory;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.corruption.Corruption;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.particle.effects.ElectricityParticleEffect;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;
import ru.feytox.etherology.particle.effects.SparkParticleEffect;
import ru.feytox.etherology.particle.subtype.ElectricitySubtype;
import ru.feytox.etherology.particle.subtype.SparkSubtype;
import ru.feytox.etherology.recipes.jewelry.AbstractJewelryRecipe;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;
import ru.feytox.etherology.util.delayedTask.DelayedTask;
import ru.feytox.etherology.util.misc.TickableBlockEntity;
import ru.feytox.etherology.util.misc.UniqueProvider;

import static ru.feytox.etherology.registry.block.EBlocks.JEWELRY_TABLE_BLOCK_ENTITY;

public class JewelryBlockEntity extends TickableBlockEntity implements EtherStorage, ImplementedInventory, UniqueProvider, NamedScreenHandlerFactory, SidedInventory {

    private static final int TICK_RATE = 10;
    private static final int IDLE_TICK_RATE = 7;

    @Getter
    private final JewelryTableInventory inventory;
    private float storedEther = 0;
    @Getter @Setter
    private Float cachedUniqueOffset = null;
    @Nullable
    private DelayedTask currentTask = null;

    public JewelryBlockEntity(BlockPos pos, BlockState state) {
        super(JEWELRY_TABLE_BLOCK_ENTITY, pos, state);
        inventory = new JewelryTableInventory(this);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        int tickRate = TICK_RATE;
        if (inventory.isEmpty() || !inventory.hasRecipe()) {
            inventory.resetRecipe();
            tickRate = IDLE_TICK_RATE;
        }
        if (world.getTime() % tickRate == 0) decrement(0.2f);
        if (!inventory.hasRecipe() || world.getTime() % 5 != 0) return;

        inventory.updateRecipe(world);
        AbstractJewelryRecipe recipe = inventory.getRecipe(world);
        if (recipe == null) return;
        if (storedEther < recipe.getEther()) return;

        storedEther = 0.0f;
        inventory.tryCraft(world);
        decrement(recipe.getEther());
        inventory.resetRecipe();
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }

        Vec3d particlePos = blockPos.toCenterPos().add(0, 0.75d, 0);
        val effect = new SparkParticleEffect(EtherParticleTypes.SPARK, new Vec3d(0, 2.0d, 0), SparkSubtype.JEWELRY);
        effect.spawnParticles(world, 6, 0.25d, particlePos);
    }

    public void applyCorruption() {
        if (!(world instanceof ServerWorld serverWorld)) return;
        Corruption corruption = new Corruption(1.0f);
        corruption.placeInChunk(serverWorld, pos);

        Vec3d start = pos.toCenterPos().add(0, 0.75f, 0);
        val effect = new SimpleParticleEffect(EtherParticleTypes.HAZE);
        effect.spawnParticles(serverWorld, 5, 0.2, start);
    }

    @Override
    public boolean isCrossEvaporate(Direction fromSide) {
        return fromSide.equals(Direction.DOWN) && !inventory.hasRecipe();
    }

    @Override
    public boolean spawnCrossParticles(BlockPos pos, World world, Direction direction) {
        if (inventory.isEmpty()) return false;
        if (world.getTime() % 4 != 0) return true;

        val effect = ElectricityParticleEffect.of(world.getRandom(), ElectricitySubtype.JEWELRY);
        effect.spawnParticles(world, 2, 0.2d, pos.toCenterPos().add(0, 0.75d, 0));
        return true;
    }

    @Override
    public float getMaxEther() {
        return inventory.hasRecipe() ? Integer.MAX_VALUE : 4.0f;
    }

    @Override
    public float getStoredEther() {
        return storedEther;
    }

    @Override
    public float getTransferSize() {
        return 1.0f;
    }

    @Override
    public void setStoredEther(float value) {
        storedEther = value;
        trySyncData();
    }

    @Override
    public boolean isInputSide(Direction side) {
        return side.equals(Direction.DOWN);
    }

    @Override
    public @Nullable Direction getOutputSide() {
        return null;
    }

    @Override
    public BlockPos getStoragePos() {
        return pos;
    }

    @Override
    public void transferTick(ServerWorld world) {}

    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory.getItems();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        inventory.writeNbt(nbt, registryLookup);
        nbt.putFloat("ether", storedEther);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        inventory.readNbt(nbt, registryLookup);
        storedEther = nbt.getFloat("ether");
    }

    public void trySyncData() {
        markDirty();
        if (world instanceof ServerWorld serverWorld) syncData(serverWorld);
    }

    @Override
    public Text getDisplayName() {
        return Text.empty();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new JewelryTableScreenHandler(syncId, inv, inventory);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }
}
