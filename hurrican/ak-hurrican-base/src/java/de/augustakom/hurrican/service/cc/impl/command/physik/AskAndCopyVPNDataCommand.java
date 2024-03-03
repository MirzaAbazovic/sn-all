/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2005 07:53:07
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VPNKonfiguration;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.augustakom.hurrican.service.utils.HistoryHelper;


/**
 * Command-Klasse, um dem neuen Auftrag die VPN-Daten des alten Auftrags zuzuordnen. <br> Die Zuordnung wird nur dann
 * ausgefuehrt, wenn sich der Source- und Ziel-Auftrag im gleichen Kundenkreis (Hauptkunde, Unterkunde) befinden.
 * Ausserdem wird vor der Uebernahme noch 'gefragt', ob die VPN-Daten wirklich uebernommen werden sollen.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.AskAndCopyVPNDataCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AskAndCopyVPNDataCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(AskAndCopyVPNDataCommand.class);

    private AuftragTechnik auftragTechnikSrc = null;
    private AuftragTechnik auftragTechnikDest = null;
    private VPN vpnSrc = null;
    private VPNKonfiguration vpnKonfSrc = null;

    private boolean copyVPN = false;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object executeAfterFlush() throws Exception {
        loadRequiredData();
        if (copyVPN) {
            copyVPNData();
        }

        return null;
    }

    /*
     * Kopiert die VPN-Daten des Ursprungs- in den Ziel-Auftrag.
     */
    private void copyVPNData() throws StoreException {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            auftragTechnikDest.setVpnId(auftragTechnikSrc.getVpnId());
            as.saveAuftragTechnik(auftragTechnikDest, false);

            if (vpnKonfSrc != null) {
                VPNKonfiguration newVPNKonf = new VPNKonfiguration();
                PropertyUtils.copyProperties(newVPNKonf, vpnKonfSrc);
                newVPNKonf.setAuftragId(getAuftragIdDest());
                HistoryHelper.setHistoryData(newVPNKonf, new Date());

                VPNService vpnService = getCCService(VPNService.class);
                vpnService.saveVPNKonfiguration(newVPNKonf, false);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addWarning(this, "Beim Kopieren der VPN-Daten ist ein unerwarteter Fehler aufgetreten. " +
                    "Bitte uebernehmen Sie diese Daten selbst.");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.physik.AbstractPhysikCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        try {
            auftragTechnikSrc = getAuftragTechnikTx(getAuftragIdSrc());
            if ((auftragTechnikSrc == null) || (auftragTechnikSrc.getVpnId() == null)) {
                return;
            }

            // pruefen, ob die Auftraege dem gleichen Kundenkreis angehoeren
            CCAuftragService as = getCCService(CCAuftragService.class);
            Auftrag auftragSrc = as.findAuftragById(getAuftragIdSrc());
            Auftrag auftragDest = as.findAuftragById(getAuftragIdDest());

            KundenService ks = getBillingService(KundenService.class);
            if (!ks.isSameKundenkreis(auftragSrc.getKundeNo(), auftragDest.getKundeNo())) {
                LOGGER.info("Auftraege sind nicht im gleichen Kundenkreis - VPN-Uebernahme nicht moeglich!");
                return;
            }

            // ServiceCallback-Objekt ermitteln
            Object tmp = getPreparedValue(KEY_SERVICE_CALLBACK);
            IServiceCallback serviceCallback = null;
            if (tmp instanceof IServiceCallback) {
                serviceCallback = (IServiceCallback) tmp;
            }
            else {
                throw new FindException("IServiceCallback konnte nicht ermittelt werden!");
            }

            auftragTechnikDest = getAuftragTechnikTx(getAuftragIdDest());
            if (auftragTechnikDest == null) {
                throw new FindException("AuftragTechnik von Ziel-Auftrag nicht gefunden! Auftrags-ID: " + getAuftragIdDest());
            }

            VPNService vpnService = getCCService(VPNService.class);
            vpnSrc = vpnService.findVPN(auftragTechnikSrc.getVpnId());
            vpnKonfSrc = vpnService.findVPNKonfiguration4Auftrag(getAuftragIdSrc());

            Map<String, Long> params = new HashMap<String, Long>();
            params.put(RangierungsService.CALLBACK_PARAM_VPN_NR, vpnSrc.getVpnNr());
            Object result = serviceCallback.doServiceCallback(this,
                    RangierungsService.CALLBACK_ASK_4_VPN_UEBERNAHME, params);
            copyVPN = ((result instanceof Boolean) && BooleanTools.nullToFalse((Boolean) result)) ? true : false;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addWarning(this, "Es konnte nicht ermittelt werden, ob VPN-Daten kopiert werden muessen. " +
                    "Bitte selbst pruefen.");
        }
    }

}


