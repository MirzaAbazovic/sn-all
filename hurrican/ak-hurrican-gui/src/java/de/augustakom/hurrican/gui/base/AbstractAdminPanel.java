/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 14:10:59
 */
package de.augustakom.hurrican.gui.base;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.table.AKTableListener;
import de.augustakom.common.gui.swing.table.AKTableOwner;


/**
 * Abstrakte Implementierung fuer die Admin-Panels.
 *
 *
 */
public abstract class AbstractAdminPanel extends AbstractDataPanel implements AKDataLoaderComponent, AKTableOwner {

    private static final long serialVersionUID = 4825241540556913529L;
    private AKTableListener tableListener;

    public AbstractAdminPanel(String resource) {
        super(resource);
        initAdminPanel();
    }

    public AbstractAdminPanel(String resource, LayoutManager layout) {
        super(resource, layout);
        initAdminPanel();
    }

    /**
     * Initialisiert das Panel
     */
    private void initAdminPanel() {
        tableListener = new AKTableListener(this);
    }

    @Override
    public abstract void showDetails(Object details);

    @Override
    public abstract void loadData();

    /**
     * Veranlasst das Panel dazu, ein neues Objekt zu erstellen.
     */
    public abstract void createNew();

    /**
     * Veranlasst das Panel dazu, die Daten zu speichern.
     */
    public abstract void saveData();

    /**
     * Gibt einen MouseListener fuer eine Tabelle zurueck. Der MouseListener reagiert auf Aenderungen in der Selektion
     * und ruft die Methode showDetails(Object) auf.
     *
     * @return
     */
    protected AKTableListener getTableListener() {
        return tableListener;
    }

    @Override
    public void readModel() throws AKGUIException {
    }

    @Override
    public void saveModel() throws AKGUIException {
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
    }

    @Override
    public Object getModel() {
        return null;
    }

}


