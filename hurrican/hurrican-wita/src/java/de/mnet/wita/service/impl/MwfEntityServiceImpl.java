/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2011 11:19:25
 */
package de.mnet.wita.service.impl;

import java.time.*;
import java.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldungPv;
import de.mnet.wita.model.SendAllowed;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaConfigService;

/**
 * Service-Implementierung von {@code MwfEntityService}
 */
@CcTxRequired
@Service
public class MwfEntityServiceImpl implements MwfEntityService {

    private static final Logger LOGGER = Logger.getLogger(MwfEntityServiceImpl.class);

    @Autowired
    MwfEntityDao mwfEntityDao;

    @Autowired
    WitaConfigService witaConfigService;

    private static LocalDateTime extractSentDate(MwfEntity mwfEntity) {
        LocalDateTime dateTime = null;
        if (mwfEntity instanceof Meldung) {
            dateTime = Instant.ofEpochMilli(((Meldung<?>) mwfEntity).getVersandZeitstempel().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        else if (mwfEntity instanceof MnetWitaRequest && ((MnetWitaRequest) mwfEntity).getSentAt() != null) {
            dateTime = Instant.ofEpochMilli(((MnetWitaRequest) mwfEntity).getSentAt().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return dateTime;
    }

    @Override
    public <T extends MwfEntity> List<T> findMwfEntitiesByExample(T example) {
        if (example == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<T> result = (List<T>) mwfEntityDao.queryByExample(example, example.getClass());
        return result;
    }

    @Override
    public <T extends MwfEntity> List<T> findMwfEntitiesByProperty(Class<T> type, String property, Object value) {
        return mwfEntityDao.findByProperty(type, property, value);
    }

    @Override
    public Auftrag getAuftragOfCbVorgang(Long cbVorgangId) {
        return mwfEntityDao.getAuftragOfCbVorgang(cbVorgangId);
    }

    @Override
    public List<Storno> getStornosOfCbVorgang(Long cbVorgangId) {
        return mwfEntityDao.getStornosOfCbVorgang(cbVorgangId);
    }

    @Override
    public List<TerminVerschiebung> getTerminVerschiebungenOfCbVorgang(Long cbVorgangId) {
        return mwfEntityDao.getTerminverschiebungenOfCbVorgang(cbVorgangId);
    }

    @Override
    public AnkuendigungsMeldungPv getLastAkmPv(String vertragsNummer) {
        return mwfEntityDao.getLastAkmPv(vertragsNummer);
    }

    @Override
    public AuftragsBestaetigungsMeldungPv getLastAbmPv(String vertragsNummer) {
        return mwfEntityDao.getLastAbmPv(vertragsNummer);
    }

    @Override
    public VerzoegerungsMeldungPv getLastVzmPv(String vertragsNummer) {
        return mwfEntityDao.getLastVzmPv(vertragsNummer);
    }

    @Override
    public AbbruchMeldungPv getLastAbbmPv(String vertragsNummer) {
        return mwfEntityDao.getLastAbbmPv(vertragsNummer);
    }

    @Override
    public AuftragsBestaetigungsMeldung getLastAbm(String externeAuftragsnummer) {
        return mwfEntityDao.getLastAbm(externeAuftragsnummer);
    }

    @Override
    public List<Long> findUnsentRequestsForEveryGeschaeftsfall(Integer maxResultsPerGeschaeftsfall)
            throws FindException {
        Preconditions.checkNotNull(maxResultsPerGeschaeftsfall);

        List<Long> witaRequests = new ArrayList<>();
        for (GeschaeftsfallTyp implementedGeschaeftsfaell : GeschaeftsfallTyp.implementedValues()) {
            List<Long> tempWitaRequests = mwfEntityDao.findUnsentRequests(implementedGeschaeftsfaell, maxResultsPerGeschaeftsfall);
            if (tempWitaRequests.size() > maxResultsPerGeschaeftsfall) {
                // Vorsicht subList 'toIndex' ist exclusive!
                witaRequests.addAll(tempWitaRequests.subList(0, maxResultsPerGeschaeftsfall));
            }
            else {
                witaRequests.addAll(tempWitaRequests);
            }
        }
        return witaRequests;
    }

    @Override
    public <T extends MwfEntity> T store(T toStore) {
        mwfEntityDao.store(toStore);
        return toStore;
    }

    @Override
    public boolean isLastMeldungTam(Meldung<?> currentTam) {
        Preconditions.checkNotNull(currentTam);
        List<Meldung<?>> meldungen = mwfEntityDao.findAllMeldungen(currentTam.getExterneAuftragsnummer());
        List<MnetWitaRequest> requests = mwfEntityDao.findAllRequests(currentTam.getExterneAuftragsnummer());
        List<MwfEntity> combinedAndSorted = combineAndSortRequestsAndMessages(requests, meldungen);
        combinedAndSorted.remove(currentTam);

        MwfEntity lastEntity = Iterables.getFirst(combinedAndSorted, null);
        return lastEntity instanceof TerminAnforderungsMeldung;
    }

    @Override
    public String findVertragsnummerFor(String businessKey) {
        Set<String> result = Sets.newHashSet();
        List<Meldung<?>> meldungen = mwfEntityDao.findAllMeldungen(businessKey);
        for (Meldung<?> meldung : meldungen) {
            if (StringUtils.isNotBlank(meldung.getVertragsNummer())) {
                result.add(meldung.getVertragsNummer());
            }
        }
        return Iterables.getOnlyElement(result);
    }

    /**
     * Kombiniert die beiden Listen und sortiert die Eintraege nach dem Sende-Datum. <br> Die Sortierung erfolgt dabei
     * nach absteigendem Datum (NULL Werte im Datum werden ganz nach oben sortiert).
     */
    List<MwfEntity> combineAndSortRequestsAndMessages(List<MnetWitaRequest> requests, List<Meldung<?>> meldungen) {
        List<MwfEntity> combined = new ArrayList<>();
        CollectionTools.addAllIgnoreNull(combined, requests);
        CollectionTools.addAllIgnoreNull(combined, meldungen);

        Collections.sort(combined, new Comparator<MwfEntity>() {
            @Override
            public int compare(MwfEntity o1, MwfEntity o2) {
                LocalDateTime date1 = extractSentDate(o1);
                LocalDateTime date2 = extractSentDate(o2);

                // ChronoLocalDateTime.compareTo is not null save, so we have to fiddle a bit
                if (date2 == null && date1 != null) {
                    return 1;
                }
                else if (date1 == null && date2 != null) {
                    return -1;
                }
                else if (date2 == null && date1 == null) {
                    return 0;
                }
                return date2.compareTo(date1);
            }
        });

        return combined;
    }

    @Override
    public List<Anlage> findUnArchivedAnlagen() {
        return mwfEntityDao.findUnArchivedAnlagen();
    }

    @Override
    public List<Meldung<?>> findMeldungenToBeSentToBsi() {
        return mwfEntityDao.findMeldungenToBeSentToBsi();
    }

    @Override
    public List<MnetWitaRequest> findRequestsToBeSentToBsi() {
        return mwfEntityDao.findRequestsToBeSentToBsi();
    }

    @Override
    public List<MnetWitaRequest> findDelayedRequestsToBeSentToBsi(int maxCountOfRequests) {
        String temp = witaConfigService.getMinutesWhileRequestIsOnHold();
        // Faktor 2 um bei Verzoegerungen im WITA Versand keine unnoetigen Protokolleintraege zu erstellen
        int delayInMinutes = Integer.parseInt(temp) * 2;
        LocalDateTime maxCreationDate = LocalDateTime.now().minusMinutes(delayInMinutes);

        List<MnetWitaRequest> requestsWithoutDelayProtocol = mwfEntityDao.findDelayedRequestsToBeSentToBsi(maxCreationDate, maxCountOfRequests);
        Iterable<MnetWitaRequest> filtered = Iterables.filter(requestsWithoutDelayProtocol, new com.google.common.base.Predicate<MnetWitaRequest>() {
            @Override
            public boolean apply(MnetWitaRequest request) {
                SendAllowed sendAllowed = witaConfigService.checkSendAllowed(request);
                return SendAllowed.KWT_IN_ZUKUNFT.equals(sendAllowed);
            }
        });

        return Lists.newArrayList(filtered);
    }

    @Override
    public MnetWitaRequest findUnsentRequest(Long witaCbVorgangId) {
        return mwfEntityDao.findUnsentRequest(witaCbVorgangId);
    }

    @Override
    public <T extends Meldung<?>> boolean checkMeldungReceived(String extAuftragsnr, Class<T> clazz) {
        if (StringUtils.isBlank(extAuftragsnr)) {
            return false;
        }
        try {
            T meldung = clazz.newInstance();
            meldung.setExterneAuftragsnummer(extAuftragsnr);
            return CollectionUtils.isNotEmpty(mwfEntityDao.queryByExample(meldung, meldung.getClass()));
        }
        catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("error instantiating class %s by newInstance()", e);
        }
        return false;
    }

    @Override
    public List<Meldung<?>> findMeldungenForSmsVersand() {
        return mwfEntityDao.findMeldungenForSmsVersand();
    }

    @Override
    public List<Meldung<?>> findMeldungenForEmailVersand() {
        return mwfEntityDao.findMessagesForEmailing();
    }
}
