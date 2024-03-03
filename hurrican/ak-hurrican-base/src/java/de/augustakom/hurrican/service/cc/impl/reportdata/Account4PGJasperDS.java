/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.12.2005 15:53:41
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Jasper DataSource, um die Account-Daten eines Kunden fuer eine bestimmte Produktgruppe zu laden. <br>
 *
 *
 */
public class Account4PGJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(Account4PGJasperDS.class);

    private Long kundeNoOrig = null;
    private Long[] produktGruppen = null;
    private List<AuftragIntAccountView> intAccountViews = null;
    private Iterator<AuftragIntAccountView> dataIterator = null;
    private AuftragIntAccountView currentData = null;
    private Reference einwahlRNReference = null;
    private String prodName = null;

    private ProduktService prodService = null;
    private ReferenceService refService = null;

    /**
     * Konstruktor mit Angabe der (original) Kundennummer des Kunden, dessen Account-Daten fuer eine bestimmte
     * Produktgruppe ermittelt werden sollen.
     *
     * @param kundeNoOrig    (original) Kundennummer
     * @param produktGruppen Array mit den IDs der Produkt-Gruppen
     * @throws AKReportException wenn bei der Daten-Ermittlung ein Fehler auftritt.
     */
    public Account4PGJasperDS(Long kundeNoOrig, Long[] produktGruppen) throws AKReportException {
        super();
        this.kundeNoOrig = kundeNoOrig;
        this.produktGruppen = (Long[]) ArrayUtils.clone(produktGruppen);
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init() Laedt die Account-Daten des
     * Kunden fuer die angegebenen Produkt-Gruppen.
     */
    @Override
    protected void init() throws AKReportException {
        try {
            this.dataIterator = null;

            List<Long> pgIDs = new ArrayList<Long>();
            CollectionUtils.addAll(pgIDs, produktGruppen);

            prodService = getCCService(ProduktService.class);
            refService = getCCService(ReferenceService.class);
            AccountService accs = getCCService(AccountService.class);
            intAccountViews = accs.findAuftragAccountView(kundeNoOrig, pgIDs);
            if (intAccountViews != null) {
                this.dataIterator = intAccountViews.iterator();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Online-Auftraege und Accounts konnten nicht ermittelt werden!", e);
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    @Override
    public boolean next() throws JRException {
        boolean hasNext = false;
        einwahlRNReference = null;

        if (this.dataIterator != null) {
            hasNext = this.dataIterator.hasNext();
            if (hasNext) {
                this.currentData = this.dataIterator.next();
                try {
                    prodName = prodService.generateProduktName4Auftrag(this.currentData.getAuftragId());

                    Long prodId = currentData.getProdId();
                    einwahlRNReference = refService.findReference(
                            Reference.REF_TYPE_EINWAHLDN_4_PRODUKT, (prodId == null ? null : prodId.intValue()));
                }
                catch (Exception e) {
                    throw new JRException("Einwahlrufnummer konnte nicht ermittelt werden!");
                }
            }
        }
        return hasNext;
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if (currentData != null) {
            if ("PRODUKT".equals(field)) {
                return prodName;
            }
            else if (VBZ_KEY.equals(field)) {
                return currentData.getVbz();
            }
            else if ("PASSWORT".equals(field)) {
                if (NumberTools.equal(IntAccount.LINR_EINWAHLACCOUNT, currentData.getAccountLiNr())) {
                    return currentData.getAccountPasswort();
                }
            }
            else if ("BENUTZERNAME".equals(field)) {
                if (NumberTools.equal(IntAccount.LINR_EINWAHLACCOUNT, currentData.getAccountLiNr())) {
                    return currentData.getAccount();
                }
            }
            else if ("AUFTRAG_ID".equals(field)) {
                return currentData.getAuftragId();
            }
            else if ("RUFNUMMER".equals(field)) {
                String rufnummer = StringUtils.isNotBlank(currentData.getAccountRufnummer())
                        ? currentData.getAccountRufnummer() : null;
                return rufnummer;
            }
            else if ("EINWAHL_RN".equals(field)) {
                return ((einwahlRNReference != null) && StringUtils.isNotBlank(einwahlRNReference.getStrValue()))
                        ? einwahlRNReference.getStrValue() : null;
            }
        }
        return null;
    }

}


