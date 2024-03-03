/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2004 14:19:20
 */
package de.augustakom.hurrican.gui.auftrag.vpn;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.hurrican.gui.base.AbstractDataFrame;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.shared.view.VPNVertragView;
import de.augustakom.hurrican.service.billing.KundenService;


/**
 * Frame fuer die Darstellung der Details eines best. VPN-Auftrags.
 *
 *
 */
public class VPNAuftragFrame extends AbstractDataFrame {

    private static final Logger LOGGER = Logger.getLogger(VPNAuftragFrame.class);

    private VPNVertragView vpnView = null;
    private VPNAuftragPanel vpnPanel = null;

    /**
     * Konstruktor mit Angabe VPN-Auftrags, dessen Details angezeigt werden sollen
     *
     * @param vpnView
     */
    public VPNAuftragFrame(VPNVertragView vpnView) {
        super(null);
        this.vpnView = vpnView;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJInternalFrame#getUniqueName()
     */
    public String getUniqueName() {
        return this.getClass().getName();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("VPN-Aufträge");

        vpnPanel = new VPNAuftragPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(vpnPanel, BorderLayout.CENTER);

        pack();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) {
        if (model instanceof VPNVertragView) {
            this.vpnView = (VPNVertragView) model;
        }

        readModel();
        vpnPanel.setModel(model);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    public void readModel() {
        String title = "Aufträge im VPN";
        try {
            if (vpnView != null) {
                KundenService ks = getBillingService(KundenService.class.getName(), KundenService.class);
                Kunde k = ks.findKunde(vpnView.getKundeNo());
                if (k != null) {
                    title += " (" + k.getName() + ")";
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        setTitle(title);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        return false;
    }

}


