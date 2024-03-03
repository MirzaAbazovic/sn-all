/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2004 08:58:58
 */
package de.augustakom.hurrican.model.cc;

import com.google.common.collect.ImmutableList;

import de.augustakom.common.tools.lang.NumberTools;


/**
 * Ueber dieses Modell wird ein Auftragstatus definiert.
 */
public class AuftragStatus extends AbstractCCIDModel {

    private static final long serialVersionUID = 8337885016305387740L;

    /**
     * Status des Auftrags ist undefiniert (0).
     */
    public static final Long UNDEFINIERT = 0L;
    /**
     * Auftrag befindet sich in der Erfassung (1000).
     */
    public static final Long ERFASSUNG = 1000L;
    /**
     * Auftrag wurde aus Taifun uebernommen (1100).
     */
    public static final Long AUS_TAIFUN_UEBERNOMMEN = 1100L;
    /**
     * Auftrag wurde storniert (1150).
     */
    public static final Long STORNO = 1150L;
    /**
     * Auftrag wird von SCV erfasst (1200).
     */
    public static final Long ERFASSUNG_SCV = 1200L;
    /**
     * Auftrag befindet sich in der Projektierung (2000).
     */
    public static final Long PROJEKTIERUNG = 2000L;
    /**
     * Projektierung fuer den Auftrag ist erledigt - BA kann erstellt werden.
     */
    public static final Long PROJEKTIERUNG_ERLEDIGT = 2800L;
    /**
     * CuDa-Bestellung wurde fuer Auftrag ausgeloest (3300).
     */
    public static final Long BESTELLUNG_CUDA = 3300L;
    /**
     * Auftrag wurde auf 'Absage' gesetzt (3400).
     */
    public static final Long ABSAGE = 3400L;
    /**
     * Auftrag ist ein Internet-Auftrag (3500).
     */
    public static final Long INTERNET_AUFTRAG = 3500L;
    /**
     * Fuer den Auftrag wird ein Anschreiben an den Kunden verschickt (3600).
     */
    public static final Long ANSCHREIBEN_KUNDEN_ERFASSUNG = 3600L;
    /**
     * Telefonbucheintrag wird fuer den Kunden erstellt/beantragt (3700).
     */
    public static final Long TELEFONBUCH = 3700L;
    /**
     * Auftrag befindet sich in der technischen Realisierung (4000).
     */
    public static final Long TECHNISCHE_REALISIERUNG = 4000L;

    /**
     * Der Auftrag befindet sich z.Z. in Betrieb (6000).
     */
    public static final Long IN_BETRIEB = 6000L;
    /**
     * Auftrag wird geaendert (6100).
     */
    public static final Long AENDERUNG = 6100L;
    /**
     * Der Auftrag befindet sich in einer Aenderung und ist z.Z. im Umlauf (6200).
     */
    public static final Long AENDERUNG_IM_UMLAUF = 6200L;
    /**
     * Auftrag wird gekuendigt (9000).
     */
    public static final Long KUENDIGUNG = 9000L;
    /**
     * Kuendigungsauftrag wird von SCV erfasst (9100).
     */
    public static final Long KUENDIGUNG_ERFASSEN = 9100L;
    /**
     * Kuendigungsauftrag befindet sich in der technischen Realisierung (9105).
     */
    public static final Long KUENDIGUNG_TECHN_REAL = 9105L;
    /**
     * CuDa-Kuendigung wird fuer den Auftrag durchgefuehrt (9300).
     */
    public static final Long KUENDIGUNG_CUDA = 9300L;
    /**
     * Anschreiben an den Kunden mit Information ueber den Kuendigungsstatus (9700).
     */
    public static final Long ANSCHREIBEN_KUNDE_KUEND = 9700L;
    /**
     * Kuendigung ist vollstaendig abgeschlossen (9800).
     */
    public static final Long AUFTRAG_GEKUENDIGT = 9800L;
    /**
     * Konsolididerter/migrierter Auftrag.
     */
    public static final Long KONSOLIDIERT = 10000L;

    public static final ImmutableList<Long> VALID_AUFTRAG_STATI = new ImmutableList.Builder<Long>()
            .add(ERFASSUNG)
            .add(AUS_TAIFUN_UEBERNOMMEN)
            .add(STORNO)
            .add(ERFASSUNG_SCV)
            .add(PROJEKTIERUNG)
            .add(PROJEKTIERUNG_ERLEDIGT)
            .add(BESTELLUNG_CUDA)
            .add(ABSAGE)
            .add(INTERNET_AUFTRAG)
            .add(ANSCHREIBEN_KUNDEN_ERFASSUNG)
            .add(TELEFONBUCH)
            .add(TECHNISCHE_REALISIERUNG)
            .add(IN_BETRIEB)
            .add(AENDERUNG)
            .add(AENDERUNG_IM_UMLAUF)
            .add(KUENDIGUNG)
            .add(KUENDIGUNG_ERFASSEN)
            .add(KUENDIGUNG_TECHN_REAL)
            .add(KUENDIGUNG_CUDA)
            .add(ANSCHREIBEN_KUNDE_KUEND)
            .add(AUFTRAG_GEKUENDIGT)
            .add(KONSOLIDIERT)
            .build();

    private String statusText = null;

    /**
     * Ueberprueft, ob es sich bei dem Status um einen der folgenden Stati handelt: <br> - Storno - Absage -
     * Konsolidiert
     *
     * @return true, wenn der angegebene Status einer der oben erwaehnten Stati ist.
     */
    public static boolean isNotValid(Long statusId) {
        return NumberTools.isIn(statusId, new Number[] { STORNO, ABSAGE, KONSOLIDIERT });
    }

    /**
     * @return Returns the statusText.
     */
    public String getStatusText() {
        return statusText;
    }

    /**
     * @param statusText The statusText to set.
     */
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
}
