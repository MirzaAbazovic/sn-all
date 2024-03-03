/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.lang.reflect.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.LocationHelper;

/**
 * Hilfsklasse, um Dialoge/Windows mit Eintrag in der TaskBar darzustellen.
 *
 *
 */
public class DialogHelper {

    private static final Logger LOGGER = Logger.getLogger(DialogHelper.class);

    /**
     * Zeigt den Dialog mit Eintrag in der TaskBar an.
     *
     * @param owner        Owner des Dialogs
     * @param dialog       Dialog, der angezeigt werden soll
     * @param modal        Flag, ob der Dialog modal sein soll
     * @param centerDialog Flag, ob der Dialog innerhalb des Owners zentriert werden soll.
     */
    public static void showDialog(Component owner, AKJDialog dialog, boolean modal, boolean centerDialog) {
        AKJFrame frame = new AKJFrame();
        frame.setBounds(-10000, -10000, 0, 0); // Frame 'verstecken'
        frame.setTitle(dialog.getTitle());
        frame.addWindowListener(new FrameActivateListener(dialog));
        frame.setIconImage(getIcon(owner));
        frame.setVisible(true);

        if (centerDialog) {
            dialog.setLocation(LocationHelper.getCenterLocation(owner, dialog));
        }
        dialog.setModal(modal);
        dialog.pack();
        dialog.addWindowListener(new DialogCloseListener(frame, true));
        dialog.setVisible(true);
    }

    /**
     * @see DialogHelper#showDialog(Component, AKJOptionDialog, boolean, boolean, PropertyChangeListener)
     */
    public static Object showDialog(Component owner, AKJOptionDialog optionDialog, boolean modal,
            boolean centerDialog) {
        return showDialog(owner, optionDialog, modal, centerDialog, null);
    }

    /**
     * Zeigt den OptionDialog mit Eintrag in der TaskBar an.
     *
     * @param owner           Owner fuer den Dialog
     * @param optionDialog    Dialog, der angezeigt werden soll
     * @param modal           Flag, ob der Dialog modal sein soll
     * @param centerDialog    Flag, ob der Dialog innerhalb des Owner zentriert werden soll.
     * @param listener4Dialog Window-Listener fuer den Dialog
     */
    public static Object showDialog(Component owner, AKJOptionDialog optionDialog, boolean modal,
            boolean centerDialog, PropertyChangeListener listener4Dialog) {
        AKJFrame frame = new AKJFrame();
        ImageIcon icon = new IconHelper().getIcon(optionDialog.getIconURL());
        if (icon != null) {
            frame.setIconImage(icon.getImage());
        }

        JDialog dialog = optionDialog.createDialog(frame, optionDialog.getTitle());

        frame.setBounds(-10000, -10000, 0, 0); // Frame 'verstecken'
        frame.setTitle(optionDialog.getTitle());
        frame.addWindowListener(new FrameActivateListener(dialog));
        frame.setVisible(true);

        /*
         * Der AKJOptionDialog leitet von JOptionPane ab. Dadurch kann kein
         * WindowListener verwendet werden, um ueber das Schliessen benachrichtigt
         * zu werden. Dies muss ueber einen PropertyChangeListener erfolgen.
         */
        optionDialog.addPropertyChangeListener(new OptionDialogPropertyChangeListener(frame, optionDialog));
        if (listener4Dialog != null) {
            optionDialog.addPropertyChangeListener(listener4Dialog);
        }

        if (centerDialog) {
            Point location = LocationHelper.getCenterLocation(owner, dialog);
            dialog.setLocation(location);
        }
        dialog.setModal(modal);
        dialog.setResizable(true);
        DialogCloseListener closeListener = new DialogCloseListener(frame);
        dialog.addWindowListener(closeListener);
        dialog.pack();
        dialog.setVisible(true);

        Object result = optionDialog.getValue();
        // wird im JOptionDialog 'ESC' gedrueckt, muss das Frame auch geschlossen werden!
        if ((result instanceof Number) && ((Number) result).equals(AKJOptionDialog.CLOSED_OPTION)) {
            closeListener.windowClosing(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
        return result;
    }

    /**
     * Checks the result returned by {@link #showDialog(Component, AKJDialog, boolean, boolean)} returning true if the
     * dialog was cancelled using the cancel button, escape key or by closing the window.
     * The check assumes that the dialog uses the correct return options (refer to {@link AKJOptionDialog} return options)
     * @param returnedResult result returned from showDialog(..)
     * @return true if dialog was closed.
     */
    public static boolean wasDialogCancelled(Object returnedResult) {
        if (returnedResult == null) {
            return true;
        }
        else if (returnedResult instanceof Number) {
            Number resAsNum = (Number) returnedResult;
            if (resAsNum.equals(AKJOptionDialog.CLOSED_OPTION) || resAsNum.equals(AKJOptionDialog.CANCEL_OPTION) || resAsNum.equals(AKJOptionDialog.NO_OPTION)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Zeigt das Window mit Eintrag in der TaskBar an.
     *
     * @param window       Window, das angezeigt werden soll.
     * @param centerWindow Flag, ob das Window zentriert dargestellt werden soll.
     */
    public static void showWindow(AKJWindow window, boolean centerWindow) {
        if (centerWindow) {
            window.setLocation(LocationHelper.getCenterLocation(null, window));
        }
        window.setVisible(true);
    }

    /**
     * Sollte das uebergebene Objekt die Methode <code>getIconImage</code> besitzen, so wird diese aufgerufen und das
     * erhaltene Image zurueck gegeben.
     *
     * @param comp
     * @return
     */
    private static Image getIcon(Component comp) {
        try {
            if (comp != null) {
                Method m = comp.getClass().getMethod("getIconImage", new Class[0]);
                Object result = m.invoke(comp, new Object[0]);
                if (result instanceof Image) {
                    return (Image) result;
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }
}

/**
 * WindowListener fuer ein Hilfs-Frame. <br> Wird das Frame aktiviert (ueber die TaskBar), dann wird der zugehörige
 * Dialog auch aktiviert.
 *
 *
 */
class FrameActivateListener extends WindowAdapter {

    private Window window4Frame = null;

    /**
     * Konstruktor mit Angabe des Windows, das von dem Listener aktiviert werden soll.
     *
     * @param window4Frame
     */
    public FrameActivateListener(Window window4Frame) {
        super();
        this.window4Frame = window4Frame;
    }

    /**
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowActivated(WindowEvent e) {
        window4Frame.toFront();
    }
}

/**
 * WindowListener fuer den darzustellenden Dialog. <br> Das Hilfsframe des Dialogs muss dem Listener uebergeben werden.
 * <br> Wird der Dialog geschlossen, wird auch das zugehoerige Frame beendet.
 *
 *
 */
class DialogCloseListener extends WindowAdapter {

    private JFrame frame4Dialog = null;
    private boolean listen4Closed = false;

    /**
     * Konstruktor mit Angabe des Frames, das von dem Listener geschlossen werden soll. <br>
     *
     * @param frame4Dialog
     */
    public DialogCloseListener(JFrame frame4Dialog) {
        super();
        this.frame4Dialog = frame4Dialog;
    }

    /**
     * Konstruktor mit Angabe des Frames, das von dem Listener geschlossen werden soll. <br>
     *
     * @param frame4Dialog  zu schliessendes Frame
     * @param listen4Closed Flag, ob nur <code>windowClosing</code> (false) oder auch <code>windowClosed</code> (true)
     *                      Events beachtet werden sollen.
     */
    public DialogCloseListener(JFrame frame4Dialog, boolean listen4Closed) {
        super();
        this.frame4Dialog = frame4Dialog;
        this.listen4Closed = listen4Closed;
    }

    /**
     * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosed(WindowEvent e) {
        if (listen4Closed) {
            frame4Dialog.dispose();
        }
    }

    /**
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent e) {
        frame4Dialog.dispose();
    }
}

/**
 * PropertyChange-Listener fuer einen AKJOptionDialog. <br> Aendert sich der Wert des OptionDialogs auf
 * <code>OK_OPTION</code>, dann wird das uebergebene Frame geschlossen.
 *
 *
 */
class OptionDialogPropertyChangeListener implements PropertyChangeListener {

    private JFrame frame4Dialog = null;
    private AKJOptionDialog optionDialog = null;

    /**
     * Konstruktor mit Angabe des Frames, das geschlossen werden soll, wenn sich der Wert des OptionDialogs auf
     * <code>OK_OPTION</code> aendert.
     *
     * @param frame4Dialog Das Frame, das fuer den Dialog in der TaskBar dargestellt wird
     * @param optionDialog Der OptionDialog.
     */
    public OptionDialogPropertyChangeListener(JFrame frame4Dialog, AKJOptionDialog optionDialog) {
        this.frame4Dialog = frame4Dialog;
        this.optionDialog = optionDialog;
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() instanceof Integer) {
            int value = ((Integer) evt.getNewValue()).intValue();
            if ((value == AKJOptionDialog.OK_OPTION)) {
                frame4Dialog.dispose();
            }
            else if ((value == AKJOptionDialog.CANCEL_OPTION) || (value == AKJOptionDialog.NO_OPTION)) {
                frame4Dialog.dispose();
            }
            else if (optionDialog.willClose()) {
                frame4Dialog.dispose();
            }
        }
        else if (optionDialog.willClose()) {
            optionDialog.removePropertyChangeListener(this);
            frame4Dialog.dispose();
        }
    }

}
