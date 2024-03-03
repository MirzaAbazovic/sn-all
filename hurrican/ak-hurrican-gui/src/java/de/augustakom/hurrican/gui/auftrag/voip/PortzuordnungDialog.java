/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.04.13 12:33
 */
package de.augustakom.hurrican.gui.auftrag.voip;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.SelectedPortsView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;

/**
 *
 */
public class PortzuordnungDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(PortzuordnungDialog.class);

    private final Collection<AuftragVoipDNView> voIPDNs;
    private AKJComboBox cbRufnummer;
    private AKJDateComponent dcGueltigVon;
    private AKJDateComponent dcGueltigBis;
    private AKJCheckBox[] chkbPorts;

    public PortzuordnungDialog(final Collection<AuftragVoipDNView> voIPDNs) {
        super("de/augustakom/hurrican/gui/auftrag/voip/resources/AuftragVoIPPanel.xml");
        this.voIPDNs = voIPDNs;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJPanel panel = new AKJPanel(new GridBagLayout());
        cbRufnummer = new AKJComboBox(AuftragVoipDNView.class, "getTaifunDescription");
        cbRufnummer.addItems(voIPDNs);
        dcGueltigVon = new AKJDateComponent();
        dcGueltigVon.setDate(new Date());
        dcGueltigBis = new AKJDateComponent();
        dcGueltigBis.setDate(DateTools.getHurricanEndDate());

        int maxPortCount = 0;

        try {
            if (!voIPDNs.isEmpty()) {
                maxPortCount = getCCService(CCLeistungsService.class)
                        .getCountEndgeraetPort(voIPDNs.iterator().next().getAuftragId(), new Date());
            }
        }
        catch (FindException | ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
            prepare4Close();
            setValue(-1);
        }

        int row = 0;
        //@formatter:off
        panel.add(new AKJLabel("Rufnummer"),    GBCFactory.createGBC(0, 0, 0, row,   1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(cbRufnummer,                  GBCFactory.createGBC(0, 0, 2, row,   1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJLabel("Gültig von"),   GBCFactory.createGBC(0, 0, 0, ++row, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(dcGueltigVon,                 GBCFactory.createGBC(0, 0, 2, row,   1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJLabel("Gültig bis"),   GBCFactory.createGBC(0, 0, 0, ++row, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(dcGueltigBis,                 GBCFactory.createGBC(0, 0, 2, row,   1, 1, GridBagConstraints.HORIZONTAL));

        chkbPorts = new AKJCheckBox[maxPortCount];

        for(int i = 0; i < chkbPorts.length; i++) {
            chkbPorts[i] = new AKJCheckBox();
            panel.add(new AKJLabel("Port "+(i+1)),  GBCFactory.createGBC(0, 0, 0, ++row, 1, 1, GridBagConstraints.HORIZONTAL));
            panel.add(chkbPorts[i],                 GBCFactory.createGBC(0, 0, 2, row,   1, 1, GridBagConstraints.HORIZONTAL));
        }
        //@formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(panel, BorderLayout.CENTER);
    }

    @Override
    protected void validateSaveButton() {
        // Speichern Button nicht managed!
    }

    @Override
    protected void execute(String command) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void update(Observable o, Object arg) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void doSave() {
        final int numberOfPorts = chkbPorts.length;

        final boolean portSelection[] = new boolean[numberOfPorts];
        for (int i = 0; i < numberOfPorts; i++) {
            portSelection[i] = chkbPorts[i].isSelected();
        }

        final Date gueltigVon = dcGueltigVon.getDate(null);
        final Date gueltigBis = dcGueltigBis.getDate(null);

        if ((gueltigVon == null) || (gueltigBis == null)) {
            MessageHelper.showMessageDialog(this, "Gültig von / Gültig bis müssen gesetzt sein!");
        }
        else {
            final SelectedPortsView newSelectedPorts =
                    SelectedPortsView.createNewSelectedPorts(portSelection, gueltigVon, gueltigBis);
            final AuftragVoipDNView selectedVoipDn = (AuftragVoipDNView) cbRufnummer.getSelectedItem();
            selectedVoipDn.addSelectedPort(newSelectedPorts);
            prepare4Close();
            setValue(selectedVoipDn);
        }
    }

}
