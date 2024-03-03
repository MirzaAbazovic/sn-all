/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2011 12:08:16
 */
package de.mnet.wita.message;

import org.apache.commons.lang.NotImplementedException;

import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.EntgeltMeldung;
import de.mnet.wita.message.meldung.EntgeltMeldungPv;
import de.mnet.wita.message.meldung.ErgebnisMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldungKunde;
import de.mnet.wita.message.meldung.ErledigtMeldungPv;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldungPv;

/**
 * Enum, um Typen von Meldungen abzubilden. Eine Meldung ist dabei eine für einen neuen oder laufenden Workflow
 * relevante Nachricht.
 */
public enum MeldungsType {
    // @formatter:off
    ABBM(   "ABBM",     AbbruchMeldung.class,                       "Abbruchmeldung"),
    ABM(    "ABM",      AuftragsBestaetigungsMeldung.class,         "Auftragsbestätigung"),
    ABBM_PV("ABBM-PV",  AbbruchMeldungPv.class,                     "Abbruchmeldung an abgebenden Provider"),
    ABM_PV( "ABM-PV",   AuftragsBestaetigungsMeldungPv.class,       "Auftragsbestätigung an abgebenden Provider"),
    AKM_PV( "AKM-PV",   AnkuendigungsMeldungPv.class,               "Ankündigung an abgebenden Provider"), /* => neuer Workflow */
    ENTM(   "ENTM",     EntgeltMeldung.class,                       "Entgeltmeldung"),
    ENTM_PV("ENTM-PV",  EntgeltMeldungPv.class,                     "Entgeltmeldung an abgebenden Provider"),
    ERLM_PV("ERLM-PV",  ErledigtMeldungPv.class,                    "Erledigungssmeldung an abgebenden Provider"),
    ERGM(   "ERGM",     ErgebnisMeldung.class,                      "Ergebnismeldung"),
    ERLM(   "ERLM",     ErledigtMeldung.class,                      "Erledigungsmeldung"),
    ERLM_K( "ERLM-K",   ErledigtMeldungKunde.class,                 "Erledigungsmeldung Kunde"),
    QEB(    "QEB",      QualifizierteEingangsBestaetigung.class,    "Qualifizierte Eingangsbestätigung"),
    RUEM_PV("RUEM-PV",  RueckMeldungPv.class,                       "Rückmeldung des abgebenden Provider"),
    TAM(    "TAM",      TerminAnforderungsMeldung.class,            "Terminanforderungsmeldung"),
    VZM(    "VZM",      VerzoegerungsMeldung.class,                 "Verzögerungsmeldung"),
    VZM_PV( "VZM-PV",   VerzoegerungsMeldungPv.class,               "Verzögerungsmeldung an abgebenden Provider"),

    TEQ(    "TEQ",      null,                                       "Technische Quittung"), /* keine Meldungsklasse! */
    STORNO( "STORNO",   null,                                       "Stornierung"),         /* keine Meldungsklasse! STORNO ist ein MnetWitaRequest */
    TV(     "TV",       null,                                       "Terminverschiebung");  /* keine Meldungsklasse! TV ist ein MnetWitaRequest */
    // @formatter:on

    private final String value;
    private final String longName;
    private final Class<? extends Meldung<?>> meldungsClass;

    private MeldungsType(String value, Class<? extends Meldung<?>> meldungsClass, String longName) {
        this.value = value;
        this.meldungsClass = meldungsClass;
        this.longName = longName;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }

    public String getLongName() {
        return longName;
    }

    public Class<? extends Meldung<?>> getMeldungClass() {
        if (meldungsClass == null) {
            throw new NotImplementedException("No meldungsclass available");
        }
        return meldungsClass;
    }

    public static MeldungsType of(String requestMeldungstyp) {
        for (MeldungsType meldungsType : MeldungsType.values()) {
            if (meldungsType.value.equals(requestMeldungstyp)) {
                return meldungsType;
            }
        }
        throw new IllegalArgumentException("There exists no Meldungstyp for " + requestMeldungstyp);
    }
}
