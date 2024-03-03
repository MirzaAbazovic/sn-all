/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.04.2005 13:08:35
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell zur Definition der moeglichen Physikaenderungs-Typen. <br> In dem Modell wird ausserdem definiert, welche
 * Abteilung <b>auf jeden Fall</code> einen Bauauftrag bei einer best. Physikaenderung erhalten soll.
 *
 *
 */
public class PhysikaenderungsTyp extends BADefault {

    /**
     * Wert fuer Physik-Uebernahme 'Anschlussuebernahme'
     */
    public static final Long STRATEGY_ANSCHLUSSUEBERNAHME = Long.valueOf(5000L);
    /**
     * @deprecated Wert fuer Physik-Uebernahme 'Up-/Downgrade'
     */
    @Deprecated
    public static final Long STRATEGY_UP_DOWNGRADE = Long.valueOf(5001L);
    /**
     * Wert fuer Physik-Uebernahme 'Wandel Analog-->ISDN'
     */
    public static final Long STRATEGY_WANDEL_ANALOG_ISDN = Long.valueOf(5002L);
    /**
     * Wert fuer Physik-Uebernahme 'Wandel ISDN-->Analog'
     */
    public static final Long STRATEGY_WANDEL_ISDN_ANALOG = Long.valueOf(5003L);
    /**
     * Wert fuer Physik-Uebernahme 'Bandbreitenaenderung'
     */
    public static final Long STRATEGY_BANDBREITENAENDERUNG = Long.valueOf(5004L);
    /**
     * Wert fuer Physik-Uebernahme 'DSL-Kreuzung'
     */
    public static final Long STRATEGY_DSL_KREUZUNG = Long.valueOf(5005L);
    /**
     * Wert fuer Physikaenderung 'Wandel SDSL 2- auf 4-Draht'.
     */
    public static final Long STRATEGY_WANDEL_SDSL_2TO4DRAHT = Long.valueOf(5007L);

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


}


