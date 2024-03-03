/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.09.2011 14:07:23
 */
package de.augustakom.hurrican.validation.cc;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;

/**
 * Hilfsklasse fuer die CBVorang-Validators.
 */
public class CBVorgangValidationHelper {

    static boolean isAnswerState(CBVorgang cbVorgang) {
        return (NumberTools.isGreaterOrEqual(cbVorgang.getStatus(), CBVorgang.STATUS_ANSWERED) &&
                NumberTools.isLess(cbVorgang.getStatus(), CBVorgang.STATUS_STORNO));
    }

}


