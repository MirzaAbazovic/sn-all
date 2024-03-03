/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.model.cc.innenauftrag;

import javax.persistence.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modell-Klasse bildet die moeglichen 'Level5' fuer Innenauftraege ab.
 */
@Entity
@Table(name = "T_IA_LEVEL5")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_IA_LEVEL5_0", allocationSize = 1)
public class IaLevel5 extends AbstractIaLevel {

    private static final long serialVersionUID = 1662578211936384479L;

}
