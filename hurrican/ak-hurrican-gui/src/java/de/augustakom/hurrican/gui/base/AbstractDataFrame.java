/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2004 07:55:34
 */
package de.augustakom.hurrican.gui.base;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;


/**
 * Abstrakte Implementierung eines InternalFrames, das Daten darstellen, anlegen und speichern kann.
 *
 *
 */
public abstract class AbstractDataFrame extends AbstractInternalServiceFrame implements AKModelOwner {

    private static final Logger LOGGER = Logger.getLogger(AbstractDataFrame.class);

    protected CloseController closeController = null;

    protected static final String CMD_SAVE = "save";
    protected AKJButton btnSave = null;

    protected AKJPanel childPanel = null;
    protected AKJPanel btnPanel = null;

    private boolean showButtons = true;

    protected static final SwingFactory internalSF =
            SwingFactory.getInstance("de/augustakom/hurrican/gui/base/resources/AbstractDataFrame.xml");

    /**
     * Konstruktor mit Angabe der Resource-Datei.
     *
     * @param resource
     */
    public AbstractDataFrame(String resource) {
        super(resource);
        initDataFrame();
    }

    /**
     * Konstruktor mit Angabe der Resource-Datei und Flag, ob die Default-Buttons (Save, Cancel) angezeigt werden
     * sollen.
     *
     * @param resource
     * @param showButtons
     */
    public AbstractDataFrame(String resource, boolean showButtons) {
        super(resource);
        this.showButtons = showButtons;
        initDataFrame();
    }

    /* Initialisiert das InternalFrame. */
    private void initDataFrame() {
        closeController = new CloseController();
        addVetoableChangeListener(closeController);
        createDefaultGUI();
    }

    /**
     * Erstellt die Standard-GUI fuer das Frame.
     */
    protected void createDefaultGUI() {
        childPanel = new AKJPanel(new BorderLayout());
        childPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        AKJPanel master = new AKJPanel(new BorderLayout());
        master.add(childPanel, BorderLayout.CENTER);

        if (showButtons) {
            btnSave = internalSF.createButton(CMD_SAVE, new SaveActionListener(this));

            btnPanel = new AKJPanel(new GridBagLayout());
            btnPanel.add(btnSave, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
            btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

            master.add(btnPanel, BorderLayout.SOUTH);
        }

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new AKJScrollPane(master), BorderLayout.CENTER);
    }

    /**
     * Gibt das ChildPanel zurueck, auf das die Implementierungen ihre GUI platzieren koennen. <br> Der Border des
     * ChildPanels ist auf <code>BevelBorder.RAISED</code> gesetzt. Der Border kann natuerlich jederzeit durch einen
     * entsprechenden Aufruf geaendert werden. Es sollte jedoch darauf geachtet werden, dass das Design einheitlich
     * bleibt.
     *
     * @return Das Panel, auf dem GUI-Komponenten platziert werden koennen.
     */
    protected AKJPanel getChildPanel() {
        return childPanel;
    }

    /**
     * Gibt das Panel zurueck, auf dem die Standard-Buttons platziert sind.
     *
     * @return
     */
    protected AKJPanel getButtonPanel() {
        return btnPanel;
    }

    /**
     * Gibt den Button zurueck, der mit dem Namen <code>name</code> uebereinstimmt.
     *
     * @param name Name des gesuchten Buttons
     * @return AKJButton oder <code>null</code>
     */
    protected AKJButton getButton(String name) {
        if (CMD_SAVE.equals(name)) {
            return btnSave;
        }
        return null;
    }

    /**
     * Konfiguriert einen Button mit dem Namen <code>btnName</code>
     *
     * @param btnName Name des zu konfigurierenden Buttons
     * @param text    Text fuer den Button
     * @param tooltip Tooltip-Text fuer den Button
     * @param visible Sichtbarkeit des Buttons
     * @param enabled Button auf enabled/disabled setzen.
     */
    protected void configureButton(String btnName, String text, String tooltip, boolean visible, boolean enabled) {
        AKJButton btn = getButton(btnName);
        if (btn != null) {
            btn.setText(text);
            btn.setToolTipText(tooltip);
            btn.setVisible(visible);
            btn.setEnabled(enabled);
        }
    }

    /**
     * Schliesst das InternalFrame und meldet es vom MainFrame ab.
     */
    protected void closeFrame() {
        HurricanSystemRegistry.instance().getMainFrame().unregisterFrame(this);
        dispose();
    }

    /**
     * Schliesst das Fenster, ohne zu pruefen, ob Aenderungen vorhanden sind.
     */
    protected void closeWithoutSave() {
        prepare4Close();
        closeFrame();
    }

    /**
     * Ueberprueft, ob Aenderungen vorhanden sind.
     */
    protected boolean hasModelChanged(Container c) {
        for (int i = 0; i < c.getComponentCount(); i++) {
            if (c.getComponent(i) instanceof AKModelOwner) {
                AKModelOwner modelOwner = (AKModelOwner) c.getComponent(i);
                if (modelOwner.hasModelChanged()) {
                    return true;
                }
            }
            else if ((c.getComponent(i) instanceof Container)
                    && hasModelChanged((Container) c.getComponent(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Bereitet das Frame auf das Schliessen vor. <br> Dabei wird der VetoableChangeListener des Frames entfernt.
     */
    protected void prepare4Close() {
        removeVetoableChangeListener(closeController);
    }

    /**
     * Zeigt einen OptionDialog an, ob die Aenderungen gespeichert werden sollen oder nicht.
     *
     * @return ausgewaehlte Option (entweder JOptionPane.YES_OPTION oder JOptionPane.NO_OPTION).
     */
    protected int saveQuestion() {
        String title = "Änderungen speichern?";
        String msg = "Es wurden Daten geändert.\nWollen Sie die Änderungen noch speichern?";

        return MessageHelper.showConfirmDialog(this, msg, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Controller, um zu ueberpruefen, ob sich die Daten geaendert haben.
     */
    class CloseController implements VetoableChangeListener {

        /**
         * @see java.beans.VetoableChangeListener#vetoableChange(java.beans.PropertyChangeEvent)
         */
        public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
            if (AKJInternalFrame.IS_CLOSED_PROPERTY.equals(evt.getPropertyName())
                    && Boolean.TRUE.equals(evt.getNewValue())
                    && Boolean.FALSE.equals(evt.getOldValue())
                    && hasModelChanged(getContentPane())) {
                switch (saveQuestion()) {
                    case JOptionPane.NO_OPTION:
                        prepare4Close();
                        break;
                    case JOptionPane.YES_OPTION:
                        throw new PropertyVetoException("user.will.save", evt);
                    default:
                        break;
                }
            }
        }
    }

    class SaveActionListener implements ActionListener {
        private Component frame = null;

        public SaveActionListener(Component frame) {
            super();
            this.frame = frame;
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if (CMD_SAVE.equals(e.getActionCommand())) {
                try {
                    saveModel();
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    MessageHelper.showErrorDialog(frame, ex);
                }
            }
        }
    }
}


