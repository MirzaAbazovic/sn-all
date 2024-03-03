/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.04.2010 15:33:48
 */
package de.augustakom.hurrican.gui.hvt.hardware;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog, um eine neue Planung fuer einen Baugruppen-Schwenk anzulegen.
 */
public class CreateHWBaugruppenChangeDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(CreateHWBaugruppenChangeDialog.class);

    private static final String STANDORT = "standort";
    private static final String CHANGE_TYPE = "change.type";
    private static final String PHYSIKTYP_NEW = "physiktyp.new";
    private static final String PLANNED_DATE = "planned.date";

    private AKReferenceField rfStandort;
    private AKReferenceField rfChangeType;
    private AKReferenceField rfPhysikTypNew;
    private AKJDateComponent dcPlanned;

    private HVTService hvtService;
    private HWBaugruppenChangeService hwBaugruppenChangeService;

    /**
     * Default-Const.
     */
    public CreateHWBaugruppenChangeDialog() {
        super("de/augustakom/hurrican/gui/hvt/hardware/resources/CreateHWBaugruppenChangeDialog.xml");
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblStandort = getSwingFactory().createLabel(STANDORT);
        AKJLabel lblChangeType = getSwingFactory().createLabel(CHANGE_TYPE);
        AKJLabel lblPhysikTypNew = getSwingFactory().createLabel(PHYSIKTYP_NEW);
        AKJLabel lblPlanned = getSwingFactory().createLabel(PLANNED_DATE);

        rfStandort = getSwingFactory().createReferenceField(STANDORT);
        rfChangeType = getSwingFactory().createReferenceField(CHANGE_TYPE);
        rfPhysikTypNew = getSwingFactory().createReferenceField(PHYSIKTYP_NEW);
        dcPlanned = getSwingFactory().createDateComponent(PLANNED_DATE);

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblStandort, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        child.add(rfStandort, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblChangeType, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(rfChangeType, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblPhysikTypNew, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(rfPhysikTypNew, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblPlanned, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(dcPlanned, GBCFactory.createGBC(0, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 5, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            hvtService = getCCService(HVTService.class);
            hwBaugruppenChangeService = getCCService(HWBaugruppenChangeService.class);

            ISimpleFindService simpleFindService = getCCService(QueryCCService.class);
            rfStandort.setFindService(simpleFindService);

            Reference changeTypeExample = new Reference(Reference.REF_TYPE_PORT_SCHWENK);
            rfChangeType.setReferenceFindExample(changeTypeExample);
            rfChangeType.setFindService(simpleFindService);

            rfPhysikTypNew.setFindService(simpleFindService);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void doSave() {
        try {
            HVTGruppeStdView stdView = rfStandort.getReferenceObjectAs(HVTGruppeStdView.class);
            HVTStandort hvtStandort = (stdView != null)
                    ? hvtService.findHVTStandort(stdView.getHvtIdStandort())
                    : null;

            HWBaugruppenChange newChange = new HWBaugruppenChange();
            newChange.setHvtStandort(hvtStandort);
            newChange.setChangeType(rfChangeType.getReferenceObjectAs(Reference.class));
            newChange.setPhysikTypNew(rfPhysikTypNew.getReferenceObjectAs(PhysikTyp.class));
            newChange.setPlannedDate(dcPlanned.getDate(new Date()));
            newChange.setPlannedFrom(HurricanSystemRegistry.instance().getCurrentUser().getNameAndFirstName());

            hwBaugruppenChangeService.saveHWBaugruppenChange(newChange);

            prepare4Close();
            setValue(newChange);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}


