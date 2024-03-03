/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.05.2004 14:55:24
 */
package de.augustakom.authentication.gui.user;

import java.awt.*;
import java.beans.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;


/**
 * Frame fuer die Darstellung einer Benutzer-Liste.
 *
 *
 */
public class UserListFrame extends AKJAbstractInternalFrame {

    private static final Logger LOGGER = Logger.getLogger(UserListFrame.class);

    private static final String ACTION_OK = "ok";

    private Object loaded = null;
    private List<AKUser> model = null;

    /**
     * Konstruktor mit Angabe der anzuzeigenden AKUser-Objekte und des Objekts, ueber das die User geladen wurden (z.Z.
     * nur AKRole oder AKAccount).
     *
     * @param loaded AKRole- oder AKAccount Objekt, das den Usern zugeordnet ist
     * @param users  List mit AKUser-Objekten
     */
    public UserListFrame(Object loaded, List<AKUser> users) {
        super("de/augustakom/authentication/gui/user/resources/UserListFrame.xml");
        this.loaded = loaded;
        this.model = users;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected void createGUI() {
        String title = getTitle();
        if (loaded instanceof AKRole) {
            AKRole role = (AKRole) loaded;
            title = getSwingFactory().getText("title.role") + role.getName();
        }
        else if (loaded instanceof AKAccount) {
            AKAccount acc = (AKAccount) loaded;
            title = getSwingFactory().getText("title.account") + acc.getName();
        }

        title += " (" + model.size() + ")";

        setTitle(title);

        AKJButton btnOk = getSwingFactory().createButton(ACTION_OK, getActionListener());
        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(new JPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnOk, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(new JPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        UserListPanel userListPanel = new UserListPanel(model);

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.setLayout(new GridBagLayout());
        child.add(userListPanel, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        child.add(btnPanel, GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(child, BorderLayout.CENTER);
        pack();
    }

    @Override
    protected void execute(String command) {
        if (ACTION_OK.equals(command)) {
            try {
                setClosed(true);
            }
            catch (PropertyVetoException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}
