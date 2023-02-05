package name.uwu.feytox.etherology.util.registry;

import name.uwu.feytox.etherology.util.feyapi.EIdentifier;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface IFeyBlock {
    String getBlockId();
    Block getBlock();

    default Block registerAll() {
        Block result = registerBlock();
        registerItem();
        return result;
    }

    default Block registerBlock() {
        return Registry.register(Registries.BLOCK, new EIdentifier(getBlockId()), getBlock());
    }

    default BlockItem registerItem() {
        return Registry.register(Registries.ITEM, new EIdentifier(getBlockId() + "_item"),
                new BlockItem(getBlock(), new FabricItemSettings()));
    }
}
