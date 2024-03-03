/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.2011 09:17:55
 */
package de.augustakom.hurrican.gui.hvt.switchmigration;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.SIPDomainService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * Migriert Switchkennung bei SIPDomains.
 *
 *
 * @since Release 10
 */
final class SIPDomainMigrator {

    private static final Logger LOGGER = Logger.getLogger(SIPDomainMigrator.class);

    @Resource
    private SIPDomainService sipDomainService;

    @Resource
    private VoIPService voipService;

    private final HWSwitch destinationSwitch;
    private List<SwitchMigrationView> migrationViews;

    /**
     * @return Returns the sipDomainService.
     * @throws ServiceNotFoundException
     */
    private SIPDomainService getSipDomainService() throws ServiceNotFoundException {
        return sipDomainService != null ? sipDomainService : CCServiceFinder.instance().getCCService(
                SIPDomainService.class);
    }

    /**
     * @return Returns the voipService.
     * @throws ServiceNotFoundException
     */
    private VoIPService getVoipService() throws ServiceNotFoundException {
        return voipService != null ? voipService : CCServiceFinder.instance().getCCService(VoIPService.class);
    }

    /**
     * @return Returns the destinationSwitch.
     */
    private HWSwitch getDestinationSwitch() {
        return destinationSwitch;
    }

    /**
     * @return Returns the migrationViews.
     */
    private List<SwitchMigrationView> getMigrationViews() {
        return migrationViews;
    }

    private SIPDomainMigrator(HWSwitch destinationSwitch) {
        this.destinationSwitch = destinationSwitch;
    }

    static SIPDomainMigrator create(HWSwitch destinationSwitch) {
        return new SIPDomainMigrator(destinationSwitch);
    }

    SIPDomainMigrator withMigrationViews(List<SwitchMigrationView> migrationViews) {
        this.migrationViews = migrationViews;
        return this;
    }

    AKWarnings migrate() {
        AKWarnings warnings = new AKWarnings();
        List<SwitchMigrationView> switchMigrationViews = getMigrationViews();
        if (CollectionTools.isNotEmpty(switchMigrationViews)) {
            for (SwitchMigrationView order : switchMigrationViews) {
                AKWarnings warnings4Order = new AKWarnings();
                try {
                    List<AuftragVoipDNView> voipDNViews = getVoipService().findVoIPDNView(order.getAuftragId());
                    if (CollectionTools.isNotEmpty(voipDNViews)) {
                        for (AuftragVoipDNView voipDNView : voipDNViews) {
                            AKWarnings newWarnings = migrateSIPDomain(order.getAuftragId(), order.getProdId(),
                                    voipDNView.getDnNoOrig(), getDestinationSwitch());
                            warnings4Order.addAKWarnings(newWarnings);
                        }
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    warnings4Order.addAKWarning(e, e.getMessage());
                }
                if (warnings4Order.isNotEmpty()) {
                    warnings.addAKWarning(this, createMessage4Order(order, warnings4Order));
                }
            }
        }
        return warnings;
    }

    private AKWarnings migrateSIPDomain(Long auftragId, Long produktId, Long voipDnNoOrig,
            HWSwitch destinationSwitch)
            throws ServiceNotFoundException, FindException, StoreException {

        AuftragVoIPDN voipDN = getVoipService().findByAuftragIDDN(auftragId, voipDnNoOrig);
        if (voipDN == null) {
            throw new FindException(String.format("Die VoIP DN '%s' konnte nicht ermittelt werden!",
                    voipDnNoOrig));
        }
        Reference migratedSIPDomain = getSipDomainService().migrateSIPDomain(produktId, voipDN.getSipDomain(),
                destinationSwitch);

        AKWarnings warnings = null;
        if ((voipDN.getSipDomain() != null) && (migratedSIPDomain == null)) {
            warnings = new AKWarnings().addAKWarning(this, String.format("Die SIP Domäne '%s' konnte nicht migriert werden, "
                            + "da für den neuen Switch keine passende Konfiguration verfügbar ist. SIP Domäne ist gelöscht!",
                    voipDN.getSipDomain().getStrValue()
            ));
        }
        voipDN.setSipDomain(migratedSIPDomain);
        getVoipService().saveAuftragVoIPDN(voipDN);
        return warnings;
    }

    private String createMessage4Order(SwitchMigrationView order, AKWarnings warnings) {
        return String.format("Auftrag ID %s%n", order.getAuftragId()) + warnings.getWarningsAsText();
    }

}
