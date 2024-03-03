package com.bitconex.danalyst.funmodmap.gui.commons;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImageHelper {
    private final static Map<String, Image> imageCache = Collections.synchronizedMap(new HashMap<>());
    public static Image createImage(String classPathURI, double width, double height) {
        String cacheKey = classPathURI + "_DIM_WIDTH_" + width + "_DIM_HEIGHT_" + height;
        if(!imageCache.containsKey(cacheKey)){
            InputStream input = ImageHelper.class.getResourceAsStream(classPathURI);
            imageCache.put(cacheKey,  new Image(input, width, height, false, false));
        }
        return imageCache.get(cacheKey);

    }
}
