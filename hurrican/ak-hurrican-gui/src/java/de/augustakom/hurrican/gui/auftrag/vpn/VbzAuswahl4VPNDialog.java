/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2004 15:43:02
 */
package de.augustakom.hurrican.gui.auftrag.vpn;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.hurrican.gui.auftrag.shared.AuftragProduktVbzTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzQuery;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzView;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Dialog zur Auswahl eines Auftrags, der als physik. Referenz fuer einen VPN-Auftrag verwendet werden soll. <br> Der
 * Dialog zeigt alle Auftraege des Kunden an, deren zugehoeriges Produkt fuer die VPN-Physik freigeschalten sind.
 * <br><br>
 * <p/>
 * Waehlt der Benutzer einen Auftrag aus (Objekt vom Typ <code>CCAuftragProduktVbzView</code>), so wird dieser der
 * Methode 'setValue' uebergeben.
 *
 *
 */
public class VbzAuswahl4VPNDialog extends AbstractServiceOptionDialog implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(VbzAuswahl4VPNDialog.class);

    private Long kundeNoOrig = null;

    private AKJTable tbVbz = null;
    private AuftragProduktVbzTableModel tbMdlVbz = null;

    /**
     * Konstruktor mit Angabe der Kunden-No.
     *
     * @param kundeNoOrig
     */
    public VbzAuswahl4VPNDialog(Long kundeNoOrig) {
        super(null);
        this.kundeNoOrig = kundeNoOrig;
        createGUI();
        load();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Physikal. Auftrag für VPN auswählen");
        configureButton(CMD_SAVE, "Übernehmen",
                "Übernimmt den selektierten Auftrag als physikal. Referenz in den VPN", true, true);

        tbMdlVbz = new AuftragProduktVbzTableModel();
        tbVbz = new AKJTable(tbMdlVbz, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbVbz.attachSorter();
        tbVbz.fitTable(new int[] { 80, 80, 100, 100, 100 });
        tbVbz.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spTable = new AKJScrollPane(tbVbz);
        spTable.setPreferredSize(new Dimension(485, 240));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /* Laedt die Auftraege fuer die Auswahl. */
    private void load() {
        if (this.kundeNoOrig != null) {
            setWaitCursor();

            final SwingWorker<List<CCAuftragProduktVbzView>, Void> worker = new SwingWorker<List<CCAuftragProduktVbzView>, Void>() {

                final Long kundeNoOrigInput = kundeNoOrig;

                @Override
                protected List<CCAuftragProduktVbzView> doInBackground() throws Exception {
                    CCAuftragService ccAS = getCCService(CCAuftragService.class);

                    CCAuftragProduktVbzQuery query = new CCAuftragProduktVbzQuery();
                    query.setKundeNo(kundeNoOrigInput);
                    query.setNurAuftraege4VPN(Boolean.TRUE);
                    List<CCAuftragProduktVbzView> auftraege = ccAS.findAuftragProduktVbzViews(query);
                    return auftraege;
                }

                @Override
                protected void done() {
                    try {
                        tbMdlVbz.setData(get());
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
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        AKMutableTableModel mdl = (AKMutableTableModel) tbVbz.getModel();
        objectSelected(mdl.getDataAtRow(tbVbz.getSelectedRow()));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    public void objectSelected(Object selection) {
        if (selection instanceof CCAuftragProduktVbzView) {
            if (getButton(CMD_SAVE).isEnabled()) {
                prepare4Close();
                setValue(selection);
            }
        }
        else {
            MessageHelper.showInfoDialog(this,
                    "Bitte selektieren Sie einen Auftrag, der als physikal. Referenz im VPN dienen soll oder brechen Sie den Dialog ab.",
                    null, true);
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
    public void update(Observable o, Object arg) {
    }

}


