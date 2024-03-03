/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.2004 07:54:52
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.tools.lang.NumberTools;


/**
 * Modell fuer die Protokollierung(!) von Physik-Uebernahmen. <br> Ueber das Field <code>aenderungstyp</code> wird
 * protokolliert, um welche Art von Physikuebernahme es sich handelt (z.B. Up-/Downgrade). <br> In
 * <code>verlaufId</code> wird die ID des Verlaufs aufgenommen, der fuer die Physikuebernahme erstellt wurde. <br><br>
 * Das Feld <code>kriterium</code> definiert, welche Auftrags-ID als ID_A bzw. als ID_B eingetragen wird.
 *
 *
 */
public class PhysikUebernahme extends AbstractCCIDModel {

    /**
     * Wert fuer 'kriterium'. Bedeutung: Als <code>auftragIdA</code> wird die 'alte' Auftrags-ID, fuer
     * <code>auftragIdB</code> die 'neue' Auftrags-ID verwendet.
     */
    public static final Integer KRITERIUM_ALT_NEU = Integer.valueOf(1);

    /**
     * Wert fuer 'kriterium'. Bedeutung: Als <code>auftragIdA</code> wird die 'neue' Auftrags-ID, fuer
     * <code>auftragIdB</code> die 'alte' Auftrags-ID verwendet.
     */
    public static final Integer KRITERIUM_NEU_ALT = Integer.valueOf(2);

    private Long vorgang = null;
    private Long auftragIdA = null;
    private Long auftragIdB = null;
    private Integer kriterium = null;
    private Long aenderungstyp = null;
    private Long verlaufId = null;

    /**
     * Ueberprueft, ob es sich bei dem Kriterium um den Typ 'NEU_ALT' handelt.
     *
     * @return
     *
     */
    public boolean isKriteriumNeu2Alt() {
        return NumberTools.equal(getKriterium(), KRITERIUM_NEU_ALT);
    }

    /**
     * @return Returns the auftragIdA.
     */
    public Long getAuftragIdA() {
        return auftragIdA;
    }

    /**
     * @param auftragIdA The auftragIdA to set.
     */
    public void setAuftragIdA(Long auftragIdA) {
        this.auftragIdA = auftragIdA;
    }

    /**
     * @return Returns the auftragIdB.
     */
    public Long getAuftragIdB() {
        return auftragIdB;
    }

    /**
     * @param auftragIdB The auftragIdB to set.
     */
    public void setAuftragIdB(Long auftragIdB) {
        this.auftragIdB = auftragIdB;
    }

    /**
     * @return Returns the kriterium.
     */
    public Integer getKriterium() {
        return kriterium;
    }

    /**
     * @param kriterium The kriterium to set.
     */
    public void setKriterium(Integer kriterium) {
        this.kriterium = kriterium;
    }

    /**
     * @return Returns the vorgang.
     */
    public Long getVorgang() {
        return vorgang;
    }

    /**
     * @param vorgang The vorgang to set.
     */
    public void setVorgang(Long vorgang) {
        this.vorgang = vorgang;
    }

    /**
     * @return Returns the aenderungstyp.
     */
    public Long getAenderungstyp() {
        return aenderungstyp;
    }

    /**
     * @param aenderungstyp The aenderungstyp to set.
     */
    public void setAenderungstyp(Long aenderungstyp) {
        this.aenderungstyp = aenderungstyp;
    }

    /**
     * @return Returns the verlaufId.
     */
    public Long getVerlaufId() {
        return verlaufId;
    }

    /**
     * @param verlaufId The verlaufId to set.
     */
    public void setVerlaufId(Long verlaufId) {
        this.verlaufId = verlaufId;
    }

}


