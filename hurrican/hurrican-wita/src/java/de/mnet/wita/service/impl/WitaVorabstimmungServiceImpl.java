/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2011 08:41:43
 */
package de.mnet.wita.service.impl;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Iterables;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.wita.dao.VorabstimmungAbgebendDao;
import de.mnet.wita.dao.VorabstimmungDao;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.VorabstimmungAbgebend;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaVorabstimmungService;

@CcTxRequired
public class WitaVorabstimmungServiceImpl implements WitaVorabstimmungService {

    @Resource(name = "de.mnet.wita.dao.VorabstimmungDao")
    private VorabstimmungDao vorabstimmungDao;

    @Resource(name = "de.mnet.wita.dao.VorabstimmungAbgebendDao")
    private VorabstimmungAbgebendDao vorabstimmungAbgebendDao;

    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierElTALService")
    private CarrierElTALService carrierElTALService;

    @Override
    public Vorabstimmung saveVorabstimmung(Vorabstimmung vorabstimmung) {
        vorabstimmungDao.store(vorabstimmung);
        return vorabstimmung;
    }

    @Override
    public void deleteVorabstimmung(Vorabstimmung vorabstimmung) {
        vorabstimmungDao.deleteVorabstimmung(vorabstimmung);
    }

    @Override
    public VorabstimmungAbgebend saveVorabstimmungAbgebend(VorabstimmungAbgebend vorabstimmungAbgebend) {
        vorabstimmungAbgebendDao.store(vorabstimmungAbgebend);
        return vorabstimmungAbgebend;
    }

    @Override
    public void deleteVorabstimmungAbgebend(VorabstimmungAbgebend vorabstimmungAbgebend) {
        vorabstimmungAbgebendDao.deleteVorabstimmungAbgebend(vorabstimmungAbgebend);
    }

    @Override
    public VorabstimmungAbgebend findVorabstimmungAbgebend(String endstelle, Long auftragId) {
        return vorabstimmungAbgebendDao.findVorabstimmung(endstelle, auftragId);
    }

    @Override
    public Vorabstimmung findVorabstimmung(Endstelle endstelle, AuftragDaten auftragDaten) {
        return vorabstimmungDao.findVorabstimmung(endstelle.getEndstelleTyp(), auftragDaten.getAuftragId());
    }

    @Override
    public Vorabstimmung findVorabstimmung(Endstelle endstelle, Long auftragId) {
        return vorabstimmungDao.findVorabstimmung(endstelle.getEndstelleTyp(), auftragId);
    }

    @Override
    public List<Vorabstimmung> findVorabstimmungForAuftragsKlammer(Long auftragsKlammer, Endstelle endstelle) {
        if (auftragsKlammer == null) {
            return null;
        }

        WitaCBVorgang example = WitaCBVorgang.createCompletelyEmptyInstance();
        example.setAuftragsKlammer(auftragsKlammer);

        try {
            List<WitaCBVorgang> witaCbVorgaenge = carrierElTALService.findCBVorgaengeByExample(example);
            if (CollectionTools.isNotEmpty(witaCbVorgaenge)) {
                List<Vorabstimmung> result = new ArrayList<>();
                for (WitaCBVorgang witaCbVorgang : witaCbVorgaenge) {
                    CollectionTools.addIfNotNull(result, findVorabstimmung(endstelle, witaCbVorgang.getAuftragId()));
                }

                return result;
            }
        }
        catch (Exception e) {
            throw new WitaBaseException("Fehler bei der Ermittlung der Vorabstimmungs-Objekte zur Auftragsklammer: "
                    + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public Vorabstimmung findVorabstimmungForRexMk(Long auftragId) {
        List<Vorabstimmung> vorabstimmungen = vorabstimmungDao.findVorabstimmungen(auftragId);
        if (vorabstimmungen.size() > 1) {
            throw new WitaBaseException("Mehrere Vorabstimmungen fuer REX-MK mit auftragsId " + auftragId + " gefunden");
        }
        return Iterables.getOnlyElement(vorabstimmungen, null);
    }

}
