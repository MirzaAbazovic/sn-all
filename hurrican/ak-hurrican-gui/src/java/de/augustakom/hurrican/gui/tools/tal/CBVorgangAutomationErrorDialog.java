/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2013 10:21:52
 */
package de.augustakom.hurrican.gui.tools.tal;

import java.awt.*;
import java.beans.*;
import java.util.*;
import java.util.List;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.tal.CBVorgangAutomationError;

/**
 *
 */
public class CBVorgangAutomationErrorDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, AKNavigationBarListener {

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/resources/CBVorgangAutomationErrorDialog.xml";

    private static final String DATE_OCCURED = "date.occured";
    private static final String ERROR_MESSAGE2 = "error.message";
    private static final String STACKTRACE = "stacktrace";

    private final List<CBVorgangAutomationError> automationErrors;

    private AKJNavigationBar navBar;
    private AKJDateComponent dcDateOccured;
    private AKJTextArea taErrorMessage;
    private AKJTextArea taStacktrace;

    public CBVorgangAutomationErrorDialog(Set<CBVorgangAutomationError> automationErrors) {
        super(RESOURCE);
        this.automationErrors = new ArrayList<>(automationErrors);
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        navBar = getSwingFactory().createNavigationBar("errors", this, true, true);

        AKJLabel lblDateOccured = getSwingFactory().createLabel(DATE_OCCURED);
        AKJLabel lblErrorMessage = getSwingFactory().createLabel(ERROR_MESSAGE2);
        AKJLabel lblStacktrace = getSwingFactory().createLabel(STACKTRACE);

        dcDateOccured = getSwingFactory().createDateComponent(DATE_OCCURED, false);
        taErrorMessage = getSwingFactory().createTextArea(ERROR_MESSAGE2, false);
        AKJScrollPane spErrorMessage = new AKJScrollPane(taErrorMessage, new Dimension(200, 50));
        taStacktrace = getSwingFactory().createTextArea(STACKTRACE, false);
        AKJScrollPane spStacktrace = new AKJScrollPane(taStacktrace, new Dimension(200, 100));

        // @formatter:on
        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(navBar, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblDateOccured, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(dcDateOccured, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblErrorMessage, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(spErrorMessage, GBCFactory.createGBC(100, 100, 2, 2, 1, 1, GridBagConstraints.BOTH));
        getChildPanel().add(lblStacktrace, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(spStacktrace, GBCFactory.createGBC(100, 100, 2, 3, 1, 1, GridBagConstraints.BOTH));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 4, 1, 1, GridBagConstraints.NONE));
        // @formatter:off
    }


    @Override
    public final void loadData() {
        navBar.setData(automationErrors);
    }

    @Override
    public void showNavigationObject(Object obj, int number) throws PropertyVetoException {
        GuiTools.cleanFields(this);
        if (obj instanceof CBVorgangAutomationError) {
            CBVorgangAutomationError error = (CBVorgangAutomationError) obj;
            dcDateOccured.setDate(error.getDateOccured());
            taErrorMessage.setText(error.getErrorMessage());
            taStacktrace.setText(error.getStacktrace());
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void doSave() {
    }

}


