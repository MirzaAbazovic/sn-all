/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 06.02.2017

 */

package de.mnet.wbci.dao.impl;


import static de.mnet.wbci.helper.AnswerDeadlineCalculationHelper.*;
import static de.mnet.wbci.model.Technologie.*;

import java.util.*;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.PreAgreementType;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * A ResultTransformer which creates {@link PreAgreementVO} objects.
 * <p>
 * Created by zieglerch on 06.02.2017.
 */
class PreAgreementVOTransformer implements ResultTransformer {

    private static final long serialVersionUID = 1L;

    @Autowired
    @Qualifier("de.mnet.wbci.dao.WbciMapper")
    private WbciMapper wbciMapper;

    /**
     * Create an object out of each row data.
     */
    @Override
    public Object transformTuple(Object[] rowData, String[] aliasNames) {
        int index = 0;
        // @formatter:off
        String vaId =                       wbciMapper.mapObject(rowData[index++], String.class);
        Long auftragId =                    wbciMapper.mapObject(rowData[index++], Long.class);
        Long auftragNoOrig =                wbciMapper.mapObject(rowData[index++], Long.class);
        KundenTyp kundenTyp =               wbciMapper.mapObject(rowData[index++], KundenTyp.class);
        GeschaeftsfallTyp gfType =          wbciMapper.mapObject(rowData[index++], GeschaeftsfallTyp.class);
        RequestTyp aenderungskz =           wbciMapper.mapObject(rowData[index++], RequestTyp.class);
        CarrierCode ekpAbg =                wbciMapper.mapObject(rowData[index++], CarrierCode.class);
        CarrierCode ekpAuf =                wbciMapper.mapObject(rowData[index++], CarrierCode.class);
        Date vorgabeDatum =                 wbciMapper.mapObject(rowData[index++], Date.class);
        Date wechseltermin =                wbciMapper.mapObject(rowData[index++], Date.class);
        WbciGeschaeftsfallStatus gfStatus = wbciMapper.mapObject(rowData[index++], WbciGeschaeftsfallStatus.class);
        WbciRequestStatus status =          wbciMapper.mapObject(rowData[index++], WbciRequestStatus.class);
        Date processedAt =                  wbciMapper.mapObject(rowData[index++], Date.class);
        MeldungTyp rueckmeldung =           wbciMapper.mapObject(rowData[index++], MeldungTyp.class);
        Date rueckmeldeDatum =              wbciMapper.mapObject(rowData[index++], Date.class);
        String medlungCodes =               wbciMapper.mapObject(rowData[index++], String.class);
        Long requestId =                    wbciMapper.mapObject(rowData[index++], Long.class);
        Long userId =                       wbciMapper.mapObject(rowData[index++], Long.class);
        String userName =                   wbciMapper.mapObject(rowData[index++], String.class);
        Long teamId =                       wbciMapper.mapObject(rowData[index++], Long.class);
        Long currentUserId =                wbciMapper.mapObject(rowData[index++], Long.class);
        String currentUserName =            wbciMapper.mapObject(rowData[index++], String.class);
        Technologie mnetTechnologie =       wbciMapper.mapObject(rowData[index++], Technologie.class);
        Boolean klaerfall =                 wbciMapper.mapObject(rowData[index++], Boolean.class);
        Boolean automatable =               wbciMapper.mapObject(rowData[index++], Boolean.class);
        String internalStatus =             wbciMapper.mapObject(rowData[index++], String.class);
        Date answerDeadline =               wbciMapper.mapObject(rowData[index++], Date.class);
        Boolean isMnetDeadline =            wbciMapper.mapObject(rowData[index++], Boolean.class);
        String niederlassung =              wbciMapper.mapObject(rowData[index++], String.class);
        Long numberOfAutomationErrors =     wbciMapper.mapObject(rowData[index], Long.class);
        // @formatter:on

        PreAgreementVO pa = new PreAgreementVO();
        pa.setVaid(vaId);
        pa.setAuftragId(auftragId);
        pa.setAuftragNoOrig(auftragNoOrig);
        pa.setGfType(gfType);
        pa.setAenderungskz(aenderungskz);
        pa.setEkpAbg(ekpAbg);
        pa.setEkpAuf(ekpAuf);
        pa.setVorgabeDatum(vorgabeDatum);
        pa.setWechseltermin(wechseltermin);
        pa.setRequestStatus(status);
        pa.setGeschaeftsfallStatus(gfStatus);
        pa.setProcessedAt(processedAt);
        pa.setRueckmeldung(rueckmeldung);
        pa.setRueckmeldeDatum(rueckmeldeDatum);
        pa.setMeldungCodes(medlungCodes);
        pa.setWbciRequestId(requestId);
        pa.setUserId(userId);
        pa.setUserName(userName);
        pa.setTeamId(teamId);
        pa.setCurrentUserId(currentUserId);
        pa.setCurrentUserName(currentUserName);
        pa.setNiederlassung(niederlassung);
        pa.setMnetTechnologie(mnetTechnologie);
        pa.setPreAgreementType(mapPreAgreementType(kundenTyp, mnetTechnologie, ekpAbg, ekpAuf));
        pa.setKlaerfall(klaerfall);
        pa.setAutomatable(automatable);
        pa.setInternalStatus(internalStatus);
        pa.setDaysUntilDeadlineMnet(calculateDaysUntilDeadlineMnet(answerDeadline, isMnetDeadline));
        pa.setDaysUntilDeadlinePartner(calculateDaysUntilDeadlinePartner(answerDeadline, isMnetDeadline));
        pa.setAutomationErrors(numberOfAutomationErrors != null && numberOfAutomationErrors > 0);

        return pa;
    }

    private PreAgreementType mapPreAgreementType(KundenTyp kundenTyp, Technologie mnetTechnologie,
            CarrierCode ekpAbg, CarrierCode ekpAuf) {
        if (isWholesaleProviderwechsel(kundenTyp, mnetTechnologie, ekpAbg, ekpAuf)) {
            return PreAgreementType.WS;
        }
        else {
            return kundenTyp != null ? PreAgreementType.valueOf(kundenTyp.name()) : null;
        }
    }

    private boolean isWholesaleProviderwechsel(KundenTyp kundenTyp, Technologie mnetTechnologie,
            CarrierCode ekpAbg, CarrierCode ekpAuf) {
        return KundenTyp.PK.equals(kundenTyp)
                && (FTTB.equals(mnetTechnologie) || FTTH.equals(mnetTechnologie))
                && isWholesaleEkp(ekpAbg, ekpAuf);
    }

    private boolean isWholesaleEkp(CarrierCode ekpAbg, CarrierCode ekpAuf) {
        return ekpAuf.isMnet() ? ekpAbg.isWholesaleCarrier() : ekpAuf.isWholesaleCarrier();
    }

    /**
     * Final result list transformation.
     */
    @Override
    public List transformList(List paramList) {
        return paramList;
    }
}
