/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.augustakom.hurrican.service.cc.impl.crossconnect;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.BrasPool;
import de.augustakom.hurrican.model.cc.EQCrossConnection;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.RegularExpressionService;

/**
 * Calculator class for cross connections of DSLAM ports. <b>NOT</b> thread-safe. Only used for the calculation of the
 * CrossConnections of a single port, then thrown away.
 *
 *
 */
public class CcCalculator {
    private static final Logger LOGGER = Logger.getLogger(CcCalculator.class);

    protected RegularExpressionService regularExpressionService;
    protected EQCrossConnectionService eqCrossConnectionService;
    Equipment port;
    HWBaugruppe baugruppe;
    protected HWDslam dslam;
    protected HWSubrack subrack;
    protected List<HWSubrack> subrackList;
    protected HWBaugruppenTyp baugruppenTyp;

    protected Map<String, BrasPool> brasPoolForPrefix;

    protected CcStrategy ltStrategy;
    protected CcStrategy ntStrategy;
    protected CcStrategy brasStrategy;


    /**
     * Give this calculator instance the required services and entities.
     */
    public void configure(EQCrossConnectionService eqCrossConnectionService,
            RegularExpressionService regularExpressionService,
            Equipment port, HWBaugruppe baugruppe, HWDslam dslam, HWSubrack subrack,
            List<HWSubrack> subrackList, HWBaugruppenTyp baugruppenTyp) {
        this.eqCrossConnectionService = eqCrossConnectionService;
        this.regularExpressionService = regularExpressionService;
        this.port = port;
        this.baugruppe = baugruppe;
        this.dslam = dslam;
        this.subrack = subrack;
        this.subrackList = subrackList;
        this.baugruppenTyp = baugruppenTyp;
        this.brasPoolForPrefix = new HashMap<String, BrasPool>();
    }

    /**
     * Determines the strategies by setting the term type of the strategy and querying the {@link
     * CcStrategy#get(CcStrategyType)} method.
     */
    public void determineStrategies(CcStrategyType strategyType) {
        CcStrategyType.TermType orig = strategyType.getTermType();
        strategyType.setTermType(CcStrategyType.TermType.LT);
        this.ltStrategy = CcStrategy.get(strategyType);
        if (this.ltStrategy == null) {
            throw new IllegalStateException("Could not determine strategy for type " + strategyType.toString());
        }
        strategyType.setTermType(CcStrategyType.TermType.NT);
        this.ntStrategy = CcStrategy.get(strategyType);
        if (this.ntStrategy == null) {
            throw new IllegalStateException("Could not determine strategy for type " + strategyType.toString());
        }
        strategyType.setTermType(CcStrategyType.TermType.BRAS);
        this.brasStrategy = CcStrategy.get(strategyType);
        if (this.brasStrategy == null) {
            throw new IllegalStateException("Could not determine strategy for type " + strategyType.toString());
        }
        strategyType.setTermType(orig);
    }

    /**
     * Get the Dslam. Useful for strategies.
     */
    public HWDslam getDslam() {
        return dslam;
    }

    /**
     * Get the Baugruppe. Useful for strategies.
     */
    public HWBaugruppe getBaugruppe() {
        return baugruppe;
    }

    /**
     * Get the regular expression service. Useful for strategies.
     */
    public RegularExpressionService getRegularExpressionService() {
        return regularExpressionService;
    }

    /**
     * Get the port. Useful for strategies.
     */
    public Equipment getEquipment() {
        return port;
    }

    /**
     * Get the Baugruppen type. Useful for strategies.
     */
    public HWBaugruppenTyp getBaugruppenTyp() {
        return baugruppenTyp;
    }

    /**
     * Get the subrack. Useful for strategies.
     */
    public HWSubrack getSubrack() {
        if (subrack == null) {
            throw new RuntimeException("Port is not in a Baugruppe that is in a subrack.");
        }
        return subrack;
    }

    /**
     * Get the list of subracks of the DSLAM. Useful for strategies.
     */
    public List<HWSubrack> getSubrackList() {
        return subrackList;
    }

    /**
     * Get a Bras Pool for a name prefix. This stores the selected pool and, if later asked for the same prefix again,
     * will return the same pool as before.
     */
    BrasPool getBrasPool(String poolPrefix) {
        BrasPool brasPool = brasPoolForPrefix.get(poolPrefix);
        if (brasPool == null) {
            try {
                List<BrasPool> pools;
                pools = eqCrossConnectionService.findBrasPoolByNamePrefix(poolPrefix); // could throw FindException
                Random random = new Random();
                brasPool = pools.get(random.nextInt(pools.size())); // could throw IllegalArgumentException
                brasPoolForPrefix.put(poolPrefix, brasPool);
                LOGGER.debug("getBrasPool() - using bras pool " + (brasPool != null ? brasPool.getName() : "null") +
                        " for pool name prefix '" + poolPrefix + "'");
            }
            catch (Exception e) { // IllegalArgumentException, FindException
                LOGGER.error("getBrasPool() - Exception trying to get VC from pool", e);
                throw new IllegalStateException("Could not find BRAS pool for pool name prefix '" + poolPrefix + "'", e);
            }
        }
        return brasPool;
    }

    /**
     * Get a VC from a BRAS pool
     */
    public Integer getVcFromBrasPool(BrasPool brasPool) {
        try {
            return eqCrossConnectionService.getVcFromPool(brasPool);
        }
        catch (FindException e) {
            LOGGER.error("getVcFromBrasPool() - Exception trying to get VC from pool", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Determine the cross connection type reference id base on the values set in this strategy type (ccType and voip).
     */
    public Long getCcRefId(CcStrategyType type) {
        switch (type.getCcType()) {
            case HSI:
                return EQCrossConnection.REF_ID_XCONN_HSI_XCONN;
            case CPE:
                return EQCrossConnection.REF_ID_XCONN_CPE_MGM_RB;
            case VOIP:
                return EQCrossConnection.REF_ID_XCONN_VOIP_XCONN;
            case IAD:
                return EQCrossConnection.REF_ID_XCONN_IAD_MGM_RB;
            default:
                throw new RuntimeException("Unknown CrossConnection type: " +
                        (type.getCcType() != null ? type.getCcType().name() : "<null>"));
        }
    }

    // Getter for CC values

    public Integer getLtOuter() {
        return ltStrategy.getOuter(this);
    }

    public Integer getLtInner() {
        return ltStrategy.getInner(this);
    }

    public Integer getNtOuter() {
        return ntStrategy.getOuter(this);
    }

    public Integer getNtInner() {
        return ntStrategy.getInner(this);
    }

    public Integer getBrasOuter() {
        return brasStrategy.getOuter(this);
    }

    public Integer getBrasInner() {
        return brasStrategy.getInner(this);
    }

    public BrasPool getBrasPool() {
        return brasStrategy.getBrasPool(this);
    }
}
