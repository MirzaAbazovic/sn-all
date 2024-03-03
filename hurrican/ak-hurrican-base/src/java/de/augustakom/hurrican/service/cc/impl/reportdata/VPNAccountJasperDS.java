/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2005 07:46:19
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.VPNService;


/**
 * Jasper-DataSource, um die Account- und Endstellen-Daten eines Auftrags zu ermitteln, der einem VPN zugeordnet ist.
 *
 *
 */
public class VPNAccountJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(VPNAccountJasperDS.class);

    private Long auftragId = null;

    private IntAccount intAccount = null;
    private VPN vpn = null;
    private List<Endstelle> endstellen = null;
    private Iterator<Endstelle> dataIterator = null;
    private Produkt produkt = null;
    private Endstelle currentES = null;

    /**
     * Konstruktor mit Angabe der ID eines Auftrags, der einem VPN zugeordnet ist.
     *
     * @param auftragId Auftrags-ID
     * @throws AKReportException
     */
    public VPNAccountJasperDS(Long auftragId) throws AKReportException {
        super();
        this.auftragId = auftragId;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragTechnik at = as.findAuftragTechnikByAuftragId(auftragId);
            if (at == null) {
                throw new AKReportException("Die Auftrags-Daten konnten nicht ermittelt werden!");
            }
            if ((at.getVpnId() == null) || (at.getIntAccountId() == null)) {
                throw new AKReportException("Der Auftrag ist keinem VPN zugeordnet oder besitzt keinen Account!");
            }

            AccountService accs = getCCService(AccountService.class);
            intAccount = accs.findIntAccountById(at.getIntAccountId());

            VPNService vpns = getCCService(VPNService.class);
            vpn = vpns.findVPN(at.getVpnId());

            ProduktService ps = getCCService(ProduktService.class);
            produkt = ps.findProdukt4Auftrag(auftragId);

            EndstellenService esSrv = getCCService(EndstellenService.class);
            endstellen = esSrv.findEndstellen4Auftrag(auftragId);
            if (endstellen != null) {
                this.dataIterator = endstellen.iterator();
            }
        }
        catch (AKReportException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Report-Daten für VPN-Account konnten nicht ermittelt werden!", e);
        }
    }

    int count = 0;

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    public boolean next() throws JRException {
        boolean hasNext = false;

        if (this.dataIterator != null) {
            hasNext = this.dataIterator.hasNext();
            if (hasNext) {
                this.currentES = this.dataIterator.next();
            }
        }
        return hasNext;
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
     */
    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        if ("VPN_NR".equals(jrField.getName())) {
            return (vpn != null) ? vpn.getVpnNr() : "";
        }
        else if ("ACCOUNT".equals(jrField.getName())) {
            return (intAccount != null) ? intAccount.getAccount() : "";
        }
        else if ("PASSWORD".equals(jrField.getName())) {
            return (intAccount != null) ? intAccount.getPasswort() : "";
        }
        else if ("PRODUKT".equals(jrField.getName())) {
            return (produkt != null) ? produkt.getAnschlussart() : "";
        }
        else if ("ES_TYP".equals(jrField.getName())) {
            return (currentES != null) ? currentES.getEndstelleTyp() : "";
        }
        else if ("ES_ANSCHLUSSART".equals(jrField.getName())) {
            if (currentES != null) {
                try {
                    PhysikService ps = getCCService(PhysikService.class);
                    Anschlussart ansArt = ps.findAnschlussart(currentES.getAnschlussart());
                    return (ansArt != null) ? ansArt.getAnschlussart() : "";
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            return "";
        }
        else if ("ES_ENDSTELLE".equals(jrField.getName())) {
            return (currentES != null) ? currentES.getEndstelle() : "";
        }
        else if ("ES_NAME".equals(jrField.getName())) {
            return (currentES != null) ? currentES.getName() : "";
        }
        else if ("ES_ORT".equals(jrField.getName())) {
            return (currentES != null) ? currentES.getOrt() : "";
        }
        else if ("ES_PLZ".equals(jrField.getName())) {
            return (currentES != null) ? currentES.getPlz() : "";
        }
        return null;
    }

}


