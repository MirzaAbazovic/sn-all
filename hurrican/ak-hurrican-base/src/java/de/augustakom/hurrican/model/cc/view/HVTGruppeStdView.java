/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2005 13:04:34
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;


/**
 * View-Modell zur Abbildung von HVT-Standorten und Gruppen.
 *
 *
 */
public class HVTGruppeStdView extends AbstractCCModel implements HvtIdStandortModel {

    private Long hvtIdStandort = null;
    private Long hvtIdGruppe = null;
    private Integer asb = null;
    private String onkz = null;
    public static final String GET_ORTSTEIL = "getOrtsteil";
    private String ortsteil = null;
    private Long standortTypRefId = null;
    private String gesicherteRealisierung = null;

    /**
     * Gibt den Namen vom HVT mit Ortsteil- und ASB zurueck.
     *
     * @return
     *
     */
    public String getName() {
        return (getOrtsteil() == null) ? null :
                StringTools.join(new String[] { getOrtsteil(), "" + getAsb() }, " - ASB ", true);
    }

    /**
     * @return Returns the asb.
     */
    public Integer getAsb() {
        return asb;
    }

    /**
     * @param asb The asb to set.
     */
    public void setAsb(Integer asb) {
        this.asb = asb;
    }

    /**
     * @return Returns the hvtIdGruppe.
     */
    public Long getHvtIdGruppe() {
        return hvtIdGruppe;
    }

    /**
     * @param hvtIdGruppe The hvtIdGruppe to set.
     */
    public void setHvtIdGruppe(Long hvtIdGruppe) {
        this.hvtIdGruppe = hvtIdGruppe;
    }

    /**
     * @return Returns the hvtIdStandort.
     */
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    /**
     * @param hvtIdStandort The hvtIdStandort to set.
     */
    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    /**
     * @return Returns the onkz.
     */
    public String getOnkz() {
        return onkz;
    }

    /**
     * @param onkz The onkz to set.
     */
    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    /**
     * @return Returns the ortsteil.
     */
    public String getOrtsteil() {
        return ortsteil;
    }

    /**
     * @param ortsteil The ortsteil to set.
     */
    public void setOrtsteil(String ortsteil) {
        this.ortsteil = ortsteil;
    }

    /**
     * @return Returns the standortTypRefId.
     */
    public Long getStandortTypRefId() {
        return standortTypRefId;
    }

    /**
     * @param standortTypRefId The standortTypRefId to set.
     */
    public void setStandortTypRefId(Long standortTypRefId) {
        this.standortTypRefId = standortTypRefId;
    }

    /**
     * @return Returns the gesicherteRealisierung.
     */
    public String getGesicherteRealisierung() {
        return gesicherteRealisierung;
    }

    /**
     * @param gesicherteRealisierung The gesicherteRealisierung to set.
     */
    public void setGesicherteRealisierung(String gesicherteRealisierung) {
        this.gesicherteRealisierung = gesicherteRealisierung;
    }

}


