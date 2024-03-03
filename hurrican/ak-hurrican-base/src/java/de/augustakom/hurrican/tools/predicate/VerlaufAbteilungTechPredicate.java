/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.01.2005 15:37:41
 */
package de.augustakom.hurrican.tools.predicate;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;


/**
 * Predicate, um aus einer Collection von Objekten des Typs <code>VerlaufAbteilung</code> die nicht-technischen
 * Abteilungen (AM, Dispo und NP) heraus zu filtern.
 *
 *
 */
public class VerlaufAbteilungTechPredicate implements Predicate {
    private static final Logger LOGGER = Logger.getLogger(VerlaufAbteilungTechPredicate.class);
    public static final List<Long> TECH_ABTEILUNGEN = ImmutableList.of(
            Abteilung.FIELD_SERVICE,
            Abteilung.ST_VOICE,
            Abteilung.ST_CONNECT,
            Abteilung.ST_ONLINE,
            Abteilung.EXTERN,
            Abteilung.MQUEUE,
            Abteilung.FFM,
            Abteilung.ZP_RESSOURCEN_DOKU,
            Abteilung.ZP_TECHNIKCENTER,
            Abteilung.ZP_TECHNOLOGIE,
            Abteilung.ZP_ZENTRALE_INFRASTRUKTUR,
            Abteilung.ZP_ZENTRALE_IP_SYSTEME
            );

    private final Map<Long, VerlaufAbteilung> verlaufAbteilungMap = new HashMap<>();
    private Long abteilungId = null;
    private Long niederlassungId = null;
    private Long parentVerlaufAbteilungId = null;

    private EvaluationStrategy strategy;


    /**
     * Filtert alle VerlaufAbteilungen heraus, die zur Systemtechnik oder zum FieldService gehoeren.
     */
    public VerlaufAbteilungTechPredicate() {
        this.strategy = new TrueForTechnikAbteilungen();
    }

    /**
     * Filtert alle VerlaufAbteilungen heraus, deren Parent VerlaufAbteilung der gegebenen VerlaufAbteilungs-ID
     * entspricht.
     */
    public VerlaufAbteilungTechPredicate(Long parentVerlaufAbteilungId) {
        if (parentVerlaufAbteilungId != null) {
            this.parentVerlaufAbteilungId = parentVerlaufAbteilungId;
            this.strategy = new UseParentVerlaufAbteilungId();
        }
        else {
            LOGGER.warn("VerlaufAbteilungTechPredicate() - parentVerlaufAbteilungId is null! Filtering for all technical departments.");
            this.strategy = new TrueForTechnikAbteilungen();
        }
    }

    /**
     * Filtert alle VerlaufAbteilungen heraus, die keinen Parent mit der gegebenen abteilung und niederlassung haben.
     */
    public VerlaufAbteilungTechPredicate(Collection<VerlaufAbteilung> verlaufAbteilung, Long abteilungId, Long niederlassungId) {
        this.abteilungId = abteilungId;
        this.niederlassungId = niederlassungId;
        boolean useParentMap = false;
        for (VerlaufAbteilung va : verlaufAbteilung) {
            if (va.getParentVerlaufAbteilungId() != null) {
                useParentMap = true;
            }
        }
        if (useParentMap) {
            for (VerlaufAbteilung va : verlaufAbteilung) {
                this.verlaufAbteilungMap.put(va.getId(), va);
            }
            if ((this.abteilungId == null) || (this.niederlassungId == null) || (this.verlaufAbteilungMap.isEmpty())) {
                useParentMap = false;
                LOGGER.error("VerlaufAbteilungTechPredicate() - abteilungId (" + abteilungId +
                        ") or niederlassungId (" + niederlassungId +
                        ") is null or not VerlaufAbteilungen given");
            }
        }
        if (useParentMap) {
            this.strategy = new UseParentMap();
        }
        else {
            this.strategy = new TrueForTechnikAbteilungen();
        }
    }


    /**
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    public boolean evaluate(Object value) {
        if (value instanceof VerlaufAbteilung) {
            return strategy.evaluate((VerlaufAbteilung) value);
        }
        return false;
    }


    private interface EvaluationStrategy {
        boolean evaluate(VerlaufAbteilung verlaufAbteilung);
    }

    private static class TrueForTechnikAbteilungen implements EvaluationStrategy {
        @Override
        public boolean evaluate(VerlaufAbteilung verlaufAbteilung) {
            return TECH_ABTEILUNGEN.contains(verlaufAbteilung.getAbteilungId());
        }
    }

    private class UseParentMap implements EvaluationStrategy {
        @Override
        public boolean evaluate(VerlaufAbteilung verlaufAbteilung) {
            VerlaufAbteilung parent = verlaufAbteilungMap.get(verlaufAbteilung.getParentVerlaufAbteilungId());
            while (parent != null) {
                if (abteilungId.equals(parent.getAbteilungId()) && niederlassungId.equals(parent.getNiederlassungId())) {
                    return true;
                }
                parent = verlaufAbteilungMap.get(parent.getParentVerlaufAbteilungId());
            }
            return false;
        }
    }

    private class UseParentVerlaufAbteilungId implements EvaluationStrategy {
        @Override
        public boolean evaluate(VerlaufAbteilung verlaufAbteilung) {
            return parentVerlaufAbteilungId.equals(verlaufAbteilung.getParentVerlaufAbteilungId());
        }
    }
}


