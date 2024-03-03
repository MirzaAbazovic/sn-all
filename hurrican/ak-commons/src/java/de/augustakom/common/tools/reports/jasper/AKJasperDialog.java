/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.2004 11:36:03
 */
package de.augustakom.common.tools.reports.jasper;

import java.awt.*;
import java.util.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRViewer;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractOptionDialog;
import de.augustakom.common.gui.swing.DialogHelper;


/**
 * Dialog, um JasperReports zu betrachten. <br> Die Implementierung verwendet fuer die Anzeige die Klasse
 * <code>net.sf.jasperreports.view.JRViewer</code>. <br> Wichtig: die Dialog-Implementierung bietet z.Z. nicht alle
 * Moeglichkeiten an, die JRViewer eigentlich bietet. Es sind nur die Aufrufe implementiert, die fuer AK-Projekte
 * sinnvoll erscheinen.
 *
 *
 */
public class AKJasperDialog extends AKJAbstractOptionDialog {

    private JRViewer viewer = null;
    private String title = null;
    private String iconURL = null;

    /**
     * Konstruktor mit Angabe des JasperPrint-Objekts
     *
     * @param jasperPrint
     * @throws JRException
     */
    AKJasperDialog(JasperPrint jasperPrint) throws JRException {
        super(null);
        this.viewer = new JRViewer(jasperPrint);
        createGUI();
    }

    /**
     * Konstruktor mit Angabe des JasperPrint-Objekts sowie dem Title-Text und der Icon-URL fuer den Dialog.
     *
     * @param jasperPrint
     * @param title
     * @param iconURL
     * @throws JRException
     */
    AKJasperDialog(JasperPrint jasperPrint, String title, String iconURL) throws JRException {
        super(null);
        this.title = title;
        this.iconURL = iconURL;
        this.viewer = new JRViewer(jasperPrint);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    protected final void createGUI() {
        setTitle((title != null) ? title : "Druckvorschau");
        setIconURL((iconURL != null) ? iconURL : "de/augustakom/common/tools/reports/jasper/printer.gif");

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(viewer, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.setPreferredSize(new Dimension(600, 400));
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * Stellt das JasperPrint-Objekt in einem Dialog dar.
     *
     * @param jasperPrint das anzuzeigende JasperPrint-Objekt
     * @throws JRException
     */
    public static void viewReport(JasperPrint jasperPrint) throws JRException {
        AKJasperDialog jasperViewer =
                new AKJasperDialog(jasperPrint);
        DialogHelper.showDialog(null, jasperViewer, true, true);
    }

    /**
     * Stellt das JasperPrint-Objekt in einem Dialog dar. <br> Ueber <code>owner</code> kann der 'Owner' des Dialogs
     * bestimmt werden.
     *
     * @param jasperPrint das anzuzeigende JasperPrint-Objekt
     * @param owner       Owner fuer den Dialog
     * @throws JRException
     */
    public static void viewReport(JasperPrint jasperPrint, Component owner) throws JRException {
        AKJasperDialog jasperViewer =
                new AKJasperDialog(jasperPrint);
        DialogHelper.showDialog(owner, jasperViewer, true, true);
    }

    /**
     * Stellt das JasperPrint-Objekt in einem Dialog dar. <br> Ueber <code>owner</code> kann der 'Owner' des Dialogs
     * bestimmt werden. <br> Ueber <code>title</code> kann die Titelleiste des Dialogs angepasst werden.
     *
     * @param jasperPrint das anzuzeigende JasperPrint-Objekt
     * @param owner       Owner fuer den Dialog
     * @param title       Titel fuer den Dialog
     * @throws JRException
     */
    public static void viewReport(JasperPrint jasperPrint, Component owner, String title) throws JRException {
        AKJasperDialog jasperViewer =
                new AKJasperDialog(jasperPrint, title, null);
        DialogHelper.showDialog(owner, jasperViewer, true, true);
    }

    /**
     * Stellt das JasperPrint-Objekt in einem Dialog dar. <br> Ueber <code>owner</code> kann der 'Owner' des Dialogs
     * bestimmt werden. <br> Ueber <code>title</code> und <code>iconURL</code> kann die Titelleiste des Dialogs
     * angepasst werden.
     *
     * @param jasperPrint das anzuzeigende JasperPrint-Objekt
     * @param owner       Owner fuer den Dialog
     * @param title       Titel fuer den Dialog
     * @param iconURL     URL des Icons fuer den Dialog
     * @throws JRException
     */
    public static void viewReport(JasperPrint jasperPrint, Component owner, String title, String iconURL) throws JRException {
        AKJasperDialog jasperViewer =
                new AKJasperDialog(jasperPrint, title, iconURL);
        DialogHelper.showDialog(owner, jasperViewer, true, true);
    }

}


