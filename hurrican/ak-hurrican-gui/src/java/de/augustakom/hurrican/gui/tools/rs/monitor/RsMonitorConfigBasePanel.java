/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2009 14:25:41
 */
package de.augustakom.hurrican.gui.tools.rs.monitor;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.RSMonitorConfig;


/**
 * Basis-Panel zur Erfassung von Schwellwerten fuer den Ressourcenmonitor.
 *
 *
 */
public class RsMonitorConfigBasePanel extends AbstractServicePanel implements AKDataLoaderComponent {

    private AKJFormattedTextField tfSW = null;
    private AKJFormattedTextField tfAnzahlTage = null;
    private AKJCheckBox cbAlarm = null;
    private AKJTextField tfUser = null;
    private AKJTextField tfDate = null;
    private RSMonitorConfig config = null;

    /**
     * Default-Const.
     */
    public RsMonitorConfigBasePanel() {
        super("de/augustakom/hurrican/gui/tools/rs/monitor/resources/RsMonitorConfigBasePanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    protected final void createGUI() {
        AKJLabel lblUser = getSwingFactory().createLabel("user");
        AKJLabel lblDate = getSwingFactory().createLabel("date");
        AKJLabel lblSW = getSwingFactory().createLabel("sw");
        AKJLabel lblAlarm = getSwingFactory().createLabel("alarm");
        AKJLabel lblAnzahlTage = getSwingFactory().createLabel("anzahlTage");

        cbAlarm = getSwingFactory().createCheckBox("alarm");
        tfSW = getSwingFactory().createFormattedTextField("sw");
        tfAnzahlTage = getSwingFactory().createFormattedTextField("anzahlTage");
        tfUser = getSwingFactory().createTextField("user", false);
        tfDate = getSwingFactory().createTextField("date", false);

        this.setLayout(new GridBagLayout());
        this.add(lblSW, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfSW, GBCFactory.createGBC(100, 0, 1, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblAnzahlTage, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfAnzahlTage, GBCFactory.createGBC(100, 0, 1, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblAlarm, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(cbAlarm, GBCFactory.createGBC(100, 0, 1, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblUser, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfUser, GBCFactory.createGBC(100, 0, 1, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblDate, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfDate, GBCFactory.createGBC(100, 0, 1, 4, 2, 1, GridBagConstraints.HORIZONTAL));
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

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
    }

    /**
     * Liefert die Daten des Panel zurueck
     *
     * @return RSMonitorConfig-Objekt
     *
     */
    protected RSMonitorConfig getData() {
        if (config == null) {
            config = new RSMonitorConfig();
        }

        config.setMinCount(tfSW.getValueAsInt(null));
        config.setDayCount(tfAnzahlTage.getValueAsInt(null));
        config.setAlarmierung(BooleanTools.nullToFalse(cbAlarm.isSelectedBoolean()));

        return config;
    }

    /**
     * Funktion setzt das RSMonitorConfig-Objekt
     *
     * @param config RsMonitorConfig-Objekt
     *
     */
    protected void setData(RSMonitorConfig config) {
        this.config = config;
        if (config != null) {
            if (config.getMinCount() != null) {
                tfSW.setText(config.getMinCount().toString());
            }

            if (config.getDayCount() != null) {
                tfAnzahlTage.setText(config.getDayCount().toString());
            }

            tfUser.setText(config.getUserw());
            tfDate.setText(DateTools.formatDate(config.getDatew(), DateTools.PATTERN_DATE_TIME));
            cbAlarm.setSelected(config.getAlarmierung());
        }
        else {
            GuiTools.cleanFields(this);
        }
    }

}


