/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.05.2005 13:29:02
 */
package de.augustakom.hurrican.gui.kubena;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.kubena.Kubena;
import de.augustakom.hurrican.model.cc.kubena.KubenaVbz;
import de.augustakom.hurrican.service.cc.KubenaService;


/**
 * Panel zur Eingabe bzw. Auswahl der VBZs, die in eine Kubena einbezogen werden sollen.
 *
 *
 */
public class KubenaVbzSelectionPanel extends AbstractServicePanel implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(KubenaVbzSelectionPanel.class);

    private AKReflectionTableModel<KubenaVbz> tbMdlVbz = null;

    private Kubena kubena = null;

    /**
     * Konstruktor mit Angabe der Kubena.
     *
     * @param kubena
     */
    public KubenaVbzSelectionPanel(Kubena kubena) {
        super("de/augustakom/hurrican/gui/kubena/resources/KubenaVbzSelectionPanel.xml");
        this.kubena = kubena;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblVbz = getSwingFactory().createLabel("vbz");
        AKJButton btnAdd = getSwingFactory().createButton("add", getActionListener());
        AKJButton btnImport = getSwingFactory().createButton("import", getActionListener());

        tbMdlVbz = new AKReflectionTableModel<KubenaVbz>(
                new String[] { VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Input", "vorhanden" },
                new String[] { "vbz", "input", "vorhanden" },
                new Class[] { String.class, String.class, Boolean.class });
        AKJTable tbVbz = new AKJTable(tbMdlVbz, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbVbz.attachSorter();
        tbVbz.fitTable(new int[] { 150, 40, 55 });
        AKJScrollPane spVbz = new AKJScrollPane(tbVbz, new Dimension(270, 200));

        this.setLayout(new GridBagLayout());
        this.add(lblVbz, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(spVbz, GBCFactory.createGBC(0, 100, 0, 1, 1, 3, GridBagConstraints.VERTICAL));
        this.add(btnAdd, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(btnImport, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 3, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();

            KubenaService ks = getCCService(KubenaService.class);
            List<KubenaVbz> kubenaVbzs = ks.findKubenaVbz(kubena.getId());
            tbMdlVbz.setData(kubenaVbzs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("add".equals(command)) {
            addVbz();
        }
        else if ("import".equals(command)) {
            importFromXls();
        }
    }

    /* Abfrage einer VerbindungsBezeichnung, die der Kubena zugeordnet werden soll. */
    private void addVbz() {
        String vbz = MessageHelper.showInputDialog(this, "Geben Sie bitte die Verbindungsbezeichnung für die Kubena ein:",
                "Verbindungsbezeichnung für Kubena", JOptionPane.PLAIN_MESSAGE);
        if ((vbz != null) && StringUtils.isNotBlank(vbz)) {
            try {
                KubenaService ks = getCCService(KubenaService.class);
                List<KubenaVbz> kVbzs = ks.addVbz2Kubena(kubena.getId(), vbz, KubenaVbz.INPUT_TYPE_MANUELL);
                if (kVbzs != null) {
                    for (KubenaVbz kVbz : kVbzs) {
                        tbMdlVbz.addObject(kVbz);
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    /*
     * Importiert die VBZs aus einem XLS-File (Excel).
     * Das File sowie das zu verwendende Worksheet, die Zeilen
     * und Spalten werden ueber einen Dialog abgefragt. Der Wizard
     * ist auch dafuer verantwortlich, die VBZs schliesslich aus dem
     * File zu lesen.
     */
    private void importFromXls() {
        KubenaVbzXlsChooser xlsChooser = new KubenaVbzXlsChooser(kubena);
        Object result = DialogHelper.showDialog(this, xlsChooser, true, true);
        if ((result instanceof Integer) && NumberTools.equal((Integer) result, KubenaVbzXlsChooser.OK_OPTION)) {
            loadData();
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


