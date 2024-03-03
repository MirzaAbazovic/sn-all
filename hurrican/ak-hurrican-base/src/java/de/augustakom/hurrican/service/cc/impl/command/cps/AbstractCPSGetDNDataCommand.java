/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.04.2009 09:53:32
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.EnumTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSDNServiceData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.ICPSDNServiceAwareModel;
import de.augustakom.hurrican.model.cc.dn.DNLeistungsView;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;

/**
 * Abstrakte Command-Klasse, um Rufnummern-Daten zu ermitteln.
 *
 *
 */
public abstract class AbstractCPSGetDNDataCommand extends AbstractCPSDataCommand {

    private static final Logger LOGGER = Logger.getLogger(AbstractCPSGetDNDataCommand.class);

    private static final String DN_SERVICE_ACTIVE = "1";

    protected RufnummerService rufnummerService;
    protected LeistungService leistungService;
    protected CCRufnummernService ccRufnummernService;

    /**
     * Called by Spring
     *
     * @throws ServiceNotFoundException
     */
    @Override
    public void init() throws ServiceNotFoundException {
        super.init();
        setRufnummerService(getBillingService(RufnummerService.class));
        setLeistungService(getBillingService(LeistungService.class));
    }

    /**
     * Ermittelt alle Rufnummern, die zum Zeitpunkt der Provisionierung auf dem Auftrag aktiv sind. <br> <br> Dies
     * geschieht nach folgender Logik: <br> <ul> <li>Provisionierungszeitpunkt = ExecutionTime + 1 Tag (temporaer)
     * <li>Rufnummern mit Status AKT/ALT und NEU werden zum Auftrag ermittelt <li>Rufnummer mit Status AKT/ALT und
     * gueltig-bis unendlich (31.12.9999) erhaelt als neues gueltig-bis Datum das gueltig-von Datum der zugehoerigen
     * Rufnummer mit Status NEU (sofern vorhanden) <li>Rufnummern werden mit temp. Provisionierungszeitpunkt verglichen
     * ueber gueltig-von <= tmpExecDate && gueltig-bis > tmpExecDate </ul> <br>
     *
     * @param orderNoOrig ID des Billing-Auftrags dessen aktive(!) Rufnummern ermittelt werden sollen.
     * @return Liste mit den zum Provisionierungszeitpunkt aktiven Rufnummern.
     * @throws ServiceCommandException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public List<Rufnummer> getActiveDNs(Long orderNoOrig) throws ServiceCommandException {
        try {
            if (!getCPSTransaction().isCancelSubscriber()) {
                List<Rufnummer> dns = rufnummerService.findRNs4Auftrag(orderNoOrig, getCPSTransaction()
                        .getEstimatedExecTime());

                List<Rufnummer> dnsForProvisioning = null;
                if (CollectionTools.isNotEmpty(dns)) {
                    // Datum wird einen Tag in die Zukunft gesetzt, um weg-portierte Rufnummern zu erkennen!
                    // (Bei abgehenden Portierungen wird die Rufnummer erst einen Tag nach der Kuendigung portiert!)
                    Date modifiedExecDate = (getServiceOrderData().isInitialLoad()) ? getCPSTransaction()
                            .getEstimatedExecTime() : DateTools.changeDate(getCPSTransaction().getEstimatedExecTime(),
                            Calendar.DAY_OF_MONTH, 1);

                    // Rufnummern filtern die zu 'modifiedExecDate' gueltig sind
                    dnsForProvisioning = filterDNsByExecutionDate(dns, getCPSTransaction().getOrderNoOrig(),
                            getCPSTransaction().getEstimatedExecTime(), modifiedExecDate);

                    // Rufnummern filtern, die vom Typ 'ROUTING'
                    dnsForProvisioning = filterRoutings(dnsForProvisioning);

                    // Rufnummern filtern, die die Leistung 'AGRU' besitzen
                    dnsForProvisioning = filterAGRU(dnsForProvisioning);

                    // Rufnummern mit 'Ansage kein Anschluss' heraus filtern
                    dnsForProvisioning = filterKeinAnschluss(dnsForProvisioning);
                }

                if (getServiceOrderData().isInitialLoad() && CollectionTools.isEmpty(dnsForProvisioning)) {
                    dnsForProvisioning = getLastActiveDNsForOrder(orderNoOrig);
                }

                return dnsForProvisioning;
            }

            // bei UseCase 'cancelSubscriber' werden keine Rufnummern ermittelt!
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error while loading active DNs: " + e.getMessage(), e);
        }
    }

    /*
     * Filtert die Rufnummern ueber das angegebene Datum (ist bereits ExecDate + 1 Tag). <br> Die Filterung der
     * Rufnummern erfolgt dann nach folgender Logik: <br> </ul> <li>NEU Rufnummer vorhanden (offene Historisierung) <li>
     * - auch AKT Rufnummer mit GueltigBis=unendlich vorhanden <li> --> AKT Rufnummer erhaelt als GueltigBis das
     * GueltigVon der NEU Rufnummer <li> ----> pruefen, ob AKT oder NEU in Provisionierungszeitraum faellt (von<=exec+1
     * && bis>exec+1) <li>keine NEU Rufnummer vorhanden (kein ohne abgeschlossene Historisierung) <li> ----> pruefen, ob
     * AKT Rufnummer in Provisionierungszeitraum faellt (von<=exec+1 && bis>exec+1) <li>ALT Rufnummer vorhanden
     * (abgeschlossene Historisierung) <li> ----> pruefen, ob ALT Rufnummer in Provisionierungszeitraum faellt
     * (von<=exec && bis>=exec) </ul> <br> Die umstaendliche Datumsersetzung bei AKT / NEU ist wg. dem
     * Historisierungskonzept von Taifun notwendig. Im Falle einer 'offenen' Historisierung besitzen naemlich sowohl die
     * AKT als auch die NEU Rufnummer als GueltigBis Datum den '31.12.9999' (also unendlich). Somit wuerden bei der
     * Datumspruefung mit 'von<exec+1 && bis>exec+1' beide Rufnummern die Kriterien erfuellen. Es soll jedoch wirklich
     * nur die zum Ausfuehrungszeitpunkt gueltige Rufnummer ermittelt werden. <br> Die ALT Rufnummern muessen ebenfalls
     * in die Pruefung mit einbezogen werden. Dies ist dann notwendig, wenn eine Auftragsmutation schon vor der
     * Provisionierung aktiviert wird. In diesem Fall wird auch schon die Historisierung der Rufnummern aktiviert. <br>
     *
     * @param dnsToFilter zu filternde Rufnummern
     *
     * @param estimatedExecDate Ausfuehrungszeitpunkt der CPS-Tx
     *
     * @param dateToCheck zu pruefendes Datum (ExecDate + 1 Tag)
     *
     * @return Liste mit den zum Provisionierungszeitpunkt gueltigen Rufnummern
     */
    List<Rufnummer> filterDNsByExecutionDate(List<Rufnummer> dnsToFilter, Long orderNoOrig, Date estimatedExecDate,
            Date dateToCheck) throws FindException {
        List<Long> unifiedDNNoOrig = new ArrayList<>(); // Liste um die DN__NOs einmalig zu speichern
        Map<Long, Rufnummer> aktDNs = new HashMap<>(); // in Map werden DNs mit Status AKT eingetragen
        Map<Long, Rufnummer> oldDNs = new HashMap<>(); // in Map werden DNs mit Status ALT eingetragen
        Map<Long, Rufnummer> newDNs = new HashMap<>(); // in Map werden DNs mit Status NEU eingetragen
        for (Rufnummer dn : dnsToFilter) {
            if (!unifiedDNNoOrig.contains(dn.getDnNoOrig())) {
                // Rufnummern einmalig in Liste speichern.
                // WICHTIG: durch List und nicht Map bleibt die Sortierung erhalten
                unifiedDNNoOrig.add(dn.getDnNoOrig());
            }

            if (StringUtils.equals(dn.getHistStatus(), BillingConstants.HIST_STATUS_NEU)) {
                newDNs.put(dn.getDnNoOrig(), dn);
            }
            else if (StringUtils.equals(dn.getHistStatus(), BillingConstants.HIST_STATUS_AKT)) {
                aktDNs.put(dn.getDnNoOrig(), dn);
            }
            else if (StringUtils.equals(dn.getHistStatus(), BillingConstants.HIST_STATUS_ALT)) {
                oldDNs.put(dn.getDnNoOrig(), dn);
            }
        }

        List<Rufnummer> filteredDNs = new ArrayList<>();
        for (Long dnNoOrig : unifiedDNNoOrig) {
            filterDN(filteredDNs, dnNoOrig, estimatedExecDate, dateToCheck, orderNoOrig, aktDNs, oldDNs, newDNs);
        }

        return filteredDNs;
    }

    /* Filterung fuer die Rufnummer mit angegebener 'dnNoOrig' */
    void filterDN(List<Rufnummer> filteredDNs, Long dnNoOrig, Date estimatedExecDate, Date dateToCheck,
            Long orderNoOrig, Map<Long, Rufnummer> aktDNs, Map<Long, Rufnummer> oldDNs, Map<Long, Rufnummer> newDNs)
            throws FindException {
        Rufnummer aktDN = aktDNs.get(dnNoOrig);
        Rufnummer newDN = newDNs.get(dnNoOrig);

        if ((aktDN != null) && (newDN == null) && !aktDN.isHistLast()) {
            // Rufnummer mit HIST_LAST=1 und Status NEU laden, um abgehende Portierungen
            // zu erkennen bzw. das GueltigBis Datum der AKT Rufnummer korrekt interpretieren zu koennen
            Rufnummer last4DN = rufnummerService.findLastRN(dnNoOrig);
            if (!StringUtils.equals(last4DN.getState(), BillingConstants.HIST_STATUS_NEU)) {
                newDN = (NumberTools.equal(aktDN.getDnNo(), last4DN.getDnNo())) ? null : last4DN;
            }
        }

        if (newDN != null) { // offene Historisierung vorhanden
            filterDNByAktOrNew(filteredDNs, dateToCheck, orderNoOrig, newDN, aktDN);
        }
        else if (aktDNs.containsKey(dnNoOrig) && (aktDN != null)) {
            if (DateTools.isDateBetween(dateToCheck, aktDN.getGueltigVon(), aktDN.getGueltigBis())) {
                filteredDNs.add(aktDN); // Rufnummer mit Status AKT verwenden
            }
        }
        else if (oldDNs.containsKey(dnNoOrig)) {
            Rufnummer oldDN = oldDNs.get(dnNoOrig);
            // bei ALT Rufnummer nicht auf '<= und >', sondern auf '<= und >=' pruefen
            // ausserdem urspruengliches ExecDate verwenden!
            if (DateTools.isDateBeforeOrEqual(oldDN.getGueltigVon(), estimatedExecDate)
                    && DateTools.isDateAfterOrEqual(oldDN.getGueltigBis(), estimatedExecDate)) {
                filteredDNs.add(oldDN); // Rufnummer mit Status ALT verwenden
            }
        }
    }

    /* Ueberprueft die zukuenftige Historisierung der Rufnummer. */
    void filterDNByAktOrNew(List<Rufnummer> filteredDNs, Date dateToCheck, Long orderNoOrig, Rufnummer newDN,
            Rufnummer aktDN) {
        if ((aktDN != null) && DateTools.isDateEqual(aktDN.getGueltigBis(), DateTools.getBillingEndDate())) {
            // Rufnummer mit AKT u. ValidTo=unendlich vorhanden
            // --> als ValidTo das ValidFrom von NEU verwenden
            if (DateTools.isDateBetween(dateToCheck, aktDN.getGueltigVon(), newDN.getGueltigVon())) {
                filteredDNs.add(aktDN); // Rufnummer mit Status AKT verwenden
            }
            else if (DateTools.isDateBetween(dateToCheck, newDN.getGueltigVon(), newDN.getGueltigBis())
                    && NumberTools.equal(newDN.getAuftragNoOrig(), orderNoOrig)) {
                filteredDNs.add(newDN); // Rufnummer mit Status NEU verwenden
            }
        }
        else {
            if (DateTools.isDateBetween(dateToCheck, newDN.getGueltigVon(), newDN.getGueltigBis())
                    && NumberTools.equal(newDN.getAuftragNoOrig(), orderNoOrig)) {
                filteredDNs.add(newDN); // Rufnummer mit Status NEU verwenden
            }
        }
    }

    /*
     * Filtert aus der angegebenen Liste alle Rufnummern, die vom Typ 'ROUTING' sind oder denen die Leistung 'PORTEDDN'
     * zugeordnet ist.
     *
     * @param toFilter
     *
     * @return
     */
    List<Rufnummer> filterRoutings(List<Rufnummer> toFilter) throws FindException {
        if (CollectionTools.isNotEmpty(toFilter)) {
            List<Rufnummer> filtered = new ArrayList<>();
            for (Rufnummer dnToCheck : toFilter) {
                if (!dnToCheck.isRouting() && !hasActiveDNServiceOfType(dnToCheck, Leistung4Dn.DN_SERVICE_PORTEDDN)) {
                    filtered.add(dnToCheck);
                }
            }
            return filtered;
        }
        return toFilter;
    }

    /* Filtert alle Rufnummer mit Typ 'Ansage kein Anschluss' aus der Liste heraus. */
    List<Rufnummer> filterKeinAnschluss(List<Rufnummer> toFilter) {
        if (CollectionTools.isNotEmpty(toFilter)) {
            List<Rufnummer> filtered = new ArrayList<>();
            for (Rufnummer dnToCheck : toFilter) {
                if (!dnToCheck.isKeinAnschluss()) {
                    filtered.add(dnToCheck);
                }
            }
            return filtered;
        }
        return toFilter;
    }

    /*
     * Filtert aus der angegebenen Liste alle Rufnummern, denen zum Ausfuerhungszeitpunkt der CPS-Tx die Leistung 'AGRU'
     * zugeordnet ist.
     *
     * @param toFilter
     *
     * @return
     */
    List<Rufnummer> filterAGRU(List<Rufnummer> toFilter) throws FindException {
        if (CollectionTools.isNotEmpty(toFilter)) {
            List<Rufnummer> filtered = new ArrayList<>();
            for (Rufnummer dnToCheck : toFilter) {
                if (!hasActiveDNServiceOfType(dnToCheck, Leistung4Dn.DN_SERVICE_AGRU)) {
                    filtered.add(dnToCheck);
                }
            }
            return filtered;
        }
        return toFilter;
    }

    /*
     * Prueft, ob dem Auftrag eine Rufnummernleistung vom angegebenen Typ zum Ausfuehrungszeitpunkt der CPS-Tx
     * zugeordnet ist.
     */
    boolean hasActiveDNServiceOfType(Rufnummer rufnummer, Long dnServiceId) throws FindException {
        Leistung2DN l2dn = getActiveDNServiceOfType(rufnummer, dnServiceId);
        return (l2dn != null);
    }

    /*
     * Ermittelt den Rufnummernleistungs-Eintrag fuer eine bestimmte Rufnummer und eine bestimmte Rufnummernleistung.
     */
    Leistung2DN getActiveDNServiceOfType(Rufnummer rufnummer, Long dnServiceId) throws FindException {
        List<Long> dnServiceIDs = new ArrayList<>();
        dnServiceIDs.add(dnServiceId);

        List<Leistung2DN> activeDNServiceOfType = ccRufnummernService.findLeistung2DN(rufnummer.getDnNo(),
                dnServiceIDs, getCPSTransaction().getEstimatedExecTime());
        return CollectionTools.isNotEmpty(activeDNServiceOfType) ? activeDNServiceOfType.get(0) : null;
    }

    List<Rufnummer> getLastActiveDNsForOrder(Long orderNoOrig) throws FindException {
        List<Rufnummer> allDNs = rufnummerService.findAllRNs4Auftrag(orderNoOrig);
        if (CollectionTools.isNotEmpty(allDNs)) {
            Date lastValidDate = null;
            for (Rufnummer rufnummer : allDNs) {
                if ((lastValidDate == null) || DateTools.isDateAfter(rufnummer.getGueltigBis(), lastValidDate)) {
                    lastValidDate = rufnummer.getGueltigBis();
                }
            }

            // Filter Rufnummern, die GueltigBis=lastValidDate haben
            final Date referenceDate = lastValidDate;
            CollectionUtils.filter(allDNs, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    Rufnummer dnToCheck = (Rufnummer) object;
                    return (DateTools.isDateEqual(dnToCheck.getGueltigBis(), referenceDate));
                }
            });
        }

        return allDNs;
    }

    /**
     * Ermittelt die Rufnummernleistungen zur angegebenen Rufnummer und uebergibt diese an das Ziel-Model
     * <code>destModel</code>. <br> <br> Eine Rufnummernleistung ist dann fuer die Provisionierung relevant, wenn
     * folgende Bedingungen erfuellt sind: <br> <ul> <li>Realisierungsdatum der Leistung <= Provisionierungszeitpunkt
     * <li>Kuendigungsdatum der Leistung == NULL oder Kuendigungsdatum > Provisionierungszeitpunkt </ul> <br> Die zur
     * Provisionierung herangezogenen Leistungen werden ausserdem noch (bei Bedarf) als provisioniert markiert. Auch
     * hier erfolgt (speziell bei Kuendigungen) ein Check ueber Datum und Provisionierungszeitpunkt.
     *
     * @param dn                           Rufnummer, deren Leistungen ermittelt werden sollen
     * @param destModel                    Ziel-Modell, in das die Rufnummernleistungen eingetragen werden sollen
     * @param tryToCreateDefaultDnServices falls {@code true} wird versucht, die Default DN-Services fuer die Rufnummer
     *                                     anzulegen
     * @throws ServiceCommandException wenn bei der Ermittlung der Rufnummernleistungen ein Fehler auftritt.
     *
     */
    protected void loadDNServices(Long auftragId, Rufnummer dn, ICPSDNServiceAwareModel destModel,
            boolean tryToCreateDefaultDnServices, Long sessionId) throws ServiceCommandException {
        try {
            List<Long> dnNos = new ArrayList<>();
            dnNos.add(dn.getDnNo());
            Date execDate = getCPSTransaction().getEstimatedExecTime();

            boolean acceptNoDnService = getServiceOrderData().isInitialLoad();
            Leistungsbuendel lb = ccRufnummernService.findLeistungsbuendel4Auftrag(auftragId, leistungService);
            if ((lb == null) && !acceptNoDnService) {
                throw new HurricanServiceCommandException("DN service definition could not be found for order!");
            }

            List<DNLeistungsView> leistungViews = (lb != null) ? ccRufnummernService
                    .findDNLeistungen(dnNos, lb.getId()) : null;
            if (CollectionTools.isNotEmpty(leistungViews)) {
                Map<String, CPSDNServiceData> dnServiceMap = new HashMap<>();
                unifyAndFilterDnServices(destModel, execDate, leistungViews, dnServiceMap);

                if (dnServiceMap.isEmpty() && !acceptNoDnService) {
                    if (tryToCreateDefaultDnServices) {
                        // versuchen, die Default DN-Services fuer die Rufnummer anzulegen u. Ermittlung erneut
                        // durchfuehren
                        tryToAttachDefaultDnServices(auftragId, dn, sessionId, execDate);
                        loadDNServices(auftragId, dn, destModel, false, sessionId);
                    }
                    else {
                        throw new HurricanServiceCommandException("No DN services found!");
                    }
                }
            }
            else {
                if (!acceptNoDnService) {
                    if (tryToCreateDefaultDnServices) {
                        // versuchen, die Default DN-Services fuer die Rufnummer anzulegen u. Ermittlung erneut
                        // durchfuehren
                        tryToAttachDefaultDnServices(auftragId, dn, sessionId, execDate);
                        loadDNServices(auftragId, dn, destModel, false, sessionId);
                    }
                    else {
                        throw new HurricanServiceCommandException("No DN services found!");
                    }
                }
            }
        }
        catch (HurricanServiceCommandException | FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error while loading DN services: " + e.getMessage(), e);
        }
    }

    /*
     * Versucht, die Default DN Services fuer die Rufnummer anzulegen. Exceptions werden lediglich geloggt, nicht weiter
     * gereicht!
     *
     * @param auftragId
     *
     * @param dn
     *
     * @param sessionId
     *
     * @param execDate
     */
    private void tryToAttachDefaultDnServices(Long auftragId, Rufnummer dn, Long sessionId, Date execDate) {
        try {
            ccRufnummernService.attachDefaultLeistungen2DN(dn, auftragId, execDate, sessionId);
        }
        catch (StoreException e) {
            LOGGER.error(e.getMessage(), e);
            createCPSTxLog(getCPSTransaction(), "Error create default DN services: " + e.getMessage());
        }
    }

    /**
     * Durchlaeuft die DN-Services und ermittelt jeden Typ nur einmal. Ausserdem wird die Liste auf CPS-relevante
     * DN-Services gefiltert.
     *
     * @param destModel
     * @param execDate
     * @param leistungViews
     * @param dnServiceMap
     * @throws ServiceCommandException
     */
    private void unifyAndFilterDnServices(ICPSDNServiceAwareModel destModel, Date execDate,
            List<DNLeistungsView> leistungViews, Map<String, CPSDNServiceData> dnServiceMap)
            throws ServiceCommandException {
        for (DNLeistungsView view : leistungViews) {
            // jede DN-Leistung nur einmal aufnehmen!
            // und pruefen, ob Rufnummernleistung fuer Provisionierung relevant ist
            if (!dnServiceMap.containsKey(view.getProvisioningName())
                    && DateTools.isDateBeforeOrEqual(view.getAmRealisierung(), execDate)
                    && StringUtils.isNotBlank(view.getProvisioningName())) {
                // Leistung an CPS uebergeben, wenn noch nicht gekuendigt
                // bzw. Kuendigung NACH dem Provisionierungszeitpunkt ist
                if ((view.getAmKuendigung() == null) || DateTools.isDateAfter(view.getAmKuendigung(), execDate)) {
                    CPSDNServiceData dnServiceData = new CPSDNServiceData();
                    dnServiceData.setServiceName(view.getProvisioningName());

                    String param = (StringUtils.isNotBlank(view.getParameter())) ? view.getParameter()
                            : DN_SERVICE_ACTIVE;
                    String modifiedParam = StringUtils.replace(param, Leistung2DN.PARAMETER_SEP,
                            CPSDNServiceData.CPS_VALUE_DELIMITER);

                    if (EnumTools.valueOfSilent(CPSDNServiceData.DnServicesMapParameterToBoolean.class,
                            view.getProvisioningName()) != null) {
                        modifiedParam = DN_SERVICE_ACTIVE;
                    }
                    dnServiceData.setServiceValue(modifiedParam);

                    dnServiceMap.put(view.getProvisioningName(), dnServiceData);
                }

                // DN-Leistung als provisioniert markieren
                markDNServiceAsProvisioned(ccRufnummernService, view, getCPSTransaction().getEstimatedExecTime());

                // ermittelte Rufnummernleistungen dem Ziel-Modell uebergeben
                destModel.setDnServices(new ArrayList<>(dnServiceMap.values()));
            }
        }
    }

    /**
     * Markiert die Rufnummernleistung als provisioniert. <br> Dies geschieht nur dann, wenn entweder eine Einrichtung
     * oder Kuendigung noch offen ist. Bei der Kuendigung wird zusaetzlich geprueft, ob das geplante Leistungskuendigung
     * nicht nach dem Provisionierungszeitpunkt liegt. Ist dies der Fall, erfolgt keine(!) Markierung der Leistung als
     * provisioniert.
     *
     * @param rs        zu verwendender Rufnummern-Service
     * @param dnLstView DNLeistungsView ueber den die zu markierende Rufnummernleistung definiert ist.
     * @param realDate  Realisierungs- bzw. Kuendigungsdatum
     */
    private void markDNServiceAsProvisioned(CCRufnummernService rs, DNLeistungsView dnLstView, Date realDate)
            throws ServiceCommandException {
        try {
            boolean modified = false;

            // Rufnummernleistung als realisiert markieren
            Leistung2DN l2dn = rs.findLeistung2DnById(dnLstView.getId());
            if ((l2dn.getScvRealisierung() != null) && (l2dn.getEwsdRealisierung() == null)) {
                l2dn.setEwsdRealisierung(realDate);
                l2dn.setEwsdUserRealisierung(CPS_USER);
                l2dn.setCpsTxIdCreation(getCPSTransaction().getId());
                modified = true;
            }
            // zusaetzlich pruefen, ob die Kuendigung wirklich schon eingetragen werden darf
            if ((l2dn.getScvKuendigung() != null) && (l2dn.getEwsdKuendigung() == null)
                    && DateTools.isDateBeforeOrEqual(l2dn.getScvKuendigung(), realDate)) {
                l2dn.setEwsdKuendigung(realDate);
                l2dn.setEwsdUserKuendigung(CPS_USER);
                l2dn.setCpsTxIdCancel(getCPSTransaction().getId());
                modified = true;
            }

            if (modified) { // nur speichern, wenn Datensatz veraendert wurde
                rs.saveLeistung2DN(l2dn);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error while combine DN service with CPS transaction: "
                    + e.getMessage(), e);
        }
    }

    /**
     * Prueft, ob es sich bei der Rufnummer um eine Mnet eigene Rufnummer handelt.
     */
    boolean isMnetDN(Rufnummer dn, RufnummerService rufnummerService) throws FindException {
        boolean mnetDN = false;
        if (dn != null) {
            mnetDN = (dn.getBlockNoOrig() != null) && rufnummerService.isMnetBlock(dn.getBlockNoOrig());
        }
        return mnetDN;
    }

    public Pair<String, String> adjustBlockLength(String blockStart, String blockEnd) {
        String blockEnd1 = blockEnd;
        String blockStart1 = blockStart;
        if (StringUtils.isNotEmpty(blockStart1) && StringUtils.isNotEmpty(blockEnd1)) {
            final int blockLength = Math.max(blockStart1.length(), blockEnd1.length());
            if (blockStart1.length() != blockEnd1.length()) {
                if (blockStart1.length() < blockLength) {
                    blockStart1 = adjustBlockToLength(blockStart1, blockLength);
                }
                else {
                    blockEnd1 = adjustBlockToLength(blockEnd1, blockLength);
                }
            }
        }
        return new Pair<>(blockStart1, blockEnd1);
    }

    private String adjustBlockToLength(String blockPart, int length) {
        StringBuilder tmp = new StringBuilder();
        for (int i = blockPart.length(); i < length; i++) {
            tmp.append("0");
        }
        return tmp.append(blockPart).toString();
    }

    /**
     * Injected
     */
    public void setRufnummerService(RufnummerService rufnummerService) {
        this.rufnummerService = rufnummerService;
    }

    /**
     * Injected
     */
    public void setLeistungService(LeistungService leistungService) {
        this.leistungService = leistungService;
    }

    /**
     * Injected
     */
    public void setCcRufnummernService(CCRufnummernService ccRufnummernService) {
        this.ccRufnummernService = ccRufnummernService;
    }

}
