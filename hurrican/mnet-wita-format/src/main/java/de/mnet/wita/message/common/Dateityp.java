/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2011 12:54:27
 */
package de.mnet.wita.message.common;

public enum Dateityp {
    TIFF("image/tiff", "tif"),
    JPEG("image/jpeg", "jpg"),
    MSWORD("application/msword", "doc"),
    EXCEL("application/excel", "xls"),
    PDF("application/pdf", "pdf"),
    UNKNOWN("unknown", "unknown");

    public final String mimeTyp;
    public final String extension;

    private Dateityp(String mimeTyp, String extension) {
        this.mimeTyp = mimeTyp;
        this.extension = extension;
    }

    public static Dateityp fromMimeTyp(String mimeTyp) {
        for (Dateityp dateityp : Dateityp.values()) {
            if (dateityp.mimeTyp.equalsIgnoreCase(mimeTyp)) {
                return dateityp;
            }
        }
        return UNKNOWN;
    }

    public static Dateityp fromExtension(String extension) {
        for (Dateityp dateityp : Dateityp.values()) {
            if (dateityp.extension.equalsIgnoreCase(extension)) {
                return dateityp;
            }
        }
        return UNKNOWN;
    }

}
