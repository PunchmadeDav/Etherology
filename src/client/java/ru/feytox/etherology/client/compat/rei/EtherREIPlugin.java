package ru.feytox.etherology.client.compat.rei;


import dev.architectury.event.CompoundEventResult;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.screen.DisplayBoundsProvider;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.categories.crafting.filler.CraftingRecipeFiller;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.client.block.empowerTable.EmpowerTableScreen;
import ru.feytox.etherology.client.compat.rei.category.*;
import ru.feytox.etherology.client.compat.rei.display.*;
import ru.feytox.etherology.client.compat.rei.filler.StaffCarpetCuttingFiller;
import ru.feytox.etherology.client.compat.rei.filler.StaffCarpetingFiller;
import ru.feytox.etherology.client.compat.rei.misc.AspectEntryDefinition;
import ru.feytox.etherology.client.compat.rei.misc.AspectPair;
import ru.feytox.etherology.client.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.client.gui.teldecore.misc.FeyIngredient;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.recipes.alchemy.AlchemyRecipe;
import ru.feytox.etherology.recipes.alchemy.AlchemyRecipeSerializer;
import ru.feytox.etherology.recipes.empower.EmpowerRecipe;
import ru.feytox.etherology.recipes.empower.EmpowerRecipeSerializer;
import ru.feytox.etherology.recipes.jewelry.LensRecipe;
import ru.feytox.etherology.recipes.jewelry.LensRecipeSerializer;
import ru.feytox.etherology.recipes.jewelry.ModifierRecipe;
import ru.feytox.etherology.recipes.jewelry.ModifierRecipeSerializer;
import ru.feytox.etherology.recipes.matrix.MatrixRecipe;
import ru.feytox.etherology.recipes.matrix.MatrixRecipeSerializer;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.util.misc.CountedAspect;
import ru.feytox.etherology.util.misc.EIdentifier;

public class EtherREIPlugin implements REIClientPlugin {

    public static final CategoryIdentifier<EmpowerDisplay> EMPOWERMENT = id("empowerment");
    public static final CategoryIdentifier<InventorDisplay> INVENTOR = id("inventor");
    public static final CategoryIdentifier<JewelryDisplay.Lens> JEWELRY_LENS = id("jewelry_lens");
    public static final CategoryIdentifier<JewelryDisplay.Modifier> JEWELRY_MODIFIER = id("jewelry_modifier");
    public static final CategoryIdentifier<AspectionDisplay> ASPECTION = id("aspection");
    public static final CategoryIdentifier<AlchemyDisplay> ALCHEMY = id("alchemy");
    public static final CategoryIdentifier<MatrixDisplay> MATRIX = id("matrix");

    public static final EntryType<AspectPair> ASPECT_ENTRY = EntryType.deferred(EIdentifier.of("aspect"));

    private static final CraftingRecipeFiller<?>[] CRAFTING_RECIPE_FILLERS = {
            new StaffCarpetingFiller(), new StaffCarpetCuttingFiller()
    };

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new EmpowerCategory(), new InventorCategory(), new JewelryCategory.Lens(), new JewelryCategory.Modifier(),
                new AspectionCategory(), new AlchemyCategory(), new MatrixCategory());

        registry.addWorkstations(EMPOWERMENT, EntryStacks.of(EBlocks.EMPOWERMENT_TABLE));
        registry.addWorkstations(INVENTOR, EntryStacks.of(EBlocks.INVENTOR_TABLE));
        registry.addWorkstations(JEWELRY_LENS, EntryStacks.of(EBlocks.JEWELRY_TABLE));
        registry.addWorkstations(JEWELRY_MODIFIER, EntryStacks.of(EBlocks.JEWELRY_TABLE));
        registry.addWorkstations(ASPECTION, EntryStacks.of(ToolItems.OCULUS));
        registry.addWorkstations(ALCHEMY, EntryStacks.of(EBlocks.BREWING_CAULDRON));
        registry.addWorkstations(MATRIX, EntryStacks.of(EBlocks.ARMILLARY_SPHERE));

        for (CraftingRecipeFiller<?> filler : CRAFTING_RECIPE_FILLERS) {
            filler.registerCategories(registry);
        }
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(EmpowerRecipe.class, EmpowerRecipeSerializer.INSTANCE.getRecipeType(), EmpowerDisplay::of);
        registry.registerRecipeFiller(LensRecipe.class, LensRecipeSerializer.INSTANCE.getRecipeType(), JewelryDisplay.Lens::of);
        registry.registerRecipeFiller(ModifierRecipe.class, ModifierRecipeSerializer.INSTANCE.getRecipeType(), JewelryDisplay.Modifier::of);
        registry.registerRecipeFiller(AlchemyRecipe.class, AlchemyRecipeSerializer.INSTANCE.getRecipeType(), AlchemyDisplay::of);
        registry.registerRecipeFiller(MatrixRecipe.class, MatrixRecipeSerializer.INSTANCE.getRecipeType(), MatrixDisplay::of);
        InventorDisplay.registerFillers(registry);
        AspectionDisplay.registerFillers(registry);

        for (CraftingRecipeFiller<?> filler : CRAFTING_RECIPE_FILLERS) {
            filler.registerDisplays(registry);
        }
    }

    @Override
    public void registerEntryTypes(EntryTypeRegistry registry) {
        registry.register(ASPECT_ENTRY, new AspectEntryDefinition());
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        for (Aspect aspect : Aspect.values()) {
            registry.addEntry(AspectPair.entry(aspect, 1));
        }
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
        registry.group(EIdentifier.of("aspects"), Text.translatable("gui.etherology.aspects"), entry -> entry.getType().equals(ASPECT_ENTRY));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(88, 32, 28, 23), EmpowerTableScreen.class, EMPOWERMENT);

        registry.registerFocusedStack((screen, mouse) -> {
            if (!(screen instanceof TeldecoreScreen teldecoreScreen)) return CompoundEventResult.pass();
            FeyIngredient focusedIngredient = teldecoreScreen.getFocusedIngredient(mouse.x, mouse.y);
            if (focusedIngredient == null) return CompoundEventResult.pass();
            EntryStack<?> focusedStack = toEntryStack(focusedIngredient);
            return focusedStack == null ? CompoundEventResult.pass() : CompoundEventResult.interruptTrue(focusedStack);
        });

        registry.registerDecider(new DisplayBoundsProvider<>() {
            @Override
            public <R extends Screen> boolean isHandingScreen(Class<R> screen) {
                return TeldecoreScreen.class.isAssignableFrom(screen);
            }

            @Override
            public @Nullable Rectangle getScreenBounds(Object object) {
                if (!(object instanceof TeldecoreScreen screen)) return null;
                return new Rectangle(screen.getX()-15, screen.getY()-5, TeldecoreScreen.BASE_WIDTH+20, TeldecoreScreen.BASE_HEIGHT+10);
            }

            @Override
            public <R extends Screen> ActionResult shouldScreenBeOverlaid(R screen) {
                return screen instanceof TeldecoreScreen ? ActionResult.SUCCESS : ActionResult.PASS;
            }
        });
    }

    private static <T extends Display> CategoryIdentifier<T> id(String path) {
        return CategoryIdentifier.of(Etherology.MOD_ID, path);
    }

    @Nullable
    private static EntryStack<?> toEntryStack(FeyIngredient ingredient) {
        Object content = ingredient.getContent();
        if (content == null) return null;
        return switch (content) {
            case ItemStack stack -> EntryStacks.of(stack);
            case CountedAspect aspectPair -> AspectPair.entry(aspectPair.aspect(), aspectPair.count());
            default -> null;
        };
    }
}
