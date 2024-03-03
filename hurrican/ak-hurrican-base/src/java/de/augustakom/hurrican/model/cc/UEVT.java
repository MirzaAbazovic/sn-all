/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2004 10:43:25
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell bildet einen Uebergabeverteiler fuer einen HVT ab.
 *
 *
 */
public class UEVT extends AbstractCCIDModel {

    private Long hvtIdStandort = null;
    public static String UEVT = "uevt";
    private String uevt = null;
    private Integer schwellwert = null;
    private Boolean projektierung = null;
    private Long rackId = null;

    /**
     * Erstellt eine Bezeichnung fuer den UEVT. (UEVT + Hvt-ID)
     *
     * @return
     */
    public String getBezeichnung() {
        if (getId() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(getUevt());
            sb.append(" (HVT-Standort: ");
            sb.append(getHvtIdStandort());
            sb.append(")");
            return sb.toString();
        }
        return " ";
    }

    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    public Integer getSchwellwert() {
        return schwellwert;
    }

    public void setSchwellwert(Integer schwellwert) {
        this.schwellwert = schwellwert;
    }

    /**
     * Gibt die UEVT Bezeichnung zurueck. Falls eine KVZ Nummer gesetzt ist, ist die UEVT Bezeichnung auch als
     * KVZ-Schaltnummer zu interpretieren.
     *
     * @return
     */
    public String getUevt() {
        return uevt;
    }

    public void setUevt(String uevt) {
        this.uevt = uevt;
    }

    public Boolean getProjektierung() {
        return projektierung;
    }

    public void setProjektierung(Boolean projektierung) {
        this.projektierung = projektierung;
    }

    public Long getRackId() {
        return rackId;
    }

    public void setRackId(Long rackId) {
        this.rackId = rackId;
    }
}
