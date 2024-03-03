/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2007 11:52:57
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.text.*;
import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Lager;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.innenauftrag.IA;
import de.augustakom.hurrican.model.cc.innenauftrag.IABudget;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahme;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahmeArtikel;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.RangierungAdminService;


/**
 * Jasper-DS fuer den Materialentnahme-Report.
 *
 *
 */
public class IAMaterialEntnahmeJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(IAMaterialEntnahmeJasperDS.class);

    private Long matEntId = null;

    private Lager lager = null;
    private IAMaterialEntnahme matEntnahme = null;
    private IA ia = null;
    private IABudget budget = null;
    private Iterator<IAMaterialEntnahmeArtikel> dataIterator = null;

    private IAMaterialEntnahmeArtikel currentData = null;
    private HVTGruppe hvtGruppe = null;
    private String budgetPerson = null;
    private int pos = 0;

    /**
     * Konstruktor mit Angabe der Materialentnahme-ID
     *
     * @param matEntId ID der zu druckenden Materialentnahme.
     * @throws AKReportException
     */
    public IAMaterialEntnahmeJasperDS(Long matEntId) throws AKReportException {
        super();
        this.matEntId = matEntId;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
        try {
            InnenauftragService ias = getCCService(InnenauftragService.class);
            matEntnahme = ias.findMaterialEntnahme(matEntId);

            List<IAMaterialEntnahmeArtikel> artikel = ias.findArtikel4MatEntnahme(matEntId);
            if (artikel != null) {
                // min. 15 Datensaetze darstellen (fuer Leerzeilen)
                while (artikel.size() < 15) {
                    artikel.add(null);
                }
            }

            dataIterator = (artikel != null) ? artikel.iterator() : null;

            lager = ias.findLager(matEntnahme.getLagerId());
            budget = ias.findBudget(matEntnahme.getIaBudgetId());
            budgetPerson = getBudgetPerson(budget.getBudgetUserId()).toString();

            ia = ias.findIA(budget.getIaId());
            IA ia = ias.findIA(budget.getIaId());

            Long rangAuftragId = ia.getRangierungsAuftragId();
            if (rangAuftragId != null) {
                // Kostenstelle von HVT-Gruppe des Rangierungsauftrags ermitteln (bei Rangierungsauftrag)
                RangierungAdminService ras = getCCService(RangierungAdminService.class);
                RangierungsAuftrag ra = ras.findRA(rangAuftragId);

                HVTService hvts = getCCService(HVTService.class);
                hvtGruppe = hvts.findHVTGruppe4Standort(ra.getHvtStandortId());
            }
            else if (matEntnahme.getHvtIdStandort() != null) {
                // Kostenstelle von HVT-Gruppe auf Material-Entnahme ermitteln
                HVTService hvts = getCCService(HVTService.class);
                hvtGruppe = hvts.findHVTGruppe4Standort(matEntnahme.getHvtIdStandort());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException(e.getMessage(), e);
        }
    }

    /* Ermittelt den Namen des Budgetverantwortlichen */
    private StringBuilder getBudgetPerson(Long userId) throws AKReportException {
        StringBuilder projectLeadBuffer = new StringBuilder();
        try {
            if (userId != null) {
                IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(IServiceLocatorNames.AUTHENTICATION_SERVICE);
                AKUserService userService = locator.getService(AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class, null);
                AKUser user = userService.findById(userId);
                projectLeadBuffer.append(user.getFirstName());
                projectLeadBuffer.append(" ");
                projectLeadBuffer.append(user.getName());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException(e.getMessage(), e);
        }
        return projectLeadBuffer;
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    public boolean next() throws JRException {
        boolean hasNext = false;

        if (dataIterator != null) {
            hasNext = dataIterator.hasNext();
            if (hasNext) {
                currentData = dataIterator.next();
            }
        }
        return hasNext;
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if (currentData != null) {
            if ("LAGER".equals(field)) {
                return (lager != null) ? lager.getName() : "";
            }
            else if (VBZ_KEY.equals(field)) {
                return ia != null ? ia.getIaNummer() : "";
            }
            else if ("PROJEKTLEITER".equals(field)) {
                return (budget != null) ? budget.getProjektleiter() : "";
            }
            else if ("IA_NUMMER".equals(field)) {
                return (ia != null) ? ia.getIaNummer() : "";
            }
            else if ("MAT_NR".equals(field)) {
                return currentData.getMaterialNr();
            }
            else if ("ARTIKEL".equals(field)) {
                return currentData.getArtikel();
            }
            else if ("EINZELPREIS".equals(field)) {
                return currentData.getEinzelpreis();
            }
            else if ("ANZAHL".equals(field)) {
                return currentData.getAnzahl();
            }
            else if ("ANLAGENBEZEICHNUNG".equals(field)) {
                return currentData.getAnlagenBez();
            }
            else if ("TYP_RESERVIERUNG".equals(field)) {
                return NumberTools.equal(IAMaterialEntnahme.TYP_RESERVATION, matEntnahme.getEntnahmetyp());
            }
            else if ("TYP_ENTNAHME".equals(field)) {
                return NumberTools.equal(IAMaterialEntnahme.TYP_WITHDRAWL, matEntnahme.getEntnahmetyp());
            }
            else if ("MAT_ENTNAHME_DATUM".equals(field)) {
                return (matEntnahme != null) && (matEntnahme.getCreatedAt() != null) ? DateFormat.getDateInstance().format(matEntnahme.getCreatedAt()) : "";
            }
            else if ("ANFORDERER".equals(field)) {
                return budgetPerson != null ? budgetPerson : "";
            }
            else if ("POS".equals(field)) {
                return Integer.toString(++pos);
            }
            else if ("KOSTENSTELLE".equals(field)) {
                return hvtGruppe != null ? hvtGruppe.getKostenstelle() : null;
            }
            else if ("STANDORT".equals(field)) {
                return hvtGruppe != null ? hvtGruppe.getOrtsteil() : null;
            }
        }
        return null;
    }

}


