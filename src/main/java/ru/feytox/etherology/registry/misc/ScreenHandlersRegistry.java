package ru.feytox.etherology.registry.misc;

import lombok.experimental.UtilityClass;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import ru.feytox.etherology.block.closet.ClosetScreenHandler;
import ru.feytox.etherology.block.crate.CrateScreenHandler;
import ru.feytox.etherology.block.empowerTable.EmpowerTableScreenHandler;
import ru.feytox.etherology.block.etherealFurnace.EtherealFurnaceScreenHandler;
import ru.feytox.etherology.block.etherealStorage.EtherealStorageScreenHandler;
import ru.feytox.etherology.block.inventorTable.InventorTableScreenHandler;
import ru.feytox.etherology.block.jewelryTable.JewelryTableScreenHandler;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class ScreenHandlersRegistry {

    public static final ScreenHandlerType<ClosetScreenHandler> CLOSET_SCREEN_HANDLER = createType(ClosetScreenHandler::new);
    public static final ScreenHandlerType<EtherealStorageScreenHandler> ETHEREAL_STORAGE_SCREEN_HANDLER = createType(EtherealStorageScreenHandler::new);
    public static final ScreenHandlerType<EtherealFurnaceScreenHandler> ETHEREAL_FURNACE_SCREEN_HANDLER = createType(EtherealFurnaceScreenHandler::new);
    public static final ScreenHandlerType<EmpowerTableScreenHandler> EMPOWER_TABLE_SCREEN_HANDLER = createType(EmpowerTableScreenHandler::new);
    public static final ScreenHandlerType<CrateScreenHandler> CRATE_SCREEN_HANDLER = createType(CrateScreenHandler::new);
    public static final ScreenHandlerType<InventorTableScreenHandler> INVENTOR_TABLE_SCREEN_HANDLER = createType(InventorTableScreenHandler::new);
    public static final ScreenHandlerType<JewelryTableScreenHandler> JEWELRY_TABLE_SCREEN_HANDLER = createType(JewelryTableScreenHandler::new);

    public static void registerServerSide() {
        registerHandler("closet_screen_handler", CLOSET_SCREEN_HANDLER);
        registerHandler("ethereal_storage_screen_handler", ETHEREAL_STORAGE_SCREEN_HANDLER);
        registerHandler("ethereal_furnace_screen_handler", ETHEREAL_FURNACE_SCREEN_HANDLER);
        registerHandler("empower_table_screen_handler", EMPOWER_TABLE_SCREEN_HANDLER);
        registerHandler("crate_screen_handler", CRATE_SCREEN_HANDLER);
        registerHandler("inventor_table_screen_handler", INVENTOR_TABLE_SCREEN_HANDLER);
        registerHandler("jewelry_table_screen_handler", JEWELRY_TABLE_SCREEN_HANDLER);
    }

    private static void registerHandler(String id, ScreenHandlerType<?> screenHandlerType) {
        Registry.register(Registries.SCREEN_HANDLER, EIdentifier.of(id), screenHandlerType);
    }
    
    private static <T extends ScreenHandler> ScreenHandlerType<T> createType(ScreenHandlerType.Factory<T> factory) {
        return new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES);
    }
}
