package ru.feytox.etherology.enums;

import io.wispforest.owo.ui.core.Color;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.block.armillar.ArmillaryMatrixBlockEntity;
import ru.feytox.etherology.block.pedestal.PedestalBlockEntity;
import ru.feytox.etherology.particle.utility.SmallLightning;
import ru.feytox.etherology.util.deprecated.FakeItem;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum InstabTypes {
    NULL(0.0D, Color.GREEN),
    VERY_LOW(0.05D, 0.2D, Color.GREEN),
    LOW(0.1D, 0.05D, 0.03D, Color.GREEN),
    MEDIUM(0.1D, 0.1D, 0.06D, 0.02D, Color.GREEN),
    HIGH(0.15D, 0.2D, 0.1D, 0.05D, 0.01D, Color.BLUE),
    VERY_HIGH(0.2D, 0.25D, 0.2D, 0.1D, 0.05D, Color.RED);

    // выброс нестабильности в мир
    private final double chance1;
    // микро-молнии
    private final double chance2;
    // выпад предмета
    private final double chance3;
    // микро-взрывы
    private final double chance4;
    // удаление предмета
    private final double chance5;
    private final Color textColor;

    InstabTypes(double chance1, double chance2, double chance3, double chance4, double chance5, Color textColor) {
        this.chance1 = chance1;
        this.chance2 = chance2;
        this.chance3 = chance3;
        this.chance4 = chance4;
        this.chance5 = chance5;
        this.textColor = textColor;
    }

    InstabTypes(double chance1, Color textColor) {
        this(chance1, 0.0d, 0.0d, 0.0d, 0.0d, textColor);
    }

    InstabTypes(double chance1, double chance2, Color textColor) {
        this(chance1, chance2, 0.0d, 0.0d, 0.0d, textColor);
    }

    InstabTypes(double chance1, double chance2, double chance3, Color textColor) {
        this(chance1, chance2, chance3, 0.0d, 0.0d, textColor);
    }

    InstabTypes(double chance1, double chance2, double chance3, double chance4, Color textColor) {
        this(chance1, chance2, chance3, chance4, 0.0d, textColor);
    }

    public String getLangKey() {
        return "instab_" + this.name().toLowerCase();
    }

    public Text getTranslation() {
        return Text.translatable(Etherology.MOD_ID + "." + this.getLangKey());
    }

    public Color getTextColor() {
        return textColor;
    }

    public static void registerInstabs() {
        List<InstabTypes> instabs = Arrays.stream(InstabTypes.values()).toList();
        instabs.forEach(instabType -> new FakeItem(instabType.getLangKey()).register());
    }

    public static InstabTypes getFromIndex(int index) {
        List<InstabTypes> instabs = Arrays.stream(InstabTypes.values()).toList();
        return instabs.size() > index ? instabs.get(index) : null;
    }

    public int getIndex() {
        List<InstabTypes> instabs = Arrays.stream(InstabTypes.values()).toList();
        return instabs.indexOf(this);
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("instabTypeInt", getIndex());
    }

    public static InstabTypes readNbt(NbtCompound nbt) {
        return getFromIndex(nbt.getInt("instabTypeInt"));
    }

    private boolean checkRandom(double chance, float multi) {
        Random rand = new Random();
        double r = rand.nextDouble();

        return r <= chance * multi;
    }

    public boolean event1(float multi, ServerWorld world) {
        if (!checkRandom(chance1, multi)) return false;

        // TODO: выброс нестабильности в мир
        List<ServerPlayerEntity> players = world.getPlayers();
        if (players.isEmpty()) return true;

        players.get(0).sendMessage(Text.of("Выброс нестабильности в мир"));
        return true;
    }

    public boolean event2(float multi, ServerWorld world, BlockPos pos, BlockState state) {
        if (!checkRandom(chance2, multi)) return false;

        // TODO: replace to Minecraft Random
        Random rand = new Random();

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ArmillaryMatrixBlockEntity armBlock)) return false;

        Vec3d randomPos = new Vec3d(pos.getX() + rand.nextInt(-8, 9), pos.getY(),
                pos.getZ() + rand.nextInt(-8, 9));

        SmallLightning smallLightning = new SmallLightning(armBlock.getCenterPos(), randomPos, 3,
                armBlock.getInstabilityMulti());
        smallLightning.spawn(world);

        return true;
    }

    public boolean event3(float multi, ServerWorld world, ArmillaryMatrixBlockEntity armillBlock) {
        if (!checkRandom(chance3, multi)) return false;

        Random rand = new Random();

        List<PedestalBlockEntity> pedestals = armillBlock.getNotEmptyPedestals(world, armillBlock.getPos(),
                world.getBlockState(armillBlock.getPos()));
        if (pedestals.isEmpty()) return false;
        PedestalBlockEntity pedestal = pedestals.get(rand.nextInt(pedestals.size()));
        BlockPos pedPos = pedestal.getPos();
        pedPos.add(0.0, 1.0, 0.0);
        ItemScatterer.spawn(world, pedPos, pedestal);
        pedestal.clear();

        return true;
    }

    public boolean event4(float multi, ServerWorld world, BlockPos pos) {
        if (!checkRandom(chance4, multi)) return false;

        Random rand = new Random();

        world.createExplosion(null, pos.getX() + rand.nextInt(-8, 9),
                pos.getY(), pos.getZ() + rand.nextInt(-8, 9), 1, World.ExplosionSourceType.NONE);

        return true;
    }

    public boolean event5(float multi, ServerWorld world, ArmillaryMatrixBlockEntity armillBlock) {
        if (!checkRandom(chance5, multi)) return false;

        Random rand = new Random();

        List<PedestalBlockEntity> pedestals = armillBlock.getNotEmptyPedestals(world, armillBlock.getPos(),
                world.getBlockState(armillBlock.getPos()));
        if (pedestals.isEmpty()) return false;
        PedestalBlockEntity pedestal = pedestals.get(rand.nextInt(pedestals.size()));
        pedestal.clear();

        return true;
    }
}
