/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.05.2011 15:36:35
 */
package de.mnet.wita.message.auftrag;

import com.google.common.collect.ImmutableList;

public enum Anrede {
    FRAU, HERR, FIRMA, UNBEKANNT;

    public static final ImmutableList<Anrede> personAnreden = ImmutableList.of(FRAU, HERR, UNBEKANNT);
    public static final ImmutableList<Anrede> firmaAnreden = ImmutableList.of(FIRMA, UNBEKANNT);
}


