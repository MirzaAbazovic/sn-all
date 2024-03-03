/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.03.2007 08:29:12
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.tools.collections.CollectionTools;


/**
 * Dialog zur Darstellung und Auswahl einer Liste von Objekten.
 *
 *
 */
public class AKSelectReferenceDialog extends AKJAbstractOptionDialog implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(AKSelectReferenceDialog.class);

    private Collection<?> references = null;
    private Class<?> refType = null;
    private String idProperty = null;
    private String nameProperty = null;

    private AKJTable tbReferences = null;

    /**
     * Konstruktor mit Angabe der auszuwaehlenden Referenzen.
     *
     * @param refs
     * @param refType
     * @param idProperty
     * @param nameProperty
     */
    protected AKSelectReferenceDialog(Collection<?> refs, Class<?> refType, String idProperty, String nameProperty) {
        super("de/augustakom/common/gui/resources/AKSelectReferenceDialog.xml");
        this.references = refs;
        this.refType = refType;
        this.idProperty = idProperty;
        this.nameProperty = nameProperty;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(refType.getSimpleName());
        setIconURL("de/augustakom/common/gui/images/referencelist.gif");

        ButtonActionListener al = new ButtonActionListener();
        AKJButton btnSelect = getSwingFactory().createButton("select", al);
        AKJButton btnCancel = getSwingFactory().createButton("cancel", al);

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnSelect, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnCancel, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.NONE));

        Class<?>[] types = new Class[2];
        try {
            if (CollectionTools.isNotEmpty(references)) {
                Object obj = references.iterator().next();
                types[0] = PropertyUtils.getPropertyType(obj, idProperty);
                types[1] = PropertyUtils.getPropertyType(obj, nameProperty);
            }
            else {
                types[0] = refType.getField(idProperty).getType();
                types[1] = refType.getField(nameProperty).getType();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            types[0] = String.class;
            types[1] = String.class;
        }

        AKMutableTableModel tbMdlReferences = new AKReflectionTableModel(
                new String[] { idProperty, nameProperty }, new String[] { idProperty, nameProperty },
                new Class[] { });
        tbMdlReferences.setData(references);

        tbReferences = new AKJTable(tbMdlReferences, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbReferences.attachSorter();
        tbReferences.fitTable(380, new double[] { 25d, 55d });
        tbReferences.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spRefs = new AKJScrollPane(tbReferences);

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spRefs, BorderLayout.CENTER);
        getChildPanel().add(btnPanel, BorderLayout.SOUTH);

        this.setPreferredSize(new Dimension(400, 250));
    }

    /*
     * Wird von dem Select-Button aufgerufen und speichert die
     * aktuell selektierte Referenz.
     */
    private void selectReference() {
        int selRow = tbReferences.getSelectedRow();
        Object selection = ((AKMutableTableModel) tbReferences.getModel()).getDataAtRow(selRow);

        objectSelected(selection);
    }

    /*
     * Wird von dem Abbrechen-Button aufgerufen - schliesst den Dialog.
     */
    private void cancel() {
        prepare4Close();
        setValue(null);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        prepare4Close();
        setValue(selection);
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
    public void update(Observable arg0, Object arg1) {
    }

    /**
     * ActionListener fuer die internen Buttons.
     */
    class ButtonActionListener implements ActionListener {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if ("select".equals(e.getActionCommand())) {
                selectReference();
            }
            else if ("cancel".equals(e.getActionCommand())) {
                cancel();
            }
        }
    }
}


