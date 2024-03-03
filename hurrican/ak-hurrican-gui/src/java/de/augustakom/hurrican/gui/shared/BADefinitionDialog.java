/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2005 10:29:39
 */
package de.augustakom.hurrican.gui.shared;


import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Dialog zur Eingabe des Realisierungstermins fuer einen Bauauftrag (oder Projektierung). Ausserdem ist es moeglich,
 * fuer den Bauauftrag eine Bemerkung einzutragen.
 *
 *
 */
public class BADefinitionDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(BADefinitionDialog.class);

    private AKJDateComponent dcRealDate = null;
    private AKJComboBox cbAnlass = null;
    private AKReferenceField rfInstall = null;
    private AKJTextArea taBemerkung = null;
    private BASelectSubOrdersPanel subOrdersPanel = null;

    private Date defaultDate = null;
    private boolean showBAAnlass = false;
    private Long produktId = null;
    private Long auftragId = null;
    private Long billingAuftragId = null;

    /**
     * Default-Konstruktor.
     *
     * @param defaultDate  Datum, das dargestellt werden soll.
     * @param showBAAnlass Flag, ob eine Auswahl des BA-Anlasses erfolgen soll. Der ausgewaehlte Anlass kann vom Client
     *                     ueber die Methode <code>getBAAnlass</code> abgefragt werden. <br> Ist dieses Flag auf 'true'
     *                     gesetzt, wird eine ComboBox zur Auswahl des Anlasses angezeigt.
     * @param produktId    wird benoetigt, wenn Flag 'showBAAnlass' auf true gesetzt ist. Ueber die Produkt-ID werden
     *                     die moeglichen BA-Anlaesse ermittelt.
     */
    public BADefinitionDialog(Date defaultDate,
            boolean showBAAnlass,
            Long produktId,
            Long auftragId,
            Long billingAuftragId) {
        super("de/augustakom/hurrican/gui/shared/resources/BADefinitionDialog.xml", true);
        this.defaultDate = (defaultDate != null) ? new Date(defaultDate.getTime()) : null;
        this.showBAAnlass = showBAAnlass;
        this.produktId = produktId;
        this.auftragId = auftragId;
        this.billingAuftragId = billingAuftragId;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, "Ok", "Uebernahme der Werte fuer den Bauauftrag", true, true);
        AKJLabel lblRealDate = getSwingFactory().createLabel("real.date", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblAnlass = getSwingFactory().createLabel("anlass", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblInstall = getSwingFactory().createLabel("install.type", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung", AKJLabel.LEFT, Font.BOLD);

        dcRealDate = getSwingFactory().createDateComponent("real.date");
        dcRealDate.setDate(defaultDate);
        cbAnlass = getSwingFactory().createComboBox("anlass",
                new AKCustomListCellRenderer<>(BAVerlaufAnlass.class, BAVerlaufAnlass::getName));
        rfInstall = getSwingFactory().createReferenceField("install.type");
        taBemerkung = getSwingFactory().createTextArea("real.date");
        taBemerkung.setWrapStyleWord(true);
        taBemerkung.setLineWrap(true);
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung, new Dimension(150, 80));
        subOrdersPanel = new BASelectSubOrdersPanel(auftragId, billingAuftragId, new Dimension(350, 100));

        AKJPanel panel = new AKJPanel(new GridBagLayout());
        panel.add(lblRealDate, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        panel.add(dcRealDate, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        if (showBAAnlass) {
            panel.add(lblAnlass, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            panel.add(cbAnlass, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        }
        panel.add(lblInstall, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(rfInstall, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblBemerkung, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(spBemerkung, GBCFactory.createGBC(0, 0, 2, 3, 1, 2, GridBagConstraints.HORIZONTAL));
        panel.add(subOrdersPanel, GBCFactory.createGBC(0, 0, 0, 5, 3, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.setBorder(BorderFactory.createTitledBorder("Bauauftragsdaten"));
        child.add(panel, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            if (showBAAnlass) {
                BAConfigService bas = getCCService(BAConfigService.class);
                List<BAVerlaufAnlass> result = bas.findPossibleBAAnlaesse4Produkt(produktId, null);
                cbAnlass.addItems(result, true, BAVerlaufAnlass.class);

                // 'Leistungsaenderung' selektieren
                cbAnlass.selectItem("getId", BAVerlaufAnlass.class, BAVerlaufAnlass.LEISTUNGSAENDERUNGEN);
            }

            ReferenceService rs = getCCService(ReferenceService.class);
            List<Reference> refs = rs.findReferencesByType(Reference.REF_TYPE_INSTALLATION_TYPE, true);
            rfInstall.setReferenceList(refs);
            rfInstall.setFindService(getCCService(QueryCCService.class));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        Date selection = dcRealDate.getDate(null);
        if (selection == null) {
            MessageHelper.showMessageDialog(this, "Bitte tragen Sie ein gültiges Datum ein.", "Ungültiges Datum",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        else if (!DateTools.isWorkDay(selection)) {
            MessageHelper.showMessageDialog(this, "Der Realisierungtermin muss auf einen Arbeitstag fallen!", "Ungültiges Datum",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            prepare4Close();
            setValue(selection);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        // intentionally left blank
    }

    /**
     * Gibt den ausgewaehlten BA-Anlass zurueck.
     *
     * @return BA-Anlass ID
     *
     */
    public Long getBAAnlass() {
        if (showBAAnlass) {
            return (Long) cbAnlass.getSelectedItemValue("getId", Long.class);
        }
        return null;
    }

    /**
     * Gibt den ausgewaehlten Installations-Typ zurueck.
     *
     * @return  InstallationType ID
     *
     */
    public Long getInstallationType() {
        return rfInstall.getReferenceIdAs(Long.class);
    }

    /**
     * Gibt die eingetragene Bemerkung zurueck.
     *
     * @return  eingetragene Bemerkung
     */
    public String getBemerkung() {
        return taBemerkung.getText();
    }

    public Set<Long> getSelectedSubOrders() {
        return subOrdersPanel.getSelectedSubOrders();
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }

}


