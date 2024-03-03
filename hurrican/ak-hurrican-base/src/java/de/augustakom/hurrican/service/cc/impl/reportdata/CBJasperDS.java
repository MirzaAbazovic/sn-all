/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2005 16:33:13
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Jasper-DataSource, um die Daten einer Carrierbestellung, der zugehoerigen Endstelle und des verwendeten HVTs zu
 * ermitteln.
 *
 *
 */
public class CBJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(CBJasperDS.class);

    private Long cbId = null;
    private Long esId = null;
    private AKUser user = null;

    private Carrierbestellung carrierbestellung = null;
    private CarrierKennung carrierKennung = null;
    private Endstelle endstelle = null;
    private HVTStandort hvtStd = null;
    private HVTGruppe hvtGruppe = null;
    private String vbzValue = null;
    private String aiName = null;

    boolean printData = true;

    private CarrierService carrierService;
    private EndstellenService endstellenService;
    private HVTService hvtService;
    private CCAuftragService auftragService;
    private CCKundenService kundenService;

    /**
     * Konstruktor mit Angabe der ID der Carrierbestellung und der Endstellen-ID, die zu der CB gehoert.
     *
     * @param cbId ID der Carrierbestellung
     * @param esId ID der Endstelle
     * @param user aktueller Benutzer
     * @throws AKReportException wenn bei der Abfrage der Daten ein Fehler auftritt
     */
    public CBJasperDS(Long cbId, Long esId, AKUser user) throws AKReportException {
        super();
        this.cbId = cbId;
        this.esId = esId;
        this.user = user;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
        try {
            carrierService = getCCService(CarrierService.class);
            endstellenService = getCCService(EndstellenService.class);
            hvtService = getCCService(HVTService.class);
            auftragService = getCCService(CCAuftragService.class);
            kundenService = getCCService(CCKundenService.class);

            carrierbestellung = carrierService.findCB(cbId);

            // Lade Anschlussinhaber-Adresse
            if ((carrierbestellung != null) && (carrierbestellung.getAiAddressId() != null)) {
                CCAddress aiAdresse = kundenService.findCCAddress(carrierbestellung.getAiAddressId());

                if (aiAdresse != null) {
                    aiName = aiAdresse.getCombinedNameData();
                }
            }

            endstelle = endstellenService.findEndstelle(esId);
            if (endstelle != null) {
                hvtStd = hvtService.findHVTStandort(endstelle.getHvtIdStandort());
                if (hvtStd != null) {
                    hvtGruppe = hvtService.findHVTGruppeById(hvtStd.getHvtGruppeId());
                    carrierKennung = carrierService.findCarrierKennung(hvtStd.getCarrierKennungId());
                }

                AuftragTechnik auftragTechnik = auftragService.findAuftragTechnik4ESGruppe(endstelle.getEndstelleGruppeId());
                if (auftragTechnik != null) {
                    // VerbindungsBezeichnung des zu kuendigenden Auftrags suchen
                    PhysikService physikService = getCCService(PhysikService.class);
                    vbzValue = physikService.getVbzValue4TAL(auftragTechnik.getAuftragId(), endstelle.getEndstelleTyp());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Daten zur Carrierbestellung konnten nicht ermittelt werden!", e);
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    public boolean next() throws JRException {
        if (printData) {
            printData = false;
            return true;
        }
        return false;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if ("CB_BESTELLT_AM".equals(field)) {
            return (carrierbestellung != null) ? carrierbestellung.getBestelltAm() : null;
        }
        else if ("CK_NAME".equals(field)) {
            return (carrierKennung != null) ? carrierKennung.getName() : null;
        }
        else if ("CK_KUNDEN_NR".equals(field)) {
            return (carrierKennung != null) ? carrierKennung.getKundenNr() : null;
        }
        else if ("CK_PLZ".equals(field)) {
            return (carrierKennung != null) ? carrierKennung.getPlz() : null;
        }
        else if ("CK_ORT".equals(field)) {
            return (carrierKennung != null) ? carrierKennung.getOrt() : null;
        }
        else if ("CB_LBZ".equals(field)) {
            return (carrierbestellung != null) ? carrierbestellung.getLbz() : null;
        }
        else if ("CB_VTRNR".equals(field)) {
            return (carrierbestellung != null) ? carrierbestellung.getVtrNr() : null;
        }
        else if (VBZ_KEY.equals(field)) {
            return vbzValue;
        }
        else if ("BEARBEITER".equals(field)) {
            return (user != null) ? user.getName() : null;
        }
        else if ("ES_NAME".equals(field)) {
            if (StringUtils.isNotBlank(aiName)) {
                return aiName;
            }
            else if (endstelle != null) {
                return endstelle.getName();
            }
            else {
                return null;
            }
        }
        else if ("ES_ENDSTELLE".equals(field)) {
            return (endstelle != null) ? endstelle.getEndstelle() : null;
        }
        else if ("ES_PLZ_ORT".equals(field)) {
            if (endstelle != null) {
                StringBuilder plzort = new StringBuilder();
                plzort.append(endstelle.getPlz());
                plzort.append((plzort.length() > 0) ? " " : "");
                plzort.append(endstelle.getOrt());
                return plzort.toString();
            }
            return null;
        }
        else if ("HVT_ONKZ".equals(field)) {
            return (hvtGruppe != null) ? hvtGruppe.getOnkz() : null;
        }
        else if ("HVT_ORT".equals(field)) {
            return (hvtGruppe != null) ? hvtGruppe.getOrt() : null;
        }
        else if ("HVT_PLZ".equals(field)) {
            return (hvtGruppe != null) ? hvtGruppe.getPlz() : null;
        }
        else if ("HVT_STRASSE".equals(field)) {
            return (hvtGruppe != null) ? hvtGruppe.getStrasse() : null;
        }
        else if ("HVT_HAUSNR".equals(field)) {
            return (hvtGruppe != null) ? hvtGruppe.getHausNr() : null;
        }
        else if ("HVT_ASB".equals(field)) {
            return (hvtStd != null) ? hvtStd.getAsb() : null;
        }
        else if ("PHONE".equals(field)) {
            return (user != null) ? user.getPhone() : null;
        }
        else if ("FAX".equals(field)) {
            return (user != null) ? user.getFax() : null;
        }

        return super.getFieldValue(field);
    }

}
