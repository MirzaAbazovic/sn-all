/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKReferenceFieldEvent;
import de.augustakom.common.gui.swing.AKReferenceFieldObserver;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.tools.wbci.tables.MeldungsCodesTableModel;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.DecisionVO;
import de.mnet.wbci.model.DecisionVOHelper;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungPositionAbbmRufnummer;
import de.mnet.wbci.model.MeldungPositionTyp;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.AbbruchmeldungBuilder;
import de.mnet.wbci.service.WbciDecisionService;
import de.mnet.wbci.service.WbciMeldungService;

/**
 * Abstract base class for all ABBM Dialogs
 *
 *
 */
public abstract class AbstractAbbmDialog<T extends WbciRequest, M extends Meldung> extends AbstractServiceOptionDialog
        implements AKDataLoaderComponent {
    private static final long serialVersionUID = -9179316066351932364L;

    protected final Logger LOGGER = Logger.getLogger(getClass()); // NOSONAR squid:S1312

    private static final String ZF3_ABLEHNUNGSGRUND = "ZF3 nicht möglich. Bitte Vorabstimmungsanfrage erneut einstellen mit ZF1 oder ZF2.";

    protected static final String INFO_LBL = "dialog.info.label";
    protected static final String INFO_TXT = "dialog.info.text";
    protected static final String DIALOG_TITLE = "dialog.title";
    protected static final String DETAILS_LBL = "details.label";
    protected static final String SONST_LBL = "sonstiges.label";
    protected static final String SONST_TF = "sonstiges.text";
    protected static final String SONST_EXCL_LBL = "sonstiges.exclusiv.label";
    protected static final String SONST_EXCL_GRUND = "sonstiges.exclusiv.grund";
    protected final Set<MeldungPositionAbbmRufnummer> activeAbbmRufnummern;

    protected MeldungsCodesTableModel tbMdlAbbm;
    protected WbciDecisionService wbciDecisionService;
    protected final T wbciRequest;
    protected AKJTextField tfSonstiges = null;
    protected AKReferenceField rfSonstigesExclusiv = null;
    protected AKJTable tbAbbm;
    protected WbciMeldungService wbciMeldungService;
    protected AKJLabel lblDetails;
    protected AKJLabel lblSonstiges;
    protected AKJLabel lblSonstigesExclusiv;
    protected final List<DecisionVO> decisionVOs;

    protected boolean useInfoPanel = false;
    protected boolean useSonstigesPanel = true;

    private String sonstigesText;

    /**
     * Abstract constructor
     */
    protected AbstractAbbmDialog(final String resource, T wbciRequest, List<DecisionVO> decisionVOs,
            boolean useInfoPanel, boolean useSonstigesPanel) {
        super(resource, true, true);
        this.wbciRequest = wbciRequest;
        this.decisionVOs = decisionVOs;
        this.activeAbbmRufnummern = (decisionVOs != null)
                ? DecisionVOHelper.extractMeldungPositionAbbmRufnummern(decisionVOs)
                : new HashSet<MeldungPositionAbbmRufnummer>();
        this.useInfoPanel = useInfoPanel;
        this.useSonstigesPanel = useSonstigesPanel;

        try {
            initServices();
            createGUI();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /**
     * Looks up to the CCServices and init the service.
     *
     * @throws de.augustakom.common.service.exceptions.ServiceNotFoundException if a service could not be looked up
     */
    protected final void initServices() throws ServiceNotFoundException {
        wbciMeldungService = getCCService(WbciMeldungService.class);
        wbciDecisionService = getCCService(WbciDecisionService.class);
    }

    @Override
    protected final void createGUI() {
        initAbbmGuiComponents();

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(createDetailPanel(), BorderLayout.CENTER);

        configureButton(CMD_SAVE, getSwingFactory().getText("send.txt"), getSwingFactory().getText("send.tooltip"),
                true, true);
        configureButton(CMD_CANCEL, getSwingFactory().getText("cancel.txt"), getSwingFactory()
                .getText("cancel.tooltip"), true, true);
    }

    protected void initAbbmGuiComponents() {
        setTitle(String.format(getSwingFactory().getText(DIALOG_TITLE),
                wbciRequest.getWbciGeschaeftsfall().getVorabstimmungsId()));

        lblDetails = getSwingFactory().createLabel(DETAILS_LBL, AKJLabel.LEFT, Font.BOLD);

        tbMdlAbbm = new MeldungsCodesTableModel();
        tbAbbm = new AKJTable(tbMdlAbbm, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
    }

    protected AKJPanel createDetailPanel() {
        AKJScrollPane scrollPane = new AKJScrollPane(tbAbbm, new Dimension(500, 200));
        tbAbbm.fitTable(new int[] { 80, 250, 150 });

        AKJPanel dtlPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        dtlPnl.add(new AKJPanel()        , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(createInfoPanel()     , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblDetails            , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(scrollPane            , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(createSonstigesPanel(), GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()        , GBCFactory.createGBC(100,100, 2, 5, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        return dtlPnl;
    }

    /**
     * Creates info panel with hinweis text and label. Based on boolean flag useInfoPanel which is set by subclasses in
     * case info panel should be added. Otherwise use empty panel.
     *
     * @return AKJPanel
     */
    private AKJPanel createInfoPanel() {
        if (useInfoPanel) {
            AKJLabel lblInfo = getSwingFactory().createLabel(INFO_LBL, AKJLabel.LEFT, Font.BOLD);
            AKJLabel lblInfoText = getSwingFactory().createLabel(INFO_TXT, AKJLabel.LEFT);
            lblInfoText.convertCurrentTextToMultiline();

            AKJPanel infoPnl = new AKJPanel(new GridBagLayout());
            // @formatter:off
            infoPnl.add(lblInfo          , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPnl.add(lblInfoText      , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPnl.add(new AKJPanel()   , GBCFactory.createGBC(100,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            // @formatter:on

            return infoPnl;
        }
        else {
            return new AKJPanel();
        }
    }

    private AKJPanel createSonstigesPanel() {
        if (useSonstigesPanel) {
            lblSonstiges = getSwingFactory().createLabel(SONST_LBL, AKJLabel.LEFT, Font.BOLD);

            tfSonstiges = getSwingFactory().createTextField(SONST_TF, true, false);
            tfSonstiges.setMaximumSize(new Dimension(430, 80));

            lblSonstigesExclusiv = getSwingFactory().createLabel(SONST_EXCL_LBL, AKJLabel.LEFT, Font.BOLD);

            rfSonstigesExclusiv = getSwingFactory().createReferenceField(SONST_EXCL_GRUND, Reference.class, "id",
                    "strValue", new Reference(Reference.REF_TYPE_ABBM_REASON_TYPE));
            rfSonstigesExclusiv.addObserver(new AKReferenceFieldObserver() {
                @Override
                public void update(AKReferenceFieldEvent akReferenceFieldEvent) throws Exception {
                    if (rfSonstigesExclusiv.getReferenceObject() != null) {
                        tfSonstiges.setEnabled(false);
                        if (StringUtils.isNotBlank(tfSonstiges.getText())) {
                            sonstigesText = tfSonstiges.getText();
                            tfSonstiges.setText("");
                        }
                        tbAbbm.setVisible(false);
                    }
                    else {
                        tfSonstiges.setEnabled(true);
                        tfSonstiges.setText(sonstigesText);
                        setZF3ReasonIfPresent();
                        tbAbbm.setVisible(true);
                    }
                }
            });
            rfSonstigesExclusiv.setBackground(Color.WHITE);
            rfSonstigesExclusiv.setMaximumSize(new Dimension(430, 80));

            AKJPanel sonstigesPanel = new AKJPanel(new GridBagLayout());
            // @formatter:off
            sonstigesPanel.add(lblSonstiges,            GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            sonstigesPanel.add(tfSonstiges,             GBCFactory.createGBC(  0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            sonstigesPanel.add(new JSeparator(),        GBCFactory.createGBC(  0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            sonstigesPanel.add(lblSonstigesExclusiv,    GBCFactory.createGBC(  0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
            sonstigesPanel.add(rfSonstigesExclusiv,     GBCFactory.createGBC(  0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
            sonstigesPanel.add(new AKJPanel(),          GBCFactory.createGBC(100, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
            // @formatter:on

            return sonstigesPanel;
        }
        else {
            return new AKJPanel();
        }
    }

    @Override
    public void loadData() {
        try {
            setWaitCursor();

            tbMdlAbbm.removeAll();
            
            if (decisionVOs != null) {
                tbMdlAbbm.setData(wbciDecisionService.getDecisionVOsForMeldungPositionTyp(decisionVOs, getPositionType()));
                setZF3ReasonIfPresent();
            }

            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfSonstigesExclusiv.setFindService(sfs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    private void setZF3ReasonIfPresent() {
        if (DecisionVOHelper.containsPortierungszeitfenserZF3(decisionVOs)) {
            tfSonstiges.setText(ZF3_ABLEHNUNGSGRUND);
            tfSonstiges.setEnabled(false);
        }
    }

    /**
     * @return Subclasses have to return the proper {@link MeldungPositionTyp}.
     */
    protected abstract MeldungPositionTyp getPositionType();


    @Override
    protected void validateSaveButton() {
        // intentionally left blank
    }

    @Override
    protected void cancel() {
        prepare4Close();
        setValue(null);
    }

    @Override
    protected void doSave() {
        try {
            wbciMeldungService.createAndSendWbciMeldung(createMeldung(), wbciRequest.getVorabstimmungsId());
            prepare4Close();
            setValue(wbciRequest);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
        // intentionally left blank
    }

    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }

    /**
     * Subclasses must overwrite this method in order to get proper {@link de.mnet.wbci.model.Meldung} object to send.
     *
     * @return an {@link Abbruchmeldung} or {@link de.mnet.wbci.model.AbbruchmeldungTechnRessource} which should be send
     * out
     */
    protected abstract M createMeldung();

    /**
     * @return a basic ABBM Builder
     */
    protected AbbruchmeldungBuilder createAbbmBuilder() {
        if (rfSonstigesExclusiv.getReferenceObject() == null) {
            return DecisionVOHelper.createAbbmBuilderFromDecisionVo(
                    tbMdlAbbm.getData(),
                    tfSonstiges.getText(null),
                    wbciRequest.getWbciGeschaeftsfall(),
                    activeAbbmRufnummern);
        }
        else {
            // Just use exclusive sonstiges position
            return DecisionVOHelper.createAbbmBuilderFromDecisionVo(
                    Collections.<DecisionVO>emptyList(),
                    rfSonstigesExclusiv.getReferenceObjectAs(Reference.class).getStrValue(),
                    wbciRequest.getWbciGeschaeftsfall(),
                    Collections.<MeldungPositionAbbmRufnummer>emptySet());
        }
    }

}
