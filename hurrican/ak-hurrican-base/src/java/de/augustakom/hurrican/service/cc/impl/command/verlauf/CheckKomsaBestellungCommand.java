/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.02.2013 09:05:54
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.PurchaseOrder;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.service.billing.PurchaseOrderService;


/**
 * Command prueft ob alle KOMSA-Bestellungen des zugehörigen Taifun-Auftrags aktiviert sind. Ein Fehler wird generiert,
 * wenn minimum eine Bestellung noch nicht aktiviert wurde.
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckKomsaBestellungCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckKomsaBestellungCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckKomsaBestellungCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.billing.PurchaseOrderService")
    private PurchaseOrderService purchaseOrderService;

    @Override
    public Object execute() throws Exception {
        if (!executeKomsaCheck()) {
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }

        try {
            BAuftrag billingAuftrag = getBillingAuftrag();

            List<PurchaseOrder> purchaseOrders = purchaseOrderService.findPurchaseOrders4Auftrag(billingAuftrag.getAuftragNoOrig());
            if (CollectionTools.isNotEmpty(purchaseOrders)) {
                Collection<PurchaseOrder> komsaPurchaseOrders = filterKomsa(purchaseOrders);
                for (PurchaseOrder purchaseOrder : komsaPurchaseOrders) {
                    if (purchaseOrder.isNotActivated()) {
                        addWarning(this, "Mindestens eine KOMSA-Bestellung ist noch nicht aktiviert.");
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    String.format("Bei der Ueberpruefung des PurchaseOrders ist ein Fehler aufgetreten: %s", e.getMessage()), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    /* Filtert aus den Bestellungen nur die heraus, die fuer KOMSA gedacht sind. */
    private Collection<PurchaseOrder> filterKomsa(List<PurchaseOrder> purchaseOrders) {
        Collection<PurchaseOrder> komsaPurchaseOrders = Collections2.filter(purchaseOrders, new Predicate<PurchaseOrder>() {
            @Override
            public boolean apply(@Nullable PurchaseOrder input) {
                if (input != null && input.isSupplierKomsa()) {
                    return true;
                }
                return false;
            }
        });
        return komsaPurchaseOrders;
    }

    /**
     * KOMSA-Check soll nur bei bestimmten Bauauftrags-Anlaessen ausgefuehrt werden!
     */
    boolean executeKomsaCheck() {
        return NumberTools.isIn(getAnlassId(), new Long[] {
                BAVerlaufAnlass.NEUSCHALTUNG,
                BAVerlaufAnlass.ABW_TKG46_AENDERUNG,
                BAVerlaufAnlass.ABW_TKG46_NEUSCHALTUNG,
                BAVerlaufAnlass.AENDERUNG_BANDBREITE,
                BAVerlaufAnlass.ANSCHLUSSUEBERNAHME
        });
    }

}


