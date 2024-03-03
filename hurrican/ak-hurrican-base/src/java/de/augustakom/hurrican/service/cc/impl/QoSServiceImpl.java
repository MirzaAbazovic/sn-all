/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.02.2008 13:48:55
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragQoS;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.TechLeistung.ExterneLeistung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.QoSService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.utils.HistoryHelper;


/**
 * Service-Implementierung von <code>QoSService</code>.
 *
 *
 */
@CcTxRequired
public class QoSServiceImpl extends DefaultCCService implements QoSService {

    private static final Logger LOGGER = Logger.getLogger(QoSServiceImpl.class);

    @Override
    public List<AuftragQoS> addDefaultQoS2Auftrag(Long auftragId, Date gueltigVon, Long sessionId)
            throws StoreException {
        try {
            // pruefen, ob bereits QoS-Eintraege existieren
            List<AuftragQoS> existing = findQoS4Auftrag(auftragId, true);
            if (CollectionTools.isNotEmpty(existing)) {
                throw new StoreException("Auftrag besitzt bereits QoS-Einträge. " +
                        "Defaults können nicht zugeordnet werden!");
            }

            // QoS-Klassen mit Default-Werten aus Reference-Liste laden
            ReferenceService refSrv = getCCService(ReferenceService.class);
            List<Reference> qosClasses = refSrv.findReferencesByType(Reference.REF_TYPE_QOS_CLASS, Boolean.TRUE);
            if (CollectionTools.isNotEmpty(qosClasses)) {
                List<AuftragQoS> retVal = new ArrayList<AuftragQoS>();
                Date from = (gueltigVon != null) ? gueltigVon : new Date();
                for (Reference ref : qosClasses) {
                    AuftragQoS qos = new AuftragQoS();
                    qos.setAuftragId(auftragId);
                    qos.setQosClassRefId(ref.getId());
                    qos.setPercentage(ref.getIntValue());
                    qos.setGueltigVon(from);

                    saveAuftragQoS(qos, sessionId);
                    retVal.add(qos);
                }
                return retVal;
            }
            else {
                throw new StoreException("Es konnten keine Default-Werte für QoS ermittelt werden!");
            }
        }
        catch (StoreException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AuftragQoS saveAuftragQoS(AuftragQoS toSave, Long sessionId) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        if ((toSave.getQosClassRefId() == null) || (toSave.getPercentage() == null) || (toSave.getAuftragId() == null)) {
            throw new StoreException("QoS-Objekt besitzt nicht alle notwendigen Daten!");
        }

        try {
            HistoryHelper.checkHistoryDates(toSave);

            toSave.setUserW(getLoginNameSilent(sessionId));

            AuftragQoS qos = null;
            if ((toSave.getId() != null) && !toSave.isHistorised()) {
                // bestehenden Datensatz historisieren
                Date now = new Date();
                AuftragQoS toSaveOldVersion = ((FindDAO) getDAO()).findById(toSave.getId(), AuftragQoS.class);
                toSaveOldVersion.setGueltigBis(now);
                ((StoreDAO) getDAO()).store(toSaveOldVersion);

                qos = new AuftragQoS();
                PropertyUtils.copyProperties(qos, toSave);
                HistoryHelper.setHistoryData(qos, now);
            }
            else {
                if (toSave.getId() != null) { // Objekt in die Session laden, sonst kracht es weiter unten
                    qos = ((FindDAO) getDAO()).findById(toSave.getId(), AuftragQoS.class);
                    PropertyUtils.copyProperties(qos, toSave);
                }
                else {
                    qos = toSave;
                }
            }

            // pruefen, ob QoS-Daten gueltig sind
            if (DateTools.isDateEqual(qos.getGueltigBis(), DateTools.getHurricanEndDate())) {
                // pruefen, ob der Auftrag noch eine aktive QoS-Leistung hat
                CCLeistungsService ls = getCCService(CCLeistungsService.class);
                boolean hasQoS = ls.hasTechLeistungWithExtLeistungNo(ExterneLeistung.QOS.leistungNo,
                        toSave.getAuftragId(), true);
                if (!hasQoS) {
                    throw new StoreException("Der Auftrag besitzt keine Quality-of-Service Leistung!");
                }

                // es darf kein weiterer QoS-Eintrag zur gleichen Klasse bestehen
                AuftragQoS example = new AuftragQoS();
                example.setAuftragId(qos.getAuftragId());
                example.setQosClassRefId(qos.getQosClassRefId());
                example.setGueltigBis(DateTools.getHurricanEndDate());
                List<AuftragQoS> existing = ((ByExampleDAO) getDAO()).queryByExample(example, AuftragQoS.class);
                // Update eines bestehenden QoS-Eintrags - kein Fehler, sonst schon:
                if (CollectionTools.isNotEmpty(existing)
                        && (existing.size() == 1)
                        && existing.get(0).getId().equals(qos.getId())) {
                    throw new StoreException("Der Auftrag besitzt noch eine gültige "
                            + "QoS-Konfiguration zur gleichen QoS-Klasse.\n"
                            + "Zuordnung kann nicht durchgeführt werden.");
                }
            }

            // Prozentwert aller QoS-Eintraege zum Auftrag (inkl. diesem) uebersteigt nicht 100%
            List<AuftragQoS> existing = findQoS4Auftrag(toSave.getAuftragId(), true);
            int percentage = (DateTools.isDateEqual(qos.getGueltigBis(), DateTools.getHurricanEndDate()))
                    ? qos.getPercentage().intValue() : 0;
            for (AuftragQoS existingQoS : existing) {
                percentage += existingQoS.getPercentage().intValue();
            }

            if (percentage > 100) {
                throw new StoreException("Die Gesamtsumme der Prozentwerte übersteigt 100%.\n" +
                        "Konfiguration nicht möglich!");
            }

            ((StoreDAO) getDAO()).store(qos);
            return qos;
        }
        catch (StoreException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragQoS> findQoS4Auftrag(Long auftragId, boolean onlyAct) throws FindException {
        try {
            AuftragQoS example = new AuftragQoS();
            example.setAuftragId(auftragId);
            if (onlyAct) {
                example.setGueltigBis(DateTools.getHurricanEndDate());
            }

            return ((ByExampleDAO) getDAO()).queryByExample(
                    example, AuftragQoS.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

}


