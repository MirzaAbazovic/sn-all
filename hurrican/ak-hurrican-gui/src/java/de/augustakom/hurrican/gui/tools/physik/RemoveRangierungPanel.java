/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.07.2005 08:59:34
 */
package de.augustakom.hurrican.gui.tools.physik;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.shared.Equipment4RangierungTableModel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Panel, um die Rangierung von einer Endstelle zu entfernen.
 *
 *
 */
public class RemoveRangierungPanel extends AbstractServicePanel implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(RemoveRangierungPanel.class);

    // GUI-Komponenten
    private AKJFormattedTextField tfEsId = null;
    private AKJRadioButton rbNow = null;
    private AKJRadioButton rbDate = null;
    private AKJDateComponent dcFreigabe = null;
    private Equipment4RangierungTableModel tbMdlRangierung = null;
    private AKJButton btnRemove = null;

    // Modelle
    private Endstelle endstelle = null;
    private List<Rangierung> rangierungen = null;

    /**
     * Default-Konstruktor.
     */
    public RemoveRangierungPanel() {
        super("de/augustakom/hurrican/gui/tools/physik/resources/RemoveRangierungPanel.xml");
        loadData();
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblEsId = getSwingFactory().createLabel("es.id");
        AKJLabel lblRangierung = getSwingFactory().createLabel("rangierung");
        AKJLabel lblFreigabe = getSwingFactory().createLabel("freigabe");

        tfEsId = getSwingFactory().createFormattedTextField("es.id");
        dcFreigabe = getSwingFactory().createDateComponent("freigabe.ab");
        ButtonGroup bg = new ButtonGroup();
        rbNow = getSwingFactory().createRadioButton("freigabe.now", bg);
        rbDate = getSwingFactory().createRadioButton("freigabe.date", bg);

        AKJButton btnShow = getSwingFactory().createButton("show", getActionListener());
        AKJButton btnClear = getSwingFactory().createButton("clear", getActionListener());
        btnRemove = getSwingFactory().createButton("remove.rangierung", getActionListener());

        tbMdlRangierung = new Equipment4RangierungTableModel();
        AKJTable tbRangierung = new AKJTable(tbMdlRangierung, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbMdlRangierung.setTable(tbRangierung);
        tbRangierung.fitTable(new int[] { 85, 95, 95, 95, 95 });
        AKJScrollPane spTable = new AKJScrollPane(tbRangierung, new Dimension(500, 250));

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(lblEsId, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 2)));
        top.add(tfEsId, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 15, 2, 2)));
        top.add(btnShow, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 15, 2, 15)));
        top.add(btnClear, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.add(top, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblRangierung, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        child.add(lblFreigabe, GBCFactory.createGBC(0, 0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        child.add(spTable, GBCFactory.createGBC(0, 0, 0, 2, 1, 5, GridBagConstraints.NONE));
        child.add(rbNow, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(rbDate, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(dcFreigabe, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 2, 10)));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.VERTICAL));
        child.add(btnRemove, GBCFactory.createGBC(0, 0, 3, 5, 1, 1, GridBagConstraints.NONE));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 7, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new BorderLayout());
        this.add(new AKJScrollPane(child));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("show".equals(command)) {
            showRangierung();
        }
        else if ("clear".equals(command)) {
            clear();
        }
        else if ("remove.rangierung".equals(command)) {
            removeRangierung();
        }
    }

    /* Laedt die Rangierung der eingetragenen Endstelle und zeigt diese an. */
    private void showRangierung() {
        try {
            this.endstelle = null;

            EndstellenService esSrv = getCCService(EndstellenService.class);
            endstelle = esSrv.findEndstelle(tfEsId.getValueAsLong(null));
            if (endstelle == null) {
                throw new HurricanGUIException("Endstelle konnte nicht ermittelt werden!");
            }

            // Rangierungen laden
            RangierungsService rs = getCCService(RangierungsService.class);
            Rangierung rangierung = rs.findRangierungWithEQ(endstelle.getRangierId());
            Rangierung rangierungAdd = null;
            if (endstelle.getRangierIdAdditional() != null) {
                rangierungAdd = rs.findRangierungWithEQ(endstelle.getRangierIdAdditional());
            }

            if ((rangierung == null) && (rangierungAdd == null)) {
                throw new HurricanGUIException("Zur Endstelle konnte keine Rangierung ermittelt werden!");
            }

            rangierungen = new ArrayList<Rangierung>();
            CollectionTools.addIfNotNull(rangierungen, rangierung);
            CollectionTools.addIfNotNull(rangierungen, rangierungAdd);

            tbMdlRangierung.setEndstelle(endstelle);
            tbMdlRangierung.setRangierung(rangierung, rangierungAdd);

            btnRemove.setEnabled(true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            btnRemove.setEnabled(false);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Entfernt die Rangierung von der Endstelle und setzt die Freigabe-Optionen. */
    private void removeRangierung() {
        try {
            int selection = MessageHelper.showConfirmDialog(getMainFrame(),
                    "Soll die Rangierung wirklich von der\nEndstelle entfernt werden?", "Rangierung entfernen?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (selection != JOptionPane.YES_OPTION) {
                return;
            }

            if ((endstelle == null) || CollectionTools.isEmpty(rangierungen)) {
                throw new HurricanGUIException("Die benötigten Endstellen-/Rangierungs-Daten fehlen!");
            }

            if (!rbNow.isSelected() && !rbDate.isSelected()) {
                throw new HurricanGUIException("Bitte Freigabe-Optionen wählen!");
            }

            for (Rangierung rangierung : rangierungen) {
                if (rangierung.isRangierungFrei(true)) {
                    throw new HurricanGUIException("Rangierung ist bereits frei oder ist freigabebereit!");
                }
                else if (NumberTools.notEqual(endstelle.getId(), rangierung.getEsId())) {
                    throw new HurricanGUIException("Die Rangierung ist einer anderen Endstelle zugeordnet!.\n" +
                            "Rangierung kann nicht entfernt werden!");
                }

                Date freigabeAb = dcFreigabe.getDate(null);
                if (rbDate.isSelected() && (freigabeAb == null)) {
                    throw new HurricanGUIException("Bitte definieren Sie ein Datum, ab dem die Rangierung wieder " +
                            "zur Verfügung stehen soll.");
                }

                EndstellenService endstellenService = getCCService(EndstellenService.class);
                EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
                PhysikService physikService = getCCService(PhysikService.class);
                RangierungsService rangierungsService = getCCService(RangierungsService.class);
                RangierungFreigabeService rangierungFreigabeService = getCCService(RangierungFreigabeService.class);

                rangierung.setFreigegeben(Freigegeben.freigegeben);
                String bemerkung = String.format("Freigabe durch %s", HurricanSystemRegistry.instance().getCurrentUserName());
                if (rbNow.isSelected()) {
                    rangierungFreigabeService.freigebenRangierung(rangierung, bemerkung, false);
                }
                else if (rbDate.isSelected()) {
                    rangierung.setBemerkung(bemerkung);
                    rangierung.setFreigabeAb(freigabeAb);
                    rangierung.setEsId(Rangierung.RANGIERUNG_NOT_ACTIVE);
                    rangierungsService.saveRangierung(rangierung, false);
                }

                endstelle.setRangierId(null);
                endstelle.setRangierIdAdditional(null);
                endstellenService.saveEndstelle(endstelle);
                endgeraeteService.updateSchicht2Protokoll4Endstelle(endstelle);

                // pruefen, ob eine Physikuebernahme protokolliert ist
                CCAuftragService as = getCCService(CCAuftragService.class);
                AuftragDaten ad = as.findAuftragDatenByEndstelle(endstelle.getId());
                if (ad != null) {
                    PhysikUebernahme pu = physikService.findLastPhysikUebernahme(ad.getAuftragId());
                    if (pu != null) {
                        MessageHelper.showInfoDialog(getMainFrame(),
                                "Zu dem Auftrag existiert eine Physikuebernahme-Protokollierung.\n" +
                                        "Bitte kontrollieren und ggf. selbst entfernen!"
                        );
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* 'Loescht' die Daten des Dialogs */
    private void clear() {
        tbMdlRangierung.removeAll();
        this.endstelle = null;
        tfEsId.setValue(null);
        dcFreigabe.setDate(null);
        rbDate.setSelected(false);
        rbNow.setSelected(false);
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


