/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.07.2005 13:29:34
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
import de.augustakom.hurrican.service.cc.CCKundenService;


/**
 * Dialog zur Anzeige/Auswahl von Auftraegen eines best. Kunden.
 *
 *
 */
public class KundeAuftragViewsDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(KundeAuftragViewsDialog.class);

    private Long kundeNoOrig = null;
    private boolean excludeInvalid = false;

    private AKJTable tbAuftraege = null;
    private AKReflectionTableModel tbMdlAuftraege = null;

    /**
     * Konstruktor mit Angabe der Kundennummer.
     *
     * @param kundeNoOrig
     * @param excludeInvalid Flag, ob nur nach 'gueltigen' Auftraegen gesucht werden soll.
     */
    public KundeAuftragViewsDialog(Long kundeNoOrig, boolean excludeInvalid) {
        super(null);
        this.kundeNoOrig = kundeNoOrig;
        this.excludeInvalid = excludeInvalid;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Aufträge des Kunden " + kundeNoOrig);
        configureButton(CMD_SAVE, "Auswahl", null, true, true);

        tbMdlAuftraege = new AKReflectionTableModel<CCKundeAuftragView>(
                new String[] { "Auftrag-Id", VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Produkt", "Status", "Endstelle B", "VPN-Id" },
                new String[] { "auftragId", "vbz", "anschlussart", "statusText", "endstelleB", "vpnId" },
                new Class[] { Long.class, String.class, String.class, String.class, String.class, Long.class });
        tbAuftraege = new AKJTable(tbMdlAuftraege, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAuftraege.attachSorter();
        AKJScrollPane spTable = new AKJScrollPane(tbAuftraege, new Dimension(550, 300));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            CCKundenService ks = getCCService(CCKundenService.class);
            List<CCKundeAuftragView> result = ks.findKundeAuftragViews4Kunde(kundeNoOrig, excludeInvalid, true);
            this.tbMdlAuftraege.setData(result);
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
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        int selection = tbAuftraege.getSelectedRow();
        AKMutableTableModel model = (AKMutableTableModel) tbAuftraege.getModel();
        Object value = model.getDataAtRow(selection);
        if (value instanceof CCKundeAuftragView) {
            prepare4Close();
            setValue(value);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(),
                    "Bitte wählen Sie einen Auftrag aus oder betätigen Sie <Abbrechen>.", null, true);
        }
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
    public void update(Observable o, Object arg) {
    }

}


