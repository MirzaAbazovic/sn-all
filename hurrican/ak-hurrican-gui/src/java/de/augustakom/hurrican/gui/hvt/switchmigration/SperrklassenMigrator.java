/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.2011 16:20:48
 */
package de.augustakom.hurrican.gui.hvt.switchmigration;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.LeistungParameter;
import de.augustakom.hurrican.model.cc.dn.Sperrklasse;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCRufnummernService;
import de.augustakom.hurrican.service.cc.SperrklasseService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * Unterschiedliche {@link HWSwitchType}s haben oft unterschiedliche Sperrklassen bei den Rufnummernleistungen.
 * Migrieren wir nun diese Leistungen zwischen Switches verschiedener Typen, so müssen die betreffenden Sperrklassen
 * bzw. Rufnummernleistungen auch angepasst werden.
 *
 *
 * @since Release 10
 */
class SperrklassenMigrator {

    private final List<Leistung2DN> leistungenToMigrate;
    private SperrklasseService sperrklasseService;
    private CCRufnummernService rufnummernService;
    private MigrationHwSwitchTypes hwSwitchTypes;
    private Date plannedExecution;

    protected SperrklassenMigrator() {
        leistungenToMigrate = new ArrayList<>();
    }

    /**
     * erzeugt eine neue Instanz mit den Typen von UrsprungsSwitch und ZielSwitch.
     *
     * @param sourceHwSwitchType
     * @param destinationHwSwitchType
     * @return
     */
    static SperrklassenMigrator create(HWSwitchType sourceHwSwitchType, HWSwitchType destinationHwSwitchType, Date plannedExecution) {
        SperrklassenMigrator migrator = new SperrklassenMigrator();
        migrator.setHwSwitchTypes(sourceHwSwitchType, destinationHwSwitchType);
        migrator.setPlannedExecution(plannedExecution);
        return migrator;
    }

    /**
     * @return Returns the plannedExecution.
     */
    protected Date getPlannedExecution() {
        return plannedExecution;
    }

    /**
     * @param plannedExecution The plannedExecution to set.
     */
    private void setPlannedExecution(Date plannedExecution) {
        this.plannedExecution = plannedExecution;
    }

    protected void setHwSwitchTypes(HWSwitchType source, HWSwitchType destination) {
        if ((source == null) || (destination == null)) {
            final String errorMsg = String
                    .format("Folgende Eingabe fuehrte zu einem Fehler: QuellSwitch (%s) und ZielSwitch (%s) fuer eine Migration!",
                            source, destination);
            throw new IllegalArgumentException(errorMsg);
        }
        this.hwSwitchTypes = MigrationHwSwitchTypes.create(source, destination);
    }

    /**
     * @return Returns the hwSwitchTypes.
     */
    protected MigrationHwSwitchTypes getHwSwitchTypes() {
        return hwSwitchTypes;
    }

    /**
     * @return Returns the sperrklasseService.
     * @throws ServiceNotFoundException
     */
    protected SperrklasseService getSperrklasseService() throws ServiceNotFoundException {
        if (sperrklasseService == null) {
            sperrklasseService = CCServiceFinder.instance().getCCService(SperrklasseService.class);
        }
        return sperrklasseService;
    }

    /**
     * @return Returns the rufnummernService.
     * @throws ServiceNotFoundException
     */
    protected CCRufnummernService getRufnummernService() throws ServiceNotFoundException {
        if (rufnummernService == null) {
            rufnummernService = CCServiceFinder.instance().getCCService(CCRufnummernService.class);
        }
        return rufnummernService;
    }

    /**
     * @return Returns the sourceHwSwitchType.
     */
    protected HWSwitchType getSourceHwSwitchType() {
        return getHwSwitchTypes().getSource();
    }

    /**
     * @return Returns the destinationHwSwitchType.
     */
    protected HWSwitchType getDestinationHwSwitchType() {
        return getHwSwitchTypes().getDestination();
    }

    protected boolean isMigrationNeeded() {
        return getHwSwitchTypes().isMigrationNeeded();
    }

    /**
     * liefert fuer einen Auftrag alle moeglichen Rufnummerleistungen.
     *
     * @param auftragId
     * @return
     * @throws FindException
     * @throws ServiceNotFoundException
     */
    protected List<Leistung2DN> findAlleLeistungen(Long auftragId) throws FindException, ServiceNotFoundException {
        return getRufnummernService().findDNLeistungen4Auftrag(auftragId);
    }

    /**
     * liefert fuer einen Auftrag alle Sperrklassen. Dazu werden ueber {@link #findAlleLeistungen(Long)} alle
     * Rufnummerleistungen geladen und nur die zurueck geliefert, die tatsaechlich eine Sperrklasse und auch aktiv
     * sind.
     *
     * @param auftragId
     * @return
     * @throws FindException
     * @throws ServiceNotFoundException
     */
    protected List<Leistung2DN> findAllSperrklassenLeistungen(Long auftragId) throws FindException,
            ServiceNotFoundException {
        List<Leistung2DN> allLeistung2DN4AuftragId = findAlleLeistungen(auftragId);
        List<Leistung2DN> result = new ArrayList<>();
        for (Leistung2DN leistung2dn : allLeistung2DN4AuftragId) {
            if (isLeistung2DNSperrklasseLeistung(leistung2dn) && isSperrklassenLeistungCurrent(leistung2dn, getPlannedExecution())) {
                result.add(leistung2dn);
            }
        }
        return result;
    }

    protected boolean isSperrklassenLeistungCurrent(Leistung2DN leistung2dn, Date plannedExecution) {
        boolean isScvRealisierungBeforePlannedExecution = DateTools.isDateBeforeOrEqual(
                leistung2dn.getScvRealisierung(), plannedExecution);
        boolean isScvKuendigungAfterPlannedExecution = (leistung2dn.getScvKuendigung() == null)
                || DateTools.isDateAfter(leistung2dn.getScvKuendigung(), plannedExecution);
        return isScvRealisierungBeforePlannedExecution && isScvKuendigungAfterPlannedExecution;
    }

    protected boolean isLeistung2DNSperrklasseLeistung(Leistung2DN leistung2dn) {
        return Leistung4Dn.SPERRKLASSE_ID.equals(leistung2dn.getLeistung4DnId())
                && ((leistung2dn.getParameterId() == null) || LeistungParameter.ID_NUMMER_DER_SPERRKLASSE.equals(leistung2dn.getParameterId()));
    }

    protected Sperrklasse findSperrklasse(Integer sperrklasseNumber) throws FindException, ServiceNotFoundException {
        return getSperrklasseService().findSperrklasseBySperrklasseNo(sperrklasseNumber,
                getSourceHwSwitchType());
    }

    protected Integer getSperrklasseNumberForDestinationSwitch(Sperrklasse sperrklasse) {
        return sperrklasse.getSperrklasseByHwSwitchType(getDestinationHwSwitchType());
    }

    /**
     * prueft, ob die angegebene Liste an {@link SwitchMigrationView}s migriert werden kann. Wenn ja, werden diese Daten
     * fuer die Migration aufbereitet. Jedoch wenn nur ein Datensatz bei der Pruefung fehlschlaegt, dann wird die
     * komplette Migration der Sperrklassen abgebrochen.
     *
     * @throws ServiceNotFoundException
     * @throws FindException
     */
    SperrklassenMigrator checkMigratable(List<? extends SwitchMigrationView> migrationViews) throws FindException,
            ServiceNotFoundException {
        if (CollectionTools.isEmpty(migrationViews)) {
            return this;
        }
        if (isMigrationNeeded()) {
            for (SwitchMigrationView order : migrationViews) {
                List<Leistung2DN> sperrklassenLeistung = findAllSperrklassenLeistungen(order.getAuftragId());
                for (Leistung2DN leistung2dn : sperrklassenLeistung) {
                    try {
                        final Integer oldSperrklasseNumber = getSperrklasseNumber(leistung2dn);
                        final Integer newSperrklasseNumber = findNewSperrklasseNumberForOldNumber(oldSperrklasseNumber);
                        if (NumberTools.notEqual(oldSperrklasseNumber, newSperrklasseNumber)) {
                            leistung2dn.setLeistungParameter("" + newSperrklasseNumber);
                            getLeistungenToMigrate().add(leistung2dn);
                        }

                    }
                    catch (IllegalStateException e) {
                        final String errorMsg = String.format(
                                "Beim Versuch eine Rufnummernleistungen auf dem Standort (%s) mit Auftrag " +
                                        "BillingNr = %s, technische Auftragsnummer = %s, DN_NO = %s ist folgender Fehler " +
                                        "aufgetreten:%n%n %s " +
                                        "%n%nDie komplette Migration ist daher gestoppt. Es wurden keine Daten migriert!",
                                order.getTechLocation(), order.getBillingAuftragId(), order.getAuftragId(),
                                leistung2dn.getDnNo(), e.getMessage()
                        );
                        throw new IllegalStateException(errorMsg);
                    }
                }
            }
        }
        return this;
    }

    Integer valueAsInteger(String value) {
        try {
            return Integer.valueOf(value);
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * liefert die Sperrklassenummer einer {@link Leistung2DN} als {@link Integer}, falls eine gesetzt ist.
     *
     * @param leistung2dn
     * @return
     */
    protected Integer getSperrklasseNumber(Leistung2DN leistung2dn) {
        final String leistungParameter = leistung2dn.getLeistungParameter();
        if (StringUtils.isBlank(leistungParameter)) {
            final String errorMsg = String.format("Fuer die Rufnummernleistung (%s) gibt es keine Sperrklassennummer!",
                    leistung2dn);
            throw new IllegalStateException(errorMsg);
        }
        return valueAsInteger(leistungParameter);
    }

    protected Integer findNewSperrklasseNumberForOldNumber(Integer oldSperrklasseNumber) throws FindException,
            ServiceNotFoundException {

        //Sonderfall Sperrklasse '0' -> SPERRKLASSE_KEINE
        if (NumberTools.equal(Sperrklasse.SPERRKLASSE_KEINE, oldSperrklasseNumber)) {
            return Sperrklasse.SPERRKLASSE_KEINE;
        }
        Sperrklasse sperrklasse = findSperrklasse(oldSperrklasseNumber);
        if (sperrklasse == null) {
            final String errorMsg = String.format(
                    "Fuer die Rufnummernleistung mit Leistungsparameter (%s) gibt es keine Sperrklasse!",
                    oldSperrklasseNumber);
            throw new IllegalStateException(errorMsg);
        }
        Integer newSperrklasseNumber = getSperrklasseNumberForDestinationSwitch(sperrklasse);
        if (newSperrklasseNumber == null) {
            final String errorMsg = String.format(
                    "Fuer die Sperrklasse (%s) auf dem Ursprungs-Switch gibt es keine vergleichbare "
                            + "Sperrklasse auf dem Ziel-Switch!", sperrklasse
            );
            throw new IllegalStateException(errorMsg);
        }
        return newSperrklasseNumber;
    }

    /**
     * iteriert ueber alle vorbearbeiteten {@link Leistung2DN} und speichert diese endgueltig ab.
     *
     * @throws StoreException
     * @throws ServiceNotFoundException
     */
    void migrate() throws StoreException, ServiceNotFoundException {
        if (CollectionTools.isNotEmpty(getLeistungenToMigrate())) {
            for (Leistung2DN leistung : getLeistungenToMigrate()) {
                getRufnummernService().saveLeistung2DN(leistung);
            }
        }
    }

    /**
     * @return Returns the leistungenToMigrate.
     */
    List<Leistung2DN> getLeistungenToMigrate() {
        return leistungenToMigrate;
    }

} // end
