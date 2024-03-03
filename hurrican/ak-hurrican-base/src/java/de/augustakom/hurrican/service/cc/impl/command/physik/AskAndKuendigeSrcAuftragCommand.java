/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 15:24:51
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import java.util.*;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Funktion wie <code>KuendigungSrcAuftragCommand</code>. <br> Allerdings wird zuvor ueber ein
 * <code>IServiceCallback</code> 'gefragt', ob der Ursprungs-Auftrag auf 'gekuendigt' gesetzt werden soll. <br> Nur bei
 * positiver Bestaetigung wird der Ursprungs-Auftrag automatisch gekuendigt.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.AskAndKuendigeSrcAuftragCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AskAndKuendigeSrcAuftragCommand extends KuendigungSrcAuftragCommand {

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.physik.KuendigungSrcAuftragCommand#execute()
     */
    @Override
    public Object executeAfterFlush() throws Exception {
        Object tmp = getPreparedValue(KEY_SERVICE_CALLBACK);
        IServiceCallback serviceCallback = null;
        if (tmp instanceof IServiceCallback) {
            serviceCallback = (IServiceCallback) tmp;
        }
        else {
            throw new StoreException("Es wurde kein IServiceCallback-Objekt angegeben.");
        }

        Map<String, Long> params = new HashMap<String, Long>();
        params.put(RangierungsService.CALLBACK_PARAM_AUFTRAG_ID_4_ANSCHLUSSUEBERNAHME,
                getAuftragIdSrc());

        Object result = serviceCallback.doServiceCallback(this,
                RangierungsService.CALLBACK_ASK_KUENDIGUNG_AUTOMATIC_4_ANSCHLUSSUEBERNAHME, params);
        if ((result instanceof Boolean) && ((Boolean) result).booleanValue()) {
            // fuehrt die eigentliche Status-Aenderung und Protokollierung durch...
            return super.executeAfterFlush();
        }

        return null;
    }


}


