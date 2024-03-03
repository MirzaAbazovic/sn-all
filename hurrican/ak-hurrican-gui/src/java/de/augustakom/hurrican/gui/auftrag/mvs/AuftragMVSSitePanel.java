/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2012 10:45:40
 */
package de.augustakom.hurrican.gui.auftrag.mvs;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.exceptions.NoDataFoundException;
import de.augustakom.common.tools.messages.AKWarning;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.model.cc.AuftragMVSSite;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.validation.cc.SubdomainValidator;

/**
 * Panel fuer die Darstellung der MVS Site-Eintraege, die dem Auftrag zugeordnet sind.
 *
 *
 * @since Release 11
 */
public class AuftragMVSSitePanel extends AbstractAuftragMVSPanel<AuftragMVSSite> {

    private static final String MVS_ENTERPRISE_NOT_FOUND = "mvs.enterprise.not.found";

    private static final long serialVersionUID = 274159367717003316L;

    private static final Logger LOGGER = Logger.getLogger(AuftragMVSSitePanel.class);

    private static final String STANDORT_KUERZEL = "standortKuerzel";

    private SubdomainValidator subdomainValidator;
    private AuftragMVSEnterprise mvsEnterpriseModel = null;

    private AKJTextField tfStandortKuerzel;

    public AuftragMVSSitePanel() {
        super("de/augustakom/hurrican/gui/auftrag/mvs/resources/AuftragMVSSitePanel.xml");
        createGUI();
        GuiTools.enableComponents(getComponentsToEnable(), false, true);
        setSubdomainValidator(new SubdomainValidator(getMVSService()));
    }

    protected AuftragMVSEnterprise getMvsEnterpriseModel() {
        return mvsEnterpriseModel;
    }

    protected void setMvsEnterpriseModel(AuftragMVSEnterprise mvsEnterpriseModel) {
        this.mvsEnterpriseModel = mvsEnterpriseModel;
    }

    protected SubdomainValidator getSubdomainValidator() {
        return subdomainValidator;
    }

    protected final void setSubdomainValidator(SubdomainValidator subdomainValidator) {
        this.subdomainValidator = subdomainValidator;
    }

    @Override
    public void readModel() throws AKGUIException {
        try {
            GuiTools.cleanFields(this);
            if (getAuftragModel() == null) { return; }

            final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                @Override
                public Void doInBackground() throws Exception {
                    try {
                        loadEnterpriseModel();
                        loadSiteModel();
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        showMvsSiteValues();
                    }
                    finally {
                        setDefaultCursor();
                    }
                }
            };

            enableAllComponents(true);
            setWaitCursor();
            worker.execute();

        }
        catch (Exception e) {
            enableAllComponents(false);
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void showMvsSiteValues() {
        enableOrDisableNewButtonAndFields();
        if (getMvsModel() != null) {
            tfUsername.setText(getMvsModel().getUserName());
            tfPassword.setText(getMvsModel().getPassword());
            tfDomain.setText(getMvsModel().getSubdomain());
            tfStandortKuerzel.setText(getMvsModel().getStandortKuerzel());
        }
    }

    protected void loadEnterpriseModel() throws FindException, NoDataFoundException {
        AuftragMVSEnterprise enterprise = getMVSService().findEnterpriseForSiteAuftragId(
                getAuftragModel().getAuftragId());
        setMvsEnterpriseModel(enterprise);
    }

    protected void loadSiteModel() throws FindException {
        AuftragMVSSite siteModel = getMVSService().findMvsSite4Auftrag(getAuftragModel().getAuftragId(), true);
        if (siteModel != null) {
            setMvsModel(siteModel);
            showMvsSiteValues();
        }
    }

    protected boolean hasSubdomainNotChanged() {
        boolean result = (getMvsModel() != null) && getMvsModel().getSubdomain().equals(tfDomain.getText());
        return result;
    }

    protected void validateDomain() throws HurricanGUIException {
        if (hasSubdomainNotChanged()) { return; }

        AKWarning warning = null;
        try {
            warning = getSubdomainValidator().validate(tfDomain.getText(), getMvsEnterpriseModel());
        }
        catch (FindException e) {
            throw new HurricanGUIException("Bei der Validierung ist ein Fehler aufgetreten!", e);
        }
        if (warning != null) { throw new HurricanGUIException(warning.getMessage()); }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        if (getMvsEnterpriseModel() != null) {
            try {
                validateDomain();
                AuftragMVSSite siteModel = getMvsModel();
                if (siteModel == null) {
                    siteModel = new AuftragMVSSite();
                }
                siteModel.setUserName(tfUsername.getText());
                siteModel.setPassword(tfPassword.getText());
                siteModel.setSubdomain(tfDomain.getText().trim());
                siteModel.setAuftragId(getAuftragModel().getAuftragId());
                siteModel.setParent(getMvsEnterpriseModel());
                getMVSService().saveAuftragMvs(siteModel);
                loadSiteModel();
            }
            catch (StoreException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showWarningDialog(this, e.getMessage(), true);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            finally {
                setDefaultCursor();
            }
        }
        else {
            MessageHelper.showWarningDialog(this, getSwingFactory().getText(MVS_ENTERPRISE_NOT_FOUND), true);
        }
    }

    @Override
    protected void createNewEntry() {
        try {
            if (getMvsEnterpriseModel() == null) {
                MessageHelper.showWarningDialog(this, getSwingFactory().getText(MVS_ENTERPRISE_NOT_FOUND), true);
                return;
            }

            super.createNewEntry();
            tfDomain.setText("");
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        // Neuer Datensatz Button?
        if (NEW.equals(command)) {
            createNewEntry();
        }
        // Zeige bisher vergebene Domains
        else if (DOMAINS.equals(command)) {
            Collection<String> domains = findAllUsedSubdomains();
            AuftragMVSDomainsDialog dialog = new AuftragMVSDomainsDialog("Subdomains", domains);
            dialog.setTitle("Alle bereits verwendeten Subdomains");
            DialogHelper.showDialog(getMainFrame(), dialog, false, true);
        }
    }

    Collection<String> findAllUsedSubdomains() {
        Collection<String> result = Collections.emptyList();
        try {
            result = getMVSService().findAllUsedSubdomains(getMvsEnterpriseModel());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        return result;
    }

    @Override
    protected void addSpecificGUIElements(int yCounter) {
        AKJLabel lblStandortKuerzel = getSwingFactory().createLabel(STANDORT_KUERZEL);
        tfStandortKuerzel = getSwingFactory().createTextField(STANDORT_KUERZEL, false);

        // @formatter:off
        getMvsDataPanel().add(lblStandortKuerzel , GBCFactory.createGBC(  0,  0, 1, yCounter, 1, 1, GridBagConstraints.HORIZONTAL));
        getMvsDataPanel().add(tfStandortKuerzel  , GBCFactory.createGBC(  0,  0, 3, yCounter, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

}
