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
import de.augustakom.common.gui.swing.AKJCheckBox;
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
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog fuer die Bearbeitung/Neuanlage von Baugruppen
 *
 *
 */
public class HWBaugruppenEditDialog extends AbstractServiceOptionDialog {
    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenEditDialog.class);

    private static final List<HWSubrack> EMPTY_SUBRACK_LIST = new ArrayList<>(0);

    private AKReferenceField rfRack = null;
    private AKReferenceField rfSubrack;
    private AKReferenceField rfBgType = null;
    private AKJTextField tfInvNumber = null;
    private AKJTextField tfModNumber = null;
    private AKJTextField tfBemerkung = null;
    private AKJCheckBox cbEingebaut = null;

    private HWBaugruppe hwBgModel = null;
    private Long hvtStandortId = null;


    /**
     * Konstruktor
     */
    public HWBaugruppenEditDialog(HWBaugruppe hwBaugruppe, Long hvtStandortId) {
        super("de/augustakom/hurrican/gui/hvt/resources/HWBaugruppenEditDialog.xml");
        this.hwBgModel = hwBaugruppe;
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
        AKJLabel lblSubrack = getSwingFactory().createLabel("subrack");
        AKJLabel lblBgType = getSwingFactory().createLabel("type");
        AKJLabel lblInvNumber = getSwingFactory().createLabel("inv.number");
        AKJLabel lblModNumber = getSwingFactory().createLabel("mod.number");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");
        AKJLabel lblEingebaut = getSwingFactory().createLabel("eingebaut");

        rfBgType = getSwingFactory().createReferenceField("type");
        rfRack = getSwingFactory().createReferenceField("rack");
        rfSubrack = getSwingFactory().createReferenceField("subrack");
        tfInvNumber = getSwingFactory().createTextField("inv.number");
        tfModNumber = getSwingFactory().createTextField("mod.number");
        tfBemerkung = getSwingFactory().createTextField("bemerkung");
        cbEingebaut = getSwingFactory().createCheckBox("eingebaut");

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        left.add(lblRack, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        left.add(rfRack, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblSubrack, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(rfSubrack, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBgType, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(rfBgType, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblEingebaut, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbEingebaut, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblInvNumber, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfInvNumber, GBCFactory.createGBC(100, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblModNumber, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfModNumber, GBCFactory.createGBC(100, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBemerkung, GBCFactory.createGBC(0, 0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBemerkung, GBCFactory.createGBC(100, 0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 3, 8, 1, 1, GridBagConstraints.VERTICAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(left, BorderLayout.CENTER);

        rfRack.addObserver(new RackObserver());
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    public void showDetails() {
        if (hwBgModel != null) {
            rfRack.setReferenceId(hwBgModel.getRackId());
            rfBgType.setReferenceId(hwBgModel.getHwBaugruppenTyp().getId());
            tfInvNumber.setText(hwBgModel.getInventarNr());
            tfModNumber.setText(hwBgModel.getModNumber());
            tfBemerkung.setText(hwBgModel.getBemerkung());
            cbEingebaut.setSelected(hwBgModel.getEingebaut());

            setSubrackReferenceList();
            rfSubrack.setReferenceId(hwBgModel.getSubrackId());
        }
        else {
            hwBgModel = null;
            GuiTools.cleanFields(this);
        }
    }

    private void setSubrackReferenceList() {
        rfSubrack.setReferenceList(EMPTY_SUBRACK_LIST);
        if (hwBgModel.getRackId() != null) {
            try {
                HWService service = getCCService(HWService.class);
                List<HWSubrack> subracks = service.findSubracksForRack(hwBgModel.getRackId());
                rfSubrack.setReferenceList(subracks);
            }
            catch (Exception e) {
                LOGGER.error("Exception trying to determine possible Subracks", e);
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
                rfSubrack.setFindService(sfs);
                rfBgType.setFindService(sfs);

                // Ermittle alle HwRacks des HVT-Standorts
                HWService service = getCCService(HWService.class);
                List<HWRack> racks = service.findRacks(hvtStandortId);
                rfRack.setReferenceList(racks);
                List<HWBaugruppenTyp> baugruppenTypen = service.findAllBaugruppenTypen();
                rfBgType.setReferenceList(baugruppenTypen);

                // Hier kann noch nicht entschieden werden, welche Subracks moeglich sind
                rfSubrack.setReferenceList(EMPTY_SUBRACK_LIST);
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
        HWBaugruppe orig = new HWBaugruppe();
        try {
            if (hwBgModel == null) {
                hwBgModel = new HWBaugruppe();
            }
            BeanUtils.copyProperties(hwBgModel, orig);

            hwBgModel.setRackId(rfRack.getReferenceIdAs(Long.class));
            hwBgModel.setSubrackId(rfSubrack.getReferenceIdAs(Long.class));
            hwBgModel.setHwBaugruppenTyp((HWBaugruppenTyp) rfBgType.getReferenceObject());
            hwBgModel.setInventarNr(tfInvNumber.getText());
            hwBgModel.setModNumber(tfModNumber.getText());
            hwBgModel.setBemerkung(tfBemerkung.getText());
            hwBgModel.setEingebaut(cbEingebaut.isSelectedBoolean());

            HWService service = getCCService(HWService.class);
            service.saveHWBaugruppe(hwBgModel);

            // Schliesse Dialog
            prepare4Close();
            setValue(hwBgModel);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            BeanUtils.copyProperties(orig, hwBgModel);
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
     * Observer, der bei einer Aenderung der Auswahl des Racks die Liste der verfuegbaren Subracks updated.
     */
    private class RackObserver implements AKReferenceFieldObserver {
        @Override
        public void update(AKReferenceFieldEvent akReferenceFieldEvent) throws Exception {
            HWService service = getCCService(HWService.class);
            List<HWSubrack> subracks = service.findSubracksForRack((Long) rfRack.getReferenceId());
            rfSubrack.setReferenceList(subracks);
        }
    }
}




