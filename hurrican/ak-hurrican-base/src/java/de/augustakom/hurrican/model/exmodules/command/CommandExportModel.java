/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 */
package de.augustakom.hurrican.model.exmodules.command;

/**
 * Modell für den Export nach Command
 *
 *
 */
public class CommandExportModel {
    public final static String STANDORT = "Standort";

    public final static String MDU = "mdu";
    public final static String MDUSERIENNUMMER = "mduSeriennummer";
    public final static String MDUTYP = "mduTyp";
    public final static String MDUPORT = "mduPort";
    public final static String LEISTEWOHNUNG = "leisteWohnung";
    public final static String STIFTWOHNUNG = "stiftWohnung";
    public final static String LEISTEDTAG = "leisteDTAG";
    public final static String STIFTDTAG = "stiftDTAG";

    public final static String ONT = "ont";
    public final static String ONTSERIENNUMMER = "ontSerienNummer";
    public final static String ONTTYP = "ontTyp";

    public final static String AGANREDE = "agAnrede";
    public final static String AGNAMEKUNDE = "agNameKunde";
    public final static String AGVORNAMEKUNDE = "agVornameKunde";
    public final static String AGAUFTRAGSNUMMERHURRICAN = "agAuftragsNummerHurrican";
    public final static String AGAUFTRAGSNUMMERTAIFUN = "agAuftragsNummerTaifun";


    public final static String KDKUNDENSTATUS = "kdKundenStatus";
    public final static String KDANREDE = "kdAnrede";
    public final static String KDNAMEANSCHLUSSADRESSE = "kdNameAnschlussAdresse";
    public final static String KDVORNAMEANSCHLUSSADRESSE = "kdVornameAnschlussadresse";
    public final static String KDLAGEANSCHLUSSDOSE = "kdLageAnschlussDose";
    public final static String KDTYPANSCHLUSSDOSE = "kdTypAnschlussDose";
    public final static String VERBINDUNGSBEZEICHNUNG = "verbindungsbezeichnung";

    public final static String EGENDGERAETTYP = "egEndgeraetTyp";
    public final static String EGENDGERAETSERIENNUMMER = "egEndgeratSerienNummer";
    public final static String EGENDGERAETCWMPID = "egEndgeraetCwmpId";

    // Standort
    private String standort = null;

    // FTTB (Technik MDU)
    private String mdu = null;
    private String mduSeriennummer = null;
    private String mduTyp = null;
    private String mduPort = null;
    private String leisteWohnung = null;
    private String stiftWohnung = null;
    private String leisteDTAG = null;
    private String stiftDTAG = null;

    // FTTH (Technik ONT)
    private String ont = null;
    private String ontSerienNummer = null;
    private String ontTyp = null;

    // Auftraggeber
    private String agAnrede = null;
    private String agNameKunde = null;
    private String agVornameKunde = null;
    private String agAuftragsNummerHurrican = null;
    private String agAuftragsNummerTaifun = null;

    // Kunde
    private String kdKundenStatus = null;
    private String kdAnrede = null;
    private String kdNameAnschlussAdresse = null;
    private String kdVornameAnschlussadresse = null;
    private String kdLageAnschlussDose = null;
    private String kdTypAnschlussDose = null;
    private String verbindungsbezeichnung = null;

    // Endgerät
    private String egEndgeraetTyp = null;
    private String egEndgeratSerienNummer = null;
    private String egEndgeraetCwmpId = null;

    /**
     * @return the agStandort
     */
    public String getStandort() {
        return standort;
    }

    /**
     * @param Standort the Standort to set
     */
    public void setStandort(String standort) {
        this.standort = standort;
    }

    /**
     * @return the agAnrede
     */
    public String getAgAnrede() {
        return agAnrede;
    }

    /**
     * @param agAnrede the agAnrede to set
     */
    public void setAgAnrede(String agAnrede) {
        this.agAnrede = agAnrede;
    }

    /**
     * @return the agNameKunde
     */
    public String getAgNameKunde() {
        return agNameKunde;
    }

    /**
     * @param agNameKunde the agNameKunde to set
     */
    public void setAgNameKunde(String agNameKunde) {
        this.agNameKunde = agNameKunde;
    }

    /**
     * @return the agVorNameKunde
     */
    public String getAgVornameKunde() {
        return agVornameKunde;
    }

    /**
     * @param agVornameKunde the agVornameKunde to set
     */
    public void setAgVorNameKunde(String agVornameKunde) {
        this.agVornameKunde = agVornameKunde;
    }

    /**
     * @return the agAuftragsNummerHurrican
     */
    public String getAgAuftragsNummerHurrican() {
        return agAuftragsNummerHurrican;
    }

    /**
     * @param agAuftragsNummerHurrican the agAuftragsNummerHurrican to set
     */
    public void setAgAuftragsNummerHurrican(String agAuftragsNummerHurrican) {
        this.agAuftragsNummerHurrican = agAuftragsNummerHurrican;
    }

    /**
     * @return the agAuftragsNummerTaifun
     */
    public String getAgAuftragsNummerTaifun() {
        return agAuftragsNummerTaifun;
    }

    /**
     * @param agAuftragsNummerTaifun the agAuftragsNummerTaifun to set
     */
    public void setAgAuftragsNummerTaifun(String agAuftragsNummerTaifun) {
        this.agAuftragsNummerTaifun = agAuftragsNummerTaifun;
    }

    /**
     * @return the kdKundenStatus
     */
    public String getKdKundenStatus() {
        return kdKundenStatus;
    }

    /**
     * @param kdKundenStatus the kdKundenStatus to set
     */
    public void setKdKundenStatus(String kdKundenStatus) {
        this.kdKundenStatus = kdKundenStatus;
    }

    /**
     * @return the kdAnrede
     */
    public String getKdAnrede() {
        return kdAnrede;
    }

    /**
     * @param kdAnrede the kdAnrede to set
     */
    public void setKdAnrede(String kdAnrede) {
        this.kdAnrede = kdAnrede;
    }

    /**
     * @return the kdNameAnschlussAdresse
     */
    public String getKdNameAnschlussAdresse() {
        return kdNameAnschlussAdresse;
    }

    /**
     * @param kdNameAnschlussAdresse the kdNameAnschlussAdresse to set
     */
    public void setKdNameAnschlussAdresse(String kdNameAnschlussAdresse) {
        this.kdNameAnschlussAdresse = kdNameAnschlussAdresse;
    }

    /**
     * @return the kdLageAnschlussDose
     */
    public String getKdLageAnschlussDose() {
        return kdLageAnschlussDose;
    }

    /**
     * @param kdLageAnschlussDose the kdLageAnschlussDose to set
     */
    public void setKdLageAnschlussDose(String kdLageAnschlussDose) {
        this.kdLageAnschlussDose = kdLageAnschlussDose;
    }

    /**
     * @return the kdTypAnschlussDose
     */
    public String getKdTypAnschlussDose() {
        return kdTypAnschlussDose;
    }

    /**
     * @param kdTypAnschlussDose the kdTypAnschlussDose to set
     */
    public void setKdTypAnschlussDose(String kdTypAnschlussDose) {
        this.kdTypAnschlussDose = kdTypAnschlussDose;
    }

    /**
     * @return the egEndgeraetTyp
     */
    public String getEgEndgeraetTyp() {
        return egEndgeraetTyp;
    }

    /**
     * @param egEndgeraetTyp the egEndgeraetTyp to set
     */
    public void setEgEndgeraetTyp(String egEndgeraetTyp) {
        this.egEndgeraetTyp = egEndgeraetTyp;
    }

    /**
     * @return the egEndgeratSerienNummer
     */
    public String getEgEndgeratSerienNummer() {
        return egEndgeratSerienNummer;
    }

    /**
     * @param egEndgeratSerienNummer the egEndgeratSerienNummer to set
     */
    public void setEgEndgeratSerienNummer(String egEndgeratSerienNummer) {
        this.egEndgeratSerienNummer = egEndgeratSerienNummer;
    }

    /**
     * @return the egEndgeraetCwmpId
     */
    public String getEgEndgeraetCwmpId() {
        return egEndgeraetCwmpId;
    }

    /**
     * @param egEndgeraetCwmpId the egEndgeraetCwmpId to set
     */
    public void setEgEndgeraetCwmpId(String egEndgeraetCwmpId) {
        this.egEndgeraetCwmpId = egEndgeraetCwmpId;
    }

    /**
     * @return the mdu
     */
    public String getMdu() {
        return mdu;
    }

    /**
     * @param mdu the mdu to set
     */
    public void setMdu(String mdu) {
        this.mdu = mdu;
    }

    /**
     * @return the mduSeriennummer
     */
    public String getMduSeriennummer() {
        return mduSeriennummer;
    }

    /**
     * @param mduSeriennummer the mduSeriennummer to set
     */
    public void setMduSeriennummer(String mduSeriennummer) {
        this.mduSeriennummer = mduSeriennummer;
    }

    /**
     * @return the mduTyp
     */
    public String getMduTyp() {
        return mduTyp;
    }

    /**
     * @param mduTyp the mduTyp to set
     */
    public void setMduTyp(String mduTyp) {
        this.mduTyp = mduTyp;
    }

    /**
     * @return the mduPort
     */
    public String getMduPort() {
        return mduPort;
    }

    /**
     * @param mduPort the mduPort to set
     */
    public void setMduPort(String mduPort) {
        this.mduPort = mduPort;
    }

    /**
     * @return the leisteWohnung
     */
    public String getLeisteWohnung() {
        return leisteWohnung;
    }

    /**
     * @param leisteWohnung the leisteWohnung to set
     */
    public void setLeisteWohnung(String leisteWohnung) {
        this.leisteWohnung = leisteWohnung;
    }

    /**
     * @return the stiftWohnung
     */
    public String getStiftWohnung() {
        return stiftWohnung;
    }

    /**
     * @param stiftWohnung the stiftWohnung to set
     */
    public void setStiftWohnung(String stiftWohnung) {
        this.stiftWohnung = stiftWohnung;
    }

    /**
     * @return the leisteDTAG
     */
    public String getLeisteDTAG() {
        return leisteDTAG;
    }

    /**
     * @param leisteDTAG the leisteDTAG to set
     */
    public void setLeisteDTAG(String leisteDTAG) {
        this.leisteDTAG = leisteDTAG;
    }

    /**
     * @return the stiftDTAG
     */
    public String getStiftDTAG() {
        return stiftDTAG;
    }

    /**
     * @param stiftDTAG the stiftDTAG to set
     */
    public void setStiftDTAG(String stiftDTAG) {
        this.stiftDTAG = stiftDTAG;
    }

    /**
     * @return the ont
     */
    public String getOnt() {
        return ont;
    }

    /**
     * @param ont the ont to set
     */
    public void setOnt(String ont) {
        this.ont = ont;
    }

    /**
     * @return the ontSerienNummer
     */
    public String getOntSerienNummer() {
        return ontSerienNummer;
    }

    /**
     * @param ontSerienNummer the ontSerienNummer to set
     */
    public void setOntSerienNummer(String ontSerienNummer) {
        this.ontSerienNummer = ontSerienNummer;
    }

    /**
     * @return the ontTyp
     */
    public String getOntTyp() {
        return ontTyp;
    }

    /**
     * @param ontTyp the ontTyp to set
     */
    public void setOntTyp(String ontTyp) {
        this.ontTyp = ontTyp;
    }

    /**
     * @return the kdVornameAnschlussadresse
     */
    public String getKdVornameAnschlussadresse() {
        return kdVornameAnschlussadresse;
    }

    /**
     * @param kdVornameAnschlussadresse the kdVornameAnschlussadresse to set
     */
    public void setKdVornameAnschlussadresse(String kdVornameAnschlussadresse) {
        this.kdVornameAnschlussadresse = kdVornameAnschlussadresse;
    }

    /**
     * @return the verbindungsbezeichnung
     */
    public String getVerbindungsbezeichnung() {
        return verbindungsbezeichnung;
    }

    /**
     * @param verbindungsbezeichnung the verbindungsbezeichnung to set
     */
    public void setVerbindungsbezeichnung(String verbindungsbezeichnung) {
        this.verbindungsbezeichnung = verbindungsbezeichnung;
    }

}
