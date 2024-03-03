/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2012 11:08:52
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.mnet.wita.model.AuftragZuordnungDto;

/**
 * Dialog zum Auswahlen eines Auftrags für die Zuordnung eiener abgebenden LEitung
 */
public class AuftragZuordnungDialog extends AbstractServiceOptionDialog {

    private static final long serialVersionUID = -2047790242741011117L;

    private static final Logger LOGGER = Logger.getLogger(AuftragZuordnungDialog.class);

    private static final String TITLE = "title";
    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/wita/resources/AuftragZuordnungDialog.xml";

    private final List<AuftragDaten> zugeordneteAuftraege;

    private AuftragZuordnungPanel auftragZuordnungPnl;

    private Pair<Long, Long> selectedAuftragIdAndCbId = null;

    public AuftragZuordnungDialog(List<AuftragDaten> zugeordneteAuftraege) {
        super(RESOURCE);
        this.zugeordneteAuftraege = zugeordneteAuftraege;
        try {
            createGUI();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText(TITLE));
        configureButton(CMD_SAVE, "Speichern", "Speichert die zugeordneten Aufträge", true, true);

        auftragZuordnungPnl = new AuftragZuordnungPanel(zugeordneteAuftraege);

        AKJPanel child = getChildPanel();
        child.setLayout(new BorderLayout());
        child.add(new AKJPanel(), BorderLayout.NORTH);
        child.add(new AKJPanel(), BorderLayout.WEST);
        child.add(auftragZuordnungPnl, BorderLayout.CENTER);
        child.add(new AKJPanel(), BorderLayout.EAST);
        child.add(new AKJPanel(), BorderLayout.SOUTH);
        child.validate();
    }

    @Override
    protected void doSave() {
        try {
            Set<AuftragZuordnungDto> selectedAuftraege = auftragZuordnungPnl.getSelectedAuftraege();
            if (!CollectionTools.hasExpectedSize(selectedAuftraege, 1)) {
                if (CollectionTools.isEmpty(selectedAuftraege)) {
                    MessageHelper.showInfoDialog(this.getMainFrame(),
                            "Bitte wählen Sie einen Auftrag für die Zuordnung aus!");
                }
                else {
                    MessageHelper.showInfoDialog(this.getMainFrame(),
                            "Es darf nur ein einziger Auftrag zu dem UserTask zugeordent werden!");
                }
                return;
            }

            AuftragZuordnungDto dto = selectedAuftraege.iterator().next();
            selectedAuftragIdAndCbId = Pair.create(dto.getAuftragId(), dto.getCarrierbestellungId());

            prepare4Close();
            setValue(JOptionPane.OK_OPTION);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /**
     * returns the id of the selected {@link Auftrag} and {@link Carrierbestellung} after user pressed button "save"
     */
    public Pair<Long, Long> getSelectedAuftragIdAndCbId() {
        return selectedAuftragIdAndCbId;
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    protected void execute(String command) {
        // not used
    }
}
