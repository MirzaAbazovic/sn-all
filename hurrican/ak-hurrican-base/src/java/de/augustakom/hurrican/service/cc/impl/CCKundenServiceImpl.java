/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.07.2004 13:34:37
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.DeleteDAO;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.common.tools.reports.jasper.AKJasperReportContext;
import de.augustakom.common.tools.reports.jasper.AKJasperReportHelper;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AnredeDAO;
import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.dao.cc.CCAddressDAO;
import de.augustakom.hurrican.dao.cc.CCAuftragViewDAO;
import de.augustakom.hurrican.dao.cc.KundeNbzDAO;
import de.augustakom.hurrican.dao.cc.ProduktDAO;
import de.augustakom.hurrican.dao.cc.TechLeistungDAO;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.model.billing.view.EndstelleAnsprechpartnerView;
import de.augustakom.hurrican.model.cc.Anrede;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.KundeNbz;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
import de.augustakom.hurrican.model.shared.view.Anschrift4ExportView;
import de.augustakom.hurrican.model.shared.view.AnschriftView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.RechnungsService;
import de.augustakom.hurrican.service.billing.utils.BillingServiceFinder;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.impl.reportdata.CCAuftragViewJasperDS;

/**
 * Service-Implementierung von <code>CCKundenService</code>
 *
 *
 */
@CcTxRequired
public class CCKundenServiceImpl extends DefaultCCService implements CCKundenService {

    private static final Logger LOGGER = Logger.getLogger(CCKundenServiceImpl.class);

    private AbstractValidator bankverbindungValidator = null;
    private AbstractValidator rechnungsanschriftValidator = null;
    private AbstractValidator ccAddressValidator = null;
    private CCAuftragViewDAO auftragViewDAO = null;
    private AuftragDatenDAO auftragDatenDAO = null;
    private AnredeDAO anredeDAO = null;
    private CCAddressDAO ccAddressDAO = null;
    private KundeNbzDAO ccKundeNbzDAO = null;
    private ProduktDAO produktDAO = null;
    private TechLeistungDAO techLeistungDAO = null;

    @Override
    public List<CCKundeAuftragView> findKundeAuftragViews4Kunde(Long kundeNo, boolean excludeKonsolidiert)
            throws FindException {
        return findKundeAuftragViews4Kunde(kundeNo, false, false, excludeKonsolidiert);
    }

    @Override
    public List<CCKundeAuftragView> findKundeAuftragViews4Kunde(Long kundeNo, boolean excludeInvalid,
            boolean excludeKonsolidiert) throws FindException {
        return findKundeAuftragViews4Kunde(kundeNo, false, excludeInvalid, excludeKonsolidiert);
    }

    private List<CCKundeAuftragView> findKundeAuftragViews4Kunde(Long kundeNo, boolean orderByBuendel,
            boolean excludeInvalid, boolean excludeKonsolidiert) throws FindException {
        if (kundeNo == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            List<CCKundeAuftragView> result = auftragViewDAO.findAuftragViews4Kunde(
                    kundeNo, orderByBuendel, excludeInvalid, excludeKonsolidiert);

            if (CollectionTools.isNotEmpty(result)) {
                List<Long> auftragIds = new ArrayList<Long>();
                for (CCKundeAuftragView view : result) {
                    if (StringUtils.isNotBlank(view.getProduktNamePattern())) {
                        auftragIds.add(view.getAuftragId());
                    }
                }

                List<Produkt> produkte = produktDAO.findAll(Produkt.class);
                Map<Long, Produkt> produktMap = new HashMap<Long, Produkt>();
                for (Produkt produkt : produkte) {
                    produktMap.put(produkt.getId(), produkt);
                }

                ProduktService produktService = getCCService(ProduktService.class);
                CCLeistungsService leistungsService = getCCService(CCLeistungsService.class);
                Map<Long, List<TechLeistung>> techLeistungen =
                        leistungsService.findTechLeistungen4Auftraege(auftragIds, null, true);
                for (CCKundeAuftragView view : result) {
                    if (StringUtils.isNotBlank(view.getProduktNamePattern())) {
                        String name = produktService.generateProduktName(produktMap.get(view.getProduktId()),
                                techLeistungen.get(view.getAuftragId()));
                        view.setProduktName(name);
                    }
                }
            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean hasAuftrag(Long kundeNo, Long prodId) throws FindException {
        if ((kundeNo == null) || (prodId == null)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            List<Long> auftraege = getAuftragDatenDAO().findAuftragIds(kundeNo, prodId);
            return ((auftraege != null) && (!auftraege.isEmpty()));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Long> findActiveAuftragInProdGruppe(Long kundeNo, List<Long> produktGruppen) throws FindException {
        if ((kundeNo == null) || CollectionTools.isEmpty(produktGruppen)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            return getAuftragDatenDAO().findAuftragIdsInProduktGruppe(kundeNo, produktGruppen);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean hasActiveAuftragInProdGruppe(Long kundeNo, List<Long> produktGruppen) throws FindException {
        if ((kundeNo == null) || (produktGruppen == null) || (produktGruppen.isEmpty())) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            List<Long> auftragIds = findActiveAuftragInProdGruppe(kundeNo, produktGruppen);
            return ((auftragIds != null) && (!auftragIds.isEmpty()));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public JasperPrint reportKundeAuftragViews(Long kundeNo) throws AKReportException {
        try {
            List<CCKundeAuftragView> data = findKundeAuftragViews4Kunde(kundeNo, true, false);

            KundenService ks = getBillingService(KundenService.class.getName(), KundenService.class);
            Kunde kunde = ks.findKunde(kundeNo);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("KUNDE_NO_ORIG", kundeNo);
            if (kunde != null) {
                params.put("KUNDE_NAME", kunde.getName());
                params.put("KUNDE_VORNAME", kunde.getVorname());
                List<CCKundeAuftragView> views4Details = new ArrayList<CCKundeAuftragView>(data);
                params.put("AUFTRAG_DETAILS_DS", new CCAuftragViewJasperDS(views4Details));
            }

            data.add(0, new CCKundeAuftragView()); // sonst wird 1. Auftrag nicht angezeigt!

            AKJasperReportContext ctx = new AKJasperReportContext(
                    "de/augustakom/hurrican/reports/kunde/KundeUebersichtMaster.jasper",
                    params, new CCAuftragViewJasperDS(data));

            AKJasperReportHelper jrh = new AKJasperReportHelper();
            return jrh.createReport(ctx);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Der Report mit den Auftragsdaten des Kunden konnte nicht erstellt werden!", e);
        }
    }

    @Override
    public Anrede findAnrede(String anredeKey, Long anredeArt) throws FindException {
        if ((anredeKey == null) || (anredeArt == null)) {
            return null;
        }
        try {
            Anrede example = new Anrede();
            example.setAnredeKey(anredeKey);
            example.setAnredeArt(anredeArt);
            List<Anrede> result = getAnredeDAO().queryByExample(example, Anrede.class);
            return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AnschriftView findREAnschrift(Long kundeNo, Long rinfoNoOrig) throws FindException {
        try {
            RechnungsService rs = getBillingService(RechnungsService.class);
            RInfo rinfo = rs.findRInfo(rinfoNoOrig);
            Adresse adresse = null;

            if ((rinfo != null) && (rinfo.getAdresseNo() != null)) {
                KundenService bks = getBillingService(KundenService.class);
                adresse = bks.getAdresseByAdressNo(rinfo.getAdresseNo());
            }

            if (adresse != null) {
                return createAnschriftView(adresse);
            }
            else {
                throw new FindException("Es konnte keine Rechnungs-Adresse zu dem Kunden ermittelt werden!");
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            throw new FindException("Anschrift konnte fuer Kunde nicht ermittelt werden!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.billing.KundenService#findAnschrift(java.lang.Integer)
     */
    @Override
    public AnschriftView findAnschrift(Long kundeNo) throws FindException {
        try {
            KundenService bks = getBillingService(KundenService.class);
            Kunde kunde = bks.findKunde(kundeNo);
            if ((kunde == null) || (kunde.getKundeNo() == null)) {
                throw new FindException("Kunde konnte nicht ermittelt werden!");
            }

            Adresse adresse = bks.getAdresse4Kunde(kunde.getKundeNo());
            if (adresse != null) {
                return createAnschriftView(adresse);
            }
            else {
                throw new FindException("Anschrift konnte fuer Kunde nicht ermittelt werden!");
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            throw new FindException("Anschrift konnte fuer Kunde nicht ermittelt werden!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.CCKundenService#findAnschrift4Export(de.augustakom.hurrican.model.billing.Kunde)
     */
    @Override
    public Anschrift4ExportView findAnschrift4Export(Kunde kunde) throws FindException {
        try {
            KundenService bks = getBillingService(KundenService.class);
            if ((kunde == null) || (kunde.getKundeNo() == null)) {
                throw new FindException("Kunde konnte nicht ermittelt werden!");
            }

            String anrede = null;
            Adresse adresse = bks.getAdresse4Kunde(kunde.getKundeNo());
            if (adresse != null) {
                Anrede adressanrede = findAnrede(adresse.getAnrede(), Anrede.ANREDEART_ANSPRACHE_SHORT);
                if ((adressanrede != null) &&
                        NumberTools.notEqual(adressanrede.getId(), Anrede.ANREDE_ID_KEINE_ANREDE)) {
                    anrede = adressanrede.getAnrede();
                }
            }
            else {
                throw new FindException("Anschrift konnte fuer Kunde nicht ermittelt werden!");
            }

            Anschrift4ExportView view = new Anschrift4ExportView();
            view.setAnrede(anrede);
            view.setFamilienname(adresse.getName());
            view.setVorname(adresse.getVorname());
            view.setKundeNo(kunde.getKundeNo());
            view.setNummer(adresse.getNummer());
            view.setOrt(adresse.getCombinedOrtOrtsteil());
            view.setPlz(adresse.getPlzTrimmed());
            view.setStrasse(adresse.getStrasse());
            view.setKundentyp(kunde.getKundenTyp());
            return view;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            throw new FindException("Anschrift konnte fuer Kunde nicht ermittelt werden!");
        }
    }

    /**
     * Erstellt die AnschriftView zu einer best. Adresse.
     *
     * @param adresse
     * @return
     * @throws FindException
     */
    @Override
    public AnschriftView createAnschriftView(Adresse adresse) throws FindException {
        AnschriftView view = new AnschriftView();
        Anrede adressanrede = findAnrede(adresse.getFormat(), Anrede.ANREDEART_ADRESSE);
        if ((adressanrede == null) || NumberTools.equal(adressanrede.getId(), Anrede.ANREDE_ID_KEINE_ANREDE)) {
            String keyTmp = (adressanrede != null) ? adressanrede.getAnredeKey() : null;
            adressanrede = new Anrede();
            adressanrede.setId(Anrede.ANREDE_ID_KEINE_ANREDE);
            adressanrede.setAnredeKey(keyTmp);
        }
        String key = adressanrede.getAnredeKey();

        String anschrift1 = "";
        String anschrift2 = "";
        String anschrift3 = "";
        String anschrift4 = "";

        // Anschrift 1
        if (Anrede.isValidAnredeKey(key)) {
            if (StringUtils.equals(key, Anrede.KEY_FIRMA) && StringUtils.isBlank(adresse.getVorname())) {
                if (StringUtils.isBlank(adresse.getAnsprechpartner())) {
                    anschrift1 = "";
                }
                else {
                    anschrift1 = adressanrede.getAnrede();
                }
            }
        }
        else {
            anschrift1 = "";
        }

        // Anschrift 2
        if (Anrede.isValidAnredeKey(key)) {
            if (!StringUtils.equals(key, Anrede.KEY_KEINE)) {
                if (StringUtils.isNotBlank(adresse.getVorname())) {
                    anschrift2 = adressanrede.getAnrede();
                    if (StringUtils.isNotBlank(adresse.getTitel())) {
                        anschrift2 += (" " + adresse.getTitel());
                    }
                }
            }
            else if (StringUtils.equals(key, Anrede.KEY_FIRMA)) {
                if (StringUtils.isBlank(adresse.getVorname())) {
                    anschrift2 = (StringUtils.isBlank(adresse.getAnsprechpartner()))
                            ? "" : adressanrede.getAnrede();
                }
                else {
                    if (StringUtils.isBlank(adresse.getAnsprechpartner())) {
                        anschrift2 = adressanrede.getAnrede();
                    }
                    else {
                        anschrift2 = adresse.getVorname();
                    }
                }
            }
        }
        else {
            anschrift2 = (StringUtils.isBlank(adresse.getAnsprechpartner()))
                    ? "" : adressanrede.getAnrede();
        }

        // Anschrift 3
        if (StringTools.isIn(key, new String[] { Anrede.KEY_HERR, Anrede.KEY_FRAU, Anrede.KEY_KEINE })) {
            anschrift3 = adresse.getName();
        }
        else if (StringTools.isIn(key, new String[] { Anrede.KEY_HERRFRAU, Anrede.KEY_FRAUHERR })) {
            anschrift3 = StringUtils.join(new Object[] { adresse.getName(), " ", adresse.getVorname(), " und" });
        }
        else if (StringUtils.equals(key, Anrede.KEY_FIRMA)) {
            if (StringUtils.isBlank(adresse.getVorname())) {
                anschrift3 = StringUtils.isBlank(adresse.getAnsprechpartner())
                        ? adressanrede.getAnrede() : adresse.getName();
            }
            else {
                anschrift3 = adresse.getName();
            }
        }
        else {
            anschrift3 = StringUtils.isBlank(adresse.getAnsprechpartner())
                    ? adresse.getName() : adresse.getVorname();
        }

        // Anschrift 4
        if (StringTools.isIn(key, new String[] { Anrede.KEY_HERR, Anrede.KEY_FRAU, Anrede.KEY_KEINE })) {
            anschrift4 = adresse.getVorname();
        }
        else if (StringTools.isIn(key, new String[] { Anrede.KEY_HERRFRAU, Anrede.KEY_FRAUHERR })) {
            anschrift4 = StringUtils.join(new Object[] { adresse.getName2(), " ", adresse.getVorname2() });
        }
        else if (StringUtils.equals(key, Anrede.KEY_FIRMA)) {
            if (StringUtils.isBlank(adresse.getVorname())) {
                anschrift4 = (StringUtils.isBlank(adresse.getAnsprechpartner()))
                        ? adresse.getName() : adresse.getAnsprechpartner();
            }
            else {
                anschrift4 = adresse.getVorname();
            }
        }
        else {
            anschrift4 = StringUtils.isBlank(adresse.getAnsprechpartner())
                    ? adresse.getVorname() : adresse.getAnsprechpartner();
        }

        // Strasse/Postfach
        String strasse = null;
        if (StringUtils.isBlank(adresse.getPostfach())) {
            strasse = adresse.getStrasse();
        }
        else {
            strasse = "Postfach " + adresse.getPostfach();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("####### Ermittlung Adress-Anschrift ###########");
            LOGGER.debug(" anschrift1: " + anschrift1);
            LOGGER.debug(" anschrift2: " + anschrift2);
            LOGGER.debug(" anschrift3: " + anschrift3);
            LOGGER.debug(" anschrift4: " + anschrift4);
            LOGGER.debug(" strasse   : " + strasse);
            LOGGER.debug(" plz ort   : " + adresse.getPlzTrimmed() + " " + adresse.getCombinedOrtOrtsteil());
        }

        view.setAnschrift1(anschrift1);
        view.setAnschrift2(anschrift2);
        view.setAnschrift3(anschrift3);
        view.setAnschrift4(anschrift4);
        view.setStrasse(strasse);
        view.setPlz(adresse.getPlzTrimmed());
        view.setOrt(adresse.getCombinedOrtOrtsteil());
        view.setAdresseNo(adresse.getAdresseNo());
        return view;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.CCKundenService#findCCAddress(java.lang.Long)
     */
    @Override
    public CCAddress findCCAddress(Long id) throws FindException {
        if (id == null) {
            return null;
        }
        try {
            return getCcAddressDAO().findById(id, CCAddress.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public CCAddress saveCCAddress(CCAddress toSave) throws StoreException, ValidationException {
        if (toSave == null) {
            throw new StoreException(
                    StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            ValidationException ve = new ValidationException(toSave, "CCAddress");
            getCcAddressValidator().validate(toSave, ve);
            if (ve.hasErrors()) {
                throw ve;
            }

            return getCcAddressDAO().store(toSave);
        }
        catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public CCAddress getCCAddress4BillingAddress(Adresse bAdresse) throws FindException {
        if (bAdresse == null) {
            return null;
        }
        try {
            // Ermittle kundeNoOrig aus Kunden-Objekt
            KundenService service = BillingServiceFinder.instance().getBillingService(KundenService.class);
            Kunde kunde = service.findKunde(bAdresse.getKundeNo());

            CCAddress address = new CCAddress();
            address.setKundeNo((kunde != null) ? kunde.getKundeNo() : null);
            address.setAddressType(CCAddress.ADDRESS_TYPE_VARIOUS);
            address.setFormatName(StringUtils.trimToNull(bAdresse.getFormatName()));
            address.setName(StringUtils.trimToNull(bAdresse.getName()));
            address.setName2(StringUtils.trimToNull(bAdresse.getName2()));
            address.setVorname(StringUtils.trimToNull(bAdresse.getVorname()));
            address.setVorname2(StringUtils.trimToNull(bAdresse.getVorname2()));
            address.setStrasse(StringUtils.trimToNull(bAdresse.getStrasse()));
            address.setStrasseAdd(StringUtils.trimToNull(bAdresse.getFloor()));
            address.setNummer(StringUtils.trimToNull(bAdresse.getNummer()));
            address.setPostfach(StringUtils.trimToNull(bAdresse.getPostfach()));
            address.setPlz(StringUtils.trimToNull(bAdresse.getPlz()));
            address.setOrt(StringUtils.trimToNull(bAdresse.getOrt()));
            address.setOrtsteil(StringUtils.trimToNull(bAdresse.getOrtsteil()));
            address.setLandId(StringUtils.trimToNull(bAdresse.getLandId()));
            address.setHausnummerZusatz(StringUtils.trimToNull(bAdresse.getHausnummerZusatz()));
            if (StringUtils.isNotBlank(address.getHausnummerZusatz())
                    && (address.getHausnummerZusatz().length() > CCAddress.MAX_LENGTH_HAUSNUMMER_ZUSATZ)) {
                address.setHausnummerZusatz(address.getHausnummerZusatz().substring(0,
                        CCAddress.MAX_LENGTH_HAUSNUMMER_ZUSATZ));
            }
            address.setPrioBrief(1);
            address.setPrioFax(1);
            address.setPrioEmail(1);
            address.setPrioSMS(1);
            address.setPrioTel(1);

            return address;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public CCAddress getCCAddressAnsprechpartner(CCAddress address, EndstelleAnsprechpartnerView auftragAnsprechpartner) {
        if (auftragAnsprechpartner == null) {
            return null;
        }

        CCAddress adrAnsprechpartner = CCAddress.getCopy(address);
        adrAnsprechpartner.setAddressType(CCAddress.ADDRESS_TYPE_CUSTOMER_CONTACT);
        adrAnsprechpartner.setName(auftragAnsprechpartner.getName());
        adrAnsprechpartner.setVorname(auftragAnsprechpartner.getVorname());
        adrAnsprechpartner.setFormatName(CCAddress.ADDRESS_FORMAT_RESIDENTIAL);
        adrAnsprechpartner.setTelefon(auftragAnsprechpartner.getTelefon());
        adrAnsprechpartner.setHandy(auftragAnsprechpartner.getMobil());
        adrAnsprechpartner.setEmail(auftragAnsprechpartner.getEmail());

        return adrAnsprechpartner;
    }

    @Override
    public List<CCAddress> findCCAddresses(Long type, String vornameIn, String nameIn, String ortIn, String strasseIn)
            throws FindException {
        String vorname = vornameIn;
        String name = nameIn;
        String ort = ortIn;
        String strasse = strasseIn;
        try {
            // Es wird eine Map genutzt um die Resultate zu aggregieren, damit
            // Elemente mit der gleichen ID im Endresultat nur einmal auftauchen.
            Map<Long, CCAddress> result = new HashMap<Long, CCAddress>();
            if (StringUtils.isEmpty(vorname)) {
                vorname = null;
            }
            else {
                vorname = WildcardTools.replaceWildcards(vorname);
            }
            if (StringUtils.isEmpty(name)) {
                name = null;
            }
            else {
                name = WildcardTools.replaceWildcards(name);
            }
            if (StringUtils.isEmpty(ort)) {
                ort = null;
            }
            else {
                ort = WildcardTools.replaceWildcards(ort);
            }
            if (StringUtils.isEmpty(strasse)) {
                strasse = null;
            }
            else {
                strasse = WildcardTools.replaceWildcards(strasse);
            }
            if ((vorname == null) && (name == null) && (ort == null) && (strasse == null)) {
                return new ArrayList<CCAddress>(0);
            }
            CCAddress example = new CCAddress();
            example.setAddressType(type);
            example.setName(name);
            example.setVorname(vorname);
            example.setOrt(ort);
            example.setStrasse(strasse);
            for (CCAddress address : getCcAddressDAO().queryByExampleLike(example, CCAddress.class)) {
                result.put(address.getId(), address);
            }
            example.setName(null);
            example.setVorname(null);
            example.setName2(name);
            example.setVorname2(vorname);
            for (CCAddress address : getCcAddressDAO().queryByExampleLike(example, CCAddress.class)) {
                result.put(address.getId(), address);
            }
            return new ArrayList<CCAddress>(result.values());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<KundeNbz> findKundeNbzByNbz(String nbz) throws FindException {
        return getCcKundeNbzDAO().findKundeNbzByNbz(nbz);
    }

    @Override
    public KundeNbz findKundeNbzByNo(Long kundeNo) throws FindException {
        return getCcKundeNbzDAO().findKundeNbzByNo(kundeNo);
    }

    @Override
    public void removeKundeNbz(Long id) throws DeleteException {
        ((DeleteDAO) getCcKundeNbzDAO()).deleteById(id);
    }

    @Override
    public void saveKundeNbz(KundeNbz kundeNbz) throws StoreException {
        if (kundeNbz == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        getCcKundeNbzDAO().saveKundeNbz(kundeNbz);
    }

    @Override
    public List<CCKundeAuftragView> findKundeAuftragViews4Address(Long addressId) throws FindException {
        if (addressId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            return auftragViewDAO.findKundeAuftragViews4Address(addressId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @return Returns the techLeistungDAO.
     */
    public TechLeistungDAO getTechLeistungDAO() {
        return techLeistungDAO;
    }

    /**
     * @param techLeistungDAO The techLeistungDAO to set.
     */
    public void setTechLeistungDAO(TechLeistungDAO techLeistungDAO) {
        this.techLeistungDAO = techLeistungDAO;
    }

    /**
     * @return Returns the produktDAO.
     */
    public ProduktDAO getProduktDAO() {
        return produktDAO;
    }

    /**
     * @param produktDAO The produktDAO to set.
     */
    public void setProduktDAO(ProduktDAO produktDAO) {
        this.produktDAO = produktDAO;
    }

    /**
     * @return Returns the auftragViewDAO.
     */
    protected CCAuftragViewDAO getAuftragViewDAO() {
        return auftragViewDAO;
    }

    /**
     * @param auftragViewDAO The auftragViewDAO to set.
     */
    public void setAuftragViewDAO(CCAuftragViewDAO auftragViewDAO) {
        this.auftragViewDAO = auftragViewDAO;
    }

    /**
     * @return Returns the rechnungsanschriftValidator.
     */
    protected AbstractValidator getRechnungsanschriftValidator() {
        return rechnungsanschriftValidator;
    }

    /**
     * @param rechnungsanschriftValidator The rechnungsanschriftValidator to set.
     */
    public void setRechnungsanschriftValidator(AbstractValidator rechnungsanschriftValidator) {
        this.rechnungsanschriftValidator = rechnungsanschriftValidator;
    }

    /**
     * @return Returns the bankverbindungValidator.
     */
    protected AbstractValidator getBankverbindungValidator() {
        return bankverbindungValidator;
    }

    /**
     * @param bankverbindungValidator The bankverbindungValidator to set.
     */
    public void setBankverbindungValidator(AbstractValidator bankverbindungValidator) {
        this.bankverbindungValidator = bankverbindungValidator;
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
     * @return Returns the ccAddressDAO.
     */
    public CCAddressDAO getCcAddressDAO() {
        return ccAddressDAO;
    }

    /**
     * @param ccAddressDAO The ccAddressDAO to set.
     */
    public void setCcAddressDAO(CCAddressDAO ccAddressDAO) {
        this.ccAddressDAO = ccAddressDAO;
    }

    /**
     * @return Returns the ccAddressValidator.
     */
    public AbstractValidator getCcAddressValidator() {
        return ccAddressValidator;
    }

    /**
     * @param ccAddressValidator The ccAddressValidator to set.
     */
    public void setCcAddressValidator(AbstractValidator ccAddressValidator) {
        this.ccAddressValidator = ccAddressValidator;
    }

    /**
     * @return the ccKundeNbzDAO
     */
    public KundeNbzDAO getCcKundeNbzDAO() {
        return ccKundeNbzDAO;
    }

    /**
     * @param ccKundeNbzDAO the ccKundeNbzDAO to set
     */
    public void setCcKundeNbzDAO(KundeNbzDAO ccKundeNbzDAO) {
        this.ccKundeNbzDAO = ccKundeNbzDAO;
    }
}
