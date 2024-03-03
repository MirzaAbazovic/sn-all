/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2011 11:53:59
 */
package de.mnet.wita.model;

import java.util.*;

/**
 * Interface zur Anzeige von Usertasks zu WITA-Vorgaenngen.
 */
public interface Vorgang extends CbTask {

    /**
     * Gibt an, ob der Task wichtig ist. Wird typischerweise farblich hervorgehoben.
     */
    boolean isImportant();

    /**
     * Wann wurde der Task zuletzt bearbeitet.
     */
    Date getLetzteAenderung();

    /**
     * Hurrican-Usertask zur Bearbeitung durch einen M-net-Mitarbeiter.
     */
    UserTask getUserTask();

    /**
     * Auftrag-Bearbeiter oder Bearbeiter des CB-Vorgangs.
     */
    String getAuftragBearbeiter();

    /**
     * Team des Auftrag-Bearbeiters.
     *
     * @return
     */
    String getAuftragBearbeiterTeam();

    /**
     * Team des Task-Bearbeiters.
     *
     * @return
     */
    String getTaskBearbeiterTeam();

    /**
     * Gibt an, ob es sich bei dem Vorgang um einen Klaerfall handelt *
     */
    Boolean isKlaerfall();

    /**
     * Markiert den Vorgang als Klearfall *
     */
    void setKlaerfall(Boolean klaerfall);

    /**
     * Gibt die Bemerkung zum Klaerfall zurück *
     */
    String getKlaerfallBemerkung();

    /**
     * Setzt die Klaerfallbemerkung *
     */
    void setKlaerfallBemerkung(String klaerfallBemerkung);

    /**
     * Spezial Mehtode for ReflectionTabels *
     */
    boolean isKlaerfallSet();
}
