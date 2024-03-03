/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2009 07:30:12
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog, ueber den fuer einen (technischen) Auftrag eine CPS-Transaction angelegt werden kann. <br> Ueber den Dialog
 * werden div. Details fuer die CPS-Tx definiert, z.B. <br> <ul> <li>Ausfuehrungsdatum und -zeit bzw. 'sofort'
 * <li>Prioritaet der CPS-Tx <li>UseCase (z.B. 'modifySub', 'cancelSub') </ul> Beim Bestaetigen des Dialogs wird die
 * CPS-Tx angelegt und anschliessend die Transaction an den CPS-Server gesendet.
 *
 *
 */
public class CPSTxCreationDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, PropertyChangeListener {

    private static final Logger LOGGER = Logger.getLogger(CPSTxCreationDialog.class);

    private static final String TITLE = "title";
    private static final String EXECUTION = "execution";
    private static final String EXECUTION_DETAILS = "execution.details";
    private static final String EXECUTION_NOW = "execution.now";
    private static final String EXECUTION_AT = "execution.at";
    private static final String EXECUTION_PRIO = "execution.prio";
    private static final String EXECUTION_TYPE = "execution.type";
    private static final String EXECUTION_AT_TIME = "execution.at.time";
    private static final String SAVE_TOOLTIP = "save.tooltip";
    private static final String FORCE_EXEC_TYPE = "force.execution";
    private static final String FORCE_EXEC_TYPE_PROPERTY_NAME = "referenceId";

    // GUI-Komponenten
    private AKJCheckBox chbExecNow = null;
    private AKJDateComponent dcExecAt = null;
    private AKJDateComponent dcExecAtTime = null;
    private AKReferenceField rfExecPrio = null;
    private AKReferenceField rfExecType = null;
    private AKJCheckBox chbForceExecType = null;

    // sonstiges
    private Long auftragId = null;
    private String formatForceExecType = null;

    /**
     * Konstruktor mit Angabe der Auftrags-ID fuer die eine CPS-Transaction angelegt werden soll.
     *
     * @param auftragId ID des (technischen) Auftrags
     */
    public CPSTxCreationDialog(Long auftragId) {
        super("de/augustakom/hurrican/gui/cps/resources/CPSTxCreationDialog.xml");
        this.auftragId = auftragId;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText(TITLE));
        AKJLabel lblExecution = getSwingFactory().createLabel(EXECUTION, AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblExecDetails = getSwingFactory().createLabel(EXECUTION_DETAILS, AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblExecNow = getSwingFactory().createLabel(EXECUTION_NOW);
        AKJLabel lblExecAt = getSwingFactory().createLabel(EXECUTION_AT);
        AKJLabel lblExecPrio = getSwingFactory().createLabel(EXECUTION_PRIO);
        AKJLabel lblExecType = getSwingFactory().createLabel(EXECUTION_TYPE);

        chbExecNow = getSwingFactory().createCheckBox(EXECUTION_NOW, new ExecNowActionListener(), true);
        dcExecAt = getSwingFactory().createDateComponent(EXECUTION_AT, false);
        dcExecAtTime = getSwingFactory().createDateComponent(EXECUTION_AT_TIME, false);
        rfExecPrio = getSwingFactory().createReferenceField(EXECUTION_PRIO);
        rfExecType = getSwingFactory().createReferenceField(EXECUTION_TYPE);
        chbForceExecType = getSwingFactory().createCheckBox(FORCE_EXEC_TYPE);

        AKJPanel pnl = new AKJPanel(new GridBagLayout());
        pnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        pnl.add(lblExecution, GBCFactory.createGBC(0, 0, 1, 1, 4, 1, GridBagConstraints.HORIZONTAL));
        pnl.add(lblExecNow, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        pnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.NONE));
        pnl.add(chbExecNow, GBCFactory.createGBC(100, 0, 3, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        pnl.add(lblExecAt, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        pnl.add(dcExecAt, GBCFactory.createGBC(50, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        pnl.add(dcExecAtTime, GBCFactory.createGBC(50, 0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        pnl.add(lblExecDetails, GBCFactory.createGBC(0, 0, 1, 4, 4, 1, GridBagConstraints.HORIZONTAL));
        pnl.add(lblExecPrio, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        pnl.add(rfExecPrio, GBCFactory.createGBC(100, 0, 3, 5, 2, 1, GridBagConstraints.HORIZONTAL));
        pnl.add(lblExecType, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        pnl.add(rfExecType, GBCFactory.createGBC(100, 0, 3, 6, 2, 1, GridBagConstraints.HORIZONTAL));
        pnl.add(chbForceExecType, GBCFactory.createGBC(100, 0, 3, 7, 2, 1, GridBagConstraints.HORIZONTAL));
        pnl.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 8, 1, 1, GridBagConstraints.BOTH));

        getButton(CMD_SAVE).setToolTipText(getSwingFactory().getText(SAVE_TOOLTIP));
        getChildPanel().add(pnl);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            QueryCCService queryService = getCCService(QueryCCService.class);

            formatForceExecType = getSwingFactory().getText(FORCE_EXEC_TYPE);

            Reference execPrioRefEx = new Reference();
            execPrioRefEx.setType(Reference.REF_TYPE_CPS_SERVICE_ORDER_PRIO);
            execPrioRefEx.setGuiVisible(Boolean.TRUE);
            rfExecPrio.setReferenceFindExample(execPrioRefEx);
            rfExecPrio.setFindService(queryService);
            rfExecPrio.setReferenceId(CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT);

            Reference execTypeRefEx = new Reference();
            execTypeRefEx.setType(Reference.REF_TYPE_CPS_SERVICE_ORDER_TYPE);
            execTypeRefEx.setGuiVisible(Boolean.TRUE);
            rfExecType.setReferenceFindExample(execTypeRefEx);
            rfExecType.setFindService(queryService);
            rfExecType.setReferenceId(CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB);

            propertyChange(new PropertyChangeEvent(rfExecType,
                    FORCE_EXEC_TYPE_PROPERTY_NAME, null, rfExecType.getReferenceId()));
            rfExecType.addPropertyChangeListener(this);

            // Datumsobjekt fuer den naechsten Werktag (8 Uhr) erzeugen
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(DateTools.plusWorkDays(1));
            Date nextWorkDay = DateTools.createDate(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 8, 0);
            dcExecAt.setDate(nextWorkDay);
            dcExecAtTime.setDate(nextWorkDay);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            setWaitCursor();
            CPSService cps = getCCService(CPSService.class);

            Long prio = rfExecPrio.getReferenceIdAs(Long.class);
            Long type = rfExecType.getReferenceIdAs(Long.class);
            Date execDate = (chbExecNow.isSelected()) ? DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH) : null;
            if (execDate == null) {
                // ExecutionTime aus Datum/Zeit erstellen
                execDate = dcExecAt.getDate(new Date());
                Date time = dcExecAtTime.getDate(new Date());
                GregorianCalendar timeCal = new GregorianCalendar();
                timeCal.setTime(time);

                GregorianCalendar at = new GregorianCalendar();
                at.setTime(execDate);
                at.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
                at.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));

                execDate = at.getTime();
            }

            if (DateTools.isDateBefore(execDate, new Date())) {
                throw new HurricanGUIException(
                        "Bitte definieren Sie ein gueltiges Datum (>= heute)");
            }

            prepare4Close();
            CPSTransactionResult cpsTxResult = cps.createCPSTransaction(
                    new CreateCPSTransactionParameter(auftragId, null, type, CPSTransaction.TX_SOURCE_HURRICAN_ORDER, prio, execDate, null,
                            null, null, null, Boolean.FALSE, isExecTypeForced(), HurricanSystemRegistry.instance().getSessionId())
            );

            CPSTransaction cpsTx = null;
            if (CollectionTools.isNotEmpty(cpsTxResult.getCpsTransactions())) {
                cpsTx = cpsTxResult.getCpsTransactions().get(0);
            }
            else {
                String warnings = (cpsTxResult.getWarnings() != null)
                        ? cpsTxResult.getWarnings().getWarningsAsText() : null;
                throw new HurricanServiceCommandException(warnings);
            }

            // Frage an User, ob die Uebermittlung an den CPS durchgefuehrt werden soll
            int result = MessageHelper.showConfirmDialog(getMainFrame(),
                    "CPS-Transaction (ID: " + cpsTx.getId() + ") wurde erstellt.\n\n" +
                            "Soll die Transaction an den CPS übertragen werden?",
                    "CPS-Übertragung?", AKJOptionDialog.YES_NO_OPTION, AKJOptionDialog.QUESTION_MESSAGE
            );

            if (result == AKJOptionDialog.YES_OPTION) {
                cps.sendCPSTx2CPS(cpsTx, HurricanSystemRegistry.instance().getSessionId());

                MessageHelper.showInfoDialog(getMainFrame(), "Transaction wurde an den CPS übertragen.");
            }

            setValue(cpsTx);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == rfExecType) {
            // Fuer das Execution Typ Referenzfeld muessen Aenderungen an die
            // zugehoerige Checkbox weitergereicht werden.
            String name = evt.getPropertyName();
            Object value = evt.getNewValue();

            if (StringUtils.equals(name, FORCE_EXEC_TYPE_PROPERTY_NAME)) {
                Long[] validStates = new Long[] { CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB,
                        CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB };
                updateCheckBoxForceExecType(NumberTools.isIn((value instanceof Long) ? (Long) value : Long.valueOf(-1),
                        validStates));
            }
        }
    }

    /**
     * Aendert sich die Provisionierungsart, muss die Checkbox angepasst werden. Jede neue Provisionierungsart setzt
     * automatisch die Auswahl der Checkbox zurueck. Der User ist dadurch gezwungen das 'Force Flag' jedes Mal explizit
     * zu aktivieren. Ungewolltes erzwingen einer Provisionierungsart sollte somit vermieden werden.
     */
    private void updateCheckBoxForceExecType(Boolean enabled) {
        String executionType = getExecType(rfExecType.getReferenceObject());
        String message = StringTools.formatString(formatForceExecType, new Object[] { executionType });
        chbForceExecType.setText(message);
        chbForceExecType.setSelected(false);
        chbForceExecType.setEnabled(enabled);
    }

    /**
     * Ermittelt den Execution Typ aus dem Referenzfeld als String.
     */
    private String getExecType(Object refObject) {
        String result = null;
        if ((refObject != null) && (refObject instanceof Reference)) {
            Reference reference = (Reference) refObject;
            result = reference.getStrValue();
        }

        return ((result != null) && (result.length() > 0)) ? result : "?";
    }

    private Boolean isExecTypeForced() {
        return (chbForceExecType.isEnabled() && chbForceExecType.isSelected()) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * ActionListener, um auf Selektion der CheckBox 'chbExecNow' zu reagieren
     */
    class ExecNowActionListener implements ActionListener {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean execNow = chbExecNow.isSelected();
            dcExecAt.setUsable(!execNow);
            dcExecAtTime.setEditable(!execNow);
        }
    }

}
