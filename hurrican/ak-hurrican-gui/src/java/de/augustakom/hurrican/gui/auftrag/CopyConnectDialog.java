/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2005 11:28:13
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.shared.KundeAuftragViewsDialog;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Dialog, um einen AK-Connect Auftrag zu kopieren.
 *
 *
 */
public class CopyConnectDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(CopyConnectDialog.class);
    private static final long serialVersionUID = 7275393888117257164L;

    private AKJFormattedTextField tfAbrechnung = null;
    private AKJFormattedTextField tfBuendelNr = null;
    private AKJButton btnSearch = null;
    private AKJDateComponent dcVorgabeSCV = null;
    private AKJFormattedTextField tfAnzahl = null;
    private AKJCheckBox chbCopyES = null;

    private Long auftragId2Copy = null;
    private Auftrag auftrag2Copy = null;
    private AuftragDaten ad2Copy = null;
    private Long parentAuftragId = null;

    private int copyCount = -1;

    /**
     * Konstruktor mit Angabe der ID des zu kopierenden Auftrags.
     *
     * @param auftragId2Copy
     */
    public CopyConnectDialog(Long auftragId2Copy) {
        super("de/augustakom/hurrican/gui/auftrag/resources/CopyConnectDialog.xml");
        this.auftragId2Copy = auftragId2Copy;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Auftrag kopieren");
        setIconURL("de/augustakom/hurrican/gui/images/copy.gif");
        configureButton(CMD_SAVE, "Kopieren", "Kopiert den aktuellen Auftrag", true, true);

        AKJLabel lblAbrechnung = getSwingFactory().createLabel("abrechnungsauftrag");
        AKJLabel lblBuendel = getSwingFactory().createLabel("buendel.nr");
        AKJLabel lblVorgabeSCV = getSwingFactory().createLabel("vorgabe.scv");
        AKJLabel lblAnzahl = getSwingFactory().createLabel("anzahl.kopien");
        AKJLabel lblCopyES = getSwingFactory().createLabel("copy.es");

        tfAbrechnung = getSwingFactory().createFormattedTextField("abrechnungsauftrag", false);
        tfBuendelNr = getSwingFactory().createFormattedTextField("buendel.nr", false);
        dcVorgabeSCV = getSwingFactory().createDateComponent("vorgabe.scv");
        tfAnzahl = getSwingFactory().createFormattedTextField("anzahl.kopien");
        chbCopyES = getSwingFactory().createCheckBox("copy.es", null, true);
        btnSearch = getSwingFactory().createButton("search", getActionListener());

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblAbrechnung, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfAbrechnung, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(btnSearch, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblBuendel, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBuendelNr, GBCFactory.createGBC(100, 0, 3, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblVorgabeSCV, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcVorgabeSCV, GBCFactory.createGBC(100, 0, 3, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCopyES, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbCopyES, GBCFactory.createGBC(100, 0, 3, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAnzahl, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfAnzahl, GBCFactory.createGBC(100, 0, 3, 4, 2, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(left, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();

            CCAuftragService as = getCCService(CCAuftragService.class);
            auftrag2Copy = as.findAuftragById(auftragId2Copy);
            ad2Copy = as.findAuftragDatenByAuftragId(auftragId2Copy);
            if ((auftrag2Copy == null) || (ad2Copy == null)) {
                throw new HurricanGUIException("Auftrags-Daten konnten nicht ermittelt werden!");
            }

            dcVorgabeSCV.setDate((DateTools.isDateAfter(ad2Copy.getVorgabeSCV(), new Date()))
                    ? ad2Copy.getVorgabeSCV() : null);
            if ((ad2Copy.getBuendelNr() != null) && (ad2Copy.getBuendelNr() > 0)) {
                tfBuendelNr.setValue(ad2Copy.getBuendelNr());
                btnSearch.setEnabled(false);
            }
        }
        catch (Exception e) {
            getButton(CMD_SAVE).setEnabled(false);
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
        try {
            setWaitCursor();

            Date vorgabeSCV = dcVorgabeSCV.getDate(null);
            boolean copyES = chbCopyES.isSelected();
            copyCount = tfAnzahl.getValueAsInt(-1);

            validateData();

            int result = MessageHelper.showConfirmDialog(getMainFrame(),
                    "Wollen Sie wirklich {0} Kopien erstellen?", new Object[] { copyCount }, "Kopien erstellen?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                CCAuftragService as = getCCService(CCAuftragService.class);

                // Kopien des Auftrags werden ueber den Service erstellt
                List<Auftrag> copies = as.copyAuftrag(HurricanSystemRegistry.instance().getSessionId(),
                        auftragId2Copy, parentAuftragId, ad2Copy.getBuendelNr(), ad2Copy.getBuendelNrHerkunft(),
                        copyCount, vorgabeSCV, copyES);

                if ((copies == null) || (copies.isEmpty())) {
                    throw new HurricanGUIException("Es wurden keine Kopien angelegt!");
                }

                StringBuilder sb = new StringBuilder();
                for (Auftrag a : copies) {
                    if (sb.length() > 0) { sb.append(SystemUtils.LINE_SEPARATOR); }
                    sb.append(a.getId());
                }

                MessageHelper.showInfoDialog(getMainFrame(),
                        "Es wurden folgende Aufträge angelegt:\n" + sb.toString(), null, true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
            prepare4Close();
            setValue(OK_OPTION);
        }
    }

    /*
     * Ueberprueft, ob mit den angegebenen Daten ein Connect-Auftrag kopiert werden kann.
     */
    private void validateData() throws HurricanGUIException {
        if ((parentAuftragId == null) && ((ad2Copy.getBuendelNr() == null) || (ad2Copy.getBuendelNr() <= 0))) {
            throw new HurricanGUIException("Wählen Sie bitte einen Abrechnungsauftrag aus oder " +
                    "erstellen Sie die Kopie von einem Auftrag, der eine Bündel-Nr besitzt.");
        }

        if (copyCount <= 0) {
            throw new HurricanGUIException("Bitte geben Sie eine gültige Anzahl Kopien an.");
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("search".equals(command)) {
            KundeAuftragViewsDialog dlg = new KundeAuftragViewsDialog(auftrag2Copy.getKundeNo(), true);
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (result instanceof CCKundeAuftragView) {
                parentAuftragId = ((CCKundeAuftragView) result).getAuftragId();
                tfAbrechnung.setValue(parentAuftragId);
            }
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


