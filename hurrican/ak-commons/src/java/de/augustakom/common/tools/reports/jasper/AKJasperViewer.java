/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2004 10:48:12
 */
package de.augustakom.common.tools.reports.jasper;

import java.io.*;
import javax.swing.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.IconHelper;


/**
 * Ableitung von JasperViewer. <br> Durch die Ableitung wird dem Viewer ein anderer Titel und ein anderes Icon
 * uebergeben.
 *
 *
 */
public class AKJasperViewer extends JasperViewer {
    private static final Logger LOGGER = Logger.getLogger(AKJasperViewer.class);

    /**
     * @param is
     * @param isXMLFile
     * @throws net.sf.jasperreports.engine.JRException
     */
    public AKJasperViewer(InputStream is, boolean isXMLFile) throws JRException {
        super(is, isXMLFile);
        initAK();
    }

    /**
     * @param is
     * @param isXMLFile
     * @param isExitOnClose
     * @throws net.sf.jasperreports.engine.JRException
     */
    public AKJasperViewer(InputStream is, boolean isXMLFile, boolean isExitOnClose) throws JRException {
        super(is, isXMLFile, isExitOnClose);
        initAK();
    }

    /**
     * @param sourceFile
     * @param isXMLFile
     * @throws net.sf.jasperreports.engine.JRException
     */
    public AKJasperViewer(String sourceFile, boolean isXMLFile) throws JRException {
        super(sourceFile, isXMLFile);
        initAK();
    }

    /**
     * @param sourceFile
     * @param isXMLFile
     * @param isExitOnClose
     * @throws net.sf.jasperreports.engine.JRException
     */
    public AKJasperViewer(String sourceFile, boolean isXMLFile, boolean isExitOnClose) throws JRException {
        super(sourceFile, isXMLFile, isExitOnClose);
        initAK();
    }

    /**
     * @param jasperPrint
     * @throws net.sf.jasperreports.engine.JRException
     */
    public AKJasperViewer(JasperPrint jasperPrint) throws JRException {
        super(jasperPrint);
        initAK();
    }

    /**
     * @param jasperPrint
     * @param isExitOnClose
     * @throws net.sf.jasperreports.engine.JRException
     */
    public AKJasperViewer(JasperPrint jasperPrint, boolean isExitOnClose) {
        super(jasperPrint, isExitOnClose);
        initAK();
    }

    /* Initialisiert den Viewer fuer AKOM. */
    private void initAK() {
        setTitle("Druckvorschau");
        try {
            IconHelper ih = new IconHelper();
            ImageIcon icon = ih.getIcon("de/augustakom/common/tools/reports/jasper/printer.gif");
            if ((icon != null) && (icon.getImage() != null)) {
                setIconImage(icon.getImage());
            }
        }
        catch (RuntimeException e) {
            LOGGER.warn("initAK() - caught exception - no action", e);
        }
    }

    /**
     * @see net.sf.jasperreports.view.JasperViewer#viewReport(java.lang.String, boolean)
     */
    public static void viewReport(String sourceFile, boolean isXMLFile) throws JRException {
        JasperViewer jasperViewer =
                new AKJasperViewer(sourceFile, isXMLFile, true);
        jasperViewer.setVisible(true);
    }

    /**
     * @see net.sf.jasperreports.view.JasperViewer#viewReport(java.io.InputStream, boolean)
     */
    public static void viewReport(InputStream is, boolean isXMLFile) throws JRException {
        JasperViewer jasperViewer =
                new AKJasperViewer(is, isXMLFile, true);
        jasperViewer.setVisible(true);
    }

    /**
     * @see net.sf.jasperreports.view.JasperViewer#viewReport(net.sf.jasperreports.engine.JasperPrint)
     */
    public static void viewReport(JasperPrint jasperPrint) {
        JasperViewer jasperViewer =
                new AKJasperViewer(jasperPrint, false);
        jasperViewer.setVisible(true);
    }

    /**
     * @see net.sf.jasperreports.view.JasperViewer#viewReport(java.lang.String, boolean, boolean)
     */
    public static void viewReport(String sourceFile, boolean isXMLFile, boolean isExitOnClose) throws JRException {
        JasperViewer jasperViewer =
                new AKJasperViewer(sourceFile, isXMLFile, isExitOnClose);
        jasperViewer.setVisible(true);
    }

    /**
     * @see net.sf.jasperreports.view.JasperViewer#viewReport(java.io.InputStream, boolean, boolean)
     */
    public static void viewReport(InputStream is, boolean isXMLFile, boolean isExitOnClose) throws JRException {
        JasperViewer jasperViewer =
                new AKJasperViewer(is, isXMLFile, isExitOnClose);
        jasperViewer.setVisible(true);
    }

    /**
     * @see net.sf.jasperreports.view.JasperViewer#viewReport(net.sf.jasperreports.engine.JasperPrint, boolean)
     */
    public static void viewReport(JasperPrint jasperPrint, boolean isExitOnClose) {
        JasperViewer jasperViewer =
                new AKJasperViewer(jasperPrint, isExitOnClose);
        jasperViewer.setVisible(true);
    }

}


