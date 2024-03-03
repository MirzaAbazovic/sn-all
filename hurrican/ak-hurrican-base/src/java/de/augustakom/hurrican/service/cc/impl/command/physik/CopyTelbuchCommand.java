/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2005 13:03:33
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Command-Klasse, um die Telefonbuch-Flags zu kopieren. <br> Die eigentliche Funktion des Commands wird nur in
 * folgenden Faellen durchgefuehrt: <br> <ul> <li>Kundennummer vom Ursprungs- und Ziel-Auftrag ist identisch
 * <li>Haupt-Rufnummer des Ursprungs- und Ziel-Auftrags ist identisch </ul> <br><br> Da das Kopieren der
 * Telefonbuch-Flags keine wichtige Funktion ist, werden evtl. auftretende Fehler lediglich als Warnung protokolliert.
 * Die Command-Klasse erzeugt keine Exception, durch die eine Transaction unterbrochen wird.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.CopyTelbuchCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CopyTelbuchCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(CopyTelbuchCommand.class);

    private AuftragDaten auftragDatenSrc = null;
    private AuftragDaten auftragDatenDest = null;

    private boolean copyTelbuch = false;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object executeAfterFlush() throws Exception {
        loadRequiredData();
        if (copyTelbuch) {
            copyTelbuchData();
        }
        return null;
    }

    /*
     * Kopiert die Telefonbuch-Daten vom Ursprungs- auf den Ziel-Auftrag.
     */
    private void copyTelbuchData() {
        try {
            CCAuftragService as = (CCAuftragService) getCCService(CCAuftragService.class);

            auftragDatenDest.setTelefonbuch(auftragDatenSrc.getTelefonbuch());
            auftragDatenDest.setInverssuche(auftragDatenSrc.getInverssuche());
            as.saveAuftragDaten(auftragDatenDest, false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addWarning(this, "Die Telefonbuch-Flags wurden aus unbekanntem Grund nicht uebernommen.\n" +
                    "Bitte selbst kontrollieren und evtl. nachtragen.");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.physik.AbstractPhysikCommand#loadRequiredData()
     */
    protected void loadRequiredData() throws FindException {
        try {
            auftragDatenSrc = getAuftragDatenTx(getAuftragIdSrc());
            if (auftragDatenSrc == null || auftragDatenSrc.getTelefonbuch() == null) {
                return;
            }

            ProduktService ps = (ProduktService) getCCService(ProduktService.class);
            Produkt produktSrc = ps.findProdukt4Auftrag(getAuftragIdSrc());
            if (produktSrc == null || !produktSrc.isDnAllowed()) {
                return;
            }

            auftragDatenDest = getAuftragDatenTx(getAuftragIdDest());
            if (auftragDatenDest != null) {
                CCAuftragService as = (CCAuftragService) getCCService(CCAuftragService.class);
                Auftrag aSrc = as.findAuftragById(auftragDatenSrc.getAuftragId());
                Auftrag aDest = as.findAuftragById(auftragDatenDest.getAuftragId());
                if (NumberTools.notEqual(aSrc.getKundeNo(), aDest.getKundeNo())) {
                    return;
                }
            }

            RufnummerService rs = (RufnummerService) getBillingService(RufnummerService.class);
            Rufnummer hauptRNSrc = rs.findHauptRN4Auftrag(auftragDatenSrc.getAuftragNoOrig(), false);
            Rufnummer hauptRNDest = rs.findHauptRN4Auftrag(auftragDatenDest.getAuftragNoOrig(), true);

            if (hauptRNSrc != null && hauptRNDest != null && hauptRNSrc.isRufnummerEqual(hauptRNDest)) {
                copyTelbuch = true;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}


