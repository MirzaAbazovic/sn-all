/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2005 09:10:08
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiInfo;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;


/**
 * Dialog zur Anzeige aller Bemerkungen zu einem best. Verlauf.
 *
 *
 */
public class VerlaufsBemerkungenDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(VerlaufsBemerkungenDialog.class);

    private Long verlaufId = null;
    private VerlaufsBemerkungenPanel verlaufsBemerkungenPanel;
    private final AbstractVerlaufPanel parentVerlaufPanel;


    /**
     * Konstruktor mit Angabe der Verlaufs-ID zu der die Bemerkungen ermittelt werden sollen.
     *
     * @param verlaufId
     */
    public VerlaufsBemerkungenDialog(Long verlaufId) {
        this(verlaufId, null);
    }

    /**
     * Konstruktor mit Angabe der Verlaufs-ID zu der die Bemerkungen ermittelt werden sollen und dem
     * AbstractVerlaufPanel, in dessen Tabelle der Verlauf dargestellt wird.
     *
     * @param verlaufId
     */
    public VerlaufsBemerkungenDialog(Long verlaufId, AbstractVerlaufPanel parentPanel) {
        super("de/augustakom/hurrican/gui/verlauf/resources/VerlaufsBemerkungenDialog.xml");
        this.verlaufId = verlaufId;
        this.parentVerlaufPanel = parentPanel;
        createGUI();
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, "OK", "Schliesst den Dialog", true, true);

        verlaufsBemerkungenPanel = new VerlaufsBemerkungenPanel(parentVerlaufPanel, false);
        verlaufsBemerkungenPanel.setVerlaufId(verlaufId);

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(verlaufsBemerkungenPanel, BorderLayout.CENTER);

        setMaximumSize(GuiInfo.getScreenSize());
    }


    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            verlaufsBemerkungenPanel.doSave();

            prepare4Close();
            setValue(Integer.valueOf(OK_OPTION));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


