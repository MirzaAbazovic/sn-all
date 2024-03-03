/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.03.2012 17:02:55
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import com.google.common.collect.ListMultimap;

import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;

/**
 * Hilfsmodell fuer den Abgleich der technischen Leistungen zu einem Auftrag.
 */
public class ExterneAuftragsLeistungen {
    public Map<Long, BAuftragPos> positionen;
    public ListMultimap<Long, BAuftragPos> extLeistNo2BAuftragPositionen;
    public List<BAuftragLeistungView> activeLeistungViews;
    public List<BAuftragLeistungView> allLeistungViews;

    public ExterneAuftragsLeistungen(Map<Long, BAuftragPos> positionen,
            ListMultimap<Long, BAuftragPos> extLeistNo2BAuftragPositionen,
            List<BAuftragLeistungView> activeLeistungViews, List<BAuftragLeistungView> allLeistungViews) {
        this.positionen = positionen;
        this.extLeistNo2BAuftragPositionen = extLeistNo2BAuftragPositionen;
        this.activeLeistungViews = activeLeistungViews;
        this.allLeistungViews = allLeistungViews;
    }
}

