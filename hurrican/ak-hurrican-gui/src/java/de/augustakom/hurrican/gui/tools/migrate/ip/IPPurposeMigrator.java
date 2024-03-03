/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.2011 14:25:11
 */
package de.augustakom.hurrican.gui.tools.migrate.ip;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.IPAddressService;

/**
 *
 */
public class IPPurposeMigrator extends AbstractServiceAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        final int choice = MessageHelper.showConfirmDialog(getMainFrame(), "Soll die Migration gestartet werden?");
        if (choice == JOptionPane.YES_OPTION) {
            try {
                IPAddressService ipAddressService = getCCService(IPAddressService.class);
                Collection<Pair<Long, Reference>> netIdsWithPurposes = ipAddressService
                        .findAllNetIdsWithPurposeFromMonline();
                int monlineNetIdCnt = 0;
                int ipAddrMigCnt = 0;
                if (CollectionTools.isNotEmpty(netIdsWithPurposes)) {
                    monlineNetIdCnt = netIdsWithPurposes.size();
                    for (Pair<Long, Reference> netIdWithPurpose : netIdsWithPurposes) {
                        List<IPAddress> ips = ipAddressService.findIPs4NetId(netIdWithPurpose.getFirst());
                        if (CollectionTools.isNotEmpty(ips)) {
                            ipAddrMigCnt += ips.size();
                            for (IPAddress ip : ips) {
                                ip.setPurpose(netIdWithPurpose.getSecond());
                                ipAddressService.saveIPAddress(ip, sessionId());
                            }
                        }
                    }
                }
                MessageHelper.showInfoDialog(getMainFrame(), String.format(
                        "Die Migration der Verwendungszwecke wurde erfolgreich durchgefuehrt (monline-NetIds:%d, IPAddressesn betroffen:%d)",
                        monlineNetIdCnt, ipAddrMigCnt));
            }
            catch (Exception exc) {
                MessageHelper.showErrorDialog(getMainFrame(), exc);
            }
        }
    }

    private Long sessionId() {
        return HurricanSystemRegistry.instance().getSessionId();
    }
}
