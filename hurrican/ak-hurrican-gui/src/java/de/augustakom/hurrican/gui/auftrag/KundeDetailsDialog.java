/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.07.2004 12:56:19
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;
import de.augustakom.hurrican.service.billing.KundenService;


/**
 * Dialog, um die Details zu einem Kunden anzuzeigen.
 *
 *
 */
public class KundeDetailsDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(KundeDetailsDialog.class);

    private AKJFormattedTextField tfKundeNoOrig = null;
    private AKJFormattedTextField tfHauptKundeNo = null;
    private AKJTextField tfName = null;
    private AKJTextField tfVorname = null;
    private AKJTextField tfStrasse = null;
    private AKJTextField tfPLZ = null;
    private AKJTextField tfOrt = null;
    private AKJTextField tfOrtsteil;
    private AKJTextField tfBetreuer = null;
    private AKJTextField tfHauptRN = null;
    private AKJTextField tfFax = null;
    private AKJTextField tfMobil = null;
    private AKJTextField tfEMail = null;
    private AKJTextField tfTyp = null;
    private AKJTextField tfVIP = null;
    private AKJCheckBox chbFK = null;

    private KundeAdresseView kaView = null;
    private Kunde kunde = null;

    /**
     * Konstruktor mit Angabe des View-Modells, in dem bereits die wichtigsten Kundendaten enthalten sind. <br> Die
     * restlichen Daten werden von dem Dialog selbst geladen.
     *
     * @param kaView
     */
    public KundeDetailsDialog(KundeAdresseView kaView) {
        super("de/augustakom/hurrican/gui/auftrag/resources/KundeDetailsDialog.xml");
        this.kaView = kaView;
        createGUI();
        read();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        StringBuilder title = new StringBuilder();
        title.append(getSwingFactory().getText("title"));
        if (kaView != null) {
            title.append(kaView.getKundeNo());
        }
        setTitle(title.toString());
        setIconURL("de/augustakom/hurrican/gui/images/kunden_daten.gif");

        configureButton(CMD_SAVE, null, null, false, false);
        configureButton(CMD_CANCEL, "Ok", null, true, true);

        AKJLabel lblKundeNoOrig = getSwingFactory().createLabel("kunde.no.orig");
        AKJLabel lblHauptKundeNo = getSwingFactory().createLabel("haupt.kunde.no");
        AKJLabel lblName = getSwingFactory().createLabel("name");
        AKJLabel lblVorname = getSwingFactory().createLabel("vorname");
        AKJLabel lblStrasse = getSwingFactory().createLabel("strasse");
        AKJLabel lblPostfach = getSwingFactory().createLabel("postfach");
        AKJLabel lblOrt = getSwingFactory().createLabel("ort");
        AKJLabel lblOrtsteil = getSwingFactory().createLabel("ortsteil");
        AKJLabel lblBetreuer = getSwingFactory().createLabel("betreuer");
        AKJLabel lblHauptRN = getSwingFactory().createLabel("rufnummer");
        AKJLabel lblFax = getSwingFactory().createLabel("fax");
        AKJLabel lblMobil = getSwingFactory().createLabel("mobil");
        AKJLabel lblEMail = getSwingFactory().createLabel("email");
        AKJLabel lblTyp = getSwingFactory().createLabel("typ");
        AKJLabel lblVIP = getSwingFactory().createLabel("vip");
        AKJLabel lblFK = getSwingFactory().createLabel("fernkatastrophe");

        tfKundeNoOrig = getSwingFactory().createFormattedTextField("kunde.no.orig", false);
        tfHauptKundeNo = getSwingFactory().createFormattedTextField("haupt.kunde.no", false);
        tfName = getSwingFactory().createTextField("name", false);
        tfVorname = getSwingFactory().createTextField("vorname", false);
        tfStrasse = getSwingFactory().createTextField("strasse", false);
        AKJTextField tfPostfach = getSwingFactory().createTextField("postfach", false);
        tfPLZ = getSwingFactory().createTextField("plz", false);
        tfOrt = getSwingFactory().createTextField("ort", false);
        tfOrtsteil = getSwingFactory().createTextField("ortsteil", false);
        tfBetreuer = getSwingFactory().createTextField("betreuer", false);
        tfHauptRN = getSwingFactory().createTextField("ort", false);
        tfFax = getSwingFactory().createTextField("rufnummer", false);
        tfMobil = getSwingFactory().createTextField("fax", false);
        tfEMail = getSwingFactory().createTextField("mobil", false);
        tfTyp = getSwingFactory().createTextField("email", false);
        tfVIP = getSwingFactory().createTextField("vip", false);
        chbFK = getSwingFactory().createCheckBox("fernkatastrophe", false);

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(lblKundeNoOrig, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfKundeNoOrig, GBCFactory.createGBC(100, 0, 2, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblHauptKundeNo, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfHauptKundeNo, GBCFactory.createGBC(100, 0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblName, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfName, GBCFactory.createGBC(100, 0, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblVorname, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfVorname, GBCFactory.createGBC(100, 0, 2, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblStrasse, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfStrasse, GBCFactory.createGBC(100, 0, 2, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblPostfach, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfPostfach, GBCFactory.createGBC(100, 0, 2, 5, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblOrt, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfPLZ, GBCFactory.createGBC(10, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfOrt, GBCFactory.createGBC(90, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblOrtsteil, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfOrtsteil, GBCFactory.createGBC(100, 0, 2, 7, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBetreuer, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBetreuer, GBCFactory.createGBC(100, 0, 2, 8, 2, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblHauptRN, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(tfHauptRN, GBCFactory.createGBC(100, 0, 2, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblFax, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfFax, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblMobil, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfMobil, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEMail, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEMail, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblTyp, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfTyp, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblVIP, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfVIP, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblFK, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(chbFK, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel main = new AKJPanel(new GridBagLayout());
        main.add(left, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        main.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        main.add(right, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.VERTICAL));
        main.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(main, BorderLayout.CENTER);
    }

    /* Laedt die benoetigten Daten und 'fuellt' die TextFields */
    private void read() {
        if (kaView != null) {
            try {
                setWaitCursor();

                KundenService service = getBillingService(KundenService.class.getName(), KundenService.class);
                kunde = service.findKunde(kaView.getKundeNo());

                showKundendaten();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /* Zeigt die Kundendaten an. */
    private void showKundendaten() {
        if (kaView != null) {
            tfKundeNoOrig.setValue(kaView.getKundeNo());
            tfHauptKundeNo.setValue(kaView.getHauptKundenNo());
            tfName.setText(kaView.getName());
            tfVorname.setText(kaView.getVorname());
            tfStrasse.setText(kaView.getStrasseWithNumber());
            tfPLZ.setText(StringUtils.trimToEmpty(kaView.getPlz()));
            tfOrt.setText(kaView.getOrt());
            tfOrtsteil.setText(kaView.getOrtsteil());
            tfBetreuer.setText(kaView.getKundenbetreuer());
        }

        if (kunde != null) {
            tfHauptRN.setText(StringUtils.trimToEmpty(kunde.getHauptRufnummer()));
            tfFax.setText(StringUtils.trimToEmpty(kunde.getRnFax()));
            tfMobil.setText(StringUtils.trimToEmpty(kunde.getRnMobile()));
            tfEMail.setText(kunde.getEmail());
            tfTyp.setText(kunde.getKundenTyp());
            tfVIP.setText(kunde.getVip());
            chbFK.setSelected(kunde.isFernkatastrophe());
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

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
    }

}


