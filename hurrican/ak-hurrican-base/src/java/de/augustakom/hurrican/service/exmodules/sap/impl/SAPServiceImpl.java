/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.2007 14:16:13
 */
package de.augustakom.hurrican.service.exmodules.sap.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.DesEncrypter;
import de.augustakom.hurrican.dao.exmodules.sap.SAPDAO;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.exmodules.sap.SAPBankverbindung;
import de.augustakom.hurrican.model.exmodules.sap.SAPBuchungssatz;
import de.augustakom.hurrican.model.exmodules.sap.SAPSaldo;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.augustakom.hurrican.service.exmodules.sap.SAPService;


/**
 * Service-Implementierung fuer SAP-Funktionen. <br>
 *
 *
 */
public class SAPServiceImpl extends DefaultSAPService implements SAPService, InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(SAPServiceImpl.class);


    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        try {
            RegistryService rs = (RegistryService) getCCService(RegistryService.class);
            String sapUser = rs.getStringValue(RegistryService.REGID_SAP_USER);
            String sapPasswort = rs.getStringValue(RegistryService.REGID_SAP_PASSWORT);
            sapPasswort = DesEncrypter.getInstance().decrypt(sapPasswort);
            String buchungskreis = rs.getStringValue(RegistryService.REGID_SAP_BUCHUNGSKREIS);

            ((SAPDAO) getDAO()).setConnectionData(sapUser, sapPasswort, buchungskreis);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.exmodules.sap.SAPService#findAusgeglichenePosten(java.lang.String)
     */
    public List<SAPBuchungssatz> findAusgeglichenePosten(String debNo) throws FindException {
        try {
            List<SAPBuchungssatz> list = ((SAPDAO) getDAO()).findAusgeglichenePosten(debNo);
            return modifyBuchungssaetze(list);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.exmodules.sap.SAPService#findAktSaldo(java.lang.String)
     */
    public SAPSaldo findAktSaldo(String debNo) throws FindException {
        try {
            return ((SAPDAO) getDAO()).findAktSaldo(debNo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.exmodules.sap.SAPService#findBankverbindung(java.lang.String)
     */
    public SAPBankverbindung findBankverbindung(String debNo) throws FindException {
        try {
            return ((SAPDAO) getDAO()).findBankverbindung(debNo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.exmodules.sap.SAPService#findBuchungssaetze(java.lang.String)
     */
    public List<SAPBuchungssatz> findBuchungssaetze(String debNo) throws FindException {
        try {
            List<SAPBuchungssatz> list = ((SAPDAO) getDAO()).findBuchungssaetze(debNo);
            return modifyBuchungssaetze(list);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.exmodules.sap.SAPService#findMahnstufe(java.lang.String)
     */
    public Integer findMahnstufe(String debNo) throws FindException {
        try {
            // Betrachte Mahnstufe der offenen Posten
            Integer mahnstufe = null;
            List<SAPBuchungssatz> list = ((SAPDAO) getDAO()).findOffenePosten(debNo);
            if (CollectionTools.isNotEmpty(list)) {
                for (SAPBuchungssatz bs : list) {
                    if (bs.getDunnLevel() != null
                            && (mahnstufe == null || bs.getDunnLevel() > mahnstufe)) {
                        mahnstufe = bs.getDunnLevel();
                    }
                }
            }
            // findMahnstufe() liefert nach SAP Logik die höchste Mahnstufe zurück.
            // Die höchste Mahnstufe wird im SAP aber erst mit dem nächsten Mahnlauf geändert.
            // Wenn seine Offenen Posten keine Mahnstufe mehr aufweisen, muss die die
            // Mahnstufe auf 0 gesetzt werden.
            return ((mahnstufe == null) || (mahnstufe == 0)) ? Integer.valueOf(0) : ((SAPDAO) getDAO()).findMahnstufe(debNo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.exmodules.sap.SAPService#findOffenePosten(java.lang.String)
     */
    public List<SAPBuchungssatz> findOffenePosten(String debNo) throws FindException {
        try {
            List<SAPBuchungssatz> list = ((SAPDAO) getDAO()).findOffenePosten(debNo);
            return modifyBuchungssaetze(list);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /* Korrigiert einzelne Werte im Datensatz "Buchungssatz" */
    private List<SAPBuchungssatz> modifyBuchungssaetze(List<SAPBuchungssatz> list) throws FindException {
        if (CollectionTools.isEmpty(list)) {
            return null;
        }
        try {
            ReferenceService refService = (ReferenceService) getCCService(ReferenceService.class);

            for (SAPBuchungssatz bs : list) {
                // Falls Haben-Buchung, multipliziere Betrag mit -1
                if (StringUtils.equals(bs.getDbCrInd(), "H") && (bs.getLcAmount() != null)) {
                    bs.setLcAmount(bs.getLcAmount() * -1);
                }

                // Berechne Nettofälligkeit (addiere Zahlungbedingung)
                if (StringUtils.isNotBlank(bs.getPmntTrms()) && (bs.getBlineDate() != null)) {
                    Reference zahl = refService.findReference("SAP_ZAHLUNGSBEDINGUNG", StringUtils.trimToEmpty(bs.getPmntTrms()));
                    if (zahl == null) {
                        throw new FindException("Keine entsprechende Zahlungsbedingung gefunden");
                    }
                    if ((zahl.getIntValue() != null) && (zahl.getIntValue() != 0)) {
                        Reference unit = refService.findReference(zahl.getUnitId());
                        if (unit != null) {
                            if (StringUtils.equals(unit.getStrValue(), "Tage")) {
                                bs.setBlineDate(DateTools.changeDate(bs.getBlineDate(), Calendar.DAY_OF_MONTH, zahl.getIntValue()));
                            }
                            else if (StringUtils.equals(unit.getStrValue(), "Monate")) {
                                bs.setBlineDate(DateTools.changeDate(bs.getBlineDate(), Calendar.MONTH, zahl.getIntValue()));
                            }
                            else {
                                throw new FindException("Keine Einheit für Zahlungsbedingung gefunden");
                            }
                        }
                        else {
                            throw new FindException("Keine Einheit für Zahlungsbedingung gefunden");
                        }
                    }
                }
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e);
        }
    }

}
