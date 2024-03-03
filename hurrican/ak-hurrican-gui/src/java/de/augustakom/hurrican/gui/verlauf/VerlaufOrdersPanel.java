package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.TableOpenOrderAction;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;

public class VerlaufOrdersPanel extends AbstractServicePanel implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(VerlaufOrdersPanel.class);

    private AKReflectionTableModel<CCAuftragIDsView> tbMdlOrders;

    private Long verlaufId;

    private BAService baService;

    public VerlaufOrdersPanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/VerlaufOrdersPanel.xml");
        initServices();
        createGUI();
    }

    private void initServices() {
        try {
            this.baService = getCCService(BAService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblOrders = getSwingFactory().createLabel("suborders");

        tbMdlOrders = new AKReflectionTableModel<CCAuftragIDsView>(
                new String[] { "Tech. Auftragsnr.", VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Produkt", "Auftragsstatus" },
                new String[] { "auftragId", "vbz", "produktName", "auftragStatusText" },
                new Class[] { Long.class, String.class, String.class, String.class });

        AKJTable tbOrders = new AKJTable(tbMdlOrders, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbOrders.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbOrders.addPopupAction(new TableOpenOrderAction());

        AKJScrollPane spOrders = new AKJScrollPane(tbOrders, new Dimension(550, 100));
        tbOrders.fitTable(new int[] { 100, 130, 170, 150 });

        this.setLayout(new BorderLayout());
        this.add(lblOrders, BorderLayout.NORTH);
        this.add(spOrders, BorderLayout.CENTER);
    }

    public Long getVerlaufId() {
        return verlaufId;
    }

    public void setVerlaufId(Long verlaufId) {
        this.verlaufId = verlaufId;
        if (verlaufId == null) {
            tbMdlOrders.setData(null);
        }
        else {
            try {
                tbMdlOrders.setData(baService.findAuftraege4Verlauf(verlaufId));
            }
            catch (FindException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    @Override
    public void objectSelected(Object selection) {
        AuftragDataFrame.openFrame((CCAuftragModel) selection);
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void execute(String command) {
    }

}
