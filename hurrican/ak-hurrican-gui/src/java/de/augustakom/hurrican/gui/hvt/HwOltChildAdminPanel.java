/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.04.2014 15:46
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import com.google.common.base.Function;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AdministrationMouseListener;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 *
 */
@ObjectsAreNonnullByDefault
public abstract class HwOltChildAdminPanel<T extends HWOltChild> extends AbstractAdminPanel {
    private static final Logger LOGGER = Logger.getLogger(HwOltChildAdminPanel.class);
    private static final long serialVersionUID = -615881708698511286L;

    protected AKReferenceField rfOLT;
    protected AKJTextField tfSerialNo;
    protected AKJTextField tfIP;
    protected AKJTextField tfOLTFrame;
    protected AKJTextField tfOLTSubrack;
    protected AKJTextField tfOLTSlot;
    protected AKJTextField tfOLTGponPort;
    protected AKJTextField tfOLTGponId;
    protected AKJTextField tfHwRackType;
    protected AKJDateComponent dcFreigabe;
    protected AKJButton btnCpsInit;
    protected AKJButton btnCpsModify;
    protected AKJButton btnCpsDelete;
    protected JComboBox<String> tddCombobox;
    protected AKJLabel tddLabel;

    protected T rack;

    protected HWService hwService;
    protected HVTService hvtService;
    protected CPSService cpsService;
    protected QueryCCService queryCCService;
    protected FTTXHardwareService fttxHardwareService;
    protected FeatureService featureService;

    public HwOltChildAdminPanel(final T rack) {
        super("de/augustakom/hurrican/gui/hvt/resources/HWOltChildAdminPanel.xml");
        this.rack = rack;
        initServices();
        createGUI();
        loadData();
        showDetails(rack);
    }

    private void initServices() {
        try {
            hwService = getCCService(HWService.class);
            hvtService = getCCService(HVTService.class);
            cpsService = getCCService(CPSService.class);
            queryCCService = getCCService(QueryCCService.class);
            fttxHardwareService = getCCService(FTTXHardwareService.class);
            featureService = getCCService(FeatureService.class);
        }
        catch (ServiceNotFoundException ex) {
            LOGGER.error(ex.getMessage());
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblOLT = getSwingFactory().createLabel("olt");
        AKJLabel lblSerialNo = getSwingFactory().createLabel("serial.no");
        AKJLabel lblOLTFrame = getSwingFactory().createLabel("olt.frame");
        AKJLabel lblOLTSubrack = getSwingFactory().createLabel("olt.subrack");
        AKJLabel lblOLTSlot = getSwingFactory().createLabel("olt.slot");
        AKJLabel lblOLTGponPort = getSwingFactory().createLabel("olt.gpon.port");
        AKJLabel lblOLTGponId = getSwingFactory().createLabel("olt.gpon.id");
        AKJLabel lblFreigabe = getSwingFactory().createLabel("freigabe");
        AKJLabel lblRackType = new AKJLabel(getRackTypeLabelTxt());


        rfOLT = getSwingFactory().createReferenceField("olt");
        tfSerialNo = getSwingFactory().createTextField("serial.no");
        tfIP = getSwingFactory().createTextField("ip.address");
        GuiTools.addAction2ComponentPopupMenu(tfIP, new CalculateAndShowOntChildIpAction(),
                new AdministrationMouseListener(), true);
        tfOLTFrame = getSwingFactory().createTextField("olt.frame");
        tfOLTSubrack = getSwingFactory().createTextField("olt.subrack");
        tfOLTSlot = getSwingFactory().createTextField("olt.slot");
        tfOLTGponPort = getSwingFactory().createTextField("olt.gpon.port");
        tfOLTGponId = getSwingFactory().createTextField("olt.gpon.id");
        tfHwRackType = getSwingFactory().createTextField("rack.type");

        dcFreigabe = getSwingFactory().createDateComponent("freigabe");


        AKJPanel left = new AKJPanel(new GridBagLayout(), getRackDisplayName());
        // @formatter:off
        int count = 0;
        left.add(new AKJPanel(), GBCFactory.createGBC(  0, 0, 0, count++, 1, 1, GridBagConstraints.NONE));
        left.add(lblOLT,         GBCFactory.createGBC(  0, 0, 1, count, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(  0, 0, 2, count, 1, 1, GridBagConstraints.NONE));
        left.add(rfOLT,          GBCFactory.createGBC(100, 0, 3, count++, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblSerialNo,    GBCFactory.createGBC(  0, 0, 1, count, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfSerialNo,     GBCFactory.createGBC(100, 0, 3, count++, 1, 1, GridBagConstraints.HORIZONTAL));
        count = addIpFields(left, count);
        left.add(lblFreigabe,    GBCFactory.createGBC(  0, 0, 1, count, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcFreigabe,     GBCFactory.createGBC(100, 0, 3, count++, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(  0, 0, 4, 1, 1, 1, GridBagConstraints.NONE));
        count = addTddProfileFields(left, rack.getId(), count);
        int i = count;
        for (final CustomFormElement customFormElement : getCustomFormElements()) {
            i++;
            left.add(customFormElement.label,     GBCFactory.createGBC(  0, 0, 1, i, 1, 1, GridBagConstraints.HORIZONTAL));
            left.add(customFormElement.component, GBCFactory.createGBC(100, 0, 3, i, 1, 1, GridBagConstraints.HORIZONTAL));
        }
        left.add(lblOLTFrame,          GBCFactory.createGBC(  0, 0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(),       GBCFactory.createGBC(  0, 0, 6, 1, 1, 1, GridBagConstraints.NONE));
        left.add(tfOLTFrame,           GBCFactory.createGBC(100, 0, 7, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblOLTSubrack,        GBCFactory.createGBC(  0, 0, 5, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfOLTSubrack,         GBCFactory.createGBC(100, 0, 7, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblOLTSlot,           GBCFactory.createGBC(  0, 0, 5, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfOLTSlot,            GBCFactory.createGBC(100, 0, 7, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblOLTGponPort,       GBCFactory.createGBC(  0, 0, 5, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfOLTGponPort,        GBCFactory.createGBC(100, 0, 7, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblOLTGponId,         GBCFactory.createGBC(  0, 0, 5, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfOLTGponId,          GBCFactory.createGBC(100, 0, 7, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblRackType,          GBCFactory.createGBC(  0, 0, 5, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfHwRackType,         GBCFactory.createGBC(100, 0, 7, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(getCpsButtonsPanel(), GBCFactory.createGBC(100, 0, 1, 7, 7, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        this.setLayout(new BorderLayout());
        this.add(left, BorderLayout.CENTER);
    }

    protected int addIpFields(AKJPanel left, int count) {
        AKJLabel lblIP = getSwingFactory().createLabel("ip.address");
        left.add(lblIP, GBCFactory.createGBC(0, 0, 1, count, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfIP, GBCFactory.createGBC(100, 0, 3, count++, 1, 1, GridBagConstraints.HORIZONTAL));
        return count;
    }

    /**
     * @see de.augustakom.hurrican.gui.hvt.HwDpuAdminPanel#addTddProfileFields(AKJPanel, Long, int)
     */
    protected int addTddProfileFields(AKJPanel left, Long rackId, int count) {
        // TDD-Profil wird aktuell nur bei DPU angelegt
        //HwDpuAdminPanel
        return count;
    }



    private AKJPanel getCpsButtonsPanel() {
        btnCpsInit = getButtonCpsInit();
        btnCpsModify = getButtonCpsModify();
        btnCpsDelete = getButtonCpsDelete();

        final AKJPanel buttonPanel = new AKJPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder());
        buttonPanel.add(btnCpsInit, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        if (btnCpsModify != null && btnCpsDelete != null) {
            buttonPanel.add(btnCpsModify, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            buttonPanel.add(btnCpsDelete, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        }
        return buttonPanel;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        try {
            rfOLT.setReferenceList(getReferenceList());
            rfOLT.setFindService(queryCCService);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    private List<HWRack> getReferenceList() throws FindException {
        HVTStandort rackStandort = hvtService.findHVTStandort(rack.getHvtIdStandort());
        List<HWRack> oltsAndDslams = new ArrayList<>();
        if (rackStandort != null && rackStandort.getBetriebsraumId() != null) {
            oltsAndDslams = hwService.findAllRacksForFtth(rackStandort.getBetriebsraumId());
        }
        else {
            // Fallback: Alle! moeglichen HW-Racks (Typ OLT + DSLAM) laden
            List<HWOlt> olts = hwService.findRacksByType(HWOlt.class);
            List<HWDslam> dslams = hwService.findRacksByType(HWDslam.class);
            if (olts != null) {
                oltsAndDslams.addAll(olts);
            }
            if (dslams != null) {
                oltsAndDslams.addAll(dslams);
            }
        }
        if (rack.getOltRackId() != null) {
            HWRack parent = hwService.findRackById(rack.getOltRackId());
            if (!oltsAndDslams.contains(parent)) {
                oltsAndDslams.add(parent);
            }
        }

        return oltsAndDslams;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if ((details != null)) {
            rack = (T) details;
            rfOLT.setReferenceId(rack.getOltRackId());

            final String serialNo = rack.getSerialNo();
            tfSerialNo.setText(serialNo);

            // for ONT & DPO disable serial no if it is already given
            if (featureService.isFeatureOnline(Feature.FeatureName.SERIALNUMBER_FFM_SERVICE)) {
                tfSerialNo.setEnabled(StringUtils.isEmpty(serialNo));
            }

            if (rack instanceof HWMdu) {
                tfIP.setText(((HWMdu) rack).getIpAddress());
            } else if (rack instanceof HWDpu) {
                tfIP.setText(((HWDpu) rack).getIpAddress());
            }
            tfOLTFrame.setText(rack.getOltFrame());
            tfOLTSubrack.setText(rack.getOltSubrack());
            tfOLTSlot.setText(rack.getOltSlot());
            tfOLTGponPort.setText(rack.getOltGPONPort());
            tfOLTGponId.setText(rack.getOltGPONId());
            tfHwRackType.setText(getHwRackTypeFromRack(rack));
            dcFreigabe.setDate(rack.getFreigabe());
            for (final CustomFormElement customFormElement : getCustomFormElements()) {
                customFormElement.setValueToComponent.apply(rack);
            }
        }
        else {
            rack = null;
            GuiTools.cleanFields(this);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        btnCpsInit.setEnabled(Boolean.FALSE);
        if (btnCpsModify != null) {
            btnCpsModify.setEnabled(Boolean.FALSE);
        }
        if (btnCpsDelete != null) {
            btnCpsDelete.setEnabled(Boolean.FALSE);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        boolean isNew = (rack != null) && (rack.getId() == null);
        try {
            if (rack == null) {
                throw new HurricanGUIException("Hardware-Rack nicht gesetzt!");
            }

            Long oltRefId = rfOLT.getReferenceIdAs(Long.class);
            if (oltRefId == null) {
                throw new HurricanGUIException("OLT nicht gesetzt!");
            }
            rack.setOltRackId(rfOLT.getReferenceIdAs(Long.class));
            rack.setSerialNo(tfSerialNo.getText(null));
            if (rack instanceof HWMdu) {
                ((HWMdu) rack).setIpAddress(tfIP.getText(null));
            } else if (rack instanceof HWDpu) {
                ((HWDpu) rack).setIpAddress(tfIP.getText(null));
                ((HWDpu) rack).setTddProfil((String)tddCombobox.getSelectedItem());
            }
            rack.setOltFrame(tfOLTFrame.getText(null));
            rack.setOltSubrack(tfOLTSubrack.getText(null));
            rack.setOltSlot(tfOLTSlot.getText(null));
            rack.setOltGPONPort(tfOLTGponPort.getText(null));
            rack.setOltGPONId(tfOLTGponId.getText(null));
            rack.setFreigabe(dcFreigabe.getDate(null));
            setHwRackTypeForRack(rack, tfHwRackType.getText());
            for (final CustomFormElement customFormElement : getCustomFormElements()) {
                customFormElement.setValueToModel.apply(this.rack);
            }
            hwService.saveHWRack(rack);
            btnCpsInit.setEnabled(Boolean.TRUE);
            if (btnCpsModify != null) {
                btnCpsModify.setEnabled(Boolean.TRUE);
            }
            if (btnCpsDelete != null) {
                btnCpsDelete.setEnabled(Boolean.TRUE);
            }
        }
        catch (Exception e) {
            if (isNew) {
                // Wenn bspw. ein Constraint zuschlägt, Datensatz wieder als 'transient' markieren
                rack.setId(null);
            }

            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    protected abstract String getRackDisplayName();

    protected abstract String getRackTypeLabelTxt();

    protected abstract String getHwRackTypeFromRack(T rack);

    protected abstract T setHwRackTypeForRack(T rack, String txt);

    protected abstract AKJButton getButtonCpsInit();

    protected abstract AKJButton getButtonCpsModify();

    protected abstract AKJButton getButtonCpsDelete();

    protected List<CustomFormElement> getCustomFormElements() {
        return Collections.emptyList();
    }

    class CalculateAndShowOntChildIpAction extends AKAbstractAction {
        private static final long serialVersionUID = -2301282124863769355L;

        public CalculateAndShowOntChildIpAction() {
            super();
            setName("IP-Adresse berechnen");
            setTooltip("Berechnet die IP-Adresse fuer das Rack neu und zeigt sie in einem Dialog an");
            setActionCommand("calculate.mdu.ip");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                HWRack oltRack = hwService.findRackById(rack.getOltRackId());
                if ((oltRack != null) && (oltRack instanceof HWOlt)) {
                    String ipAddress = hwService.generateOltChildIp((HWOlt) oltRack, rack);
                    MessageHelper.showInfoDialog(getMainFrame(),
                            "Berechnete IP-Adresse fuer die MDU: {0}", "IP-Adresse",
                            new Object[] { ipAddress }, true);
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }

    protected final class CustomFormElement {
        public final Component component;
        public final AKJLabel label;
        public final Function<T, T> setValueToModel;
        public final Function<T, Component> setValueToComponent;

        public CustomFormElement(final Component component, final AKJLabel label,
                Function<T, T> setValueToModel, Function<T, Component> setValueToComponent) {
            this.component = component;
            this.label = label;
            this.setValueToModel = setValueToModel;
            this.setValueToComponent = setValueToComponent;
        }
    }
}
