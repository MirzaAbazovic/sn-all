/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2007 09:13:22
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKDepartmentService;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.innenauftrag.IA;
import de.augustakom.hurrican.model.cc.innenauftrag.IABudget;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.RangierungAdminService;


/**
 * Jasper-DS, um die Daten fuer den Budget-Report zu ermitteln.
 */
public class IABudgetJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(IABudgetJasperDS.class);
    private static final String[] locationShortCutList = new String[] { "A", "M", "N", "Z" };
    private static final String SELECTED = "X";
    private Long budgetId = null;
    private IA ia = null;
    private IABudget budget = null;
    private HVTGruppe hvtGruppe = null;
    private String kostenstelle = null;
    private AKUser projectLeadPerson = null;
    private AKDepartment projectLeadDepartment = null;
    private AKUser budgetResponsiblePerson = null;
    private AKDepartment budgetResponsibleDepartment = null;
    private boolean printData = true;
    private boolean isAugsburg = false;
    private boolean isMunich = false;
    private boolean isNuremberg = false;
    private boolean isCentral = false;
    private boolean isInnenauftragNeu = true;
    private InnenauftragService innenauftragService;
    private RangierungAdminService rangierungAdminService;
    private AKUserService userService;
    private HVTService hvtService;
    private AKDepartmentService departmentService;

    /**
     * Konstruktor mit Angabe der Budget-ID.
     *
     * @param budgetId
     */
    public IABudgetJasperDS(Long budgetId) throws AKReportException {
        super();
        this.budgetId = budgetId;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
        try {
            innenauftragService = getCCService(InnenauftragService.class);
            rangierungAdminService = getCCService(RangierungAdminService.class);
            hvtService = getCCService(HVTService.class);
            userService = getAuthenticationService(AKUserService.class);
            departmentService = getAuthenticationService(AKDepartmentService.class);

            budget = innenauftragService.findBudget(budgetId);
            if (BooleanTools.nullToFalse(budget.getCancelled())) {
                throw new AKReportException("Budget ist bereits storniert. Ausdruck nicht zulässig!");
            }

            ia = innenauftragService.findIA(budget.getIaId());

            // Kostenstelle bei Innenauftrag ermmitteln
            Long auftragId = ia.getAuftragId();
            if (auftragId != null) {
                kostenstelle = ia.getKostenstelle();
            }

            // Kostenstelle von HVT-Gruppe ermitteln (bei Rangierungsauftrag)
            Long rangAuftragId = ia.getRangierungsAuftragId();
            if (rangAuftragId != null) {
                RangierungsAuftrag ra = rangierungAdminService.findRA(rangAuftragId);

                hvtGruppe = hvtService.findHVTGruppe4Standort(ra.getHvtStandortId());
                kostenstelle = hvtGruppe.getKostenstelle();
            }

            if (budget.getResponsibleUserId() != null) {
                budgetResponsiblePerson = userService.findById(budget.getResponsibleUserId());
                if (budgetResponsiblePerson != null) {
                    budgetResponsibleDepartment = departmentService.findDepartmentById(budgetResponsiblePerson.getDepartmentId());
                }
            }

            if (ia.getProjectLeadId() != null) {
                projectLeadPerson = userService.findById(ia.getProjectLeadId());
                if (projectLeadPerson != null) {
                    projectLeadDepartment = departmentService.findDepartmentById(projectLeadPerson.getDepartmentId());
                }
            }

            preSelectLocation();
            evaluateIaType();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException(e.getMessage(), e);
        }
    }

    /*
     * Vorbelegung Standort
     */
    private void preSelectLocation() {
        String iANumber = ia.getIaNummer();

        if ((iANumber != null) && (iANumber.length() > 0)) {
            for (int i = 0; i < locationShortCutList.length; i++) {
                if (iANumber.substring(0, 1).equalsIgnoreCase(locationShortCutList[i])) {
                    switch (i) {
                        case 0:
                            isAugsburg = Boolean.TRUE;
                            break;
                        case 1:
                            isMunich = Boolean.TRUE;
                            break;
                        case 2:
                            isNuremberg = Boolean.TRUE;
                            break;
                        case 3:
                            isCentral = Boolean.TRUE;
                            break;
                        default:
                            break;
                    }
                    break;
                }
            }
        }
    }

    /*
     * Ermittelt, ob es sich um ein neues Budget oder einen Nachtrag handelt.
     * Es handelt sich um einen Nachtrag, wenn bereits ein abgeschlossenes Budget
     * vorhanden ist.
     */
    private void evaluateIaType() {
        try {
            List<IABudget> budgets = innenauftragService.findBudgets4IA(budget.getIaId());
            if (CollectionTools.isNotEmpty(budgets)) {
                boolean closedBudgetFound = false;
                for (IABudget tmp : budgets) {
                    if (NumberTools.isLess(tmp.getId(), budget.getId())
                            && (tmp.getClosedAt() != null)
                            && !BooleanTools.nullToFalse(tmp.getCancelled())) {
                        closedBudgetFound = true;
                        break;
                    }
                }

                isInnenauftragNeu = !closedBudgetFound;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    public boolean next() throws JRException {
        if (printData) {
            printData = false;
            return true;
        }
        return false;
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if (field == null) {
            return null;
        }
        else {
            switch (field) {
                case "IA_NUMMER":
                    return (ia != null) ? ia.getIaNummer() : null;
                case "LVL1_NAME":
                    return (budget != null) ? budget.getIaLevel1() : null;
                case "LVL1_ID":
                    return (budget != null) ? budget.getIaLevel1Id() : null;
                case "LVL3_NAME":
                    return (budget != null) ? budget.getIaLevel3() : null;
                case "LVL3_ID":
                    return (budget != null) ? budget.getIaLevel3Id() : null;
                case "LVL5_NAME":
                    return (budget != null) ? budget.getIaLevel5() : null;
                case "LVL5_ID":
                    return (budget != null) ? budget.getIaLevel5Id() : null;
                case "PROJEKTLEITER":
                    return (budget != null) ? budget.getProjektleiter() : null;
                case "BUDGET":
                    return (budget != null) ? budget.getBudget() : null;
                case "BUDGET_DATE":
                    return (budget != null) ? budget.getCreatedAt() : null;
                case "BUDGET_PLANNED_FINISH_DATE":
                    return (budget != null) ? budget.getPlannedFinishDate() : null;
                case "PROJEKTBEZ":
                    return (ia != null) ? ia.getProjektbez() : null;
                case "KOSTENSTELLE":
                    return (kostenstelle != null) ? kostenstelle.trim() : null;
                case "PROJECTLEAD_FIRSTNAME":
                    return (projectLeadPerson != null) ? projectLeadPerson.getFirstName() : null;
                case "PROJECTLEAD_NAME":
                    return (projectLeadPerson != null) ? projectLeadPerson.getName() : null;
                case "PROJECTLEAD_PHONE":
                    return (projectLeadPerson != null) ? projectLeadPerson.getPhone() : null;
                case "PROJECTLEAD_DEPARTMENT":
                    return (projectLeadDepartment != null) ? projectLeadDepartment.getDescription() : null;
                case "BUDGETRESPONSIBLE_FIRSTNAME":
                    return (budgetResponsiblePerson != null) ? budgetResponsiblePerson.getFirstName() : null;
                case "BUDGETRESPONSIBLE_NAME":
                    return (budgetResponsiblePerson != null) ? budgetResponsiblePerson.getName() : null;
                case "BUDGETRESPONSIBLE_PHONE":
                    return (budgetResponsiblePerson != null) ? budgetResponsiblePerson.getPhone() : null;
                case "BUDGETRESPONSIBLE_DEPARTMENT":
                    return (budgetResponsibleDepartment != null) ? budgetResponsibleDepartment.getDescription() : null;
                case "IS_AUGSBURG":
                    return isAugsburg ? SELECTED : null;
                case "IS_CENTRAL":
                    return isCentral ? SELECTED : null;
                case "IS_MUNICH":
                    return isMunich ? SELECTED : null;
                case "IS_NUREMBERG":
                    return isNuremberg ? SELECTED : null;
                case "IS_INNENAUFTRAG_NEU":
                    return isInnenauftragNeu ? SELECTED : null;
                case "IS_BUDGETNACHTRAG":
                    return !isInnenauftragNeu ? SELECTED : null;
                default:
                    return null;
            }
        }
    }
}


