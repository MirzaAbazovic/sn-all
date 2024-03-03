/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2005 12:00:13
 */
package de.augustakom.hurrican.gui.auftrag.carrier;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.hurrican.service.cc.CarrierService;


/**
 * Panel fuer die einfache Eingabe der LBZ fuer eine Carrierbestellung bei DTAG.
 *
 *
 */
public class CarrierLbzDTAGPanel extends AbstractCarrierLbzPanel {

    private static final Logger LOGGER = Logger.getLogger(CarrierLbzDTAGPanel.class);

    private AKJTextField tfPrefix = null;
    private AKJTextField tfSuffix = null;
    private AKJLabel lblONKZ = null;

    private String baseLbz = null;

    /**
     * Default-Konstruktor.
     */
    public CarrierLbzDTAGPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/CarrierLbzDTAGPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    protected final void createGUI() {
        lblONKZ = new AKJLabel(baseLbz);
        tfPrefix = getSwingFactory().createTextField("lbz.prefix");
        tfPrefix.addFocusListener(new LbzPrefixFocusListener());
        tfSuffix = getSwingFactory().createTextField("lbz.suffix");

        setLayout(new GridBagLayout());
        add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        add(tfPrefix, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        add(lblONKZ, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        add(tfSuffix, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 2, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            CarrierService cs = getCCService(CarrierService.class);
            baseLbz = cs.createLbz(getCarrierId(), getEndstelleId());
            lblONKZ.setText(baseLbz);

            revalidate();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.auftrag.carrier.AbstractCarrierLbzPanel#getLbz()
     */
    protected String getLbz() {
        StringBuilder result = new StringBuilder();
        result.append(tfPrefix.getText(""));
        result.append(baseLbz);
        result.append(tfSuffix.getText(""));
        return result.toString();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /*
     * FocusListener fuer das TextField mit dem LBZ-Prefix. <br>
     * Bei 'focusLost' werden alle eingetragenen Buchstaben in Grossbuchstaben gewandelt.
     */
    class LbzPrefixFocusListener extends FocusAdapter {
        /**
         * @see java.awt.event.FocusAdapter#focusLost(java.awt.event.FocusEvent)
         */
        public void focusLost(FocusEvent e) {
            if (e.getSource() == tfPrefix) {
                tfPrefix.setText(tfPrefix.getText("").toUpperCase());
            }
        }
    }
}


