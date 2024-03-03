/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.02.2015
 */
package de.augustakom.hurrican.gui.sip.peering;

import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.IPAddress;

class IpSetTableModel extends AKReflectionTableModel<IPAddress> {
    private static final long serialVersionUID = -573225571971517850L;

    public IpSetTableModel() {
        super(
                new String[] { "IP-Adresse", "Typ" },
                new String[] { "address", "ipType" },
                new Class[] { String.class, AddressTypeEnum.class });
    }
}
