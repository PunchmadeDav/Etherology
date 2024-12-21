package ru.feytox.etherology.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryWrapper;
import ru.feytox.etherology.block.etherealChannel.EtherealChannel;
import ru.feytox.etherology.registry.block.AutoBlockLootTable;
import ru.feytox.etherology.registry.item.DecoBlockItems;
import ru.feytox.etherology.util.misc.RandomChanceWithFortuneCondition;

import java.util.concurrent.CompletableFuture;

import static ru.feytox.etherology.registry.block.DecoBlocks.*;
import static ru.feytox.etherology.registry.block.EBlocks.CHANNEL_CASE;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_CHANNEL;
import static ru.feytox.etherology.registry.item.DecoBlockItems.ENRICHED_ATTRAHITE;
import static ru.feytox.etherology.registry.item.DecoBlockItems.THUJA_SEEDS;

public class BlockLootTableGeneration extends FabricBlockLootTableProvider {

    protected BlockLootTableGeneration(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        AutoBlockLootTable.acceptData((block, drop) -> {
            if (drop == null) addDrop(block);
            else addDrop(block, drop);
        });

        addDrop(PEACH_DOOR, doorDrops(PEACH_DOOR));
        addDrop(PEACH_SIGN, DecoBlockItems.PEACH_SIGN);
        addDrop(PEACH_WALL_SIGN, DecoBlockItems.PEACH_SIGN);
        addDrop(PEACH_HANGING_SIGN, DecoBlockItems.PEACH_HANGING_SIGN);
        addDrop(PEACH_WALL_HANGING_SIGN, DecoBlockItems.PEACH_HANGING_SIGN);
        addDrop(PEACH_LEAVES, leavesDrops(PEACH_LEAVES, PEACH_SAPLING, SAPLING_DROP_CHANCE));
        addDrop(WEEPING_PEACH_LOG, drops(WEEPING_PEACH_LOG, PEACH_LOG));

        addDrop(ATTRAHITE, dropsWithSilkTouch(ATTRAHITE, applyExplosionDecay(ATTRAHITE, ItemEntry.builder(ENRICHED_ATTRAHITE).conditionally(RandomChanceWithFortuneCondition.builder(registryLookup, 0.02F, 0.02F)))));
        generateChannelDrop();

        addDrop(BEAMER, DecoBlockItems.BEAM_FRUIT);
        addDrop(THUJA, THUJA_SEEDS);
        addDrop(THUJA_PLANT, THUJA_SEEDS);
        addPottedPlantDrops(POTTED_BEAMER);
        addPottedPlantDrops(POTTED_THUJA);
        addPottedPlantDrops(POTTED_PEACH_SAPLING);

        addDrop(LIGHTELET, this::shortPlantDrops);
    }

    private void generateChannelDrop() {
        addDrop(ETHEREAL_CHANNEL, LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0F))
                        .with(this.applyExplosionDecay(ETHEREAL_CHANNEL, ItemEntry.builder(ETHEREAL_CHANNEL))))
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0F))
                        .with(this.applyExplosionDecay(CHANNEL_CASE, ItemEntry.builder(CHANNEL_CASE)))
                        .conditionally(BlockStatePropertyLootCondition.builder(ETHEREAL_CHANNEL)
                                .properties(StatePredicate.Builder.create().exactMatch(EtherealChannel.IN_CASE, true)))));
    }
}
