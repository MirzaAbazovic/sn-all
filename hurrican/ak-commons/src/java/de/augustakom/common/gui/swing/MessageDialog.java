/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2004 09:36:43
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import de.augustakom.common.gui.awt.LocationHelper;
import de.augustakom.common.gui.swing.xml.XmlTextPane;
import de.augustakom.common.tools.lang.StringTools;

/**
 * Implementierung eines Dialogs, der eine Nachricht, dessen Nachrichtentyp und die gewünschten Buttons anzeigen kann.
 * Dabei kann der Dialog auch die in der übergebenen Nachricht enthalten Platzhalter ersetzen. Die möglichen
 * Nachrichtentypen (Auswirkung auf Dialog-Icon) sind aus einer der folgenden Konstanten zu wählen: <ul> <li>{@link
 * JOptionPane#ERROR_MESSAGE} -> use {@link AKJErrorDialog} instead</li> <li>{@link
 * JOptionPane#INFORMATION_MESSAGE}</li> <li>{@link JOptionPane#WARNING_MESSAGE}</li> <li>{@link
 * JOptionPane#QUESTION_MESSAGE}</li> <li>{@link JOptionPane#PLAIN_MESSAGE}</li> </ul> Außerdem werden ja nach Wahl der
 * folgenden Konstanten andere Buttons angezeigt: <ul> <li>{@link JOptionPane#DEFAULT_OPTION} -> nur OK-Button</li>
 * <li>{@link JOptionPane#YES_NO_OPTION}</li> <li>{@link JOptionPane#YES_NO_CANCEL_OPTION}</li> <li>{@link
 * JOptionPane#OK_CANCEL_OPTION}</li> </ul> Je nach gedrücktem Button wird eines der folgenden Ergebnisse zurückgegeben:
 * <ul> <li>{@link JOptionPane#YES_OPTION}</li> <li>{@link JOptionPane#NO_OPTION}</li> <li>{@link
 * JOptionPane#CANCEL_OPTION}</li> <li>{@link JOptionPane#OK_OPTION}</li> <li>{@link JOptionPane#CLOSED_OPTION} -> bei
 * Schließen über ESC oder X (rechts oben)</li> </ul>
 */
public class MessageDialog {

    private enum TextStyle {
        DEFAULT,
        XML
    }

    private static final Dimension DEFAULT_SIZE = new Dimension(400, 300);

    /**
     * Zeigt einen Dialog mit {@code message} und den darin ersetzten Variablen durch {@code messageParams} an. Je nach
     * Wahl von {@code modal} ist der Dialog modal oder nicht, dass heißt, bei {@code modal = true} muss der Dialog
     * geschlossen werden, bevor Hurrican witer benutzt werden kann. Zusätzlich wird der angezeigte Dialog auf den
     * übergebenen {@code owner} zentriert.
     *
     * @param owner         auf den der Dialog zentriert werden soll.
     * @param message       die angezeigt werden soll (evtl. vorhandene Parameter werden durch {@code messageParams}
     *                      ersetzt
     * @param title         der den Standard-Titel aus {@code messageType} überschreibt oder {@code null} für
     *                      Standard-Titel
     * @param modal         bestimmt, ob der Dialog modal (Hurrican kann erst nach Beendigung weiter benutzt werden)
     *                      ist
     * @param messageType   ja nach gewählter der folgenden Optionen werden Icon, Standard-Titel und Tab-Titel des
     *                      Dialogs gesetzt: <ul> <li>{@link JOptionPane#ERROR_MESSAGE} -> use {@link AKJErrorDialog}
     *                      instead</li> <li>{@link JOptionPane#INFORMATION_MESSAGE}</li> <li>{@link
     *                      JOptionPane#WARNING_MESSAGE}</li> <li>{@link JOptionPane#QUESTION_MESSAGE}</li> <li>{@link
     *                      JOptionPane#PLAIN_MESSAGE}</li> </ul>
     * @param optionType    bestimmt die anzeigten Buttons aus folgender Auswahl<ul> <li>{@link
     *                      JOptionPane#DEFAULT_OPTION} -> nur OK-Button</li> <li>{@link JOptionPane#YES_NO_OPTION}</li>
     *                      <li>{@link JOptionPane#YES_NO_CANCEL_OPTION}</li> <li>{@link JOptionPane#OK_CANCEL_OPTION}</li>
     *                      </ul>
     * @param messageParams Werte für die Parameter der Meldung {@code message}.
     * @return Dialog mit {@code modal = true} liefert Wert des gedrückten Buttons, andere Dialoge immer {@link
     * JOptionPane#CLOSED_OPTION}. Werte für Buttons sind:<ul> <li>{@link JOptionPane#YES_OPTION}</li> <li>{@link
     * JOptionPane#NO_OPTION}</li> <li>{@link JOptionPane#CANCEL_OPTION}</li> <li>{@link JOptionPane#OK_OPTION}</li>
     * <li>{@link JOptionPane#CLOSED_OPTION} -> bei Schließen über ESC oder X (rechts oben)</li> </ul>
     */
    static int show(Component owner, String message, String title, int messageType, int optionType,
            boolean modal, Object... messageParams) {
        return (new MessageDialog(TextStyle.DEFAULT, DEFAULT_SIZE, message, title, messageType, optionType, messageParams)).show(owner, modal);
    }

    static int show(Component owner, Dimension preferredSize, String message, String title, int messageType, int optionType,
            boolean modal, Object... messageParams) {
        return (new MessageDialog(TextStyle.DEFAULT, preferredSize, message, title, messageType, optionType, messageParams)).show(owner, modal);
    }

    /**
     * see {@link #show(Component, String, String, int, int, boolean, Object...)} mit {@code modal = true}
     */
    static int show(Component owner, String message, String title, int messageType, int optionType, Object... messageParams) {
        return show(owner, message, title, messageType, optionType, true, messageParams);
    }

    /**
     * see {@link #show(Component, String, String, int, int, boolean, Object...)} mit Standard-Titel und {@code modal =
     * true}
     */
    static int show(Component owner, String message, int messageType, int optionType, Object... messageParams) {
        return show(owner, message, null, messageType, optionType, true, messageParams);
    }

    static int showXml(Component owner, Dimension preferredSize, String message, String title, int messageType, int optionType,
            boolean modal, Object... messageParams) {
        return (new MessageDialog(TextStyle.XML, preferredSize, message, title, messageType, optionType, messageParams)).show(owner, modal);
    }

    private static final String RESOURCE = "de/augustakom/common/gui/resources/MessageDialog.xml";

    private enum MessageType {
        PLAIN(JOptionPane.PLAIN_MESSAGE, "plain"),
        INFO(JOptionPane.INFORMATION_MESSAGE, "info"),
        QUESTION(JOptionPane.QUESTION_MESSAGE, "question"),
        WARNING(JOptionPane.WARNING_MESSAGE, "warning"),
        ERROR(JOptionPane.ERROR_MESSAGE, "error");

        private final int option;
        private final String prefix;

        private MessageType(int option, String prefix) {
            this.option = option;
            this.prefix = prefix;
        }

        private static MessageType getMessageTypeFor(int messageTypeOfOptionPane) {
            for (MessageType messageType : MessageType.values()) {
                if (messageType.option == messageTypeOfOptionPane) {
                    return messageType;
                }
            }
            return PLAIN;
        }
    }

    private final String title;
    private final ImageIcon icon;
    private final JOptionPane optionPane;

    /**
     * Konstruktor fuer den Info-Dialog mit Angabe einer Message und Parametern. <br> Sind in dem String
     * <code>information</code> Platzhalter (z.B. <code>{0}</code>) enthalten, werden sie durch die Elemente von
     * <code>params</code> ersetzt.
     *
     * @param preferredSize
     * @param message       mit evtl. vorhandenen Platzhaltern
     * @param title         des Dialogs (überschreibt den Default-Titel)
     * @param messageType   Art der Nachricht aus {@link JOptionPane}
     * @param messageParams werden fuer die vorhandenen Platzhalter verwendet
     * @throws IllegalArgumentException falls übergebenen {@code messageType} nicht existiert in {@link JOptionPane}
     */
    private MessageDialog(TextStyle textStyle, Dimension preferredSize, String message, String title, int messageType, int optionType, Object... messageParams) {
        final MessageType msgType = MessageType.getMessageTypeFor(messageType);
        if (msgType == null) {
            throw new IllegalArgumentException("supplied message type does not exist in JOptionPane");
        }

        final SwingFactory swingFactory = SwingFactory.getInstance(RESOURCE);

        this.title = (title == null) ? swingFactory.getText(msgType.prefix + ".title") : title;
        icon = new IconHelper().getIcon(swingFactory.getText(msgType.prefix + ".icon"));

        final JTextComponent txMessage = (TextStyle.XML == textStyle) ? new XmlTextPane() : swingFactory.createTextArea("message.area");
        if (TextStyle.XML != textStyle) {
            ((AKJTextArea) txMessage).setWrapStyleWord(true);
            ((AKJTextArea) txMessage).setLineWrap(true);
            ((AKJTextArea) txMessage).setFontStyle(Font.BOLD);
        }
        txMessage.setEditable(false);
        txMessage.setText(StringTools.formatString(message, messageParams));

        final AKJTabbedPane tabbedPane = new AKJTabbedPane();
        tabbedPane.addTab(swingFactory.getText(msgType.prefix + ".tabtitle"), new JScrollPane(txMessage));
        tabbedPane.setPreferredSize(preferredSize);

        // required container such that resizing of JOptionPane and JDialog, respectively, works correctly
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.add(tabbedPane, BorderLayout.CENTER);

        Object oldBorder = UIManager.get("OptionPane.border");
        Object oldButtonAreaBorder = UIManager.get("OptionPane.buttonAreaBorder");
        try {
            UIManager.put("OptionPane.border", new EmptyBorder(0, 0, 0, 0));
            UIManager.put("OptionPane.buttonAreaBorder", new EmptyBorder(0, 0, 3, 0));
            optionPane = new JOptionPane("", JOptionPane.PLAIN_MESSAGE, optionType);
            optionPane.setMessage(container);
        }
        finally {
            UIManager.put("OptionPane.border", oldBorder);
            UIManager.put("OptionPane.buttonAreaBorder", oldButtonAreaBorder);
        }
    }

    /**
     * Zeigt den Dialog mit einem Eintrag in der TaskBar an (dazu {@code optionPane.createDialog(title)} ohne Parameter
     * {@code owner} aufrufen
     *
     * @param owner Owner fuer den der Dialog zentriert wird
     * @param modal Flag, ob der Dialog modal sein soll
     * @return die ausgewählte Option eines modalen ({@code modal = true}) Dialog, ansonsten {@link
     * JOptionPane#CLOSED_OPTION}
     */
    private int show(Component owner, boolean modal) {

        JDialog dialog = optionPane.createDialog(title);
        dialog.setModal(modal);
        dialog.setResizable(true);
        dialog.setIconImage((icon == null) ? null : icon.getImage());
        dialog.pack();
        dialog.setLocation(LocationHelper.getCenterLocation(owner, dialog));
        dialog.setVisible(true);
        dialog.addMouseListener(new MaximizeDialogMouseListener(dialog));

        Object result = optionPane.getValue();
        if (result instanceof Integer) {
            return (Integer) result;
        }
        return JOptionPane.CLOSED_OPTION;
    }


    /**
     * MouseListener, der durch einen Doppelklick auf den Dialog (untere Leiste neben dem OK-Button) diesen maximiert
     * bzw. wieder auf die urspruengliche Groesse setzt.
     */
    static class MaximizeDialogMouseListener extends MouseAdapter {
        private boolean isMaximized = false;
        private Dimension oldSize = null;
        private JDialog dialog;

        public MaximizeDialogMouseListener(JDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                if (isMaximized) {
                    isMaximized = false;
                    dialog.setPreferredSize(oldSize);
                    dialog.pack();
                }
                else {
                    oldSize = dialog.getSize();
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    isMaximized = true;
                    dialog.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight() - 25);
                    dialog.setLocation(0, 0);
                }
            }
        }
    }
}
