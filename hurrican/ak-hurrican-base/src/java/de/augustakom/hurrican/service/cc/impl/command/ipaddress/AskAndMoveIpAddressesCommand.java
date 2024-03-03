/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.01.2013 11:32:21
 */
package de.augustakom.hurrican.service.cc.impl.command.ipaddress;

import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.IPAddressService;

/**
 * Funktion wie Super-Klasse {@link MoveIpAddressesCommand}.<br> Allerdings wird der Benutzer zunaechst 'gefragt' (ueber
 * das Interface IServiceCallback), ob die Uebernahme durchgefuehrt werden soll. <br> WICHTIG: die Frage, ob die IP
 * Adressen uebernommen werden sollen, wird nur dann gestellt, wenn die beteiligten Auftraege dem gleichen(!) Kunden
 * zugeordnet sind und sich die Billingauftragsnummern unterscheiden.
 */
@CcTxRequired
public class AskAndMoveIpAddressesCommand extends MoveIpAddressesCommand {

    @Override
    public Object execute() throws Exception {
        try {
            if (isSameCustomer() && !isSameBillingOrder() && isMoveNecessary() && askMoveIpAddresses()) {
                return super.execute();
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(String.format("Die IP Adressuebernahme ist fehlgeschlagen: %s ",
                    e.getMessage()), e);
        }
        return null;
    }

    /**
     * Nachfrage ueber ein ServiceCallback, ob die IP Adressen uebernommen werden sollen.
     */
    private boolean askMoveIpAddresses() {
        IServiceCallback serviceCallback = getServiceCallback();
        if (serviceCallback != null) {
            Object result = getServiceCallback().doServiceCallback(this,
                    IPAddressService.CALLBACK_ASK_4_IP_ADDRESS_UEBERNAHME, null);
            if ((result instanceof Boolean) && ((Boolean) result).booleanValue()) {
                // IP Adressen uebernehmen
                return true;
            }
        }
        return false;
    }
}


