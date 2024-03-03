/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2005 09:02:55
 */
package de.augustakom.hurrican.gui.kubena;


import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.file.FileFilterExtension;
import de.augustakom.common.tools.poi.HSSFSheetTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.kubena.Kubena;
import de.augustakom.hurrican.model.cc.kubena.KubenaVbz;
import de.augustakom.hurrican.service.cc.KubenaService;


/**
 * Dialog zur Auswahl eines Xls-Files, aus dem die VBZs fuer die Kubena eingelesen werden sollen. <br> In dem Dialog
 * wird das Xls-File dargestellt. Der User kann die VBZs selektieren und dadurch der Kubena zuordnen.
 *
 *
 */
public class KubenaVbzXlsChooser extends AbstractServiceOptionDialog implements ListSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(KubenaVbzXlsChooser.class);
    private static final long serialVersionUID = -546887006483663535L;

    private Kubena kubena = null;
    private File xlsFile = null;
    private HSSFWorkbook workbook = null;

    private AKJTextField tfFile = null;
    private AKJList lsWorksheets = null;
    private DefaultListModel lsMdlWorksheets = null;

    private AKJTable tbSheet = null;
    private HSSFSheetTableModel tbMdlSheet = null;

    /**
     * Konstruktor mit Angabe der Kubena.
     *
     * @param kubena
     */
    public KubenaVbzXlsChooser(Kubena kubena) {
        super("de/augustakom/hurrican/gui/kubena/resources/KubenaVbzXlsChooser.xml");
        this.kubena = kubena;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, "Auslesen", "Liest die Verbindungsbezeichnungen aus dem angegebenen File aus", true, true);

        AKJLabel lblFile = getSwingFactory().createLabel("file");
        AKJLabel lblWorksheets = getSwingFactory().createLabel("worksheets");

        tfFile = getSwingFactory().createTextField("file", false);
        AKJButton btnFile = getSwingFactory().createButton("file.chooser", getActionListener());
        lsMdlWorksheets = new DefaultListModel();
        lsWorksheets = getSwingFactory().createList("worksheets", lsMdlWorksheets);
        lsWorksheets.setEnabled(false);
        lsWorksheets.addListSelectionListener(this);
        AKJScrollPane spWorksheets = new AKJScrollPane(lsWorksheets, new Dimension(180, 60));

        tbSheet = new AKJTable();
        tbSheet.setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        tbSheet.setCellSelectionEnabled(true);
        tbSheet.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        AKJScrollPane spSheet = new AKJScrollPane(tbSheet, new Dimension(200, 250));

        AKJPanel sub = new AKJPanel(new GridBagLayout());
        sub.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        sub.add(lblFile, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        sub.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        sub.add(tfFile, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        sub.add(btnFile, GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.NONE));
        sub.add(lblWorksheets, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        sub.add(spWorksheets, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(sub, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(spSheet, GBCFactory.createGBC(100, 100, 0, 1, 2, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
        // intentionally left blank
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            int[] columns = tbSheet.getSelectedColumns();
            int[] rows = tbSheet.getSelectedRows();

            if ((columns == null) || (columns.length == 0) || (rows == null) || (rows.length == 0)) {
                throw new HurricanGUIException("Bitte selektieren Sie die Spalten mit den Verbindungsbezeichnungen.");
            }

            List<String> vbzs = new ArrayList<>();
            for (int c = 0; c < columns.length; c++) {
                for (int r = 0; r < rows.length; r++) {
                    Object value = tbMdlSheet.getValueAt(rows[r], columns[c]);
                    if (value instanceof String) {
                        vbzs.add((String) value);
                    }
                }
            }

            Map<String, Exception> errors = createKubenaVbzs(vbzs);

            if (!errors.isEmpty()) {
                StringBuilder msg = new StringBuilder();
                msg.append("Bei der Zuordnung der Verbindungsbezeichnungen zur Kubena traten Fehler auf. Betroffene Verbindungsbezeichnungen:");
                Iterator<String> keyIt = errors.keySet().iterator();
                while (keyIt.hasNext()) {
                    msg.append(SystemUtils.LINE_SEPARATOR);
                    msg.append(keyIt.next());
                }
                msg.append(SystemUtils.LINE_SEPARATOR);
                msg.append(SystemUtils.LINE_SEPARATOR);
                msg.append("Mögliche Gründe: ");
                msg.append(SystemUtils.LINE_SEPARATOR);
                msg.append("Die Verbindungsbezeichnungen sind der Kubena bereits zugeordnet oder die Verbindungsbezeichnungen" +
                        " konnten nicht ermittelt werden.");

                MessageHelper.showInfoDialog(getMainFrame(), msg.toString());
            }

            prepare4Close();
            setValue(OK_OPTION);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private Map<String, Exception> createKubenaVbzs(List<String> vbzs) throws ServiceNotFoundException {
        Map<String, Exception> errors = new HashMap<>();
        KubenaService ks = getCCService(KubenaService.class);
        for (String vbz : vbzs) {
            try {
                List<KubenaVbz> kVbzs = ks.addVbz2Kubena(kubena.getId(), vbz, KubenaVbz.INPUT_TYPE_AUTO);
                if ((kVbzs == null) || (kVbzs.isEmpty())) {
                    throw new HurricanGUIException("KubenaVbz wurde nicht angelegt!");
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                errors.put(vbz, e);
            }
        }
        return errors;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("file.chooser".equals(command)) {
            selectXlsFile();
        }
    }

    /* Oeffnet einen File-Chooser und liest das angegebene Xls-File aus. */
    private void selectXlsFile() {
        try {
            xlsFile = null;

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.addChoosableFileFilter(new FileFilterExtension(FileFilterExtension.FILE_EXTENSION_EXCEL));

            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                xlsFile = fileChooser.getSelectedFile();
                if (xlsFile != null) {
                    tfFile.setText(xlsFile.getAbsolutePath());
                    readXlsFile();
                }
                else {
                    throw new HurricanGUIException("Die gewählte Datei ist ungültig!");
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Liest das Excel-File aus. */
    private void readXlsFile() {
        clearFields();
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
            workbook = new HSSFWorkbook(fs, false);
            if (workbook.getNumberOfSheets() > 0) {
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    String sheetName = workbook.getSheetName(i);
                    lsMdlWorksheets.addElement(sheetName);
                }
            }
            else {
                throw new HurricanGUIException("Excel-File enthält keine Arbeitsblätter!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            validateWorksheets();
        }
    }

    /* Setzt die Liste mit den Worksheets auf enabled/disabled - abhaengig vom Inhalt. */
    private void validateWorksheets() {
        lsWorksheets.setEnabled((lsMdlWorksheets.getSize() > 0));
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        Object[] selection = lsWorksheets.getSelectedValues();
        if ((selection != null) && (selection.length == 1)) {
            HSSFSheet sheet = workbook.getSheet((String) lsWorksheets.getSelectedValue());
            tbMdlSheet = new HSSFSheetTableModel(sheet);
            tbSheet.setModel(tbMdlSheet);
        }
    }

    /* 'Loescht' alle Felder. */
    private void clearFields() {
        lsMdlWorksheets.removeAllElements();
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }

}


