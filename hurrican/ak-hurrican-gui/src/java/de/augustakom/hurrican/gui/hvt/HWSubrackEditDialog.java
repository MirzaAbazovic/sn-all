/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.12.2008 15:45:26
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKReferenceFieldEvent;
import de.augustakom.common.gui.swing.AKReferenceFieldObserver;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrackTyp;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog fuer die Bearbeitung/Neuanlage von Subracks
 *
 *
 */
public class HWSubrackEditDialog extends AbstractServiceOptionDialog {
    private static final Logger LOGGER = Logger.getLogger(HWSubrackEditDialog.class);

    private static final List<HWSubrackTyp> EMPTY_SUBRACK_TYPE_LIST = new ArrayList<>(0);

    private AKReferenceField rfRack = null;
    private AKReferenceField rfSubrackType = null;
    private AKJTextField tfModNumber;

    private HWSubrack hwSubrackModel = null;
    private Long hvtStandortId = null;


    /**
     * Konstruktor
     */
    public HWSubrackEditDialog(HWSubrack hwSubrack, Long hvtStandortId) {
        super("de/augustakom/hurrican/gui/hvt/resources/HWSubrackEditDialog.xml");
        this.hwSubrackModel = hwSubrack;
        this.hvtStandortId = hvtStandortId;
        createGUI();
        loadData();
        showDetails();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        AKJLabel lblRack = getSwingFactory().createLabel("rack");
        AKJLabel lblBgType = getSwingFactory().createLabel("type");
        AKJLabel lblModNumber = getSwingFactory().createLabel("mod.number");

        rfSubrackType = getSwingFactory().createReferenceField("type");
        rfRack = getSwingFactory().createReferenceField("rack");
        tfModNumber = getSwingFactory().createTextField("mod.number");

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        left.add(lblRack, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        left.add(rfRack, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBgType, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(rfSubrackType, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblModNumber, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfModNumber, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 3, 4, 1, 1, GridBagConstraints.VERTICAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(left, BorderLayout.CENTER);

        rfRack.addObserver(new RackObserver());
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    public void showDetails() {
        if (hwSubrackModel != null) {
            rfRack.setReferenceId(hwSubrackModel.getRackId());
            tfModNumber.setText(hwSubrackModel.getModNumber());

            setSubrackTypeReferenceList();
            rfSubrackType.setReferenceId(hwSubrackModel.getSubrackTyp().getId());
        }
        else {
            hwSubrackModel = null;
            GuiTools.cleanFields(this);
        }
    }

    private void setSubrackTypeReferenceList() {
        rfSubrackType.setReferenceList(EMPTY_SUBRACK_TYPE_LIST);
        HWRack rack = (HWRack) rfRack.getReferenceObject();
        if (rack != null) {
            try {
                HWService service = getCCService(HWService.class);
                List<HWSubrackTyp> subrackTypes = service.findAllSubrackTypes(rack.getRackTyp());
                rfSubrackType.setReferenceList(subrackTypes);
            }
            catch (Exception e) {
                LOGGER.error("Exception trying to determine possible Subrack Types", e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    public final void loadData() {
        if (hvtStandortId != null) {
            try {
                ISimpleFindService sfs = getCCService(QueryCCService.class);
                rfRack.setFindService(sfs);
                rfSubrackType.setFindService(sfs);

                // Ermittle alle HwRacks des HVT-Standorts
                HWService service = getCCService(HWService.class);
                List<HWRack> racks = service.findRacks(hvtStandortId);
                rfRack.setReferenceList(racks);

                // Noch keine zulaessigen Subrack-Typen bestimmbar
                rfSubrackType.setReferenceList(EMPTY_SUBRACK_TYPE_LIST);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        HWSubrack orig = new HWSubrack();
        try {
            if (hwSubrackModel == null) {
                hwSubrackModel = new HWSubrack();
            }
            BeanUtils.copyProperties(hwSubrackModel, orig);

            hwSubrackModel.setRackId(rfRack.getReferenceIdAs(Long.class));
            hwSubrackModel.setSubrackTyp((HWSubrackTyp) rfSubrackType.getReferenceObject());
            hwSubrackModel.setModNumber(tfModNumber.getText());

            HWService service = getCCService(HWService.class);
            service.saveHWSubrack(hwSubrackModel);

            // Schliesse Dialog
            prepare4Close();
            setValue(hwSubrackModel);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            BeanUtils.copyProperties(orig, hwSubrackModel);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
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

    /**
     * Observer, der bei einer Aenderung der Auswahl des Racks die Liste der verfuegbaren Subrack-Typen updated.
     */
    private class RackObserver implements AKReferenceFieldObserver {
        @Override
        public void update(AKReferenceFieldEvent akReferenceFieldEvent) throws Exception {
            HWRack rack = (HWRack) rfRack.getReferenceObject();
            if (rack != null) {
                HWService service = getCCService(HWService.class);
                List<HWSubrackTyp> subrackTypes = service.findAllSubrackTypes(rack.getRackTyp());
                rfSubrackType.setReferenceList(subrackTypes);
            }
        }
    }
}




