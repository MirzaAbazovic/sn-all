/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 04.07.2014 
 */
package de.augustakom.hurrican.model;

import java.util.*;

/**
 * Used for encapsulating the <b>M-Net</b> internal TNB(Teilnehmernetzbetreiber) details.
 * Partner TNB details (Vodafone, DTAG, etc.) are located in the database (DNTNB) and should <b>not</b> stored here.
 */
public enum TNB {

    AKOM("AKOM", "D043"),
    NEFKOM("Nefkom", "D007"),
    MNET("Mnet", "D052"),
    MNET_MOBIL("M-net (Mobile)", "MNET"),
    MNET_NGN("Mnet (NGN)", "D235");

    public final String carrierNameUC;
    public final String carrierName;
    public final String tnbKennung;

    TNB(String carrierName, String tnbKennung) {
        this.carrierName = carrierName;
        this.tnbKennung = tnbKennung;
        this.carrierNameUC = carrierName.toUpperCase();
    }

    public static TNB fromTnbKennung(String tnbKennungParam) {
        return Arrays.stream(TNB.values())
                .filter(v -> v.tnbKennung.equals(tnbKennungParam))
                .findFirst()
                .orElse(null);
    }

}
