package ru.feytox.etherology.recipes.matrix;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import ru.feytox.etherology.block.matrix.MatrixBlockEntity;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.recipes.FeyRecipe;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

import java.util.List;

@RequiredArgsConstructor
public class MatrixRecipe implements FeyRecipe<MatrixBlockEntity> {

    @Getter
    private final Ingredient centerInput;
    @Getter
    private final List<Aspect> aspects;
    @Getter
    private final float etherPoints;
    private final ItemStack outputStack;

    @Override
    public boolean matches(MatrixBlockEntity inventory, World world) {
        if (!centerInput.test(inventory.getStack(0))) return false;

        List<Aspect> aspects = inventory.getSortedAspects();
        if (aspects == null) return false;
        return aspects.equals(this.aspects);
    }

    @Override
    public ItemStack craft(MatrixBlockEntity inventory, RegistryWrapper.WrapperLookup lookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return getOutput();
    }

    public ItemStack getOutput() {
        return outputStack.copy();
    }

    @Override
    public FeyRecipeSerializer<?> getSerializer() {
        return MatrixRecipeSerializer.INSTANCE;
    }
}
