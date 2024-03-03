/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.07.2012 17:33:16
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKDepartmentService;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.iface.AKSearchComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKSearchKeyListener;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableSingleClickMouseListener;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;

/**
 *
 */
public class FindUserDialog extends AbstractServiceOptionDialog implements AKSearchComponent, AKObjectSelectionListener {
    private static final Logger LOGGER = Logger.getLogger(FindUserDialog.class);

    private Map<Object, Object> departmentMap;
    private AKUserService userService;
    private AKJTextField tfName;
    private AKJTextField tfFirstname;
    private AKJTextField tfLoginname;

    private AKJTable userTable;

    private AKUser selected;

    public FindUserDialog() {
        super("de/augustakom/hurrican/gui/shared/resources/FindUserDialog.xml");
        loadData();
        createGUI();
    }

    private void loadData() {
        try {
            userService = getAuthenticationService(AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
            AKDepartmentService depService = getAuthenticationService(AKAuthenticationServiceNames.DEPARTMENT_SERVICE,
                    AKDepartmentService.class);
            List<AKDepartment> allDeps = depService.findAll();
            departmentMap = CollectionMapConverter.convert2Map(allDeps, "getId", null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, "Auswählen", "Wählt den markierten Datensatz aus und schliesst den Dialog", true,
                true);
        AKJLabel lblName = getSwingFactory().createLabel(AKUser.NAME);
        AKJLabel lblFirstname = getSwingFactory().createLabel(AKUser.FIRST_NAME);
        AKJLabel lblLoginname = getSwingFactory().createLabel(AKUser.LOGIN_NAME);

        AKSearchKeyListener searchKeyListener = new AKSearchKeyListener(this, new int[] { KeyEvent.VK_ENTER });
        tfName = getSwingFactory().createTextField(AKUser.NAME, true, true, searchKeyListener);
        tfFirstname = getSwingFactory().createTextField(AKUser.FIRST_NAME, true, true, searchKeyListener);
        tfLoginname = getSwingFactory().createTextField(AKUser.LOGIN_NAME, true, true, searchKeyListener);

        AKReferenceAwareTableModel<AKUser> userModel = new AKReferenceAwareTableModel<AKUser>(
                new String[] { "Login", "Name", "Vorname", "Abteilung" },
                new String[] { AKUser.LOGIN_NAME, AKUser.NAME, AKUser.FIRST_NAME, AKUser.DEPARTMENT_ID },
                new Class<?>[] { String.class, String.class, String.class, String.class });
        userModel.addReference(3, departmentMap, "name");
        userTable = new AKJTable(userModel, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        userTable.attachSorter();
        userTable.fitTable(new int[] { 100, 100, 100, 100 });
        userTable.addMouseListener(new AKTableSingleClickMouseListener(this));
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    doSave();
                }
            }
        });
        AKJScrollPane spUsers = new AKJScrollPane(userTable, new Dimension(420, 300));
        AKJButton btnSearch = getSwingFactory().createButton("search", getActionListener());

        // @formatter:off
        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(lblName         , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfName          , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblFirstname    , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfFirstname     , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblLoginname    , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfLoginname     , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(btnSearch       , GBCFactory.createGBC(  0,  0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(spUsers         , GBCFactory.createGBC(100,  0, 0, 3, 5, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void objectSelected(Object selection) {
        selected = (AKUser) selection;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void doSearch() {
        Map<String, Object> searchParams = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(tfName.getText())) {
            searchParams.put(AKUser.NAME, tfName.getText());
        }
        if (StringUtils.isNotBlank(tfFirstname.getText())) {
            searchParams.put(AKUser.FIRST_NAME, tfFirstname.getText());
        }
        if (StringUtils.isNotBlank(tfLoginname.getText())) {
            searchParams.put(AKUser.LOGIN_NAME, tfLoginname.getText());
        }
        searchParams.put(AKUser.ACTIVE, Boolean.TRUE);
        try {
            List<AKUser> users = userService.findByCriteria(searchParams);
            ((AKMutableTableModel) userTable.getModel()).setData(users);
        }
        catch (AKAuthenticationException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

    }

    @Override
    protected void doSave() {
        prepare4Close();
        setValue(selected);
    }

    @Override
    protected void execute(String command) {
        doSearch();
    }

}


