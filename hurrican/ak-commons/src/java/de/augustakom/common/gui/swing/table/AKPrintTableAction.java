/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.04.2005 10:13:43
 */
package de.augustakom.common.gui.swing.table;

import java.awt.event.*;
import java.text.*;
import javax.print.attribute.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJTable;


/**
 * Action, um eine Tabelle zu drucken
 *
 *
 */
public class AKPrintTableAction extends AKAbstractAction {

    private static final Logger LOGGER = Logger.getLogger(AKPrintTableAction.class);

    private AKJTable table = null;

    private PrintRequestAttributeSet printReqAttribSet = null;
    private JTable.PrintMode printMode = JTable.PrintMode.NORMAL;
    private MessageFormat mfHeader = null;
    private MessageFormat mfFooter = null;

    /**
     * Konstruktor mit Angabe der Tabelle, die von der Action gedruckt werden soll.
     *
     * @param table
     * @param name  Name der Action (wird als Text in Menus verwendet)
     */
    public AKPrintTableAction(AKJTable table, String name) {
        super();
        this.table = table;
        setName((StringUtils.isNotBlank(name)) ? name : "Tabelle drucken");
        setActionCommand("print.table:" + getClass().getName());
    }

    /**
     * Uebergibt der Action den zu verwendenden PrintMode (NORMAL oder FIT_WIDTH)
     *
     * @param printMode
     */
    public void setPrintMode(JTable.PrintMode printMode) {
        this.printMode = printMode;
    }

    /**
     * Uebergibt der Action ein Attribute-Set mit Print-Parametern.
     *
     * @param attributeSet
     */
    public void setPrintRequestAttributeSet(PrintRequestAttributeSet attributeSet) {
        this.printReqAttribSet = attributeSet;
    }

    /**
     * Uebergibt einen Header-Text, der auf jeder Seite gedruckt werden soll.
     *
     * @param header
     */
    public void setPageHeader(String header) {
        this.mfHeader = new MessageFormat(header);
    }

    /**
     * Uebergibt einen Footer-Text, der auf jeder Seite gedruckt werden soll.
     *
     * @param footer
     */
    public void setPageFooter(String footer) {
        this.mfFooter = new MessageFormat(footer);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            table.print(printMode, mfHeader, mfFooter, true, printReqAttribSet, true);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}


