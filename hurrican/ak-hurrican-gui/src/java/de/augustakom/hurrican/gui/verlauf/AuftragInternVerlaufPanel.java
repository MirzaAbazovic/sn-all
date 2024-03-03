/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2009 14:53:27
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.AuftragInternService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Panel fuer die Bauauftrags-Maske, um relevante Daten von internen Arbeitsauftraegen darzustellen.
 *
 *
 */
public class AuftragInternVerlaufPanel extends AbstractServicePanel implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(AuftragInternVerlaufPanel.class);

    private AKReferenceField rfHVT = null;
    private AKReferenceField rfWorkType = null;
    private AKJTextArea taDesc = null;

    /**
     * Default-Const.
     */
    public AuftragInternVerlaufPanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/AuftragInternVerlaufPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblHVT = getSwingFactory().createLabel("hvt.standort");
        AKJLabel lblWorkType = getSwingFactory().createLabel("work.type");
        AKJLabel lblDesc = getSwingFactory().createLabel("description");

        rfHVT = getSwingFactory().createReferenceField("hvt.standort");
        rfWorkType = getSwingFactory().createReferenceField("work.type");
        taDesc = getSwingFactory().createTextArea("description", false, true, true);
        AKJScrollPane spDesc = new AKJScrollPane(taDesc, new Dimension(250, 150));

        AKJPanel defPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("intern.work.def"));
        defPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        defPnl.add(lblHVT, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        defPnl.add(rfHVT, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(lblWorkType, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(rfWorkType, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(lblDesc, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(spDesc, GBCFactory.createGBC(100, 100, 3, 2, 1, 2, GridBagConstraints.HORIZONTAL));
        defPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 3, 1, 1, GridBagConstraints.VERTICAL));
        defPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 4, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new GridBagLayout());
        this.add(defPnl, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));

        GuiTools.enableComponents(new Component[] { rfHVT, rfWorkType });
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            HVTService hvts = getCCService(HVTService.class);
            List<HVTGruppeStdView> hvtViews = hvts.findHVTViews();

            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfHVT.setFindService(sfs);
            rfHVT.setReferenceList(hvtViews);

            Reference workTypeRefEx = new Reference();
            workTypeRefEx.setType(Reference.REF_TYPE_WORKING_TYPE);
            rfWorkType.setReferenceFindExample(workTypeRefEx);
            rfWorkType.setFindService(sfs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    protected void setModel(CCAuftragModel model) {
        GuiTools.cleanFields(this);
        if (model != null) {
            try {
                AuftragInternService ais = getCCService(AuftragInternService.class);
                AuftragIntern auftragIntern = ais.findByAuftragId(model.getAuftragId());

                if (auftragIntern != null) {
                    rfHVT.setReferenceId(auftragIntern.getHvtStandortId());
                    rfWorkType.setReferenceId(auftragIntern.getWorkingTypeRefId());
                    taDesc.setText(auftragIntern.getDescription());
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
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
    public void update(Observable o, Object arg) {
    }

}


