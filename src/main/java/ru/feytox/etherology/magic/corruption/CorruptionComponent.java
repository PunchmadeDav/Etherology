package ru.feytox.etherology.magic.corruption;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.util.EtherologyComponents;

@RequiredArgsConstructor
public class CorruptionComponent implements ServerTickingComponent, AutoSyncedComponent {
    private static final int INFECTION_TICK_RATE = 60*20;
    private static final int EVAPORATION_TICK_RATE = 20;
    private static final float EVAPORATION_SPEED = 1/3f;
    private static final float EVAPORATION_DELTA = 14;
    private static final float MIN_CORRUPTION = 1/256f;
    private static final float INFECTION_LIMIT = 48.0f;
    public static final float MAX_CHUNK_CORRUPTION = 64.0f;

    private final Chunk chunk;

    @Nullable
    @Getter
    private Corruption corruption;

    private int ticks;

    public void setCorruption(@Nullable Corruption corruption) {
        this.corruption = corruption;
        save();
    }

    public void save() {
        this.chunk.setNeedsSaving(true);
        EtherologyComponents.CORRUPTION.sync(chunk);
    }

    public void increment(float value) {
        if (value <= MIN_CORRUPTION) return;
        setCorruption(corruption == null ? new Corruption(value) : corruption.increment(value));
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound nbt) {
        NbtCompound corruptionNbt = nbt.getCompound("Corruption");
        corruption = Corruption.readFromNbt(corruptionNbt);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound nbt) {
        NbtCompound corruptionNbt = new NbtCompound();
        if (corruption != null && !corruption.isEmpty()) corruption.writeNbt(corruptionNbt);
        nbt.put("Corruption", corruptionNbt);
    }

    @Override
    public void serverTick() {
        ticks++;
        if (tickInfection() && tickEvaporation()) return;

        save();
    }

    private boolean tickInfection() {
        if (ticks % INFECTION_TICK_RATE != 0) return true;
        if (corruption == null) return true;

        float corruptionLevel = corruption.getCorruptionValue();
        if (corruptionLevel <= INFECTION_LIMIT) return true;
        if (!(chunk instanceof WorldChunk worldChunk)) return true;

        World world = worldChunk.getWorld();
        float tipValue = Math.min(corruptionLevel - INFECTION_LIMIT, 1.0f);
        ChunkPos chunkPos = worldChunk.getPos();
        increment(-tipValue);

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == z || x == -z) continue;
                Chunk sideChunk = world.getChunk(x + chunkPos.x, z + chunkPos.z);
                CorruptionComponent component = sideChunk.getComponent(EtherologyComponents.CORRUPTION);
                component.increment(tipValue / 4);
            }
        }
        return false;
    }

    private boolean tickEvaporation() {
        if (ticks % EVAPORATION_TICK_RATE != 0) return true;
        if (corruption == null) return true;

        float value = corruption.getCorruptionValue();
        if (ticks % (1200 * EVAPORATION_TICK_RATE * EVAPORATION_SPEED * (value + EVAPORATION_DELTA)) != 0) return true;

        increment(-1);
        return false;
    }
}
