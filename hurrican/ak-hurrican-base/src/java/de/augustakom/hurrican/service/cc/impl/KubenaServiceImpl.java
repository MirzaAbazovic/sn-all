/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2005 08:26:11
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.file.FileTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.common.tools.poi.HSSFCellTools;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AnredeDAO;
import de.augustakom.hurrican.dao.cc.KubenaDAO;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Ansprechpartner;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Anrede;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.kubena.AbstractKubenaRefModel;
import de.augustakom.hurrican.model.cc.kubena.Kubena;
import de.augustakom.hurrican.model.cc.kubena.KubenaHVT;
import de.augustakom.hurrican.model.cc.kubena.KubenaProdukt;
import de.augustakom.hurrican.model.cc.kubena.KubenaResultView;
import de.augustakom.hurrican.model.cc.kubena.KubenaVbz;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.KubenaService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Service-Implementierung von <code>KubenaService</code>.
 *
 *
 */
@CcTxRequired
public class KubenaServiceImpl extends DefaultCCService implements KubenaService {

    private static final Logger LOGGER = Logger.getLogger(KubenaServiceImpl.class);

    private AnredeDAO anredeDAO = null;
    private AbstractValidator kubenaValidator = null;

    private List<Anrede> anreden = null;

    @Override
    public List<Kubena> findKubenas() throws FindException {
        try {
            return ((KubenaDAO) getDAO()).findAll(Kubena.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveKubena(Kubena toStore) throws StoreException, ValidationException {
        if (toStore == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }

        ValidationException ve = new ValidationException(toStore, "Kubena");
        getKubenaValidator().validate(toStore, ve);
        if (ve.hasErrors()) {
            throw ve;
        }

        try {
            ((KubenaDAO) getDAO()).store(toStore);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<KubenaHVT> findKubenaHVTs(Long kubenaId) throws FindException {
        return findKubenaRef(kubenaId, KubenaHVT.class);
    }

    @Override
    public void saveKubenaHVTs(List<KubenaHVT> toStore) throws StoreException {
        if (toStore != null) {
            for (KubenaHVT obj : toStore) {
                storeKubenaRef(obj);
            }
        }
        else {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
    }

    @Override
    public void deleteKubenaHVTs(Long kubenaId, List<Long> hvtStdIds) throws DeleteException {
        if ((kubenaId == null) || (hvtStdIds == null)) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }

        try {
            KubenaDAO dao = (KubenaDAO) getDAO();
            for (Long hvtId : hvtStdIds) {
                dao.deleteKubenaHVT(kubenaId, hvtId);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<KubenaProdukt> findKubenaProdukt(Long kubenaId) throws FindException {
        return findKubenaRef(kubenaId, KubenaProdukt.class);
    }

    @Override
    public void saveKubenaProdukte(List<KubenaProdukt> toStore) throws StoreException {
        if (toStore != null) {
            for (KubenaProdukt obj : toStore) {
                storeKubenaRef(obj);
            }
        }
        else {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
    }

    @Override
    public void deleteKubenaProdukte(Long kubenaId, List<Long> prodIds) throws DeleteException {
        if ((kubenaId == null) || (prodIds == null)) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }

        try {
            KubenaDAO dao = (KubenaDAO) getDAO();
            for (Long prodId : prodIds) {
                dao.deleteKubenaProdukt(kubenaId, prodId);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<KubenaVbz> findKubenaVbz(Long kubenaId) throws FindException {
        return findKubenaRef(kubenaId, KubenaVbz.class);
    }

    @Override
    public void saveKubenaVbz(KubenaVbz toStore) throws StoreException {
        storeKubenaRef(toStore);
    }

    @Override
    public List<KubenaVbz> addVbz2Kubena(Long kubenaId, String vbz, String inputType) throws StoreException {
        if ((kubenaId == null) || StringUtils.isBlank(vbz)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        if (StringUtils.contains(vbz, WildcardTools.SYSTEM_WILDCARD) || StringUtils.contains(vbz, WildcardTools.DB_WILDCARD)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        try {
            PhysikService ps = getCCService(PhysikService.class);
            List<VerbindungsBezeichnung> vbzs = ps.findVerbindungsBezeichnungLike(StringUtils.trimToNull(vbz));

            List<KubenaVbz> retVal = new ArrayList<>();
            if ((vbzs != null) && (!vbzs.isEmpty())) {
                for (VerbindungsBezeichnung verbindungsBezeichnung : vbzs) {
                    KubenaVbz kt = createKubenaVbz(kubenaId, verbindungsBezeichnung.getVbz(), inputType, verbindungsBezeichnung);
                    saveKubenaVbz(kt);
                    retVal.add(kt);
                }
            }
            else {
                KubenaVbz kt = createKubenaVbz(kubenaId, vbz, inputType, null);
                saveKubenaVbz(kt);
                retVal.add(kt);
            }

            return retVal;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR);
        }
    }

    /**
     * Erstellt ein neues KubenaVbz-Objekt.
     *
     * @param kubenaId
     * @param verbindungsBezeichnung
     * @param inputType
     * @param verbindungsBezeichnung
     * @return
     */
    private KubenaVbz createKubenaVbz(Long kubenaId, String vbz, String inputType, VerbindungsBezeichnung verbindungsBezeichnung) {
        KubenaVbz kt = new KubenaVbz();
        kt.setKubenaId(kubenaId);
        kt.setVbz(vbz);
        kt.setInput(inputType);
        kt.setVorhanden((verbindungsBezeichnung != null) ? Boolean.TRUE : Boolean.FALSE);
        return kt;
    }

    /**
     * Sucht nach einer best. Kubena-Referenz (z.B. KubenaHVT).
     *
     * @param kubenaId
     * @param refType
     * @return
     * @throws FindException
     */
    protected <T> List<T> findKubenaRef(Long kubenaId, Class<T> refType) throws FindException {
        if (kubenaId == null) { return null; }
        if (refType == null) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            AbstractKubenaRefModel refObj = (AbstractKubenaRefModel) refType.newInstance();
            refObj.setKubenaId(kubenaId);
            return ((KubenaDAO) getDAO()).queryByExample(refObj, refType);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Speichert das Objekt <code>toStore</code>.
     *
     * @param toStore
     * @throws StoreException
     */
    protected void storeKubenaRef(AbstractKubenaRefModel toStore) throws StoreException {
        if (toStore == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            ((StoreDAO) getDAO()).store(toStore);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public File createKubena(Long kubenaId) throws FindException {
        if (kubenaId == null) { return null; }
        FileOutputStream out = null;
        try {
            Kubena kubena = ((KubenaDAO) getDAO()).findById(kubenaId, Kubena.class);
            if (kubena == null) {
                throw new FindException("Daten fuer die Kubena konnten nicht ermittelt werden!");
            }

            List<KubenaResultView> kubenaViews = getKubenaResultViews(kubenaId, kubena);

            // Excel-File + Sheet erzeugen
            String filePath = SystemUtils.USER_HOME + SystemUtils.FILE_SEPARATOR + kubena.getName() +
                    "_" + DateTools.formatDate(new Date(), "dd-MM-yyyy_HH-mm-ss") + ".xls";
            out = new FileOutputStream(filePath);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet();

            List<Long> kNoOrigs = new ArrayList<>();
            for (KubenaResultView view : kubenaViews) {
                kNoOrigs.add(view.getKundNoOrig());
            }

            // Kunden ermitteln
            KundenService kundenService = getBillingService(KundenService.class);
            List<Kunde> kunden = kundenService.findByKundeNos(kNoOrigs);
            if (kunden == null) {
                throw new FindException("Kundendaten konnten nicht ermittelt werden!");
            }
            Map<Long, Kunde> kundenMap = new HashMap<>();
            CollectionMapConverter.convert2Map(kunden, kundenMap, "getKundeNo", null);

            List<Long> kNos = new ArrayList<>();
            for (Kunde k : kunden) {
                kNos.add(k.getKundeNo());
            }

            // Ansprechpartner und Adressen ermitteln
            List<Ansprechpartner> ansprechpartner = kundenService.getAnsprechpartner4Kunden(kNoOrigs);
            KundenAnspPredicate anspPredicate = new KundenAnspPredicate();

            List<Adresse> adressen = kundenService.findAdressen4Kunden(kNos);
            Map<Long, Adresse> adresseMap = new HashMap<>();
            if (adressen != null) {
                CollectionMapConverter.convert2Map(adressen, adresseMap, "getKundeNo", null);
            }

            // Header-Zeile anlegen
            writeHeader(sheet.createRow(0));

            CCAuftragService as = getCCService(CCAuftragService.class);
            int rowCount = 1;
            for (KubenaResultView view : kubenaViews) {
                rowCount = writeRows(sheet, kundenMap, ansprechpartner, anspPredicate, adresseMap, as, rowCount, view);
            }

            wb.write(out);
            out.close();

            return new File(filePath);
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
        finally {
            FileTools.closeStreamSilent(out);
        }
    }

    private List<KubenaResultView> getKubenaResultViews(Long kubenaId, Kubena kubena) throws FindException {
        List<KubenaResultView> kubenaViews = null;
        if (NumberTools.equal(kubena.getKriterium(), Kubena.KRITERIUM_HVT)) {
            kubenaViews = ((KubenaDAO) getDAO()).queryKubenaHVT(kubenaId);
        }
        else if (NumberTools.equal(kubena.getKriterium(), Kubena.KRITERIUM_HVT_PROD)) {
            kubenaViews = ((KubenaDAO) getDAO()).queryKubenaProd(kubenaId);
        }
        else if (NumberTools.equal(kubena.getKriterium(), Kubena.KRITERIUM_VBZ)) {
            kubenaViews = ((KubenaDAO) getDAO()).queryKubenaVbz(kubenaId);
        }
        else {
            throw new FindException("Kubena-Kriterium wird nicht unterstuetzt!");
        }

        if ((kubenaViews == null) || (kubenaViews.isEmpty())) {
            throw new FindException("Es konnten keine Daten zu den Such-Parametern der Kubena ermittelt werden!");
        }
        return kubenaViews;
    }

    private int writeRows(HSSFSheet sheet, Map<Long, Kunde> kundenMap, List<Ansprechpartner> ansprechpartner,
            KundenAnspPredicate anspPredicate, Map<Long, Adresse> adresseMap, CCAuftragService as, int rowCount,
            KubenaResultView view) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, FindException {
        int rowCount1 = rowCount;
        HSSFRow row = sheet.createRow(rowCount1++);

        // nach Kundenanschrift suchen und Excel-File erzeugen
        Kunde kunde = kundenMap.get(view.getKundNoOrig());
        Adresse adresse = (kunde != null) ? adresseMap.get(kunde.getKundeNo()) : null;
        int col = writeKunde(row, kunde, adresse, 0);
        col = writeAuftrag(row, view, col);

        AuftragDaten ad = as.findAuftragDatenByAuftragId(view.getAuftragId());
        col = writeAuftragDaten(row, ad, col);

        // Ansprechpartner des Kunden ermitteln
        if ((kunde != null) && (ansprechpartner != null)) {
            anspPredicate.setKundeNo(kunde.getKundeNo());
            Collection<Ansprechpartner> kundenAnsp = CollectionUtils.select(ansprechpartner, anspPredicate);
            if (kundenAnsp != null) {
                int anspCount = 0;
                for (Ansprechpartner ansp : kundenAnsp) {
                    if (anspCount > 0) {
                        HSSFRow newRow = sheet.createRow(rowCount1++);
                        col = writeKunde(newRow, kunde, adresse, 0);
                        col = writeAuftrag(newRow, view, col);
                        col = writeAuftragDaten(newRow, ad, col);
                    }
                    col = writeAnsprechpartner(row, ansp, col);
                    anspCount++;
                }
            }
        }
        return rowCount1;
    }

    /* Schreibt die Header-Zeile.
     * @param row
     */
    private void writeHeader(HSSFRow row) {
        String headers = new ResourceReader("de.augustakom.hurrican.service.cc.resources.KubenaService")
                .getValue("cell.headers");
        String[] headerNames = StringUtils.split(headers, ";");
        for (int i = 0; i < headerNames.length; i++) {
            HSSFCellTools.createCell(row, i, headerNames[i]);
        }
    }

    /* Schreibt die Kunden-Daten in eine Excel-Zeile.
     * @param row
     * @param kunde
     * @param startWithCol
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private int writeKunde(HSSFRow row, Kunde kunde, Adresse adresse, int startWithCol) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        int startCol = startWithCol;
        String adressAnrede = null;
        String briefAnrede = null;
        if (adresse != null) {
            Anrede ansprache = getAnrede(adresse.getAnrede(), Anrede.ANREDEART_ANSPRACHE);
            adressAnrede = (ansprache != null) ? ansprache.getAnrede() : null;

            Anrede brief = getAnrede(adresse.getAnrede(), Anrede.ANREDEART_ADRESSE);
            briefAnrede = (brief != null) ? brief.getAnrede() : null;
        }

        HSSFCellTools.createCell(row, startCol++, adressAnrede);
        HSSFCellTools.createCell(row, startCol++, briefAnrede);
        HSSFCellTools.createCell(row, startCol++, adresse, "titel", true);
        HSSFCellTools.createCell(row, startCol++, adresse, "vorname", true);
        HSSFCellTools.createCell(row, startCol++, adresse, "name", true);
        HSSFCellTools.createCell(row, startCol++, adresse, "nameAdd", true);
        HSSFCellTools.createCell(row, startCol++, adresse, "strasse", true);
        HSSFCellTools.createCell(row, startCol++, adresse, "nummer", true);
        HSSFCellTools.createCell(row, startCol++, adresse, "postfach", true);
        HSSFCellTools.createCell(row, startCol++, adresse, "plz", true);
        HSSFCellTools.createCell(row, startCol++, adresse, "ort", true);
        HSSFCellTools.createCell(row, startCol++, kunde, "kundeNo", true);
        HSSFCellTools.createCell(row, startCol++, ((kunde != null) && (kunde.getHauptKundenNo() == null)) ? "1" : "0");
        HSSFCellTools.createCell(row, startCol++, kunde, "hauptKundenNo", true);
        HSSFCellTools.createCell(row, startCol++, kunde, "hauptRufnummer", true);
        HSSFCellTools.createCell(row, startCol++, kunde, "email", true);

        return startCol;
    }

    /* Schreibt die Auftrags-Daten in eine Excel-Zeile.
     * @param row
     * @param view
     * @param startWithCol
     * @return Anzahl der bereits enthaltenen Zellen in der Zeile 'row'
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private int writeAuftrag(HSSFRow row, KubenaResultView view, int startWithCol) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        int startCol = startWithCol;
        HSSFCellTools.createCell(row, startCol++, view, "auftragId", false);
        HSSFCellTools.createCell(row, startCol++, view, "vbz", false);
        HSSFCellTools.createCell(row, startCol++, view, "endstelleA", false);
        HSSFCellTools.createCell(row, startCol++, view, "endstelleAName", false);
        HSSFCellTools.createCell(row, startCol++, view, "endstelleAPLZ", false);
        HSSFCellTools.createCell(row, startCol++, view, "endstelleAOrt", false);
        HSSFCellTools.createCell(row, startCol++, view, "endstelleB", false);
        HSSFCellTools.createCell(row, startCol++, view, "endstelleBName", false);
        HSSFCellTools.createCell(row, startCol++, view, "endstelleBPLZ", false);
        HSSFCellTools.createCell(row, startCol++, view, "endstelleBOrt", false);
        HSSFCellTools.createCell(row, startCol++, view, "produkt", false);
        return startCol;
    }

    /* Schreibt Informationen aus den AuftragsDaten in eine Excel-Zeile
     * @param row
     * @param ad
     * @param startCol
     * @return Anzahl der bereits enthaltenen Zellen in der Zeile 'row'
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private int writeAuftragDaten(HSSFRow row, AuftragDaten ad, int startCol) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        int startCol1 = startCol;
        startCol1++;
        if (ad != null) {
            HSSFCellTools.createCell(row, startCol1, ad, "bestellNr", false);
        }
        return startCol1;
    }

    /* Schreibt die Ansprechpartner-Daten in eine Excel-Zeile.
     * @param row
     * @param ansp
     * @param startWithCol
     * @return Anzahl der bereits enthaltenen Zellen in der Zeile 'row'
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private int writeAnsprechpartner(HSSFRow row, Ansprechpartner ansp, int startWithCol) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        int startCol = startWithCol;
        HSSFCellTools.createCell(row, startCol++, ansp, "typ", true);
        HSSFCellTools.createCell(row, startCol++, ansp, "titel", true);
        HSSFCellTools.createCell(row, startCol++, ansp, "vorname", true);
        HSSFCellTools.createCell(row, startCol++, ansp, "name", true);
        HSSFCellTools.createCell(row, startCol++, ansp, "rufnummer", true);
        HSSFCellTools.createCell(row, startCol++, ansp, "fax", true);
        HSSFCellTools.createCell(row, startCol++, ansp, "email", true);
        HSSFCellTools.createCell(row, startCol++, ansp, "info", true);

        return startCol;
    }

    /*
     * Ermittelt die Anrede abhaengig von key und anredeArt.
     */
    private Anrede getAnrede(final String key, final Long anredeArt) {
        if (anreden == null) {
            anreden = getAnredeDAO().findAll(Anrede.class);
        }

        if (anreden != null) {
            Object result = CollectionUtils.find(anreden, new Predicate() {
                public boolean evaluate(Object obj) {
                    if (obj instanceof Anrede) {
                        Anrede anrede = (Anrede) obj;
                        if (StringUtils.equals(anrede.getAnredeKey(), key) &&
                                NumberTools.equal(anrede.getAnredeArt(), anredeArt)) {
                            return true;
                        }
                    }
                    return false;
                }
            });

            return (result instanceof Anrede) ? (Anrede) result : null;
        }
        return null;
    }

    /**
     * @return Returns the kubenaValidator.
     */
    public AbstractValidator getKubenaValidator() {
        return kubenaValidator;
    }

    /**
     * @param kubenaValidator The kubenaValidator to set.
     */
    public void setKubenaValidator(AbstractValidator kubenaValidator) {
        this.kubenaValidator = kubenaValidator;
    }

    /**
     * @return Returns the anredeDAO.
     */
    public AnredeDAO getAnredeDAO() {
        return anredeDAO;
    }

    /**
     * @param anredeDAO The anredeDAO to set.
     */
    public void setAnredeDAO(AnredeDAO anredeDAO) {
        this.anredeDAO = anredeDAO;
    }

    /*
     * Predicate, um nach den Ansprechpartnern zu einem best. Kunden zu filtern.
     */
    static class KundenAnspPredicate implements Predicate {
        private Long kundeNo = null;

        /**
         * Setzt die Kundennummer, nach der gefiltert werden soll.
         *
         * @param kundeNo
         */
        protected void setKundeNo(Long kundeNo) {
            this.kundeNo = kundeNo;
        }

        /**
         * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
         */
        public boolean evaluate(Object obj) {
            return (obj instanceof Ansprechpartner) &&
                    NumberTools.equal(((Ansprechpartner) obj).getKundeNo(), this.kundeNo);
        }

    }

}





