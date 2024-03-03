/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2012 14:12:50
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 * Modell bildet ein Feature ab.
 *
 *
 */
@Entity
@Table(name = "T_FEATURE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_FEATURE_0", allocationSize = 1)
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class Feature extends AbstractCCIDModel {

    private static final long serialVersionUID = 1939852747161711450L;

    public enum FeatureName {
        /**
         * Feature Name "ORDER AUTOMATION".
         */
        ORDER_AUTOMATION,

        /**
         * Dummy Feature Name "DUMMY_ORDER AUTOMATION" - nur fuer Tests.
         */
        DUMMY_ORDER_AUTOMATION,

        /**
         * Feature "WITA_CANCELLATION_AUTOMATION gibt an, ob WITA Kuendigungen automatisch abgeschlossen werden duerfen
         */
        WITA_CANCELLATION_AUTOMATION,

        /**
         * Feature "WITA_ORDER_AUTOMATION gibt an, ob WITA Bestellungen automatisch abgeschlossen werden duerfen
         */
        WITA_ORDER_AUTOMATION,

        /**
         * Feature WBCI_KFT_TEST_MODE gibt an, ob die weiter Processierung einer WBCI Nachricht übersprungen werden
         * soll. Die weiter Processierung (Autoassign Taifun Auftrag, ABBM-TR automatisch verschicken) darf wehrend des
         * KFTs nicht gemacht werden.
         */
        WBCI_KFT_TEST_MODE,

        /**
         * Feature WBCI_WITA_AUTO_PROCESSING gibt an, ob innerhalb des elektronischen Vorabstimmungsprozesses Nachrichten
         * automatisiert weiter prozessiert werden (z.B. nach einer eingehende RUEM-VA das Versenden einer AKM-TR und
         * die Auslösung des WITA Vorgangs).
         * Wenn der Autoprozess-Modus deaktiviert ist, sind sämtliche automatische Prozessierungsschritte ausgeschaltet.
         */
        WBCI_WITA_AUTO_PROCESSING,

        /**
         * Feature WBCI_ENABLED gibt an, ob der elektronische Vorabstimmungsprozess freigeschaltet ist
         */
        WBCI_ENABLED,

        /**
         * Feature, um die REX-MK Funktionen der WITA zu aktivieren. (Mit GoLive von WBCI mit der DTAG sollen die REX-MK
         * Funktionen deaktiviert werden, da dieser WITA GF durch eine WBCI Vorabstimmung nicht mehr notwendig ist.)
         */
        WITA_REX_MK,

        /**
         * Feature WBCI_PHONETIC_SEARCH gibt an, ob die Suche nach Geo-Lokationen in WBCI phonetisch durchgeführt werden
         * soll.
         */
        WBCI_PHONETIC_SEARCH,

        /**
         * Feature HVT_KVZ_RQW_AUTOMATION gibt an, ob die RQW-Automatisierung für die HVT-KVZ-Aufträge aktiviert ist.
         */
        HVT_KVZ_RQW_AUTOMATION,

        /**
         * Feature WBCI_RUEMVA_AUTO_PROCESSING gibt an, ob die RQW-Automatisierung für die RUEM_VA Verarbeitung
         * Fall aktiviert ist. <br/>
         * Bei dieser 'Automatisierung' handelt es sich darum, ob die RUEM-VA Daten 'automatisch' (per GUI Aktion)
         * nach Taifun uebernommen werden koennen.
         */
        WBCI_RUEMVA_AUTO_PROCESSING,

        /**
         * Feature WBCI_RRNP_AUTO_PROCESSING gibt an, ob die RQW-Automatisierung für die nachträgliche RRNP Verarbeitung
         * aktiviert ist.
         */
        WBCI_RRNP_AUTO_PROCESSING,

        /**
         * Feature WBCI_AKMTR_AUTO_PROCESSING gibt an, ob die RQW-Automatisierung für die AKM_TR Verarbeitung
         * aktiviert ist. <br/>
         * Bei dieser 'Automatisierung' handelt es sich darum, ob die AKM-TR Daten (speziell PKI-Kennung) 'automatisch'
         * (per GUI Aktion) nach Taifun uebernommen werden koennen.
         */
        WBCI_AKMTR_AUTO_PROCESSING,

        /**
         * Feature WBCI_TVS_VA_AUTO_PROCESSING gibt an, ob die Automatisierung für die TVS-VA-Verarbeitung aktiviert
         * ist. In diesem Fall kann man nach dem Erhalt der ERLM zu dem TVS-VA die entsprechenden Daten in Taifun
         * automatisiert aktualisieren. <br/>
         * Bei dieser 'Automatisierung' handelt es sich darum, ob Daten aus einer eingegangenen TV
         * 'automatisch' (per GUI Aktion) nach Taifun uebernommen werden koennen.
         */
        WBCI_TVS_VA_AUTO_PROCESSING,

        /**
         * Feature WBCI_DIAL_NUMBER_UPDATE gibt an, ob eine Rufnummer ueber Elektra in Taifun angelegt bzw.
         * geloescht werden kann/darf.
         */
        WBCI_DIAL_NUMBER_UPDATE,

        /**
         * Dieses Feature Flag gibt an, ob im abgebenden WBCI Fall nach einer versendeten RUEM-VA weitere Aktionen
         * (z.B. Taifun Auftrag kuendigung; Kundenanschreiben erstellen) automatisch durchgefuehrt werden duerfen.
         * Das Flag wird nur zur Steuerung der GUI verwendet, um dem User die Moeglichkeit zu geben, den zugehoerigen
         * WBCI GF als {@link WbciGeschaeftsfall#automatable} zu markieren.
         */
        WBCI_ABGEBEND_RUEMVA_AUTO_PROCESSING,

        /**
         * Dieses Feature Flag steuert im Storno-Dialog (nur abgebender Fall!), ob eine CheckBox fuer
         * {@link WbciGeschaeftsfall#automatable} angeboten wird.
         */
        WBCI_ABGEBEND_STR_AUF_AUTO_PROCESSING,

        /**
         * Dieses Feature Flag gibt an, ob der Service SerialNumberFFMService aktiv ist
         */
        SERIALNUMBER_FFM_SERVICE,

        /**
         * Feature Flag für Abschaltung des NGN Portierungsservices
         */
        NGN_PORTIERING_WEB_SERVICE,

        /**
         * Neuen Eintrag Portierungskennung NGN. Umschaltung von der bisherigen Funktionalität zur neuen
         */
        NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED,

        /**
         * WITA mit Version 10 verwenden
         */
        WITA_V10_ENABLED,


        /**
         * Experimentelle neue Implementierung um die Rechnungen zum Portal zu kopieren
         * Ist notwendig, da die Funktion in der Dev-Umgebung nur schlecht testbar ist.
         * Falls es sich bewaehrt, kann das Flag ausgebaut und die neue Implementierung uebernommen werden
         */
        FAST_COPY_BILL_TO_PORTAL_IMPORT,


        /**
         * Im WBCI Dialog: Anzeige PV senden im Kontextmenu und Anzeige Tab Wholesale
         */
        WHOLESALE_PV,
    }

    private FeatureName name;
    private Boolean flag;

    @Enumerated(EnumType.STRING)
    @Column(name = "NAME")
    @NotNull
    public FeatureName getName() {
        return name;
    }

    public void setName(FeatureName name) {
        this.name = name;
    }

    @Column(name = "FLAG")
    @NotNull
    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

}
