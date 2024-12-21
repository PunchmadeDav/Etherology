package ru.feytox.etherology.util.misc;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

public record Color(float red, float green, float blue) {

    public static Codec<Color> CODEC;

    public static Color from(@Nullable RGBColor color) {
        if (color == null) return of(0xFFFFFF);
        return of(color.asHex());
    }

    private static Color of(int hex) {
        float r = ((hex >> 16) & 0xFF) / 255f;
        float g = ((hex >> 8) & 0xFF) / 255f;
        float b = (hex & 0xFF) / 255f;
        return new Color(r, g, b);
    }

    private static Color of(String hexStr) {
        return of(Integer.parseInt(hexStr, 16));
    }

    @Override
    public String toString() {
        int color = (int) red*255;
        color = (color << 8) + (int) green*255;
        color = (color << 8) + (int) blue*255;
        return Integer.toHexString(color).toUpperCase();
    }

    static {
        CODEC = Codec.STRING.xmap(Color::of, Color::toString);
    }
}
