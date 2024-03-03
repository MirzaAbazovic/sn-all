/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.2009 17:38:59
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteToken;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.IPSecService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 *
 */
public class IPSecClient2SiteTokenDetailPanel extends AbstractDataPanel {
    private static final Logger LOGGER = Logger.getLogger(IPSecClient2SiteTokenDetailPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/IPSecClient2SiteTokenDetailPanel.xml";
    private static final String AUFTRAG_ID = "auftrag.id";
    private static final String SAP_ORDER_ID = "sap.order.id";
    private static final String SERIAL_NUMBER = "serial.number";
    private static final String LAUFZEIT_IN_MONATEN = "laufzeit.in.monaten";
    private static final String LIEFERDATUM = "liefer.datum";
    private static final String BEMERKUNG = "bemerkung";
    private static final String BATTERIE_ENDE = "batterie.ende";
    private static final String BATCH = "batch";
    private static final String TOKEN_STATUS = "token.status";

    private IPSecService ipSecService;

    private AKJTextField tfAuftragId;
    private AKJTextField tfSapOrderId;
    private AKJTextField tfSerialNumber;
    private AKJTextField tfLaufzeitInMonaten;
    private AKJTextField tfBatch;
    private AKJDateComponent dcLieferdatum;
    private AKJTextArea taBemerkung;
    private AKJDateComponent dcBatterieEnde;
    private AKReferenceField rfStatus;

    private IPSecClient2SiteToken token;

    private boolean isAdminPanel = false;

    /**
     * @param resource
     */
    public IPSecClient2SiteTokenDetailPanel() {
        super(RESOURCE);
        try {
            ipSecService = getCCService(IPSecService.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder("Token Detail"));
        int row = 0;
        int column = 0;

        column = addAuftragId(row, column);
        column = addSapOrderId(row, column);
        column = addLieferdatum(row, column);
        column = addLaufzeitInMonaten(row, column);
        column = addBemerkung(row, column);

        row++;
        column = 0;
        column = addSerialNumber(row, column);
        column = addBatch(row, column);
        column = addBatterieEnde(row, column);
        column = addStatus(row, column);

        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, column + 4, 0, 1, 3, GridBagConstraints.HORIZONTAL));

        enableTokenDetailFields(false);
        loadData();
    }

    private void loadData() {
        try {
            QueryCCService queryService = getCCService(QueryCCService.class);
            Reference tokenStatusRefEx = new Reference();
            tokenStatusRefEx.setType(Reference.REF_TYPE_IPSEC_TOKEN_STATUS);
            tokenStatusRefEx.setGuiVisible(Boolean.TRUE);
            rfStatus.setReferenceFindExample(tokenStatusRefEx);
            rfStatus.setFindService(queryService);
            rfStatus.setReferenceId(IPSecClient2SiteToken.REF_ID_TOKEN_STATUS_ACTIVE);

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int addBatterieEnde(int row, int columnIn) {
        int column = columnIn;
        AKJLabel lbBatterieEnde = getSwingFactory().createLabel(BATTERIE_ENDE);
        dcBatterieEnde = getSwingFactory().createDateComponent(BATTERIE_ENDE);
        this.add(lbBatterieEnde, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        this.add(dcBatterieEnde, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        return column;
    }

    private int addBemerkung(int row, int columnIn) {
        int column = columnIn;
        AKJLabel lbBemerkung = getSwingFactory().createLabel(BEMERKUNG);
        taBemerkung = getSwingFactory().createTextArea(BEMERKUNG);
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung, new Dimension(170, 60));
        this.add(lbBemerkung, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        this.add(spBemerkung, GBCFactory.createGBC(50, 0, column, row, 1, 3, GridBagConstraints.BOTH));
        column++;
        return column;
    }

    private int addLaufzeitInMonaten(int row, int columnIn) {
        int column = columnIn;
        AKJLabel lbLaufzeitInMonaten = getSwingFactory().createLabel(LAUFZEIT_IN_MONATEN);
        tfLaufzeitInMonaten = getSwingFactory().createTextField(LAUFZEIT_IN_MONATEN);
        this.add(lbLaufzeitInMonaten, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        this.add(tfLaufzeitInMonaten, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        return column;
    }

    private int addLieferdatum(int row, int columnIn) {
        int column = columnIn;
        AKJLabel lbLieferdatum = getSwingFactory().createLabel(LIEFERDATUM);
        dcLieferdatum = getSwingFactory().createDateComponent(LIEFERDATUM);
        this.add(lbLieferdatum, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        this.add(dcLieferdatum, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        return column;
    }

    private int addSapOrderId(int row, int columnIn) {
        int column = columnIn;
        AKJLabel lbSapOrderId = getSwingFactory().createLabel(SAP_ORDER_ID);
        tfSapOrderId = getSwingFactory().createTextField(SAP_ORDER_ID);
        this.add(lbSapOrderId, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        this.add(tfSapOrderId, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        return column;
    }

    private int addSerialNumber(int row, int columnIn) {
        int column = columnIn;
        AKJLabel lbSerialNumber = getSwingFactory().createLabel(SERIAL_NUMBER);
        tfSerialNumber = getSwingFactory().createTextField(SERIAL_NUMBER);
        this.add(lbSerialNumber, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        this.add(tfSerialNumber, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        return column;
    }

    private int addBatch(int row, int columnIn) {
        int column = columnIn;
        AKJLabel lbBatch = getSwingFactory().createLabel(BATCH);
        tfBatch = getSwingFactory().createTextField(BATCH);
        this.add(lbBatch, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        this.add(tfBatch, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        return column;
    }

    private int addStatus(int row, int columnIn) {
        int column = columnIn;
        AKJLabel lbStatus = getSwingFactory().createLabel(TOKEN_STATUS);
        rfStatus = getSwingFactory().createReferenceField(TOKEN_STATUS);
        this.add(lbStatus, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        this.add(rfStatus, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        return column;
    }

    private int addAuftragId(int row, int columnIn) {
        int column = columnIn;
        AKJLabel lbAuftragId = getSwingFactory().createLabel(AUFTRAG_ID);
        tfAuftragId = getSwingFactory().createTextField(AUFTRAG_ID);
        this.add(lbAuftragId, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        this.add(tfAuftragId, GBCFactory.createGBC(0, 0, column, row, 1, 1, GridBagConstraints.HORIZONTAL));
        column++;
        return column;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() throws AKGUIException {
        if (token != null) {
            enableTokenDetailFields(true);
            tfAuftragId.setText(token.getAuftragId());
            tfSapOrderId.setText(token.getSapOrderId());
            dcLieferdatum.setDate(token.getLieferdatum());
            dcBatterieEnde.setDate(token.getBatterieEnde());
            tfSerialNumber.setText(token.getSerialNumber());
            tfLaufzeitInMonaten.setText(token.getLaufzeitInMonaten());
            tfBatch.setText(token.getBatch());
            taBemerkung.setText(token.getBemerkung());
            rfStatus.setReferenceId(token.getStatusRefId());
        }
        else {
            GuiTools.cleanFields(this);
            enableTokenDetailFields(false);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        if (token != null) {
            token.setAuftragId(tfAuftragId.getTextAsLong(null));
            token.setSapOrderId(tfSapOrderId.getText(null));
            token.setSerialNumber(tfSerialNumber.getText(null));
            token.setLieferdatum(dcLieferdatum.getDate(null));
            token.setBatterieEnde(dcBatterieEnde.getDate(null));
            token.setLaufzeitInMonaten(tfLaufzeitInMonaten.getTextAsInt(null));
            token.setBatch(tfBatch.getText(null));
            token.setBemerkung(taBemerkung.getText(null));
            token.setStatusRefId(rfStatus.getReferenceIdAs(Long.class));
            try {
                ipSecService.saveClient2SiteToken(token);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                token = null;
                throw new AKGUIException(e);
            }
        }
    }

    private void enableTokenDetailFields(boolean enabled) {
        taBemerkung.setEnabled(enabled);
        rfStatus.setEnabled(enabled);
        if (!isAdminPanel) {
            enabled = false;
        }
        tfAuftragId.setEnabled(enabled);
        tfSapOrderId.setEnabled(enabled);
        dcLieferdatum.setEnabled(enabled);
        dcBatterieEnde.setEnabled(enabled);
        tfSerialNumber.setEnabled(enabled);
        tfLaufzeitInMonaten.setEnabled(enabled);
        tfBatch.setEnabled(enabled);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return token;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) throws AKGUIException {
        if (model instanceof IPSecClient2SiteToken) {
            token = (IPSecClient2SiteToken) model;
        }
        else {
            token = null;
        }
        readModel();
    }


    public void setAdminPanel(boolean isAdminPanel) {
        this.isAdminPanel = isAdminPanel;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}
