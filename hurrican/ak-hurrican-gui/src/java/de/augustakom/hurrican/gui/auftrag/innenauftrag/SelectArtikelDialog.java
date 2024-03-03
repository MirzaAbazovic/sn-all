/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.2007 11:14:31
 */
package de.augustakom.hurrican.gui.auftrag.innenauftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterial;
import de.augustakom.hurrican.service.cc.InnenauftragService;


/**
 * Dialog, um die Materialliste anzuzeigen und ein Material auszuwählen.
 *
 *
 */
public class SelectArtikelDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent,
        AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(SelectArtikelDialog.class);

    private AKJTable tbArtikel = null;
    private AKReflectionTableModel<IAMaterial> tbMdlArtikel = null;

    /**
     * Default-Const.
     */
    public SelectArtikelDialog() {
        super(null);
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Artikelliste");
        configureButton(CMD_SAVE, "Übernehmen", null, true, true);

        tbMdlArtikel = new AKReflectionTableModel<IAMaterial>(
                new String[] { "Artikel", "Text", "Material-Nr", "Einzelpreis" },
                new String[] { "artikel", "text", "materialNr", "einzelpreis" },
                new Class[] { String.class, String.class, String.class, Float.class });
        tbArtikel = new AKJTable(tbMdlArtikel, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbArtikel.attachSorter();
        tbArtikel.fitTable(new int[] { 130, 200, 100, 100 });
        tbArtikel.addMouseListener(new AKTableDoubleClickMouseListener(this));

        AKJScrollPane sp = new AKJScrollPane(tbArtikel, new Dimension(550, 200));
        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(sp, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
        // do nothing
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        setWaitCursor();
        final SwingWorker<List<IAMaterial>, Void> worker = new SwingWorker<List<IAMaterial>, Void>() {

            @Override
            protected List<IAMaterial> doInBackground() throws Exception {
                InnenauftragService ias = getCCService(InnenauftragService.class);
                List<IAMaterial> materials = ias.findIAMaterialien();
                return materials;
            }

            @Override
            protected void done() {
                try {
                    List<IAMaterial> materials = get();
                    tbMdlArtikel.setData(materials);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    setDefaultCursor();
                }
            }


        };
        worker.execute();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    public void objectSelected(Object selection) {
        try {
            if (selection instanceof IAMaterial) {
                prepare4Close();
                setValue(selection);
            }
            else {
                throw new HurricanGUIException("Bitte wählen Sie ein Material aus!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        int selRow = tbArtikel.getSelectedRow();
        Object model = ((AKMutableTableModel) tbArtikel.getModel()).getDataAtRow(selRow);
        objectSelected(model);
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
    public void update(Observable o, Object arg) {
    }

}


