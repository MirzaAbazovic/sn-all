/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 16.02.2010 15:56:30
  */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.IAuftragStatusValidator;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleConnect;
import de.augustakom.hurrican.model.cc.EndstelleConnect.ESEinstellung;
import de.augustakom.hurrican.model.cc.EndstelleConnect.ESRouterInfo;
import de.augustakom.hurrican.model.cc.EndstelleConnect.ESSchnittstelle;
import de.augustakom.hurrican.service.cc.ConnectService;

/**
 * Panel für die Darstellung der Connect-Daten einer Endstelle
 */
public class EndstelleConnectPanel extends AbstractDataPanel implements IAuftragStatusValidator {

    private static final String NEW_CONNECT = "new.connect";

    private static final Logger LOGGER = Logger.getLogger(EndstelleConnectPanel.class);

    private static final String WATCH_ENDSTELLE_CONNECT_DATEN = "watch.es.connect";
    private static final long serialVersionUID = -8414706988707901066L;

    private AKJTextField tfGebaude = null;
    private AKJTextField tfEtage = null;
    private AKJTextField tfRaum = null;
    private AKJTextField tfSchrank = null;
    private AKJTextField tfBandbreite = null;
    private AKJTextField tfUebergabe = null;
    private AKJComboBox cbSchnittstelle = null;
    private AKJLabel lblEinstellung = null;
    private AKJComboBox cbEinstellung = null;
    private AKJComboBox cbRouterinfo = null;
    private AKJTextField tfRoutertyp = null;
    private AKJTextField tfDefaultGateway = null;
    private AKJTextArea taBemerkung = null;
    private AKJButton btnNew = null;

    private Endstelle endstelle = null;
    private EndstelleConnect endstelleConnect = null;

    private boolean inLoad = false;

    public EndstelleConnectPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/EndstelleConnectPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblGebaude = getSwingFactory().createLabel("connect.gebaeude");
        tfGebaude = getSwingFactory().createTextField("connect.gebaeude");

        AKJLabel lblEtage = getSwingFactory().createLabel("connect.etage");
        tfEtage = getSwingFactory().createTextField("connect.etage");

        AKJLabel lblRaum = getSwingFactory().createLabel("connect.raum");
        tfRaum = getSwingFactory().createTextField("connect.raum");

        AKJLabel lblSchrank = getSwingFactory().createLabel("connect.schrank");
        tfSchrank = getSwingFactory().createTextField("connect.schrank");

        AKJLabel lblBandbreite = getSwingFactory().createLabel("connect.bandbreite");
        tfBandbreite = getSwingFactory().createTextField("connect.bandbreite");

        AKJLabel lblUebergabe = getSwingFactory().createLabel("connect.uebergabe");
        tfUebergabe = getSwingFactory().createTextField("connect.uebergabe");

        AKJLabel lblSchnittstelle = getSwingFactory().createLabel("connect.if");
        cbSchnittstelle = getSwingFactory().createComboBox("connect.if");
        cbSchnittstelle.addItems(Arrays.asList(ESSchnittstelle.values()), true);
        cbSchnittstelle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AKJComboBox cb = (AKJComboBox) e.getSource();
                ESSchnittstelle selected = (ESSchnittstelle) cb.getSelectedItem();
                if ((selected != null) && (selected.needsEinstellung())) {
                    if (cbEinstellung.getSelectedItem() == null) {
                        cbEinstellung.setSelectedIndex(0);
                    }
                    lblEinstellung.setVisible(true);
                    cbEinstellung.setVisible(true);
                }
                else {
                    lblEinstellung.setVisible(false);
                    cbEinstellung.setVisible(false);
                }
            }
        });

        lblEinstellung = getSwingFactory().createLabel("connect.if-einstellung");
        cbEinstellung = getSwingFactory().createComboBox("connect.if-einstellung");
        cbEinstellung.addItems(Arrays.asList(ESEinstellung.values()), true);

        AKJLabel lblRouterinfo = getSwingFactory().createLabel("connect.routerinfo");
        cbRouterinfo = getSwingFactory().createComboBox("connect.routerinfo");
        cbRouterinfo.addItems(Arrays.asList(ESRouterInfo.values()), true);

        AKJLabel lblRoutertyp = getSwingFactory().createLabel("connect.routertyp");
        tfRoutertyp = getSwingFactory().createTextField("connect.routertyp");

        AKJLabel lblDefaultGateway = getSwingFactory().createLabel("connect.default.gateway");
        tfDefaultGateway = getSwingFactory().createTextField("connect.default.gateway");

        AKJLabel lblBemerkung = getSwingFactory().createLabel("connect.bemerkung");
        taBemerkung = getSwingFactory().createTextArea("connect.bemerkung");
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung);
        spBemerkung.setPreferredSize(new Dimension(170, 95));

        btnNew = getSwingFactory().createButton(NEW_CONNECT, getActionListener(), null);

        // @formatter:off
        AKJPanel panel = new AKJPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Connect-Daten zur Endstelle"));
        panel.add(btnNew,           GBCFactory.createGBC(  0,  0,  0, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        panel.add(lblGebaude,       GBCFactory.createGBC(  0,  0,  0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(),   GBCFactory.createGBC(  0,  0,  1, 1, 1, 1, GridBagConstraints.NONE));
        panel.add(tfGebaude,        GBCFactory.createGBC(  0,  0,  2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(),   GBCFactory.createGBC(  0,  0,  3, 1, 1, 1, GridBagConstraints.NONE));
        panel.add(lblEtage,         GBCFactory.createGBC(  0,  0,  4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(),   GBCFactory.createGBC(  0,  0,  5, 1, 1, 1, GridBagConstraints.NONE));
        panel.add(tfEtage,          GBCFactory.createGBC(  0,  0,  6, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(),   GBCFactory.createGBC(  0,  0,  7, 1, 1, 1, GridBagConstraints.NONE));
        panel.add(lblRaum,          GBCFactory.createGBC(  0,  0,  8, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(),   GBCFactory.createGBC(  0,  0,  9, 1, 1, 1, GridBagConstraints.NONE));
        panel.add(tfRaum,           GBCFactory.createGBC(  0,  0, 10, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(),   GBCFactory.createGBC(  0,  0, 11, 1, 1, 1, GridBagConstraints.NONE));
        panel.add(lblSchrank,       GBCFactory.createGBC(  0,  0, 12, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(),   GBCFactory.createGBC(  0,  0, 13, 1, 1, 1, GridBagConstraints.NONE));
        panel.add(tfSchrank,        GBCFactory.createGBC(  0,  0, 14, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        panel.add(lblBandbreite,    GBCFactory.createGBC(  0,  0,  0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfBandbreite,     GBCFactory.createGBC(  0,  0,  2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblUebergabe,     GBCFactory.createGBC(  0,  0,  4, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfUebergabe,      GBCFactory.createGBC(  0,  0,  6, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblSchnittstelle, GBCFactory.createGBC(  0,  0,  8, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(cbSchnittstelle,  GBCFactory.createGBC(  0,  0, 10, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblEinstellung,   GBCFactory.createGBC(  0,  0, 12, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(cbEinstellung,    GBCFactory.createGBC(  0,  0, 14, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        panel.add(lblRouterinfo,    GBCFactory.createGBC(  0,  0,  0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(cbRouterinfo,     GBCFactory.createGBC(  0,  0,  2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblRoutertyp,     GBCFactory.createGBC(  0,  0,  4, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfRoutertyp,      GBCFactory.createGBC(  0,  0,  6, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblDefaultGateway,GBCFactory.createGBC(  0,  0,  8, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfDefaultGateway, GBCFactory.createGBC(  0,  0, 10, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        panel.add(lblBemerkung,     GBCFactory.createGBC(  0,  0,  0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(spBemerkung,      GBCFactory.createGBC(  0,  0,  2, 4, 5, 2, GridBagConstraints.BOTH));

        panel.add(new AKJPanel(),   GBCFactory.createGBC(100,100, 15, 1, 1, 1, GridBagConstraints.NONE));
        // @formatter:on

        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.NORTH);

        manageGUI(tfGebaude, tfEtage, tfRaum, tfSchrank,
                tfBandbreite, tfUebergabe, cbSchnittstelle, cbEinstellung, cbRouterinfo,
                tfRoutertyp, taBemerkung);

        GuiTools.enableContainerComponents(this, false);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.endstelle = null;
        if (model instanceof Endstelle) {
            this.endstelle = (Endstelle) model;
        }
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        try {
            setWaitCursor();
            inLoad = true;
            GuiTools.cleanFields(this);
            if (endstelle != null) {
                btnNew.setEnabled(false);

                endstelleConnect = getCCService(ConnectService.class).findEndstelleConnectByEndstelle(endstelle);
                addObjectToWatch(WATCH_ENDSTELLE_CONNECT_DATEN, endstelleConnect);

                if (endstelleConnect != null) {
                    loadFieldValues();
                }
                else {
                    GuiTools.enableContainerComponents(this, false);
                    btnNew.setEnabled(true);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            inLoad = false;
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        try {
            setWaitCursor();
            if ((!inLoad) && (endstelle != null) && (endstelleConnect != null) && hasModelChanged()) {
                // um die Daten zu setzen (eigentlich durch parent erfolgt)
                getCCService(ConnectService.class).saveEndstelleConnect(endstelleConnect);
                addObjectToWatch(WATCH_ENDSTELLE_CONNECT_DATEN, endstelleConnect);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new AKGUIException(e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        retrieveFieldValues();
        return (!inLoad) && (hasChanged(WATCH_ENDSTELLE_CONNECT_DATEN, endstelleConnect));
    }

    /**
     * Lädt die Daten des Objekts <code>leitungsnummer</code> für den Dialog
     */
    private void loadFieldValues() {
        if (endstelleConnect != null) {
            GuiTools.enableContainerComponents(this, true);
            tfGebaude.setText(endstelleConnect.getGebaeude());
            tfEtage.setText(endstelleConnect.getEtage());
            tfRaum.setText(endstelleConnect.getRaum());
            tfSchrank.setText(endstelleConnect.getSchrank());
            tfBandbreite.setText(endstelleConnect.getBandbreite());
            tfUebergabe.setText(endstelleConnect.getUebergabe());
            tfDefaultGateway.setText(endstelleConnect.getDefaultGateway());
            cbSchnittstelle.setSelectedItem(endstelleConnect.getSchnittstelle());
            cbEinstellung.setSelectedItem(endstelleConnect.getEinstellung());
            cbRouterinfo.setSelectedItem(endstelleConnect.getRouterinfo());
            tfRoutertyp.setText(endstelleConnect.getRoutertyp());
            taBemerkung.setText(endstelleConnect.getBemerkung());
        }
    }

    /**
     * Überträgt die im Dialog eingegebenen Daten in das Objekt <code>leitungsnummer<code>
     */
    private void retrieveFieldValues() {
        if (endstelleConnect != null) {
            endstelleConnect.setGebaeude(tfGebaude.getText(null));
            endstelleConnect.setEtage(tfEtage.getText(null));
            endstelleConnect.setRaum(tfRaum.getText(null));
            endstelleConnect.setSchrank(tfSchrank.getText(null));
            endstelleConnect.setBandbreite(tfBandbreite.getText(null));
            endstelleConnect.setUebergabe(tfUebergabe.getText(null));
            endstelleConnect.setDefaultGateway(tfDefaultGateway.getText(null));
            endstelleConnect.setSchnittstelle((ESSchnittstelle) cbSchnittstelle.getSelectedItem());
            if (cbEinstellung.isVisible()) {
                endstelleConnect.setEinstellung((ESEinstellung) cbEinstellung.getSelectedItem());
            }
            else {
                endstelleConnect.setEinstellung(null);
            }
            endstelleConnect.setRouterinfo((ESRouterInfo) cbRouterinfo.getSelectedItem());
            endstelleConnect.setRoutertyp(tfRoutertyp.getText(null));

            endstelleConnect.setBemerkung(taBemerkung.getText(null));
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (NEW_CONNECT.equals(command)) {
            if (endstelleConnect == null) {
                endstelleConnect = new EndstelleConnect();
                endstelleConnect.setEndstelleId(endstelle.getId());
            }
            GuiTools.enableContainerComponents(this, true);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IAuftragStatusValidator#validate4Status(Long)
     */
    @Override
    public void validate4Status(Long auftragStatus) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }
}
