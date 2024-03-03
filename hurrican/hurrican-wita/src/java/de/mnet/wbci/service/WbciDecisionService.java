/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.13
 */
package de.mnet.wbci.service;

import java.util.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.mnet.wbci.model.DecisionVO;
import de.mnet.wbci.model.MeldungPositionTyp;
import de.mnet.wbci.model.Rufnummernportierung;

/**
 * WBCI Service, um die Daten einer erhaltenen Vorabstimmung (M-net = abgebender Provider) mit den Daten aus dem
 * zugeordneten Taifun Auftrag zu vergleichen. <br>
 */
public interface WbciDecisionService extends WbciService {

    /**
     * Stellt die in einer Vorabstimmung angegebenen Daten (wie z.B. Standort, Kunde, Rufnummern) mit den im M-net
     * Bestand befindlichen Daten gegenüber und erstellt eine Liste von Entscheidungsmerkmalen für die Beantwortung der
     * Vorabstimmung (z.B. ABBM, RUEMVA).
     *
     * @param vorabstimmungsId
     * @return
     */
    List<DecisionVO> evaluateDecisionData(String vorabstimmungsId);


    /**
     * Ermittelt aus der angegebenen Liste mit {@link DecisionVO} Objekten die jenigen heraus, die zu dem angegebenen
     * {@link MeldungPositionTyp} passen.
     *
     * @param decisionVOs
     * @param meldungPositionTyp {@link MeldungPositionTyp} auf den gefiltert werden soll
     * @return (neue) Liste mit den {@link DecisionVO} Objekten, die zu dem {@link MeldungPositionTyp} passen.
     */
    List<DecisionVO> getDecisionVOsForMeldungPositionTyp(@NotNull Collection<DecisionVO> decisionVOs, @NotNull MeldungPositionTyp meldungPositionTyp);

    /**
     * Stellt eine angegebene WBCI-{@link Rufnummernportierung} mit Taifun-{@link Rufnummer}n gegenüber und überprüft
     * diese auf Validität.
     *
     * @param rnp                            Rufnummernportierung einer VA oder RUEM-VA
     * @param taifunRufnummern               Liste an Taifun-Rufnummern
     * @param acceptMissingRufnummerInTaifun
     * @return Eine Liste an Entscheidungs-Objekten im {@link DecisionVO}-Format.
     */
    List<DecisionVO> evaluateRufnummernDecisionData(Rufnummernportierung rnp, List<Rufnummer> taifunRufnummern, boolean acceptMissingRufnummerInTaifun);
}
