/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.10.2016
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;

@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckGFastModemCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckGFastModemCommand extends AbstractVerlaufCheckCommand {
    private static final long G_FAST_PHYSIK_ID = 810L;
    private static final long G_FAST_MODEM_ID = 666L;
    private static final String NO_MODEM_WARNING = "Diesem Auftrag ist kein Modem zugeordnet";

    @Override
    public Object execute() throws Exception {
        if (!isFTTXDSL() && isPhysicTypGFast()) {
            List<EG2AuftragView> eg2Auftrags = getTerminals4Order();
            if (!containsGFastModem(eg2Auftrags))
                addWarning(this, NO_MODEM_WARNING);
        }
        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    private boolean isFTTXDSL() {
        return Produkt.PROD_ID_FTTX_DSL_FON.equals(getProdukt().getProdId());
    }

    private boolean containsGFastModem(List<EG2AuftragView> egs) {
        return egs != null
                && egs.stream().filter(eg -> eg.getEgId() == G_FAST_MODEM_ID).findAny().isPresent();
    }

    private List<EG2AuftragView> getTerminals4Order() throws ServiceNotFoundException, FindException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        return endgeraeteService.findEG2AuftragViews(getAuftragId());
    }

    private boolean isPhysicTypGFast() throws ServiceNotFoundException, FindException {
        final Endstelle endstelle = findEnstelle();
        if (endstelle != null && endstelle.getRangierId() != null ) {
            final Rangierung rangierung = findRangierung(endstelle);
            return rangierung != null
                    && rangierung.getPhysikTypId() != null
                    && rangierung.getPhysikTypId() == G_FAST_PHYSIK_ID;
        }
        return false;
    }

    private Endstelle findEnstelle() throws ServiceNotFoundException, FindException {
        EndstellenService endstellenService = getCCService(EndstellenService.class);
        return endstellenService.findEndstelle4Auftrag(getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
    }

    private Rangierung findRangierung(Endstelle endstelle) throws ServiceNotFoundException, FindException {
        RangierungsService rangierungsService = getCCService(RangierungsService.class);
        return rangierungsService.findRangierung(endstelle.getRangierId());
    }
}