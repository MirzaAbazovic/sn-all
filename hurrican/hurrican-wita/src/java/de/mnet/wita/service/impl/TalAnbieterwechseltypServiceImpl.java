/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.08.2011 13:45:44
 */
package de.mnet.wita.service.impl;

import static de.augustakom.hurrican.model.cc.Carrier.*;

import javax.annotation.*;
import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wita.dao.AnbieterwechselConfigDao;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.model.AnbieterwechselConfig;
import de.mnet.wita.model.AnbieterwechselConfig.NeuProdukt;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.TalAnbieterwechseltypService;
import de.mnet.wita.service.WitaVorabstimmungService;
import de.mnet.wita.service.WitaWbciServiceFacade;

@CcTxRequired
public class TalAnbieterwechseltypServiceImpl implements TalAnbieterwechseltypService {

    private static final Logger LOGGER = Logger.getLogger(TalAnbieterwechseltypServiceImpl.class);

    @Autowired
    AnbieterwechselConfigDao anbieterwechselConfigDao;
    @Resource(name = "de.mnet.wita.service.impl.WitaDataService")
    WitaDataService witaDataService;
    @Resource(name = "de.mnet.wita.service.WitaVorabstimmungService")
    WitaVorabstimmungService witaVorabstimmungService;
    @Resource(name = "de.mnet.wita.service.WitaWbciServiceFacade")
    WitaWbciServiceFacade witaWbciServiceFacade;

    @Override
    public GeschaeftsfallTyp determineGeschaeftsfall(Carrierbestellung carrierbestellung, WitaCBVorgang witaCBVorgang)
            throws WitaBaseException {
        Long auftragIdNew = witaCBVorgang.getAuftragId();
        Preconditions.checkNotNull(auftragIdNew, "Keine Auftrag-Id angegeben.");
        Equipment equipment = getEquipment(carrierbestellung, auftragIdNew);

        if (witaCBVorgang.getVorabstimmungsId() != null) {
            return determineAnbieterwechseltyp(witaCBVorgang.getVorabstimmungsId(), equipment);
        }
        else {
            Vorabstimmung vorabstimmung = witaDataService.loadVorabstimmung(witaCBVorgang);
            Preconditions.checkNotNull(vorabstimmung,
                    "Konnte keine Provider-Daten für die Carrierbestellung ermitteln.");
            Preconditions.checkArgument(isProduktGruppeValid(vorabstimmung),
                    "Die gewählte Produktgruppe ist nicht zulässig.");

            // Interimsprozess fuer Anbieterwechsel (WITA-740)
            de.augustakom.hurrican.model.cc.Carrier zielcarrier = vorabstimmung.getCarrier();
            if (!BooleanTools.nullToFalse(zielcarrier.getHasWitaInterface())) {
                return GeschaeftsfallTyp.BEREITSTELLUNG;
            }

            // Determine Anbieterwechseltyp from config
            return checkAnbieterwechseltyp(determineAnbieterwechseltyp(vorabstimmung, equipment));
        }
    }

    @Override
    public GeschaeftsfallTyp determineAnbieterwechseltyp(String wbciVorabstimmungsId, Equipment equipment) {
        GeschaeftsfallTyp anbieterwechseltyp;
        WbciGeschaeftsfall wbciGeschaeftsfall = witaWbciServiceFacade.getWbciGeschaeftsfall(wbciVorabstimmungsId);
            Preconditions.checkNotNull(wbciGeschaeftsfall,
                String.format("WBCI Geschäftsfall zu '%s' konnte nicht ermittelt werden!", wbciVorabstimmungsId));
            if (CarrierCode.DTAG.equals(wbciGeschaeftsfall.getAbgebenderEKP())) {
                // DTAG -> VBL
                return GeschaeftsfallTyp.VERBUNDLEISTUNG;
            }
            else {
                UebernahmeRessourceMeldung akmTr = witaWbciServiceFacade.getLastAkmTr(wbciVorabstimmungsId);
                Preconditions.checkNotNull(akmTr,
                    String.format("UebernahmeRessource Meldung zu '%s' konnte nicht ermittelt werden!", wbciVorabstimmungsId));

                if (akmTr.isUebernahme()) {
                    RueckmeldungVorabstimmung ruemVa = witaWbciServiceFacade.getRuemVa(wbciVorabstimmungsId);
                    Preconditions.checkNotNull(ruemVa,
                        String.format("Rueckmeldung Vorabstimmung zu '%s' konnte nicht ermittelt werden!", wbciVorabstimmungsId));

                    Technologie istTechnologie = ruemVa.getTechnologie();
                    NeuProdukt neuProdukt = getNeuProdukt(equipment);
                    if (!neuProdukt.equals(NeuProdukt.TAL)) {
                        throw new WitaBaseException(
                                String.format("Ermittlung des WITA Geschaeftsfalltyps zu '%s' nicht möglich, " +
                                                "da Zieltechnologie ungleich TAL!",
                                    wbciVorabstimmungsId
                                )
                        );
                    }

                    anbieterwechseltyp = determineAnbieterwechseltyp(Carrier.OTHER, istTechnologie.getProduktGruppe(), neuProdukt);
                }
                else {
                    throw new WitaBaseException(String.format("Ermittlung des WITA Geschaeftsfalltyps zu '%s' nicht möglich, " +
                        "da in der WBCI AKM-TR keine Übernahme der technischen Resource angegeben ist!", wbciVorabstimmungsId));
                }
            }
        return checkAnbieterwechseltyp(anbieterwechseltyp);
    }

    private GeschaeftsfallTyp checkAnbieterwechseltyp(GeschaeftsfallTyp anbieterwechseltyp) throws WitaBaseException {
        if (anbieterwechseltyp == GeschaeftsfallTyp.BEREITSTELLUNG) {
            throw new WitaBaseException(
                    "Dieser Geschaeftsfall wird z.Z. nicht automatisch unterstützt. Der abgebende Provider muss den Anschluss kündigen, anschliessend muss eine Neubestellung ausgelöst werden.");
        }
        if (anbieterwechseltyp == GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG) {
            throw new WitaBaseException(
                    "Bitte Rex-Mk Vorgang über eigenen Button im Carrierbestellungs-Panel auslösen.");
        }
        return anbieterwechseltyp;
    }

    boolean isProduktGruppeValid(Vorabstimmung vorabstimmung) {
        if (vorabstimmung.isCarrierDtag()) {
            return vorabstimmung.getProduktGruppe() == ProduktGruppe.DTAG_ANY;
        }
        return vorabstimmung.getProduktGruppe() != ProduktGruppe.DTAG_ANY;
    }

    @Override
    public GeschaeftsfallTyp determineAnbieterwechseltyp(Vorabstimmung vorabstimmung, Equipment equipment) {
        return determineAnbieterwechseltyp(getCarrier(vorabstimmung), vorabstimmung.getProduktGruppe(), getNeuProdukt(equipment));
    }

    private GeschaeftsfallTyp determineAnbieterwechseltyp(Carrier carrier, ProduktGruppe produktGruppe, NeuProdukt neuProdukt) {
        AnbieterwechselConfig config = anbieterwechselConfigDao.findConfig(carrier, produktGruppe, neuProdukt);
        return config != null ? config.getGeschaeftsfallTyp() : null;
    }

    private Carrier getCarrier(Vorabstimmung vorabstimmung) {
        return (vorabstimmung.isCarrierDtag()) ? Carrier.DTAG : Carrier.OTHER;
    }

    private NeuProdukt getNeuProdukt(Equipment equipment) {
        if (CARRIER_DTAG.equals(equipment.getCarrier())) {
            return NeuProdukt.TAL;
        }
        return NeuProdukt.KEINE_DTAG_LEITUNG;
    }

    @Override
    public Equipment getEquipment(Carrierbestellung carrierbestellung, Long auftragIdNew) {
        Pair<Equipment, Equipment> equipments;
        try {
            equipments = witaDataService.loadEquipments(carrierbestellung, auftragIdNew);
            if ((equipments.getSecond() == null)) {
                throw new WitaBaseException(
                        "Fuer die Ermittlung des Anbieterwechselgeschaeftsfall muss der neue Port angegeben werden!");
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaBaseException("Fehler bei der Ermittlung des Anbieterwechselgeschaeftsfalls: "
                    + e.getMessage(), e);
        }
        return equipments.getSecond();
    }

}
