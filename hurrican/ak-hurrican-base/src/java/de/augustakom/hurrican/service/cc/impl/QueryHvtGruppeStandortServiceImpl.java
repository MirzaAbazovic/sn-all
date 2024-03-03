/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2015
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.query.HVTQuery;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.QueryHvtGruppeStandorService;

@CcTxRequired
public class QueryHvtGruppeStandortServiceImpl extends DefaultCCService implements QueryHvtGruppeStandorService {

    private static final Logger LOGGER = Logger.getLogger(QueryHvtGruppeStandortServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;


    @Override
    public <T> List<T> findAll(Class<T> clazz) throws Exception {
        if (HVTGruppeStdView.class.isAssignableFrom(clazz)) {
            return (List<T>) hvtService.findHVTViews();
        }
        return Collections.emptyList();
    }

    @Override
    public <T> List<T> findByExample(Object example, Class<T> clazz) throws Exception {
        if (HVTGruppeStdView.class.isAssignableFrom(clazz)) {
            HVTGruppeStdView view = (HVTGruppeStdView)example;
            HVTQuery query = new HVTQuery();
            query.setHvtIdStandort(view.getHvtIdStandort());
            return (List<T>) hvtService.findHVTViews(query);
        }
        return Collections.emptyList();
    }

    @Override
    public <T> T findById(Serializable id, Class<T> clazz) throws Exception {
        if (HVTGruppeStdView.class.isAssignableFrom(clazz) && id instanceof Long) {
            HVTQuery query = new HVTQuery();
            query.setHvtIdStandort((Long)id);
            List<HVTGruppeStdView> result = hvtService.findHVTViews(query);
            if (result.size() == 1) {
                return (T) result.get(0);
            }
        }
        return null;
    }
}
