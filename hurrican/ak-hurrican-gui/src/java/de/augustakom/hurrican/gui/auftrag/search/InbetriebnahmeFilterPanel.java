/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.06.2005 11:40:37
 */
package de.augustakom.hurrican.gui.auftrag.search;


import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.text.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.auftrag.shared.AuftragRealisierungTableModel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IFilterOwner;
import de.augustakom.hurrican.gui.base.KNoMaskFormatter;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.shared.view.AuftragRealisierungQuery;
import de.augustakom.hurrican.model.shared.view.AuftragRealisierungView;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Filter-Panel fuer die Suche ueber das Inbetriebnahme- bzw. VorgabeAM-Datum.
 *
 *
 */
public class InbetriebnahmeFilterPanel extends AbstractServicePanel implements IFilterOwner<AuftragRealisierungQuery, AuftragRealisierungTableModel>,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(InbetriebnahmeFilterPanel.class);

    private KeyListener searchKL = null;

    private AKJComboBox cbPGruppe = null;
    private AKJDateComponent dcFrom = null;
    private AKJDateComponent dcTo = null;
    private AKJFormattedTextField tfKundeNoOrig = null;
    private AKJComboBox niederlassungGruppe = null;
    private AKJCheckBox cbInbetriebnahme = null;
    private AKJCheckBox cbKuendigung = null;
    private AKJCheckBox cbRealisierung = null;

    /**
     * Default-Konstruktor.
     */
    public InbetriebnahmeFilterPanel(KeyListener searchKL) {
        super("de/augustakom/hurrican/gui/auftrag/resources/InbetriebnahmeFilterPanel.xml");
        this.searchKL = searchKL;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblPGruppe = getSwingFactory().createLabel("produktgruppe");
        AKJLabel lblFrom = getSwingFactory().createLabel("inbetriebnahme.von");
        AKJLabel lblTo = getSwingFactory().createLabel("inbetriebnahme.bis");
        AKJLabel lblKundeNoOrig = getSwingFactory().createLabel("kunden.no");
        AKJLabel lblNiederlassungGruppe = getSwingFactory().createLabel("niederlassung");
        AKJLabel lblInbetriebnahme = getSwingFactory().createLabel("inbetriebnahme");
        AKJLabel lblKuendigung = getSwingFactory().createLabel("kuendigung");
        AKJLabel lblRealisierung = getSwingFactory().createLabel("realisierung");

        cbPGruppe = getSwingFactory().createComboBox("produktgruppe");
        cbPGruppe.setRenderer(new AKCustomListCellRenderer<>(ProduktGruppe.class, ProduktGruppe::getProduktGruppe));
        dcFrom = getSwingFactory().createDateComponent("inbetriebnahme.von", searchKL);
        dcTo = getSwingFactory().createDateComponent("inbetriebnahme.bis", searchKL);
        tfKundeNoOrig = getSwingFactory().createFormattedTextField("kunden.no", searchKL);
        tfKundeNoOrig.setFormatterFactory(new DefaultFormatterFactory(new KNoMaskFormatter()));
        niederlassungGruppe = getSwingFactory().createComboBox("niederlassung");
        niederlassungGruppe.setRenderer(new AKCustomListCellRenderer<>(Niederlassung.class, Niederlassung::getName));
        cbInbetriebnahme = getSwingFactory().createCheckBox("inbetriebnahme", true);
        cbRealisierung = getSwingFactory().createCheckBox("realisierung", true);
        cbKuendigung = getSwingFactory().createCheckBox("kuendigung", true);

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblPGruppe, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        left.add(cbPGruppe, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblFrom, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcFrom, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblTo, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcTo, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblKundeNoOrig, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfKundeNoOrig, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 4, 4, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        right.add(lblNiederlassungGruppe, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(niederlassungGruppe, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblInbetriebnahme, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbInbetriebnahme, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblRealisierung, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbRealisierung, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblKuendigung, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbKuendigung, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 4, 4, 1, 1, GridBagConstraints.VERTICAL));

        this.setLayout(new GridBagLayout());
        this.add(left, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        this.add(right, GBCFactory.createGBC(0, 100, 2, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            ProduktService ps = getCCService(ProduktService.class);
            List<ProduktGruppe> pgs = ps.findProduktGruppen();
            cbPGruppe.addItems(pgs, true, ProduktGruppe.class);

            NiederlassungService nls = getCCService(NiederlassungService.class);
            List<Niederlassung> nlList = nls.findNiederlassungen();
            niederlassungGruppe.addItems(nlList, true, Niederlassung.class);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#getFilter()
     */
    public AuftragRealisierungQuery getFilter() throws HurricanGUIException {
        AuftragRealisierungQuery query = new AuftragRealisierungQuery();
        query.setProduktGruppeId((cbPGruppe.getSelectedItem() instanceof ProduktGruppe)
                ? ((ProduktGruppe) cbPGruppe.getSelectedItem()).getId() : null);
        query.setInbetriebnahmeFrom(dcFrom.getDate(null));
        query.setInbetriebnahmeTo(dcTo.getDate(null));
        query.setInbetriebnahme(cbInbetriebnahme.isSelectedBoolean());
        query.setRealisierung(cbRealisierung.isSelectedBoolean());
        query.setKuendigung(cbKuendigung.isSelectedBoolean());
        query.setNiederlassungId((niederlassungGruppe.getSelectedItem() instanceof Niederlassung)
                ? ((Niederlassung) niederlassungGruppe.getSelectedItem()).getId() : null);

        Long kNo = tfKundeNoOrig.getValueAsLong(null);
        query.setKundeNo(((kNo == null) || (kNo.longValue() == 0)) ? null : kNo);

        if ((query.getInbetriebnahmeFrom() == null) || (query.getInbetriebnahmeTo() == null)) {
            throw new HurricanGUIException("Bitte definieren Sie ein gültiges Inbetriebnahme-Von und " +
                    "Inbetriebnahme-Bis Datum.");
        }

        if (!query.getInbetriebnahme() && !query.getRealisierung() && !query.getKuendigung()) {
            throw new HurricanGUIException("Eine Filtermöglichkeit muss selektiert sein.");
        }
        return query;
    }

    public AuftragRealisierungTableModel doSearch(AuftragRealisierungQuery query) throws HurricanGUIException {
        try {
            CCAuftragService service = getCCService(CCAuftragService.class);
            List<AuftragRealisierungView> result = service.findRealisierungViews(query, true);

            AuftragRealisierungTableModel tbModel = new AuftragRealisierungTableModel();
            tbModel.setData(result);
            return tbModel;
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    @Override
    public void updateGui(AuftragRealisierungTableModel tableModel, AKJTable resultTable) {
        resultTable.setModel(tableModel);
        resultTable.attachSorter();
        resultTable.fitTable(new int[] { 70, 80, 75, 75, 75, 75, 75, 75, 80, 80, 75, 100, 100, 90 });
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#clearFilter()
     */
    public void clearFilter() {
        GuiTools.cleanFields(this);
        cbInbetriebnahme.setSelected(Boolean.TRUE);
        cbRealisierung.setSelected(Boolean.TRUE);
        cbKuendigung.setSelected(Boolean.TRUE);
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

}


