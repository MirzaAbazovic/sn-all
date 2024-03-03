/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.2016
 */
package de.mnet.wita.message.common;

/**
 * Created by kauzko on 05.09.2016.
 * <p>
 * Änhlich wie SMS-Status.
 */
public enum EmailStatus {
    OFFEN,
    GESENDET,
    KEINE_EMAIL,
    VERALTET,
    UNGUELTIG,
    UNERWUENSCHT,
    KEINE_CONFIG,
    FALSCHER_AUFTRAGSTATUS
}
