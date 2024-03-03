/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.01.2012 13:52:16
 */
package de.augustakom.hurrican.gui.auftrag.mvs;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.messages.AKWarning;
import de.augustakom.common.tools.validation.EMailValidator;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.validation.cc.DomainValid.DomainValidator;

/**
 * Panel fuer die Darstellung der MVS Enterprise-Eintraege, die dem Auftrag zugeordnet sind.
 *
 *
 * @since Release 11
 */
public class AuftragMVSEnterprisePanel extends AbstractAuftragMVSPanel<AuftragMVSEnterprise> {

    private static final long serialVersionUID = 1087610891531719719L;

    private static final Logger LOGGER = Logger.getLogger(AuftragMVSEnterprisePanel.class);

    private static final String ID_DOMAINS = "domains",
            ID_NEW = "new",
            BLANK_STRING = "";

    private AKJTextField tfEmailAddress;
    private DomainValidator domainValidator;

    public AuftragMVSEnterprisePanel() {
        super("de/augustakom/hurrican/gui/auftrag/mvs/resources/AuftragMVSEnterprisePanel.xml");
        createGUI();
        GuiTools.enableComponents(getComponentsToEnable(), false, true);
        setDomainValidator(new DomainValidator(getMVSService()));
    }

    protected DomainValidator getDomainValidator() {
        return domainValidator;
    }

    protected final void setDomainValidator(DomainValidator domainValidator) {
        this.domainValidator = domainValidator;
    }

    protected boolean hasDomainNotChanged() {
        return (getMvsModel() != null) && getMvsModel().getDomain().equals(tfDomain.getText());
    }

    protected void validateDomain() throws HurricanGUIException {
        if (hasDomainNotChanged()) {
            return;
        }

        AKWarning warning;
        try {
            warning = getDomainValidator().validate(tfDomain.getText());
        }
        catch (FindException e) {
            throw new HurricanGUIException("Bei der Validierung ist ein Fehler aufgetreten!", e);
        }
        if (warning != null) {
            throw new HurricanGUIException(warning.getMessage());
        }
    }

    protected void validateEmail() throws HurricanGUIException {
        final String emailAddress = tfEmailAddress.getText(null);
        if (!EMailValidator.getInstance().isValid(emailAddress)) {
            throw new HurricanGUIException("Die angegebene EMail Adresse ist nicht gueltig!");
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() throws AKGUIException {
        try {
            GuiTools.cleanFields(this);
            if (getAuftragModel() == null) {
                return;
            }
            loadModel();
            if (getMvsModel() != null) {
                tfUsername.setText(getMvsModel().getUserName());
                tfPassword.setText(getMvsModel().getPassword());
                tfEmailAddress.setText(getMvsModel().getMail());
                tfDomain.setText(getMvsModel().getDomain());
            }
            enableOrDisableNewButtonAndFields();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void loadModel() throws FindException {
        AuftragMVSEnterprise tmpModel = getMVSService().findMvsEnterprise4Auftrag(getAuftragModel()
                .getAuftragId());
        setMvsModel(tmpModel);
    }

    /**
     * @throws AKGUIException
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        try {
            validateDomain();
            validateEmail();
            AuftragMVSEnterprise enterpriseModel = getMvsModel();
            if (enterpriseModel == null) {
                enterpriseModel = new AuftragMVSEnterprise();
            }
            enterpriseModel.setUserName(tfUsername.getText());
            enterpriseModel.setPassword(tfPassword.getText());
            enterpriseModel.setMail(tfEmailAddress.getText().trim());
            enterpriseModel.setDomain(tfDomain.getText().trim());
            enterpriseModel.setAuftragId(getAuftragModel().getAuftragId());
            getMVSService().saveAuftragMvs(enterpriseModel);
            loadModel();
            enableOrDisableNewButtonAndFields();

        }
        catch (StoreException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showWarningDialog(getMainFrame(), e.getMessage(), true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    @Override
    protected void createNewEntry() {
        super.createNewEntry();
        tfEmailAddress.setText(BLANK_STRING);
        tfDomain.setText(BLANK_STRING);
    }

    @Override
    protected void execute(String command) {
        // Neuer Datensatz Button?
        if (ID_NEW.equals(command)) {
            createNewEntry();
        }
        // Zeige bisher vergebene Domains
        else if (ID_DOMAINS.equals(command)) {
            Collection<String> domains = findAllUsedDomains();
            AuftragMVSDomainsDialog dialog = new AuftragMVSDomainsDialog("Domains", domains);
            dialog.setTitle("Alle bereits verwendeten Domains");
            DialogHelper.showDialog(getMainFrame(), dialog, false, true);
        }
    }

    Collection<String> findAllUsedDomains() {
        Collection<String> result = Collections.emptyList();
        try {
            result = getMVSService().findAllUsedDomains();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        return result;
    }

    @Override
    protected final Component[] getComponentsToEnable() {
        return new Component[] { tfDomain, tfEmailAddress };
    }

    @Override
    protected void addSpecificGUIElements(int yCounter) {
        final String emailAddressId = "emailAddress";
        AKJLabel lblEmail = getSwingFactory().createLabel(emailAddressId);
        tfEmailAddress = getSwingFactory().createTextField(emailAddressId);

        // @formatter:off
        getMvsDataPanel().add(lblEmail          , GBCFactory.createGBC(  0,  0, 1, yCounter, 1, 1, GridBagConstraints.HORIZONTAL));
        getMvsDataPanel().add(tfEmailAddress    , GBCFactory.createGBC(  0,  0, 3, yCounter, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        manageGUI(tfEmailAddress);

    }

}

