package ru.feytox.etherology.registry.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.item.BattlePickaxe;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import static net.minecraft.item.ToolMaterials.*;
import static ru.feytox.etherology.registry.util.EtherToolMaterials.ETHRIL;
import static ru.feytox.etherology.registry.util.EtherToolMaterials.TELDER_STEEL;

public class ToolItems {
    // ethril tools
    public static Item ETHRIL_AXE = register("ethril_axe", new AxeItem(ETHRIL, 5, -3, new FabricItemSettings()));
    public static Item ETHRIL_PICKAXE = register("ethril_pickaxe", new PickaxeItem(ETHRIL, 0, -2.8F, new FabricItemSettings()));
    public static Item ETHRIL_HOE = register("ethril_hoe", new HoeItem(ETHRIL, 0, -3, new FabricItemSettings()));
    public static Item ETHRIL_SHOVEL = register("ethril_shovel", new ShovelItem(ETHRIL, 0.5f, -3, new FabricItemSettings()));
    public static Item ETHRIL_SWORD = register("ethril_sword", new SwordItem(ETHRIL, 3, -2.4f, new FabricItemSettings()));
    public static Item ETHRIL_BATTLE_PICKAXE = register("ethril_battle_pickaxe", new BattlePickaxe(ETHRIL, 1, -2.6f));
    public static Item ETHRIL_HAMMER = register("ethril_hammer", new HammerItem(ETHRIL, 8, -3.4f));

    // telder steel tools
    public static Item TELDER_STEEL_AXE = register("telder_steel_axe", new AxeItem(TELDER_STEEL, 5, -3.1F, new FabricItemSettings()));
    public static Item TELDER_STEEL_PICKAXE = register("telder_steel_pickaxe", new PickaxeItem(TELDER_STEEL, 0, -2.8F, new FabricItemSettings()));
    public static Item TELDER_STEEL_HOE = register("telder_steel_hoe", new HoeItem(TELDER_STEEL, -2, -1, new FabricItemSettings()));
    public static Item TELDER_STEEL_SHOVEL = register("telder_steel_shovel", new ShovelItem(TELDER_STEEL, 0.5f, -3, new FabricItemSettings()));
    public static Item TELDER_STEEL_SWORD = register("telder_steel_sword", new SwordItem(TELDER_STEEL, 3, -2.4f, new FabricItemSettings()));
    public static Item TELDER_STEEL_BATTLE_PICKAXE = register("telder_steel_battle_pickaxe", new BattlePickaxe(TELDER_STEEL, 1, -2.6f));
    public static Item TELDER_STEEL_HAMMER = register("telder_steel_hammer", new HammerItem(TELDER_STEEL, 8, -3.3f));

    // vanilla battle pickaxes
    public static Item WOODEN_BATTLE_PICKAXE = register("wooden_battle_pickaxe", new BattlePickaxe(WOOD, 2, -2.6f));
    public static Item STONE_BATTLE_PICKAXE = register("stone_battle_pickaxe", new BattlePickaxe(STONE, 2, -2.6f));
    public static Item IRON_BATTLE_PICKAXE = register("iron_battle_pickaxe", new BattlePickaxe(IRON, 2, -2.6f));
    public static Item GOLDEN_BATTLE_PICKAXE = register("golden_battle_pickaxe", new BattlePickaxe(GOLD, 2, -2.6f));
    public static Item DIAMOND_BATTLE_PICKAXE = register("diamond_battle_pickaxe", new BattlePickaxe(DIAMOND, 2, -2.6f));
    public static Item NETHERITE_BATTLE_PICKAXE = register("netherite_battle_pickaxe", new BattlePickaxe(NETHERITE, 2, -2.6f));

    // vanilla hammers
    public static Item WOODEN_HAMMER = register("wooden_hammer", new HammerItem(WOOD, 9, -3.5f));
    public static Item STONE_HAMMER = register("stone_hammer", new HammerItem(STONE, 8, -3.5f));
    public static Item IRON_HAMMER = register("iron_hammer", new HammerItem(IRON, 9, -3.4f));
    public static Item GOLDEN_HAMMER = register("golden_hammer", new HammerItem(GOLD, 9, -3.4f));
    public static Item DIAMOND_HAMMER = register("diamond_hammer", new HammerItem(DIAMOND, 8, -3.3f));
    public static Item NETHERITE_HAMMER = register("netherite_hammer", new HammerItem(NETHERITE, 7, -3.3f));

    private static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, new EIdentifier(id), item);
    }

    public static void registerAll() {}
}
