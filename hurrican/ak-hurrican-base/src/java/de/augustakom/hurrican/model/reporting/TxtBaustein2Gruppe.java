/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.04.2007 10:33:57
 */
package de.augustakom.hurrican.model.reporting;

/**
 * Abbildung der Zuordnung von Txt-Bausteinen zu Txt-Bausteinen-Gruppen.
 *
 *
 */
public class TxtBaustein2Gruppe extends AbstractReportLongIdModel {

    private Long txtBausteinIdOrig = null;
    private Long txtBausteinGruppeId = null;

    /**
     * @return txtBausteinGruppeId
     */
    public Long getTxtBausteinGruppeId() {
        return txtBausteinGruppeId;
    }

    /**
     * @param txtBausteinGruppeId Festzulegender txtBausteinGruppeId
     */
    public void setTxtBausteinGruppeId(Long txtBausteinGruppeId) {
        this.txtBausteinGruppeId = txtBausteinGruppeId;
    }

    /**
     * @return txtBausteinId
     */
    public Long getTxtBausteinIdOrig() {
        return txtBausteinIdOrig;
    }

    /**
     * @param txtBausteinId Festzulegender txtBausteinId
     */
    public void setTxtBausteinIdOrig(Long txtBausteinId) {
        this.txtBausteinIdOrig = txtBausteinId;
    }


}


