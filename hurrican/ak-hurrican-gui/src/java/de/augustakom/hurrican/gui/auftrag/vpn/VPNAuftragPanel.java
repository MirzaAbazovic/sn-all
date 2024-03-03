/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2004 14:23:08
 */
package de.augustakom.hurrican.gui.auftrag.vpn;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.reports.jasper.AKJasperDialog;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.auftrag.shared.AuftragEndstelleExtTableModel;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.model.shared.view.VPNVertragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.VPNService;


/**
 * Panel fuer die Darstellung der Details zu einem VPN-Auftrag. <br> Ueber dieses Panel koennen auch Auftraege in den
 * VPN aufgenommen bzw. aus dem VPN entfernt werden.
 *
 *
 */
public class VPNAuftragPanel extends AbstractDataPanel implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(VPNAuftragPanel.class);

    private AuftragEndstelleExtTableModel tbMdlVPN = null;
    private AKJTable tbVPN = null;
    private AKJButton btnAdd = null;
    private AKJButton btnRemove = null;
    private AKJButton btnPrint = null;
    private AKJLabel lblCount = null;

    private VPNVertragView model = null;
    private List<AuftragEndstelleView> vpnAuftraege = null;

    private AKManageableComponent[] managedComponents;

    public VPNAuftragPanel() {
        super("de/augustakom/hurrican/gui/auftrag/vpn/resources/VPNAuftragPanel.xml");
        createGUI();
        readModel();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblTitle = getSwingFactory().createLabel("anzahl");
        lblCount = new AKJLabel();

        btnAdd = getSwingFactory().createButton("add", getActionListener());
        btnRemove = getSwingFactory().createButton("remove", getActionListener());
        btnPrint = getSwingFactory().createButton("print", getActionListener());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnAdd, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnRemove, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnPrint, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlVPN = new AuftragEndstelleExtTableModel();
        tbVPN = new AKJTable(tbMdlVPN, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbVPN.attachSorter();
        tbVPN.fitTable(new int[] { 75, 80, 80, 80, 90, 90, 100, 100, 100, 120, 75, 20 });
        tbVPN.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spVPN = new AKJScrollPane(tbVPN);
        spVPN.setPreferredSize(new Dimension(900, 450));

        this.setLayout(new GridBagLayout());
        this.add(lblTitle, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblCount, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(spVPN, GBCFactory.createGBC(100, 100, 0, 1, 2, 1, GridBagConstraints.BOTH));
        this.add(btnPanel, GBCFactory.createGBC(100, 0, 0, 2, 2, 1, GridBagConstraints.HORIZONTAL));

        managedComponents = new AKManageableComponent[] { btnAdd, btnRemove };
        manageGUI(managedComponents);
    }

    @Override
    public void setModel(Observable model) {
        if (model instanceof VPNVertragView) {
            this.model = (VPNVertragView) model;
        }
        else {
            this.model = null;
        }

        readModel();
    }

    @Override
    public void readModel() {
        tbMdlVPN.removeAll();

        if (model != null) {
            setWaitCursor();
            final SwingWorker<List<AuftragEndstelleView>, Void> worker = new SwingWorker<List<AuftragEndstelleView>, Void>() {

                final Long vpnId = model.getId();

                @Override
                protected List<AuftragEndstelleView> doInBackground() throws Exception {
                    CCAuftragService ccAS = getCCService(CCAuftragService.class);
                    List<AuftragEndstelleView> vpnAuftraege = ccAS.findAuftragEndstelleViews4VPN(vpnId, null);
                    return vpnAuftraege;
                }

                @Override
                protected void done() {
                    try {
                        // Auftraege des VPNs laden
                        vpnAuftraege = get();
                        tbMdlVPN.setData(vpnAuftraege);
                        GuiTools.unlockComponents(new Component[] { btnAdd, btnRemove, btnPrint });
                        manageGUI(managedComponents);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        lblCount.setText("" + tbMdlVPN.getRowCount());
                        setDefaultCursor();
                    }
                }

            };
            GuiTools.lockComponents(new Component[] { btnAdd, btnRemove, btnPrint });
            worker.execute();
        }
    }

    @Override
    protected void execute(String command) {
        try {
            if ("add".equals(command)) {
                addAuftrag2VPN();
            }
            else if ("remove".equals(command)) {
                removeAuftragFromVPN();
            }
            else if ("print".equals(command)) {
                printVPNUebersicht();
            }
        }
        catch (HurricanGUIException e) {
            MessageHelper.showInfoDialog(this, e.getMessage(), null, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /* Fuegt dem VPN einen Auftrag hinzu. Dazu wird ein Dialog fuer die Auftrags-Auswahl geoeffnet. */
    private void addAuftrag2VPN() throws HurricanGUIException, ServiceNotFoundException, FindException, StoreException {
        if ((model == null) || (model.getKundeNo() == null)) {
            throw new HurricanGUIException("Es ist kein VPN ausgewählt, dem Aufträge zugeordnet werden können.");
        }

        VPNAuftragAuswahlDialog dlg = new VPNAuftragAuswahlDialog(model.getKundeNo());
        Object value = DialogHelper.showDialog(this, dlg, true, true);
        if (value instanceof Long) {
            Long auftragId = (Long) value;
            try {
                setWaitCursor();
                CCAuftragService ccAS = getCCService(CCAuftragService.class);

                // AuftragTechnik laden + VPN pruefen
                AuftragTechnik auftragTechnik = ccAS.findAuftragTechnikByAuftragId(auftragId);
                if (auftragTechnik == null) {
                    throw new HurricanGUIException("Der Auftrag, der dem VPN zugeordnet werden soll konnte nicht ermittelt werden.");
                }
                if (auftragTechnik.getVpnId() != null) {
                    throw new HurricanGUIException("Der Auftrag ist bereits einem VPN zugeordnet!");
                }

                // VPN-ID der AuftragTechnik zuordnen
                VPNService vpnService = getCCService(VPNService.class);
                vpnService.addAuftrag2VPN(model.getId(), auftragTechnik);
                readModel();
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /* Entfernt den aktuell markierten Auftrag aus dem VPN. */
    private void removeAuftragFromVPN() throws FindException, ServiceNotFoundException, StoreException {
        try {
            setWaitCursor();
            AKMutableTableModel mdl = (AKMutableTableModel) tbVPN.getModel();
            Object selection = mdl.getDataAtRow(tbVPN.getSelectedRow());
            if (selection instanceof CCAuftragModel) {
                String title = "Auftrag aus VPN entfernen?";
                String msg = "Soll der selektierte Auftrag wirklich aus\ndem VPN entfernt werden?";
                int result = MessageHelper.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.NO_OPTION) {
                    return;
                }

                CCAuftragModel auftragModel = (CCAuftragModel) selection;
                CCAuftragService ccAS = getCCService(CCAuftragService.class);
                AuftragTechnik auftragTechnik = ccAS.findAuftragTechnikByAuftragId(auftragModel.getAuftragId());

                VPNService vpnService = getCCService(VPNService.class);
                vpnService.removeAuftragFromVPN(auftragTechnik);
                readModel();
            }
            else {
                MessageHelper.showInfoDialog(this,
                        "Bitte markieren Sie einen Auftrag, der aus dem VPN entfernt werden soll.", null, true);
            }
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Erstellt einen Report mit der VPN-Uebersicht. */
    private void printVPNUebersicht() {
        try {
            setWaitCursor();
            VPNService vs = getCCService(VPNService.class);
            JasperPrint jp = vs.reportVPNUebersicht(model.getId(), vpnAuftraege);

            AKJasperDialog.viewReport(jp, this);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    @Override
    public void objectSelected(Object selection) {
        try {
            setWaitCursor();
            if (selection instanceof CCAuftragModel) {
                AbstractMDIMainFrame mainFrame = getMainFrame();
                AKJInternalFrame[] frames =
                        mainFrame.findInternalFrames(AuftragDataFrame.class);
                AuftragDataFrame dataFrame = null;
                if ((frames != null) && (frames.length == 1)) {
                    dataFrame = (AuftragDataFrame) frames[0];
                    mainFrame.activateInternalFrame(dataFrame.getUniqueName());
                }
                else {
                    dataFrame = new AuftragDataFrame();
                    mainFrame.registerFrame(dataFrame, true);
                }

                dataFrame.setModel((Observable) selection);
            }
        }
        finally {
            setDefaultCursor();
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

    /* Panel fuer ein OptionPane, um ein Datum abzufragen */
    static class DateSelPanel extends AKJPanel {
        private AKJDateComponent dcDate = null;

        public DateSelPanel() {
            super();
            createGUI();
        }

        /* Erstellt die GUI. */
        private void createGUI() {
            AKJLabel lblMsg = new AKJLabel("Berechnung erfolgt über Hurrican bis:");
            dcDate = new AKJDateComponent(new Date());

            this.setLayout(new GridBagLayout());
            this.add(lblMsg, GBCFactory.createGBC(100, 0, 0, 0, 2, 1, GridBagConstraints.HORIZONTAL));
            this.add(dcDate, GBCFactory.createGBC(20, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            this.add(new AKJPanel(), GBCFactory.createGBC(80, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        }

        /**
         * Gibt das eingetragene Datum zurueck.
         */
        public Date getDate() {
            return dcDate.getDate(null);
        }
    }

}


