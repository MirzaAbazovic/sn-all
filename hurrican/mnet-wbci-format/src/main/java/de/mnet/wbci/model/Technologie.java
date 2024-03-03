/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.01.14
 */
package de.mnet.wbci.model;

import java.util.*;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.ArrayUtils;

/**
 * Enum definiert die im WBCI Workstream abgestimmten Technologien. <br/> Die WBCI Schnittstelle selbst kennt in der XSD
 * keine Einschraenkung der Technologien; lediglich das Format ist definiert. Allerdings ist im WBCI Workstream eine
 * Excel-Liste mit den gueltigen Technologien abgestimmt, an die sich alle Carrier halten muessen. <br/> Diese Liste ist
 * in diesem Enum abgebildet.
 */
public enum Technologie {

    TAL_ISDN("001 TAL ISDN", ProduktGruppe.TAL, true, true, true),
    TAL_DSL("002 TAL DSL", ProduktGruppe.TAL, true, true, true),
    TAL_VDSL("003 TAL VDSL", ProduktGruppe.TAL, true, true, true),
    ADSL_SA_Annex_J("004 ADSL SA Annex J", ProduktGruppe.ADSL_SA, false, false, true),
    ADSL_SA_Annex_B("005 ADSL SA Annex B", ProduktGruppe.ADSL_SA, false, false, true),
    ADSL_SA("006 ADSL SA", ProduktGruppe.ADSL_SA, false, false, true),
    ADSL_SH("007 ADSL SH", ProduktGruppe.ADSL_SH, false, false, true),
    SDSL_SA("008 SDSL SA", ProduktGruppe.SDSL, false, false, true),
    VDSL_SA("009 VDSL SA", ProduktGruppe.VDSL, false, false, true),
    UMTS("010 UMTS", ProduktGruppe.MOBILFUNK, false, false, false),
    LTE("011 LTE", ProduktGruppe.MOBILFUNK, false, false, false),
    FTTC("012 FTTC", ProduktGruppe.FTTC, true, true, true),
    FTTB("013 FTTB", ProduktGruppe.FTTB, false, true, true),
    FTTH("014 FTTH", ProduktGruppe.FTTH, false, true, true),
    HFC("015 HFC", ProduktGruppe.KOAX, false, false, false),
    KOAX("016 Koax", ProduktGruppe.KOAX, false, false, false),
    KUPFER("017 Kupfer", ProduktGruppe.TAL, false, false, true, CarrierCode.DTAG),
    KUPFER_MX("018 Kupfer MX", ProduktGruppe.TAL, false, false, true, CarrierCode.DTAG),
    GF("019 GF", ProduktGruppe.GF, false, false, false),
    KUPFER_GF("020 Kupfer GF", ProduktGruppe.TAL, false, false, false),
    SONSTIGES("021 Sonstiges", ProduktGruppe.DTAG_ANY, false, false, true, CarrierCode.DTAG);

    /**
     * Multimap mit der Angabe, welche Technologie eines abgebenden Carrier (=Key der Multimap) zu welchen M-net
     * Technologien kompatibel ist. <br/>
     * <p/>
     * <pre>
     *   Key = Technologie des abgebenden Providers
     *   Values = M-net Technologien, die zu der Technologie des abgebenden Providers kompatibel sind
     * </pre>
     */
    private static final Multimap<Technologie, Technologie> compatibleAufnehmend =
            ImmutableMultimap.<Technologie, Technologie>builder()
                    .putAll(TAL_ISDN, TAL_ISDN, TAL_DSL, TAL_VDSL, FTTC)
                    .putAll(TAL_DSL, TAL_ISDN, TAL_DSL, TAL_VDSL, FTTC)
                    .putAll(TAL_VDSL, TAL_ISDN, TAL_DSL, TAL_VDSL, FTTC)
                    .putAll(KUPFER, TAL_ISDN, TAL_DSL, TAL_VDSL, FTTC)
                    .putAll(KUPFER_MX, TAL_ISDN, TAL_DSL, TAL_VDSL, FTTC)
                    .putAll(SONSTIGES, TAL_ISDN, TAL_DSL, TAL_VDSL, FTTC)
                    .putAll(FTTB, FTTB)
                    .putAll(FTTH, FTTH)
                    .putAll(FTTC, FTTC, TAL_ISDN, TAL_DSL, TAL_VDSL)
                    .putAll(ADSL_SA, TAL_ISDN, TAL_DSL, TAL_VDSL, FTTC)
                    .putAll(ADSL_SH, TAL_ISDN, TAL_DSL, TAL_VDSL, FTTC)
                    .putAll(SDSL_SA, TAL_ISDN, TAL_DSL, TAL_VDSL, FTTC)
                    .putAll(VDSL_SA, TAL_ISDN, TAL_DSL, TAL_VDSL, FTTC)
                    .putAll(ADSL_SA_Annex_J, TAL_ISDN, TAL_DSL, TAL_VDSL, FTTC)
                    .putAll(ADSL_SA_Annex_B, TAL_ISDN, TAL_DSL, TAL_VDSL, FTTC)
                    .build();

    private String wbciTechnologieCode;
    private ProduktGruppe produktGruppe;
    private boolean witaTechnologie;
    private boolean ressourcenUebernahmeAbg;
    private boolean ressourcenUebernahmeAuf;
    private CarrierCode[] restrictedCarrierAuf;

    /**
     * @param wbciTechnologieCode     Definiert den im WBCI Workstream abgestimmten Namen fuer die Technologie, wie er
     *                                in dem XML angegeben werden muss  
     * @param produktGruppe
     * @param witaTechnologie         Flag gibt an, ob M-net diese Technologie ueber die WITA SST bestellt
     * @param ressourcenUebernahmeAbg Flag gibt an, ob die Ressource von M-net an einen anderen Carrier abgegeben werden
     *                                kann
     * @param ressourcenUebernahmeAuf Flag gibt an, ob die Ressource von M-net von einem anderen Carrier uebernommen
     *                                werden kann
     * @param restrictedCarrierAuf    Angabe der Carrier schraenkt das Flag {@code ressourcenUebernahmeAuf} ein; nur
     *                                wenn der Partner-Carrier in der Liste konfiguriert ist, kann M-net die Ressource
     *                                aufnehmen, sonst nicht. <br/> Falls kein CarrierCode definiert ist, kann M-net die
     *                                Ressource von jedem Carrier aufnehmen. <br/> Die Einschraenkung kommt zu Stande,
     *                                da die DTAG i.d.R. '017 Kupfer' melden wird, auch wenn es sich im herkoemmlichen
     *                                Sinne um eine TAL handelt; die Ressource kann von M-net also aufgenommen werden.
     *                                Meldet hingegen ein anderer Carrier (z.B. Vodafone) die Technologie '017 Kupfer'
     *                                zurueck kann sie von M-net nicht uebernommen werden.
     */
    private Technologie(String wbciTechnologieCode,
            ProduktGruppe produktGruppe,
            boolean witaTechnologie,
            boolean ressourcenUebernahmeAbg,
            boolean ressourcenUebernahmeAuf,
            CarrierCode... restrictedCarrierAuf) {
        this.wbciTechnologieCode = wbciTechnologieCode;
        this.produktGruppe = produktGruppe;
        this.witaTechnologie = witaTechnologie;
        this.ressourcenUebernahmeAbg = ressourcenUebernahmeAbg;
        this.ressourcenUebernahmeAuf = ressourcenUebernahmeAuf;
        this.restrictedCarrierAuf = restrictedCarrierAuf;
    }

    public static Technologie lookUpWbciTechnologieCode(String wbciTechnologieCode) {
        if (wbciTechnologieCode == null) {
            return null;
        }
        for (Technologie technologie : Technologie.values()) {
            if (technologie.getWbciTechnologieCode().equals(wbciTechnologieCode)) {
                return technologie;
            }
        }
        throw new IllegalArgumentException(
                String.format("There exists no 'Technologie' for wbciTechnologieCode '%s'", wbciTechnologieCode));
    }

    public boolean isWitaTechnologie() {
        return witaTechnologie;
    }

    public String getWbciTechnologieCode() {
        return wbciTechnologieCode;
    }

    public ProduktGruppe getProduktGruppe() {
        return produktGruppe;
    }

    /**
     * Ueberprueft, ob die aktuelle Technologie von dem Partner uebernommen (IOType=IN) bzw. an den Partner abgegeben
     * (IOType=OUT) werden kann.
     *
     * @param ruemVaIoType   IOType (IN/OUT) der RUEM-VA: <br/>
     *                       <pre>
     *                        * IN  -> Partner-Carrier will die Ressource von M-net aufnehmen
     *                        * OUT -> M-net will die Ressource eines anderen Carriers aufnehmen
     *                       </pre>
     * @param partnerCarrier Angabe des Partner-Carriers, an den eine Ressource abgegeben bzw. von dem eine Ressource
     *                       aufgenommen werden soll
     * @return {@code true} wenn die Ressourcen-Uebernahme moeglich ist
     */
    public boolean isRessourcenUebernahmePossible(IOType ruemVaIoType, CarrierCode partnerCarrier) {
        if (IOType.OUT.equals(ruemVaIoType)) {
            // M-net = aufnehmender Provider
            if (ArrayUtils.isNotEmpty(restrictedCarrierAuf)) {
                if (ArrayUtils.contains(restrictedCarrierAuf, partnerCarrier)) {
                    return ressourcenUebernahmeAuf;
                }
                return false;
            }
            return ressourcenUebernahmeAuf;
        }
        else if (IOType.IN.equals(ruemVaIoType)) {
            // M-net = abgebender Provider
            return ressourcenUebernahmeAbg;
        }

        return false;
    }

    /**
     * Ueberprueft, ob die aktuelle Technologie mit der angegebenen Technologie 'kompatibel' ist. <br/> Beispiel: <br/>
     * <p/>
     * <pre>
     *   Technologie abg. Carrier  |   M-net Technologie    |   kompatibel
     *   ---------------------------------------------------------------------------------
     *   017 Kuper                     TAL ISDN                 ja
     *   001 TAL ISDN                  TAL DSL                  ja
     *   001 TAL ISDN                  TAL ISDN                 ja
     *   001 TAL ISDN                  TAL DSL                  ja
     *   012 FTTC                      TAL ISDN                 ja
     *   012 FTTC                      Kupfer                   nein
     *   017 Kupfer                    FTTC                     ja
     *   021 Sonstiges                 Sonstiges                nein
     * </pre>
     * <p/>
     * Diese Methode ist nur mit vorgelagerter Pruefung fuer die Ressourcen-Uebernahme ( {@link
     * de.mnet.wbci.model.Technologie#isRessourcenUebernahmePossible}) zu verwenden und macht eigentlich nur Sinn, wenn
     * M-net der aufnehmende Carrier ist.
     *
     * @param technologie
     * @return
     */
    public boolean isCompatibleTo(Technologie technologie) {
        Collection<Technologie> compatibleTechnologies = compatibleAufnehmend.get(this);
        return compatibleTechnologies != null && compatibleTechnologies.contains(technologie);
    }

    /**
     * @return the {@link List} of WITA relevant {@link Technologie}s.
     * <p/>
     * Currently these are: TAL_ISDN, TAL_DSL, TAL_VDSL, FTTC
     */
    public static List<Technologie> getWitaOrderRelevantTechnologies() {
        List<Technologie> witaTechnologies = new ArrayList<>();
        for (Technologie technologie : Technologie.values()) {
            if (technologie.isWitaTechnologie()) {
                witaTechnologies.add(technologie);
            }
        }
        return witaTechnologies;
    }
}
