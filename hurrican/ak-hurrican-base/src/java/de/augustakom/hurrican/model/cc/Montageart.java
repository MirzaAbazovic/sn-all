/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2004 16:21:30
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.tools.lang.NumberTools;


/**
 * Modell fuer die Abbildung einer Montage-Art.
 *
 *
 */
public class Montageart extends AbstractCCIDModel {

    /**
     * ID fuer die Montageart 'Akom'
     */
    public static final Long MONTAGEART_AKOM = Long.valueOf(1);
    /**
     * ID fuer die Montageart 'Akom mit AKom-Modem'
     */
    public static final Long MONTAGEART_AKOM_AND_MODEM = Long.valueOf(2);
    /**
     * ID fuer die Montageart 'Allgaeukom'
     */
    public static final Long MONTAGEART_ALLGAEUKOM = Long.valueOf(3);
    /**
     * ID fuer die Montageart 'NT-Uebernahme'
     */
    public static final Long MONTAGEART_NT_UEBERNAHME = Long.valueOf(5);
    /**
     * ID fuer die Montageart 'Modem-Uebernahme'
     */
    public static final Long MONTAGEART_MODEM_UEBERNAHME = Long.valueOf(4);
    /**
     * ID fuer die Montageart 'Selbstmontage'
     */
    public static final Long MONTAGEART_SELBSTMONTAGE = Long.valueOf(6);
    /**
     * ID fuer die Montageart 'Selbstmontage mit Herkunft AKom'
     */
    public static final Long MONTAGEART_SELBSTMONTAGE2 = Long.valueOf(7);
    /**
     * ID fuer die Montageart 'Uebernahme'
     */
    public static final Long MONTAGEART_UEBERNAHME = Long.valueOf(8);
    /**
     * ID fuer die Montageart 'M-net'
     */
    public static final Long MONTAGEART_MNET = Long.valueOf(9);

    private String name = null;

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Ueberprueft, ob es sich bei der Montageart um eine Montage durch AKom oder Allgaeukom handelt.
     *
     * @param id
     * @return
     *
     */
    public static boolean isMontageAKomOrAllgaeukom(Long id) {
        return NumberTools.isIn(id, new Long[] { MONTAGEART_AKOM,
                MONTAGEART_AKOM_AND_MODEM, MONTAGEART_ALLGAEUKOM, MONTAGEART_MNET });
    }
}


