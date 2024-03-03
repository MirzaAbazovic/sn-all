/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2005 15:44:03
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.shared.iface.PortierungsartModel;


/**
 * Model fuer die Portierungsart.
 *
 *
 */
public class Portierungsart extends AbstractCCIDModel implements PortierungsartModel {

    /**
     * ID fuer die Portierungsart, wenn sie nicht bekannt ist.
     */
    public static final Long PORTIERUNG_NOT_DEFINED = Long.valueOf(0);
    /**
     * ID fuer die Portierungsart 'Standard' (8-12 bzw 12-16 Uhr)
     */
    public static final Long PORTIERUNG_STANDARD = Long.valueOf(1);
    /**
     * ID fuer die Portierungsart 'Export' (6-8 Uhr)
     */
    public static final Long PORTIERUNG_EXPORT = Long.valueOf(2);
    /**
     * ID fuer die Portierungsart 'Sonderportierung' (5-7 Uhr)
     */
    public static final Long PORTIERUNG_SONDER = Long.valueOf(3);

    /**
     * int Wert fuer die Portierungsart, wenn sie nicht bekannt ist.
     */
    public static final int PORTIERUNG_NOT_DEFINED_INT = 0;
    /**
     * int Wert fuer die Portierungsart 'Standard' (8-12 bzw 12-16 Uhr)
     */
    public static final int PORTIERUNG_STANDARD_INT = 1;
    /**
     * int Wert fuer die Portierungsart 'Export' (6-8 Uhr)
     */
    public static final int PORTIERUNG_EXPORT_INT = 2;
    /**
     * int Wert fuer die Portierungsart 'Sonderportierung' (5-7 Uhr)
     */
    public static final int PORTIERUNG_SONDER_INT = 3;

    private String text = null;

    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.PortierungsartModel#getPortierungsartId()
     */
    public Long getPortierungsartId() {
        return getId();
    }
}


