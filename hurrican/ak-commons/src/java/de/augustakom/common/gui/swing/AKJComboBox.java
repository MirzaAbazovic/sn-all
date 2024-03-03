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
import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKColorChangeableComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;


/**
 * AK-Implementierung einer JComboBox.
 *
 *
 * @see javax.swing.JComboBox
 */
public class AKJComboBox extends JComboBox implements AKColorChangeableComponent, AKManageableComponent {

    private static final Logger LOGGER = Logger.getLogger(AKJComboBox.class);

    private static final AKJTextField referenceField = new AKJTextField();

    private List<Object> itemValues = null;

    private Class<?> modelClass4Renderer = null;
    private String displayMethod = null;

    private Color activeColor = null;
    private Color inactiveColor = null;
    private boolean focusListenerBound = false;
    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;

    /**
     * Erzeugt eine 'leere' JComboBox.
     */
    public AKJComboBox() {
        super();
        init();
    }

    /**
     * Erzeugt eine ComboBox mit den im Array angegebenen Items.
     */
    public AKJComboBox(Object[] items) {
        super(items);
        init();
    }

    /**
     * Erzeugt eine ComboBox mit den im Vector vorhandenen Items.
     */
    public AKJComboBox(Vector<?> items) {
        super(items);
        init();
    }

    /**
     * Erzeugt eine ComboBox, die die Items aus dem Modell anzeigt.
     *
     */
    public AKJComboBox(ComboBoxModel aModel) {
        super(aModel);
        init();
    }

    /**
     * Erzeugt eine 'leere' JComboBox mit dem angegebenen Renderer.
     *
     */
    public AKJComboBox(ListCellRenderer renderer) {
        super();
        setRenderer(renderer);
        init();
    }

    /**
     * Erzeugt eine 'leere' JComboBox mit einem ReflectionListCellRenderer.
     *
     * @param modelClass4Renderer Modell-Klasse fuer den CellRenderer
     * @param displayMethod       Methode vom Modell, deren Result als Display-Wert verwendet werden soll
     */
    public AKJComboBox(Class<?> modelClass4Renderer, String displayMethod) {
        super();
        this.modelClass4Renderer = modelClass4Renderer;
        this.displayMethod = displayMethod;
        setRenderer(new AKReflectionListCellRenderer(modelClass4Renderer, displayMethod));
        init();
    }

    /* Initialisiert die ComboBox */
    private void init() {
        // JTextField und JComboBox besitzen unterschiedliche Schriftgroessen
        // --> werden hier identisch gesetzt.
        setFont(referenceField.getFont());
    }

    /**
     * Fuegt die Inhalte der Collection <code>items</code> der ComboBox hinzu. <br> Diese Methode kann nur verwendet
     * werden, wenn der ComboBox bereits ein ComboBoxModel zugeordnet ist.
     *
     * @param items Items, die dem ComboBoxModel zugeordnet werden sollen
     * @throws RuntimeException wenn die ComboBox kein ComboBoxModel (vom Typ MutableComboBoxModel) besitzt.
     */
    public void addItems(Collection<?> items) {
        if (!(getModel() instanceof MutableComboBoxModel)) {
            throw new RuntimeException("Items koennen nicht hinzugefuegt werden! ComboBox besitzt kein Model vom Typ MutableComboBoxModel.");
        }

        if (items != null) {
            MutableComboBoxModel cbMdl = (MutableComboBoxModel) getModel();
            for (Iterator<?> iter = items.iterator(); iter.hasNext(); ) {
                Object element = iter.next();
                cbMdl.addElement(element);
            }
        }
    }

    /**
     * @param items       Items, die dem ComboBoxModel zugeordnet werden sollen
     * @param createModel Flag, ob ein ComboBoxModel erzeugt und der ComboBox zugeordnet werden soll, falls noch kein
     *                    Model vorhanden ist.
     * @throws RuntimeException wenn die ComboBox kein ComboBoxModel besitzt und das Flag <code>createModel</code> den
     *                          Wert <code>false</code> besitzt.
     * @see #addItems(java.util.Collection) Ueber den Parameter <code>createModel</code> kann definiert werden, ob ein
     * ComboBoxModel erzeugt und zugeordnet werden soll, falls die ComboBox noch kein ComboBoxModel besitzt.
     */
    public void addItems(Collection<?> items, boolean createModel) {
        if (createModel && !(getModel() instanceof MutableComboBoxModel)) {
            setModel(new DefaultComboBoxModel());
        }
        addItems(items);
    }

    /**
     * @param removeAll        Flag, ob vor dem Hinzufuegen von Items bestehende Items entfernt werden sollen
     *
     * @see #addItems(Collection, boolean, Class)
     */
    public void addItems(Collection<?> items, boolean createModel, Class<?> type4EmptyObject, boolean removeAll) {
        if (removeAll) {
            removeAllItems();
        }
        addItems(items, createModel, type4EmptyObject);
    }

    /**
     * @param type4EmptyObject Typ des 'leeren' Objekts, das dem ComboBoxModel an erster Stelle uebergeben werden soll.
     *                         <br> Die Klasse benoetigt zwingend einen Standardkonstruktor (Konstruktor ohne
     *                         Argumente)!
     * @throws RuntimeException wenn die ComboBox kein ComboBoxModel besitzt und das Flag <code>createModel</code> den
     *                          Wert <code>false</code> besitzt.
     * @see #addItems(Collection, boolean) Zusaetzlich zu der Collection wird dem ComboBoxModel an die erste Position ein
     * neues Objekt vom Typ <code>type4EmptyObject</code> uebergeben.
     */
    @SuppressWarnings("unchecked")
    public void addItems(Collection items, boolean createModel, Class type4EmptyObject) {
        try {
            // Liefert Hibernate eine leere Liste zurueck, ist diese vom Typ 'EmptyList'.
            // Dieses Objekt ist immutable!!! Deshalb die Umsetzung auf eine ArrayList.
            if ((items != null) && "java.util.Collections$EmptyList".equals(items.getClass().getName())) {
                items = new ArrayList();
            }

            Object empty = null;
            if (type4EmptyObject != null) {
                if (String.class.equals(type4EmptyObject) || (type4EmptyObject.isEnum())) {
                    empty = " ";
                }
                else {
                    empty = type4EmptyObject.newInstance();
                }
            }

            Collection itemsWithEmpty = null;
            if (items instanceof List) {
                itemsWithEmpty = new ArrayList();
                itemsWithEmpty.addAll(items);
                if (empty != null) {
                    ((List) itemsWithEmpty).add(0, empty);
                }
            }
            else {
                itemsWithEmpty = new ArrayList();
                if (empty != null) {
                    itemsWithEmpty.add(empty);
                }
                if (items != null) {
                    itemsWithEmpty.addAll(items);
                }
            }

            addItems(itemsWithEmpty, createModel);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Fuegt der ComboBox einen Eintrag mit definierten Text und Value hinzu.
     */
    public void addItem(String text, Object value) {
        addItem(text);

        if (itemValues == null) {
            itemValues = new ArrayList<Object>();
        }
        itemValues.add(value);
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
     * Kopiert alle Elemente aus toCopy in das ComboBoxModel
     *
     * @param toCopy Liste, deren Elemente in das ComboBoxModel eingefuegt werden sollen
     * @param model  ComboBoxModel, in das die Elemente eingefuegt werden sollen.
     */
    public void copyList2Model(List<?> toCopy, DefaultComboBoxModel model) {
        if ((toCopy != null) && (model != null)) {
            for (int i = 0; i < toCopy.size(); i++) {
                model.addElement(toCopy.get(i));
            }
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
     * @param value Wert des Objekts, das selektiert werden soll. Bei dem Wert handelt es sich um den dargestellten
     *              Text, nicht das Objekt selbst.
     * @return true wenn ein Item selektiert wurde - sonst false.
     *
     * @see #selectItem(String, Class, Object) Diese Methode kann nur verwendet werden, wenn die ComboBox ueber den
     * Konstruktor <code>AKJComboBox(Class, String)</code> erzeugt wurde.
     */
    public boolean selectItem(Object value) {
        if ((displayMethod != null) && (modelClass4Renderer != null)) {
            return selectItem(displayMethod, modelClass4Renderer, value);
        }
        return false;
    }

    /**
     * Sucht nach einem ComboBox-Eintrag, bei dem der Rueckgabewert der Methode <code>method</code> mit dem Wert
     * <code>equalValue</code> uebereinstimmt. Der ComboBox-Eintrag muss ausserdem vom Typ <code>type</code> sein. <br>
     * Wird eine Uebereinstimmung gefunden, wird das Item selektiert und die Methode verlassen.
     *
     * @param method     Methode, die den <code>equalValue</code> liefern soll.
     * @param type       Typ des ComboBox-Eintrags auf dem die Methode <code>method</code> aufgerufen werden soll
     * @param equalValue Wert, den die Methode <code>method</code> liefern soll
     * @return true wenn ein Item selektiert wurde - sonst false.
     */
    public boolean selectItem(String method, Class<?> type, Object equalValue) {
        for (int i = 0; i < getItemCount(); i++) {
            Object o = getItemAt(i);
            if (o != null) {
                if (type.isAssignableFrom(o.getClass())) {
                    try {
                        Method toInvoke = type.getMethod(method, new Class[] { });
                        Object retVal = toInvoke.invoke(o, new Object[] { });
                        if (equalValue == null) {
                            if (retVal == null) {
                                setSelectedIndex(i);
                                return true;
                            }
                        }
                        else if (equalValue.equals(retVal)) {
                            setSelectedIndex(i);
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
                else {
                    LOGGER.warn("ComboBox-Objekt ist nicht vom Typ " + type.getName());
                }
            }
        }
        return false;
    }

    public boolean selectItemRaw(Object item) {
        for (int i = 0; i < getItemCount(); i++) {
            Object o = getItemAt(i);
            if ((o != null) && o.equals(item)) {
                setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Kann verwendet werden, wenn die ComboBox ItemValues ueber die Methode <code>setItemValue</code> erhalten hat.
     * <br> Es wird das Item selektiert, das 'equal' zu <code>equalValue</code> ist.
     */
    public void selectItemWithValue(Object equalValue) {
        if (itemValues != null) {
            int i = 0;
            for (Object itemValue : itemValues) {
                if (itemValue == null) {
                    if (equalValue == null) {
                        setSelectedIndex(i);
                        break;
                    }
                }
                else {
                    if (ObjectUtils.equals(itemValue, equalValue)) {
                        setSelectedIndex(i);
                        break;
                    }
                }

                i++;
            }
        }
    }

    /**
     * Ermittelt von dem aktuell selektierten Item ueber die Methode <code>method</code> einen Wert und gibt diesen
     * zurueck.
     *
     * @param method        Methoden-Name, der fuer den Rueckgabewert aufgerufen werden soll.
     * @param requestedType Typ des erwarteten Results.
     * @return Rueckgabewert der Methode <code>method</code> auf dem aktuell selektierten Objekt oder <code>null</code>.
     */
    public Object getSelectedItemValue(String method, Class<?> requestedType) {
        try {
            Object tmp = getSelectedItem();
            if (tmp != null) {
                Method toInvoke = tmp.getClass().getMethod(method, new Class[] { });
                Object result = toInvoke.invoke(tmp, new Object[] { });
                if ((requestedType != null) && (result != null)) {
                    return (requestedType.isAssignableFrom(result.getClass())) ? result : null;
                }
                return result;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Kann nur verwendet werden, wenn die ComboBox ItemValues ueber die Methode <code>setItemValue</code> erhalten hat.
     * <br>
     */
    public Object getSelectedItemValue() {
        if (itemValues != null) {
            int index = getSelectedIndex();
            return ((itemValues.size() > index) && (index >= 0)) ? itemValues.get(index) : null;
        }
        return null;
    }

    /**
     * Ueberprueft, ob die ComboBox einen Eintrag mit den angegebenen Werten enthaelt.
     *
     * @param method     Methode, die den <code>equalValue</code> liefern soll.
     * @param type       Typ des ComboBox-Eintrags auf dem die Methode <code>method</code> aufgerufen werden soll
     * @param equalValue Wert, den die Methode <code>method</code> liefern soll
     * @return true wenn ein Item selektiert wurde - sonst false.
     *
     */
    public boolean containsItem(String method, Class<?> type, Object equalValue) {
        for (int i = 0; i < getItemCount(); i++) {
            Object o = getItemAt(i);
            if (o != null) {
                if (type.isAssignableFrom(o.getClass())) {
                    try {
                        Method toInvoke = type.getMethod(method, new Class[] { });
                        Object retVal = toInvoke.invoke(o, new Object[] { });
                        if (equalValue == null) {
                            if (retVal == null) {
                                return true;
                            }
                        }
                        else if (equalValue.equals(retVal)) {
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
                else {
                    LOGGER.warn("ComboBox-Objekt ist nicht vom Typ " + type.getName());
                }
            }
        }
        return false;
    }

    /**
     * @see java.awt.Component#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean b) {
        boolean x = (!isComponentExecutable()) ? false : b;
        super.setEnabled(x);
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
     * Aendert den Schriftstil des Labels. Die Schriftart und die Schriftgroesse bleiben erhalten.
     *
     * @param fontStyle Stil, der gesetzt werden soll. Moegliche Werte fuer fontStyle: <br> <ul> <li>Font.PLAIN
     *                  <li>Font.BOLD <li>Font.ITALIC </ul>
     */
    public void setFontStyle(int fontStyle) {
        setFont(getFont().deriveFont(fontStyle));
    }

    public Class<?> getModelClass4Renderer() {
        return modelClass4Renderer;
    }

}
