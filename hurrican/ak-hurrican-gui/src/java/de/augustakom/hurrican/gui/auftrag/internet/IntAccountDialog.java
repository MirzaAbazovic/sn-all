/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.10.2009 10:26:07
 */

package de.augustakom.hurrican.gui.auftrag.internet;

import java.awt.*;
import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.IntAccount;


/**
 * Dialog zur manuellen Eingabe eines IntAccounts
 *
 *
 */
public class IntAccountDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(IntAccountDialog.class);

    private static final String TITLE = "title";
    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/internet/resources/IntAccountDialog.xml";
    private static final String PASSWORD = "password";
    private static final String ACCOUNT = "account";

    private IntAccount intAccount;
    private IntAccount copyOfIntAccount;

    // GUI-Elemente
    private AKJTextField textFieldAccount;
    private AKJTextField textFieldPassword;


    /**
     * Konstruktor mit Angabe des zu editierenden IntAccount Objekts.
     *
     * @param intAccount
     */
    public IntAccountDialog(IntAccount intAccount) {
        super(RESOURCE);
        if (intAccount == null) {
            throw new IllegalArgumentException("Es wurde kein IntAccount-Objekt angegeben.");
        }

        this.intAccount = intAccount;
        this.copyOfIntAccount = new IntAccount();
        try {
            PropertyUtils.copyProperties(this.copyOfIntAccount, intAccount);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        createGUI();
        showValues();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText(TITLE));

        AKJLabel labelAccount = getSwingFactory().createLabel(ACCOUNT);
        textFieldAccount = getSwingFactory().createTextField(ACCOUNT);
        AKJLabel labelPassword = getSwingFactory().createLabel(PASSWORD);
        textFieldPassword = getSwingFactory().createTextField(PASSWORD);

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(labelAccount, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(textFieldAccount, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(labelPassword, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        child.add(textFieldPassword, GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    /* Zeigt die Daten des uebergebenen IntAccount-Objekts an. */
    private void showValues() {
        GuiTools.cleanFields(getChildPanel());
        if (copyOfIntAccount != null) {
            textFieldAccount.setText(copyOfIntAccount.getAccount());
            textFieldPassword.setText(copyOfIntAccount.getPasswort());
        }
    }

    private void setValues() {
        copyOfIntAccount.setAccount(textFieldAccount.getText());
        copyOfIntAccount.setPasswort(textFieldPassword.getText());
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            setValues();
            PropertyUtils.copyProperties(intAccount, copyOfIntAccount);

            if (StringUtils.isBlank(intAccount.getAccount())) {
                throw new HurricanGUIException("Es ist kein Account-Name angegeben!");
            }
            AKUser user = HurricanSystemRegistry.instance().getCurrentUser();
            intAccount.setBearbeiter((user != null) ? user.getLoginName() : "unknown");
            prepare4Close();
            setValue(intAccount);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
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
        // do nothing
    }
}
