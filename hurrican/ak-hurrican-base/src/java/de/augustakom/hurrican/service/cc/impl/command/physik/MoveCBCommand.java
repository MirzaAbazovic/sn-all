/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.09.2005 08:04:03
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;


/**
 * Command-Klasse, um die Carrierbestellungen der Endstelle B des Ursprungs-Auftrags auf den Ziel-Auftrag (bzw. dessen
 * Endstelle B) zu uebertragen. <br> Ausserdem wird aus der letzten/aktuellsten Carrierbestellung die Kuendigung
 * ausgetragen.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.MoveCBCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MoveCBCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(MoveCBCommand.class);

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object executeAfterFlush() throws Exception {
        moveCB();
        return null;
    }

    /*
     * Uebertraegt die Carrierbestellungen des Ursprungs- auf den Ziel-Auftrag.
     * Von der letzten CB wird die Kuendigung entfernt.
     */
    private void moveCB() throws StoreException {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            Endstelle esBSrc = esSrv.findEndstelle4Auftrag(getAuftragIdSrc(), Endstelle.ENDSTELLEN_TYP_B);
            Endstelle esBDest = esSrv.findEndstelle4Auftrag(getAuftragIdDest(), Endstelle.ENDSTELLEN_TYP_B);
            if (esBSrc == null || esBDest == null) {
                throw new FindException("Endstelle B des Ursprungs- oder Ziel-Auftrags nicht gefunden!");
            }

            esBDest.setCb2EsId(esBSrc.getCb2EsId());
            esSrv.saveEndstelle(esBDest);

            CarrierService cs = getCCService(CarrierService.class);
            List<Carrierbestellung> cbs = cs.findCBs4EndstelleTx(esBSrc.getId());
            if (cbs != null && !cbs.isEmpty()) {
                Carrierbestellung cb = cbs.get(0);
                cb.setKuendigungAnCarrier(null);
                cb.setKuendBestaetigungCarrier(null);

                if (cb.getAiAddressId() == null) {
                    // Standortadresse der "alten" Endstelle ermitteln und als
                    // Anschlussinhaberadresse verwenden
                    AddressModel address =
                            esSrv.findAnschlussadresse4Auftrag(getAuftragIdSrc(), esBSrc.getEndstelleTyp());
                    if (address != null) {
                        // Adresse als Anschlussinhaberadresse kopieren
                        CCAddress aiAdr = new CCAddress();
                        PropertyUtils.copyProperties(aiAdr, address);
                        aiAdr.setAddressType(CCAddress.ADDRESS_TYPE_ACCESSPOINT_OWNER);

                        CCKundenService ks = getCCService(CCKundenService.class);
                        ks.saveCCAddress(aiAdr);

                        cb.setAiAddressId(aiAdr.getId());
                    }
                }

                cs.saveCB(cb);
            }
            else {
                addWarning(this, "Es konnte keine Carrierbestellung ermittelt werden, die auf den " +
                        "Ziel-Auftrag uebertragen werden soll!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addWarning(this, "Die Kuendigung der urspruenglichen Carrierbestellung konnte nicht rueckgaengig " +
                    "gemacht werden.\nGrund: " + e.getMessage());
        }
    }

}


