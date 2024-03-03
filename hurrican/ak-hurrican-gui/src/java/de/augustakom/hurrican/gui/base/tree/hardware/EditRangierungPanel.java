/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 19.07.2010
  */
package de.augustakom.hurrican.gui.base.tree.hardware;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJComboBoxCellEditor;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.tree.hardware.node.EquipmentNodeContainer;
import de.augustakom.hurrican.gui.tools.physik.EquipmentChangeDialog;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.view.RangierungWithEquipmentView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;
import de.augustakom.hurrican.service.cc.RangierungsService;

public class EditRangierungPanel extends AbstractServicePanel {

    private static final Logger LOGGER = Logger.getLogger(EditRangierungPanel.class);

    private static final String BTN_SAVE = "save";
    private static final String BTN_RANG_FREIGEBEN = "rangierungen.freigeben";
    private static final String BTN_RANG_AUFBRECHEN = "rangierungen.aufbrechen";

    private AKJTable tbRangierungen;
    private AKTableModelXML<RangierungWithEquipmentView> tbMdlRangierungen;
    private AKJComboBox cbPhysikTyp;

    private final List<EquipmentNodeContainer> nodes;

    private RangierungsService rangierungsService;
    private PhysikService physikService;

    public EditRangierungPanel(List<EquipmentNodeContainer> nodes) {
        super("de/augustakom/hurrican/gui/base/tree/hardware/resources/EditRangierungPanel.xml");
        this.nodes = nodes;
        createGUI();
        initServices();
        loadData(nodes);
    }

    private void initServices() {
        try {
            rangierungsService = getCCService(RangierungsService.class);
            physikService = getCCService(PhysikService.class);
        }
        catch (Exception e) {
            LOGGER.error(e, e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    @Override
    protected final void createGUI() {
        AKJButton btnSave = getSwingFactory().createButton(BTN_SAVE, getActionListener());
        AKJButton btnRangFreigeben = getSwingFactory().createButton(BTN_RANG_FREIGEBEN, getActionListener());
        AKJButton btnRangAufbrechen = getSwingFactory().createButton(BTN_RANG_AUFBRECHEN, getActionListener());

        cbPhysikTyp = getSwingFactory().createComboBox("physiktyp", new AKCustomListCellRenderer<>(PhysikTyp.class, PhysikTyp::getName));

        tbMdlRangierungen = new AKTableModelXML<>(
                "de/augustakom/hurrican/gui/base/tree/hardware/resources/RangierungTableModel.xml");
        tbRangierungen = new AKJTable(tbMdlRangierungen);
        tbRangierungen.attachSorter();
        tbRangierungen.setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        tbRangierungen.addPopupAction(new PortChangeAction());
        tbRangierungen.setDefaultEditor(PhysikTyp.class, new AKJComboBoxCellEditor(cbPhysikTyp));
        tbRangierungen.setDefaultRenderer(PhysikTyp.class, new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText(value == null ? "" : ((PhysikTyp) value).getName());
            }
        });
        AKJScrollPane spEquipments = new AKJScrollPane(tbRangierungen);
        spEquipments.setPreferredSize(new Dimension(400, 400));
        setLayout(new GridBagLayout());

        add(spEquipments, GBCFactory.createGBC(100, 100, 0, 0, 4, 1, GridBagConstraints.BOTH));
        add(btnSave, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        add(btnRangFreigeben, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        add(btnRangAufbrechen, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.BOTH));

        manageGUI(btnSave, btnRangFreigeben, btnRangAufbrechen);
    }

    private void loadData(List<EquipmentNodeContainer> nodes) {
        try {
            tbMdlRangierungen.setData(null);

            Set<Long> rangierungIds = new HashSet<>();
            rangierungIds.addAll(getRangierungIds(nodes, rangierungsService, true));
            if (rangierungIds.isEmpty()) {
                rangierungIds.addAll(getRangierungIds(nodes, rangierungsService, false));
            }
            tbMdlRangierungen.setData(rangierungsService.findRangierungWithEquipmentViews(rangierungIds));

            List<PhysikTyp> physikTypen = physikService.findPhysikTypen();
            cbPhysikTyp.addItems(physikTypen, true);
        }
        catch (Exception e) {
            LOGGER.error(e, e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    private Set<Long> getRangierungIds(List<EquipmentNodeContainer> nodes, RangierungsService rs, boolean findEqIn)
            throws FindException {

        Set<Long> result = new HashSet<>();

        for (EquipmentNodeContainer container : nodes) {
            for (Equipment equipment : container.getEquipments()) {
                Rangierung rangierung = rs.findRangierung4Equipment(equipment.getId(), findEqIn);
                if (rangierung != null) {
                    result.add(rangierung.getId());
                }
            }
        }
        return result;
    }

    @Override
    protected void execute(String command) {
        if (BTN_SAVE.equals(command)) {
            saveData();
        }
        else if (BTN_RANG_FREIGEBEN.equals(command)) {
            rangierungenFreigeben();
            loadData(nodes);
        }
        else if (BTN_RANG_AUFBRECHEN.equals(command)) {
            rangierungenAufbrechen();
            loadData(nodes);
        }
    }

    private void saveData() {
        Collection<RangierungWithEquipmentView> data = tbMdlRangierungen.getModified();
        if (!data.isEmpty()) {
            try {
                AKUserService us = getAuthenticationService(AKUserService.class);
                AKUser user = us.findUserBySessionId(HurricanSystemRegistry.instance().getSessionId());

                RangierungsService rangierungsService = getCCService(RangierungsService.class);
                for (RangierungWithEquipmentView view : data) {
                    Rangierung rangierung = rangierungsService.findRangierung(view.getRangierId());
                    rangierung.setPhysikTypId(view.getPhysikTyp().getId());
                    rangierung.setFreigegeben(view.getFreigegeben());
                    rangierung.setFreigabeAb(view.getFreigabeAb());
                    rangierung.setOntId(view.getOntId());
                    if (!StringUtils.equals(rangierung.getBemerkung(), view.getBemerkung())) {
                        // Beim manuellen Editieren sollen auch Bemerkungen mit "Querverbindung:*" ersetzt werden können
                        rangierung.removeBemerkung();
                        rangierung.setBemerkung(view.getBemerkung());
                    }
                    rangierung.setUserW(user.getLoginName());
                    rangierungsService.saveRangierung(rangierung, false);
                    if (view.getRangierId2() != null) {
                        Rangierung rangierungAdd = rangierungsService.findRangierung(view.getRangierId2());
                        rangierungAdd.setPhysikTypId(view.getPhysikTyp2().getId());
                        rangierungAdd.setFreigegeben(view.getFreigegeben2());
                        rangierungAdd.setFreigabeAb(view.getFreigabeAb2());
                        rangierungAdd.setUserW(user.getLoginName());
                        rangierungsService.saveRangierung(rangierungAdd, false);
                    }
                }
                MessageHelper.showInfoDialog(this, "Rangierungen erfolgreich gespeichert.");
            }
            catch (Exception e) {
                LOGGER.error("Konnte Rangierungen nicht speichern", e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
        else {
            MessageHelper.showInfoDialog(this, "Keine geänderten Rangierungen gefunden.");
        }
    }

    private void rangierungenFreigeben() {
        try {
            List<RangierungWithEquipmentView> released = new ArrayList<>();
            List<RangierungWithEquipmentView> notReleased = new ArrayList<>();

            RangierungFreigabeService rangierungFreigabeService = getCCService(RangierungFreigabeService.class);
            for (RangierungWithEquipmentView view : getSelectedRows()) {
                if (rangierungFreigabeService.rangierungenFreigeben(view.getRangierId(), view.getRangierId2(),
                        HurricanSystemRegistry.instance().getSessionId())) {
                    released.add(view);
                }
                else {
                    notReleased.add(view);
                }
            }

            showInfoMsg4RangierungenIfNotEmpty(notReleased,
                    "Folgende Rangierungen konnten nicht freigegeben werden, da sie bereits freigegeben"
                            + ", noch einer Endstelle zugeordnet sind oder das FreigabeAb-Datum in der Zukunft liegt:"
            );
            showInfoMsg4RangierungenIfNotEmpty(released, "Folgende Rangierungen wurden freigegeben:");
        }
        catch (Exception ex) {
            LOGGER.error("Konnte Rangierungen nicht freigeben.", ex);
            MessageHelper.showErrorDialog(this, ex);
        }
    }

    private void rangierungenAufbrechen() {
        try {
            Object selection = MessageHelper.showInputDialog(getMainFrame(), Arrays.asList(EqStatus.values()),
                    new AKCustomListCellRenderer<>(EqStatus.class, EqStatus::toString),
                    "Auswahl Eq-Status", "Bitte künftigen Status der aufzubrechenden Equipments wählen.", "Eq-Status:");
            if (selection instanceof EqStatus) {
                EqStatus newEqStatus = (EqStatus) selection;

                List<RangierungWithEquipmentView> done = new ArrayList<>();
                List<RangierungWithEquipmentView> notDone = new ArrayList<>();

                RangierungsService rs = getCCService(RangierungsService.class);
                for (RangierungWithEquipmentView view : getSelectedRows()) {

                    Rangierung rangierung = rs.findRangierung(view.getRangierId());
                    Rangierung rangierung2 = rs.findRangierung(view.getRangierId2());

                    if ((rangierung.getEsId() == null) && ((rangierung2 == null) || (rangierung2.getEsId() == null))) {

                        // Zusätzliche Warnung, wenn Freigegeben-Status der Rangierung nicht freigegeben
                        if (!((rangierung.isRangierungFrei(false)) && ((rangierung2 == null) || (rangierung2.isRangierungFrei(false))))) {
                            int result = MessageHelper.showWarningDialog(this, "Der Freigegeben-Status der Rangierung(en) {0}{1}"
                                    + " steht auf {2}{3}. Wollen Sie diese Rangierung(en) trotzdem aufbrechen?"
                                    , null, JOptionPane.YES_NO_OPTION
                                    , rangierung.getId().toString(), (rangierung2 == null) ? "" : " und " + rangierung2.getId()
                                    , rangierung.getFreigegeben(), (rangierung2 == null) ? "" : " bzw. " + rangierung2.getFreigegeben());
                            if (result == JOptionPane.NO_OPTION) {
                                notDone.add(view);
                                continue;
                            }
                        }

                        rs.breakRangierungUsingNewEqStatus(rangierung, newEqStatus, HurricanSystemRegistry.instance().getSessionId());
                        if (rangierung2 != null) {
                            rs.breakRangierungUsingNewEqStatus(rangierung2, newEqStatus, HurricanSystemRegistry.instance().getSessionId());
                        }
                        done.add(view);
                    }
                    else {
                        notDone.add(view);
                    }
                }

                showInfoMsg4RangierungenIfNotEmpty(notDone, "Folgende Rangierungen konnten nicht aufgebrochen werden"
                        + ", da sie entweder nicht frei sind oder die Aktion vom Benutzer abgebrochen wurde:");
                showInfoMsg4RangierungenIfNotEmpty(done, "Folgende Rangierungen wurden aufgebrochen:");
            }
            else {
                MessageHelper.showInfoDialog(this, "Es wurden keine Rangierungen aufgebrochen.");
            }
        }
        catch (Exception ex) {
            LOGGER.error("Konnte Rangierungen nicht aufbrechen.", ex);
            MessageHelper.showErrorDialog(this, ex);
        }
    }

    /**
     * @return Liste mit selekierten Rangierungen der Tabelle, niemals {@code null}
     */
    private List<RangierungWithEquipmentView> getSelectedRows() {
        List<RangierungWithEquipmentView> result = new ArrayList<>();
        if (tbRangierungen.getSelectedRowCount() > 0) {
            int[] rows = tbRangierungen.getSelectedRows();
            for (int currRow : rows) {
                @SuppressWarnings("unchecked")
                RangierungWithEquipmentView rangierung = ((AKMutableTableModel<RangierungWithEquipmentView>) tbRangierungen.getModel()).getDataAtRow(currRow);
                result.add(rangierung);
            }
        }
        else {
            MessageHelper.showInfoDialog(this, "Bitte wählen Sie zuerst eine oder mehrere Rangierungen aus.");
        }
        return result;
    }

    private void showInfoMsg4RangierungenIfNotEmpty(List<RangierungWithEquipmentView> rangierungen, String msgText) {
        if (!rangierungen.isEmpty()) {
            StringBuilder msg = new StringBuilder(msgText);
            for (RangierungWithEquipmentView view : rangierungen) {
                msg.append("\n  - ").append(view.getRangierId());
                if (view.getRangierId2() != null) {
                    msg.append(" und ").append(view.getRangierId2());
                }
                msg.append(" (Endstelle-Id: ").append(view.getEndstelleId()).append(")");
            }
            MessageHelper.showInfoDialog(this, msg.toString());
        }
    }

    @Override
    public String getName() {
        return getSwingFactory().getText("border.title");
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    private class PortChangeAction extends AKAbstractAction {

        public PortChangeAction() {
            super("Equipment austauschen");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                RangierungWithEquipmentView view = (RangierungWithEquipmentView) getValue(OBJECT_4_ACTION);
                Rangierung rangierung = rangierungsService.findRangierungWithEQ(view.getRangierId());
                Rangierung rangierungAdd = rangierungsService.findRangierungWithEQ(view.getRangierId2());
                EquipmentChangeDialog dlg = new EquipmentChangeDialog(rangierung, rangierungAdd);
                DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                loadData(nodes);
            }
            catch (Exception ex) {
                LOGGER.error(ex, ex);
                MessageHelper.showErrorDialog(EditRangierungPanel.this, ex);
            }
        }

    }
}
