/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2005 14:48:04
 */
package de.augustakom.hurrican.gui.tools.consistency;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKActionFinishListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;

/**
 * Abstrakte Basis-Klasse für ConsistenceCheck-Panels.
 *
 * @param <T> the result type returned by {@code SwingWorker's} {@code doInBackground} and {@code get} methods
 * @param <V> the type used for carrying out intermediate results by {@code SwingWorker's} {@code publish} and {@code
 *            process} methods
 */
public abstract class AbstractConsistenceCheckPanel<T, V> extends AbstractServicePanel
        implements IConsistenceCheckPanel, AKActionFinishListener {

    private static final SwingFactory INTERNAL_SWINGFACTORY =
            SwingFactory.getInstance("de/augustakom/hurrican/gui/tools/consistency/resources/AbstractConsistenceCheckPanel.xml");

    protected static final String CMD_START = "start.check";
    protected static final String CMD_CANCEL = "cancel.check";

    private AKJPanel childPanel = null;
    private AKJButton btnStart = null;
    private AKJButton btnCancel = null;

    protected SwingWorker<T, V> swingWorker;

    public AbstractConsistenceCheckPanel(String resource) {
        super(resource);
        createDefaultGUI();
    }

    /**
     * Erstellt die Standard-GUI für alle Panel
     */
    private void createDefaultGUI() {

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (CMD_START.equals(event.getActionCommand())) {
                    enableGuiButtonsForSwingWorkerInProgress(true);
                    executeCheckConsistence();
                }
                else if (CMD_CANCEL.equals(event.getActionCommand())) {
                    cancelCheckConsistence();
                }
            }
        };

        btnStart = INTERNAL_SWINGFACTORY.createButton(CMD_START, actionListener);
        btnCancel = INTERNAL_SWINGFACTORY.createButton(CMD_CANCEL, actionListener);

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnStart, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnCancel, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.NONE));

        childPanel = new AKJPanel(new BorderLayout());
        childPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        AKJPanel master = new AKJPanel(new BorderLayout());
        master.add(childPanel, BorderLayout.CENTER);
        master.add(btnPanel, BorderLayout.SOUTH);

        this.setLayout(new BorderLayout());
        this.add(master, BorderLayout.CENTER);
    }

    @Override
    public abstract void executeCheckConsistence();

    @Override
    public void cancelCheckConsistence() {
        if (swingWorker != null) {
            swingWorker.cancel(true);
        }
    }

    @Override
    public void actionFinished(boolean finishSuccessful) {
        enableGuiButtonsForSwingWorkerInProgress(false);
    }

    protected void enableGuiButtonsForSwingWorkerInProgress(boolean inProgress) {
        btnStart.setEnabled(!inProgress);
        btnCancel.setEnabled(inProgress);
    }

    /**
     * Gibt das Panel zurück, auf dem die Ableitungen ihre GUI-Komponenten platzieren können.
     */
    protected AKJPanel getChildPanel() {
        return childPanel;
    }
}
