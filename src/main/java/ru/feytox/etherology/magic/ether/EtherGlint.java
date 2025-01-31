package ru.feytox.etherology.magic.ether;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import ru.feytox.etherology.item.glints.GlintItem;

public class EtherGlint {

    @Getter
    private final float maxEther;
    private final ItemStack stack;

    public EtherGlint(ItemStack glintStack) {
        this.stack = glintStack;
        if (glintStack.getItem() instanceof GlintItem glintItem) {
            this.maxEther = glintItem.getMaxEther();
        } else {
            this.maxEther = 0;
        }
    }

    public boolean isFull() {
        return getStoredEther() == maxEther;
    }

    /**
     * @return излишек, который не поместился в глинт
     */
    public float increment(float value) {
        return GlintItem.increment(stack, maxEther, value);
    }

    /**
     * @return количество забранного эфира
     */
    public float decrement(float value) {
        return GlintItem.decrement(stack, value);
    }

    public float getStoredEther() {
        return GlintItem.getStoredEther(stack);
    }
}
