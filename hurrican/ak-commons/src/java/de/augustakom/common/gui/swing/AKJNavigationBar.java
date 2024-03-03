/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2004 11:18:01
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKNavigationBarListener;


/**
 * NavigationBar, um beliebige Datenmengen zu durchlaufen.
 *
 *
 */
public class AKJNavigationBar extends AKJPanel implements AKManageableComponent {

    private static final Logger LOGGER = Logger.getLogger(AKJNavigationBar.class);

    private static final int TYPE_FIRST = 0;
    private static final int TYPE_LAST = 1;
    private static final int TYPE_NEXT = 2;
    private static final int TYPE_PREVIOUS = 3;
    private static final long serialVersionUID = 3852357475588400858L;

    private List<AKNavigationBarListener> listener = null;
    private boolean doShowCount = true;
    private boolean doShowFirstAndLast = true;
    @SuppressWarnings({ "rawtypes" })
    private List data = null;
    private int actual = 0;
    private int previousIndex = 0;

    private AKJButton btnFirst = null;
    private AKJButton btnPrevious = null;
    private AKJButton btnNext = null;
    private AKJButton btnLast = null;
    private AKJLabel lblCount = null;
    private ButtonActionListener actionListener = null;

    private boolean initialized = false;
    private volatile MouseListener adminML;
    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;

    private boolean enabled = true;

    /**
     * Konstruktor fuer die Navigationsleiste. <br>
     *
     * @param listener  Listener, der bei einer Aenderung der Selektion benachrichtigt werden soll.
     * @param doShowCount Flag, ob die Anzahl der Datensaetze in einem Label angezeigt werden soll (Default: true).
     */
    public AKJNavigationBar(AKNavigationBarListener listener, boolean doShowCount) {
        super();
        this.listener = new ArrayList<>();
        if (listener != null) {
            this.listener.add(listener);
        }
        this.doShowCount = doShowCount;
        createGUI();
    }

    /**
     * Konstruktor fuer die Navigationsleiste. <br>
     *
     * @param listener         Listener, der bei einer Aenderung der Selektion benachrichtigt werden soll.
     * @param doShowCount      Flag, ob die Anzahl der Datensaetze in einem Label angezeigt werden soll (Default:
     *                         true).
     * @param doShowFirstAndLast Flag, ob die Buttons 'erster DS' und 'letzter DS' angezeigt werden sollen (Default:
     *                         true).
     */
    public AKJNavigationBar(AKNavigationBarListener listener, boolean doShowCount, boolean doShowFirstAndLast) {
        super();
        this.listener = new ArrayList<>();
        if (listener != null) {
            this.listener.add(listener);
        }
        this.doShowCount = doShowCount;
        this.doShowFirstAndLast = doShowFirstAndLast;
        createGUI();
    }

    /**
     * @see java.awt.Component#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        boolean x = (!isComponentExecutable()) ? false : enabled;
        this.enabled = x;
        btnFirst.setEnabled(x);
        btnLast.setEnabled(x);
        btnNext.setEnabled(x);
        btnPrevious.setEnabled(x);
    }

    /**
     * @see java.awt.Component#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean b) {
        boolean x = (!isComponentVisible()) ? false : b;
        btnFirst.setVisible(x);
        btnLast.setVisible(x);
        btnNext.setVisible(x);
        btnPrevious.setVisible(x);
    }

    /**
     * Bestimmt, ob die Buttons 'first' und 'last' angezeigt werden sollen. (Default=true)
     *
     * @param showFirstAndLast
     */
    public void showFirstAndLast(boolean showFirstAndLast) {
        this.doShowFirstAndLast = showFirstAndLast;
    }

    /**
     * Uebergibt der NavigationBar eine Liste, die durchlaufen werden kann.
     *
     * @param data
     */
    public void setData(List<?> data) {
        this.data = data;
        actual = 0;
        previousIndex = 0;
        validateButtons();
        showCount();
        notifyListener();
    }

    /**
     * Gibt die eine Liste mit den Nav-Objekten zurueck.
     *
     * @return
     */
    public List<?> getData() {
        return data;
    }

    /**
     * GUI erzeugen.
     */
    protected void createGUI() {
        actionListener = new ButtonActionListener();

        btnFirst = createButton(TYPE_FIRST);
        btnLast = createButton(TYPE_LAST);
        btnNext = createButton(TYPE_NEXT);
        btnPrevious = createButton(TYPE_PREVIOUS);

        if (doShowCount) {
            lblCount = new AKJLabel();
            Font font = lblCount.getFont();
            Font f = new Font(font.getName(), Font.PLAIN, font.getSize() - 1);
            lblCount.setFont(f);
        }

        this.setLayout(new GridBagLayout());
        int x = 0;
        if (doShowFirstAndLast) {
            this.add(btnFirst, GBCFactory.createGBC(0, 0, x++, 0, 1, 1, GridBagConstraints.NONE));
        }
        this.add(btnPrevious, GBCFactory.createGBC(0, 0, x++, 0, 1, 1, GridBagConstraints.NONE));
        this.add(btnNext, GBCFactory.createGBC(0, 0, x++, 0, 1, 1, GridBagConstraints.NONE));
        if (doShowFirstAndLast) {
            this.add(btnLast, GBCFactory.createGBC(0, 0, x++, 0, 1, 1, GridBagConstraints.NONE));
        }

        if (doShowCount) {
            this.add(lblCount, GBCFactory.createGBC(100, 0, x, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        }

        initialized = true;
        validateButtons();
        showCount();
        notifyListener();
    }

    /**
     * Scrollt zum ersten Datensatz
     */
    protected void showFirst() {
        previousIndex = actual;
        actual = 0;
        showCount();
        validateButtons();
        notifyListener();
    }

    /**
     * Scrollt zum letzten Datensatz
     */
    protected void showLast() {
        previousIndex = actual;
        actual = (data != null) ? data.size() - 1 : 0;
        showCount();
        validateButtons();
        notifyListener();
    }

    /**
     * Scrollt zum vorherigen Datensatz
     */
    protected void showPrevious() {
        previousIndex = actual;
        if (actual > 0) {
            actual -= 1;
            showCount();
            validateButtons();
            notifyListener();
        }
    }

    /**
     * Scrollt zum naechsten Datensatz
     */
    protected void showNext() {
        previousIndex = actual;
        actual = ((data != null) && ((data.size() - 1) > actual)) ? actual + 1 : actual;
        showCount();
        validateButtons();
        notifyListener();
    }

    /**
     * Navigiert zu dem Objekt das an der angegebenen Position (number) existiert. Wird eine ungueltige Nummer/Position
     * angegeben, wird auf Position '0' gesprungen.
     *
     * @param number
     */
    public void navigateTo(int number) {
        if ((number >= 0) && (number <= getNavCount())) {
            actual = number;
        }
        else {
            actual = 0;
        }

        showCount();
        validateButtons();
        notifyListener();
    }

    /**
     * Ruft auf jedem Objekt der NavigationBar die Methode <code>method</code> auf und ueberprueft, ob das Result mit
     * <code>equalValue</code> ueberein stimmt. Ist dies der Fall wird zu diesem Objekt navigiert.
     *
     * @param method     aufzurufende Methode auf den Navigation-Objekten
     * @param equalValue erwarteter Wert
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     *
     */
    public void navigateTo(String method, Object equalValue) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                Object obj = data.get(i);
                Object result = MethodUtils.invokeMethod(obj, method, null);
                if (result != null) {
                    if (result.equals(equalValue)) {
                        navigateTo(i);
                        break;
                    }
                }
                else if (equalValue == null) {
                    navigateTo(i);
                    break;
                }
            }
        }
    }

    /**
     * Navigiert zu dem letzten Objekt in der Nav-Bar.
     */
    public void navigateToLast() {
        showLast();
    }

    /**
     * Navigiert zu dem ersten Objekt in der Nav-Bar.
     */
    public void navigateToFirst() {
        showFirst();
    }

    /**
     * Gibt die Anzahl der navigierbaren Objekte zurueck.
     */
    public int getNavCount() {
        return (data != null) ? data.size() : 0;
    }

    /**
     * Gibt die aktuelle Position der NavigationBar zurueck.
     *
     * @return
     */
    public int getNavPosition() {
        return actual;
    }

    /**
     * Uebergibt der NavigationBar ein weiteres Objekt und springt zu dem uebergebenen Objekt.
     *
     * @param toAdd
     */
    @SuppressWarnings("unchecked")
    public void addNavigationObject(Object toAdd) {
        if (toAdd != null) {
            if (data == null) {
                data = new ArrayList<>();
            }

            data.add(toAdd);
            actual = data.size() - 1;
            showCount();
            validateButtons();
            notifyListener();
        }
    }

    /**
     * Fuegt ein Objekt der Navigation-Bar hinzu.
     *
     * @param pos   Position fuer das neue Objekt
     * @param toAdd
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addNavigationObjectAt(int pos, Object toAdd) {
        if (toAdd != null) {
            if (data == null) {
                data = new ArrayList();
            }

            if ((pos >= 0) && (pos <= data.size())) {
                data.add(pos, toAdd);
                showCount();
                validateButtons();
            }
        }
    }

    /**
     * Ersetzt ein Objekt in der Nav-Bar durch ein anderes Objekt
     *
     * @param index     Position des zu ersetzenden Elements.
     * @param newObject
     * @return true, wenn das Objekt erfolgreich ersetzt wurde.
     */
    @SuppressWarnings("unchecked")
    public boolean replaceNavigationObject(int index, Object newObject) {
        if (data != null) {
            try {
                data.set(index, newObject);
                return true;
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return false;
            }
        }
        return false;
    }

    /**
     * Fuegt alle Objekte der List der NavigationBar hinzu.
     *
     * @param toAdd
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addNavigationObjects(List toAdd) {
        if (toAdd != null) {
            if (data == null) {
                data = new ArrayList();
            }

            data.addAll(toAdd);
            validateButtons();
            showCount();
        }
    }

    /**
     * Entfernt das Objekt an dem angegebenen Index.
     *
     * @param index Index des Objekts, das entfernt werden soll.
     */
    public void removeNavigationObject(int index) {
        if ((data != null) && (index >= 0) && (index <= (data.size() - 1))) {
            data.remove(index);
            if (actual == index) {
                actual -= 1;
            }
            showCount();
            validateButtons();
            notifyListener();
        }
    }

    /**
     * Gibt das Objekt zurueck, das am Index <code>index</code> registriert ist. <br> Wichtig: Es wird nicht zu dem
     * Index 'gesprungen'!
     *
     * @param index Index des gewuenschten Objekts.
     * @return Objekt am Index oder <code>null</code>.
     */
    public Object getNavigationObjectAt(int index) {
        if ((data != null) && (index > -1) && (index <= (data.size() - 1))) {
            return data.get(index);
        }
        return null;
    }

    /* Stellt im Label die Anzahl Datensaetze dar. */
    private void showCount() {
        if (initialized && doShowCount) {
            if ((data == null) || (data.isEmpty())) {
                lblCount.setText("0 von 0");
            }
            else {
                StringBuilder sb = new StringBuilder();
                sb.append(actual + 1);
                sb.append(" von ");
                sb.append(data.size());

                lblCount.setText(sb.toString());
                lblCount.repaint();
            }
        }
    }

    /* Validiert die Navigations-Buttons */
    private void validateButtons() {
        if (initialized && enabled) {
            if ((data == null) || (data.isEmpty())) {
                btnFirst.setEnabled(false);
                btnLast.setEnabled(false);
                btnNext.setEnabled(false);
                btnPrevious.setEnabled(false);
            }
            else {
                if (actual == 0) {
                    btnFirst.setEnabled(false);
                    btnPrevious.setEnabled(false);
                    if (data.size() > 1) {
                        btnLast.setEnabled(true);
                        btnNext.setEnabled(true);
                    }
                    else {
                        btnLast.setEnabled(false);
                        btnNext.setEnabled(false);
                    }
                }
                else if ((actual > 0) && (actual < (data.size() - 1))) {
                    btnFirst.setEnabled(true);
                    btnLast.setEnabled(true);
                    btnNext.setEnabled(true);
                    btnPrevious.setEnabled(true);
                }
                else if (actual == (data.size() - 1)) {
                    btnFirst.setEnabled(true);
                    btnLast.setEnabled(false);
                    btnNext.setEnabled(false);
                    btnPrevious.setEnabled(true);
                }
            }
        }
    }

    /**
     * Fuegt der NavigationBar einen weiteren Listener hinzu.
     *
     * @param toAdd Listener fuer die Nav-Bar.
     */
    public void addNavigationListener(AKNavigationBarListener toAdd) {
        if (toAdd != null) {
            if (this.listener == null) {
                this.listener = new ArrayList<>();
            }
            this.listener.add(toAdd);
        }
    }

    /**
     * Benachrichtigt alle Listener darueber, welches Objekt angezeigt werden soll.
     */
    protected void notifyListener() {
        Object choosenObject = ((data != null) && (data.size() > actual) && (actual >= 0)) ? data.get(actual) : null;
        if (listener != null) {
            for (int i = 0; i < listener.size(); i++) {
                Object o = listener.get(i);
                if (o instanceof AKNavigationBarListener) {
                    try {
                        ((AKNavigationBarListener) o).showNavigationObject(choosenObject, actual);
                    }
                    catch (PropertyVetoException e) {
                        LOGGER.error(e.getMessage());
                        actual = previousIndex;
                        validateButtons();
                        showCount();
                    }
                }
            }
        }
    }

    /* Erzeugt einen Button fuer die Nav-Bar. */
    private AKJButton createButton(int type) {
        AKJButton btn = new AKJButton();
        btn.addActionListener(actionListener);

        String name = "";
        String tooltip = "";
        String icon = "";

        switch (type) {
            case TYPE_FIRST:
                name = "first";
                tooltip = "Zeigt den ersten Datensatz an";
                icon = "de/augustakom/common/gui/images/first.gif";
                break;

            case TYPE_LAST:
                name = "last";
                tooltip = "Zeigt den letzten Datensatz an";
                icon = "de/augustakom/common/gui/images/last.gif";
                break;

            case TYPE_NEXT:
                name = "next";
                tooltip = "Zeigt den nächsten Datensatz an";
                icon = "de/augustakom/common/gui/images/next.gif";
                break;

            case TYPE_PREVIOUS:
                name = "previous";
                tooltip = "Zeigt den vorherigen Datensatz an";
                icon = "de/augustakom/common/gui/images/previous.gif";
                break;

            default:
                throw new RuntimeException(String.format("Unknown type for navigation button! Type: %d", type));
        }

        btn.setActionCommand(name);
        btn.getAccessibleContext().setAccessibleName(name);
        btn.setToolTipText(tooltip);
        btn.setBorder(BorderFactory.createEmptyBorder());

        try {
            ImageIcon i = new IconHelper().getIcon(icon);
            btn.setIcon(i);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return btn;
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
     * @see java.awt.Component#addMouseListener(java.awt.event.MouseListener)
     */
    @Override
    public synchronized void addMouseListener(MouseListener l) {
        adminML = l;
        super.addMouseListener(adminML);

        // Workaround: den Buttons wird ein anderer
        // MouseListener uebergeben, der die Events mit einer
        // anderen Event-Source an <code>l</code> weiterleitet.
        NavBarMouseListener ml = new NavBarMouseListener(this);
        btnFirst.addMouseListener(ml);
        btnLast.addMouseListener(ml);
        btnNext.addMouseListener(ml);
        btnPrevious.addMouseListener(ml);
        lblCount.addMouseListener(ml);
    }

    /* ActionListener fuer die Navigations-Buttons */
    class ButtonActionListener implements ActionListener {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if ("first".equals(e.getActionCommand())) {
                showFirst();
            }
            else if ("last".equals(e.getActionCommand())) {
                showLast();
            }
            else if ("next".equals(e.getActionCommand())) {
                showNext();
            }
            else if ("previous".equals(e.getActionCommand())) {
                showPrevious();
            }
        }
    }

    /**
     * MouseListener fuer die Buttons, um dem AdminMouseListener die NavBar und nicht die Buttons als
     * AKManageableComponent zu uebergeben.
     *
     *
     */
    class NavBarMouseListener extends MouseAdapter {
        AKJNavigationBar navBar = null;

        /**
         * Konstruktor mit Angabe der Nav-Bar, die an den AdminMouseListener uebergeben werden soll.
         */
        public NavBarMouseListener(AKJNavigationBar navBar) {
            this.navBar = navBar;
        }

        /**
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            e.setSource(navBar);
            adminML.mousePressed(e);
        }

        /**
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            e.setSource(navBar);
            adminML.mouseReleased(e);
        }
    }
}


