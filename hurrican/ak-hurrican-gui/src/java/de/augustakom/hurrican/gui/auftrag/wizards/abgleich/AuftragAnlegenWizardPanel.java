/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.2004 14:10:40
 */
package de.augustakom.hurrican.gui.auftrag.wizards.abgleich;

import java.awt.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.auftrag.wizards.shared.AuftragDetailsPanel;
import de.augustakom.hurrican.gui.auftrag.wizards.shared.CreateAccountAction;
import de.augustakom.hurrican.gui.auftrag.wizards.shared.CreateEndstellenAction;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Wohnheim;
import de.augustakom.hurrican.model.cc.temp.AuftragsMonitor;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.WohnheimService;


/**
 * Wizard-Panel, um die Daten fuer einen neuen Auftrag abzufragen und die Auftrags-Daten zu speichern.
 *
 *
 */
public class AuftragAnlegenWizardPanel extends AbstractServiceWizardPanel implements AuftragWizardObjectNames,
        AKDataLoaderComponent, IServiceCallback {

    private static final Logger LOGGER = Logger.getLogger(AuftragAnlegenWizardPanel.class);
    private static final long serialVersionUID = 3534184390875840085L;

    private AuftragDetailsPanel detailsPanel = null;

    private AuftragsMonitor auftragsMonitor = null;
    private List<AuftragsMonitor> auftragsMonitorList = null;
    private Long kundeNoOrig = null;
    private AuftragDaten tmpAuftragDaten = null;
    private AuftragTechnik tmpAuftragTechnik = null;
    private Wohnheim wohnheim = null;

    private ProduktService produktService;
    private WohnheimService wohnheimService;
    private KundenService kundenService;

    /**
     * Konstruktor
     *
     * @param wizardComponents
     */
    public AuftragAnlegenWizardPanel(AKJWizardComponents wizardComponents) {
        super(null, wizardComponents);
        createGUI();
        initServices();
    }

    private void initServices() {
        try {
            produktService = getCCService(ProduktService.class);
            wohnheimService = getCCService(WohnheimService.class);
            kundenService = getBillingService(KundenService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showInfoDialog(HurricanSystemRegistry.instance().getMainFrame(),
                    String.format("Services konnten nicht initialisiert werden!%n%s",
                            ExceptionUtils.getFullStackTrace(e))
            );
            cancel();
        }
    }

    @Override
    protected final void createGUI() {
        detailsPanel = new AuftragDetailsPanel(getWizardComponents());
        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(detailsPanel, BorderLayout.CENTER);
    }

    @Override
    public void update() {
        if (getWizardComponents().getDirection() == AKJWizardComponents.DIRECTION_FORWARD) {
            setFinishButtonEnabled(false);
            loadData();
        }
    }

    /**
     * Liest alle benoetigten Daten aus.
     *
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            init();

            setWaitCursor();
            kundeNoOrig = (Long) getWizardObject(WIZARD_OBJECT_KUNDEN_NO);

            tmpAuftragDaten = new AuftragDaten();
            tmpAuftragDaten.setAuftragNoOrig(auftragsMonitor.getAuftragNoOrig());
            tmpAuftragDaten.setBearbeiter(HurricanSystemRegistry.instance().getCurrentUserName());
            tmpAuftragDaten.setStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN);

            tmpAuftragTechnik = new AuftragTechnik();
            getWizardComponents().addWizardObject(WIZARD_OBJECT_CC_AUFTRAG_TECHNIK, tmpAuftragTechnik);

            detailsPanel.setModel(tmpAuftragDaten);
            setFinishButtonEnabled(true);
        }
        catch (HurricanGUIException e) {
            error();
            MessageHelper.showInfoDialog(this, e.getMessage(), null, true);
        }
        catch (Exception e) {
            error();
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }


    @SuppressWarnings("unchecked")
    private void init() throws HurricanGUIException {
        auftragsMonitor = (AuftragsMonitor) getWizardObject(WIZARD_SELECTED_AUFTRAGS_MONITOR);
        auftragsMonitorList = (List<AuftragsMonitor>) getWizardObject(WIZARD_SELECTED_AUFTRAGS_MONITOR_LIST);

        if ((auftragsMonitor == null) && CollectionTools.isEmpty(auftragsMonitorList)) {
            throw new HurricanGUIException("Es ist kein Objekt fuer den Abgleich definiert!");
        }
        else if ((auftragsMonitor == null) && CollectionTools.isNotEmpty(auftragsMonitorList)) {
            sortAuftragsMonitorList();
            auftragsMonitor = auftragsMonitorList.get(0);
        }
    }

    /*
     * Sortiert die Liste der AuftragsMonitor Objekte ueber das Produkt-Flag "Kombi-Produkt".
     * Kombi-Produkte werden "nach oben" sortiert, andere einfach hinten angefuegt.
     * Dadurch wird bei Auftraegen der Kombination "DSL+ISDN" und "ISDN TK" der DSL+ISDN Auftrag
     * zuerst angelegt; im Anschluss daran die anderen.
     * (Da die Reihenfolge fuer die Bearbeitung nicht kritisch ist wird hier auch nicht weitergehend geprueft.)
     */
    private void sortAuftragsMonitorList() {
        List<AuftragsMonitor> tmpList = new ArrayList<AuftragsMonitor>(auftragsMonitorList.size());
        for (AuftragsMonitor am : auftragsMonitorList) {
            if (isCombiProdukt(am.getCcProduktId())) {
                tmpList.add(0, am);
            }
            else {
                tmpList.add(am);
            }
        }
        auftragsMonitorList = tmpList;
    }


    private boolean isCombiProdukt(Long produktId) {
        Produkt produkt;
        try {
            produkt = produktService.findProdukt(produktId);
            return BooleanTools.nullToFalse(produkt.getIsCombiProdukt());
        }
        catch (FindException e) {
            return false;
        }
    }


    @Override
    public void finish() {
        try {
            setWaitCursor();
            setFinishButtonEnabled(false);
            detailsPanel.saveModel();  // Detail-Werte werden in temp. Objekte geschrieben; keine DB-Aktion!

            if (CollectionTools.isEmpty(auftragsMonitorList)) {
                finish4SingleOrderCreation();
            }
            else {
                finishe4MultipleOrderCreation();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
            setFinishButtonEnabled(true);
        }
    }

    /* Legt genau einen Hurrican Auftrag zu dem aktuellen AuftragsMonitor an. */
    private void finish4SingleOrderCreation() throws FindException, ServiceNotFoundException, HurricanGUIException, StoreException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Produkt produkt = checkIfProductExists(auftragsMonitor.getCcProduktId());
        selectWohnheim(produkt);

        Auftrag auftrag = createAuftrag(auftragsMonitor, produkt);

        // Auftragsmaske oeffnen
        AuftragDataFrame.openFrame(auftrag);
    }

    /* Durchlaeuft die AuftragsMonitor-Liste und legt jeweils die notwendigen Hurrican-Auftraege an. */
    private void finishe4MultipleOrderCreation() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, StoreException, HurricanGUIException, ServiceNotFoundException {
        Auftrag firstCreated = null;
        for (AuftragsMonitor toCreate : auftragsMonitorList) {
            Produkt produkt = checkIfProductExists(toCreate.getCcProduktId());

            for (int i = 0; i < toCreate.getDifferenz(); i++) {  // Anzahl im AuftragsMonitor Objekt beachten und n Hurrican-Auftraege anlegen
                Auftrag auftrag = createAuftrag(toCreate, produkt);
                if (firstCreated == null) {
                    firstCreated = auftrag;
                }
            }
        }
        AuftragDataFrame.openFrame(firstCreated);
    }


    private Auftrag createAuftrag(AuftragsMonitor toCreate, Produkt produkt) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ServiceNotFoundException, StoreException {
        AuftragDaten auftragDaten = new AuftragDaten();
        PropertyUtils.copyProperties(auftragDaten, tmpAuftragDaten);
        auftragDaten.setProdId(toCreate.getCcProduktId());
        if (produkt != null && produkt.getErstellStatusId() != null) {
            auftragDaten.setStatusId(produkt.getErstellStatusId());
        }

        AuftragTechnik auftragTechnik = new AuftragTechnik();
        PropertyUtils.copyProperties(auftragTechnik, tmpAuftragTechnik);

        CCAuftragService auftragService = getCCService(CCAuftragService.class);
        Auftrag auftrag = auftragService.createAuftrag(kundeNoOrig, auftragDaten, auftragTechnik, wohnheim,
                HurricanSystemRegistry.instance().getSessionId(), this);

        CreateEndstellenAction esAction = new CreateEndstellenAction(auftrag, this);
        esAction.actionPerformed(null);

        CreateAccountAction accAction = new CreateAccountAction(auftrag, this);
        accAction.actionPerformed(null);

        return auftrag;
    }


    /* Ueberprueft, ob es sich um einen Appartement-Anschluss handelt und
     * sucht in diesem Fall nach dem zugehoerigen Wohnheim.
     */
    private void selectWohnheim(Produkt produkt) throws FindException {
        if (Produkt.PROD_ID_APPARTEMENT.equals(produkt.getId()) &&
                !BooleanTools.nullToFalse(produkt.getLeitungsNrAnlegen())) {
            Kunde kunde = kundenService.findKunde(kundeNoOrig);

            List<Wohnheim> wohnheime = wohnheimService.findByKundeNoOrig(kunde.getHauptKundenNo());

            if ((wohnheime != null) && (!wohnheime.isEmpty())) {
                if (wohnheime.size() > 1) {
                    // Wohnheim selektieren
                    while (wohnheim == null) {
                        WohnheimSelectionDialog dlg = new WohnheimSelectionDialog(wohnheime);
                        Object selection = DialogHelper.showDialog(this, dlg, true, true);
                        if (selection instanceof Wohnheim) {
                            wohnheim = (Wohnheim) selection;
                        }
                    }
                }
                else {
                    wohnheim = wohnheime.get(0);
                }
            }
            else {
                StringBuilder msg = new StringBuilder();
                msg.append("Der Hauptkundennummer {0} ist kein Wohnheim zugeordnet. ");
                msg.append("Deshalb kann dem Auftrag keine Leitungsnummer zugeordnet werden.");
                String hkno = (kunde.getHauptKundenNo() != null) ? kunde.getHauptKundenNo().toString() : "<null>";
                MessageHelper.showInfoDialog(this, msg.toString(), hkno);
            }
        }
    }


    private Produkt checkIfProductExists(Long produktId) throws HurricanGUIException {
        try {
            Produkt produkt = produktService.findProdukt(produktId);
            if (produkt == null) {
                StringBuilder msg = new StringBuilder();
                msg.append("Es konnte kein Hurrican-Produkt gefunden werden, dass zu der ");
                msg.append("Produkt-ID {0} passt.");
                throw new HurricanGUIException(StringTools.formatString(msg.toString(),
                        new Object[] { "" + auftragsMonitor.getCcProduktId() }, null));
            }
            return produkt;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException(
                    "Bei der Ermittlung des zugehörigen Hurrican-Produkts ist ein Fehler aufgetreten.", e);
        }
    }

    @Override
    public Object doServiceCallback(Object source, int callbackAction, Map<String, ?> parameters) {
        if (callbackAction == CCAuftragService.CALLBACK_CREATE_AUFTRAG_INFO) {
            String message = (String) parameters.get(CCAuftragService.CALLBACK_PARAM_CREATE_AUFTRAG_INFO);
            MessageHelper.showInfoDialog(HurricanSystemRegistry.instance().getMainFrame(), message);
        }
        return null;
    }

    /* Stellt sicher, dass der Wizard nicht beendet werden kann. */
    private void error() {
        setFinishButtonEnabled(false);
    }
}


