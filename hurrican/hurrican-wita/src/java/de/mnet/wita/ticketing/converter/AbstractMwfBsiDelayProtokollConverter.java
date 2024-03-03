/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2013 08:57:59
 */
package de.mnet.wita.ticketing.converter;

import de.mnet.wita.message.MwfEntity;

/**
 * Basis-Klasse fuer Konverter-Funktionen, um aus vorgehaltenen(!) WITA-Objekten einen BSI-Protokolleintrag zu
 * erstellen
 */
public abstract class AbstractMwfBsiDelayProtokollConverter<T extends MwfEntity> extends AbstractMwfBsiProtokollConverter<T> {

}


