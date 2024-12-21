package ru.feytox.etherology.client.gui.teldecore.button;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.client.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;

public class ChapterButton extends AbstractButton {

    private static final Identifier MARK = EIdentifier.of("textures/gui/teldecore/icon/chapter_mark.png");
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;

    private final Identifier target;
    private final ItemStack icon;
    private final List<Text> tooltip;
    private final float dx;
    @Getter
    private final float dy;
    private final boolean wasOpened;
    private final boolean isSubTab;
    private final boolean glowing;

    public ChapterButton(TeldecoreScreen parent, Identifier texture, Identifier target, ItemStack icon, List<Text> tooltip, boolean wasOpened, boolean isSubTab, boolean glowing, float rootX, float rootY, float dx, float dy) {
        super(parent, texture, null, rootX, rootY, dx-WIDTH/2f, dy-HEIGHT/2f, WIDTH, HEIGHT);
        this.target = target;
        this.icon = icon;
        this.dx = dx-WIDTH/2f;
        this.dy = dy-HEIGHT/2f;
        this.tooltip = tooltip;
        this.wasOpened = wasOpened;
        this.isSubTab = isSubTab;
        this.glowing = glowing;
    }

    public void move(float rootX, float rootY) {
        this.baseX = rootX + dx;
        this.baseY = rootY + dy;
    }

    public void renderTinted(DrawContext context, int mouseX, int mouseY, float delta, float tint) {
        if (glowing) RenderSystem.setShaderColor(tint, tint, tint, 1.0f);
        super.render(context, mouseX, mouseY, delta);
        if (glowing) RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    protected void renderExtra(DrawContext context, boolean hovered) {
        float x = baseX + width / 2f - 8.0f;
        float y = baseY + height / 2f - 8.0f;
        context.push();
        context.translate(x, y, 0);
        context.drawItem(icon, 0, 0);
        context.pop();
        if (wasOpened) return;

        context.push();
        context.translate(baseX+width-7, baseY-1, 0);
        context.drawTexture(MARK, 0, 0, 0, 0, 3, 10, 3, 10);
        context.pop();
    }

    public void renderTooltip(DrawContext context, int mouseX, int mouseY) {
        if (!isMouseOver(mouseX, mouseY)) return;
        context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button) {
        return dataAction("Failed to handle click on chapter %s button".formatted(target.toString()), data -> {
            if (isSubTab) data.switchTab(target);
            else data.setSelectedChapter(target);
            data.addOpened(target);
            parent.clearAndInit();
            parent.executeOnPlayer(player -> player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0f, 0.9f + 0.1f * player.getWorld().getRandom().nextFloat()));
        });
    }
}
