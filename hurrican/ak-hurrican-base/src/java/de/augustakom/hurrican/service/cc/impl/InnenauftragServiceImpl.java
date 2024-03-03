/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.01.2007 15:12:28
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.common.tools.reports.jasper.AKJasperReportContext;
import de.augustakom.common.tools.reports.jasper.AKJasperReportHelper;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.IABudgetDAO;
import de.augustakom.hurrican.dao.cc.IAMaterialDAO;
import de.augustakom.hurrican.dao.cc.LagerDAO;
import de.augustakom.hurrican.model.cc.Lager;
import de.augustakom.hurrican.model.cc.innenauftrag.IA;
import de.augustakom.hurrican.model.cc.innenauftrag.IABudget;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterial;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahme;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahmeArtikel;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel1;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel3;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel5;
import de.augustakom.hurrican.model.shared.view.InnenauftragQuery;
import de.augustakom.hurrican.model.shared.view.InnenauftragView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.augustakom.hurrican.service.cc.impl.reportdata.IABudgetJasperDS;
import de.augustakom.hurrican.service.cc.impl.reportdata.IAMaterialEntnahmeJasperDS;


/**
 * Service-Implementierung von <code>InnenauftragService</code>.
 *
 *
 */
@CcTxRequired
public class InnenauftragServiceImpl extends DefaultCCService implements InnenauftragService {

    private static final Logger LOGGER = Logger.getLogger(InnenauftragServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.RegistryService")
    private RegistryService registryService;

    @Resource(name = "iaMaterialDAO")
    private IAMaterialDAO materialDAO = null;

    @Resource(name = "iaBudgetDAO")
    private IABudgetDAO budgetDAO = null;

    @Resource(name = "lagerDAO")
    private LagerDAO lagerDAO = null;


    @Override
    public IA createIA(Long auftragId, String iaNummer, Long projectLeadId, String kostenstelle) throws StoreException, ValidationException {
        if ((auftragId == null) || StringUtils.isBlank(iaNummer)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        try {
            // evtl. sollte hier noch eine Pruefung der IA-Nummer stattfinden
            IA ia = new IA();
            ia.setAuftragId(auftragId);
            ia.setIaNummer(iaNummer);
            ia.setProjectLeadId(projectLeadId);
            ia.setKostenstelle(kostenstelle);

            getBudgetDAO().store(ia);

            return ia;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveIA(IA toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            getBudgetDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public IA findIA4Auftrag(Long auftragId) throws FindException {
        if (auftragId == null) { return null; }
        try {
            IA example = new IA();
            example.setAuftragId(auftragId);

            List<IA> ias = getBudgetDAO().queryByExample(example, IA.class, new String[] { "id" }, null);
            if (CollectionTools.isNotEmpty(ias)) {
                if (ias.size() == 1) {
                    return ias.get(0);
                }
                throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, ias.size() });
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public IA findIA4RangierungsAuftrag(Long raId) throws FindException {
        if (raId == null) { return null; }
        try {
            IA example = new IA();
            example.setRangierungsAuftragId(raId);

            List<IA> ias = getBudgetDAO().queryByExample(example, IA.class, new String[] { "id" }, null);
            if (CollectionTools.isNotEmpty(ias)) {
                if (ias.size() == 1) {
                    return ias.get(0);
                }
                throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, ias.size() });
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public IA findIA(Long iaId) throws FindException {
        if (iaId == null) { return null; }
        try {
            return getBudgetDAO().findById(iaId, IA.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<InnenauftragView> findIAViews(InnenauftragQuery query) throws FindException {
        if ((query == null) || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            List<InnenauftragView> views = getBudgetDAO().findIAViews(query);

            // Kundendaten laden
            KundenService ks = getBillingService(KundenService.class);
            ks.loadKundendaten4AuftragViews(views);

            return views;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveBudget(IABudget toSave, Long sessionId) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            if (toSave.getId() == null) {
                // pruefen, ob noch ein offenes Budget vorhanden ist
                List<IABudget> budgets = findBudgets4IA(toSave.getIaId());
                if (CollectionTools.isNotEmpty(budgets)) {
                    for (IABudget bud : budgets) {
                        if (bud.isOpen()) {
                            throw new StoreException("Budget kann nicht angelegt werden, da ein " +
                                    "anderes Budget noch offen ist!");
                        }
                    }
                }

                if (StringUtils.isBlank(toSave.getProjektleiter())) {
                    IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(IServiceLocatorNames.AUTHENTICATION_SERVICE);
                    AKUserService userService = locator.getService(AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class, null);
                    AKUser user = userService.findById(toSave.getBudgetUserId());

                    toSave.setProjektleiter(
                            (user != null) ? user.getFirstName() + " " + user.getName() : HurricanConstants.UNKNOWN);
                }
            }

            getBudgetDAO().store(toSave);
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<IABudget> findBudgets4IA(Long iaId) throws FindException {
        if (iaId == null) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            IABudget example = new IABudget();
            example.setIaId(iaId);

            return getBudgetDAO().queryByExample(example, IABudget.class, new String[] { IABudget.ID }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public IABudget findBudget(Long budgetId) throws FindException {
        if (budgetId == null) { return null; }
        try {
            return getBudgetDAO().findById(budgetId, IABudget.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean hasOpenBudget(Long auftragId) throws FindException {
        if (auftragId == null) { return false; }
        try {
            IA ia = findIA4Auftrag(auftragId);
            if (ia != null) {
                List<IABudget> budgets = findBudgets4IA(ia.getId());
                if (CollectionTools.isNotEmpty(budgets)) {
                    for (IABudget budget : budgets) {
                        if (budget.isOpen()) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveMaterialEntnahme(IAMaterialEntnahme toSave, Long sessionId) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            // pruefen, ob Budget vorhanden und noch 'offen' ist
            IABudget budget = findBudget(toSave.getIaBudgetId());
            if (budget == null) {
                throw new StoreException("Zugeordnetes Budget konnte nicht ermittelt werden!");
            }
            else if ((budget.getClosedAt() != null) || BooleanTools.nullToFalse(budget.getCancelled())) {
                throw new StoreException("Das zugeordnete Budget ist bereits geschlossen. " +
                        "Materialentnahme deshalb nicht moeglich!");
            }

            if (toSave.getId() == null) {
                // Pflichtfelder setzen
                AKUser user = getAKUserBySessionIdSilent(sessionId);

                toSave.setCreatedAt(new Date());
                toSave.setCreatedFrom((user != null) ? user.getName() : HurricanConstants.UNKNOWN);
            }

            getMaterialDAO().store(toSave);
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<IAMaterialEntnahme> findMaterialEntnahmen4Budget(Long budgetId) throws FindException {
        if (budgetId == null) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            IAMaterialEntnahme example = new IAMaterialEntnahme();
            example.setIaBudgetId(budgetId);

            return getMaterialDAO().queryByExample(example, IAMaterialEntnahme.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public IAMaterialEntnahme findMaterialEntnahme(Long id) throws FindException {
        if (id == null) { return null; }
        try {
            return getMaterialDAO().findById(id, IAMaterialEntnahme.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveMaterialEntnahmeArtikel(IAMaterialEntnahmeArtikel toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            getMaterialDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<IAMaterialEntnahmeArtikel> findArtikel4MatEntnahme(Long matEntnahmeId) throws FindException {
        if (matEntnahmeId == null) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            return getMaterialDAO().findArtikel4MatEntnahme(matEntnahmeId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<IAMaterial> findIAMaterialien() throws FindException {
        try {
            return getMaterialDAO().findAll(IAMaterial.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public IAMaterial findMaterial(String materialNr) throws FindException {
        if (StringUtils.isBlank(materialNr)) { return null; }
        try {
            IAMaterial ex = new IAMaterial();
            ex.setMaterialNr(materialNr);

            List<IAMaterial> result = getMaterialDAO().queryByExample(ex, IAMaterial.class);
            if (CollectionTools.isNotEmpty(result)) {
                if (result.size() == 1) {
                    return result.get(0);
                }
                throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, result.size() });
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Lager> findLager() throws FindException {
        try {
            return getLagerDAO().findAll(Lager.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Lager findLager(Long lagerId) throws FindException {
        if (lagerId == null) { return null; }
        try {
            return getLagerDAO().findById(lagerId, Lager.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public JasperPrint printBudget(Long budgetId) throws FindException, AKReportException {
        if (budgetId == null) { throw new AKReportException("Kein Budget angegeben!"); }
        try {
            AKJasperReportContext ctx = new AKJasperReportContext(
                    "de/augustakom/hurrican/reports/innenauftrag/IABudget.jasper", null,
                    new IABudgetJasperDS(budgetId));

            AKJasperReportHelper jrh = new AKJasperReportHelper();
            return jrh.createReport(ctx);
        }
        catch (AKReportException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Fehler beim Erstellen des Budget-Reports!");
        }
    }

    @Override
    public JasperPrint printMaterialentnahme(Long matEntnahmeId) throws FindException, AKReportException {
        if (matEntnahmeId == null) { throw new AKReportException("Keine Materialentnahme angegeben!"); }
        try {
            AKJasperReportContext ctx = new AKJasperReportContext(
                    "de/augustakom/hurrican/reports/innenauftrag/IAMaterialEntnahme.jasper", null,
                    new IAMaterialEntnahmeJasperDS(matEntnahmeId));

            AKJasperReportHelper jrh = new AKJasperReportHelper();
            return jrh.createReport(ctx);
        }
        catch (AKReportException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Fehler beim Erstellen des Budget-Reports!");
        }
    }

    @Override
    public int importMaterialliste(File file) throws StoreException {
        if (file == null) { throw new StoreException("Kein File fuer den Import angegeben!"); }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StringTools.CC_DEFAULT_CHARSET));

            // vorhandene Artikel aus Tabelle loeschen
            //getMaterialDAO().deleteMaterials();  // kein Delete mehr!!
            // Stand 24.01.2008 lt Herrn Winkler muessen die vorhandenen Artikel zuerst geloescht werden  - Hanke
            getMaterialDAO().deleteMaterials();
            // neue Artikel einspielen
            int count = 0;
            String line;
            while ((line = reader.readLine()) != null) {

                // In einem Text vorkommende ; entfernen. Diese Texte sind in " eingeschlossen.
                String escaped = StringUtils.substringBetween(line, "\"");
                if (StringUtils.isNotBlank(escaped)) {
                    String repl = StringUtils.replace(escaped, ";", ",");
                    repl = StringUtils.replace(repl, "\"", "");
                    line = StringUtils.replace(line, escaped, repl);
                    line = StringUtils.remove(line, "\"");
                }

                String[] tokens = StringUtils.splitPreserveAllTokens(line, ";");
                if ((tokens != null) && (tokens.length >= 2)) {
                    String matNr = tokens[0];

                    // Artikel in DB suchen
                    IAMaterial material = getMaterialDAO().findMaterial(matNr);
                    if (material == null) {
                        material = new IAMaterial();
                        material.setMaterialNr(matNr);
                    }

                    String quantity = StringUtils.substringBeforeLast(tokens[3], ",");
                    quantity = StringUtils.replace(quantity, ".", "");
                    Integer quantityN = (StringUtils.isNotBlank(quantity)) ? Integer.valueOf(quantity) : Integer.valueOf(0);

                    String value = StringUtils.replace(tokens[4], ".", "");
                    value = StringUtils.replace(value, ",", ".");
                    Float valueF = (StringUtils.isNotBlank(value)) ? new Float(value) : new Float(0f);

                    // Einzelpreis errechnet sich aus value/quantity
                    Float epreis = ((valueF != 0f) && (quantityN > 0))
                            ? new Float(valueF / quantityN) :
                            (material.getEinzelpreis() == null) ? new Float(0f) : material.getEinzelpreis();

                    material.setArtikel(tokens[1]);
                    if (StringUtils.isNotBlank(tokens[2])) {
                        material.setText(tokens[2]);
                    }
                    material.setEinzelpreis(epreis);

                    getMaterialDAO().store(material);
                    count++;
                }
            }
            return count;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Import der Artikel ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Exception e) {
                    LOGGER.warn("importMaterialliste() - exception closing reader", e);
                }
            }
        }
    }

    @Override
    public List<IaLevel1> findIaLevels() throws FindException {
        try {
            return getBudgetDAO().findAll(IaLevel1.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @Nonnull
    public List<IaLevel1> findIaLevelsForUser(final @Nonnull AKUser user) {
        return getBudgetDAO()
                .find("from IaLevel1 l where l.bereichName = ? or l.lockMode = false", (user.getBereich() != null) ? user.getBereich().getName() : null);
    }

    @Override
    public void saveIaLevel(IaLevel1 toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            getBudgetDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteIaLevel(IaLevel1 toDelete) throws DeleteException {
        try {
            getBudgetDAO().delete(toDelete);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CheckForNull
    public IaLevel3 findLevel3Projekt4PD(@Nonnull IA innenauftrag, float budget) throws FindException {
        Integer schwellwert = registryService.getIntValue(RegistryService.REGID_IA_BUDGET_PROJEKTE_SCHWELLWERT);
        if (schwellwert == null) {
            throw new FindException("Der Schwellwert zur Unterscheidung von Standard- und Grossprojekten konnte "
                    + "nicht ermittelt werden!");
        }
        List<IABudget> iaBudgets = findBudgets4IA(innenauftrag.getId());
        if (iaBudgets == null) {
            return findLevel3Projekt(RegistryService.REGID_IA_BUDGET_EBENE3_PD_STANDARDPROJEKTE_GK);
        }
        float summeBudgets = iaBudgets.stream()
                .filter(b -> !BooleanTools.nullToFalse(b.getCancelled()))
                .map(IABudget::getBudget)
                .reduce((b1, b2) -> b1 + b2)
                .orElse(0.0f);
        return (summeBudgets + budget >= schwellwert.floatValue()) ?
                findLevel3Projekt(RegistryService.REGID_IA_BUDGET_EBENE3_PD_GROSSPROJEKTE_GK)
                : findLevel3Projekt(RegistryService.REGID_IA_BUDGET_EBENE3_PD_STANDARDPROJEKTE_GK);
    }

    @Override
    public IaLevel3 findLevel3Grossprojekt4PD() throws FindException {
        return findLevel3Projekt(RegistryService.REGID_IA_BUDGET_EBENE3_PD_GROSSPROJEKTE_GK);
    }

    @Override
    @Nullable
    public IaLevel5 getLevel5ByTaifunProduktName(@Nonnull final List<IaLevel5> iaLevel5s, @Nullable final String taifunProduktName) {
        return iaLevel5s.stream()
                .filter(l5 -> StringUtils.containsIgnoreCase(l5.getName(), taifunProduktName))
                .sorted((s1, s2) -> Integer.compare(s1.getName().length(), s2.getName().length()))
                .findFirst()
                .orElse(null);
    }

    IaLevel3 findLevel3Projekt(Long registryId4Name) throws FindException {
        IaLevel3 result = null;
        String name = registryService.getStringValue(registryId4Name);
        if (StringUtils.isNotBlank(name)) {
            List<IaLevel3> queryResult = getBudgetDAO().find("from IaLevel3 l where l.name = ?", name);
            if (queryResult.size() == 1) {
                result = queryResult.get(0);
            }
        }
        return result;
    }

    /**
     * @return Returns the budgetDAO.
     */
    public IABudgetDAO getBudgetDAO() {
        return this.budgetDAO;
    }

    /**
     * @param budgetDAO The budgetDAO to set.
     */
    public void setBudgetDAO(IABudgetDAO budgetDAO) {
        this.budgetDAO = budgetDAO;
    }

    /**
     * @return Returns the materialDAO.
     */
    public IAMaterialDAO getMaterialDAO() {
        return this.materialDAO;
    }

    /**
     * @param materialDAO The materialDAO to set.
     */
    public void setMaterialDAO(IAMaterialDAO materialDAO) {
        this.materialDAO = materialDAO;
    }

    /**
     * @return Returns the lagerDAO.
     */
    public LagerDAO getLagerDAO() {
        return this.lagerDAO;
    }

    /**
     * @param lagerDAO The lagerDAO to set.
     */
    public void setLagerDAO(LagerDAO lagerDAO) {
        this.lagerDAO = lagerDAO;
    }

    public String fetchInnenAuftragKostenstelle(Long auftragId) {
        return this.budgetDAO.fetchInnenAuftragKostenstelle(auftragId);
    }

}
