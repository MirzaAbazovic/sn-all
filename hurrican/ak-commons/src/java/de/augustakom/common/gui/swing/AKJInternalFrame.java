/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import de.augustakom.common.gui.iface.AKMenuOwner;
import de.augustakom.common.gui.iface.AKMenuOwnerListener;


/**
 * AK-Implementierung eines JInternalFrames.
 *
 *
 * @see javax.swing.JInternalFrame
 */
public class AKJInternalFrame extends JInternalFrame implements AKMenuOwner {

    private AbstractMDIMainFrame mainFrame = null;

    protected EventListenerList menuOwnerListenerList = new EventListenerList();

    /**
     * Erzeugt ein neues Internal-Frame. <br> Das Internal-Frame ist non-resizable, non-closable, non-maximizable und
     * non-iconifiable.
     */
    public AKJInternalFrame() {
        super();
        init();
    }

    /**
     * Erzeugt ein neues Internal-Frame mit Angabe des Titels. <br> Das Internal-Frame ist non-resizable, non-closable,
     * non-maximizable und non-iconifiable.
     *
     * @param title
     */
    public AKJInternalFrame(String title) {
        super(title);
        init();
    }

    /**
     * Erzeugt ein neues Internal-Frame mit Angabe des Titels und der Resize-Option. <br> Das Internal-Frame ist
     * non-closable, non-maximizable und non-iconifiable.
     *
     * @param title
     * @param resizable
     */
    public AKJInternalFrame(String title, boolean resizable) {
        super(title, resizable);
        init();
    }

    /**
     * Erzeugt ein neues Internal-Frame mit Angabe des Titels, der Resize-Option und der Closable-Option <br> Das
     * Internal-Frame ist non-maximizable und non-iconifiable.
     *
     * @param title
     * @param resizable
     * @param closable
     */
    public AKJInternalFrame(String title, boolean resizable, boolean closable) {
        super(title, resizable, closable);
        init();
    }

    /**
     * Erzeugt ein neues Internal-Frame mit Angabe des Titels, der Resize-, Closable- und Maximizable-Option. <br> Das
     * Internal-Frame ist non-iconifiable.
     *
     * @param title
     * @param resizable
     * @param closable
     * @param maximizable
     */
    public AKJInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable) {
        super(title, resizable, closable, maximizable);
        init();
    }

    /**
     * Erzeugt ein neues Internal-Frame mit Angabe des Titels, der Resize-, Closable-, Maximizable- und
     * Iconifiable-Option.
     *
     * @param title
     * @param resizable
     * @param closable
     * @param maximizable
     * @param iconifiable
     */
    public AKJInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
        super(title, resizable, closable, maximizable, iconifiable);
        init();
    }

    /**
     * Gibt einen eindeutigen Namen fuer das Frame zurueck. <br> Standardmaessig liefert diese Methode den Wert von
     * <code>getClass().getName()</code> zurueck. Dieser Wert ist jedoch nicht zwangslaeufig eindeutig! <br> Die
     * Ableitungen sollten diese Methode ueberschreiben, sofern der Klassenname nicht als eindeutiger Name dienen
     * kann/soll.
     *
     * @return
     */
    public String getUniqueName() {
        return getClass().getName();
    }

    /**
     * Kann aufgerufen werden, um das Frame darueber zu informieren, das es geschlossen werden soll. <br>
     */
    public void frameWillClose() {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKMenuOwner#getMenuOfOwner()
     */
    public AKJMenu getMenuOfOwner() {
        return null;
    }

    /**
     * Uebergibt dem InternalFrame das MainFrame, in dem es dargestellt wird.
     *
     * @param mainFrame
     */
    public void setMainFrame(AbstractMDIMainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    /**
     * Gibt das MainFrame zurueck, in dem das InternalFrame dargestellt wird.
     *
     * @return
     */
    public AbstractMDIMainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKMenuOwner#addMenuOwnerListener(de.augustakom.common.gui.iface.AKMenuOwnerListener)
     */
    public void addMenuOwnerListener(AKMenuOwnerListener listener) {
        menuOwnerListenerList.add(AKMenuOwnerListener.class, listener);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKMenuOwner#removeMenuOwnerListener(de.augustakom.common.gui.iface.AKMenuOwnerListener)
     */
    public void removeMenuOwnerListener(AKMenuOwnerListener listener) {
        menuOwnerListenerList.remove(AKMenuOwnerListener.class, listener);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKMenuOwner#notifyMenuOwnerListeners()
     */
    public void notifyMenuOwnerListeners() {
        AKMenuOwnerListener[] listener = menuOwnerListenerList.getListeners(AKMenuOwnerListener.class);
        for (int i = 0; i < listener.length; i++) {
            listener[i].menuChanged(this);
        }
    }


    private void init() {
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doDefaultCloseAction();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }
}
