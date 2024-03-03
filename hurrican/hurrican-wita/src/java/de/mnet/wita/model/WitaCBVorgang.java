/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 12:31:44
 */
package de.mnet.wita.model;

import static com.google.common.collect.Sets.*;
import static de.mnet.wita.model.WitaCBVorgang.AbmState.*;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.IndexColumn;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.service.holiday.DateCalculationHelper.DateCalculationMode;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.WitaCBVorgangAnlage;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.activiti.CanOpenActivitiWorkflow;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.validators.MontagehinweisValid;
import de.mnet.wita.validators.groups.V1;

/**
 * Subclass of CB-Vorgang adding WITA-Specifics
 */
@Entity
@DiscriminatorValue("WITA")
@MontagehinweisValid
public class WitaCBVorgang extends CBVorgang implements CanOpenActivitiWorkflow {

    private static final long serialVersionUID = 1983978229071854145L;

    /**
     * Projektkenner-String fuer Anbieterwechsel (PV oder Verbundleistung), den DTAG benoetigt wenn der Anbieterwechsel
     * zu einem Provider stattfindet, der die WITA Schnittstelle nicht implementiert hat.
     */
    public static final String PK_FOR_ANBIETERWECHSEL_CARRIER_WITHOUT_WITA = "PV TNBab";

    /**
     * Prefix fuer Montagehinweis, um einen Anbieterwechsel nach §46TKG zu markieren.
     */
    public static final String ANBIETERWECHSEL_46TKG = "#abw#";

    private GeschaeftsfallTyp witaGeschaeftsfallTyp;

    public static final String VORABSTIMMUNGSID = "vorabstimmungsId";

    /**
     * aktuelles Aenderungskennzeichen<br/>
     * <p/>
     * Beispiel: <ul> <li>Neubestellung geht raus -> Standard</li> <li>Storno geht raus -> Storno</li> <li>Abbm auf
     * Storno -> Standard</li> </ul>
     */
    private AenderungsKennzeichen aenderungsKennzeichen = AenderungsKennzeichen.STANDARD;
    public static final String AENDERUNGS_KENNZEICHEN = "aenderungsKennzeichen";

    /**
     * Aenderungskennzeichen der letzten gesendeten Nachricht<br/>
     * <p/>
     * Beispiel: <ul> <li>Neubestellung geht raus -> Standard</li> <li>Storno geht raus -> Storno</li> <li>Abbm auf
     * Storno -> Storno</li> </ul>
     */
    private AenderungsKennzeichen letztesGesendetesAenderungsKennzeichen;
    private Long auftragsKlammer;
    private TamUserTask tamUserTask;
    private AbmState abmState = NO_ABM;
    private Zeitfenster realisierungsZeitfenster;
    private List<WitaCBVorgangAnlage> anlagen = new ArrayList<>(0);
    private String projektKenner;
    private String kopplungsKenner;
    private String vorabstimmungsId;
    private Uebertragungsverfahren previousUebertragungsVerfahren;
    private Long statusLast;
    /**
     * DN__NOs fuer die Rufnummerportierung
     */
    private Set<Long> rufnummerIds = newHashSet();
    /**
     * Gibt an, wenn ein Storno/eine Terminverschiebung nicht als neuer Request geschickt wird und stattdessesn der
     * existierende Request (Auftrag, Storno oder TV) geändert/storniert wurde.
     */
    private boolean requestOnUnsentRequest = false;

    /**
     * Speichert das letzte bekannte Vorgabedatum, um nach erfolgloser ABBM zuruecksetzen zu koennen.
     */
    private Date previousVorgabeMnet;

    public static final String CB_VORGANG_REF_ID = "cbVorgangRefId";
    private Long cbVorgangRefId;

    public static WitaCBVorgang createCompletelyEmptyInstance() {
        WitaCBVorgang instance = new WitaCBVorgang();
        instance.setAbmState(null);
        instance.setAnlagen(null);
        instance.setRufnummerIds(null);
        instance.setAenderungsKennzeichen(null);
        return instance;
    }

    @Transient
    public boolean isRequestOnUnsentRequest() {
        return requestOnUnsentRequest;
    }

    public void setRequestOnUnsentRequest(boolean requestOnUnsentRequest) {
        this.requestOnUnsentRequest = requestOnUnsentRequest;
    }

    @Transient
    public Boolean getPrio() {
        return getSecondAbmReceived() || getAbbmOnAbm() || isTerminverschiebungWithoutAnswer();
    }

    @Transient
    public Boolean getSecondAbmReceived() {
        return AbmState.SECOND_ABM.equals(getAbmState());
    }

    @Transient
    public Boolean getAbbmOnAbm() {
        // Bei einer Abbm gibt es kein ReturnRealDate, aber ReturnOk ist false
        return Boolean.FALSE.equals(getReturnOk()) && (getReturnRealDate() != null);
    }

    @Transient
    public boolean isStandardPositiv() {
        return BooleanTools.nullToFalse(getReturnOk())
                && AenderungsKennzeichen.STANDARD.equals(getAenderungsKennzeichen());
    }

    @Transient
    private boolean isTerminverschiebungWithoutAnswer() {
        if ((AenderungsKennzeichen.TERMINVERSCHIEBUNG == aenderungsKennzeichen) && !isAnswered()) {
            LocalDate submitted = DateConverterUtils.asLocalDate(getSubmittedAt()).atStartOfDay(ZoneId.systemDefault()).toLocalDate();
            return DateCalculationHelper.getDaysBetween(submitted, LocalDate.now(), DateCalculationMode.WORKINGDAYS) >= 3;
        }
        return false;
    }

    @Transient
    public boolean aenderungsKennzeichenIsDifferent() {
        return getAenderungsKennzeichen() != getLetztesGesendetesAenderungsKennzeichen();
    }

    /**
     * Prueft, ob es sich bei dem WITA Vorgang um einen Anbieterwechsel handelt. Dies ist dann der Fall, wenn
     * 
     * <pre>
     *     - WITA GF ist Verbundleistung bzw. Anbieterwechsel
     *     ODER
     *     - WITA GF ist 'NEU' und Montageleistung beinhaltet "#abw#"
     * </pre>
     * 
     * @return
     */
    @Transient
    public boolean isWitaGfAnbieterwechsel() {
        if (GeschaeftsfallTyp.VERBUNDLEISTUNG.equals(getWitaGeschaeftsfallTyp())
                || GeschaeftsfallTyp.PROVIDERWECHSEL.equals(getWitaGeschaeftsfallTyp())) {
            return true;
        }
        else if (GeschaeftsfallTyp.BEREITSTELLUNG.equals(getWitaGeschaeftsfallTyp()) &&
                StringUtils.containsIgnoreCase(getMontagehinweis(), ANBIETERWECHSEL_46TKG)) {
            return true;
        }

        return false;
    }

    @Column(name = "REALISERUNG_ZEITFENSTER")
    @Enumerated(EnumType.STRING)
    public Zeitfenster getRealisierungsZeitfenster() {
        return realisierungsZeitfenster;
    }

    public void setRealisierungsZeitfenster(Zeitfenster realisierungsZeitfenster) {
        this.realisierungsZeitfenster = realisierungsZeitfenster;
    }

    @Override
    public void close() {
        super.close();
        resetAbmState();
    }

    @Override
    @Transient
    public String getBusinessKey() {
        return getCarrierRefNr();
    }

    @Override
    @Transient
    public boolean isStorno() {
        return AenderungsKennzeichen.STORNO == getAenderungsKennzeichen();
    }

    @Column(name = "WITA_GESCHAEFTSFALL_TYP")
    @NotNull
    @Enumerated(EnumType.STRING)
    public GeschaeftsfallTyp getWitaGeschaeftsfallTyp() {
        return witaGeschaeftsfallTyp;
    }

    public void setWitaGeschaeftsfallTyp(GeschaeftsfallTyp witaGeschaeftsfallTyp) {
        this.witaGeschaeftsfallTyp = witaGeschaeftsfallTyp;
    }

    @Column(name = "WITA_AENDERUNGSKENNZEICHEN")
    @NotNull
    @Enumerated(EnumType.STRING)
    public AenderungsKennzeichen getAenderungsKennzeichen() {
        return aenderungsKennzeichen;
    }

    public void setAenderungsKennzeichen(AenderungsKennzeichen aenderungsKennzeichen) {
        this.aenderungsKennzeichen = aenderungsKennzeichen;
    }

    @Column(name = "WITA_AENDERUNGSKZ_LAST")
    @Enumerated(EnumType.STRING)
    public AenderungsKennzeichen getLetztesGesendetesAenderungsKennzeichen() {
        return letztesGesendetesAenderungsKennzeichen;
    }

    public void setLetztesGesendetesAenderungsKennzeichen(AenderungsKennzeichen letztesGesendetesAenderungsKennzeichen) {
        this.letztesGesendetesAenderungsKennzeichen = letztesGesendetesAenderungsKennzeichen;
    }

    @Column(name = "WITA_AUFTRAG_KLAMMER")
    @Digits(fraction = 0, integer = 10)
    public Long getAuftragsKlammer() {
        return auftragsKlammer;
    }

    public void setAuftragsKlammer(Long auftragsKlammer) {
        this.auftragsKlammer = auftragsKlammer;
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_TASK_ID", nullable = true)
    public TamUserTask getTamUserTask() {
        return tamUserTask;
    }

    public void setTamUserTask(TamUserTask tamUserTask) {
        this.tamUserTask = tamUserTask;
    }

    @Column(name = "ABM_STATE")
    @NotNull
    @Enumerated(EnumType.STRING)
    public AbmState getAbmState() {
        return abmState;
    }

    public void setAbmState(AbmState abmState) {
        this.abmState = abmState;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "CBVORGANG_ID", nullable = false)
    @BatchSize(size = 1000)
    public List<WitaCBVorgangAnlage> getAnlagen() {
        return anlagen;
    }

    public void setAnlagen(List<WitaCBVorgangAnlage> anlagen) {
        this.anlagen = anlagen;
    }

    public void addAnlage(WitaCBVorgangAnlage anlage) {
        this.anlagen.add(anlage);
    }

    @Column(name = "PROJEKT_KENNER")
    @Size(max = 30)
    public String getProjektKenner() {
        return projektKenner;
    }

    public void setProjektKenner(String projektKenner) {
        this.projektKenner = projektKenner;
    }

    @Column(name = "KOPPLUNG_KENNER")
    @Size(max = 30)
    public String getKopplungsKenner() {
        return kopplungsKenner;
    }

    public void setKopplungsKenner(String kopplungsKenner) {
        this.kopplungsKenner = kopplungsKenner;
    }

    @Size(groups = V1.class, min = 16, max = 21)
    @Column(name = "VORABSTIMMUNGSID")
    public String getVorabstimmungsId() {
        return vorabstimmungsId;
    }

    public void setVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
    }

    /**
     * Gibt vorheriges Uebertragungsverfahren an, das am Equipement hing bevor eine LMAE beantragt wurde.
     */
    @Column(name = "PRE_UETV")
    @Enumerated(EnumType.STRING)
    public Uebertragungsverfahren getPreviousUebertragungsVerfahren() {
        return previousUebertragungsVerfahren;
    }

    public void setPreviousUebertragungsVerfahren(Uebertragungsverfahren previousUebertragungsVerfahren) {
        this.previousUebertragungsVerfahren = previousUebertragungsVerfahren;
    }

    @Column(name = "VORGABE_MNET_LAST")
    @Temporal(TemporalType.DATE)
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Dateobjekt wird nicht veraendert!")
    public Date getPreviousVorgabeMnet() {
        return previousVorgabeMnet;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Dateobjekt wird nicht veraendert!")
    public void setPreviousVorgabeMnet(Date previousVorgabeMnet) {
        this.previousVorgabeMnet = previousVorgabeMnet;
    }

    @ElementCollection(targetClass = Long.class)
    @JoinTable(name = "T_CB_VORGANG_RUFNUMMERN", joinColumns = @JoinColumn(name = "CB_VORGANG_ID"))
    @IndexColumn(name = "ID", base = 1)
    @Column(name = "RUFNUMMER_ID", nullable = false)
    public Set<Long> getRufnummerIds() {
        return rufnummerIds;
    }

    public void setRufnummerIds(Set<Long> rufnummerIds) {
        this.rufnummerIds = rufnummerIds;
    }

    @Column(name = "STATUS_LAST")
    public Long getStatusLast() {
        return statusLast;
    }

    public void setStatusLast(Long statusLast) {
        this.statusLast = statusLast;
    }

    @Column(name = "CBVORGANG_REF_ID")
    public Long getCbVorgangRefId() {
        return cbVorgangRefId;
    }

    public void setCbVorgangRefId(Long cbVorgangRefId) {
        this.cbVorgangRefId = cbVorgangRefId;
    }

    @Transient
    public boolean isHvtToKvz() {
        return cbVorgangRefId != null;
    }

    public void nextAbm() {
        setAbmState(abmState.next());
    }

    public void resetAbmState() {
        setAbmState(NO_ABM);
    }

    public enum AbmState {
        NO_ABM,
        FIRST_ABM,
        SECOND_ABM;

        AbmState next() {
            switch (this) {
                case NO_ABM:
                    return FIRST_ABM;
                default:
                    return SECOND_ABM;
            }
        }
    }

    /**
     * Ermittelt aus den Konstanten fuer einen CBVorgangs-Typ die moeglichen WITA Geschaeftsfall-Typen.
     *
     * @param cbVorgangTyp
     * @param vorabstimmungCarrier
     * @return
     */
    public static GeschaeftsfallTyp[] transformCbVorgangToGeschaeftsfallTyp(Long cbVorgangTyp, Long vorabstimmungCarrier) {
        if (CBVorgang.TYP_REX_MK.equals(cbVorgangTyp)) {
            return new GeschaeftsfallTyp[] { GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG };
        }
        else if (CBVorgang.TYP_KUENDIGUNG.equals(cbVorgangTyp)) {
            return new GeschaeftsfallTyp[] { GeschaeftsfallTyp.KUENDIGUNG_KUNDE };
        }
        else if (CBVorgang.TYP_NEU.equals(cbVorgangTyp) || CBVorgang.TYP_HVT_KVZ.equals(cbVorgangTyp)) {
            return new GeschaeftsfallTyp[] { GeschaeftsfallTyp.BEREITSTELLUNG };
        }
        else if (CBVorgang.TYP_ANBIETERWECHSEL.equals(cbVorgangTyp)) {
            // Sonderfall PV/VBL: bei Carrier=DTAG --> VBL; sonst PV
            if (Carrier.ID_DTAG.equals(vorabstimmungCarrier)) {
                return new GeschaeftsfallTyp[] { GeschaeftsfallTyp.VERBUNDLEISTUNG };
            }
            return new GeschaeftsfallTyp[] { GeschaeftsfallTyp.PROVIDERWECHSEL };
        }
        else if (CBVorgang.TYP_PORTWECHSEL.equals(cbVorgangTyp)) {
            return new GeschaeftsfallTyp[] {
                    GeschaeftsfallTyp.LEISTUNGS_AENDERUNG,
                    GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG,
                    GeschaeftsfallTyp.PORTWECHSEL };
        }
        return null;
    }
}
