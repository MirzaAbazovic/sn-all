/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2012 13:09:55
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Command prueft, ob die Bandbreite des Zielauftrags mit der zu uebernehmenden Physik realisierbar ist. Es genuegt nur
 * die erste Rangierung der Endstelle B zu betrachten.
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.CheckDownstreamBandwidthCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckDownstreamBandwidthCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckDownstreamBandwidthCommand.class);

    /**
     * dem Quellauftrag zugeordnete erste Rangierung (Endstelle B)
     */
    protected Rangierung rangierung = null;
    /**
     * Bandbreite des neuen Auftrages
     */
    private Bandwidth bandwidth = null;

    protected RangierungsService rangierungsService = null;
    protected CCLeistungsService ccLeistungsService = null;
    protected EndstellenService endstellenService = null;

    @Override
    public Object executeAfterFlush() throws Exception {
        try {
            ccLeistungsService = getCCService(CCLeistungsService.class);
            rangierungsService = getCCService(RangierungsService.class);
            endstellenService = getCCService(EndstellenService.class);
            loadRequiredData();

            if (getRangierung() != null
                    && !rangierungsService.isBandwidthPossible4Rangierung(getRangierung(), getBandwidth())) {
                return handleBandwidthNotPossible();
            }
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(String.format("Fehler in der Bandbreitenprüfung:%n%s",
                    e.getMessage()), e);
        }
        return null;
    }

    /**
     * Handling für den Fall, dass die erforderliche Bandbreite von der Rangierung nicht erfuellt ist.
     *
     * @throws HurricanServiceCommandException
     */
    protected Object handleBandwidthNotPossible() throws HurricanServiceCommandException {
        throw new HurricanServiceCommandException(
                String.format(
                        "Die benötigte Bandbreite %s kann mit der zu übernehmenden Physik/Hardware nicht realisiert werden!",
                        getBandwidth())
        );
    }

    @Override
    protected void loadRequiredData() throws FindException {
        try {
            Endstelle esBSrc = endstellenService.findEndstelle4Auftrag(getAuftragIdSrc(), Endstelle.ENDSTELLEN_TYP_B);
            if ((esBSrc != null) && (esBSrc.getRangierId() != null)) {
                setRangierung(rangierungsService.findRangierung(esBSrc.getRangierId()));
            }
            setBandwidth(ccLeistungsService.findBandwidth4Auftrag(getAuftragIdDest(), false));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Rangierung.%nPrüfung der benötigten "
                    + "Bandbreite für Auftrag nicht möglich!", e);
        }
    }

    public Rangierung getRangierung() {
        return rangierung;
    }

    private void setRangierung(Rangierung rangierung) {
        this.rangierung = rangierung;
    }

    public Bandwidth getBandwidth() {
        return bandwidth;
    }

    private void setBandwidth(Bandwidth bandwidth) {
        this.bandwidth = bandwidth;
    }

}
