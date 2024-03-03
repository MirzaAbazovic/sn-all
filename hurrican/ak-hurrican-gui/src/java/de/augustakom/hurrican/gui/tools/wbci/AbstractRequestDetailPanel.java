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
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.gui.utils.GuiTools;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.model.WbciEntity;
import de.mnet.wbci.model.WbciRequest;

/**
 * Abstraktes Panel fuer die Darstellung von Details zu einem WBCI Request (VA/TV/STORNO). <br/><br/> Dieses Panel
 * stellt per Default den Zeitstempel. <br/> Ableitungen des Panels koennen noch weitere Details darstellen.
 */
public abstract class AbstractRequestDetailPanel<R extends WbciRequest> extends AbstractVaDetailPanel {

    private static final long serialVersionUID = 3222948411870790876L;

    public static final String TIMESTAMP = "timestamp";
    public static final String BASIC_INFORMATIONS = "basic.informations";
    public static final String AUFNEHMEND = "aufnehmender.ekp";
    public static final String ABGEBEND = "abgebender.ekp";
    public static final String GESCHAEFTSFALL = "geschaeftsfall";

    protected R wbciRequest;
    private final SwingFactory swingFactory = SwingFactory.getInstance("de/augustakom/hurrican/gui/tools/wbci/resources/AbstractRequestDetailPanel.xml");

    private AKJDateComponent dcTimestamp;
    private AKJTextField tfAufnehmend;
    private AKJTextField tfAbgebend;
    private AKJTextField tfGf;

    public AbstractRequestDetailPanel(String resource) {
        super(resource);
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblTimestamp = swingFactory.createLabel(TIMESTAMP);
        AKJLabel lblAufnehmend = swingFactory.createLabel(AUFNEHMEND);
        AKJLabel lblAbgebend = swingFactory.createLabel(ABGEBEND);
        AKJLabel lblGf = swingFactory.createLabel(GESCHAEFTSFALL);

        dcTimestamp = swingFactory.createDateComponent(TIMESTAMP, false);
        tfAufnehmend = getSwingFactory().createTextField(AUFNEHMEND, false);
        tfAbgebend = getSwingFactory().createTextField(ABGEBEND, false);
        tfGf = getSwingFactory().createTextField(GESCHAEFTSFALL, false);

        // @formatter:off
        AKJPanel basePnl = new AKJPanel(new GridBagLayout(), swingFactory.getText(BASIC_INFORMATIONS));
        basePnl.add(lblTimestamp    , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        basePnl.add(dcTimestamp     , GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(lblAufnehmend   , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(tfAufnehmend    , GBCFactory.createGBC(100,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(lblAbgebend     , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(tfAbgebend      , GBCFactory.createGBC(100,  0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(lblGf           , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(tfGf            , GBCFactory.createGBC(100,  0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(new AKJPanel()  , GBCFactory.createGBC(100,100, 3, 4, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        this.setLayout(new BorderLayout());
        this.add(basePnl, BorderLayout.WEST);
        this.add(buildDetailPanel(), BorderLayout.CENTER);
    }

    @Override
    protected void setVaModel(WbciEntity wbciEntity) {
        this.wbciRequest = (R) wbciEntity;
    }

    /**
     * Zeigt Details zu der Meldung an.
     */
    @Override
    protected void showVaDetails() {
        GuiTools.cleanFields(this);
        dcTimestamp.setDateTime(DateConverterUtils.asLocalDateTime(wbciRequest.getProcessedAt()));
        tfAufnehmend.setText(wbciRequest.getWbciGeschaeftsfall().getAufnehmenderEKP().name());
        tfAbgebend.setText(wbciRequest.getWbciGeschaeftsfall().getAbgebenderEKP().name());
        tfGf.setText(wbciRequest.getWbciGeschaeftsfall().getTyp().getShortName());
    }

    @Override
    protected void execute(String command) {

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
