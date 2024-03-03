/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import de.augustakom.common.tools.lang.StringTools;

/**
 * Ueber den MessageHelper koennen Benutzerbenachrichtigungen angezeigt werden. <br> Die Methoden entsprechen zum
 * groessten Teil denen von <code>javax.swing.JOptionPane</code>. Die Anzeige von Benutzerbenachrichtigungen sollte
 * jedoch stets ueber diese Klasse erfolgen, da eine spaetere Aenderung der Dialoge hierueber einfacher zu realisieren
 * ist. <br><br>
 * <p/>
 * Fuer genauere Beschreibungen ueber die Verwendungen der einzelnen Methoden sollte deshalb auch die JavaDOC von
 * <code>java.swing.JOptionPane</code> herangezogen werden.
 *
 *
 */
public class MessageHelper {

    private static List<Throwable> errorList = null;
    private static List<Component> errorComp = null;
    private static ErrorDlgCloseListener errorDlgCloseListener = null;

    static {
        errorList = new ArrayList<Throwable>();
        errorComp = new ArrayList<Component>();
        errorDlgCloseListener = new ErrorDlgCloseListener(errorList, errorComp);
    }

    /**
     * @see javax.swing.JOptionPane#showInputDialog(Object)
     */
    public static String showInputDialog(Object message) {
        return JOptionPane.showInputDialog(message);
    }

    /**
     * @see javax.swing.JOptionPane#showInputDialog(Object, Object)
     */
    public static String showInputDialog(Object message, Object initialSelectionValue) {
        return JOptionPane.showInputDialog(null, message, initialSelectionValue);
    }

    /**
     * @see javax.swing.JOptionPane#showInputDialog(Component, Object)
     */
    public static String showInputDialog(Component parentComponent, Object message) {
        return JOptionPane.showInputDialog(parentComponent, message);
    }

    /**
     * @see javax.swing.JOptionPane#showInputDialog(Component, Object, Object)
     */
    public static String showInputDialog(Component parentComponent, Object message, Object initialSelectionValue) {
        return JOptionPane.showInputDialog(parentComponent, message, initialSelectionValue);
    }

    /**
     * @see javax.swing.JOptionPane#showInputDialog(Component, Object, String, int)
     */
    public static String showInputDialog(Component parentComponent, Object message, String title, int messageType) {
        return JOptionPane.showInputDialog(parentComponent, message, title, messageType);
    }

    /**
     * @see javax.swing.JOptionPane#showInputDialog(Component, Object, String, int, Icon, Object[], Object)
     */
    public static Object showInputDialog(Component parentComponent,
            Object message, String title, int messageType, Icon icon,
            Object[] selectionValues, Object initialSelectionValue) {
        return JOptionPane.showInputDialog(parentComponent, message, title, messageType, icon,
                selectionValues, initialSelectionValue);
    }

    /**
     * Zeigt einen Dialog mit einer JComboBox an.
     *
     * @param parentComponent Parent fuer den Dialog
     * @param values          Objekte fuer die ComboBox
     * @param renderer        Renderer fuer die ComboBox
     * @param dialogTitle     Dialog-Titel
     * @param message         Message
     * @param selectionName   Name der ComboBox
     * @return ausgewaehltes Objekt oder <code>null</code>.
     *
     */
    public static Object showInputDialog(Component parentComponent, List<?> values, ListCellRenderer renderer,
            String dialogTitle, String message, String selectionName) {
        AKJSelectionDialog dlg = new AKJSelectionDialog(values, renderer, dialogTitle, message, selectionName);
        Object result = DialogHelper.showDialog(parentComponent, dlg, true, true);
        return result;
    }

    /**
     * @see javax.swing.JOptionPane#showMessageDialog(Component, Object)
     */
    public static void showMessageDialog(Component parentComponent, Object message) {
        JOptionPane.showMessageDialog(parentComponent, message);
    }

    /**
     * @see javax.swing.JOptionPane#showMessageDialog(Component, Object, String, int)
     */
    public static void showMessageDialog(Component parentComponent,
            Object message, String title, int messageType) {
        JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
    }

    /**
     * Zeigt einen MessageDialog an. <br> Platzhalter (z.B. {0}) in dem String <code>message</code> werden durch die
     * entsprechenden Werte von <code>msgParams</code> ersetzt.
     */
    public static void showMessageDialog(Component parentComponent,
            String message, Object[] msgParams, String title, int messageType) {
        String msg = StringTools.formatString(message, msgParams, null);
        JOptionPane.showMessageDialog(parentComponent, msg, title, messageType);
    }

    /**
     * @see javax.swing.JOptionPane#showMessageDialog(Component, Object, String, int, Icon)
     */
    public static void showMessageDialog(Component parentComponent,
            Object message, String title, int messageType, Icon icon) {
        JOptionPane.showMessageDialog(parentComponent, message, title, messageType, icon);
    }

    /**
     * @see javax.swing.JOptionPane#showConfirmDialog(Component, Object)
     */
    public static int showConfirmDialog(Component parentComponent, Object message) {
        return JOptionPane.showConfirmDialog(parentComponent, message);
    }

    /**
     * @see javax.swing.JOptionPane#showConfirmDialog(Component, Object, String, int)
     */
    public static int showConfirmDialog(Component parentComponent,
            Object message, String title, int optionType) {
        return JOptionPane.showConfirmDialog(parentComponent, message, title, optionType);
    }

    /**
     * Platzhalter in <code>message</code> werden durch <code>msgParams</code> ersetzt.
     *
     * @see javax.swing.JOptionPane#showConfirmDialog(Component, Object, String, int)
     */
    public static int showConfirmDialog(Component parentComponent,
            String message, Object[] msgParams, String title, int optionType) {
        String msg = StringTools.formatString(message, msgParams, null);
        return JOptionPane.showConfirmDialog(parentComponent, msg, title, optionType);
    }

    /**
     * @see javax.swing.JOptionPane#showConfirmDialog(Component, Object, String, int, int)
     */
    public static int showConfirmDialog(Component parentComponent,
            Object message, String title, int optionType, int messageType) {
        return JOptionPane.showConfirmDialog(parentComponent, message, title, optionType, messageType);
    }

    /**
     * Platzhalter in <code>message</code> werden durch <code>msgParams</code> ersetzt.
     *
     * @see javax.swing.JOptionPane#showConfirmDialog(Component, Object, String, int, int)
     */
    public static int showConfirmDialog(Component parentComponent,
            String message, Object[] msgParams, String title, int optionType, int messageType) {
        String msg = StringTools.formatString(message, msgParams, null);
        return JOptionPane.showConfirmDialog(parentComponent, msg, title, optionType, messageType);
    }

    /**
     * @see javax.swing.JOptionPane#showConfirmDialog(Component, Object, String, int, int, Icon)
     */
    public static int showConfirmDialog(Component parentComponent,
            Object message, String title, int optionType, int messageType, Icon icon) {
        return JOptionPane.showConfirmDialog(parentComponent, message, title,
                optionType, messageType, icon);
    }

    /**
     * @see javax.swing.JOptionPane#showOptionDialog(Component, Object, String, int, int, Icon, Object[], Object)
     */
    public static int showOptionDialog(Component parentComponent,
            Object message, String title, int optionType, int messageType,
            Icon icon, Object[] options, Object initialValue) {
        return JOptionPane.showOptionDialog(parentComponent, message,
                title, optionType, messageType, icon, options, initialValue);
    }

    /**
     * @see javax.swing.JOptionPane#showInternalMessageDialog(Component, Object)
     */
    public static void showInternalMessageDialog(Component parentComponent, Object message) {
        JOptionPane.showInternalMessageDialog(parentComponent, message);
    }

    /**
     * @see javax.swing.JOptionPane#showInternalMessageDialog(Component, Object, String, int)
     */
    public static void showInternalMessageDialog(Component parentComponent,
            Object message, String title, int messageType) {
        JOptionPane.showInternalMessageDialog(parentComponent, message, title, messageType);
    }

    /**
     * @see javax.swing.JOptionPane#showInternalMessageDialog(Component, Object, String, int, Icon)
     */
    public static void showInternalMessageDialog(Component parentComponent,
            Object message, String title, int messageType, Icon icon) {
        JOptionPane.showInternalMessageDialog(parentComponent, message,
                title, messageType, icon);
    }

    /**
     * @see javax.swing.JOptionPane#showInternalConfirmDialog(Component, Object)
     */
    public static int showInternalConfirmDialog(Component parentComponent, Object message) {
        return JOptionPane.showInternalConfirmDialog(parentComponent, message);
    }

    /**
     * @see javax.swing.JOptionPane#showInternalConfirmDialog(Component, Object, String, int)
     */
    public static int showInternalConfirmDialog(Component parentComponent,
            Object message, String title, int optionType) {
        return JOptionPane.showInternalConfirmDialog(parentComponent, message,
                title, optionType);
    }

    /**
     * @see javax.swing.JOptionPane#showInternalConfirmDialog(Component, Object, String, int, int)
     */
    public static int showInternalConfirmDialog(Component parentComponent,
            Object message, String title, int optionType, int messageType) {
        return JOptionPane.showInternalConfirmDialog(parentComponent,
                message, title, optionType, messageType);
    }

    /**
     * @see javax.swing.JOptionPane#showInternalConfirmDialog(Component, Object, String, int, int, Icon)
     */
    public static int showInternalConfirmDialog(Component parentComponent,
            Object message, String title, int optionType, int messageType, Icon icon) {
        return JOptionPane.showInternalConfirmDialog(parentComponent,
                message, title, optionType, messageType, icon);
    }

    /**
     * @see javax.swing.JOptionPane#showInternalOptionDialog(Component, Object, String, int, int, Icon, Object[],
     * Object)
     */
    public static int showInternalOptionDialog(Component parentComponent,
            Object message, String title, int optionType, int messageType, Icon icon,
            Object[] options, Object initialValue) {
        return JOptionPane.showInternalOptionDialog(parentComponent,
                message, title, optionType, messageType, icon, options, initialValue);
    }

    /**
     * @see javax.swing.JOptionPane#showInternalInputDialog(Component, Object)
     */
    public static String showInternalInputDialog(Component parentComponent, Object message) {
        return JOptionPane.showInternalInputDialog(parentComponent, message);
    }

    /**
     * @see javax.swing.JOptionPane#showInternalInputDialog(Component, Object, String, int)
     */
    public static String showInternalInputDialog(Component parentComponent, Object message, String title, int messageType) {
        return JOptionPane.showInternalInputDialog(parentComponent, message, title, messageType);
    }

    /**
     * @see javax.swing.JOptionPane#showInternalInputDialog(Component, Object, String, int, Icon, Object[], Object)
     */
    public static Object showInternalInputDialog(Component parentComponent,
            Object message, String title, int messageType, Icon icon,
            Object[] selectionValues, Object initialSelectionValue) {
        return JOptionPane.showInternalInputDialog(parentComponent,
                message, title, messageType, icon, selectionValues, initialSelectionValue);
    }

    //////////////////////////////////////////////////////////////
    // Methoden fuer die Anzeige von Dialogen, die nicht auf JOptionPane basieren.

    /**
     * Zeigt einen {@link MessageDialog} mit den Buttons Yes/No an. Der Typ des {@link MessageDialog} ist {@link
     * JOptionPane#QUESTION_MESSAGE}.
     *
     * @param parent Parent-Komponente
     * @param msg    Text
     * @param title  Titel
     * @return {@link JOptionPane#YES_OPTION} oder {@link JOptionPane#NO_OPTION}
     */
    public static int showYesNoQuestion(Component parent, String msg, String title) {
        return MessageDialog.show(parent, msg, title, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
    }

    /**
     * Zeigt einen {@link MessageDialog} mit den Buttons Yes, No und Cancel an. Der Typ des {@link MessageDialog} ist
     * {@link JOptionPane#QUESTION_MESSAGE}.
     *
     * @param parent Parent-Komponente
     * @param msg    Text
     * @param title  Titel
     * @return {@link JOptionPane#YES_OPTION}, {@link JOptionPane#NO_OPTION} oder {@link JOptionPane#CANCEL_OPTION}
     */
    public static int showYesNoCancelQuestion(Component parent, String msg, String title) {
        return MessageDialog.show(parent, msg, title, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
    }

    /**
     * Zeigt einen Info-Dialog mit <code>message</code> und evtl. <code>params</code> an.
     *
     * @param parentComponent Owner fuer den Dialog.
     * @param message         Info-Text, der angezeigt werden soll. (Evtl. vorhandene Parameter werden durch die
     *                        Elemente von <code>params</code> ersetzt.)
     * @param params          Parameter fuer den Info-Text.
     */
    public static void showInfoDialog(Component parentComponent, String message, Object... params) {
        MessageDialog.show(parentComponent, message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, params);
    }

    /**
     * Zeigt einen Info-Dialog mit <code>message</code> und evtl. <code>params</code> an.
     *
     * @param parentComponent Owner fuer den Dialog.
     * @param message         Info-Text, der angezeigt werden soll. (Evtl. vorhandene Parameter werden durch die
     *                        Elemente von <code>params</code> ersetzt.)
     * @param params          Parameter fuer den Info-Text.
     * @param modal           Dialog-Modus
     */
    public static void showInfoDialog(Component parentComponent, String message, Object[] params, boolean modal) {
        MessageDialog.show(parentComponent, message, null, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, modal, params);
    }

    /**
     * Zeigt einen Info-Dialog mit <code>message</code> und evtl. <code>params</code> an.
     *
     * @param parentComponent Owner fuer den Dialog.
     * @param message         Info-Text, der angezeigt werden soll. (Evtl. vorhandene Parameter werden durch die
     *                        Elemente von <code>params</code> ersetzt.)
     * @param title           Titel fuer den Dialog
     * @param params          Parameter fuer den Info-Text.
     * @param modal           Dialog-Modus
     */
    public static void showInfoDialog(Component parentComponent, String message, String title, Object[] params, boolean modal) {
        MessageDialog.show(parentComponent, message, title, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, modal, params);
    }

    /**
     * @param parentComponent
     * @param preferredSize
     * @param message
     * @param title
     * @param params
     * @param modal
     * @see MessageHelper#showInfoDialog(Component, String, String, Object[], boolean)
     */
    public static void showInfoDialog(Component parentComponent, Dimension preferredSize, String message, String title, Object[] params, boolean modal) {
        MessageDialog.show(parentComponent, preferredSize, message, title, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, modal, params);
    }

    /**
     * Zeigt einen Info-Dialog an, bei dem der Inhalt (die Message) als XML-String formattiert wird.
     */
    public static void showInfoDialogWithXml(Component parentComponent, Dimension preferredSize, String message, String title, Object[] params, boolean modal) {
        MessageDialog.showXml(parentComponent, preferredSize, message, title, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, modal, params);
    }

    /**
     * Zeigt einen Warnung-Dialog mit <code>message</code> und evtl. <code>params</code> mit {@code modal = true} an.
     *
     * @param parentComponent Owner fuer den Dialog.
     * @param message         Info-Text, der angezeigt werden soll. (Evtl. vorhandene Parameter werden durch die
     *                        Elemente von <code>params</code> ersetzt.)
     * @param title           Titel fuer den Dialog
     * @param optionType      für die anzuzeigenden Buttons aus {@link JOptionPane}
     * @param params          Parameter fuer den Info-Text.
     */
    public static int showWarningDialog(Component parentComponent, String message, String title, int optionType, Object... params) {
        return MessageDialog.show(parentComponent, message, title, JOptionPane.WARNING_MESSAGE, optionType, params);
    }

    /**
     * Zeigt einen Warnung-Dialog mit <code>message</code> und evtl. <code>params</code> mit Ok-Button an.
     *
     * @param parentComponent Owner fuer den Dialog.
     * @param message         Info-Text, der angezeigt werden soll. (Evtl. vorhandene Parameter werden durch die
     *                        Elemente von <code>params</code> ersetzt.)
     * @param modal           Dialog-Modus
     * @param params          Parameter fuer den Info-Text.
     */
    public static void showWarningDialog(Component parentComponent, String message, boolean modal, Object... params) {
        MessageDialog.show(parentComponent, message, null, JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION, modal, params);
    }

    /**
     * Zeigt das Throwable-Objekt <code>error</code> in einem Dialog an. <br> Es wird immer nur ein Error-Dialog
     * angezeigt! Werden mehrere Fehler nahezu zeitgleich gemeldet, waehrend ein Dialog geoeffnet ist, werden diese
     * zwischengespeichert und erst dann angezeigt, wenn der vorherige Error-Dialog geschlossen wird. <br> Die Anzeige
     * der Fehler erfolgt nach dem FIFO-Prinzip.
     *
     * @param parentComponent Owner fuer den Dialog.
     * @param error           Fehler-Objekt, das angezeigt werden soll.
     */
    public static void showErrorDialog(Component parentComponent, Throwable error) {
        errorList.add(error);
        errorComp.add(parentComponent);
        if (errorList.size() == 1) {
            displayError(errorComp.get(0), errorList.get(0));
        }
    }

    /**
     * Zeigt alle Throwable-Objekte aus <code>errors</code> in einem Dialog an.
     *
     * @param parentComponent Owner fuer den Dialog
     * @param errors          Liste mit den anzuzeigenden Fehlern.
     */
    public static void showErrorDialog(Component parentComponent, List<Throwable> errors) {
        AKJErrorDialog dlg = new AKJErrorDialog(errors);
        DialogHelper.showDialog(parentComponent, dlg, true, true, errorDlgCloseListener);
    }

    /* Zeigt einen Error-Dialog an. */
    static void displayError(Component parentComponent, Throwable error) {
        AKJErrorDialog dlg = new AKJErrorDialog(error);
        DialogHelper.showDialog(parentComponent, dlg, true, true, errorDlgCloseListener);
    }

    /**
     * @param parent Owner fuer den Dialog
     * @param error  Fehler-Objekt
     * @param force  Angabe, ob der Dialog sofort angezeigt werden soll (true) oder nach dem FIFO-Prinzip (false).
     * @see MessageHelper.showErrorDialog(Component, Throwable) Ueber diese Methode kann jedoch alternativ erzwungen
     * werden, dass der Error-Dialog sofort angezeigt wird.
     */
    public static void showErrorDialog(Component parent, Throwable error, boolean force) {
        if (force) {
            displayError(parent, error);
        }
        else {
            MessageHelper.showErrorDialog(parent, error);
        }
    }
}

/* Listener fuer den Error-Dialog, um Errors nach dem FIFO-Prinzip anzuzeigen. */
class ErrorDlgCloseListener implements PropertyChangeListener {
    private List<Throwable> errorList = null;
    private List<Component> errorComp = null;

    /**
     * Konstruktor
     *
     * @param errorList
     * @param errorComp
     */
    ErrorDlgCloseListener(List<Throwable> errorList, List<Component> errorComp) {
        super();
        this.errorList = errorList;
        this.errorComp = errorComp;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (errorList.size() > 1) {
            errorList.remove(0);
            Component parent = null;
            if (errorComp.size() > 1) {
                errorComp.remove(0);
                parent = errorComp.get(0);
            }
            MessageHelper.displayError(parent, errorList.get(0));
        }
        else if (errorList.size() == 1) {
            errorList.remove(0);
            errorComp.remove(0);
        }
    }
}

