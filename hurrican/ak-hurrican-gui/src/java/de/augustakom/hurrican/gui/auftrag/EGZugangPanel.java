/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.2004 14:11:36
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.cc.Zugang;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.EndgeraeteService;


/**
 * Panel fuer die Verwaltung der Endgeraete-Zugangsdaten.
 *
 *
 */
public class EGZugangPanel extends AbstractDataPanel implements AKModelOwner, AKNavigationBarListener {

    private static final Logger LOGGER = Logger.getLogger(EGZugangPanel.class);

    /* Key, um Objektaenderungen zu ueberwachen. */
    private static final String WATCH_ZUGANG_X = "watch.zugangsdaten.";

    // GUI
    private AKJNavigationBar navBar = null;
    private AKJButton btnDelete = null;
    private AKJTextField tfRufnummer = null;
    private AKJComboBox cbZugangsart = null;

    // Modelle
    private CCAuftragModel model = null;
    private Zugang actualNavObject = null;

    // Sonstiges
    private int actualNavNumber = -1;
    private boolean guiCreated = false;

    /**
     * Konstruktor.
     */
    public EGZugangPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/EGZugangPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblRufnummer = getSwingFactory().createLabel("rufnummer");
        AKJLabel lblZugangsart = getSwingFactory().createLabel("zugangsart");

        navBar = getSwingFactory().createNavigationBar("navBar", this, true, true);
        AKJButton btnNew = getSwingFactory().createButton("new.zugang", getActionListener());
        btnNew.setBorder(null);
        btnDelete = getSwingFactory().createButton("delete.zugang", getActionListener());
        btnDelete.setBorder(null);
        tfRufnummer = getSwingFactory().createTextField("rufnummer");
        cbZugangsart = getSwingFactory().createComboBox("zugangsart");

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.setBorder(BorderFactory.createTitledBorder("Zugangsdaten"));
        top.add(btnNew, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        top.add(btnDelete, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        top.add(navBar, GBCFactory.createGBC(100, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblRufnummer, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 2, 2)));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        top.add(tfRufnummer, GBCFactory.createGBC(100, 0, 2, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblZugangsart, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 2, 2)));
        top.add(cbZugangsart, GBCFactory.createGBC(100, 0, 2, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 3, 1, 1, GridBagConstraints.NONE));

        this.setLayout(new GridBagLayout());
        this.add(top, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));

        guiCreated = true;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) {
        this.model = (model instanceof CCAuftragModel) ? (CCAuftragModel) model : null;
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    public void readModel() {
        clear();
        navBar.setData(null);
        if (this.model != null) {
            setWaitCursor();
            try {
                EndgeraeteService es = getCCService(EndgeraeteService.class);
                List<Zugang> zugaenge = es.findZugaenge4Auftrag(model.getAuftragId());
                if (zugaenge != null) {
                    int i = 0;
                    for (Zugang z : zugaenge) {
                        addObjectToWatch(WATCH_ZUGANG_X + i, z);
                        i++;
                    }
                }
                navBar.setData(zugaenge);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    public void saveModel() throws AKGUIException {
        setWaitCursor();
        try {
            if (model != null && hasModelChanged()) {
                EndgeraeteService egService = getCCService(EndgeraeteService.class);
                for (int i = 0; i < navBar.getNavCount(); i++) {
                    Zugang z = (Zugang) navBar.getNavigationObjectAt(i);
                    if (hasChanged(WATCH_ZUGANG_X + i, z)) {
                        if (z.getAuftragId() == null) {
                            z.setAuftragId(model.getAuftragId());
                        }
                        egService.saveZugang(z);
                        addObjectToWatch(WATCH_ZUGANG_X + i, z);
                    }
                }
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
     * @see de.augustakom.common.gui.iface.AKNavigationBarListener#showNavigationObject(java.lang.Object, int)
     */
    public void showNavigationObject(Object obj, int number) {
        if (guiCreated) {
            setValues();
            if (obj instanceof Zugang) {
                actualNavNumber = number;
                actualNavObject = (Zugang) obj;
                showValues(actualNavObject);
            }
            else {
                actualNavNumber = -1;
                actualNavObject = null;
                btnDelete.setEnabled(false);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        setValues();
        for (int i = 0; i < navBar.getNavCount(); i++) {
            if (hasChanged(WATCH_ZUGANG_X + i, navBar.getNavigationObjectAt(i))) {
                return true;
            }
        }
        return false;
    }

    /* Uebergibt die angezeigten Daten dem Modell. */
    private void setValues() {
        if (actualNavObject != null) {
            actualNavObject.setEinwahlrufnummer(tfRufnummer.getText());
            actualNavObject.setArt((cbZugangsart.getSelectedItem() instanceof String)
                    ? (String) cbZugangsart.getSelectedItem() : null);
        }
    }

    /* Zeigt die Daten von <code>toShow</code> an. */
    private void showValues(Zugang toShow) {
        if (toShow.getId() == null && navBar.getNavCount() >= 1) {
            btnDelete.setEnabled(true);
        }
        else {
            btnDelete.setEnabled(false);
        }

        tfRufnummer.setText(toShow.getEinwahlrufnummer());
        if (StringUtils.isBlank(toShow.getArt())) {
            cbZugangsart.setSelectedIndex(0);
        }
        else {
            cbZugangsart.selectItem("toString", String.class, toShow.getArt());
        }
    }

    /* 'Loescht' den Inhalt aller Felder. */
    private void clear() {
        navBar.setData(null);
        tfRufnummer.setText("");
        cbZugangsart.setSelectedIndex(0);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("new.zugang".equals(command)) {
            createNewZugang();
        }
        else if ("delete.zugang".equals(command)) {
            deleteZugang();
        }
    }

    /* Erstellt ein neues Objekt vom Typ 'Zugang'. */
    private void createNewZugang() {
        Zugang z = new Zugang();
        addObjectToWatch(WATCH_ZUGANG_X + (actualNavNumber + 1), z);
        navBar.addNavigationObject(z);
    }

    /*
     * Entfernt das aktuell angezeigte Zugang-Objekt aus der Ansicht.
     * Aber nur, wenn das Objekt noch nicht gespeichert ist!
     */
    private void deleteZugang() {
        if (actualNavObject != null && actualNavNumber >= 0) {
            if (actualNavObject.getId() == null) {
                removeObjectFromWatch(WATCH_ZUGANG_X + actualNavNumber);  // Reihenfolge einhalten!
                navBar.removeNavigationObject(actualNavNumber);
            }
            else {
                MessageHelper.showInfoDialog(this,
                        "Bereits gespeicherte EG-Zugangsdaten können nicht mehr gelöscht werden!", null, true);
            }
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

}


