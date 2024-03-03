/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2009 16:24:43
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;


/**
 * Dialog zur Auswahl eines Benutzers.
 *
 *
 */
public class SelectUserDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(SelectUserDialog.class);

    private AKJComboBox cbUsers = null;

    private Long abtId = null;

    /**
     * Konstruktor mit Angabe der Abteilungs-ID, aus der die User selektiert werden sollen.
     *
     * @param abtId
     */
    public SelectUserDialog(Long abtId) {
        super("de/augustakom/hurrican/gui/shared/resources/SelectUserDialog.xml");
        this.abtId = abtId;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblUsers = getSwingFactory().createLabel("users");
        cbUsers = getSwingFactory().createComboBox("users",
                new AKCustomListCellRenderer<>(AKUser.class, AKUser::getNameAndFirstName));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(lblUsers, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(cbUsers, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.BOTH));
    }

    @Override
    public final void loadData() {
        try {
            AKUserService us = getAuthenticationService(
                    AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
            List<AKUser> users = us.findByHurricanAbteilungId(abtId);
            CollectionUtils.filter(users, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    if ((object instanceof AKUser) && ((AKUser) object).isActive()) {
                        return true;
                    }
                    return false;
                }
            });
            cbUsers.addItems(users, true, AKUser.class, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void doSave() {
        try {
            Object retVal = cbUsers.getSelectedItem();
            if (retVal instanceof AKUser) {
                AKUser user = (AKUser) retVal;
                if (user.getId() != null) {
                    prepare4Close();
                    setValue(user);
                }
                else {
                    throw new HurricanGUIException("Bitte einen User auswaehlen!");
                }
            }
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}
