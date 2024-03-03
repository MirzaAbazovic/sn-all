/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2004 15:11:30
 */
package de.augustakom.hurrican.gui.auftrag.internet;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IpRoute;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.IpRouteService;


/**
 * Panel fuer die Darstellung von IP-Daten.
 *
 *
 */
public class IpRoutePanel extends AbstractDataPanel implements AKModelOwner, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(IpRoutePanel.class);

    private static final String TABLE_MODEL_RESOURCE = "de/augustakom/hurrican/gui/auftrag/internet/resources/IpRoutePanelTableModel.xml";
    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/internet/resources/IpRoutePanel.xml";

    private static final String ADD_IP_ROUTE = "add.ip.route";
    private static final String DELETE_IP_ROUTE = "delete.ip.route";

    // GUI-Komponenten
    private AKTableModelXML<IpRoute> tbMdlIpRoutes = null;
    private AKJTable tbIpRoutes = null;

    // Modelle
    private CCAuftragModel model = null;
    private AuftragDaten auftragDaten = null;

    private IpRouteService ipRouteService = null;

    /**
     * Konstruktor
     */
    public IpRoutePanel() {
        super(RESOURCE);
        createGUI();
    }

    @Override
    protected final void createGUI() {
        tbMdlIpRoutes = new AKTableModelXML<IpRoute>(TABLE_MODEL_RESOURCE);
        tbIpRoutes = new AKJTable(tbMdlIpRoutes, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbIpRoutes.attachSorter();
        tbIpRoutes.fitTable(new int[] { 250, 80, 300 });
        tbIpRoutes.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spIpRoutes = new AKJScrollPane(tbIpRoutes);
        spIpRoutes.setPreferredSize(new Dimension(650, 50));

        MoveIpRouteAction moveIpRouteAction = new MoveIpRouteAction();
        moveIpRouteAction.setParentClass(this.getClass());
        tbIpRoutes.addPopupAction(moveIpRouteAction);

        AKJButton btnAddIpRoute = getSwingFactory().createButton(ADD_IP_ROUTE, getActionListener(), null);
        AKJButton btnDeleteIpRoute = getSwingFactory().createButton(DELETE_IP_ROUTE, getActionListener(), null);

        // @formatter:off
        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnAddIpRoute    , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnDeleteIpRoute , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        this.setLayout(new BorderLayout());
        this.add(btnPnl, BorderLayout.WEST);
        this.add(spIpRoutes, BorderLayout.CENTER);

        manageGUI(moveIpRouteAction, btnAddIpRoute, btnDeleteIpRoute);
    }

    @Override
    public void setModel(Observable model) {
        this.model = (model instanceof CCAuftragModel) ? (CCAuftragModel) model : null;
        readModel();
    }

    @Override
    public void readModel() {
        tbMdlIpRoutes.setData(null);
        if (this.model != null) {
            try {
                setWaitCursor();

                CCAuftragService auftragService = getCCService(CCAuftragService.class);
                auftragDaten = auftragService.findAuftragDatenByAuftragId(model.getAuftragId());
                if (auftragDaten == null) {
                    throw new FindException("Auftrag Daten konnten nicht ermittelt werden!");
                }
                ipRouteService = getCCService(IpRouteService.class);
                List<IpRoute> ipRoutes = ipRouteService.findIpRoutesByOrder(model.getAuftragId());
                tbMdlIpRoutes.setData(ipRoutes);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    @Override
    protected void execute(String command) {
        if (ADD_IP_ROUTE.equals(command)) {
            addIpRoute();
        }
        else if (DELETE_IP_ROUTE.equals(command)) {
            deleteIpRoute();
        }
    }

    /**
     * Erstellt eine neue Ip-Route (ueber einen eigenen Dialog).
     */
    private void addIpRoute() {
        IpRoute ipRouteToAdd = new IpRoute();
        ipRouteToAdd.setAuftragId(model.getAuftragId());
        editIpRoute(ipRouteToAdd);
    }

    /**
     * Editiert die angegebene IP-Route.
     *
     * @param toEdit
     */
    private void editIpRoute(IpRoute toEdit) {
        try {
            if (toEdit == null) {
                throw new HurricanGUIException("Bitte wählen Sie die zu ändernde Route.");
            }

            boolean isNewRoute = (toEdit.getId() == null) ? true : false;
            EditIpRouteDialog dialog = new EditIpRouteDialog(toEdit, auftragDaten);
            Object result = DialogHelper.showDialog(getMainFrame(), dialog, true, true);
            if ((result instanceof Integer) && (((Integer) result).intValue() == JOptionPane.OK_OPTION)) {
                if (isNewRoute) {
                    tbMdlIpRoutes.addObject(toEdit);
                }
                else {
                    tbMdlIpRoutes.fireTableDataChanged();
                }
            }
            else {
                readModel();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Loescht die aktuell ausgewaehlte IP-Route.
     */
    private void deleteIpRoute() {
        try {
            if (getTableSelection() == null) {
                throw new HurricanGUIException("Bitte wählen Sie zuerst eine IP-Route aus.");
            }

            int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                    "Wollen Sie die Route wirklich löschen?", "Route löschen?");
            if (option == JOptionPane.YES_OPTION) {
                ipRouteService.deleteIpRoute(getTableSelection(), HurricanSystemRegistry.instance().getSessionId());
                readModel();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Verschiebt (bzw. kopiert) die IP-Route auf einen anderen Auftrag
     *
     * @param toMove
     */
    private void moveIpRoute(IpRoute toMove) {
        try {
            if (toMove == null) {
                throw new HurricanGUIException("Bitte wählen Sie zuerst eine Route aus!");
            }

            String input = MessageHelper.showInputDialog(
                    getMainFrame(), "neue Hurrican(!) Auftrags ID:", "IP-Route umziehen", JOptionPane.PLAIN_MESSAGE);

            if (StringUtils.isNotBlank(input)) {
                Long auftragId = Long.valueOf(input);
                int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                        "Soll die Route wirklich auf den Hurrican\nAuftrag " + auftragId + " umgezogen werden?", "IP-Route umziehen?");
                if (option == JOptionPane.YES_OPTION) {
                    ipRouteService.moveIpRoute(toMove, auftragId, HurricanSystemRegistry.instance().getSessionId());

                    MessageHelper.showInfoDialog(getMainFrame(),
                            "Die Route {0} wurde auf Auftrag {1} umgezogen", "IP-Route umgezogen",
                            new Object[] { String.format("%s", (toMove.getIpAddressRef() != null) ? toMove.getIpAddressRef().getAddress() : "?"),
                                    String.format("%s", auftragId) },
                            true
                    );
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof IpRoute) {
            editIpRoute((IpRoute) selection);
        }
    }

    @Override
    public void saveModel() throws AKGUIException {
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    private IpRoute getTableSelection() {
        @SuppressWarnings("unchecked")
        IpRoute ipRouteToMove = ((AKMutableTableModel<IpRoute>) tbIpRoutes.getModel()).getDataAtRow(tbIpRoutes.getSelectedRow());
        return ipRouteToMove;
    }

    /**
     * Action-Klasse, um eine Ip-Route umzuziehen.
     */
    class MoveIpRouteAction extends AKAbstractAction {
        public MoveIpRouteAction() {
            super();
            setName("IP-Route umziehen...");
            setTooltip("IP-Route auf anderen Auftrag umziehen");
            setActionCommand("move.ip.route");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            moveIpRoute(getTableSelection());
        }
    }
}


