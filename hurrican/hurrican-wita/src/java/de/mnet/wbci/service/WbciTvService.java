/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.service;

import javax.validation.constraints.*;

import de.mnet.wbci.config.WbciConstants;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 * Interface fuer Services, die eine ausgehende WBCI-Terminverschiebung erstellen koennen.
 *
 * @param <GF>
 */
public interface WbciTvService<GF extends WbciGeschaeftsfall> extends WbciService, WbciConstants {


    /**
     * Erstellt und validiert eine neue ausgehende {@link de.mnet.wbci.model.TerminverschiebungsAnfrage}, die sich auf
     * die angegebene {@link de.mnet.wbci.model.VorabstimmungsAnfrage} bezieht. Tritt ein Fehler bei der Validierung
     * auf, wird eine {@link de.mnet.wbci.exception.WbciValidationException} geworfen. Für alle anderen Fehler wird eine
     * {@link de.mnet.wbci.exception.WbciServiceException} geworfen.
     *
     * @param tv               die Terminverschiebung, die angelegt wird
     * @param vorabstimmungsId der {@link de.mnet.wbci.model.WbciGeschaeftsfall}, auf den sich die TV bezieht
     * @return
     */
    TerminverschiebungsAnfrage<GF> createWbciTv(@NotNull TerminverschiebungsAnfrage tv,
            @NotNull String vorabstimmungsId);

    /**
     * Verarbeitet eine eingehende Terminverschiebung nachdem sie bereits gespeichert und ins IO-Archiv eingetragen
     * wurde. Wird von der Camel-Route aufgerufen.
     * <p/>
     * Es wird geprueft, ob es zu der von der TV referenzierten Vorabstimmung bereits eine (prozessierte) RUEM-VA
     * Meldung gibt. Ist dies nicht der Fall, wird eine {@link de.mnet.wbci.model.Abbruchmeldung} (ABBM) generiert und
     * versendet und die {@link TerminverschiebungsAnfrage} als 'ungueltig' markiert.
     *
     * @param terminverschiebungsAnfrage Die Terminverschiebung, die verarbeitet werden muss.
     */
    void postProcessIncomingTv(TerminverschiebungsAnfrage<GF> terminverschiebungsAnfrage);

    /**
     * Pre-Processing of an incoming 'Terminverschiebung'. Loads the 'Geschaeftsfall' of the referencing WBCI request
     * and assigns it to the TV.
     *
     * @param metadata the container used for storing the processing results
     * @param tv       the {@link TerminverschiebungsAnfrage}
     */
    void processIncomingTv(MessageProcessingMetadata metadata, TerminverschiebungsAnfrage tv);

}
