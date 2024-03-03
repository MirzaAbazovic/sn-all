/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 15.02.2017

 */

package de.augustakom.hurrican.gui.tools.wbci;


/**
 * Created by wieran on 15.02.2017.
 */
public interface WholesaleDataChangeListener {

    /**
     * Listener method to update the wholesalePanel, after for example sendPVOrder
     */
    void updateWholesaleData();
}
