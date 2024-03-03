/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2004 16:22:07
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.BAVerlaufConfig;
import de.augustakom.hurrican.model.cc.BAVerlaufZusatz;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Dialog, um einen Zusatz fuer eine Bauauftragskonfiguration zu definieren.
 *
 *
 */
public class BAVerlaufZusatzDialog extends AbstractServiceOptionDialog implements ItemListener {

    private static final Logger LOGGER = Logger.getLogger(BAVerlaufZusatzDialog.class);

    private static final Reference NO_STANDORT_SELECTION = new Reference();
    public static final HVTGruppe EMPTY_HG = new HVTGruppe();
    static {
        NO_STANDORT_SELECTION.setStrValue("");
        EMPTY_HG.setOrtsteil("--alle--");
    }

    private AKJComboBox cbHVTGruppen = null;
    private AKJComboBox cbAbteilungen = null;
    private AKJCheckBox chbSelbstmontage = null;

    private List<HVTGruppe> hvtGruppen = null;
    private List<Abteilung> abteilungen = null;
    private BAVerlaufConfig baVerlConf = null;
    private BAVerlaufZusatz zusatz = null;

    private AKJComboBox cbStandortTyp;

    /**
     * Konstruktor, um einen neuen BAVerlauf-Zusatz anzulegen.
     *
     * @param hvtGruppen  Liste mit den selektierbaren HVT-Gruppen.
     * @param abteilungen Liste mit den selektierbaren Abteilungen.
     * @param baVerlConf  BAVerlaufConfig-Objekt, zu dem der Zusatz definiert werden soll.
     */
    public BAVerlaufZusatzDialog(List<HVTGruppe> hvtGruppen, List<Abteilung> abteilungen, BAVerlaufConfig baVerlConf) {
        super("de/augustakom/hurrican/gui/verlauf/resources/BAVerlaufZusatzDialog.xml");
        this.hvtGruppen = hvtGruppen;
        this.abteilungen = abteilungen;
        this.baVerlConf = baVerlConf;
        createGUI();
        read();
    }

    /**
     * Konstruktor, um einen vorhandenen BAVerlauf-Zusatz zu editieren.
     *
     * @param hvtGruppen  Liste mit den selektierbaren HVT-Gruppen.
     * @param abteilungen Liste mit den selektierbaren Abteilungen.
     * @param zusatz      Objekt, das editiert werden soll.
     */
    public BAVerlaufZusatzDialog(List<HVTGruppe> hvtGruppen, List<Abteilung> abteilungen, BAVerlaufZusatz zusatz) {
        super("de/augustakom/hurrican/gui/verlauf/resources/BAVerlaufZusatzDialog.xml");
        this.hvtGruppen = hvtGruppen;
        this.abteilungen = abteilungen;
        this.zusatz = zusatz;

        createGUI();
        read();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblHVTGruppen = getSwingFactory().createLabel("hvt.gruppen");
        AKJLabel lblAbteilungen = getSwingFactory().createLabel("abteilungen");
        AKJLabel lblSelbstmontage = getSwingFactory().createLabel("selbstmontage");

        cbHVTGruppen = getSwingFactory().createComboBox("hvt.gruppen");
        cbHVTGruppen.setRenderer(new AKCustomListCellRenderer<>(HVTGruppe.class, HVTGruppe::getOrtsteil));
        cbAbteilungen = getSwingFactory().createComboBox("abteilungen");
        cbAbteilungen.setRenderer(new AKCustomListCellRenderer<>(Abteilung.class, Abteilung::getName));
        cbAbteilungen.addItemListener(this);
        chbSelbstmontage = getSwingFactory().createCheckBox("selbstmontage");
        chbSelbstmontage.setEnabled(false);

        cbStandortTyp = new AKJComboBox(Reference.class, "getStrValue");
        final AKJLabel standorttyp = new AKJLabel("Standorttyp");

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new AKJPanel(),     GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(lblHVTGruppen,      GBCFactory.createGBC(  0,   0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(),     GBCFactory.createGBC(  0,   0, 2, 1, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(cbHVTGruppen,       GBCFactory.createGBC(100,   0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(standorttyp,        GBCFactory.createGBC(  0,   0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(),     GBCFactory.createGBC(  0,   0, 2, 2, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(cbStandortTyp,      GBCFactory.createGBC(100,   0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblAbteilungen,     GBCFactory.createGBC(  0,   0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(cbAbteilungen,      GBCFactory.createGBC(100,   0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblSelbstmontage,   GBCFactory.createGBC(  0,   0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(chbSelbstmontage,   GBCFactory.createGBC(100,   0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(),     GBCFactory.createGBC(100, 100, 4, 5, 1, 1, GridBagConstraints.BOTH));
    }

    /* Liest die Daten aus. */
    private void read() {
        DefaultComboBoxModel mdlHVT = new DefaultComboBoxModel();
        mdlHVT.addElement(EMPTY_HG);
        cbHVTGruppen.copyList2Model(hvtGruppen, mdlHVT);
        cbHVTGruppen.setModel(mdlHVT);

        DefaultComboBoxModel mdlAbt = new DefaultComboBoxModel();
        cbAbteilungen.copyList2Model(abteilungen, mdlAbt);
        cbAbteilungen.setModel(mdlAbt);

        try {
            cbStandortTyp.addItems(getCCService(ReferenceService.class)
                    .findReferencesByType(Reference.REF_TYPE_STANDORT_TYP, false), true);
            cbStandortTyp.addItem(NO_STANDORT_SELECTION);
            cbStandortTyp.selectItem(NO_STANDORT_SELECTION.getStrValue());
        }
        catch (FindException | ServiceNotFoundException e) {
            LOGGER.error(e);
            MessageHelper.showErrorDialog(this, e);
        }

        if (zusatz != null) {
            chbSelbstmontage.setSelected(zusatz.getAuchSelbstmontage());
            cbHVTGruppen.selectItem("getId", HVTGruppe.class, zusatz.getHvtGruppeId());
            cbAbteilungen.selectItem("getId", Abteilung.class, zusatz.getAbtId());
            cbStandortTyp.selectItem("getId", Reference.class, zusatz.getStandortTypRefId());
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            setWaitCursor();

            final HVTGruppe hvtSelected = (HVTGruppe) cbHVTGruppen.getSelectedItem();
            final Reference reference = ((Reference) cbStandortTyp.getSelectedItem());

            if (hvtSelected != EMPTY_HG && reference != NO_STANDORT_SELECTION)   {
                MessageHelper.showWarningDialog(this, "Bitte entweder eine HVT-Gruppe ODER einen Standorttyp angeben", true);
            }
            else {
                BAConfigService service = getCCService(BAConfigService.class);
                if (zusatz == null) {
                    zusatz = new BAVerlaufZusatz();
                    zusatz.setBaVerlaufConfigId(baVerlConf.getId());
                }

                Object abt = cbAbteilungen.getSelectedItem();

                zusatz.setHvtGruppeId(hvtSelected.getId());
                zusatz.setAbtId((abt instanceof Abteilung) ? ((Abteilung) abt).getId() : null);
                zusatz.setAuchSelbstmontage(chbSelbstmontage.isSelected());
                zusatz.setStandortTypRefId(reference != NO_STANDORT_SELECTION ? reference.getId() : null);

                service.saveBAVerlaufZusatz(zusatz, HurricanSystemRegistry.instance().getSessionId());
                prepare4Close();
                setValue(zusatz);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent e) {
        if ((e.getSource() == cbAbteilungen) && (e.getStateChange() == ItemEvent.SELECTED)) {
            Object item = cbAbteilungen.getSelectedItem();
            if (item instanceof Abteilung) {
                Abteilung abt = (Abteilung) item;
                if (NumberTools.equal(abt.getId(), Abteilung.FIELD_SERVICE)) {
                    chbSelbstmontage.setEnabled(true);
                }
                else {
                    chbSelbstmontage.setEnabled(false);
                }
            }
        }
    }
}


