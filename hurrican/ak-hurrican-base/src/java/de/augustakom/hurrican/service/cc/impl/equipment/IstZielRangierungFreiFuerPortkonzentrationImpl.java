/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.2014 11:03
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import javax.annotation.*;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 *
 */
public class IstZielRangierungFreiFuerPortkonzentrationImpl implements IstRangierungFreiFuerPortkonzentration {
    public IstZielRangierungFreiFuerPortkonzentrationImpl(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    private final CCAuftragService auftragService;

    private boolean rangierungIstFreiFuerPortkonzentration(Rangierung rangierung) throws FindException {
        final boolean rangierungIstFrei;
        if (rangierung == null) {
            rangierungIstFrei = true;
        }
        else {
            if (Rangierung.RANGIERUNG_NOT_ACTIVE.equals(rangierung.getEsId())) {
                rangierungIstFrei = false;
            }
            else if (rangierung.getEsId() == null) {
                rangierungIstFrei = !Rangierung.Freigegeben.WEPLA.equals(rangierung.getFreigegeben());
            }
            else {
                final AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelleTx(rangierung.getEsId());
                rangierungIstFrei = auftragDaten.isAuftragClosed();
            }
        }
        return rangierungIstFrei;
    }

    @Nullable
    @Override
    public Boolean apply(@Nullable Rangierung input) {
        try {
            return this.rangierungIstFreiFuerPortkonzentration(input);
        }
        catch (FindException e) {
            throw new RuntimeException(e);
        }
    }
}
