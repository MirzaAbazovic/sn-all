/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.07.2005 09:49:14
 */
package de.augustakom.hurrican.gui.tools.physik;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Panel, um Rangierungen zu kreuzen.
 *
 *
 */
public class RangierungKreuzenPanel extends AbstractServicePanel implements ItemListener {

    private static final Logger LOGGER = Logger.getLogger(RangierungKreuzenPanel.class);
    private static final long serialVersionUID = 580378462638856126L;

    // GUI-Elemente
    private AKJFormattedTextField tfRang1 = null;
    private AKJFormattedTextField tfRang2 = null;
    private AKJRadioButton rbEqOut = null;
    private AKJRadioButton rbEqIn = null;
    private AKJCheckBox chbUpdateES = null;
    private AKJCheckBox chbSwitchCB = null;
    private AKJCheckBox chbIgnorePhysik = null;
    private AKReflectionTableModel<Rangierung> tbMdlRangierungen = null;
    private AKJRadioButton rbEmpty = null;
    private ButtonGroup bgPhysik = null;

    // Modelle
    private Rangierung rangierungA = null;
    private Rangierung rangierungB = null;

    /**
     * Default-Konstruktor.
     */
    public RangierungKreuzenPanel() {
        super("de/augustakom/hurrican/gui/tools/physik/resources/RangierungKreuzenPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblRang1 = getSwingFactory().createLabel("rangierung1");
        AKJLabel lblRang2 = getSwingFactory().createLabel("rangierung2");
        AKJLabel lblKreuzung = getSwingFactory().createLabel("kreuzung");

        tfRang1 = getSwingFactory().createFormattedTextField("rangierung1");
        tfRang2 = getSwingFactory().createFormattedTextField("rangierung2");
        chbUpdateES = getSwingFactory().createCheckBox("update.es", null, true);
        chbUpdateES.addItemListener(this);
        chbSwitchCB = getSwingFactory().createCheckBox("switch.cb", null, true);
        chbIgnorePhysik = getSwingFactory().createCheckBox("ignore.physiktyp", null, false);
        ButtonGroup bg = new ButtonGroup();
        rbEqOut = getSwingFactory().createRadioButton("eq.out", bg, true);
        rbEqIn = getSwingFactory().createRadioButton("eq.in", bg);
        AKJButton btnShow = getSwingFactory().createButton("show", getActionListener());
        AKJButton btnKreuzen = getSwingFactory().createButton("kreuzen", getActionListener());
        AKJButton btnClear = getSwingFactory().createButton("clear", getActionListener());

        bgPhysik = new ButtonGroup();
        AKJRadioButton rbUebernahme = getSwingFactory().createRadioButton("physik.uebernahme", bgPhysik);
        rbUebernahme.setValueObject(PhysikaenderungsTyp.STRATEGY_ANSCHLUSSUEBERNAHME);
        AKJRadioButton rbAb2Isdn = getSwingFactory().createRadioButton("physik.wandel.ab.isdn", bgPhysik);
        rbAb2Isdn.setValueObject(PhysikaenderungsTyp.STRATEGY_WANDEL_ANALOG_ISDN);
        AKJRadioButton rbIsdn2Ab = getSwingFactory().createRadioButton("physik.wandel.isdn.ab", bgPhysik);
        rbIsdn2Ab.setValueObject(PhysikaenderungsTyp.STRATEGY_WANDEL_ISDN_ANALOG);
        AKJRadioButton rbDSLKreuzung = getSwingFactory().createRadioButton("physik.dsl.kreuzung", bgPhysik);
        rbDSLKreuzung.setValueObject(PhysikaenderungsTyp.STRATEGY_DSL_KREUZUNG);
        rbEmpty = new AKJRadioButton(bgPhysik);

        tbMdlRangierungen = new AKReflectionTableModel<Rangierung>(
                new String[] { "ID", "EQ-In", "EQ-Out", "Physiktyp", "LtgGes-ID", "History-From",
                        "History-Count", "ES-ID", "freigegeben", "Gültig von", "Gültig bis" },
                new String[] { "id", "eqInId", "eqOutId", "physikTypId", "leitungGesamtId", "historyFrom",
                        "historyCount", "esId", "freigegebenBoolean", "gueltigVon", "gueltigBis" },
                new Class[] { Long.class, Long.class, Long.class, Long.class, Integer.class, Long.class,
                        Integer.class, Long.class, Boolean.class, Date.class, Date.class }
        );
        AKJTable tbRangierungen = new AKJTable(tbMdlRangierungen, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbRangierungen.attachSorter();
        tbRangierungen.fitTable(new int[] { 80, 80, 80, 30, 70, 80, 40, 80, 20, 80, 80 });
        AKJScrollPane spTable = new AKJScrollPane(tbRangierungen, new Dimension(750, 150));

        AKJPanel pnlPhy = new AKJPanel(new GridBagLayout());
        pnlPhy.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("physikuebernahme")));
        pnlPhy.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        pnlPhy.add(rbUebernahme, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlPhy.add(rbAb2Isdn, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlPhy.add(rbIsdn2Ab, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlPhy.add(rbDSLKreuzung, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlPhy.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, 5, 1, 1, GridBagConstraints.BOTH));

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(lblRang1, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        top.add(tfRang1, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblRang2, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfRang2, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(btnShow, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.NONE, new Insets(2, 10, 2, 2)));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        top.add(lblKreuzung, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(rbEqOut, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(chbUpdateES, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(rbEqIn, GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(chbSwitchCB, GBCFactory.createGBC(0, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(chbIgnorePhysik, GBCFactory.createGBC(0, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 3, 6, 1, 1, GridBagConstraints.VERTICAL));
        top.add(pnlPhy, GBCFactory.createGBC(0, 100, 4, 0, 1, 7, GridBagConstraints.VERTICAL, new Insets(2, 15, 2, 2)));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 5, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnKreuzen, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnClear, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.NORTH);
        this.add(spTable, BorderLayout.CENTER);
        this.add(btnPnl, BorderLayout.SOUTH);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("kreuzen".equals(command)) {
            rangierungenKreuzen();
        }
        else if ("clear".equals(command)) {
            clear(true);
        }
        else if ("show".equals(command)) {
            showRangierungen();
        }
    }

    /* 'Loescht' die Anzeige */
    private void clear(boolean cleanAll) {
        rangierungA = null;
        rangierungB = null;
        tbMdlRangierungen.setData(null);
        if (cleanAll) {
            rbEmpty.setSelected(true);
        }
    }

    /* Laedt die eingetragenen Rangierungen. */
    private void showRangierungen() {
        clear(false);
        try {
            Long idA = tfRang1.getValueAsLong(null);
            Long idB = tfRang2.getValueAsLong(null);
            if ((idA == null) || (idB == null)) {
                throw new HurricanGUIException("Bitte die IDs der zu kreuzenden Rangierungen eintragen!");
            }

            RangierungsService rs = getCCService(RangierungsService.class);
            rangierungA = rs.findRangierung(idA);
            rangierungB = rs.findRangierung(idB);

            if ((rangierungA == null) || (rangierungB == null)) {
                throw new HurricanGUIException("Die Rangierungen konnten nicht geladen werden.\n" +
                        "Bitte kontrollieren Sie die IDs.");
            }

            tbMdlRangierungen.addObject(rangierungA);
            tbMdlRangierungen.addObject(rangierungB);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /* Fuehrt die Kreuzung der Rangierungen durch. */
    private void rangierungenKreuzen() {
        try {
            String msg = "Sollen die Rangierungen wirklich gekreuzt werden?!";
            int selection = MessageHelper.showConfirmDialog(getMainFrame(), msg, "Kreuzen?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (selection == JOptionPane.NO_OPTION) {
                return;
            }

            AKJRadioButton rbPhysik = GuiTools.getSelectedRadioButton(bgPhysik);
            Long physikaenderung = (rbPhysik != null) ? (Long) rbPhysik.getValueObject() : null;

            RangierungsService rs = getCCService(RangierungsService.class);
            EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
            List<Rangierung> result = rs.rangierungenKreuzen(rangierungA, rangierungB, rbEqOut.isSelected(),
                    rbEqIn.isSelected(), chbUpdateES.isSelected(), chbSwitchCB.isSelected(),
                    chbIgnorePhysik.isSelected(), physikaenderung);
            endgeraeteService.updateSchicht2Protokoll4Rangierungen(result);
            if ((result != null) && (!result.isEmpty())) {
                showRangierungen();
                tbMdlRangierungen.addObjects(result);

                MessageHelper.showInfoDialog(this, "Kreuzung wurde durchgeführt.\n" +
                        "Bitte noch Carrierbestellung kontrollieren (z.B. Kündigungsdatum).", null, true);
            }
            else {
                throw new HurricanGUIException("Kreuzung lieferte keine Daten - bitte kontrollieren!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == chbUpdateES) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                chbSwitchCB.setEnabled(false);
            }
            else {
                chbSwitchCB.setEnabled(true);
            }
        }
    }

}


