/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2012 15:28:58
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.IconHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.RegistryService;

/**
 * Dialog, um die DSL Synchraten (Attainable Bitrates) anzuzeigen
 */
public class QueryDSLBitratesDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(QueryDSLBitratesDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/cps/resources/QueryDSLBitratesDialog.xml";
    private static final String BTN_CLOSE_TOOLTIP = "btn.close.tooltip";
    private static final String BTN_CLOSE_TEXT = "btn.close.text";
    private static final String PROGRESS_TEXT = "progress.text";
    private static final String FTF_DOWNSTREAM = "tf.downstream";
    private static final String FTF_UPSTREAM = "tf.upstream";
    private static final String TITLE = "title";
    private static final String AMPEL_BASE_PATH = "de/augustakom/hurrican/gui/images/";
    private static final String AMPEL_YELLOW = "light_yellow.gif";
    private static final String AMPEL_GREEN = "light_green.gif";
    private static final String AMPEL_RED = "light_red.gif";
    private static final String AMPEL_OUT = "light_out.gif";
    private static final String LBL_AMPEL = "lbl.ampel";
    private static final String RESULT_YELLOW = "result.yellow";
    private static final String RESULT_GREEN = "result.green";
    private static final String RESULT_RED = "result.red";
    private static final String RESULT_OUT = "result.out";
    private static final String TA_RESULT = "ta.result";

    private AKJFormattedTextField ftfDownstream = null;
    private AKJFormattedTextField ftfUpstream = null;
    private AKJTextArea taResult = null;
    private AKJLabel lblAmpel = null;

    private Pair<Integer, Integer> bitrates = null;
    private Long auftragId = null;

    public QueryDSLBitratesDialog(Long auftragId) {
        super(RESOURCE);
        this.auftragId = auftragId;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(new StringBuilder(getSwingFactory().getText(TITLE)).append(auftragId).toString());
        configureButton(CMD_CANCEL, null, null, Boolean.FALSE, Boolean.FALSE);
        configureButton(CMD_SAVE, getSwingFactory().getText(BTN_CLOSE_TEXT),
                getSwingFactory().getText(BTN_CLOSE_TOOLTIP), Boolean.TRUE, Boolean.TRUE);

        AKJLabel lblDownstream = getSwingFactory().createLabel(FTF_DOWNSTREAM);
        AKJLabel lblUpstream = getSwingFactory().createLabel(FTF_UPSTREAM);
        lblAmpel = getSwingFactory().createLabel(LBL_AMPEL);

        ftfDownstream = getSwingFactory().createFormattedTextField(FTF_DOWNSTREAM);
        ftfDownstream.setEditable(false);
        ftfUpstream = getSwingFactory().createFormattedTextField(FTF_UPSTREAM);
        ftfUpstream.setEditable(false);
        taResult = getSwingFactory().createTextArea(TA_RESULT);
        taResult.setEditable(false);

        setResultMessage(RESULT_OUT);
        setAmpelIcon(AMPEL_OUT);

        AKJPanel ampelPanel = new AKJPanel(new GridBagLayout());

        // @formatter:off
        ampelPanel.add(lblAmpel,       GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        ampelPanel.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        ampelPanel.add(taResult,       GBCFactory.createGBC(100,100, 2, 0, 1, 3, GridBagConstraints.BOTH));
        // @formatter:on

        // @formatter:off
        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(lblDownstream,  GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(ftfDownstream,  GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblUpstream,    GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(ftfUpstream,    GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(ampelPanel,     GBCFactory.createGBC(  0,  0, 0, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100,100, 3, 3, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
    }

    @Override
    protected void validateSaveButton() {
        // no validation for the save button!
    }

    private void setResultMessage(String whichMessage) {
        taResult.setText(getSwingFactory().getText(whichMessage));
    }

    private void setAmpelIcon(String whichIcon) {
        Icon icnAmpel = new IconHelper().getIcon(AMPEL_BASE_PATH + whichIcon);
        lblAmpel.setIcon(icnAmpel);
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void doSave() {
        prepare4Close();
        setValue(null);
    }

    @Override
    protected void execute(String command) {
    }

    private void showValues() throws ServiceNotFoundException, FindException {
        if (bitrates != null) {
            RegistryService registryService = getCCService(RegistryService.class);
            ftfDownstream.setText(bitrates.getFirst().toString());
            ftfUpstream.setText(bitrates.getSecond().toString());
            Integer threshold = registryService.getIntValue(RegistryService.REGID_DSL_DOWNSTREAM_THRESHOLD);
            if (threshold == null) {
                throw new FindException("Der Schwellwert für die Downstream Bitrate konnte nicht ermittelt werden!");
            }
            if ((bitrates.getFirst() == null) || NumberTools.isLessOrEqual(bitrates.getFirst(), Integer.valueOf(0))) {
                setAmpelIcon(AMPEL_YELLOW);
                setResultMessage(RESULT_YELLOW);
            }
            else if (NumberTools.isLessOrEqual(bitrates.getFirst(), threshold)) {
                setAmpelIcon(AMPEL_GREEN);
                setResultMessage(RESULT_GREEN);
            }
            else {
                setAmpelIcon(AMPEL_RED);
                setResultMessage(RESULT_RED);
            }
        }
        else {
            setAmpelIcon(AMPEL_YELLOW);
            setResultMessage(RESULT_YELLOW);
        }
    }

    @Override
    public final void loadData() {
        setWaitCursor();
        showProgressBar(getSwingFactory().getText(PROGRESS_TEXT));
        final SwingWorker<Pair<Integer, Integer>, Void> worker = new SwingWorker<Pair<Integer, Integer>, Void>() {

            @Override
            protected Pair<Integer, Integer> doInBackground() throws Exception {
                CPSService cpsService = getCCService(CPSService.class);
                Pair<Integer, Integer> result = cpsService.queryAttainableBitrate(auftragId, HurricanSystemRegistry
                        .instance().getSessionId());
                return result;
            }

            @Override
            protected void done() {
                try {
                    bitrates = get();
                    if (bitrates == null) {
                        MessageHelper.showInfoDialog(getMainFrame(), String.format(
                                "Zur Auftrag ID %d können momentan keine Bitraten ermittelt werden!", auftragId));
                    }
                    showValues();
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    setDefaultCursor();
                    stopProgressBar();
                }
            }

        };
        worker.execute();
    }

}


