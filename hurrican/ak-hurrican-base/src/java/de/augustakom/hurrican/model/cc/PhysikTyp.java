/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2004 09:03:12
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.tools.lang.NumberTools;

/**
 * Modell bildet einen bestimmten PhysikTyp ab. Die PhysikTypen sind ueber die UEVTs einem bestimmten HVT-Standort
 * zugeordnet. Darueber kann ermittelt werden, welche Produkte ein bestimmter HVT-Standort abdecken kann.
 *
 *
 */
public class PhysikTyp extends AbstractCCIDModel {

    /**
     * ID des PhysikTyps 'Undefiniert'
     */
    public static final Long PHYSIKTYP_UNDEFINIERT = Long.valueOf(11);

    // Physik-Typen Siemens
    public static final Long PHYSIKTYP_UK0 = Long.valueOf(1);
    public static final Long PHYSIKTYP_AB = Long.valueOf(3);
    public static final Long PHYSIKTYP_4H = Long.valueOf(4);
    public static final Long PHYSIKTYP_2H = Long.valueOf(5);
    public static final Long PHYSIKTYP_ADSL_DA = Long.valueOf(7);
    public static final Long PHYSIKTYP_ADSL_UK0 = Long.valueOf(8);
    public static final Long PHYSIKTYP_SDSL_DA = Long.valueOf(9);
    public static final Long PHYSIKTYP_ADSL_AB = Long.valueOf(12);
    public static final Long PHYSIKTYP_ADSL2P = Long.valueOf(13);
    public static final Long PHYSIKTYP_V52 = Long.valueOf(50);
    public static final Long PHYSIKTYP_MVO = Long.valueOf(51);

    // Physik-Typen Alcatel
    public static final Long PHYSIKTYP_ADSL_ATM_ALCATEL = Long.valueOf(100);
    public static final Long PHYSIKTYP_ADSL2P_ATM_ALCATEL = Long.valueOf(101);
    public static final Long PHYSIKTYP_ADSL2P_IP_ALCATEL = Long.valueOf(102);
    public static final Long PHYSIKTYP_ADSL_UK0_ALCATEL = Long.valueOf(103);
    public static final Long PHYSIKTYP_ADSL_AB_ALCATEL = Long.valueOf(104);
    public static final Long PHYSIKTYP_ADSL2P_ONLY_ALCATEL = Long.valueOf(105);
    public static final Long PHYSIKTYP_SDSL_ATM_ALCATEL = Long.valueOf(106);
    public static final Long PHYSIKTYP_SDSL_IP_ALCATEL = Long.valueOf(107);
    public static final Long PHYSIKTYP_SHDSL_IP_ALCATEL = Long.valueOf(108);

    // Physik-Typen Huawei
    public static final Long PHYSIKTYP_ADSL_DA_HUAWEI = Long.valueOf(507);
    public static final Long PHYSIKTYP_ADSL_UK0_HUAWEI = Long.valueOf(508);
    public static final Long PHYSIKTYP_SDSL_DA_HUAWEI = Long.valueOf(509);
    public static final Long PHYSIKTYP_ADSL_AB_HUAWEI = Long.valueOf(512);
    public static final Long PHYSIKTYP_ADSL2P_HUAWEI = Long.valueOf(513);
    public static final Long PHYSIKTYP_ADSL2P_ONLY_HUAWEI = Long.valueOf(514);
    public static final Long PHYSIKTYP_SHDSL_HUAWEI = Long.valueOf(515);
    public static final Long PHYSIKTYP_ADSL2P_MS_HUAWEI = Long.valueOf(516);
    public static final Long PHYSIKTYP_ADSL2P_ONLY_MS_HUAWEI = Long.valueOf(517);

    // FTTX Physik-Typen
    public static final Long PHYSIKTYP_FTTB_VDSL = Long.valueOf(800);
    public static final Long PHYSIKTYP_FTTB_POTS = Long.valueOf(801);
    public static final Long PHYSIKTYP_FTTH = Long.valueOf(803);
    public static final Long PHYSIKTYP_FTTC_VDSL = Long.valueOf(804);
    public static final Long PHYSIKTYP_FTTB_RF = Long.valueOf(805);
    public static final Long PHYSIKTYP_FTTH_RF = Long.valueOf(806);
    public static final Long PHYSIKTYP_FTTH_POTS = Long.valueOf(807);
    public static final Long PHYSIKTYP_FTTH_ETH = Long.valueOf(808);
    public static final Long PHYSIKTYP_FTTB_DPO_VDSL = Long.valueOf(809);
    public static final Long PHYSIKTYP_FTTB_GFAST = Long.valueOf(810);

    // virtuelle Typen
    public static final Long PHYSIKTYP_L2TP = Long.valueOf(109);
    public static final Long PHYSIKTYP_ATM = Long.valueOf(110);

    /**
     * Physiktyp fuer Connect-Rangierungen
     */
    public static final Long PHYSIKTYP_CONNECT = Long.valueOf(120);

    /**
     * Physiktyp fuer manuell aufgebaute Rangierungen.
     */
    public static final Long PHYSIKTYP_MANUELL = Long.valueOf(99);

    /**
     * Konstante fuer 'ptGroup' fuer Phone-Physiktypen
     */
    public static final Long PT_GROUP_PHONE = Long.valueOf(1);
    /**
     * Konstante fuer 'ptGroup' fuer Daten-Physiktypen
     */
    public static final Long PT_GROUP_DATA = Long.valueOf(2);
    /**
     * Konstante fuer 'ptGroup' fuer TV-Physiktypen
     */
    public static final Long PT_GROUP_TV = Long.valueOf(3);
    /**
     * Konstante fuer 'ptGroup' fuer Connect-Physiktypen
     */
    public static final Long PT_GROUP_CONNECT = Long.valueOf(5);

    public static final String NAME = "name";
    private String name = null;
    private String beschreibung = null;
    private Long hvtTechnikId = null;
    private Bandwidth maxBandwidth = null;
    private String hwSchnittstelle = null;
    private Long ptGroup = null;
    private String cpsTransferMethod = null;

    /**
     * Standardkonstruktor
     */
    public PhysikTyp() {
        super();
    }

    /**
     * Konstruktor mit Angabe aller Parameter
     *
     * @param id
     * @param name
     * @param beschreibung
     * @param hvtTechnikId
     */
    public PhysikTyp(Long id, String name, String beschreibung, Long hvtTechnikId) {
        super();
        setId(id);
        this.name = name;
        this.beschreibung = beschreibung;
        this.hvtTechnikId = hvtTechnikId;
    }

    /**
     * Prueft, ob der Physiktyp dem Typ {@code physikTypId} entspricht.
     *
     * @param physikTypId
     * @return
     */
    public boolean isOfType(Long physikTypId) {
        return NumberTools.equal(getId(), physikTypId);
    }

    /**
     * Gibt an, ob es sich bei dem Physiktyp um einen Phone-Typ handelt.
     *
     * @return true wenn der Physiktyp ein Phone-Typ ist.
     *
     */
    public boolean isPhone() {
        return NumberTools.equal(getPtGroup(), PT_GROUP_PHONE);
    }

    /**
     * @return Returns the beschreibung.
     */
    public String getBeschreibung() {
        return beschreibung;
    }

    /**
     * @param beschreibung The beschreibung to set.
     */
    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

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
     * @return Returns the hvtTechnikId.
     */
    public Long getHvtTechnikId() {
        return hvtTechnikId;
    }

    /**
     * @param hvtTechnikId The hvtTechnikId to set.
     */
    public void setHvtTechnikId(Long hvtTechnikId) {
        this.hvtTechnikId = hvtTechnikId;
    }

    /**
     * Gibt die maximal moegliche Bandbreite (in bit/s) fuer den Physiktyp zurueck.
     *
     * @return Returns the maxBandwidth.
     */
    public Bandwidth getMaxBandwidth() {
        return this.maxBandwidth;
    }

    /**
     * Setzt die maximal moegliche Bandbreite (in bit/s) fuer den Physiktyp.
     *
     * @param maxBandwidth The maxBandwidth to set.
     */
    public void setMaxBandwidth(Bandwidth maxBandwidth) {
        this.maxBandwidth = maxBandwidth;
    }

    /**
     * @return Returns the hwSchnittstelle.
     */
    public String getHwSchnittstelle() {
        return hwSchnittstelle;
    }

    /**
     * @param hwSchnittstelle The hwSchnittstelle to set.
     */
    public void setHwSchnittstelle(String hwSchnittstelle) {
        this.hwSchnittstelle = hwSchnittstelle;
    }

    /**
     * @return Returns the ptGroup.
     */
    public Long getPtGroup() {
        return ptGroup;
    }

    /**
     * @param ptGroup The ptGroup to set.
     */
    public void setPtGroup(Long ptGroup) {
        this.ptGroup = ptGroup;
    }

    /**
     * Gibt den Namen des Physiktyps zurueck, wie er fuer den CPS-Parameter DSL.TRANSFER_METHOD erwartet wird (z.B.
     * 'ADSL2+').
     *
     * @return the cpsTransferMethod
     */
    public String getCpsTransferMethod() {
        return cpsTransferMethod;
    }

    /**
     * @param cpsTransferMethod the cpsTransferMethod to set
     */
    public void setCpsTransferMethod(String cpsTransferMethod) {
        this.cpsTransferMethod = cpsTransferMethod;
    }
}
