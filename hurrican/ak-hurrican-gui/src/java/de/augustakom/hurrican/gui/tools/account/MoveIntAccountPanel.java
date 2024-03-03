/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2005 13:55:49
 */
package de.augustakom.hurrican.gui.tools.account;

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
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Panel, ueber das ein Account von einem Auftrag auf einen anderen Auftrag 'verschoben' werden kann.
 *
 *
 */
public class MoveIntAccountPanel extends AbstractServicePanel {

    private static final Logger LOGGER = Logger.getLogger(MoveIntAccountPanel.class);

    private AKJTextField tfAccount = null;
    private AKJFormattedTextField tfAuftragId = null;
    private AKJCheckBox chbEntsperren = null;

    /**
     * Default-Konstruktor.
     */
    public MoveIntAccountPanel() {
        super("de/augustakom/hurrican/gui/tools/account/resources/MoveIntAccountPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblAccount = getSwingFactory().createLabel("account");
        AKJLabel lblAuftragId = getSwingFactory().createLabel("auftrag.id");
        AKJLabel lblEntsperren = getSwingFactory().createLabel("entsperren");

        tfAccount = getSwingFactory().createTextField("account");
        tfAuftragId = getSwingFactory().createFormattedTextField("auftrag.id");
        chbEntsperren = getSwingFactory().createCheckBox("entsperren");
        AKJButton btnSave = getSwingFactory().createButton("save", getActionListener());

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblAccount, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        child.add(tfAccount, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblAuftragId, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfAuftragId, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblEntsperren, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(chbEntsperren, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2)));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnSave, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(child, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(btnPnl, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(5, 2, 2, 2)));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 2, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("save".equals(command)) {
            doSave();
        }
    }

    /* 'Verschiebt' den Account auf den angegebenen Auftrag. */
    private void doSave() {
        int sel = MessageHelper.showConfirmDialog(getMainFrame(),
                "Soll der Account wirklich auf einen\nanderen Auftrag verschoben werden?", "Account verschieben?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (sel != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            AccountService accs = getCCService(AccountService.class);
            IntAccount account = accs.findIntAccount(tfAccount.getText());
            if (account == null) {
                throw new HurricanGUIException("Account wurde nicht gefunden!");
            }

            CCAuftragService as = getCCService(CCAuftragService.class);
            Long auftragId = tfAuftragId.getValueAsLong(null);
            AuftragTechnik auftragTechnik = as.findAuftragTechnikByAuftragId(auftragId);
            if (auftragTechnik == null) {
                throw new HurricanGUIException("Zur Auftrags-ID wurde kein passender Auftrag gefunden!");
            }

            // pruefen, ob Account-Typ fuer neuen Auftrag zulaessig ist
            ProduktService ps = getCCService(ProduktService.class);
            Produkt produkt = ps.findProdukt4Auftrag(auftragId);
            if (produkt == null) {
                throw new HurricanGUIException("Es konnte nicht ermittelt werden, um welches Produkt es sich handelt!");
            }

            if (NumberTools.notEqual(produkt.getLiNr(), account.getLiNr())) {
                throw new HurricanGUIException("Der Account-Typ ist für den Auftrag bzw. das Produkt nicht möglich!");
            }

            List<AuftragTechnik> ats4Acc = as.findAuftragTechnik4IntAccount(account.getId());
            if ((ats4Acc != null) && !ats4Acc.isEmpty()) {
                // ermitteln, ob Auftraege des Account gekuendigt sind
                for (AuftragTechnik at : ats4Acc) {
                    AuftragDaten ad = as.findAuftragDatenByAuftragId(at.getAuftragId());
                    Long status = ad.getStatusId();

                    if ((status < AuftragStatus.KUENDIGUNG)
                            && !NumberTools.isIn(status, new Number[] { AuftragStatus.STORNO, AuftragStatus.ABSAGE })) {
                        throw new HurricanGUIException("Der Account ist noch einem aktiven Auftrag zugeordnet!");
                    }
                }

                // Account ist auf keinem aktiven Auftrag --> umhaengen erlaubt
                auftragTechnik.setIntAccountId(account.getId());
                as.saveAuftragTechnik(auftragTechnik, true);

                if (chbEntsperren.isSelected()) {
                    account.setGesperrt(Boolean.FALSE);
                    accs.saveIntAccount(account, false);
                }

                MessageHelper.showInfoDialog(getMainFrame(), "Account wurde erfolgreich umgehängt.", null, true);
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
        // intentionally left blank
    }

}


