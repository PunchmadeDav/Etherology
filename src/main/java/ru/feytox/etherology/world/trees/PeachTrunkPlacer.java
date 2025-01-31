package ru.feytox.etherology.world.trees;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static ru.feytox.etherology.registry.world.TreesRegistry.PEACH_TRUNK_PLACER;

public class PeachTrunkPlacer extends TrunkPlacer {

    public static final MapCodec<PeachTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            fillTrunkPlacerFields(instance).apply(instance, PeachTrunkPlacer::new));

    // TODO: 18.06.2024 replace arrays with something better
    private static final Direction[] OFFSETS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private static final Vec3i[] OFFSETS_CORNERS = {new Vec3i(-1, 0, 0), new Vec3i(1, 0, 0), new Vec3i(0, 0, -1), new Vec3i(0, 0, 1), new Vec3i(-1, 0, -1), new Vec3i(-1, 0, 1), new Vec3i(1, 0, -1), new Vec3i(1, 0, 1)};

    public PeachTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return PEACH_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
        setToDirt(world, replacer, random, startPos.down(), config);

        List<Direction> offsets = new ObjectArrayList<>(OFFSETS);
        Collections.shuffle(offsets);
        int mainLimit = MathHelper.ceil(height * (0.4f + 0.2f * random.nextFloat()));
        int branchCount = 0;
        int nextBranch = random.nextBetween(2, 3);
        ImmutableList.Builder<FoliagePlacer.TreeNode> leaves = new ImmutableList.Builder<>();

        for (int dy = 0; dy < mainLimit; dy++) {
            getAndSetState(world, replacer, random, startPos.up(dy), config);
            if (tryPlaceBranch(world, replacer, random, startPos, config, branchCount, dy, nextBranch, leaves, offsets)) continue;
            branchCount += 1;
            nextBranch = dy + random.nextBetween(2, 3);
        }

        Vec3i offset = getRandomOffset(random, OFFSETS_CORNERS);
        BlockPos forkPos = startPos.add(offset);
        for (int dy = mainLimit - 1; dy < height; dy++) {
            getAndSetState(world, replacer, random, forkPos.up(dy), config);
            if (dy >= height - 2) continue;
            if (tryPlaceBranch(world, replacer, random, forkPos, config, branchCount, dy, nextBranch, leaves, offsets)) continue;
            branchCount += 1;
            nextBranch = dy + random.nextBetween(2, 3);
        }
        leaves.add(new FoliagePlacer.TreeNode(forkPos.up(height-2), 0, true));
        return leaves.build();
    }

    private boolean tryPlaceBranch(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos startPos, TreeFeatureConfig config, int branchCount, int dy, int nextBranch, ImmutableList.Builder<FoliagePlacer.TreeNode> leaves, List<Direction> offsets) {
        if (dy < nextBranch) return true;

        Direction direction = offsets.get(branchCount % 4);
        BlockPos branchPos = startPos.add(direction.getVector()).up(dy);
        getAndSetState(world, replacer, random, branchPos, config, state -> state.withIfExists(PillarBlock.AXIS, direction.getAxis()));
        leaves.add(new FoliagePlacer.TreeNode(branchPos, 0, false));
        return false;
    }

    private Vec3i getRandomOffset(Random random, Vec3i... offsets) {
        return offsets[random.nextInt(offsets.length)];
    }
}
