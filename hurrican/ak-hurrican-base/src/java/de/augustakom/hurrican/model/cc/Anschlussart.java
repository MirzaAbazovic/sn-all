/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2004 08:57:18
 */
package de.augustakom.hurrican.model.cc;


import de.augustakom.common.tools.lang.NumberTools;

/**
 * Modell, um eine Anschlussart abzubilden. <br> Eine Anschlussart definiert, ob ein Kunde z.B. ueber das
 * AugustaKom-Netz oder ein Fremdnetz angeschlossen wird.
 *
 *
 */
public class Anschlussart extends AbstractCCIDModel {

    /**
     * Anschlussart: HVT
     */
    public static final Long ANSCHLUSSART_HVT = 1L;
    /**
     * Anschlussart: LEW
     */
    public static final Long ANSCHLUSSART_LEW = 2L;
    /**
     * Anschlussart: Direktanschluss
     */
    public static final Long ANSCHLUSSART_DIREKT = 3L;
    /**
     * Anschlussart: M-Net
     */
    public static final Long ANSCHLUSSART_MNET = 4L;
    /**
     * Anschlussart: o.tel.o
     */
    public static final Long ANSCHLUSSART_OTELO = 5L;
    /**
     * Anschlussart: BNK
     */
    public static final Long ANSCHLUSSART_BNK = 6L;
    /**
     * Anschlussart: Virtuell
     */
    public static final Long ANSCHLUSSART_VIRTUELL = 7L;
    /**
     * Anschlussart: Netzkopplung
     */
    public static final Long ANSCHLUSSART_NETZKOPPLUNG = 8L;
    /**
     * Anschlussart: Stawa
     */
    public static final Long ANSCHLUSSART_STAWA = 9L;
    /**
     * Anschlussart: Viag
     */
    public static final Long ANSCHLUSSART_VIAG = 10L;
    /**
     * Anschlussart: AK-Partner
     */
    public static final Long ANSCHLUSSART_AK_PARTNER = 11L;
    /**
     * Anschlussart: DTAG CFV
     */
    public static final Long ANSCHLUSSART_DTAG_CFV = 12L;
    /**
     * Anschlussart: AK-Online
     */
    public static final Long ANSCHLUSSART_AK_ONLINE = 13L;
    /**
     * Anschlussart: PreSelection
     */
    public static final Long ANSCHLUSSART_PRE_SELECTION = 14L;
    /**
     * Anschlussart: FttB
     */
    public static final Long ANSCHLUSSART_FTTB = 15L;
    /**
     * Anschlussart: FttH
     */
    public static final Long ANSCHLUSSART_FTTH = 16L;
    /**
     * Anschlussart: FttX
     */
    public static final Long ANSCHLUSSART_FTTX = 17L;
    /**
     * Anschlussart: KVZ
     */
    public static final Long ANSCHLUSSART_KVZ = 20L;

    private static final long serialVersionUID = 7404765064717472675L;

    private String anschlussart = null;

    /**
     * @return Returns the anschlussart.
     */
    public String getAnschlussart() {
        return anschlussart;
    }

    /**
     * @param anschlussart The anschlussart to set.
     */
    public void setAnschlussart(String anschlussart) {
        this.anschlussart = anschlussart;
    }


    /**
     * Prueft, ob es sich bei der angegebenen Anschlussart um eine "spezielle" Verbindung handelt (z.B. 'Virtuell',
     * 'Direktanschluss' etc) oder ob es sich um einen Standort (z.B. 'HVT', 'FTTB' etc.) handelt.
     * @param anschlussartId
     * @return true wenn es sich um eine spezielle Verbindung handelt.
     */
    public static boolean isSpecialConnection(Long anschlussartId) {
        return NumberTools.isIn(anschlussartId,
                new Number[]{
                        ANSCHLUSSART_LEW,
                        ANSCHLUSSART_DIREKT,
                        ANSCHLUSSART_MNET,
                        ANSCHLUSSART_OTELO,
                        ANSCHLUSSART_BNK,
                        ANSCHLUSSART_VIRTUELL,
                        ANSCHLUSSART_NETZKOPPLUNG,
                        ANSCHLUSSART_STAWA,
                        ANSCHLUSSART_VIAG,
                        ANSCHLUSSART_AK_PARTNER,
                        ANSCHLUSSART_AK_ONLINE,
                        ANSCHLUSSART_PRE_SELECTION
                });
    }

}
