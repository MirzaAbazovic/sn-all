package com.bitconex.danalyst.funmodmap.adapter.jsonfile;

import javafx.scene.paint.Color;

public class ColorDTO {
    public double red;
    public double green;
    public double blue;
    public double opacity;

    public static ColorDTO fromColor(Color color) {
        ColorDTO colorDTO = new ColorDTO();
        colorDTO.red = color.getRed();
        colorDTO.green = color.getGreen();
        colorDTO.blue = color.getBlue();
        colorDTO.opacity = color.getOpacity();
        return colorDTO;
    }

    public static Color toColor(ColorDTO colorDTO) {
        return new Color(
                colorDTO.red,
                colorDTO.green,
                colorDTO.blue,
                colorDTO.opacity
        );
    }
}
