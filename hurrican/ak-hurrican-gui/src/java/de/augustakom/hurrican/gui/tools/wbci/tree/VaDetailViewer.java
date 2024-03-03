/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci.tree;

import de.mnet.wbci.model.WbciEntity;

/**
 * Interface fuer GUI-Elemente (z.B. Panels), die Details zu einer WBCI Nachricht (Request oder Meldung) anzeigen
 * koennen.
 */
public interface VaDetailViewer {

    public void showVaDetails(WbciEntity wbciEntity);

}
