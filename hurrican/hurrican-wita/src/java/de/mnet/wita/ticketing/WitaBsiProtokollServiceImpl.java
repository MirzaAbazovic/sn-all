/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.02.2012 17:05:29
 */
package de.mnet.wita.ticketing;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static de.augustakom.hurrican.service.location.CamelProxyLookupService.*;
import static de.mnet.wita.message.MeldungsType.*;
import static de.mnet.wita.message.common.BsiProtokollEintragSent.*;
import static de.mnet.wita.message.meldung.Meldung.*;
import static de.mnet.wita.model.UserTask.UserTaskStatus.*;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.mnet.common.customer.service.CustomerService;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.dao.TaskDao;
import de.mnet.wita.message.MessageWithSentToBsiFlag;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.common.BsiDelayProtokollEintragSent;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.ticketing.converter.AbstractMwfBsiDelayProtokollConverter;
import de.mnet.wita.ticketing.converter.AbstractMwfBsiProtokollConverter;

@CcTxRequired
public class WitaBsiProtokollServiceImpl implements WitaBsiProtokollService {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(WitaBsiProtokollServiceImpl.class);
    private final Map<Class<? extends MwfEntity>, AbstractMwfBsiProtokollConverter<?>> meldungsTyp2Converter = new HashMap<>();
    private final Map<Class<? extends MwfEntity>, AbstractMwfBsiDelayProtokollConverter<?>> meldungsTyp2DelayConverter = new HashMap<>();
    @Autowired
    protected List<AbstractMwfBsiProtokollConverter<? extends MwfEntity>> bsiProtokollConverters;
    @Autowired
    protected List<AbstractMwfBsiDelayProtokollConverter<? extends MwfEntity>> bsiDelayProtokollConverters;
    @Autowired
    private CamelProxyLookupService camelProxyLookupService;
    @Autowired
    private MwfEntityDao mwfEntityDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private BillingAuftragService billingAuftragService;
    @Autowired
    private CCAuftragService auftragService;

    @PostConstruct
    public void init() {
        for (AbstractMwfBsiProtokollConverter<?> converter : bsiProtokollConverters) {
            if (!(converter instanceof AbstractMwfBsiDelayProtokollConverter<?>)) {  // zusaetzlicher Check wg. Klassenhierarchie
                meldungsTyp2Converter.put(converter.getTypeToConvert(), converter);
            }
        }

        for (AbstractMwfBsiDelayProtokollConverter<?> delayConverter : bsiDelayProtokollConverters) {
            meldungsTyp2DelayConverter.put(delayConverter.getTypeToConvert(), delayConverter);
        }
    }

    @Override
    public <T extends MwfEntity & MessageWithSentToBsiFlag> void protokolliereNachricht(T nachricht) {
        try {
            if (nachricht.getSentToBsi() == null) {
                nachricht.setSentToBsi(NOT_SENT_TO_BSI);
            }

            AddCommunication bsiProtokollEintrag = createProtokollEintrag(nachricht);
            if (bsiProtokollEintrag != null) {
                CustomerService customerService = camelProxyLookupService.lookupCamelProxy(PROXY_CUSTOMER_SERVICE,
                        CustomerService.class);

                customerService.sendCustomerServiceProtocol(bsiProtokollEintrag);
                nachricht.setSentToBsi(SENT_TO_BSI);
            }
            else {
                nachricht.setSentToBsi(DONT_SEND_TO_BSI);
            }
            mwfEntityDao.store(nachricht);
        }
        catch (Exception e) {
            LOGGER.warn("Problem beim Senden des BSI-Protokolleintrags zur Nachricht " + nachricht.toString(), e);
            persistMeldungNotSent(nachricht);
        }
    }

    /**
     * @return null bedeutet keinen Protokolleintrag schreiben
     */
    private <T extends MwfEntity & MessageWithSentToBsiFlag> AddCommunication createProtokollEintrag(T nachricht) throws FindException {
        if (meldungsTyp2Converter.containsKey(nachricht.getClass())) {
            AbstractMwfBsiProtokollConverter<T> bsiProtokollConverter = (AbstractMwfBsiProtokollConverter<T>) meldungsTyp2Converter.get(nachricht.getClass());

            AddCommunication bsiProtokollEintrag = bsiProtokollConverter.convert(nachricht);
            addContractData(bsiProtokollConverter.findHurricanAuftragId(nachricht), bsiProtokollEintrag);

            return bsiProtokollEintrag;
        }
        return null;
    }

    @Override
    public <T extends MnetWitaRequest & MessageWithSentToBsiFlag> void protokolliereDelay(T witaRequest) throws StoreException {
        try {
            AddCommunication bsiProtokollEintrag = createDelayProtokollEintrag(witaRequest);
            if (bsiProtokollEintrag != null) {
                CustomerService customerService = camelProxyLookupService.lookupCamelProxy(PROXY_CUSTOMER_SERVICE,
                        CustomerService.class);

                customerService.sendCustomerServiceProtocol(bsiProtokollEintrag);
                witaRequest.setDelaySentToBsi(BsiDelayProtokollEintragSent.DELAY_SENT_TO_BSI);
            }
            else {
                witaRequest.setDelaySentToBsi(BsiDelayProtokollEintragSent.DONT_SEND_DELAY_TO_BSI);
            }
            mwfEntityDao.store(witaRequest);
        }
        catch (Exception e) {
            LOGGER.warn("Problem beim Senden des BSI-Protokolleintrags (Delay) fuer Request " + witaRequest.toString(), e);
            persistDelaySentCausedError(witaRequest);
        }
    }

    /**
     * @return null bedeutet keinen Protokolleintrag schreiben
     */
    private <T extends MnetWitaRequest & MessageWithSentToBsiFlag> AddCommunication createDelayProtokollEintrag(T witaRequest) throws FindException {
        if (meldungsTyp2DelayConverter.containsKey(witaRequest.getClass())) {
            AbstractMwfBsiDelayProtokollConverter<T> bsiDelayProtokollConverter = (AbstractMwfBsiDelayProtokollConverter<T>) meldungsTyp2DelayConverter.get(witaRequest.getClass());
            AddCommunication bsiProtokollEintrag = bsiDelayProtokollConverter.convert(witaRequest);
            addContractData(bsiDelayProtokollConverter.findHurricanAuftragId(witaRequest), bsiProtokollEintrag);

            return bsiProtokollEintrag;
        }
        return null;
    }

    /**
     * Adds contract data to BSI message according to given Hurrican auftrag.
     *
     * @param hurricanAuftragId
     * @param bsiProtokollEintrag
     * @throws FindException
     */
    public void addContractData(Long hurricanAuftragId, AddCommunication bsiProtokollEintrag)
            throws FindException {
        Preconditions.checkNotNull(hurricanAuftragId);
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(hurricanAuftragId);
        BAuftrag bAuftrag = billingAuftragService.findAuftrag(auftragDaten.getAuftragNoOrig());
        if (bAuftrag == null) {
            bAuftrag = billingAuftragService.findAuftragStornoByAuftragNoOrig(auftragDaten.getAuftragNoOrig());
        }

        if (bAuftrag.getAuftragNoOrig() != null) {
            AddCommunication.Context context = new AddCommunication.Context();
            context.setContractId(bAuftrag.getAuftragNoOrig().toString());
            bsiProtokollEintrag.setContext(context);
        }

        if (bAuftrag.getKundeNo() != null) {
            bsiProtokollEintrag.setCustomerId(bAuftrag.getKundeNo().toString());
        }
    }

    private <T extends MwfEntity & MessageWithSentToBsiFlag> void persistMeldungNotSent(T nachricht) {
        nachricht.setSentToBsi(nachricht.getSentToBsi().failedSend());
        mwfEntityDao.store(nachricht);
    }

    private void persistDelaySentCausedError(MnetWitaRequest witaRequest) {
        witaRequest.setDelaySentToBsi(BsiDelayProtokollEintragSent.ERROR_SENDING_DELAY_TO_BSI);
        mwfEntityDao.store(witaRequest);
    }

    @Override
    public void dontSentPvMeldungenIfAuftragIsNotSet() {
        List<Meldung<?>> meldungen = mwfEntityDao.findPvMeldungenNotToBeSendToBsi();
        if (CollectionUtils.isNotEmpty(meldungen)) {
            LOGGER.info("Found " + meldungen.size() + " Meldungen set in ERROR and try to resolve them.");

            Set<String> extAuftragsnummern = newHashSet(transform(meldungen, GET_EXTERNE_AUFTRAGSNUMMER));
            for (String extAuftragsnr : extAuftragsnummern) {
                if (hasAbbmPvOrEntmPvReceivedFor(extAuftragsnr) && checkUserTaskIsClosed(extAuftragsnr)) {
                    persistDontSendToBsiMeldungen(extAuftragsnr);
                }
            }
        }
    }

    private Boolean hasAbbmPvOrEntmPvReceivedFor(String extAuftragsnr) {
        List<Meldung<?>> meldungen = mwfEntityDao.findAllMeldungen(extAuftragsnr);
        for (Meldung<?> meldung : meldungen) {
            if ((ABBM_PV == meldung.getMeldungsTyp()) || (ENTM_PV == meldung.getMeldungsTyp())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkUserTaskIsClosed(String externeAuftragsnummer) {
        AkmPvUserTask userTask = taskDao.findAkmPvUserTask(externeAuftragsnummer);
        return (userTask == null) || (userTask.getStatus() == GESCHLOSSEN); // No Usertask is like a closed Usertask
    }

    private void persistDontSendToBsiMeldungen(String extAuftragnr) {
        List<Meldung<?>> meldungen = mwfEntityDao.findAllMeldungen(extAuftragnr);
        for (Meldung<?> meldung : meldungen) {
            if (ERROR_SEND_TO_BSI == meldung.getSentToBsi()) {
                meldung.setSentToBsi(DONT_SEND_TO_BSI);
                mwfEntityDao.store(meldung);

                LOGGER.info("Fixed Sent-to-BSI-Status for " + meldung.toString());
            }
        }
    }

}
