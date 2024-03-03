/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2009 14:38:45
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EndgeraetAcl;
import de.augustakom.hurrican.service.cc.EndgeraeteService;


/**
 *
 */
public class EndgeraetAclDialog extends AbstractServiceOptionDialog
        implements AKDataLoaderComponent, AKObjectSelectionListener {
    private static final Logger LOGGER = Logger.getLogger(EndgeraetAclDialog.class);

    private EGConfig egConfig;
    private AKReflectionTableModel<EndgeraetAcl> tbModelAcls;
    private AKJTable tbAcls;
    private List<EndgeraetAcl> endgeraetAcls;

    private EndgeraeteService endgeraeteService;

    /**
     * Konstruktor mit Angabe des zu editierenden EGConfig Objekts.
     *
     * @param egConfig
     * @throws ServiceNotFoundException
     */
    public EndgeraetAclDialog(EGConfig egConfig) {
        super("de/augustakom/hurrican/gui/auftrag/resources/EndgeraetAclDialog.xml", RESOURCE_WITH_OK_BTN);
        if (egConfig == null) {
            throw new IllegalArgumentException("Es wurde kein EGConfig-Objekt angegeben.");
        }

        try {
            endgeraeteService = getCCService(EndgeraeteService.class);
            endgeraetAcls = endgeraeteService.findAllEndgeraetAcls();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.egConfig = egConfig;

        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        tbModelAcls = new AKReflectionTableModel<EndgeraetAcl>(
                new String[] { "Name", "Router Typ" },
                new String[] { "name", "routerTyp" },
                new Class[] { String.class, String.class, String.class });
        tbAcls = new AKJTable(tbModelAcls,
                AKJTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbAcls.attachSorter();
        tbAcls.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJButton btnNewAcl = getSwingFactory().createButton("new.acl", getActionListener(), null);
        AKJButton btnDelAcl = getSwingFactory().createButton("del.acl", getActionListener(), null);

        AKJScrollPane spAcls = new AKJScrollPane(tbAcls, new Dimension(700, 150));
        child.add(btnNewAcl, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(btnDelAcl, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        child.add(spAcls, GBCFactory.createGBC(100, 100, 1, 0, 1, 3, GridBagConstraints.BOTH));

        manageGUI(new AKManageableComponent[] { btnDelAcl, btnNewAcl });
    }

    /* @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData() */
    @Override
    public final void loadData() {
        // Default-Daten fuer den Dialog laden
        try {
            endgeraetAcls = endgeraeteService.findAllEndgeraetAcls();
            tbModelAcls.setData(endgeraetAcls);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* Speichert die eingetragenen Daten in das PortForwarding-Objekt. */
    private void setValues() {
        int[] selectedRows = tbAcls.getSelectedRows();
        for (int row : selectedRows) {
            egConfig.addEndgeraetAcl((EndgeraetAcl)
                    ((AKMutableTableModel) tbAcls.getModel()).getDataAtRow(row));
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            setValues();

            prepare4Close();
            setValue(egConfig.getEndgeraetAcls());
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
        if ("new.acl".equals(command)) {
            createEndgeraetAcl();
        }
        else if ("del.acl".equals(command)) {
            deleteEndgeraetAcl();
        }
    }

    /**
     *
     */
    private void deleteEndgeraetAcl() {
        try {
            int[] selectedRows = tbAcls.getSelectedRows();
            List<EndgeraetAcl> selected = new ArrayList<>();
            for (int row : selectedRows) {
                selected.add((EndgeraetAcl)
                        ((AKMutableTableModel) tbAcls.getModel()).getDataAtRow(row));
            }

            if (CollectionTools.isEmpty(selected)) {
                throw new HurricanGUIException("Bitte wählen Sie zuerst einen Datensatz aus.");
            }
            final int opt = MessageHelper.showYesNoQuestion(this,
                    "Sollen die " + selected.size() + " ACLs wirklich entfernt werden?", "Löschen?");

            if (opt == JOptionPane.YES_OPTION) {
                for (EndgeraetAcl acl : selected) {
                    endgeraeteService.deleteEndgeraetAcl(acl);
                }
                loadData();
                tbAcls.updateUI();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    private void createEndgeraetAcl() {
        EndgeraetAcl endgeraetAcl = new EndgeraetAcl();
        EndgeraetAclEditDialog dlg = new EndgeraetAclEditDialog(endgeraetAcl);
        DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        loadData();
        tbAcls.updateUI();
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof EndgeraetAcl) {
            editEndgeraetAcl(tbAcls.getSelectedRow(), (EndgeraetAcl) selection);
        }
    }

    private void editEndgeraetAcl(int selectedRow, EndgeraetAcl selection) {
        EndgeraetAclEditDialog dlg = new EndgeraetAclEditDialog(selection);
        DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        loadData();
        tbAcls.updateUI();
    }

}
