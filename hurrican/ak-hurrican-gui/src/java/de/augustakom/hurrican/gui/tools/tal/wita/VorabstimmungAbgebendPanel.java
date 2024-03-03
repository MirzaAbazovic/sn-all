/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2012 17:27:48
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.model.VorabstimmungAbgebend;
import de.mnet.wita.service.WitaVorabstimmungService;

/**
 * Panel zur Anzeige u. Bearbeitung der Daten für die Vorabstimmung abgebend
 */
public class VorabstimmungAbgebendPanel extends AbstractDataPanel implements AKDataLoaderComponent {

    private static final long serialVersionUID = -4007680889766744624L;

    private static final Logger LOGGER = Logger.getLogger(VorabstimmungAbgebendPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/wita/resources/VorabstimmungAbgebendPanel.xml";

    private static final String VORABSTIMMUNG_CARRIER_AUFNEHMEND = "vorabstimmung.carrier.aufnehmend";
    private static final String ANBIETERWECHSEL_DATUM = "anbieterwechsel.datum";
    private static final String VORABSTIMMUNG_ERGEBNISS = "vorabstimmung.ergebniss";
    private static final String BEMERKUNG_VORABSTIMMUNG = "bemerkung.vorabstimmung";

    private static final String RUECKMELDUNG_POSITIV = "positiv";
    private final Endstelle endstelle;
    private final AuftragDaten auftragDaten;
    private CarrierService carrierService;
    private WitaVorabstimmungService witaVorabstimmungService;
    private AKJComboBox cbVorabstimmungCarrierAufnehmend;
    private AKJDateComponent dcAnbieterwechselDatum;
    private AKJComboBox cbVorabstimmungErgebnis;
    private AKJTextArea taBemerkungVorabstimmung;
    private VorabstimmungAbgebend vorabstimmungAbgebend;
    private AKJTextField vorabstimmungsID = new AKJTextField();

    public VorabstimmungAbgebendPanel(Endstelle endstelle, AuftragDaten auftragDaten) {
        super(RESOURCE);
        this.endstelle = endstelle;
        this.auftragDaten = auftragDaten;
        try {
            createGUI();
            initServices();
            loadDefaultData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    private void initServices() throws ServiceNotFoundException {
        carrierService = getCCService(CarrierService.class);
        witaVorabstimmungService = getCCService(WitaVorabstimmungService.class);
    }

    private void loadDefaultData() throws FindException {
        List<Carrier> carrierList = carrierService.findCarrierForAnbieterwechsel();
        cbVorabstimmungCarrierAufnehmend.addItems(carrierList, true, Carrier.class);
        cbVorabstimmungErgebnis.addItems(new ArrayList<>(Arrays.asList("positiv", "negativ")));
        cbVorabstimmungErgebnis.setSelectedItem(null);
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblVorabstimmungCarrierAufnehmend = getSwingFactory().createLabel(VORABSTIMMUNG_CARRIER_AUFNEHMEND);
        AKJLabel lblAnbieterwechselDatum = getSwingFactory().createLabel(ANBIETERWECHSEL_DATUM);
        AKJLabel lblVorabtimmungErgebnis = getSwingFactory().createLabel(VORABSTIMMUNG_ERGEBNISS);
        AKJLabel lblBemerkungVorabstimmung = getSwingFactory().createLabel(BEMERKUNG_VORABSTIMMUNG);

        cbVorabstimmungCarrierAufnehmend = getSwingFactory().createComboBox(VORABSTIMMUNG_CARRIER_AUFNEHMEND,
                new AKCustomListCellRenderer<>(Carrier.class, Carrier::getPortierungskennungAndName));
        cbVorabstimmungCarrierAufnehmend.addActionListener(getActionListener());
        dcAnbieterwechselDatum = getSwingFactory().createDateComponent(ANBIETERWECHSEL_DATUM, true);
        cbVorabstimmungErgebnis = getSwingFactory().createComboBox(VORABSTIMMUNG_ERGEBNISS);
        taBemerkungVorabstimmung = getSwingFactory().createTextArea(BEMERKUNG_VORABSTIMMUNG);

        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkungVorabstimmung);

        // @formatter:off
        this.setLayout(new GridBagLayout());
        this.add(new JLabel("Vorabstimmungs-ID:"), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()                     , GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(vorabstimmungsID                   , GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblVorabstimmungCarrierAufnehmend  , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()                     , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(cbVorabstimmungCarrierAufnehmend   , GBCFactory.createGBC(100,  0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblAnbieterwechselDatum            , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(dcAnbieterwechselDatum             , GBCFactory.createGBC(100,  0, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblVorabtimmungErgebnis            , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(cbVorabstimmungErgebnis            , GBCFactory.createGBC(100,  0, 2, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblBemerkungVorabstimmung          , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(spBemerkung                        , GBCFactory.createGBC(100,  0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()                     , GBCFactory.createGBC(  0,  0, 4, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    @Override
    public void readModel() throws AKGUIException {
        // not used
    }

    @Override
    public void saveModel() throws AKGUIException {
        // not used
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    public Object getModel() {
        return vorabstimmungAbgebend;
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
        vorabstimmungAbgebend = null;
        if (model instanceof VorabstimmungAbgebend) {
            vorabstimmungAbgebend = (VorabstimmungAbgebend) model;
            if (vorabstimmungAbgebend.getCarrier() != null) {
                cbVorabstimmungCarrierAufnehmend.selectItem("getId", Carrier.class, vorabstimmungAbgebend.getCarrier()
                        .getId());
            }
            if (vorabstimmungAbgebend.getRueckmeldung()) {
                cbVorabstimmungErgebnis.setSelectedIndex(0);
            }
            else {
                cbVorabstimmungErgebnis.setSelectedIndex(1);
            }
            vorabstimmungsID.setText(vorabstimmungAbgebend.getVorabstimmungsIDFax());
            dcAnbieterwechselDatum.setDate(Date.from(vorabstimmungAbgebend.getAbgestimmterProdiverwechsel().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            taBemerkungVorabstimmung.setText(vorabstimmungAbgebend.getBemerkung());
        }
    }

    public boolean saveVorabstimmung() {
        try {
            vorabstimmungAbgebend = witaVorabstimmungService.findVorabstimmungAbgebend(endstelle.getEndstelleTyp(), auftragDaten.getAuftragId());
            if (allFieldsClean()) {
                if (vorabstimmungAbgebend == null) {
                    return true;
                }
                witaVorabstimmungService.deleteVorabstimmungAbgebend(vorabstimmungAbgebend);
                return true;
            }

            Carrier selectedCarrier = (Carrier) cbVorabstimmungCarrierAufnehmend.getSelectedItem();
            if ((selectedCarrier == null) || (selectedCarrier.getId() == null)) {
                MessageHelper.showInfoDialog(getMainFrame(), getSwingFactory().getText("no.carrier"), null, null, true);
                return false;
            }
            if (vorabstimmungAbgebend == null) {
                vorabstimmungAbgebend = new VorabstimmungAbgebend();
            }
            vorabstimmungAbgebend.setCarrier(selectedCarrier);
            vorabstimmungAbgebend.setAuftragId(auftragDaten.getAuftragId());
            vorabstimmungAbgebend.setEndstelleTyp(endstelle.getEndstelleTyp());
            Date anbieterwechselDatum = dcAnbieterwechselDatum.getDate(null);
            if (anbieterwechselDatum == null) {
                MessageHelper.showInfoDialog(getMainFrame(), getSwingFactory().getText("no.anbieterwechsel.date"), null, null, true);
                return false;
            }
            vorabstimmungAbgebend.setAbgestimmterProdiverwechsel(DateConverterUtils.asLocalDate(anbieterwechselDatum));
            String selectedErgebniss = (String) cbVorabstimmungErgebnis.getSelectedItem();
            if (selectedErgebniss == null) {
                MessageHelper.showInfoDialog(getMainFrame(), getSwingFactory().getText("no.result"), null, null, true);
                return false;
            }
            vorabstimmungAbgebend.setRueckmeldung(selectedErgebniss.equals(RUECKMELDUNG_POSITIV));
            vorabstimmungAbgebend.setBemerkung(taBemerkungVorabstimmung.getText());
            vorabstimmungAbgebend.setVorabstimmungsIDFax(vorabstimmungsID.getText());
            vorabstimmungAbgebend = witaVorabstimmungService.saveVorabstimmungAbgebend(vorabstimmungAbgebend);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            return false;
        }
        return true;
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    public final void loadData() {
        // not used
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    private boolean allFieldsClean() {
        // @formatter:off
        Carrier selectedCarrier = (Carrier) cbVorabstimmungCarrierAufnehmend.getSelectedItem();
        return (selectedCarrier == null || selectedCarrier.getId() == null)
                && dcAnbieterwechselDatum.getDate(null) == null
                // && StringUtils.isBlank((String) cbVorabstimmungErgebnis.getSelectedItemValue()) // just yes or no
                && StringUtils.isBlank(taBemerkungVorabstimmung.getText());
        // @formatter:on
    }
}
