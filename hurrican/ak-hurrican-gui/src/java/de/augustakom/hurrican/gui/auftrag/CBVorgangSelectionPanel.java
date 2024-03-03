/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.09.2011 13:11:14
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.wita.model.CbVorgangDto;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Panel zur Auswahl eines CbVorgangs, wenn mehr als einer an der Carrierbestellung haengt.
 */
public class CBVorgangSelectionPanel extends AbstractServicePanel {

    private static final long serialVersionUID = 6612770675997210050L;
    private AKTableModelXML<CbVorgangDto> tbMdlCbVorgaenge;
    private AKJTable tbCBVorgaenge;

    private List<CbVorgangDto> cbVorgaengeForSelection;
    private final List<WitaCBVorgang> cbVorgaenge;

    private ReferenceService referenceService;

    public CBVorgangSelectionPanel(List<WitaCBVorgang> cbVorgaenge) {
        super("de/augustakom/hurrican/gui/auftrag/resources/CBVorgangSelectionPanel.xml");
        this.cbVorgaenge = cbVorgaenge;
        initServices();
        createGUI();
        setData();
    }

    private void initServices() {
        try {
            referenceService = getCCService(ReferenceService.class);
        }
        catch (ServiceNotFoundException e) {
            MessageHelper.showErrorDialog(this, e);
        }
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblCbVorgaenge = getSwingFactory().createLabel("cbVorgaenge");
        tbMdlCbVorgaenge = new AKTableModelXML<CbVorgangDto>(
                "de/augustakom/hurrican/gui/auftrag/resources/CBVorgangSelectionTable.xml");
        tbCBVorgaenge = new AKJTable(tbMdlCbVorgaenge, JTable.AUTO_RESIZE_OFF,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbCBVorgaenge.fitTable(tbMdlCbVorgaenge.getFitList());
        AKJScrollPane spCbVorgaenge = new AKJScrollPane(tbCBVorgaenge, new Dimension(455, 110));

        this.setLayout(new GridBagLayout());
        // @formatter:off
        int actcol = 0;
        int actline = 0;
        this.add(lblCbVorgaenge,    GBCFactory.createGBC(0,   0, actcol++, actline,   1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(),    GBCFactory.createGBC(100, 0, actcol++, actline,   1, 1, GridBagConstraints.NONE));
        actcol = 0;
        this.add(spCbVorgaenge,     GBCFactory.createGBC(0, 100, actcol++, ++actline, 4, 1, GridBagConstraints.BOTH));
        // @formatter:on

    }

    public final void setData() {
        cbVorgaengeForSelection = new ArrayList<CbVorgangDto>();
        if (cbVorgaenge != null) {
            try {
                for (CBVorgang cbVorgang : cbVorgaenge) {
                    cbVorgaengeForSelection.add(toCbVorgangDto(cbVorgang));
                }

                if ((tbCBVorgaenge != null) && (tbMdlCbVorgaenge != null)) {
                    tbMdlCbVorgaenge.setData(cbVorgaengeForSelection);
                    tbCBVorgaenge.repaint();
                }
            }
            catch (FindException e) {
                // reference not found
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    private CbVorgangDto toCbVorgangDto(CBVorgang cbVorgang) throws FindException {
        return new CbVorgangDto(Boolean.FALSE, cbVorgang, referenceService.findReference(
                cbVorgang.getTyp()).getStrValue());
    }

    public Set<Long> getSelectedCbVorgangIds() {
        Set<Long> selectedCbVorgangIds = new HashSet<Long>();
        for (CbVorgangDto cbVorgangDto : cbVorgaengeForSelection) {
            if (cbVorgangDto.getSelected()) {
                selectedCbVorgangIds.add(cbVorgangDto.getCbVorgangId());
            }
        }
        return selectedCbVorgangIds;
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


