/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.10.2011 10:53:20
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.SperrklasseDAO;
import de.augustakom.hurrican.model.cc.dn.Sperrklasse;
import de.augustakom.hurrican.model.cc.dn.Sperrklasse.SperrklassenTypEnum;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.SperrklasseService;

/**
 * Implementiert {@link SperrklasseService}.
 *
 *
 * @since Release 10
 */
@CcTxRequired
public class SperrklasseServiceImpl extends DefaultCCService implements SperrklasseService {

    private static final Logger LOGGER = Logger.getLogger(SperrklasseServiceImpl.class);

    private List<Sperrklasse> findSperrklasseByExample(Sperrklasse example) {
        SperrklasseDAO dao = (SperrklasseDAO) getDAO();
        return dao.queryByExample(example, Sperrklasse.class);
    }

    public List<Sperrklasse> findSperrklasseByHwSwitchType(final HWSwitchType hwSwitchType) {
        Sperrklasse example = new Sperrklasse();
        List<Sperrklasse> sperrklassen = findSperrklasseByExample(example);
        List<Sperrklasse> filteredSk = Lists.newArrayList(Iterables.filter(sperrklassen,
                input -> input.getSperrklasseByHwSwitchType(hwSwitchType) != null));
        Collections.sort(filteredSk,
                (o1, o2) -> o1.getSperrklasseByHwSwitchType(hwSwitchType)
                        .compareTo(o2.getSperrklasseByHwSwitchType(hwSwitchType)));
        return filteredSk;
    }

    @Override
    public Sperrklasse findSperrklasseByExample(Sperrklasse example, HWSwitchType hwSwitchType) throws FindException {
        if (example == null) {
            return null;
        }
        try {
            List<Sperrklasse> sperrklassen = findSperrklasseByExample(example);
            if (CollectionTools.isNotEmpty(sperrklassen)) {
                for (Sperrklasse sperrklasse : sperrklassen) {
                    if(sperrklasse.getSperrklasseByHwSwitchType(hwSwitchType) != null) {
                        return sperrklasse;
                    }
                }
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Long> findPossibleSperrtypen(Sperrklasse example, HWSwitchType hwSwitchType) {
        List<Sperrklasse> sperrklassen = findSperrklasseByExample(example);
        List<Long> typen = new ArrayList<>();
        for (Sperrklasse sperrklasse : sperrklassen) {
            if (sperrklasse.getSperrklasseByHwSwitchType(hwSwitchType) != null) {
                typen.addAll(getSperrklassenTypen(sperrklasse));
            }
        }
        return typen;
    }

    List<Long> getSperrklassenTypen(@Nonnull Sperrklasse sperrklasse) {
        List<Long> typen = new ArrayList<>();
        typen.add((BooleanTools.nullToFalse(sperrklasse.getAbgehend())) ? SperrklassenTypEnum.ABGEHEND.getId() : null);
        typen.add((BooleanTools.nullToFalse(sperrklasse.getNational())) ? SperrklassenTypEnum.NATIONAL.getId() : null);
        typen.add((BooleanTools.nullToFalse(sperrklasse.getInnovativeDienste())) ? SperrklassenTypEnum.INNOVATIVE_DIENSTE
                .getId() : null);
        typen.add((BooleanTools.nullToFalse(sperrklasse.getMabez())) ? SperrklassenTypEnum.MABEZ.getId() : null);
        typen.add((BooleanTools.nullToFalse(sperrklasse.getMobil())) ? SperrklassenTypEnum.MOBIL.getId() : null);
        typen.add((BooleanTools.nullToFalse(sperrklasse.getVpn())) ? SperrklassenTypEnum.VPN.getId() : null);
        typen.add((BooleanTools.nullToFalse(sperrklasse.getPrd())) ? SperrklassenTypEnum.PRD.getId() : null);
        typen.add((BooleanTools.nullToFalse(sperrklasse.getAuskunftsdienste())) ? SperrklassenTypEnum.AUSKUNFTSDIENSTE
                .getId() : null);
        typen.add((BooleanTools.nullToFalse(sperrklasse.getInternational())) ? SperrklassenTypEnum.INTERNATIONAL.getId()
                : null);
        typen.add((BooleanTools.nullToFalse(sperrklasse.getOffline())) ? SperrklassenTypEnum.OFFLINE.getId() : null);
        typen.add((BooleanTools.nullToFalse(sperrklasse.getPremiumServicesInt())) ? SperrklassenTypEnum.PREMIUM_SERVICES_INT
                .getId() : null);
        return typen;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.SperrklasseService#findSperrklasseBySperrklasseNo(java.lang.Integer,
     * de.augustakom.hurrican.model.cc.hardware.HWSwitchType)
     */
    @Override
    public Sperrklasse findSperrklasseBySperrklasseNo(Integer sperrklasseNo, HWSwitchType hwSwitchType)
            throws FindException {

        SperrklasseDAO dao = (SperrklasseDAO) getDAO();
        if (HWSwitchType.isImsOrNsp(hwSwitchType)) {
            return dao.findImsSperrklasse(sperrklasseNo);
        }
        else {
            return dao.findDefaultSperrklasse(sperrklasseNo);
        }
    }

}
