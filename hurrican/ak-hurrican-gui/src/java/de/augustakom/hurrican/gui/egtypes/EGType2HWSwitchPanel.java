/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2006 09:30:06
 */
package de.augustakom.hurrican.gui.egtypes;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.EGType;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.service.cc.HWSwitchService;

/**
 * Sub panel to view  certified {@link HWSwitch}es of the {@link EGType}.
 *
 *
 */
@SuppressWarnings("unchecked")
public class EGType2HWSwitchPanel extends AbstractAdminPanel {

    private static final String TITLE = "title";
    private static final String CERTIFIED_LIST = "certified.list";
    private static final String UNCERTIFIED_LIST = "uncertified.list";
    private static final String ADD_SWITCH = "add.switch";
    private static final String REMOVE_SWITCH = "remove.switch";
    private static final String MOVE_PRIORITY_UP = "move.priority.up";
    private static final String MOVE_PRIORITY_DOWN = "move.priority.down";
    private static final Logger LOGGER = Logger.getLogger(EGType2HWSwitchPanel.class);
    /* Aktuelles Modell, das ueber die Methode showDetails(Object) gesetzt wird. */
    private EGType model = null;

    private AKJList lsCertified = null;
    private DefaultListModel lsMdlCertified = null;
    private AKJList lsUncertified = null;
    private DefaultListModel lsMdlUncertifed = null;
    private AKJButton btnMovePriorityDown;
    private AKJButton btnAddSwitch;
    private AKJButton btnRemoveSwitch;
    private AKJButton btnMovePriorityUp;
    private HWSwitchService hwSwitchService;

    public EGType2HWSwitchPanel() {
        super("de/augustakom/hurrican/gui/egtypes/resources/EGType2HwSwitchPanel.xml");
        createGUI();
        loadData();
        loadServices();
    }

    private void loadServices() {
        try {
            hwSwitchService = getCCService(HWSwitchService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        // Erzeuge GUI-Komponenten und ordne diese auf dem Panel an
        AKJLabel lblCertified = getSwingFactory().createLabel(CERTIFIED_LIST);
        AKJLabel lblUncertified = getSwingFactory().createLabel(UNCERTIFIED_LIST);

        btnAddSwitch = getSwingFactory().createButton(ADD_SWITCH, getActionListener(), null);
        btnRemoveSwitch = getSwingFactory().createButton(REMOVE_SWITCH, getActionListener(), null);
        btnMovePriorityUp = getSwingFactory().createButton(MOVE_PRIORITY_UP, getActionListener(), null);
        btnMovePriorityDown = getSwingFactory().createButton(MOVE_PRIORITY_DOWN, getActionListener(), null);

        // @formatter:off
        lsMdlCertified = new DefaultListModel();
        lsCertified = new AKJList(lsMdlCertified);
        lsCertified.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsCertified.setCellRenderer(new AKCustomListCellRenderer<>(HWSwitch.class, HWSwitch::getName));
        AKJScrollPane spCertified = new AKJScrollPane(lsCertified, new Dimension(140, 100));

        lsMdlUncertifed = new DefaultListModel();
        lsUncertified = new AKJList(lsMdlUncertifed);
        lsUncertified.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsUncertified.setCellRenderer(new AKCustomListCellRenderer<>(HWSwitch.class, HWSwitch::getName));
         AKJScrollPane spUncertified = new AKJScrollPane(lsUncertified, new Dimension(140, 100));

        AKJPanel actionBarVertical = new AKJPanel(new GridBagLayout());
        actionBarVertical.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        actionBarVertical.add(btnMovePriorityUp, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        actionBarVertical.add(btnMovePriorityDown, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        actionBarVertical.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel actionBarHorizontal = new AKJPanel(new GridBagLayout());
        actionBarHorizontal.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        actionBarHorizontal.add(btnAddSwitch, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        actionBarHorizontal.add(btnRemoveSwitch, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        actionBarHorizontal.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel panel = new AKJPanel(new GridBagLayout(), getSwingFactory().getText(TITLE));
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        panel.add(lblCertified, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        panel.add(lblUncertified, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        panel.add(actionBarVertical, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        panel.add(spCertified, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        panel.add(actionBarHorizontal, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.NONE));
        panel.add(spUncertified, GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.NONE));
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));


        this.setLayout(new GridBagLayout());
        this.add(panel);
    }

    @Override
    public void showDetails(Object details) {
        //not needed
    }

    @Override
    public final void loadData() {
        //not needed
    }

    @Override
    public void createNew() {
        //not needed
    }

    @Override
    public void saveData() {
        //not needed
    }


    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        switch (command){
            case ADD_SWITCH:
                addCertifiedSwitch();
                break;
            case REMOVE_SWITCH:
                removeCertifiedSwitch();
                break;
            case MOVE_PRIORITY_UP:
                movePriorityUp();
                break;
            case MOVE_PRIORITY_DOWN:
                movePriorityDown();
                break;
            default:
                break;
        }
    }

    /**
     * @return a list of all currently selected certified {@link HWSwitch}es
     */
    public List<HWSwitch> getSelectionOfCertifiedSwitches() {
        final ListModel listModel = lsCertified.getModel();
        List<HWSwitch> updatedList = new ArrayList<>();
        for(int i =0 ;i < listModel.getSize(); i++ ){
            updatedList.add(i, (HWSwitch) listModel.getElementAt(i));
        }
        return updatedList;
    }

    @Override
    public void setModel(Observable model) {
        this.model = (model instanceof EGType) ? (EGType) model : null;
        readModel();
    }

    @Override
    public void readModel() {
        // Lösche alle Einträge
        GuiTools.cleanFields(this);

        // Lade Daten aus Model in GUI-Komponenten
        lsMdlUncertifed.removeAllElements();
        lsMdlCertified.removeAllElements();
        final List<HWSwitch> allSwitches = hwSwitchService.findSwitchesByType(HWSwitchType.IMS_OR_NSP);
        if (model != null) {
            try {
                List<HWSwitch> certifiedSwitches = model.getOrderedCertifiedSwitches();
                if (certifiedSwitches == null) {
                    certifiedSwitches = Collections.emptyList();
                }
                certifiedSwitches.forEach(lsMdlCertified::addElement);
                allSwitches.removeAll(certifiedSwitches);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        allSwitches.stream().forEach(lsMdlUncertifed::addElement);
    }

    private void addCertifiedSwitch() {
        Object value = lsUncertified.getSelectedValue();
        if ((value != null) && (value instanceof HWSwitch)) {
            lsMdlCertified.addElement(value);
            lsMdlUncertifed.removeElement(value);
        }
    }

    private void removeCertifiedSwitch() {
        Object value = lsCertified.getSelectedValue();
        if ((value != null) && (value instanceof HWSwitch)) {
            lsMdlUncertifed.addElement(value);
            lsMdlCertified.removeElement(value);
        }
    }

    /* Verschiebt den selektierten Switch nach oben. */
    private void movePriorityUp() {
        int index = lsCertified.getSelectedIndex();
        lsCertified.switchItems(index, --index);
    }

    /* Verschiebt das selektierte Switch nach unten. */
    private void movePriorityDown() {
        int index = lsCertified.getSelectedIndex();
        lsCertified.switchItems(index, ++index);
    }

    @Override
    public void update(Observable o, Object arg) {
        model = (arg instanceof EGType) ? (EGType) arg : null;
        readModel();
    }

    @Override
    public void setEnabled( boolean enabled){
        btnAddSwitch.setEnabled(enabled);
        btnRemoveSwitch.setEnabled(enabled);
        btnMovePriorityDown.setEnabled(enabled);
        btnMovePriorityUp.setEnabled(enabled);
        lsCertified.setEnabled(enabled);
        lsUncertified.setEnabled(enabled);
        if(enabled){
            lsCertified.setBackground(Color.WHITE);
            lsUncertified.setBackground(Color.WHITE);
        }
        super.setEnabled(enabled);
    }

}
