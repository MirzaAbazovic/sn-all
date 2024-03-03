/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.05.2005 14:23:37
 */
package de.augustakom.hurrican.model.cc.kubena;

import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.cc.Endstelle;


/**
 * View-Modell fuer das Ergebnis einer Kubena-Abfrage.
 *
 *
 */
public class KubenaResultView extends AbstractCCModel {

    private String vbz = null;
    private String endstelleA = null;
    private String endstelleAName = null;
    private String endstelleAPLZ = null;
    private String endstelleAOrt = null;
    private String endstelleB = null;
    private String endstelleBName = null;
    private String endstelleBPLZ = null;
    private String endstelleBOrt = null;
    private Long auftragId = null;
    private Long kundNoOrig = null;
    private String produkt = null;

    /**
     * Setzt die Bezeichnung der Endstelle. <br> Abhaengig von <code>esTyp</code> wird die Methode setEndstelleA oder
     * setEndstelleB aufgerufen.
     *
     * @param endstelle Bezeichnung der Endstelle
     * @param esTyp     Endstellentyp
     */
    public void setEndstelle(String endstelle, String esTyp) {
        if (Endstelle.ENDSTELLEN_TYP_A.equals(esTyp)) {
            setEndstelleA(endstelle);
        }
        else {
            setEndstelleB(endstelle);
        }
    }

    /**
     * Setzt den Namen der Endstelle. <br> Abhaengig von <code>esTyp</code> wird die Methode setEndstelleAName oder
     * setEndstelleBName aufgerufen.
     *
     * @param endstelle Name der Endstelle
     * @param esTyp     Endstellentyp
     */
    public void setEndstelleName(String name, String esTyp) {
        if (Endstelle.ENDSTELLEN_TYP_A.equals(esTyp)) {
            setEndstelleAName(name);
        }
        else {
            setEndstelleBName(name);
        }
    }

    /**
     * Setzt den Ort der Endstelle. <br> Abhaengig von <code>esTyp</code> wird die Methode setEndstelleAOrt oder
     * setEndstelleBOrt aufgerufen.
     *
     * @param endstelle Ort der Endstelle
     * @param esTyp     Endstellentyp
     */
    public void setEndstellePLZ(String plz, String esTyp) {
        if (Endstelle.ENDSTELLEN_TYP_A.equals(esTyp)) {
            setEndstelleAPLZ(plz);
        }
        else {
            setEndstelleBPLZ(plz);
        }
    }

    /**
     * Setzt die PLZ der Endstelle. <br> Abhaengig von <code>esTyp</code> wird die Methode setEndstelleAPLZ oder
     * setEndstelleBPLZ aufgerufen.
     *
     * @param endstelle PLZ der Endstelle
     * @param esTyp     Endstellentyp
     */
    public void setEndstelleOrt(String ort, String esTyp) {
        if (Endstelle.ENDSTELLEN_TYP_A.equals(esTyp)) {
            setEndstelleAOrt(ort);
        }
        else {
            setEndstelleBOrt(ort);
        }
    }

    /**
     * @return Returns the auftragId.
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the endstelleA.
     */
    public String getEndstelleA() {
        return endstelleA;
    }

    /**
     * @param endstelleA The endstelleA to set.
     */
    public void setEndstelleA(String endstelleA) {
        this.endstelleA = endstelleA;
    }

    /**
     * @return Returns the endstelleB.
     */
    public String getEndstelleB() {
        return endstelleB;
    }

    /**
     * @param endstelleB The endstelleB to set.
     */
    public void setEndstelleB(String endstelleB) {
        this.endstelleB = endstelleB;
    }

    /**
     * @return Returns the kundNoOrig.
     */
    public Long getKundNoOrig() {
        return kundNoOrig;
    }

    /**
     * @param kundNoOrig The kundNoOrig to set.
     */
    public void setKundNoOrig(Long kundNoOrig) {
        this.kundNoOrig = kundNoOrig;
    }

    /**
     * @return Returns the produkt.
     */
    public String getProdukt() {
        return produkt;
    }

    /**
     * @param produkt The produkt to set.
     */
    public void setProdukt(String produkt) {
        this.produkt = produkt;
    }

    /**
     * @return Returns the verbindungsBezeichnung.
     */
    public String getVbz() {
        return vbz;
    }

    /**
     * @param verbindungsBezeichnung The verbindungsBezeichnung to set.
     */
    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    /**
     * @return Returns the endstelleAName.
     */
    public String getEndstelleAName() {
        return endstelleAName;
    }

    /**
     * @param endstelleAName The endstelleAName to set.
     */
    public void setEndstelleAName(String endstelleAName) {
        this.endstelleAName = endstelleAName;
    }

    /**
     * @return Returns the endstelleAOrt.
     */
    public String getEndstelleAOrt() {
        return endstelleAOrt;
    }

    /**
     * @param endstelleAOrt The endstelleAOrt to set.
     */
    public void setEndstelleAOrt(String endstelleAOrt) {
        this.endstelleAOrt = endstelleAOrt;
    }

    /**
     * @return Returns the endstelleAPLZ.
     */
    public String getEndstelleAPLZ() {
        return endstelleAPLZ;
    }

    /**
     * @param endstelleAPLZ The endstelleAPLZ to set.
     */
    public void setEndstelleAPLZ(String endstelleAPLZ) {
        this.endstelleAPLZ = endstelleAPLZ;
    }

    /**
     * @return Returns the endstelleBName.
     */
    public String getEndstelleBName() {
        return endstelleBName;
    }

    /**
     * @param endstelleBName The endstelleBName to set.
     */
    public void setEndstelleBName(String endstelleBName) {
        this.endstelleBName = endstelleBName;
    }

    /**
     * @return Returns the endstelleBOrt.
     */
    public String getEndstelleBOrt() {
        return endstelleBOrt;
    }

    /**
     * @param endstelleBOrt The endstelleBOrt to set.
     */
    public void setEndstelleBOrt(String endstelleBOrt) {
        this.endstelleBOrt = endstelleBOrt;
    }

    /**
     * @return Returns the endstelleBPLZ.
     */
    public String getEndstelleBPLZ() {
        return endstelleBPLZ;
    }

    /**
     * @param endstelleBPLZ The endstelleBPLZ to set.
     */
    public void setEndstelleBPLZ(String endstelleBPLZ) {
        this.endstelleBPLZ = endstelleBPLZ;
    }

}


