/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 06.02.2017

 */

package de.mnet.wbci.dao.impl;


import java.util.*;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.OverdueAbmPvVO;

/**
 * A ResultTransformer which creates {@link OverdueAbmPvVO} objects.
 *
 * Created by zieglerch on 06.02.2017.
 */
class OverdueAbmPvVOTransformer implements ResultTransformer {

    private static final long serialVersionUID = -6299303442734287367L;

    @Autowired
    @Qualifier("de.mnet.wbci.dao.WbciMapper")
    private WbciMapper wbciMapper;

    @Override
    public Object transformTuple(Object[] rowData, String[] aliases) {
        int index = 0;
        // @formatter:off
        String vaId =                       wbciMapper.mapObject(rowData[index++], String.class);
        CarrierCode ekpAuf =                wbciMapper.mapObject(rowData[index++], CarrierCode.class);
        CarrierCode ekpAbg =                wbciMapper.mapObject(rowData[index++], CarrierCode.class);
        Date wechseltermin =                wbciMapper.mapObject(rowData[index++], Date.class);
        Long auftragNoOrig =                wbciMapper.mapObject(rowData[index++], Long.class);
        Long auftragId =                    wbciMapper.mapObject(rowData[index++], Long.class);
        String vertragsNummer =             wbciMapper.mapObject(rowData[index++], String.class);
        boolean akmPvReceived =             wbciMapper.mapObject(rowData[index++], Boolean.class);
        boolean abbmPvReceived =            wbciMapper.mapObject(rowData[index], Boolean.class);
        // @formatter:on

        OverdueAbmPvVO vo = new OverdueAbmPvVO();
        vo.setVaid(vaId);
        vo.setEkpAuf(ekpAuf);
        vo.setEkpAbg(ekpAbg);
        vo.setWechseltermin(wechseltermin);
        vo.setAuftragNoOrig(auftragNoOrig);
        vo.setAuftragId(auftragId);
        vo.setVertragsnummer(vertragsNummer);
        vo.setAkmPvReceived(akmPvReceived);
        vo.setAbbmPvReceived(abbmPvReceived);

        return vo;
    }

    @Override
    public List transformList(List paramList) {
        return paramList;
    }
}
