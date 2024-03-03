/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.04.2010 14:28:40
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import java.util.*;
import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.messages.IWarningAware;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;


//@formatter:off
/**
 * Klasse, um einen Baugruppen-Schwenk vom Typ 'einfacher Kartenwechsel' durchzufuehren. <br/>
 * Ablauf fuer den Baugruppen-Schwenk: <br/>
 * <ul>
 *     <li>Ports innerhalb der Rangierungen tauschen u. Rangierung wieder freigeben
 *     <li>alte Ports u. Baugruppe als 'abgebaut' markieren
 *     <li>neue Ports als 'rangiert' markieren u. zugehoerige Baugruppe als 'eingebaut'
 *     <li>CrossConnection fuer neuen Port berechnen (sofern nicht manuell konfiguriert)
 * </ul>
 */
//@formatter:on
public class HWBaugruppenChangeCardExecuter implements HWBaugruppenChangeExecuter, IWarningAware {

    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenChangeCardExecuter.class);

    private HwBaugruppenChangeCardHelper helper;

    private Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping;
    private Map<Long, Rangierung> aktualisierteRangierungen;
    private final AKWarnings warnings = new AKWarnings();

    /**
     * Uebergibt dem Executer die notwendigen Modelle u. Services.
     *
     * @param toExecute
     * @param hwService
     * @param hwBaugruppenChangeService
     * @param rangierungsService
     * @param eqCrossConnectionService
     * @param produktService
     * @param sessionId
     */
    public void configure(HWBaugruppenChange toExecute,
            HWService hwService,
            HWBaugruppenChangeService hwBaugruppenChangeService,
            RangierungsService rangierungsService,
            EQCrossConnectionService eqCrossConnectionService,
            ProduktService produktService,
            DSLAMService dslamService,
            PhysikService physikService,
            Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping,
            Long sessionId,
            String userW) {
        this.dslamProfileMapping = dslamProfileMapping;
        helper = new HwBaugruppenChangeCardHelper(eqCrossConnectionService, hwService, hwBaugruppenChangeService,
                produktService, rangierungsService, physikService, toExecute, sessionId, dslamService, userW);
    }

    /**
     * Fuehrt den Baugruppen-Schwenk durch
     *
     * @throws StoreException
     */
    @Override
    public void execute() throws StoreException {
        try {
            Date executionDate = new Date();
            helper.checkData();
            helper.loadPortMappingViews();
            aktualisierteRangierungen = helper.movePorts();
            helper.declarePortsAsRemoved();
            helper.declareCardsAsRemoved(null);
            helper.declareNewPortsAndCardAsBuiltIn();
            helper.calculateCrossConnections(executionDate);
            warnings.addAKWarnings(helper.changeDslamProfiles(executionDate, dslamProfileMapping, aktualisierteRangierungen));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Ausfuehrung des Baugruppen-Schwenks: " + e.getMessage(), e);
        }
    }

    @Override
    public AKWarnings getWarnings() {
        return warnings;
    }
}


