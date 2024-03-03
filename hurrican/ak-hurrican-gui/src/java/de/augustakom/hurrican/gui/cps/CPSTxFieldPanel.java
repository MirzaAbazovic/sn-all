package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import javax.swing.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.SwingFactory;

/**
 *
 */
public class CPSTxFieldPanel extends AKJPanel implements CPSTxObserver {
    private static final String ID = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.id";
    private static final String TAIFUN_ORDER_NO = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.taifunOrderNo";
    private static final String AUFTRAG_ID = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.auftragId";
    private static final String VERLAUF_ID = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.verlaufId";
    private static final String TX_STATE = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.txState";
    private static final String TX_SOURCE = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.txSource";
    private static final String SERVICE_ORDER_TYPE = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.serviceOrderType";
    private static final String SERVICE_ORDER_PRIO = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.serviceOrderPrio";
    private static final String SO_STACK_ID = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.soStackId";
    private static final String SO_STACK_SEQ = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.soStackSeq";
    private static final String REGION = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.region";
    private static final String ESTIMATED_EXEC_TIME = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.estimatedExecTime";
    private static final String RESPONSE_AT = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.responseAt";
    private static final String TX_USER = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.txUser";
    private static final String USERW = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.userW";
    private static final String TIMEST = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.timest";
    private static final String REQUEST_AT = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.requestAt";

    private AKJTextField tfId = null;
    private AKJTextField tfTaifunOrderNo = null;
    private AKJTextField tfAuftragId = null;
    private AKJTextField tfVerlaufId = null;
    private AKJTextField tfTxState = null;
    private AKJTextField tfTxSource = null;
    private AKJTextField tfServiceOrderType = null;
    private AKJTextField tfServiceOrderPrio = null;
    private AKJTextField tfSoStackId = null;
    private AKJTextField tfSoStackSeq = null;
    private AKJTextField tfRegion = null;
    private AKJDateComponent dcEstimatedExecTime = null;
    private AKJDateComponent dcResponseAt = null;
    private AKJTextField tfTxUser = null;
    private AKJTextField tfUserW = null;
    private AKJDateComponent dcTimest = null;
    private AKJDateComponent dcRequestAt = null;

    /**
     * Default-Konstruktor
     */
    public CPSTxFieldPanel() {
        super();
        setLayout(new GridBagLayout());

        AKJLabel lblId = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(ID);
        AKJLabel lblTaifunOrderNo = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(TAIFUN_ORDER_NO);
        AKJLabel lblAuftragId = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(AUFTRAG_ID);
        AKJLabel lblVerlaufId = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(VERLAUF_ID);
        AKJLabel lblTxState = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(TX_STATE);
        AKJLabel lblTxSource = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(TX_SOURCE);
        AKJLabel lblServiceOrderType = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(SERVICE_ORDER_TYPE);
        AKJLabel lblServiceOrderPrio = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(SERVICE_ORDER_PRIO);
        AKJLabel lblSoStackId = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(SO_STACK_ID);
        AKJLabel lblSoStackSeq = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(SO_STACK_SEQ);
        AKJLabel lblRegion = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(REGION);
        AKJLabel lblEstimatedExecTime = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(ESTIMATED_EXEC_TIME);
        AKJLabel lblResponseAt = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(RESPONSE_AT);
        AKJLabel lblTxUser = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(TX_USER);
        AKJLabel lblUserW = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(USERW);
        AKJLabel lblTimest = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(TIMEST);
        AKJLabel lblRequestAt = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createLabel(REQUEST_AT);

        tfId = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createTextField(ID, Boolean.FALSE);
        tfTaifunOrderNo = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createTextField(TAIFUN_ORDER_NO, Boolean.FALSE);
        tfAuftragId = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createTextField(AUFTRAG_ID, Boolean.FALSE);
        tfVerlaufId = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createTextField(VERLAUF_ID, Boolean.FALSE);
        tfTxState = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createTextField(TX_STATE, Boolean.FALSE);
        tfTxSource = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createTextField(TX_SOURCE, Boolean.FALSE);
        tfServiceOrderType = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createTextField(SERVICE_ORDER_TYPE, Boolean.FALSE);
        tfServiceOrderPrio = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createTextField(SERVICE_ORDER_PRIO, Boolean.FALSE);
        tfSoStackId = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createTextField(SO_STACK_ID, Boolean.FALSE);
        tfSoStackSeq = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createTextField(SO_STACK_SEQ, Boolean.FALSE);
        tfRegion = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createTextField(REGION, Boolean.FALSE);
        dcEstimatedExecTime = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createDateComponent(ESTIMATED_EXEC_TIME, Boolean.FALSE);
        dcResponseAt = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createDateComponent(RESPONSE_AT, Boolean.FALSE);
        tfTxUser = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createTextField(TX_USER, Boolean.FALSE);
        tfUserW = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createTextField(USERW, Boolean.FALSE);
        dcTimest = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createDateComponent(TIMEST, Boolean.FALSE);
        dcRequestAt = SwingFactory.getInstance(CPSTxPanel.RESOURCE).createDateComponent(REQUEST_AT, Boolean.FALSE);

        this.add(lblId, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblTaifunOrderNo, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblAuftragId, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblVerlaufId, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblTxState, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblTxSource, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblServiceOrderType, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblServiceOrderPrio, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblSoStackId, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblSoStackSeq, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblRegion, GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblEstimatedExecTime, GBCFactory.createGBC(0, 0, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblResponseAt, GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblTxUser, GBCFactory.createGBC(0, 0, 4, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblUserW, GBCFactory.createGBC(0, 0, 4, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblTimest, GBCFactory.createGBC(0, 0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblRequestAt, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.add(tfId, GBCFactory.createGBC(33, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfTaifunOrderNo, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfAuftragId, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfVerlaufId, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfTxState, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfTxSource, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfServiceOrderType, GBCFactory.createGBC(33, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfServiceOrderPrio, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfSoStackId, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfSoStackSeq, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfRegion, GBCFactory.createGBC(0, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(dcEstimatedExecTime, GBCFactory.createGBC(0, 0, 5, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(dcResponseAt, GBCFactory.createGBC(33, 0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfTxUser, GBCFactory.createGBC(0, 0, 5, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfUserW, GBCFactory.createGBC(0, 0, 5, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(dcTimest, GBCFactory.createGBC(0, 0, 5, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(dcRequestAt, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        JPanel fillPanel = new JPanel();
        this.add(fillPanel, GBCFactory.createGBC(100, 100, 0, 7, 6, 1, GridBagConstraints.BOTH));
    }

    /**
     * @param tfId the tfId to set
     */
    public void setTfId(AKJTextField tfId) {
        this.tfId = tfId;
    }

    /**
     * @return the tfTaifunOrderNo
     */
    public AKJTextField getTfTaifunOrderNo() {
        return tfTaifunOrderNo;
    }

    /**
     * @param tfTaifunOrderNo the tfTaifunOrderNo to set
     */
    public void setTfTaifunOrderNo(AKJTextField tfTaifunOrderNo) {
        this.tfTaifunOrderNo = tfTaifunOrderNo;
    }

    /**
     * @return the tfAuftragId
     */
    public AKJTextField getTfAuftragId() {
        return tfAuftragId;
    }

    /**
     * @param tfAuftragId the tfAuftragId to set
     */
    public void setTfAuftragId(AKJTextField tfAuftragId) {
        this.tfAuftragId = tfAuftragId;
    }

    /**
     * @return the tfVerlaufId
     */
    public AKJTextField getTfVerlaufId() {
        return tfVerlaufId;
    }

    /**
     * @param tfVerlaufId the tfVerlaufId to set
     */
    public void setTfVerlaufId(AKJTextField tfVerlaufId) {
        this.tfVerlaufId = tfVerlaufId;
    }

    /**
     * @return the tfTxState
     */
    public AKJTextField getTfTxState() {
        return tfTxState;
    }

    /**
     * @param tfTxState the tfTxState to set
     */
    public void setTfTxState(AKJTextField tfTxState) {
        this.tfTxState = tfTxState;
    }

    /**
     * @return the tfTxSource
     */
    public AKJTextField getTfTxSource() {
        return tfTxSource;
    }

    /**
     * @param tfTxSource the tfTxSource to set
     */
    public void setTfTxSource(AKJTextField tfTxSource) {
        this.tfTxSource = tfTxSource;
    }

    /**
     * @return the tfServiceOrderType
     */
    public AKJTextField getTfServiceOrderType() {
        return tfServiceOrderType;
    }

    /**
     * @param tfServiceOrderType the tfServiceOrderType to set
     */
    public void setTfServiceOrderType(AKJTextField tfServiceOrderType) {
        this.tfServiceOrderType = tfServiceOrderType;
    }

    /**
     * @return the tfServiceOrderPrio
     */
    public AKJTextField getTfServiceOrderPrio() {
        return tfServiceOrderPrio;
    }

    /**
     * @param tfServiceOrderPrio the tfServiceOrderPrio to set
     */
    public void setTfServiceOrderPrio(AKJTextField tfServiceOrderPrio) {
        this.tfServiceOrderPrio = tfServiceOrderPrio;
    }

    /**
     * @return the tfSoStackId
     */
    public AKJTextField getTfSoStackId() {
        return tfSoStackId;
    }

    /**
     * @param tfSoStackId the tfSoStackId to set
     */
    public void setTfSoStackId(AKJTextField tfSoStackId) {
        this.tfSoStackId = tfSoStackId;
    }

    /**
     * @return the tfSoStackSeq
     */
    public AKJTextField getTfSoStackSeq() {
        return tfSoStackSeq;
    }

    /**
     * @param tfSoStackSeq the tfSoStackSeq to set
     */
    public void setTfSoStackSeq(AKJTextField tfSoStackSeq) {
        this.tfSoStackSeq = tfSoStackSeq;
    }

    /**
     * @return the tfRegion
     */
    public AKJTextField getTfRegion() {
        return tfRegion;
    }

    /**
     * @param tfRegion the tfRegion to set
     */
    public void setTfRegion(AKJTextField tfRegion) {
        this.tfRegion = tfRegion;
    }

    /**
     * @return the tfEstimatedExecTime
     */
    public AKJDateComponent getDcEstimatedExecTime() {
        return dcEstimatedExecTime;
    }

    /**
     * @param dcEstimatedExecTime the tfEstimatedExecTime to set
     */
    public void setTfEstimatedExecTime(AKJDateComponent dcEstimatedExecTime) {
        this.dcEstimatedExecTime = dcEstimatedExecTime;
    }

    /**
     * @return the tfResponseAt
     */
    public AKJDateComponent getDcResponseAt() {
        return dcResponseAt;
    }

    /**
     * @param dcResponseAt the tfResponseAt to set
     */
    public void setTfResponseAt(AKJDateComponent dcResponseAt) {
        this.dcResponseAt = dcResponseAt;
    }

    /**
     * @return the tfTxUser
     */
    public AKJTextField getTfTxUser() {
        return tfTxUser;
    }

    /**
     * @param tfTxUser the tfTxUser to set
     */
    public void setTfTxUser(AKJTextField tfTxUser) {
        this.tfTxUser = tfTxUser;
    }

    /**
     * @return the tfUserW
     */
    public AKJTextField getTfUserW() {
        return tfUserW;
    }

    /**
     * @param tfUserW the tfUserW to set
     */
    public void setTfUserW(AKJTextField tfUserW) {
        this.tfUserW = tfUserW;
    }

    /**
     * @return the tfTimest
     */
    public AKJDateComponent getDcTimest() {
        return dcTimest;
    }

    /**
     * @param tfTimest the tfTimest to set
     */
    public void setTfTimest(AKJDateComponent tfTimest) {
        this.dcTimest = tfTimest;
    }

    /**
     * @return the tfRequestAt
     */
    public AKJDateComponent getDcRequestAt() {
        return dcRequestAt;
    }

    /**
     * @return the tfId
     */
    public AKJTextField getTfId() {
        return tfId;
    }

    /**
     * @param dcRequestAt the tfRequestAt to set
     */
    public void setDcRequestAt(AKJDateComponent dcRequestAt) {
        this.dcRequestAt = dcRequestAt;
    }

    /**
     * @see de.augustakom.hurrican.gui.cps.CPSTxObserver#update(de.augustakom.hurrican.gui.cps.CPSTxObservable)
     */
    public void update(CPSTxObservable observable) {
        CPSTxObserverHelper.cleanFields(observable, this);
    }
}
