/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.07.2004 07:51:24
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AKJMenu;
import de.augustakom.common.gui.swing.AKJMenuItem;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.AdministrationMouseListener;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataFrame;
import de.augustakom.hurrican.gui.utils.GUIDefinitionHelper;
import de.augustakom.hurrican.gui.utils.MenuHelper;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;
import de.augustakom.hurrican.model.cc.gui.GUIDefinition;
import de.augustakom.hurrican.service.cc.GUIService;


/**
 * Frame, um die Auftragsuebersicht fuer einen Kunden darzustellen.
 */
public class AuftragUebersichtFrame extends AbstractDataFrame implements AKModelOwner {

    private static final Logger LOGGER = Logger.getLogger(AuftragUebersichtFrame.class);

    private static final String TITLE_BASE = "Auftragsübersicht";
    private static final long serialVersionUID = -3478856068498946610L;

    private AuftragUebersichtPanel auPanel = null;
    private AKJMenu menuOfFrame = null;
    private boolean menuCreated = false;

    private KundeAdresseView model = null;

    /**
     * Konstruktor
     */
    public AuftragUebersichtFrame() {
        super(null, false);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    public Object getModel() {
        return this.model;
    }

    /**
     * Uebergibt dem Frame Kundendaten des Kunden, fuer den die Auftragsuebersicht erstellt werden soll.
     *
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) {
        StringBuilder sb = new StringBuilder();
        sb.append(TITLE_BASE);
        if (model instanceof KundeAdresseView) {
            this.model = (KundeAdresseView) model;
            sb.append(" für Kunde ");
            sb.append(this.model.getName());
            sb.append(" (");
            sb.append(this.model.getKundeNo());
            sb.append(")");
        }
        setTitle(sb.toString());

        // Model an Panel uebergeben
        auPanel.setModel(model);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected final void createGUI() {
        setTitle(TITLE_BASE);
        setIcon("de/augustakom/hurrican/gui/images/auftraege.gif");

        auPanel = new AuftragUebersichtPanel();
        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().setBorder(null);
        getChildPanel().add(auPanel, BorderLayout.CENTER);

        pack();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKMenuOwner#getMenuOfOwner()
     */
    public AKJMenu getMenuOfOwner() {
        if (menuOfFrame == null && !menuCreated) {
            try {
                GUIService service = getCCService(GUIService.class);
                GUIDefinition guiDef = service.findGUIDefinitionByClass(this.getClass().getName());
                if (guiDef != null) {
                    List<GUIDefinition> defs = service.getGUIDefinitions4Reference(guiDef.getId(),
                            GUIDefinition.class.getName(), GUIDefinition.TYPE_ACTION);
                    List<AKAbstractAction> actions = GUIDefinitionHelper.createActions(defs);
                    if (!actions.isEmpty()) {
                        menuOfFrame = MenuHelper.createMenu("Kunde", null, KeyEvent.VK_K);

                        AdministrationMouseListener adminML = new AdministrationMouseListener();
                        for (Iterator<AKAbstractAction> iter = actions.iterator(); iter.hasNext(); ) {
                            Action action = iter.next();
                            action.putValue(AKAbstractAction.MODEL_OWNER, this);

                            AKJMenuItem item = new AKJMenuItem(action);
                            item.addMouseListener(adminML);
                            item.addMenuKeyListener(adminML);
                            menuOfFrame.add(item);
                        }
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            finally {
                menuCreated = true;
            }
        }

        return menuOfFrame;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJInternalFrame#getUniqueName()
     */
    public String getUniqueName() {
        return TITLE_BASE;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    public void readModel() {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    public void saveModel() {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * Oeffnet (oder aktiviert) das Frame mit der Auftragsuebersicht fuer einen Kunden.
     */
    public static void showAuftragUebersicht(KundeAdresseView kaView) {
        AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        AKJInternalFrame[] frames =
                mainFrame.findInternalFrames(AuftragUebersichtFrame.class);
        AuftragUebersichtFrame auFrame = null;
        if (frames != null && frames.length == 1) {
            auFrame = (AuftragUebersichtFrame) frames[0];
            mainFrame.activateInternalFrame(auFrame.getUniqueName());
        }
        else {
            auFrame = new AuftragUebersichtFrame();
            mainFrame.registerFrame(auFrame, false);

            // Frame stellte immer eine ScrollPane dar - konnte ich nicht anders loesen...
            Dimension prefSize = auFrame.getPreferredSize();
            auFrame.setSize((int) prefSize.getWidth() + 5, (int) prefSize.getHeight() + 5);
        }

        auFrame.setModel(kaView);
    }

}


