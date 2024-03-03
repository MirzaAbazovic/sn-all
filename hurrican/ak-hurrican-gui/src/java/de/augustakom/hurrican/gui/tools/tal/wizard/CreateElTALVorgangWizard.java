/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2007 08:12:24
 */
package de.augustakom.hurrican.gui.tools.tal.wizard;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardOptionDialog;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.mnet.wbci.model.WbciWitaOrderDataVO;
import de.mnet.wbci.service.WbciWitaServiceFacade;

/**
 * Wizard, um einen el. TAL-Vorgang auszuloesen.
 *
 *
 */
public final class CreateElTALVorgangWizard extends AbstractServiceWizardOptionDialog {

    private static final long serialVersionUID = -6966324928374111537L;

    /**
     * Wizard Objekt-Name fuer die ID der Carrierbestellung.
     */
    static final String WIZARD_OBJECT_CB_ID = "cb.id";

    /**
     * Wizard Objekt-Name fuer die ID des Ziel-Carriers.
     */
    static final String WIZARD_OBJECT_CARRIER_ID = "carrier.id";

    /**
     * Wizard Objekt-Name fuer die ID des HVT-Standorts.
     */
    static final String WIZARD_OBJECT_HVT_STANDORT_ID = "hvt.std.id";

    /**
     * Wizard Objekt-Name fuer das AuftragDaten-Objekt.
     */
    public static final String WIZARD_OBJECT_AUFTRAG_DATEN = "auftrag.daten";

    /**
     * Wizard Objekt-Name fuer eine Liste mit CBVorgangSubOrder Modellen.
     */
    static final String WIZARD_OBJECT_SUB_ORDERS_4_KLAMMERUNG = "orders.for.klammerung";

    /**
     * Wizard Objekt-Name fuer ein Map mit Auftrag IDs und der zugehörige Set von {@link ArchiveDocumentDto}, die an die
     * TAL-Bestellung angehaengt werden sollen.
     */
    static final String WIZARD_OBJECT_AUFTRAG_WITH_ATTACHMENTS = "auftrag.with.attachments";

    /**
     * Wizard Objekt-Name fuer die Angabe, ob eine 4-Draht Bestellung mit zwei Ports vorliegt.
     */
    static final String WIZARD_OBJECT_4_DRAHT = "cb.vierDraht";

    /**
     * Wizard Objekt-Name fuer die ID des gewaehlten CBVorgang-Typs.
     */
    static final String WIZARD_OBJECT_CBVORGANG_TYP = "cb.vorgang.typ";

    /**
     * Wizard Objekt-Name fuer die ID des gewaehlten internen Geschaeftsfall-Typs.
     */
    static final String WIZARD_OBJECT_TAL_GF_TYP_INTERN = "gf.typ.intern";

    /**
     * Wizard Objekt-Name fuer die ID des gewaehlten DTAG-Usecases.
     */
    static final String WIZARD_OBJECT_DTAG_USECASE = "dtag.usecase";

    /**
     * Wizard Objekt-Name fuer den AM Usernamen (nur fuer den WITA-Simulator Testmodus).
     */
    static final String WIZARD_OBJECT_TEST_AM_USER = "test.am.user";

    /**
     * Wizard Objekt-Name fuer die ID des (Alt-)Auftrags, der fuer eine Port-Aenderung verwendet werden soll
     */
    static final String WIZARD_OBJECT_AUFTRAG_ID_4_PORT_CHANGE = "auftrag.id.4.port.change";

    /**
     * Wizard Objekt-Name fuer die ID des HVT-Auftrags, der fuer den Wechsel nach KVz verwendet werden soll
     */
    static final String WIZARD_OBJECT_AUFTRAG_ID_4_HVT_TO_KVZ = "auftrag.id.4.hvt.to.kvz";

    /**
     * Wizard Objekt-Name fuer die Angabe der Auftrags-ID, von dem der Account auf den aktuellen Auftrag uebernommen
     * werden soll.
     */
    static final String WIZARD_OBJECT_TRANSFER_ACCOUNT_FROM = "transferAccountFrom";

    static final String WIZARD_OBJECT_ENDSTELLE = "endstelle";

    /**
     * Wizard Objekt-Name fuer die WBCI VorabstimmungsId, der voreingestellt werden soll.
     */
    public static final String WIZARD_OBJECT_WBCI_VORABSTIMMUNGSID = "wbci.vorabstimmungsId";

    /**
     * Wizard Objekt-Name fuer das ein oder aus-blenden der WITA GUI innerhalb des Wizards. Wenn das Objekt auf {@link
     * Boolean#TRUE} gesetzt ist, wird das WITA GUI ausgeblendet. Ansonsten wird das WITA GUI angezeigt.
     */
    static final String WIZARD_OBJECT_HIDE_WITA_WIZARD = "wita.gui.hide";

    /**
     * Wizard Objekt-Name fuer das ein oder aus-blenden der WBCI GUI innerhalb des Wizards. Wenn das Objekt auf {@link
     * Boolean#TRUE} gesetzt ist, wird das WBCI GUI ausgeblendet. Ansonsten wird das WBCI GUI angezeigt.
     */
    static final String WIZARD_OBJECT_HIDE_WBCI_WIZARD = "wbci.gui.hide";

    private AbstractServiceWizardPanel selectCbVorgangPanel;

    public static CreateElTALVorgangWizard create(Long cbId, Long carrierId, Endstelle endstelle,
            AuftragDaten auftragDaten, Long cbVorgangTyp) {
        if (endstelle == null || auftragDaten == null) {
            throw new IllegalArgumentException(
                    "TAL-Wizard benoetigt eine gueltige Endstelle und den Auftrag!");
        }

        CreateElTALVorgangWizard wizard = new CreateElTALVorgangWizard(cbId, carrierId, endstelle, auftragDaten,
                cbVorgangTyp, Boolean.FALSE, Boolean.FALSE, null);

        wizard.setSelectCbVorgangPanel(new WitaSelectCBVorgangWizardPanel(wizard.getWizardComponents()));

        wizard.createGUI();
        return wizard;
    }

    /**
     * Returns the {@link CreateElTALVorgangWizard} to be used when creating a new Vorabstimmung, to replace a cancelled
     * (STR-AEN) Vorabstimmung. The wizzard uses the supplied hurrican {@code auftragId} to retrieve all data required
     * by the Wizzard (like Endstelle, Carrierbestellung, etc) and also ensures that all Wita Vorgange are hidden.
     *
     * @param auftragId              the hurrican auftragsId
     * @param strAenVorabstimmungsId the vorabstimmungsId of the cancelled vorabstimmung.
     * @return
     * @throws ServiceNotFoundException
     * @throws FindException
     */
    public static CreateElTALVorgangWizard createWbciWizardWithPreselectedVaId(long auftragId, String strAenVorabstimmungsId) throws ServiceNotFoundException, FindException {
        EndstellenService endstellenService = CCServiceFinder.instance().getCCService(EndstellenService.class);
        CarrierService carrierService = CCServiceFinder.instance().getCCService(CarrierService.class);
        CCAuftragService ccAuftragService = CCServiceFinder.instance().getCCService(CCAuftragService.class);

        Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        Carrierbestellung carrierbestellung = carrierService.findCBs4Endstelle(endstelle.getId()).get(0);

        Long carrierId = 0L; // this is not relevent for WBCI and can be set to 0
        AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(auftragId);

        CreateElTALVorgangWizard wizard = new CreateElTALVorgangWizard(carrierbestellung.getId(), carrierId, endstelle, auftragDaten,
                null, Boolean.TRUE, Boolean.FALSE, strAenVorabstimmungsId);

        wizard.setSelectCbVorgangPanel(new WitaSelectCBVorgangWizardPanel(wizard.getWizardComponents()));

        wizard.createGUI();
        return wizard;
    }

    /**
     * Returns the {@link CreateElTALVorgangWizard} to be used when creating a new WITA el. Vorgang. The wizard uses the
     * supplied hurrican {@code auftragId} to retrieve all data required by the Wizzard (like Endstelle,
     * Carrierbestellung, etc) and also ensures that all WBCI Vorgange are hidden.
     *
     * @param vorabstimmungsId the vorabstimmungsId of the cancelled vorabstimmung.
     * @param cbVorgangTyp     the type of CB Vorgang e.g. {@link de.augustakom.hurrican.model.cc.tal.CBVorgang#TYP_ANBIETERWECHSEL}.
     *                         Set to {@code null} if no Typ should be preselected.
     * @return
     * @throws ServiceNotFoundException
     * @throws FindException
     */
    public static CreateElTALVorgangWizard createWitaWizardWithPreselectedVaId(String vorabstimmungsId, Long cbVorgangTyp) throws ServiceNotFoundException {
        WbciWitaServiceFacade wbciWitaServiceFacade = CCServiceFinder.instance().getCCService(WbciWitaServiceFacade.class);
        WbciWitaOrderDataVO vo = wbciWitaServiceFacade.generateWitaDataForPreAggreement(vorabstimmungsId);

        CreateElTALVorgangWizard wizard = new CreateElTALVorgangWizard(vo.getCbId(), vo.getCarrierId(), vo.getEndstelle(), vo.getAuftragDaten(),
                cbVorgangTyp, Boolean.FALSE, Boolean.TRUE, vorabstimmungsId);

        wizard.setSelectCbVorgangPanel(new WitaSelectCBVorgangWizardPanel(wizard.getWizardComponents()));

        wizard.createGUI();
        return wizard;
    }

    /**
     * Konstruktor fuer den Wizard.
     *
     * @param cbId            ID der Carrierbestellung zu der ein Vorgang ausgeloest werden soll
     * @param carrierId       ID des Carriers, fuer den der Vorgang ausgeloest werden soll
     * @param endstelle
     * @param auftragDaten    AuftragDaten-Objekt, zu dem der TAL-Vorgang erstellt wird
     * @param cbVorgangTyp    (optional) Angabe des Vorgangs-Typs, der voreingestellt werden soll
     * @param hideWitaWizard  (optional) Wenn {@link Boolean#TRUE}, blendet die WITA Wizard aus
     * @param hideWbciWizard  (optional) Wenn {@link Boolean#TRUE}, blendet die WBCI Wizard aus
     * @param preselectedVaId (optional) Angabe der WBCI VorabstimmungsId, der voreingestellt werden soll
     */
    private CreateElTALVorgangWizard(Long cbId, Long carrierId, Endstelle endstelle, AuftragDaten auftragDaten,
            Long cbVorgangTyp, Boolean hideWitaWizard, Boolean hideWbciWizard, String preselectedVaId) {
        super(null);
        getWizardComponents().addWizardObject(WIZARD_OBJECT_CB_ID, cbId);
        getWizardComponents().addWizardObject(WIZARD_OBJECT_CARRIER_ID, carrierId);
        getWizardComponents().addWizardObject(WIZARD_OBJECT_HVT_STANDORT_ID, endstelle.getHvtIdStandort());
        getWizardComponents().addWizardObject(WIZARD_OBJECT_ENDSTELLE, endstelle);
        getWizardComponents().addWizardObject(WIZARD_OBJECT_AUFTRAG_DATEN, auftragDaten);
        getWizardComponents().addWizardObject(WIZARD_OBJECT_CBVORGANG_TYP, cbVorgangTyp);
        getWizardComponents().addWizardObject(WIZARD_OBJECT_HIDE_WITA_WIZARD, hideWitaWizard);
        getWizardComponents().addWizardObject(WIZARD_OBJECT_HIDE_WBCI_WIZARD, hideWbciWizard);
        getWizardComponents().addWizardObject(WIZARD_OBJECT_WBCI_VORABSTIMMUNGSID, preselectedVaId);
    }

    @Override
    protected void createGUI() {
        setTitle("el. Vorgang");
        getWizardComponents().addWizardPanel(selectCbVorgangPanel);
        getWizardComponents().getFinishButton().setText("Absenden");
    }

    public void setSelectCbVorgangPanel(AbstractServiceWizardPanel selectCbVorgangPanel) {
        this.selectCbVorgangPanel = selectCbVorgangPanel;
    }

}
