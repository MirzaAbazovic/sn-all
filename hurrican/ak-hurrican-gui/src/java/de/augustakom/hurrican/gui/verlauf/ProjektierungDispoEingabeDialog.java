/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2005 10:17:55
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.view.AbstractVerlaufView;
import de.augustakom.hurrican.model.cc.view.ProjektierungsView;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Dialog zur Eingabe von Detail-Daten zu der Projektierung. <br> Aktuell werden folgende Daten ueber den Dialog
 * erfasst: <ul> <li>Vorgabe AM <li>Anschlussart Endstelle A <li>Anschlussart Endstelle B </ul>
 *
 *
 */
public class ProjektierungDispoEingabeDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(ProjektierungDispoEingabeDialog.class);

    // GUI-Komponenten
    private AKJDateComponent dcVorgabeAm = null;
    private AKJComboBox cbAnschlussartA = null;
    private AKJComboBox cbAnschlussartB = null;

    // Modelle
    private AbstractVerlaufView verlaufView = null;

    /**
     * Konstruktor mit Angabe der Verlaufs-View, zu der die Daten erfasst werden sollen.
     *
     * @param verlaufView
     */
    public ProjektierungDispoEingabeDialog(AbstractVerlaufView verlaufView) {
        super("de/augustakom/hurrican/gui/verlauf/resources/ProjektierungDispoEingabeDialog.xml");
        this.verlaufView = verlaufView;
        if ((verlaufView == null) || (verlaufView.getAuftragId() == null)) {
            throw new IllegalArgumentException("Ungueltige Angabe des Verlaufs!");
        }

        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Daten zum Auftrag");

        AKJLabel lblVorgabeAm = getSwingFactory().createLabel("vorgabe.am");
        AKJLabel lblAnschlussartA = getSwingFactory().createLabel("anschlussart.a");
        AKJLabel lblAnschlussartB = getSwingFactory().createLabel("anschlussart.b");

        dcVorgabeAm = getSwingFactory().createDateComponent("vorgabe.am");
        ListCellRenderer ansArtRenderer = new AKCustomListCellRenderer<>(Anschlussart.class, Anschlussart::getAnschlussart);
        cbAnschlussartA = getSwingFactory().createComboBox("anschlussart.a", ansArtRenderer);
        cbAnschlussartB = getSwingFactory().createComboBox("anschlussart.b", ansArtRenderer);

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblVorgabeAm, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        child.add(dcVorgabeAm, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.NONE));
        child.add(lblAnschlussartA, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(cbAnschlussartA, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblAnschlussartB, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(cbAnschlussartB, GBCFactory.createGBC(0, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 5, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            setWaitCursor();
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten auftragDaten = as.findAuftragDatenByAuftragId(verlaufView.getAuftragId());
            AuftragTechnik auftragTechnik = as.findAuftragTechnikByAuftragId(verlaufView.getAuftragId());
            if (auftragDaten != null) {
                dcVorgabeAm.setDate(auftragDaten.getVorgabeSCV());
            }

            PhysikService ps = getCCService(PhysikService.class);
            List<Anschlussart> anschlussarten = ps.findAnschlussarten();

            cbAnschlussartA.addItems(anschlussarten, true, Anschlussart.class);
            cbAnschlussartB.addItems(anschlussarten, true, Anschlussart.class);

            if (auftragTechnik != null) {
                EndstellenService esSrv = getCCService(EndstellenService.class);
                Endstelle esA = esSrv.findEndstelle(auftragTechnik.getAuftragTechnik2EndstelleId(), Endstelle.ENDSTELLEN_TYP_A);
                if (esA != null) {
                    cbAnschlussartA.selectItem("getId", Anschlussart.class, esA.getAnschlussart());
                }

                Endstelle esB = esSrv.findEndstelle(auftragTechnik.getAuftragTechnik2EndstelleId(), Endstelle.ENDSTELLEN_TYP_B);
                if (esB != null) {
                    cbAnschlussartB.selectItem("getId", Anschlussart.class, esB.getAnschlussart());
                }
            }
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
        Date vorgabeAm = dcVorgabeAm.getDate(null);
        if (vorgabeAm == null) {
            MessageHelper.showInfoDialog(this, "Bitte tragen Sie für 'Vorgabe AM' ein gültiges Datum ein.", null, true);
            return;
        }

        try {
            setWaitCursor();
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten ad = as.findAuftragDatenByAuftragId(verlaufView.getAuftragId());
            AuftragTechnik at = as.findAuftragTechnikByAuftragId(verlaufView.getAuftragId());
            if ((ad == null) || (at == null)) {
                throw new HurricanGUIException("Auftrags-Daten zur ID " + verlaufView.getAuftragId() + " konnten nicht geladen werden.");
            }
            ad.setVorgabeSCV(vorgabeAm);
            as.saveAuftragDaten(ad, false);

            if (verlaufView instanceof ProjektierungsView) {
                ((ProjektierungsView) verlaufView).setVorgabeAm(vorgabeAm);
            }

            EndstellenService esSrv = getCCService(EndstellenService.class);
            Anschlussart artA = (Anschlussart) cbAnschlussartA.getSelectedItem();
            if ((artA != null) && (artA.getId() != null)) {
                Endstelle esA = esSrv.findEndstelle(at.getAuftragTechnik2EndstelleId(), Endstelle.ENDSTELLEN_TYP_A);
                if (esA == null) {
                    throw new HurricanGUIException("Endstelle A zum Auftrag konnte nicht ermittelt werden!");
                }
                esA.setAnschlussart(artA.getId());
                esSrv.saveEndstelle(esA);
            }

            Anschlussart artB = (Anschlussart) cbAnschlussartB.getSelectedItem();
            if ((artB != null) && (artB.getId() != null)) {
                Endstelle esB = esSrv.findEndstelle(at.getAuftragTechnik2EndstelleId(), Endstelle.ENDSTELLEN_TYP_B);
                if (esB == null) {
                    throw new HurricanGUIException("Endstelle B zum Auftrag konnte nicht ermittelt werden!");
                }
                esB.setAnschlussart(artB.getId());
                esSrv.saveEndstelle(esB);
            }

            as.saveAuftragTechnik(at, false);

            prepare4Close();
            setValue(Integer.valueOf(OK_OPTION));
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


