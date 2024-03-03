/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 09:13:10
 */
package de.augustakom.hurrican.gui.auftrag.wizards.anlage;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Wizard-Panel, um ein Produkt auszuwaehlen.
 *
 *
 */
public class SelectProduktWizardPanel extends AbstractServiceWizardPanel implements ItemListener,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(SelectProduktWizardPanel.class);

    private AKJComboBox cbProduktGruppe = null;
    private AKJTable tbProdukte = null;
    private ProduktTableModel tbMdlProdukte = null;

    /* Map mit allen vorfuegbaren Leitungsarten.
     * Als Key wird die ID der Leitungsart verwendet, als Value Objekte vom
     * Typ <code>Leitungsart</code>.
     */
    private Map<Long, Leitungsart> leitungsarten = null;

    /**
     * Konstruktor mit Angabe der Wizard-Komponenten.
     */
    public SelectProduktWizardPanel(AKJWizardComponents wizardComponents) {
        super("de/augustakom/hurrican/gui/auftrag/wizards/anlage/SelectProduktWizardPanel.xml", wizardComponents);
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        setNextButtonEnabled(false);
        AKJLabel lblProduktGruppe = getSwingFactory().createLabel("produkt.gruppe");
        AKJLabel lblProdukt = getSwingFactory().createLabel("produkt");

        cbProduktGruppe = getSwingFactory().createComboBox("produkt.gruppe");
        cbProduktGruppe.setRenderer(new AKCustomListCellRenderer<>(ProduktGruppe.class, ProduktGruppe::getProduktGruppe));
        cbProduktGruppe.addItemListener(this);

        tbMdlProdukte = new ProduktTableModel();
        tbProdukte = new AKJTable(tbMdlProdukte, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbProdukte.fitTable(new int[] { 100, 125, 100, 150 });
        tbProdukte.addMouseListener(new TableMouseListener());
        AKJScrollPane spTable = new AKJScrollPane(tbProdukte);
        spTable.setPreferredSize(new Dimension(500, 200));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(lblProduktGruppe, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(cbProduktGruppe, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblProdukt, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(spTable, GBCFactory.createGBC(100, 100, 1, 3, 4, 1, GridBagConstraints.BOTH));

    }

    /**
     * Liest die benoetigten Daten ein.
     *
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            leitungsarten = new HashMap<Long, Leitungsart>();
            ProduktService ps = getCCService(ProduktService.class);

            // Leitungsarten auslesen
            List<Leitungsart> larts = ps.findLeitungsarten();
            if (larts != null) {
                for (Leitungsart element : larts) {
                    leitungsarten.put(element.getId(), element);
                }
            }

            // Produktgruppen auslesen, fuer die ein Hurrican-Auftrag erstellt werden kann.
            List<ProduktGruppe> result = ps.findProduktGruppen4Hurrican();
            DefaultComboBoxModel cbMdlPG = new DefaultComboBoxModel();
            ProduktGruppe emptyPG = new ProduktGruppe();
            cbMdlPG.addElement(emptyPG);

            cbProduktGruppe.copyList2Model(result, cbMdlPG);
            cbProduktGruppe.setModel(cbMdlPG);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
            getWizardComponents().getWizardMaster().cancelWizard();
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJDefaultWizardPanel#goNext()
     */
    @Override
    protected boolean goNext() {
        int selection = tbProdukte.getSelectedRow();
        Object selObject = tbMdlProdukte.getDataAtRow(selection);
        if (selObject instanceof Produkt) {
            Produkt selProd = (Produkt) selObject;
            if (NumberTools.equal(selProd.getProduktGruppeId(), ProduktGruppe.AK_INTERN_WORK)) {
                // Panel fuer Auswahl interne Arbeit!
                AuftragInternWizardPanel internWP = new AuftragInternWizardPanel(getWizardComponents());
                getWizardComponents().addWizardPanel(internWP);
            }
            else {
                SelectAuftragsartWizardPanel selAArtWP = new SelectAuftragsartWizardPanel(getWizardComponents());
                getWizardComponents().addWizardPanel(selAArtWP);

                AuftragDetailsWizardPanel detailWP = new AuftragDetailsWizardPanel(getWizardComponents());
                getWizardComponents().addWizardPanel(detailWP);
            }
        }

        return super.goNext();
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJDefaultWizardPanel#next()
     */
    @Override
    public boolean next() {
        AuftragDaten ad = (AuftragDaten) getWizardObject(AuftragWizardObjectNames.WIZARD_OBJECT_CC_AUFTRAG_DATEN);
        ad.setBearbeiter(HurricanSystemRegistry.instance().getCurrentUserName());

        int selection = tbProdukte.getSelectedRow();
        Object selObject = tbMdlProdukte.getDataAtRow(selection);
        if ((selObject instanceof Produkt) && (((Produkt) selObject).getId() != null)) {
            ad.setProdId(((Produkt) selObject).getId());
        }
        else {
            String msg = "Bitte wählen Sie zuerst ein Produkt aus.";
            String title = "Warnung";
            MessageHelper.showMessageDialog(this, msg, title, JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return super.next();
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJDefaultWizardPanel#update()
     */
    @Override
    public void update() {
        if (getWizardComponents().getDirection() == AKJWizardComponents.DIRECTION_BACKWARD) {
            setNextButtonEnabled(false);
            removePanelsAfter(this);
        }
    }

    /**
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        tbMdlProdukte.removeAll();

        // Produkte zur ausgewaehlten ProduktGruppe laden
        if ((e.getSource() == cbProduktGruppe) && (e.getStateChange() == ItemEvent.SELECTED)
                && (cbProduktGruppe.getSelectedItem() instanceof ProduktGruppe)) {
            ProduktGruppe pg = (ProduktGruppe) cbProduktGruppe.getSelectedItem();
            if (pg.getId() != null) {
                try {
                    setWaitCursor();
                    ProduktService ps = getCCService(ProduktService.class);
                    List<Produkt> result = ps.findProdukte4PGAndHurrican(pg.getId());
                    tbMdlProdukte.setData(result);
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
                finally {
                    setDefaultCursor();
                }
            }
        }
    }

    class TableMouseListener extends MouseAdapter {
        /**
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (tbProdukte.getSelectedRow() >= 0) {
                setNextButtonEnabled(true);
            }
            else {
                setNextButtonEnabled(false);
            }
        }
    }

    /**
     * TableModel fuer die Darstellung der Produkte
     */
    class ProduktTableModel extends AKTableModel<Produkt> {
        private static final int COL_PROD_NR = 0;
        private static final int COL_ANSCHLUSSART = 1;
        private static final int COL_LEITUNGSART = 2;
        private static final int COL_BESCHREIBUNG = 3;

        private static final int COL_COUNT = 4;

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_PROD_NR:
                    return "Produkt-Nr";
                case COL_ANSCHLUSSART:
                    return "Anschlussart";
                case COL_LEITUNGSART:
                    return "Leitungsart";
                case COL_BESCHREIBUNG:
                    return "Beschreibung";
                default:
                    return "";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof Produkt) {
                Produkt p = (Produkt) o;
                switch (column) {
                    case COL_PROD_NR:
                        return p.getProduktNr();
                    case COL_ANSCHLUSSART:
                        return p.getAnschlussart();
                    case COL_LEITUNGSART:
                        return getLeitungsart(p.getLeitungsart());
                    case COL_BESCHREIBUNG:
                        return p.getBeschreibung();
                    default:
                        break;
                }
            }
            return null;
        }

        /* Gibt den Namen der Leitungsart mit der angegebenen ID zurueck. */
        private String getLeitungsart(Long leitungsartId) {
            if ((leitungsarten != null) && (leitungsartId != null)) {
                Leitungsart lart = leitungsarten.get(leitungsartId);
                return (lart != null) ? lart.getName() : null;
            }
            return null;
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

    }

}
