/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.service;

import javax.validation.constraints.*;

import de.mnet.wbci.config.WbciConstants;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 * Interface fuer Services, die eine ausgehende WBCI-Storno erstellen koennen.
 *
 * @param <GF>
 */
public interface WbciStornoService<GF extends WbciGeschaeftsfall> extends WbciService, WbciConstants {


    /**
     * Erstellt und validiert eine neue ausgehende {@link de.mnet.wbci.model.StornoAnfrage}. Tritt ein Fehler bei der
     * Validierung auf, wird eine {@link de.mnet.wbci.exception.WbciValidationException} geworfen. Für alle anderen
     * Fehler wird eine {@link de.mnet.wbci.exception.WbciServiceException} geworfen.
     *
     * @param stornoAnfrage    die StornoAnfrage, die angelegt wird
     * @param vorabstimmungsId der {@link de.mnet.wbci.model.WbciGeschaeftsfall}, auf den sich die Storno bezieht
     * @return
     */
    StornoAnfrage<GF> createWbciStorno(@NotNull StornoAnfrage stornoAnfrage, @NotNull String vorabstimmungsId);

    /**
     * Verarbeitet eine eingehende Stornoanfrage.
     *
     * @param metadata      die Ergebnisse der Verarbeitung
     * @param stornoAnfrage die Stornoanfrage, die verarbeitet wird
     */
    void processIncomingStorno(@NotNull MessageProcessingMetadata metadata, @NotNull StornoAnfrage stornoAnfrage);

}
