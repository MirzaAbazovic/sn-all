/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2005 13:15:26
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.io.*;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.dao.cc.PhysikTypDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;


/**
 * Command-Klasse fuer die Ueberpruefung der zugeordneten Physik-Typen zu einem Auftrag.
 *
 *
 */
@CcTxRequired
public class PhysiktypConsistenceCheckCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(PhysiktypConsistenceCheckCommand.class);

    /**
     * Key fuer die prepare-Methode, um die Auftrags-ID zu uebergeben, mit der die Ueberpruefung gestartet werden soll.
     */
    public static final String START_AUFTRAG_ID = "start.auftrag.id";

    /**
     * Key fuer die prepare-Methode, um die Auftrags-ID zu uebergeben, bis zu der die Ueberpruefung durchgefuehrt werden
     * soll.
     */
    public static final String END_AUFTRAG_ID = "end.auftrag.id";

    private Long startAuftragId = null;
    private Long endAuftragId = null;
    private List<Produkt2PhysikTyp> prod2Physiktyp = null;
    private Map<Long, String> result = null;

    // DAOs
    private CCAuftragDAO ccAuftragDAO = null;
    private AuftragDatenDAO auftragDatenDAO = null;
    private PhysikTypDAO physikTypDAO = null;

    // sonstiges
    private ProduktPhysiktypPredicate prod2ptPredicate = null;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        checkValues();
        loadProduktPhysiktypen();

        result = new HashMap<>();
        prod2ptPredicate = new ProduktPhysiktypPredicate();

        long start = startAuftragId;
        long end = endAuftragId;
        for (long i = start; i <= end; i++) {
            checkPhysiktypConsistence(i);
        }

        return result;
    }

    /*
     * Ueberprueft, ob die Physiktypen des Auftrags korrekt sind.
     * Ist dies nicht der Fall, wird ein Objekt vom Typ <code>PhysikTypConsistence</code>
     * erzeugt und der Result-Liste zugeordnet.
     */
    private void checkPhysiktypConsistence(Long auftragId) {
        try {
            // Auftrag laden und Status pruefen
            AuftragDaten ad = getAuftragDatenDAO().findByAuftragId(auftragId);
            if ((ad == null) ||
                    NumberTools.isIn(ad.getStatusId(), new Number[] { AuftragStatus.STORNO, AuftragStatus.ABSAGE })
                    || (ad.getStatusId() >= AuftragStatus.KUENDIGUNG)) {
                return;
            }

            // zugeordnete Physiktypen laden
            List<Long> physiktypen = getPhysikTypDAO().findPhysiktypen4Auftrag(auftragId);
            if ((physiktypen != null) && (!physiktypen.isEmpty())) {
                prod2ptPredicate.setProduktId(ad.getProdId());
                @SuppressWarnings("unchecked")
                Collection<Produkt2PhysikTyp> possiblePTs = CollectionUtils.select(prod2Physiktyp, prod2ptPredicate);
                if ((possiblePTs == null) || (possiblePTs.isEmpty())) {
                    result.put(auftragId, "Moegliche Physiktypen konnten nicht ermittelt werden!");
                    return;
                }

                boolean physiktypOk = false;
                StringBuilder message = new StringBuilder("zugeordnete Physiktypen: ");
                for (Long x : physiktypen) {
                    message.append(x);
                    message.append("; ");
                }
                message.append(" - moegliche Physiktypen: ");
                for (Produkt2PhysikTyp p2pt : possiblePTs) {
                    message.append(p2pt.getPhysikTypId());
                    message.append("; ");
                }

                for (Long assignedPhysiktyp : physiktypen) {
                    for (Produkt2PhysikTyp p2pt : possiblePTs) {
                        if (NumberTools.equal(assignedPhysiktyp, p2pt.getPhysikTypId())) {
                            physiktypOk = true;
                            break;
                        }
                    }
                }

                if (!physiktypOk) {
                    result.put(auftragId, message.toString());
                }
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            result.put(auftragId, "Error bei Check: " + e.getMessage());
        }
    }

    /* Laedt die Zuordnungen zwischen Produkt und Physiktypen */
    private void loadProduktPhysiktypen() throws ServiceCommandException {
        try {
            prod2Physiktyp = getPhysikTypDAO().findAll(Produkt2PhysikTyp.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Die Produkt-Physiktyp Zuordnungen konnten nicht ermittelt werden!");
        }
    }

    /* Ueberprueft, ob alle benoetigten Daten vorhanden sind. */
    private void checkValues() throws ServiceCommandException {
        startAuftragId = (Long) getPreparedValue(START_AUFTRAG_ID);
        if (startAuftragId == null) {
            startAuftragId = 1L;
        }

        endAuftragId = (Long) getPreparedValue(END_AUFTRAG_ID);
        if (endAuftragId == null) {
            try {
                endAuftragId = getCcAuftragDAO().getMaxAuftragId();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        if (endAuftragId == null) {
            throw new HurricanServiceCommandException("Die hoechste Auftrags-ID konnte nicht ermittelt werden!");
        }
    }

    /**
     * @return Returns the ccAuftragDAO.
     */
    public CCAuftragDAO getCcAuftragDAO() {
        return ccAuftragDAO;
    }

    /**
     * @param ccAuftragDAO The ccAuftragDAO to set.
     */
    public void setCcAuftragDAO(CCAuftragDAO ccAuftragDAO) {
        this.ccAuftragDAO = ccAuftragDAO;
    }

    /**
     * @return Returns the physikTypDAO.
     */
    public PhysikTypDAO getPhysikTypDAO() {
        return physikTypDAO;
    }

    /**
     * @param physikTypDAO The physikTypDAO to set.
     */
    public void setPhysikTypDAO(PhysikTypDAO physikTypDAO) {
        this.physikTypDAO = physikTypDAO;
    }

    /**
     * @return Returns the auftragDatenDAO.
     */
    public AuftragDatenDAO getAuftragDatenDAO() {
        return auftragDatenDAO;
    }

    /**
     * @param auftragDatenDAO The auftragDatenDAO to set.
     */
    public void setAuftragDatenDAO(AuftragDatenDAO auftragDatenDAO) {
        this.auftragDatenDAO = auftragDatenDAO;
    }

    /**
     * Predicate, um nur die Produkt-Physiktyp Zuordnungen zu filtern, die zu einem best. Produkt gehoeren.
     *
     *
     */
    static class ProduktPhysiktypPredicate implements Predicate, Serializable {
        private static final long serialVersionUID = -7623525294865835829L;
        private Long prodId = null;

        /**
         * Uebergibt dem Predicate die Produkt-ID.
         *
         * @param prodId
         */
        protected void setProduktId(Long prodId) {
            this.prodId = prodId;
        }

        /**
         * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
         */
        @Override
        public boolean evaluate(Object obj) {
            return ((obj instanceof Produkt2PhysikTyp)
                    && NumberTools.equal(prodId, ((Produkt2PhysikTyp) obj).getProduktId()));
        }
    }
}


