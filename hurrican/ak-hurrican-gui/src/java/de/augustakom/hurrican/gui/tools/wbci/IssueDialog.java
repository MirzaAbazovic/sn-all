/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.12.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Dialog, um eine WBCI Klaerfall aufzuloesen.
 */
public class IssueDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final long serialVersionUID = 2588851129053215119L;

    private static final Logger LOGGER = Logger.getLogger(IssueDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/IssueDialog.xml";

    protected static final String DEFAULT_ISSUE_RESOLVED_COMMENT = "Klärfall aufgelöst";

    public static final String DIALOG_TITLE = "dialog.title";
    private static final String COMMENT_LBL = "comment.label";
    private static final String COMMENT_TXT = "comment.text";
    protected AKJLabel lblComment;
    protected AKJTextField tfComment;

    private final WbciRequest wbciRequest;

    private WbciGeschaeftsfallService wbciGeschaeftsfallService;

    /**
     * Konstruktor mit Angabe des {@link WbciRequest}s, der als Klaerfall gekennzeichnet ist.
     *
     * @param wbciRequest
     */
    public IssueDialog(WbciRequest wbciRequest) {
        super(RESOURCE, true, true);
        this.wbciRequest = wbciRequest;

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
    protected void initServices() throws ServiceNotFoundException {
        wbciGeschaeftsfallService = getCCService(WbciGeschaeftsfallService.class);
    }

    @Override
    protected final void createGUI() {
        setTitle(String.format(getSwingFactory().getText(DIALOG_TITLE), getVorabstimmungsId()));

        lblComment = getSwingFactory().createLabel(COMMENT_LBL);
        tfComment = getSwingFactory().createTextField(COMMENT_TXT, true, false);
        tfComment.setMaximumSize(new Dimension(430, 80));
        tfComment.setText(DEFAULT_ISSUE_RESOLVED_COMMENT);

        AKJPanel dtlPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(lblComment           , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(tfComment            , GBCFactory.createGBC(  0,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(100,100, 4, 8, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(dtlPnl, BorderLayout.CENTER);

        configureButton(CMD_SAVE, getSwingFactory().getText("save.txt"), getSwingFactory().getText("save.tooltip"),
                true, true);
        configureButton(CMD_CANCEL, getSwingFactory().getText("cancel.txt"), getSwingFactory()
                .getText("cancel.tooltip"), true, true);
    }

    @Override
    protected void validateSaveButton() {
        // nothing to do
    }

    @Override
    public final void loadData() {
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void cancel() {
        prepare4Close();
        setValue(null);
    }

    @Override
    protected void doSave() {
        try {
            Long gfId = wbciRequest.getWbciGeschaeftsfall().getId();
            AKUser currentUser = HurricanSystemRegistry.instance().getCurrentUser();

            String comment = tfComment.getText();
            if (StringUtils.isBlank(comment)) {
                comment = DEFAULT_ISSUE_RESOLVED_COMMENT;
            }
            wbciGeschaeftsfallService.issueClarified(gfId, currentUser, comment);

            prepare4Close();
            setValue(wbciRequest);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private String getVorabstimmungsId() {
        if (wbciRequest != null && wbciRequest.getWbciGeschaeftsfall() != null) {
            return wbciRequest.getWbciGeschaeftsfall().getVorabstimmungsId();
        }
        return "";
    }

}
