/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.model;

import javax.persistence.*;

/**
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("ERLM-TV")
public class ErledigtmeldungTerminverschiebung extends Erledigtmeldung {
    public static final String DB_MELDUNG_TYP = "ERLM-TV";
    private static final long serialVersionUID = -1992140850274863416L;
}
