/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2011 11:58:22
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.tal.ProduktDtag;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Service-Definition fuer TAL-Bestellungen ueber die ESAA Schnittstelle der DTAG.
 */
public interface ESAATalOrderService extends TALOrderService {

    /**
     * sucht nach einem Mapping der fuer die Tal-Schnittstelle benötigten DTAG-Produkte anhand eines example
     *
     * @param ProduktDatg example
     * @return Liste vom Typ ProduktDtag
     * @throws FindException
     */
    public List<ProduktDtag> getProduktDtag(ProduktDtag example) throws FindException;

}


