/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;
import java.time.*;
import java.util.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.gui.utils.GuiTools;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.WbciEntity;

/**
 * Abstraktes Panel fuer die Darstellung von Details zu einer WBCI Meldung. <br/><br/> Dieses Panel stellt per Default
 * den Zeitstempel der Nachricht sowie die Melde-Codes dar. <br/> Ableitungen des Panels koennen noch weitere Details
 * darstellen.
 */
public abstract class AbstractMeldungDetailPanel<M extends Meldung> extends AbstractVaDetailPanel {

    private static final long serialVersionUID = -7581863032920018809L;

    public static final String MELDUNGSCODES = "meldungscodes";
    public static final String TIMESTAMP = "timestamp";
    public static final String BASIC_INFORMATIONS = "basic.informations";

    protected M meldung;
    private final SwingFactory swingFactory = SwingFactory.getInstance("de/augustakom/hurrican/gui/tools/wbci/resources/AbstractMeldungDetailPanel.xml");

    private AKJDateComponent dcTimestamp;
    private AKJTextArea taMeldungscodes;

    public AbstractMeldungDetailPanel(String resource) {
        super(resource);
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblTimestamp = swingFactory.createLabel(TIMESTAMP);
        AKJLabel lblMeldungscodes = swingFactory.createLabel(MELDUNGSCODES);

        dcTimestamp = swingFactory.createDateComponent(TIMESTAMP, false);
        taMeldungscodes = swingFactory.createTextArea(MELDUNGSCODES, false, true, true);
        AKJScrollPane spMeldungscodes = new AKJScrollPane(taMeldungscodes, new Dimension(200, 150));

        // @formatter:off
        AKJPanel basePnl = new AKJPanel(new GridBagLayout(), swingFactory.getText(BASIC_INFORMATIONS));
        basePnl.add(lblTimestamp    , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        basePnl.add(dcTimestamp     , GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(lblMeldungscodes, GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(spMeldungscodes , GBCFactory.createGBC(100,100, 2, 1, 1, 2, GridBagConstraints.BOTH));
        // @formatter:on

        this.setLayout(new BorderLayout());
        this.add(basePnl, BorderLayout.WEST);
        this.add(buildDetailPanel(), BorderLayout.CENTER);
    }

    @Override
    protected void setVaModel(WbciEntity wbciEntity) {
        this.meldung = (M) wbciEntity;
    }

    /**
     * Zeigt Details zu der Meldung an.
     */
    @Override
    protected void showVaDetails() {
        GuiTools.cleanFields(this);
        dcTimestamp.setDateTime(DateConverterUtils.asLocalDateTime(meldung.getProcessedAt()));
        taMeldungscodes.setText(meldung.extractMeldungspositionen());
    }

    @Override
    protected void execute(String command) {

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
