/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2006 07:42:28
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;

import de.augustakom.common.gui.awt.GBCFactory;


/**
 * Dialog, um aus einer Liste (ComboBox) einen Wert auszuwaehlen. Im Gegensatz zu einer "normalen" JOptionPane kann
 * diesem Dialog ein Renderer fuer die ComboBox mit uebergeben werden.
 *
 *
 */
public class AKJSelectionDialog extends AKJAbstractOptionDialog {

    private List<?> values = null;
    private ListCellRenderer renderer = null;
    private String dialogTitle = null;
    private String message = null;
    private String comboBoxName = null;

    private AKJComboBox cbValues = null;

    /**
     * Konstruktor mit Angabe der notwendigen Parameter fuer den Dialog.
     *
     * @param values       Liste mit den darzustellenden Objekten
     * @param renderer     Renderer fuer die ComboBox
     * @param dialogTitle  Dialog-Titel
     * @param msg          Message, die ueber der ComboBox dargestellt werden soll
     * @param comboBoxName Bezeichnung/Beschriftung der ComboBox
     */
    public AKJSelectionDialog(List<?> values, ListCellRenderer renderer,
            String dialogTitle, String msg, String comboBoxName) {
        super("de/augustakom/common/gui/resources/AKJSelectionDialog.xml");
        this.values = values;
        this.renderer = renderer;
        this.dialogTitle = dialogTitle;
        this.message = msg;
        this.comboBoxName = comboBoxName;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    protected final void createGUI() {
        setTitle(dialogTitle);

        AKJButton btnOk = getSwingFactory().createButton("ok", getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton("cancel", getActionListener());

        cbValues = new AKJComboBox(renderer);
        cbValues.addItems(values, true);

        AKJLabel lblMsg = new AKJLabel(message);
        AKJLabel lblName = new AKJLabel(comboBoxName);

        AKJPanel main = new AKJPanel(new GridBagLayout());
        main.add(lblMsg, GBCFactory.createGBC(100, 0, 0, 0, 2, 1, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 2, 2)));
        main.add(lblName, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        main.add(cbValues, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        main.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, 2, 1, 1, GridBagConstraints.BOTH));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnOk, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnCancel, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(main, BorderLayout.CENTER);
        getChildPanel().add(btnPnl, BorderLayout.SOUTH);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    protected void execute(String command) {
        if ("ok".equals(command)) {
            prepare4Close();
            setValue(cbValues.getSelectedItem());
        }
        else if ("cancel".equals(command)) {
            prepare4Close();
            setValue(null);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

}


