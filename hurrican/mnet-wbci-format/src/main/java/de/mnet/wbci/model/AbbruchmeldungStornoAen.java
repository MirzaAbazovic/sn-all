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
@DiscriminatorValue("ABBM-STRAEN")
public class AbbruchmeldungStornoAen extends Abbruchmeldung {

    private static final long serialVersionUID = 8048950728163209887L;
}
