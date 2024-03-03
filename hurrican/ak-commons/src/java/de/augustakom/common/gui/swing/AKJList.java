/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKColorChangeableComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKSwingConstants;


/**
 * AK-Implementierung einer JList.
 *
 *
 * @see javax.swing.JList
 */
public class AKJList extends JList implements AKColorChangeableComponent,
        AKManageableComponent, AKSwingConstants {

    private static final Logger LOGGER = Logger.getLogger(AKJList.class);
    private static final long serialVersionUID = -2777677183136031580L;

    private Color activeColor = null;
    private Color inactiveColor = null;
    private boolean focusListenerBound = false;
    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;

    /**
     * Erzeugt eine 'leere' JList.
     */
    public AKJList() {
        super();
    }

    /**
     * Erzeugt ein List-Element mit den im Array angegebenen Items.
     *
     * @param listData
     */
    public AKJList(Object[] listData) {
        super(listData);
    }

    /**
     * Erzeugt ein List-Element mit den im Vector angegebenen Items.
     *
     * @param listData
     */
    public AKJList(Vector<?> listData) {
        super(listData);
    }

    /**
     * Erzeugt ein List-Element, das die Items aus dem Modell anzeigt.
     *
     * @param dataModel
     */
    public AKJList(ListModel dataModel) {
        super(dataModel);
    }


    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    @Override
    public void setActiveColor(Color activeColor) {
        this.activeColor = activeColor;
        boundColorChangeFocusListener();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    @Override
    public Color getActiveColor() {
        return activeColor;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    @Override
    public void setInactiveColor(Color inactiveColor) {
        this.inactiveColor = inactiveColor;
        boundColorChangeFocusListener();
        if (this.inactiveColor != null) {
            setBackground(this.inactiveColor);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    @Override
    public Color getInactiveColor() {
        return inactiveColor;
    }

    /**
     * Fuegt dem TextField einmalig einen FocusListener vom Typ ChangeColorFocusListener hinzu.
     */
    private void boundColorChangeFocusListener() {
        if (!focusListenerBound) {
            addFocusListener(new AKChangeColorFocusListener());
            focusListenerBound = true;
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#getComponentName()
     */
    @Override
    public String getComponentName() {
        return ((getAccessibleContext() != null) && (getAccessibleContext().getAccessibleName() != null))
                ? getAccessibleContext().getAccessibleName() : getName();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentExecutable()
     */
    @Override
    public boolean isComponentExecutable() {
        return this.executable;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentVisible()
     */
    @Override
    public boolean isComponentVisible() {
        return this.visible;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentExecutable(boolean)
     */
    @Override
    public void setComponentExecutable(boolean executable) {
        this.executable = executable;
        setEnabled(executable);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentVisible(boolean)
     */
    @Override
    public void setComponentVisible(boolean visible) {
        this.visible = visible;
        setVisible(visible);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isManagementCalled()
     */
    @Override
    public boolean isManagementCalled() {
        return managementCalled;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setManagementCalled(boolean)
     */
    @Override
    public void setManagementCalled(boolean called) {
        this.managementCalled = called;
    }

    /**
     * Kopiert alle Elemente aus toCopy in das ListModel
     *
     * @param toCopy Liste, deren Elemente in das ListModel eingefuegt werden sollen
     * @param model  ListModel, in das die Elemente eingefuegt werden sollen.
     */
    public void copyList2Model(List<?> toCopy, DefaultListModel model) {
        if ((toCopy != null) && (model != null)) {
            for (int i = 0; i < toCopy.size(); i++) {
                model.addElement(toCopy.get(i));
            }
        }
    }

    /**
     * @param items
     * @see addItems(Collection)
     */
    public void addItems(Object[] items) {
        Collection<?> coll = new ArrayList<Object>();
        CollectionUtils.addAll(coll, items);
        addItems(coll);
    }

    /**
     * Fuegt die Inhalte der Collection <code>items</code> der AKJList hinzu. <br> Diese Methode kann nur verwendet
     * werden, wenn der AKJList bereits ein DefaultListModel zugeordnet ist.
     *
     * @param items Items, die dem ListModel zugeordnet werden sollen
     * @throws RuntimeException wenn die AKJList kein DefaultListModel besitzt.
     */
    public void addItems(Collection<?> items) {
        if (!(getModel() instanceof DefaultListModel)) {
            throw new RuntimeException("Items koennen nicht hinzugefuegt werden! JList besitzt kein Model vom Typ DefaultListModel.");
        }

        if (items != null) {
            DefaultListModel cbMdl = (DefaultListModel) getModel();
            for (Object element : items) {
                cbMdl.addElement(element);
            }
        }
    }

    /**
     * @param items       Items, die dem ListModel zugeordnet werden sollen
     * @param createModel Flag, ob ein ListModel erzeugt und der AKJList zugeordnet werden soll, falls noch kein Model
     *                    vorhanden ist.
     * @throws RuntimeException wenn die AKJList kein ListModel besitzt und das Flag <code>createModel</code> den Wert
     *                          <code>false</code> besitzt.
     * @see addItems(java.util.Collection) Ueber den Parameter <code>createModel</code> kann definiert werden, ob ein
     * ListModel erzeugt und zugeordnet werden soll, falls die AKJList noch kein ListModel besitzt.
     */
    public void addItems(Collection<?> items, boolean createModel) {
        if (createModel && !(getModel() instanceof DefaultListModel)) {
            setModel(new DefaultListModel());
        }
        addItems(items);
    }

    /**
     * Entfernt alle Items aus dem hinterlegten List-Model.
     */
    public void removeAllItems() {
        if (getModel() instanceof DefaultListModel) {
            ((DefaultListModel) getModel()).removeAllElements();
        }
    }

    /**
     * @see java.awt.Component#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        boolean x = (!isComponentExecutable()) ? false : enabled;
        super.setEnabled(x);

        if (!x) {
            setBackground(PANEL_BACKGROUND_COLOR);
        }
        else {
            if (hasFocus()) {
                setBackground(activeColor);
            }
            else {
                setBackground(inactiveColor);
            }
        }
    }

    /**
     * @see java.awt.Component#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean b) {
        boolean x = (!isComponentVisible()) ? false : b;
        super.setVisible(x);
    }

    /**
     * Ueberprueft, ob die Liste ein Objekt vom Typ <code>type</code> besitzt, dessen Methode <code>method</code> dem
     * Wert <code>equalValue</code> entspricht.
     *
     * @param method      Methodenname, der auf den einzelnen Objekten aufgerufen werden soll
     * @param type        Typ der Klasse, dem das Objekt entsprechen soll
     * @param equalValue  Wert, der mit dem Rueckgabewert von <code>method</code> uebereinstimmt.
     * @param selectMatch Flag, ob das Objekt mit Uebereinstimmung selektiert werden soll.
     * @return true, wenn eine Uebereinstimmung gefunden wurde.
     */
    public boolean containsItem(String method, Class<?> type, Object equalValue, boolean selectMatch) {
        ListModel mdl = getModel();
        if (mdl != null) {
            for (int i = 0, x = mdl.getSize(); i < x; i++) {
                Object o = mdl.getElementAt(i);
                if ((o != null) && type.isAssignableFrom(o.getClass())) {
                    try {
                        Method toInvoke = type.getMethod(method);
                        Object retVal = toInvoke.invoke(o);
                        if (equalValue == null) {
                            if (retVal == null) {
                                if (selectMatch) { setSelectedIndex(i); }
                                return true;
                            }
                        }
                        else if (equalValue.equals(retVal)) {
                            if (selectMatch) { setSelectedIndex(i); }
                            return true;
                        }
                    }
                    catch (NoSuchMethodError e) {
                        LOGGER.warn("Methode " + method + " ist auf Objekt vom Typ " + type.getName() + " nicht vorhanden!");
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Verschiebt ein Objekt von der Position <code>from</code> auf die Position <code>to</code> - und umgekehrt.
     *
     * @param from Index des Objekts, das verschoben werden soll
     * @param to   Index, an den das Objekt verschoben werden soll
     */
    public void switchItems(int from, int to) {
        if (getModel() instanceof DefaultListModel) {
            DefaultListModel mdl = (DefaultListModel) getModel();

            if ((from >= 0) && (from < mdl.getSize()) && (to >= 0) && (to < mdl.getSize())) {
                Object a = mdl.get(from);
                Object b = mdl.get(to);

                mdl.setElementAt(a, to);
                mdl.setElementAt(b, from);
                setSelectedIndex(to);
            }
            else {
                LOGGER.warn("Index ungueltig fuer switchItems (von-nach-size): " + from + "-" + to + "-" + mdl.getSize());
            }
        }
        else {
            LOGGER.warn("AKJList.switchItems kann nicht ausgefuehrt werden - kein DefaultListModel!");
        }
    }

    /**
     * Liefert eine Liste aller selektierten Werte in aufsteigender Sortierung basierend auf den Indizes in der Liste.
     * Die Liste enthält nur Werte des Typs {@code type}.
     */
    public <T> List<T> getSelectedValues(Class<T> type) {
        if (type == null) {return null;}
        Object[] selectedObjects = getSelectedValues();
        if ((selectedObjects != null) && (selectedObjects.length > 0)) {
            List<T> selectedValues = new ArrayList<T>();
            for (Object selectedObject : selectedObjects) {
                if (type.isInstance(selectedObject)) {
                    @SuppressWarnings("unchecked")
                    T selectedValue = (T) selectedObject;
                    selectedValues.add(selectedValue);
                }
            }
            return (!selectedValues.isEmpty()) ? selectedValues : null;
        }
        return null;
    }

    /**
     * Selektiert alle enthaltenen Items.
     */
    public void selectAll() {
        if (getModel() != null && getModel().getSize() > 0) {
            setSelectionInterval(0, getModel().getSize() - 1);
        }
    }

}
