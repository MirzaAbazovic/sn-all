/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2009 17:28:58
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.CfgRegularExpression;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSDNPortation;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.RegularExpressionService;


/**
 * Command-Klasse, um Informationen ueber abgehende Rufnummern-Portierungen zu ermitteln.
 *
 *
 */
public class CPSGetDNPortationGoingDataCommand extends AbstractCPSGetDNDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSGetDNPortationGoingDataCommand.class);

    private Map<Long, PortationDataHelper> dnNo2PortationType = null;

    private RufnummerService rufnummerService;
    private RegularExpressionService regularExpressionService;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            dnNo2PortationType = new HashMap<Long, PortationDataHelper>();

            List<Rufnummer> dns4Portation = getDNs4Portation(getCPSTransaction().getOrderNoOrig(), rufnummerService);
            if (CollectionTools.isNotEmpty(dns4Portation)) {
                List<CPSDNPortation> portations = new ArrayList<CPSDNPortation>();
                for (Rufnummer dn : dns4Portation) {
                    CPSDNPortation dnPortation = new CPSDNPortation();
                    dnPortation.transferDNData(dn);

                    boolean mnetDN = isMnetDN(dn, rufnummerService);
                    dnPortation.setMnetDN(BooleanTools.getBooleanAsString(mnetDN));

                    // PortationType und Target setzen
                    definePortationTypeAndTarget(dnPortation, dn, rufnummerService);

                    portations.add(dnPortation);
                }

                // abgehende Portierungen der ServiceOrderData uebergeben
                getServiceOrderData().setDnPortations(portations);
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK,
                    null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error while loading portation data: " + e.getMessage(), this.getClass());
        }
    }

    /*
     * Ermittelt alle Rufnummern, fuer den Portierungsblock. <br>
     * <br>
     * Fuer den Portierungsblock werden Rufnummern geladen die:
     * <ul>
     *   <li>gekuendigt werden sollen
     *   <li>eine Rufumwertung besitzen (Routing)
     *   <li>die Leistung 'AGRU' zugeordnet haben
     * </ul>
     * <br>
     * Dies ist dann der Fall, wenn der letzte Datensatz einer Rufnummer keinem
     * Auftrag mehr zugeordnet ist und die Rufnummer als 'abgehende Portierung'
     * markiert ist (inkl. Future-Carrier). <br>
     * Ausserdem wird das (abgehende) Portierungsdatum beruecksichtigt - es muss
     * gleich dem geplanten Ausfuehrungszeitpunkt der CPS-Tx sein.
     * @param orderNoOrig ID des Billing-Auftrags
     * @param rs Rufnummern-Service
     * @return Liste mit den zu kuendigenden Rufnummern
     * @throws ServiceCommandException wenn bei der Ermittlung ein Fehler auftritt.
     */
    List<Rufnummer> getDNs4Portation(Long orderNoOrig, RufnummerService rufnummerService) throws ServiceCommandException {
        try {
            List<Rufnummer> dns4Portation = new ArrayList<Rufnummer>();
            Date portationDateToCheck = (getServiceOrderData().isInitialLoad())
                    ? getCPSTransaction().getEstimatedExecTime()
                    : DateTools.changeDate(getCPSTransaction().getEstimatedExecTime(), Calendar.DATE, 1);

            List<Rufnummer> dns = rufnummerService.findAllRNs4Auftrag(orderNoOrig);
            if (CollectionTools.isNotEmpty(dns)) {
                Map<Long, Rufnummer> done = new HashMap<Long, Rufnummer>();
                for (Rufnummer dn : dns) {
                    Rufnummer dnToCheck = getLatestDnByPortationDate(dn, portationDateToCheck);

                    // Ermittlung, ob die ermittelte Rufnummer auf einem anderen Taifun Auftrag ist.
                    // Ist dies der Fall, dann darf die Rufnummer fuer die weitere Bearbeitung
                    // nicht mehr betrachtet werden!
                    boolean isDnOnDifferentOrder = (dnToCheck.getAuftragNoOrig() != null)
                            ? !NumberTools.equal(dnToCheck.getAuftragNoOrig(), getCPSTransaction().getOrderNoOrig())
                            : false;

                    if (!done.containsKey(dnToCheck.getDnNoOrig()) && !isDnOnDifferentOrder) {
                        done.put(dnToCheck.getDnNoOrig(), dnToCheck);
                        filterPortations(dnToCheck, dns4Portation, rufnummerService);
                    }
                }
            }

            return dns4Portation;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error while loading DNs for portation: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt die letzte Rufnummern-Historisierung zu einer Rufnummer, bei der das GueltigVon Datum <= dem zu
     * pruefenden Portierungsdatum ist.
     *
     * @param baseDN               Rufnummer, von der die letzte relevante Historisierung ermittelt werden soll
     * @param portationDateToCheck zu beruecksichtigendes Portierungsdatum
     * @return letzte ermittelte Rufnummer oder <code>baseDN</code>, wenn zu dem Datum keine Eintraege gefunden wurden.
     * @throws HurricanServiceCommandException
     */
    Rufnummer getLatestDnByPortationDate(Rufnummer baseDN, Date portationDateToCheck) throws HurricanServiceCommandException {
        try {
            List<Rufnummer> rufnummern = rufnummerService.findDNsByDnNoOrig(baseDN.getDnNoOrig(), portationDateToCheck);
            if (CollectionTools.isNotEmpty(rufnummern)) {
                // es wird die letzte Rufnummer ermittelt, die in zu dem angegebenen Datum gueltig ist
                return rufnummern.get(rufnummern.size() - 1);
            }
            else {
                return baseDN;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler bei der Ermittlung der zu portierenden Rufnummern!");
        }
    }

    /*
     * Ueberprueft, ob die angegebene Rufnummer portiert oder umgewertet bzw. geroutet
     * werden soll.
     * Ist dies der Fall, wird die Rufnummer in die Liste 'dns4Portation' eingetragen.
     */
    void filterPortations(Rufnummer dnToCheck, List<Rufnummer> dns4Portation, RufnummerService rufnummerService)
            throws FindException, ServiceNotFoundException {
        if ((dnToCheck.getAuftragNoOrig() == null) &&
                dnToCheck.isPortierungAbgehend() && StringUtils.isNotBlank(dnToCheck.getFutureCarrier())) {
            // Last-DN pruefen, ob keinem Auftrag zugeordnet und Portierung abgehend
            if (DateTools.isDateEqual(
                    getCPSTransaction().getEstimatedExecTime(), dnToCheck.getRealDate())) {
                dns4Portation.add(dnToCheck);

                dnNo2PortationType.put(dnToCheck.getDnNo(),
                        new PortationDataHelper(
                                CPSDNPortation.PTYPE_PORTATION_GOING,
                                StringUtils.trimToEmpty(dnToCheck.getFutureCarrierPortKennung()))
                );
            }
        }
        else if (dnToCheck.isRouting() && (dnToCheck.getAuftragNoOrig() != null)) {
            // Rufnummern aufnehmen, die vom Typ ROUTING sind
            dns4Portation.add(dnToCheck);
            dnNo2PortationType.put(dnToCheck.getDnNo(),
                    new PortationDataHelper(
                            CPSDNPortation.PTYPE_PORTATION_RUW,
                            getPhoneNumberFromRemark(dnToCheck))
            );
        }
        else if (dnToCheck.isAGRU() && (dnToCheck.getAuftragNoOrig() != null)) {
            // Rufnummern aufnehmen, die vom Typ ROUTING sind
            dns4Portation.add(dnToCheck);
            dnNo2PortationType.put(dnToCheck.getDnNo(),
                    new PortationDataHelper(
                            CPSDNPortation.PTYPE_PORTATION_AGRU,
                            getPhoneNumberFromRemark(dnToCheck))
            );
        }
        else {
            Leistung2DN l2dnAGRU = getActiveDNServiceOfType(dnToCheck, Leistung4Dn.DN_SERVICE_AGRU);
            if (l2dnAGRU != null) {
                // Rufnummern aufnehmen, die aktive DN-Leistung AGRU haben
                dns4Portation.add(dnToCheck);
                dnNo2PortationType.put(dnToCheck.getDnNo(),
                        new PortationDataHelper(
                                CPSDNPortation.PTYPE_PORTATION_AGRU,
                                l2dnAGRU.getLeistungParameter())
                );
            }
            else {
                Leistung2DN l2dnPORTEDDN = getActiveDNServiceOfType(dnToCheck, Leistung4Dn.DN_SERVICE_PORTEDDN);
                if (l2dnPORTEDDN != null) {
                    // Rufnummern aufnehmen, die die Leistung PORTEDDN zugeordnet haben
                    dns4Portation.add(dnToCheck);
                    dnNo2PortationType.put(dnToCheck.getDnNo(),
                            new PortationDataHelper(
                                    CPSDNPortation.PTYPE_PORTATION_RUW,
                                    l2dnPORTEDDN.getLeistungParameter())
                    );
                }
            }
        }
    }

    /*
     * Ermittelt den Portierungstyp und das Ziel der Portierung.
     * @param dnPort Datenmodell in das der PortationType und das Target eingetragen wird
     * @param dn betroffene Rufnummer
     * @param rs Rufnummern-Service
     * @throws ServiceCommandException
     */
    void definePortationTypeAndTarget(CPSDNPortation dnPort, Rufnummer dn, RufnummerService rs) throws ServiceCommandException {
        try {
            PortationDataHelper dataHelper = dnNo2PortationType.get(dn.getDnNo());
            dnPort.setPortationType(dataHelper.portationType);
            dnPort.setTarget(dataHelper.destination);

            if (StringUtils.isBlank(dnPort.getTarget())) {
                throw new HurricanServiceCommandException(
                        "TARGET of PORTATION is not defined! DN: " + dn.getRufnummer());
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error while defining portation type and target for dn: " + e.getMessage(), e);
        }
    }

    /*
     * Ermittelt aus dem Bemerkungsfeld der Rufnummer die Ziel-Rufnummer.
     * Es werden nur numerische Zeichen zurueck geliefert.
     */
    String getPhoneNumberFromRemark(Rufnummer dn) throws ServiceNotFoundException {
        if ((dn != null) && StringUtils.isNotBlank(dn.getRemarks())) {
            String destinationDN = regularExpressionService.match(Rufnummer.CFG_REG_EXP_REQUESTED_INFO_ROUTING,
                    Rufnummer.class, CfgRegularExpression.Info.PHONE_NUMBER, dn.getRemarks());
            if (StringUtils.isNotBlank(destinationDN)) {
                return StringTools.getDigitsFromString(destinationDN);
            }
        }
        return null;
    }

    static class PortationDataHelper {
        String portationType;
        String destination;

        PortationDataHelper(String portationType, String destination) {
            this.portationType = portationType;
            this.destination = destination;
        }
    }

    /**
     * @param rufnummerService The rufnummerService to set.
     */
    @Override
    public void setRufnummerService(RufnummerService rufnummerService) {
        this.rufnummerService = rufnummerService;
    }


    /**
     * Injected
     */
    public void setRegularExpressionService(RegularExpressionService regularExpressionService) {
        this.regularExpressionService = regularExpressionService;
    }

}
