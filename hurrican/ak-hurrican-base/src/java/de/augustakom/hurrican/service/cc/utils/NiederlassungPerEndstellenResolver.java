/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.10.2011 18:06:12
 */
package de.augustakom.hurrican.service.cc.utils;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.NiederlassungService;

/**
 * Sucht und findet fuer eine AuftragsId einen Standort (Niederlassung).
 *
 * TODO die Klasse sollte nach Moeglichkeit aufgeloest und die einzig relevante Methode 'findSite4Auftrag'
 *      in den NiederlassungService uebertragen werden!
 *
 *
 *
 * @since Release 10
 */
public class NiederlassungPerEndstellenResolver {
    /**
     * @return Returns the niederlassungService.
     * @throws ServiceNotFoundException
     */
    NiederlassungService getNiederlassungService() throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(NiederlassungService.class);
    }

    /**
     * @return Returns the endstellenService.
     * @throws ServiceNotFoundException
     */
    EndstellenService getEndstellenService() throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(EndstellenService.class);
    }

    /**
     * @return Returns the hvtService.
     * @throws ServiceNotFoundException
     */
    HVTService getHvtService() throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(HVTService.class);
    }

    protected HVTGruppe findHvtGruppeWithReferenceToNiederlassung(Long hvtStandortId) throws FindException,
            ServiceNotFoundException {
        if (hvtStandortId == null) {
            return null;
        }
        HVTGruppe hvtGruppe = getHvtService().findHVTGruppe4Standort(hvtStandortId);
        if ((hvtGruppe != null) && (hvtGruppe.getNiederlassungId() != null)) {
            return hvtGruppe;
        }
        return null;
    }

    protected Niederlassung findSite4HVTStandortId(Long hvtStandortId) throws ServiceNotFoundException,
            FindException {
        HVTGruppe hvtGruppe = findHvtGruppeWithReferenceToNiederlassung(hvtStandortId);
        if (hvtGruppe != null) {
            Niederlassung niederlassung = getNiederlassungService().findNiederlassung(hvtGruppe.getNiederlassungId());
            if (niederlassung != null) {
                return niederlassung;
            }
        }
        return null;
    }

    protected Niederlassung findSite4Endstellen(List<Endstelle> endstellen) throws ServiceNotFoundException,
            FindException {
        if (CollectionTools.isNotEmpty(endstellen)) {
            for (Endstelle endstelle : endstellen) {
                if (StringUtils.equals(endstelle.getEndstelleTyp(), Endstelle.ENDSTELLEN_TYP_B)) {
                    Niederlassung niederlassung = findSite4HVTStandortId(endstelle.getHvtIdStandort());
                    if (niederlassung != null) {
                        return niederlassung;
                    }
                }
            }
        }
        return null;
    }

    protected List<Endstelle> findEndstellen4Auftrag(Long auftragId) throws FindException, ServiceNotFoundException {
        return getEndstellenService().findEndstellen4Auftrag(auftragId);
    }

    /**
     * Sucht und findet fuer eine AuftragsId einen Standort (Niederlassung).
     *
     * @param auftragId
     * @return
     * @throws ServiceNotFoundException
     * @throws FindException
     */
    public Niederlassung findSite4Auftrag(Long auftragId) throws ServiceNotFoundException, FindException {
        if (auftragId == null) {
            return null;
        }
        List<Endstelle> endstellen = findEndstellen4Auftrag(auftragId);
        return findSite4Endstellen(endstellen);
    }

}

