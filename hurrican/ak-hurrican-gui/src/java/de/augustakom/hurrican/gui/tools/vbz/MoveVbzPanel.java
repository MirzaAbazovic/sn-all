/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.08.2005 10:21:04
 */
package de.augustakom.hurrican.gui.tools.vbz;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Panel, um die Verbindungsbezeichnung eines bestimmten Auftrags auf einen anderen Auftrag zu verschieben. <br> <br>
 * Wichtig: die Verbindungsbezeichnung darf keinem aktiven Auftrag zugeordnet sein!
 *
 *
 */
public class MoveVbzPanel extends AbstractServicePanel {

    private static final Logger LOGGER = Logger.getLogger(MoveVbzPanel.class);

    // GUI-Elemente
    private AKJFormattedTextField tfAuftragOld = null;
    private AKJFormattedTextField tfAuftragNew = null;
    private AKJCheckBox chbMoveCB = null;

    /**
     * Default-Konstruktor.
     */
    public MoveVbzPanel() {
        super("de/augustakom/hurrican/gui/tools/vbz/resources/MoveVbzPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblAuftragOld = getSwingFactory().createLabel("auftrag.old");
        AKJLabel lblAuftragNew = getSwingFactory().createLabel("auftrag.new");

        tfAuftragOld = getSwingFactory().createFormattedTextField("auftrag.old");
        tfAuftragNew = getSwingFactory().createFormattedTextField("auftrag.new");
        chbMoveCB = getSwingFactory().createCheckBox("move.cb");
        AKJButton btnMove = getSwingFactory().createButton("move.vbz", getActionListener());

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        top.add(lblAuftragOld, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        top.add(tfAuftragOld, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblAuftragNew, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfAuftragNew, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(chbMoveCB, GBCFactory.createGBC(100, 0, 3, 3, 2, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2)));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 4, 1, 1, GridBagConstraints.BOTH));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnMove, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.CENTER);
        this.add(btnPnl, BorderLayout.SOUTH);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("move.vbz".equals(command)) {
            moveVbz();
        }
    }

    /*
     * Verschiebt die Verbindungsbezeichnung des 'alten' Auftrags auf den 'neuen' Auftrag.
     * Dabei wird u.a. geprueft, ob die Verbindungsbezeichnung keinem aktiven Auftrag zugeordnet ist.
     */
    private void moveVbz() {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragTechnik atOld = as.findAuftragTechnikByAuftragId(tfAuftragOld.getValueAsLong(null));
            AuftragTechnik atNew = as.findAuftragTechnikByAuftragId(tfAuftragNew.getValueAsLong(null));

            if ((atOld == null) || (atNew == null)) {
                throw new HurricanGUIException("Bitte korrekte Auftrag-IDs angeben!");
            }

            PhysikService ps = getCCService(PhysikService.class);
            VerbindungsBezeichnung verbindungsBezeichnung = ps.findVerbindungsBezeichnungById(atOld.getVbzId());
            if (verbindungsBezeichnung == null) {
                throw new HurricanGUIException("Verbindungsbezeichnung konnte nicht ermittelt werden!");
            }

            // pruefen, ob VerbindungsBezeichnung noch auf einem aktivem Auftrag vorhanden ist.
            AuftragEndstelleQuery query = new AuftragEndstelleQuery();
            query.setVbz(verbindungsBezeichnung.getVbz());
            List<AuftragEndstelleView> views = as.findAuftragEndstelleViews(query);
            if (views != null) {
                for (AuftragEndstelleView view : views) {
                    int status = (view.getAuftragStatusId() != null) ? view.getAuftragStatusId().intValue() : 0;
                    if ((status >= AuftragStatus.TECHNISCHE_REALISIERUNG) && (status < AuftragStatus.KUENDIGUNG)) {
                        throw new HurricanGUIException("Die Verbindungsbezeichnung befindet sich noch auf einem aktiven Auftrag!");
                    }
                }
            }

            atNew.setVbzId(verbindungsBezeichnung.getId());
            as.saveAuftragTechnik(atNew, true);
            MessageHelper.showMessageDialog(this, getSwingFactory().getText("vbz.moved", verbindungsBezeichnung.getVbz(),
                    "" + atNew.getAuftragId()), "Verbindungsbezeichnung verschoben", JOptionPane.INFORMATION_MESSAGE);

            if (chbMoveCB.isSelected()) {
                EndstellenService esSrv = getCCService(EndstellenService.class);
                Endstelle esANew = esSrv.findEndstelle(atNew.getAuftragTechnik2EndstelleId(), Endstelle.ENDSTELLEN_TYP_A);
                Endstelle esBNew = esSrv.findEndstelle(atNew.getAuftragTechnik2EndstelleId(), Endstelle.ENDSTELLEN_TYP_B);
                if ((esANew.getCb2EsId() != null) || (esBNew.getCb2EsId() != null)) {
                    throw new HurricanGUIException("Carrierbestellung kann nicht verschoben werden, da " +
                            "den Endstellen des neuen Auftrags bereits eine CB zugeordnet ist!");
                }

                Endstelle esAOld = esSrv.findEndstelle(atOld.getAuftragTechnik2EndstelleId(), Endstelle.ENDSTELLEN_TYP_A);
                Endstelle esBOld = esSrv.findEndstelle(atOld.getAuftragTechnik2EndstelleId(), Endstelle.ENDSTELLEN_TYP_B);

                esANew.setCb2EsId(esAOld.getCb2EsId());
                esBNew.setCb2EsId(esBOld.getCb2EsId());

                esSrv.saveEndstelle(esANew);
                esSrv.saveEndstelle(esBNew);

                MessageHelper.showMessageDialog(this, "Carrierbestellungen wurden auf den neuen Auftrag verschoben.",
                        "Carrierbestellung verschoben", JOptionPane.INFORMATION_MESSAGE);
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

}


