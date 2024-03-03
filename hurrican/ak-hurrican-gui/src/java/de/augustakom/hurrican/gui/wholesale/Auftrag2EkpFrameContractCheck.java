/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2012 13:36:20
 */
package de.augustakom.hurrican.gui.wholesale;

import java.awt.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.fttx.A10Nsp;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * Helper um zu prüfen ob für einen {@link EkpFrameContract}, {@link A10NspPort} oder {@link A10Nsp} Aufträge zugeordnet
 * sind.<br> Über die "checkWithDialog" Methoden kann dementsprechend über einen Dialog eine doppelte Warnung vor dem
 * Speichern/Löschen angezeigt werden.
 *
 *
 */
public class Auftrag2EkpFrameContractCheck {


    private EkpFrameContractService getEkpFrameContractService() throws ServiceNotFoundException {
        EkpFrameContractService frameContractService = CCServiceFinder.instance().getCCService(
                EkpFrameContractService.class);
        return frameContractService;
    }

    /**
     * @param ekp
     * @return true wenn dem Contract Aufträge zugeordnet sind oder es sich um den MNET EKP handelt
     * @throws Exception
     */
    public boolean check(EkpFrameContract ekp) throws Exception {
        EkpFrameContractService frameContractService = getEkpFrameContractService();
        return EkpFrameContract.EKP_ID_MNET.equals(ekp.getEkpId())
                || frameContractService.hasAuftrag2EkpFrameContract(ekp);
    }

    /**
     * Zeigt einen doppelten Warndialog an wenn dem EKP Aufträge zugeordnet sind.
     *
     * @param parent
     * @param ekp
     * @return {@link JOptionPane#OK_OPTION} wenn der Benutzer die Dialoge mit OK bestätigt hat
     * @throws Exception
     */
    public int checkWithDialog(Component parent, EkpFrameContract ekp) throws Exception {
        if (check(ekp)) {
            return showDialogs(parent, "EKP");
        }
        return JOptionPane.OK_OPTION;
    }

    public boolean check(A10NspPort a10NspPort) throws Exception {
        EkpFrameContractService frameContractService = getEkpFrameContractService();
        return frameContractService.hasAuftrag2EkpFrameContract(a10NspPort);
    }

    /**
     * Zeigt einen doppelten Warndialog an wenn dem A10NspPort über dessen EKP Zuordnung Aufträge zugeordnet sind.
     *
     * @param parent
     * @param a10NspPort
     * @return {@link JOptionPane#OK_OPTION} wenn der Benutzer die Dialoge mit OK bestätigt hat
     * @throws Exception
     */
    public int checkWithDialog(Component parent, A10NspPort a10NspPort) throws Exception {
        if (check(a10NspPort)) {
            return showDialogs(parent, "A10-NSP Port");
        }
        return JOptionPane.OK_OPTION;
    }

    public boolean check(A10Nsp a10Nsp) throws Exception {
        EkpFrameContractService frameContractService = getEkpFrameContractService();
        return frameContractService.hasAuftrag2EkpFrameContract(a10Nsp);
    }

    /**
     * Zeigt einen doppelten Warndialog an wenn den Ports des A10Nsp über deren EKP Zuordnung Aufträge zugeordnet sind.
     *
     * @param parent
     * @param a10Nsp
     * @return {@link JOptionPane#OK_OPTION} wenn der Benutzer die Dialoge mit OK bestätigt hat
     * @throws Exception
     */
    public int checkWithDialog(Component parent, A10Nsp a10Nsp) throws Exception {
        if (check(a10Nsp)) {
            return showDialogs(parent, "A10-NSP");
        }
        return JOptionPane.OK_OPTION;
    }

    private int showDialogs(Component parent, String msgParam) {
        SwingFactory sf = SwingFactory
                .getInstance("de/augustakom/hurrican/gui/wholesale/resources/Auftrag2EkpFrameContractCheck.xml");
        String cancelText = sf.getText("cancel");
        int confirmResult = JOptionPane.showOptionDialog(parent,
                sf.getText("first.message", msgParam),
                sf.getText("first.title", msgParam),
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                new Object[] { sf.getText("save"), cancelText }, cancelText);
        if (confirmResult == JOptionPane.OK_OPTION) {
            confirmResult = JOptionPane.showOptionDialog(parent,
                    sf.getText("second.message", msgParam),
                    sf.getText("second.title", msgParam),
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                    new Object[] { sf.getText("save"), cancelText }, cancelText);
        }

        return confirmResult;
    }
}
