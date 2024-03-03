/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import static de.augustakom.hurrican.gui.tools.wbci.helper.HurricanAuftragHelper.CBVorgangType.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.tools.wbci.helper.HurricanAuftragHelper;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.LeitungBuilder;
import de.mnet.wbci.model.builder.MeldungPositionUebernahmeRessourceMeldungBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungBuilder;
import de.mnet.wbci.model.helper.RueckmeldungVorabstimmungHelper;
import de.mnet.wbci.service.WbciAutomationService;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciMeldungService;

/**
 * Dialog zur Angabe von Details fuer die WBCI AKM-TR Meldung.
 */
public class AkmTrDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, ActionListener, ItemListener {

    private static final long serialVersionUID = 2588851180053215119L;

    private static final Logger LOGGER = Logger.getLogger(AkmTrDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/AkmTrDialog.xml";

    public static final String AKMTR_TITLE = "akmtr.title";
    public static final String WECHSELTERMIN = "wechseltermin";
    public static final String LEITUNGSUEBERNAHME = "leitungsuebernahme";
    public static final String SICHERER_HAFEN = "sicherer.hafen";
    public static final String PKI_AUF = "pki.auf";
    public static final String VTR_NRS = "vtr.nrs";
    public static final String LINE_IDS = "line.ids";

    public static final String WHOLESALEHINWEIS = "wholesale.hinweis";
    private AKJDateComponent dcWechseltermin;


    private AKJCheckBox chbLeitungsuebernahme;
    private AKJPanel pnlLeistungsuebernahme;
    private AKJLabel lblWholesaleHinweis;
    private AKJCheckBox chbSichererHafen;
    private AKJTextField tfPkiAuf;
    private AKJList lsVtrNrs;

    private final WbciRequest wbciRequest;
    private final boolean isWholesaleRequest;

    private RueckmeldungVorabstimmung ruemVa;
    private WbciCommonService wbciCommonService;
    private WbciMeldungService wbciMeldungService;
    private WbciAutomationService wbciAutomationService;

    /**
     * Konstruktor mit Angabe des {@link WbciRequest}s sowie der {@link RueckmeldungVorabstimmung}, auf die sich die
     * AKM-TR beziehen soll.
     *
     * @param wbciRequest
     */
    public AkmTrDialog(WbciRequest wbciRequest, boolean isWholesaleRequest) {
        super(RESOURCE, true);
        this.wbciRequest = wbciRequest;
        this.isWholesaleRequest = isWholesaleRequest;

        try {
            initServices();
            createGUI();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /**
     * Looks up to the CCServices and init the service.
     *
     * @throws de.augustakom.common.service.exceptions.ServiceNotFoundException if a service could not be looked up
     */
    protected final void initServices() throws ServiceNotFoundException {
        wbciCommonService = getCCService(WbciCommonService.class);
        wbciMeldungService = getCCService(WbciMeldungService.class);
        wbciAutomationService = getCCService(WbciAutomationService.class);
    }

    @Override
    protected final void createGUI() {
        setTitle("AKM-TR Daten");

        AKJLabel lblWechseltermin = getSwingFactory().createLabel(WECHSELTERMIN);
        AKJLabel lblLeitungsuebernahme = getSwingFactory().createLabel(LEITUNGSUEBERNAHME);
        AKJLabel lblSichererHafen = getSwingFactory().createLabel(SICHERER_HAFEN);
        AKJLabel lblPkiAuf = getSwingFactory().createLabel(PKI_AUF);
        AKJLabel lblVtrNrs = getSwingFactory().createLabel(VTR_NRS);
        AKJLabel lblLineIds = getSwingFactory().createLabel(LINE_IDS);


        dcWechseltermin = getSwingFactory().createDateComponent(WECHSELTERMIN, false);
        chbSichererHafen = getSwingFactory().createCheckBox(SICHERER_HAFEN);

        createLeistungsuebernahmePanel();

        tfPkiAuf = getSwingFactory().createTextField(PKI_AUF, false);
        lsVtrNrs = getSwingFactory().createList(VTR_NRS);
        AKJScrollPane spVtrNrs = new AKJScrollPane(lsVtrNrs, new Dimension(200, 130));
        lsVtrNrs.setEnabled(false);

        AKJPanel dtlPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(lblWechseltermin     , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(dcWechseltermin      , GBCFactory.createGBC(  0,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblSichererHafen     , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(chbSichererHafen     , GBCFactory.createGBC(  0,  0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblLeitungsuebernahme, GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(pnlLeistungsuebernahme, GBCFactory.createGBC(  0,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblPkiAuf            , GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(tfPkiAuf             , GBCFactory.createGBC(  0,  0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblVtrNrs            , GBCFactory.createGBC(  0,  0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblLineIds           , GBCFactory.createGBC(  0,  0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(spVtrNrs             , GBCFactory.createGBC(  0,  0, 3, 5, 1, 3, GridBagConstraints.BOTH));
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(100,100, 4, 8, 1, 1, GridBagConstraints.BOTH));

        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(dtlPnl, BorderLayout.CENTER);

        configureButton(CMD_SAVE, "Senden", "Erzeugt und versendet eine AKM-TR", true, true);
    }

    private void createLeistungsuebernahmePanel() {
        FlowLayout lytLeistungsuebernahme = new FlowLayout(FlowLayout.LEFT);
        lytLeistungsuebernahme.setHgap(0);
        pnlLeistungsuebernahme = new AKJPanel(lytLeistungsuebernahme);

        chbLeitungsuebernahme = getSwingFactory().createCheckBox(LEITUNGSUEBERNAHME, this, false);
        chbLeitungsuebernahme.addItemListener(this);
        pnlLeistungsuebernahme.add(chbLeitungsuebernahme);

        lblWholesaleHinweis = getSwingFactory().createLabel(WHOLESALEHINWEIS);
        lblWholesaleHinweis.setForeground(Color.red);
        lblWholesaleHinweis.setVisible(false);
        pnlLeistungsuebernahme.add(lblWholesaleHinweis);
    }

    @Override
    public final void loadData() {
        this.ruemVa = wbciCommonService.findLastForVaId(wbciRequest.getVorabstimmungsId(), RueckmeldungVorabstimmung.class);
        setTitle(String.format(getSwingFactory().getText(AKMTR_TITLE), ruemVa.getWbciGeschaeftsfall().getVorabstimmungsId()));
        dcWechseltermin.setDateTime(wbciRequest.getWbciGeschaeftsfall().getWechseltermin().atStartOfDay());
        lsVtrNrs.addItems(RueckmeldungVorabstimmungHelper.getWitaVtrNrsAndLineIds(ruemVa));

        if (GeschaeftsfallTyp.VA_KUE_MRN.equals(wbciRequest.getWbciGeschaeftsfall().getTyp())) {
            String portierungskennungPKIauf = wbciCommonService.getTnbKennung(wbciRequest.getWbciGeschaeftsfall().getAuftragId());
            tfPkiAuf.setText(portierungskennungPKIauf);
        }

        final Technologie ruemVaTechnologie = ruemVa.getTechnologie();
        if (ruemVaTechnologie != null
                && ruemVaTechnologie.isRessourcenUebernahmePossible(IOType.OUT, ruemVa.getAbsender())
                && ruemVaTechnologie.isCompatibleTo(wbciRequest.getWbciGeschaeftsfall().getMnetTechnologie())) {

            // Check for ADA codes in the RUEM-VA Meldung
            // If any ADA codes are found then it is NOT possible to send a AKM-TR with Leitungsuebernahme = TRUE. A
            // Leitungsuebernahme is in this case only possible when a new VA with the correct address is sent (i.e. a
            // STR-AEN followed by a new VA with the correct address)
            if (ruemVa.hasADAMeldungsCode()) {
                MessageHelper.showInfoDialog(this,
                        getSwingFactory().getText("ressourcen.uebernahme.adapresent.msg"),
                        getSwingFactory().getText("ressourcen.uebernahme.adapresent.title"), null, true);
                useSichererHafen();
            }
            else {
                // Ressourcen-Uebernahme ist erlaubt und die Technologie des abgebenden Carriers ist zur M-net Technologie
                // grundsaetzlich kompatibel --> Leitungsuebernahme automatisch markieren
                chbLeitungsuebernahme.setSelected(true);

                if (Technologie.SONSTIGES.equals(ruemVaTechnologie)) {
                    if (ruemVa.hasWitaVtrNr()) {
                        MessageHelper.showInfoDialog(this,
                                getSwingFactory().getText("ressourcen.uebernahme.sonstiges.msg"),
                                getSwingFactory().getText("ressourcen.uebernahme.sonstiges.title"), null, true);
                    }
                    else {
                        useSichererHafen();
                    }
                }
            }
        }
        else {
            useSichererHafen();
        }
    }

    private void useSichererHafen() {
        chbSichererHafen.setSelected(true);
        chbLeitungsuebernahme.setEnabled(false);

        chbLeitungsuebernahme.setSelected(false);
        lsVtrNrs.setEnabled(false);
    }


    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void doSave() {
        try {
            UebernahmeRessourceMeldungBuilder meldung = new UebernahmeRessourceMeldungBuilder()
                    .withSichererhafen(chbSichererHafen.isSelected())
                    .withUebernahme(chbLeitungsuebernahme.isSelected())
                    .withPortierungskennungPKIauf(tfPkiAuf.getText(null))
                    .addMeldungPosition(
                            new MeldungPositionUebernahmeRessourceMeldungBuilder()
                                    .withMeldungsCode(MeldungsCode.AKMTR_CODE)
                                    .withMeldungsText(MeldungsCode.AKMTR_CODE.getStandardText())
                                    .build()
                    );

            if (chbLeitungsuebernahme.isSelected()) {
                addSelectedLineIds(meldung);
            }

            wbciMeldungService.createAndSendWbciMeldung(meldung.build(), wbciRequest.getWbciGeschaeftsfall().getVorabstimmungsId());

            prepare4Close();
            setValue(AKJOptionDialog.OK_OPTION);

            if (wbciAutomationService.canWitaOrderBeProcessedAutomatically(wbciRequest.getVorabstimmungsId())) {
                MessageHelper.showInfoDialog(this,
                        getSwingFactory().getText("automated.wita.order.info.dialog.msg"),
                        getSwingFactory().getText("automated.wita.order.info.dialog.title"), null, true);
            }
            else {
            // Open the next GUI for the user - this depends on how the line should be ordered (WITA Neubestellung, ...)
            Long auftragId = wbciRequest.getWbciGeschaeftsfall().getAuftragId();
            String vaId = wbciRequest.getVorabstimmungsId();
            if (isResourceUebernahme()) {
                // opens the WITA el. Vorgang Wizard to perform an Anbieterwechsel
                HurricanAuftragHelper.openWitaElektronischerVorgangWizard(vaId, ANBIETER_WECHSEL);
            }
            else if (canMnetTechnologyBeOrderedWithWita()) {
                // opens the WITA el. Vorgang Wizard to perform a Neubestellung
                HurricanAuftragHelper.openWitaElektronischerVorgangWizard(vaId, NEUBESTELLUNG);
            }
            else {
                // opens the Hurrican Auftrag
                HurricanAuftragHelper.openHurricanAuftrag(auftragId);
            }
        }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Checks to see if the line <i>could</i> be ordered using WITA.
     * <p/>
     * The assumption made here is when the M-Net-{@link de.mnet.wbci.model.Technologie} is transferable to M-net then
     * the new line can be ordered using WITA.
     *
     * @return
     */
    private boolean canMnetTechnologyBeOrderedWithWita() {
        Technologie mnetTechnologie = wbciRequest.getWbciGeschaeftsfall().getMnetTechnologie();
        CarrierCode ekpPartner = wbciRequest.getWbciGeschaeftsfall().getEKPPartner();
        return mnetTechnologie.isRessourcenUebernahmePossible(IOType.IN, ekpPartner);
    }

    /**
     * Checks to see
     *
     * @return
     */
    private boolean isResourceUebernahme() {
        return wbciCommonService.isResourceUebernahmeRequested(wbciRequest.getVorabstimmungsId());
    }

    /*
     * Ermittelt die selektierten LineId bzw. WITA VtrNrs und fuegt diese
     * dem angegebenen {@link UebernahmeRessourceMeldungBuilder} hinzu.
     */
    private void addSelectedLineIds(UebernahmeRessourceMeldungBuilder meldungBuilder) throws AKGUIException {
        List<String> selectedVtrNrs = lsVtrNrs.getSelectedValues(String.class);
        if (CollectionUtils.isEmpty(selectedVtrNrs)) {
            throw new AKGUIException("Wenn Leitungsübernahme angekreutzt ist, muss mindestens eine WITA " +
                    "Vertragsnummer oder eine WBCI Line ID ausgewaehlt werden!");
        }

        for (String selectedItem : selectedVtrNrs) {
            LeitungBuilder leitungBuilder = new LeitungBuilder();

            boolean found = false;
            for (TechnischeRessource technischeRessource : ruemVa.getTechnischeRessourcen()) {
                if (StringUtils.equals(technischeRessource.getVertragsnummer(), selectedItem)) {
                    leitungBuilder.withVertragsnummer(selectedItem);
                    found = true;
                    break;
                }
                else if (StringUtils.equals(technischeRessource.getLineId(), selectedItem)) {
                    leitungBuilder.withLineId(selectedItem);
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new IllegalArgumentException(String.format(
                        "Die LineId/Vertragsnummer '%s' konnte nicht in der Rueckmeldung Vorabstimmung gefunden werden!", selectedItem));
            }

            meldungBuilder.addLeitung(leitungBuilder.build());
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        checkboxSelectionChanged(event);
    }

    private void checkboxSelectionChanged(EventObject event) {
        if (event.getSource() == chbLeitungsuebernahme) {
            lsVtrNrs.setEnabled(chbLeitungsuebernahme.isSelected());

            if (isWholesaleRequest) {
                lblWholesaleHinweis.setVisible(chbLeitungsuebernahme.isSelected());
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        checkboxSelectionChanged(itemEvent);
    }
}
