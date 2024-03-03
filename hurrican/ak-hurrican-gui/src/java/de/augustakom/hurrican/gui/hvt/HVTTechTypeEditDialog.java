/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.01.2011 11:49:01
 */

package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.HVTStandortTechType;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog fuer die Bearbeitung/Neuanlage von Technologietypen
 *
 *
 */
public class HVTTechTypeEditDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(HVTTechTypeEditDialog.class);

    private static final String TECHNOLOGY_TYPE_REFERENCE = "technology.type.reference";
    private static final String AVAILABLE_FROM = "available.from";
    private static final String AVAILABLE_TO = "available.to";
    private static final String TITLE = "title";

    private HVTStandortTechType hvtStandortTechType = null;
    private Long hvtStandortId = null;

    private AKReferenceField rfTechnologyType = null;
    private AKJDateComponent dcAvailableFrom = null;
    private AKJDateComponent dcAvailableTo = null;


    public HVTTechTypeEditDialog(HVTStandortTechType hvtStandortTechType, Long hvtStandortId) {
        super("de/augustakom/hurrican/gui/hvt/resources/HVTTechTypeEditDialog.xml");
        this.hvtStandortTechType = hvtStandortTechType;
        this.hvtStandortId = hvtStandortId;
        createGUI();
        loadData();
        showDetails();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText(TITLE));
        AKJLabel lblTechTypeRef = getSwingFactory().createLabel(TECHNOLOGY_TYPE_REFERENCE);
        AKJLabel lblAvailableFrom = getSwingFactory().createLabel(AVAILABLE_FROM);
        AKJLabel lblAvailableTo = getSwingFactory().createLabel(AVAILABLE_TO);

        rfTechnologyType = getSwingFactory().createReferenceField(TECHNOLOGY_TYPE_REFERENCE);
        dcAvailableFrom = getSwingFactory().createDateComponent(AVAILABLE_FROM);
        dcAvailableTo = getSwingFactory().createDateComponent(AVAILABLE_TO);

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        left.add(lblTechTypeRef, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        left.add(rfTechnologyType, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAvailableFrom, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcAvailableFrom, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAvailableTo, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcAvailableTo, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 3, 4, 1, 1, GridBagConstraints.VERTICAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(left, BorderLayout.CENTER);
    }

    @Override
    protected void doSave() {
        HVTStandortTechType orig = new HVTStandortTechType();
        try {
            if (hvtStandortTechType == null) {
                hvtStandortTechType = new HVTStandortTechType();
                hvtStandortTechType.setHvtIdStandort(hvtStandortId);
            }
            BeanUtils.copyProperties(hvtStandortTechType, orig);

            hvtStandortTechType.setTechnologyTypeReference(rfTechnologyType.getReferenceObjectAs(Reference.class));
            hvtStandortTechType.setAvailableFrom(dcAvailableFrom.getDate(null));
            hvtStandortTechType.setAvailableTo(dcAvailableTo.getDate(null));

            HVTService hvtService = getCCService(HVTService.class);
            hvtService.saveTechType(hvtStandortTechType, HurricanSystemRegistry.instance().getSessionId());

            // Schliesse Dialog
            prepare4Close();
            setValue(hvtStandortTechType);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            BeanUtils.copyProperties(orig, hvtStandortTechType);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    public final void loadData() {
        if (hvtStandortId != null) {
            try {
                ISimpleFindService simpleFindService = getCCService(QueryCCService.class);
                rfTechnologyType.setFindService(simpleFindService);
                Reference techTypeExample = new Reference();
                techTypeExample.setType(Reference.REF_TYPE_TECHNOLOGY_TYPE);
                rfTechnologyType.setReferenceFindExample(techTypeExample);
                rfTechnologyType.setFindService(simpleFindService);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    public void showDetails() {
        if (hvtStandortTechType != null) {
            rfTechnologyType.setReferenceObject(hvtStandortTechType.getTechnologyTypeReference());
            dcAvailableFrom.setDate(hvtStandortTechType.getAvailableFrom());
            dcAvailableTo.setDate(hvtStandortTechType.getAvailableTo());
        }
        else {
            GuiTools.cleanFields(this);
            dcAvailableFrom.setDate(new Date());
            dcAvailableTo.setDate(DateTools.getHurricanEndDate());
        }
    }

}
