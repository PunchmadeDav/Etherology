package ru.feytox.etherology.client.gui.oculus;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.magic.aspects.EtherologyAspect;

public class AspectComponent extends FlowLayout {

    public AspectComponent(Aspect aspect, int value) {
        super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);

        TextureComponent aspectTexture = Components.texture(Aspect.TEXTURE, aspect.getTextureMinX(), aspect.getTextureMinY(), aspect.getWidth(), aspect.getHeight(), EtherologyAspect.TEXTURE_WIDTH, EtherologyAspect.TEXTURE_HEIGHT);
        LabelComponent valueComponent = Components.label(Text.of(String.valueOf(value))).shadow(true);

        this.child(aspectTexture.blend(true).sizing(Sizing.fixed(32))).child(valueComponent.positioning(Positioning.relative(95, 95)));
    }
}
