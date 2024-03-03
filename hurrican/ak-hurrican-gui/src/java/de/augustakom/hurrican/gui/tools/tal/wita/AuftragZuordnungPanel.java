/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2012 18:02:50
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.mnet.wita.model.AuftragZuordnungDto;

public class AuftragZuordnungPanel extends AbstractServicePanel implements AKObjectSelectionListener {

    private static final long serialVersionUID = -9199698937788924419L;

    private static final Logger LOGGER = Logger.getLogger(AuftragZuordnungPanel.class);

    private static final String ADD_AUFTRAG = "add.auftrag";
    private static final String DEL_AUFTRAG = "del.auftrag";

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/wita/resources/AuftragZuordnungDialog.xml";

    private final Set<AuftragZuordnungDto> auftraegeList = new HashSet<>();
    private final List<AuftragDaten> zugeordneteAuftraege;

    private AKTableModelXML<AuftragZuordnungDto> tbMdlAuftraege;
    private AKJTable tbAuftraege;

    private PhysikService physikService;
    private EndstellenService endstellenService;
    private CarrierService carrierService;
    private CCAuftragService auftragService;

    public AuftragZuordnungPanel(List<AuftragDaten> zugeordneteAuftraege) {
        super(RESOURCE);
        this.zugeordneteAuftraege = zugeordneteAuftraege;
        try {
            init();
            createGUI();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    private void init() throws ServiceNotFoundException {
        physikService = getCCService(PhysikService.class);
        endstellenService = getCCService(EndstellenService.class);
        carrierService = getCCService(CarrierService.class);
        auftragService = getCCService(CCAuftragService.class);
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    public final void loadData() {
        try {
            if (CollectionTools.isEmpty(auftraegeList) && CollectionTools.isNotEmpty(zugeordneteAuftraege)) {
                for (AuftragDaten auftragDaten : zugeordneteAuftraege) {
                    auftraegeList.addAll(loadAuftragDtos(auftragDaten, true));
                }
            }
            tbMdlAuftraege.setData(auftraegeList);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        tbMdlAuftraege = new AKTableModelXML<>(
                "de/augustakom/hurrican/gui/tools/tal/wita/resources/AuftragZuordnungTable.xml");
        tbAuftraege = new AKJTable(tbMdlAuftraege, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAuftraege.fitTable(tbMdlAuftraege.getFitList());
        AKJScrollPane spTable = new AKJScrollPane(tbAuftraege, new Dimension(525, 110));

        AKJPanel auftragTablePanel = new AKJPanel(new BorderLayout());
        auftragTablePanel.add(spTable, BorderLayout.CENTER);

        AKJButton btnAddAuftrag = getSwingFactory().createButton(ADD_AUFTRAG, getActionListener(), null);

        final AKJButton btnDelAuftrag = getSwingFactory().createButton(DEL_AUFTRAG, getActionListener(), null);
        btnDelAuftrag.setEnabled(false);
        tbAuftraege.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (tbAuftraege.getSelectedRow() < 0) {
                    btnDelAuftrag.setEnabled(false);
                }
                else {
                    btnDelAuftrag.setEnabled(true);
                }
            }
        });

        AKJPanel auftragButtonPanel = new AKJPanel(new GridBagLayout());
        // @formatter:off
        auftragButtonPanel.add(btnAddAuftrag,  GBCFactory.createGBC(0,     0, 0, 0, 1, 1, GridBagConstraints.NONE));
        auftragButtonPanel.add(new AKJPanel(), GBCFactory.createGBC(0,     0, 0, 1, 1, 1, GridBagConstraints.VERTICAL));
        auftragButtonPanel.add(btnDelAuftrag,  GBCFactory.createGBC(0,     0, 0, 2, 1, 1, GridBagConstraints.NONE));
        auftragButtonPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        AKJPanel auftragPanel = new AKJPanel(new BorderLayout());
        auftragPanel.setBorder(BorderFactory.createTitledBorder("Aufträge für die Zuordnung"));
        auftragPanel.add(auftragButtonPanel, BorderLayout.WEST);
        auftragPanel.add(auftragTablePanel, BorderLayout.CENTER);

        // @formatter:off
        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(auftragPanel,   GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
    }

    @Override
    protected void execute(String command) {
        try {
            if (ADD_AUFTRAG.equals(command)) {
                addAuftrag();
            }
            if (DEL_AUFTRAG.equals(command)) {
                deleteAuftrag();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
            loadData();
        }
    }

    @Override
    public void objectSelected(Object selection) {
        // not used
    }

    public Set<AuftragZuordnungDto> getSelectedAuftraege() {
        Set<AuftragZuordnungDto> selectedAuftraege = new HashSet<>();
        if (CollectionTools.isNotEmpty(auftraegeList)) {
            for (AuftragZuordnungDto aufrag : auftraegeList) {
                if (aufrag.isSelected()) {
                    selectedAuftraege.add(aufrag);
                }
            }
        }
        return selectedAuftraege;
    }

    private Set<AuftragZuordnungDto> loadAuftragDtos(AuftragDaten auftragDaten, boolean selected) throws FindException {
        Set<AuftragZuordnungDto> result = Sets.newHashSet();
        for (Carrierbestellung carrierbestellung : findCarrierbestellungen4Auftrag(auftragDaten)) {
            AuftragZuordnungDto dto = new AuftragZuordnungDto();
            dto.setSelected(selected);
            dto.setAuftragId(auftragDaten.getAuftragId());
            dto.setVbz(loadVbz(auftragDaten));
            dto.setCarrierbestellungId(carrierbestellung.getId());
            dto.setDtagVertragsnummer(carrierbestellung.getVtrNr());
            dto.setDtagLbz(carrierbestellung.getLbz());

            result.add(dto);
        }
        return result;
    }

    private String loadVbz(AuftragDaten auftragDaten) throws FindException {
        VerbindungsBezeichnung verbindungsBezeichnung = physikService
                .findVerbindungsBezeichnungByAuftragId(auftragDaten.getAuftragId());
        return (verbindungsBezeichnung != null) ? verbindungsBezeichnung.getVbz() : null;
    }

    private Set<Carrierbestellung> findCarrierbestellungen4Auftrag(AuftragDaten auftragDaten) throws FindException {
        List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftragDaten.getAuftragId());

        Set<Carrierbestellung> result = Sets.newHashSet();
        for (Endstelle endstelle : endstellen) {
            Carrierbestellung lastCb = carrierService.findLastCB4Endstelle(endstelle.getId());
            if (lastCb != null) {
                result.add(lastCb);
            }
        }
        return result;
    }

    private void addAuftrag() {
        try {
            String input = MessageHelper.showInputDialog(getMainFrame(),
                    "Technische Auftragsnr. (Hurrican-Auftrag-Id), die dem Task zugeordnet werden soll:",
                    "Techn. Auftragsnr. zuordnen", JOptionPane.QUESTION_MESSAGE);
            if (StringUtils.isBlank(input)) {
                return;
            }
            Long auftragId = Long.valueOf(input);

            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
            if (auftragDaten == null) {
                throw new HurricanGUIException(String.format("Auftrag mit der ID %s konnte nicht ermittelt werden!",
                        auftragId));
            }
            Set<AuftragZuordnungDto> dtos = loadAuftragDtos(auftragDaten, Boolean.FALSE);
            auftraegeList.addAll(dtos);
            tbMdlAuftraege.addObjects(dtos);

            tbAuftraege.repaint();
        }
        catch (NumberFormatException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showInfoDialog(this, "Es wurde keine gültige Auftragnummer eingegeben.");
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
    }

    private void deleteAuftrag() {
        if (tbAuftraege.getSelectedRow() >= 0) {
            @SuppressWarnings("unchecked")
            AKMutableTableModel<AuftragZuordnungDto> tbMdl = (AKMutableTableModel<AuftragZuordnungDto>) tbAuftraege
                    .getModel();

            AuftragZuordnungDto auftrag = tbMdl.getDataAtRow(tbAuftraege.getSelectedRow());
            auftraegeList.remove(auftrag);
            tbMdlAuftraege.removeObject(auftrag);

            tbAuftraege.repaint();
        }
    }
}
