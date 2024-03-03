/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 01.06.2010 12:19:11
  */

package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog, um abzufragen, bei wie vielen aufeinanderfolgenden Ports das Layer2-Protokoll geaendert werden soll. <br> Der
 * Dialog fuehrt die Aenderung bei Bestaetigung auch direkt durch.
 */
public class ChangeLayer2ProtocolDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(ChangeLayer2ProtocolDialog.class);
    private static final long serialVersionUID = 1119805708577878735L;

    private AKJTextField tfCount;
    private AKJComboBox cbLayer2;

    private final Equipment firstPort;

    private CCAuftragService ccAuftragService;
    private RangierungsService rangierungsService;

    public ChangeLayer2ProtocolDialog(Equipment equipment) {
        super("de/augustakom/hurrican/gui/auftrag/resources/ChangeLayer2ProtocolDialog.xml");
        firstPort = equipment;
        initServices();
        createGUI();
    }

    /**
     * @throws ServiceNotFoundException
     */
    private void initServices() {
        try {
            ccAuftragService = getCCService(CCAuftragService.class);
            rangierungsService = getCCService(RangierungsService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        AKJLabel lblCount = getSwingFactory().createLabel("count");
        AKJLabel lblLayer2 = getSwingFactory().createLabel("layer2");

        tfCount = getSwingFactory().createTextField("count");
        cbLayer2 = getSwingFactory().createComboBox("layer2", new AKCustomListCellRenderer<>(Schicht2Protokoll.class, Enum::name));
        cbLayer2.addItems(Arrays.asList(Schicht2Protokoll.values()), true);
        cbLayer2.setSelectedItem(firstPort.getSchicht2Protokoll());

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(lblCount, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(tfCount, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblLayer2, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(cbLayer2, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 3, 1, 1, GridBagConstraints.BOTH));
    }

    @Override
    protected void doSave() {
        try {
            int numPorts = tfCount.getTextAsInt(0);
            if (numPorts <= 0) {
                MessageHelper.showMessageDialog(getMainFrame(), "Ungültige Anzahl an Ports", "Fehler", ERROR_MESSAGE);
                return;
            }
            if (cbLayer2.getSelectedItem() == null) {
                MessageHelper.showMessageDialog(getMainFrame(), "Bitte ein Schicht2-Protokoll auswählen", "Fehler", ERROR_MESSAGE);
                return;
            }
            Long taifunOrderID = null;
            List<AuftragDaten> auftragDatenList = ccAuftragService.findAuftragDatenByEquipment(firstPort.getId());
            for (AuftragDaten auftragDaten : auftragDatenList) {
                if (auftragDaten.getAuftragStatusId() < AuftragStatus.AUFTRAG_GEKUENDIGT) {
                    taifunOrderID = auftragDaten.getAuftragNoOrig();
                }
            }
            List<Equipment> consecutivePorts = new ArrayList<>();
            consecutivePorts.add(firstPort);
            if (numPorts > 1) {
                try {
                    consecutivePorts = rangierungsService.findConsecutivePorts(firstPort, numPorts);
                    if (CollectionUtils.isEmpty(consecutivePorts)) {
                        throw new FindException("Es konnten nicht genügend zusammenhängende Ports ermittelt werden!");
                    }
                }
                catch (FindException e) {
                    MessageHelper.showMessageDialog(getMainFrame(), e.getMessage(), "Fehler", ERROR_MESSAGE);
                    return;
                }
                for (Equipment equipment : consecutivePorts) {
                    auftragDatenList.addAll(ccAuftragService.findAuftragDatenByEquipment(equipment.getId()));
                }
                for (AuftragDaten auftragDaten : auftragDatenList) {
                    if (auftragDaten.getAuftragStatusId() < AuftragStatus.AUFTRAG_GEKUENDIGT
                            && NumberTools.notEqual(taifunOrderID, auftragDaten.getAuftragNoOrig())) {
                        MessageHelper.showMessageDialog(getMainFrame(), "Mindestens ein betroffener Port gehört zu einem anderen Auftrag", "Fehler", ERROR_MESSAGE);
                        return;
                    }

                }
            }
            rangierungsService.setLayer2ProtocolForPorts(consecutivePorts, (Schicht2Protokoll) cbLayer2.getSelectedItem());
            prepare4Close();
            setValue(firstPort);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
        // NOP
    }

    @Override
    public void update(Observable o, Object arg) {
        // NOP
    }

}
