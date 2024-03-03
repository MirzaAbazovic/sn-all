/**
 * Copyright (c) 2010 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2010 14:21:04
 */
package de.augustakom.hurrican.gui.tools.nbz;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.KundeNbz;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCKundenService;

/**
 * Dialog, um einer KundeNo eine NBZ zuzuordnen
 */
public class AttachNBZDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(AttachNBZDialog.class);

    private AKJTextField tfNBZ = null;

    private Long kundeNo = null;

    private KundeNbz kundeNbz = null;
    private CCKundenService ccKundenService = null;

    public AttachNBZDialog(Long kundeNo) {
        super("de/augustakom/hurrican/gui/tools/nbz/resources/AttachNBZDialog.xml");

        if (kundeNo == null) {
            throw new IllegalArgumentException("Keine Taifun-Auftragsnummer vorhanden.");
        }

        this.kundeNo = kundeNo;

        createGUI();
        doLoad();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title.nbz"));

        AKJLabel nbz = getSwingFactory().createLabel("nbz");

        tfNBZ = getSwingFactory().createTextField("nbz");

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(nbz, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        child.add(tfNBZ, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(child, BorderLayout.CENTER);
    }

    /**
     * method to load data by given kundeNo
     */
    private void doLoad() {
        try {
            ccKundenService = getCCService(CCKundenService.class);
            kundeNbz = ccKundenService.findKundeNbzByNo(this.kundeNo);
            if (kundeNbz == null) {
                kundeNbz = new KundeNbz();
                kundeNbz.setKundeNo(kundeNo);
            }
            else {
                tfNBZ.setText(kundeNbz.getNbz());
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
        kundeNbz.setNbz(tfNBZ.getText());
        try {
            // bei leerer NBZ Eintrag aus Tabelle löschen
            if (StringUtils.isBlank(kundeNbz.getNbz())) {
                if (kundeNbz.getId() != null) {
                    ccKundenService.removeKundeNbz(kundeNbz.getId());
                    MessageHelper.showInfoDialog(this, "Nutzerbezeichnung für Kunde " + kundeNo + " entfernt");
                }
            }
            else {
                // prüfen, ob NBZ korrekt formatiert ist
                if (kundeNbz.getNbz().length() != 6) {
                    MessageHelper.showErrorDialog(getMainFrame(), new StoreException("Nutzerbezeichnung - Falsche Länge (muss 6 Zeichen lang sein)"));
                    return;
                }
                if (kundeNbz.getNbz().matches(".*[^A-Z0-9x].*")) {
                    MessageHelper.showErrorDialog(getMainFrame(), new StoreException("Nutzerbezeichnung - Darf nur Großbuchstaben und Zahlen enthalten)"));
                    return;
                }

                // Warnhinweis ausgeben, wenn NBZ schon auf anderer Kundennr. hinterlegt
                boolean nbzExists = false;
                List<KundeNbz> existing = ccKundenService.findKundeNbzByNbz(kundeNbz.getNbz());
                for (KundeNbz knbz : existing) {
                    if (!knbz.getKundeNo().equals(kundeNo)) {
                        nbzExists = true;
                        break;
                    }
                }
                if (nbzExists) {
                    int result = MessageHelper.showYesNoQuestion(getMainFrame(), "Nutzerbezeichnung ist bereits hinterlegt. Wollen Sie überschreiben?", "Nutzerbezeichnung");
                    if (result == JOptionPane.NO_OPTION) { return; }
                }

                ccKundenService.saveKundeNbz(kundeNbz);
                MessageHelper.showInfoDialog(getMainFrame(), "Die Nutzerbezeichung wurde gespeichert.");
            }

            prepare4Close();
            setValue(JOptionPane.OK_OPTION);
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
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }
}
