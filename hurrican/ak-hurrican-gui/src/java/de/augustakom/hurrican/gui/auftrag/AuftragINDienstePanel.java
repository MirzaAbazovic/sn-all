/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.09.2004 15:13:12
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.IAuftragStatusValidator;
import de.augustakom.hurrican.model.billing.BAuftragBNFC;
import de.augustakom.hurrican.model.cc.AK0800;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.INRufnummerService;


/**
 * Panel fuer die Darstellung/Erfassung von IN-Diensten zu einem Auftrag.
 *
 *
 */
public class AuftragINDienstePanel extends AbstractAuftragPanel implements AKModelOwner, IAuftragStatusValidator {

    private static final Logger LOGGER = Logger.getLogger(AuftragINDienstePanel.class);

    private static final String WATCH_AK0800 = "ak0800";

    // GUI-Komponenten
    private AKJCheckBox chbNeuschaltung = null;
    private AKJCheckBox chbUebernahme = null;
    private AKJTextField tfPrefix = null;
    private AKJTextField tfBusinessNr = null;
    private AKJTextField tfDest = null;
    private AKJTextField tfCodenummer = null;
    private AKJDateComponent dcEingang = null;
    private AKJDateComponent dcAnRegTP = null;
    private AKJDateComponent dcInbetriebnahme = null;
    private AKJDateComponent dcWirksamAb = null;
    private AKJDateComponent dcKuendigung = null;
    private AKJDateComponent dcWunschtermin = null;

    // Sonstiges
    private boolean guiCreated = false;
    private boolean fieldsEnabled = true;

    // Modelle
    private CCAuftragModel auftragModel = null;
    private AK0800 ak0800 = null;
    private BAuftragBNFC auftragBNFC = null;

    /**
     * Konstruktor
     */
    public AuftragINDienstePanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragINDienstePanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblNeuschaltung = getSwingFactory().createLabel("neuschaltung");
        AKJLabel lblUebernahme = getSwingFactory().createLabel("uebernahme");
        AKJLabel lblBNNr = getSwingFactory().createLabel("in.nummer");
        AKJLabel lblDest = getSwingFactory().createLabel("ziel");
        AKJLabel lblCodenummer = getSwingFactory().createLabel("codenummer");
        AKJLabel lblEingang = getSwingFactory().createLabel("auftragseingang");
        AKJLabel lblWunschtermin = getSwingFactory().createLabel("wunschtermin");
        AKJLabel lblAnRegTP = getSwingFactory().createLabel("an.reg.tp");
        AKJLabel lblInbetriebnahme = getSwingFactory().createLabel("inbetriebnahme");
        AKJLabel lblWirksamAb = getSwingFactory().createLabel("wirksam.ab");
        AKJLabel lblKuendigung = getSwingFactory().createLabel("kuendigung");

        chbNeuschaltung = getSwingFactory().createCheckBox("neuschaltung");
        chbUebernahme = getSwingFactory().createCheckBox("uebernahme");
        ButtonGroup bg = new ButtonGroup();
        bg.add(chbNeuschaltung);
        bg.add(chbUebernahme);

        tfPrefix = getSwingFactory().createTextField("vorwahl", false);
        tfBusinessNr = getSwingFactory().createTextField("rufnummer", false);
        tfDest = getSwingFactory().createTextField("ziel", false);
        tfCodenummer = getSwingFactory().createTextField("codenummer");
        dcEingang = getSwingFactory().createDateComponent("auftragseingang");
        dcWunschtermin = getSwingFactory().createDateComponent("wunschtermin");
        dcAnRegTP = getSwingFactory().createDateComponent("an.reg.tp");
        dcInbetriebnahme = getSwingFactory().createDateComponent("inbetriebnahme");
        dcWirksamAb = getSwingFactory().createDateComponent("wirksam.ab");
        dcKuendigung = getSwingFactory().createDateComponent("kuendigung");

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(lblBNNr, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfPrefix, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBusinessNr, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblDest, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfDest, GBCFactory.createGBC(100, 0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblNeuschaltung, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbNeuschaltung, GBCFactory.createGBC(100, 0, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblUebernahme, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbUebernahme, GBCFactory.createGBC(100, 0, 2, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCodenummer, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfCodenummer, GBCFactory.createGBC(100, 0, 2, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 5, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblEingang, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(dcEingang, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblWunschtermin, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcWunschtermin, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblAnRegTP, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcAnRegTP, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblInbetriebnahme, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcInbetriebnahme, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblWirksamAb, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcWirksamAb, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblKuendigung, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcKuendigung, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 6, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel fill = new AKJPanel();
        fill.setPreferredSize(new Dimension(40, 2));
        AKJPanel data = new AKJPanel(new GridBagLayout());
        data.setBorder(BorderFactory.createTitledBorder("IN-Daten"));
        data.add(left, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        data.add(fill, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        data.add(right, GBCFactory.createGBC(0, 100, 2, 0, 1, 1, GridBagConstraints.VERTICAL));

        this.setLayout(new GridBagLayout());
        this.add(data, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));

        guiCreated = true;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        if (model instanceof CCAuftragModel) {
            this.auftragModel = (CCAuftragModel) model;
        }
        else {
            this.auftragModel = null;
        }

        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        clear();
        ak0800 = null;
        auftragBNFC = null;

        AuftragDaten auftragDaten = null;
        try {
            setWaitCursor();

            if ((auftragModel != null) && (auftragModel.getAuftragId() != null)) {
                CCAuftragService as = getCCService(CCAuftragService.class);
                auftragDaten = as.findAuftragDatenByAuftragId(auftragModel.getAuftragId());
                if ((auftragDaten != null) && (auftragDaten.getAuftragNoOrig() != null)) {
                    RufnummerService billingRS = getBillingService(RufnummerService.class);
                    auftragBNFC = billingRS.findBusinessNumber(auftragDaten.getAuftragNoOrig());
                }

                if (auftragBNFC != null) {
                    INRufnummerService rs = getCCService(INRufnummerService.class);
                    ak0800 = rs.findINDetails(auftragModel.getAuftragId());

                    if (ak0800 == null) {
                        ak0800 = new AK0800();
                        ak0800.setAuftragId(auftragModel.getAuftragId());
                    }
                    addObjectToWatch(WATCH_AK0800, ak0800);

                    showValues();
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            Long tmpStatusId = (auftragDaten != null) ? auftragDaten.getStatusId() : null;
            validate4Status(tmpStatusId);
            validateFields();
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
            setValues();

            if (hasModelChanged()) {
                INRufnummerService rs = getCCService(INRufnummerService.class);
                rs.saveAK0800(ak0800, false);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
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
        if (guiCreated && (ak0800 != null)) {
            setValues();
            return hasChanged(WATCH_AK0800, ak0800);
        }
        return false;
    }

    /* Uebergibt die angezeigten Werte dem aktuellen AK0800-Objekt */
    private void setValues() {
        if (ak0800 != null) {
            ak0800.setNeuschaltung(chbNeuschaltung.isSelected());
            ak0800.setUebernahme(chbUebernahme.isSelected());
            ak0800.setCodeNummer(tfCodenummer.getText(null));
            ak0800.setAuftragsEingang(dcEingang.getDate(null));
            ak0800.setWunschtermin(dcWunschtermin.getDate(null));
            ak0800.setRegAn(dcAnRegTP.getDate(null));
            ak0800.setInbetriebnahme(dcInbetriebnahme.getDate(null));
            ak0800.setWirksamAb(dcWirksamAb.getDate(null));
            ak0800.setKuendigung(dcKuendigung.getDate(null));
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
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

    /* Zeigt die Daten des Objekts <code>ak0800</code> an. */
    private void showValues() {
        if (auftragBNFC != null) {
            tfPrefix.setText(auftragBNFC.getPrefix());
            tfBusinessNr.setText(auftragBNFC.getBusinessNr());
            tfDest.setText(auftragBNFC.getDestination());
            chbNeuschaltung.setSelected(ak0800.isNeuschaltung());
            chbUebernahme.setSelected(ak0800.isUebernahme());
            tfCodenummer.setText(ak0800.getCodeNummer());
            dcEingang.setDate(ak0800.getAuftragsEingang());
            dcWunschtermin.setDate(ak0800.getWunschtermin());
            dcAnRegTP.setDate(ak0800.getRegAn());
            dcInbetriebnahme.setDate(ak0800.getInbetriebnahme());
            dcWirksamAb.setDate(ak0800.getWirksamAb());
            dcKuendigung.setDate(ak0800.getKuendigung());
        }
    }

    /* 'Loescht' alle Felder */
    private void clear() {
        GuiTools.cleanFields(this);
    }

    /* Validiert die dargestellten Felder. */
    private void validateFields() {
        enableFields((auftragBNFC == null) ? false : true);
    }

    /* Setzt die Fields auf enabled/disabled */
    private void enableFields(boolean enable) {
        if (fieldsEnabled != enable) {
            fieldsEnabled = enable;

            chbNeuschaltung.setEnabled(enable);
            chbUebernahme.setEnabled(enable);
            tfCodenummer.setEnabled(enable);
            dcEingang.setEnabled(enable);
            dcAnRegTP.setEnabled(enable);
            dcInbetriebnahme.setEnabled(enable);
            dcWirksamAb.setEnabled(enable);
            dcKuendigung.setEnabled(enable);
            dcWunschtermin.setEnabled(enable);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IAuftragStatusValidator#validate4Status(java.lang.Integer)
     */
    @Override
    public void validate4Status(Long auftragStatus) {
        IntRange range = new IntRange(AuftragStatus.ANSCHREIBEN_KUNDEN_ERFASSUNG, AuftragStatus.ANSCHREIBEN_KUNDE_KUEND);
        if (NumberTools.isIn(auftragStatus, new Number[] { AuftragStatus.UNDEFINIERT, AuftragStatus.ERFASSUNG,
                AuftragStatus.AUS_TAIFUN_UEBERNOMMEN, AuftragStatus.ERFASSUNG_SCV, AuftragStatus.PROJEKTIERUNG,
                AuftragStatus.PROJEKTIERUNG_ERLEDIGT }) ||
                range.containsInteger(auftragStatus)) {
            enableFields(true);
        }
        else {
            // Storno/Absage/Gekuendigt oder unbekannter Status
            enableFields(false);
        }
    }
}


