/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2007 08:31:11 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.billing.AbstractBillingModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * View-Klasse, um die formatierte Anschrift eines Kunden abzubilden. Die Adresse wird entsprechend den Vorgaben aus
 * Taifun formatiert.
 *
 *
 */
public class FormattedAddress extends AbstractBillingModel implements KundenModel, DebugModel {

    private Long kundeNo = null;
    private Long adresseNo = null;
    private String[] adresse = null;

    /**
     * @return adresse
     */
    public String[] getAdresse() {
        return adresse;
    }

    /**
     * @param adresse Festzulegender adresse
     */
    public void setAdresse(String[] adresse) {
        this.adresse = adresse;
    }

    /**
     * @return adresseNo
     */
    public Long getAdresseNo() {
        return adresseNo;
    }

    /**
     * @param adresseNo Festzulegender adresseNo
     */
    public void setAdresseNo(Long adresseNo) {
        this.adresseNo = adresseNo;
    }

    /**
     * @return Returns the kundeNo.
     */
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + this.getClass().getName());
            logger.debug("  kundeNoOrig: " + getKundeNo());
            logger.debug("  adresseNo  : " + getAdresseNo());
            logger.debug("  adresse    : " + Arrays.toString(getAdresse()));
        }
    }

}

