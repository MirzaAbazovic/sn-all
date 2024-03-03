/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2007 13:56:35
 */
package de.augustakom.hurrican.dao.exmodules.sap.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jawin.win32.Ole32;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.exceptions.SAPDataAccessException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.exmodules.sap.SAPDAO;
import de.augustakom.hurrican.model.exmodules.sap.SAPBankverbindung;
import de.augustakom.hurrican.model.exmodules.sap.SAPBuchungssatz;
import de.augustakom.hurrican.model.exmodules.sap.SAPSaldo;


/**
 * DAO-Implementierung von <code>SAPDAO</code>
 *
 *
 */
public class SAPDAOImpl extends SAPDAOSupport implements SAPDAO {


    /**
     * @see de.augustakom.hurrican.dao.exmodules.sap.SAPDAO#findAusgeglichenePosten(java.lang.String)
     */
    public List<SAPBuchungssatz> findAusgeglichenePosten(String debNo) {
        IMnetSapConn sapConn = null;
        List<SAPBuchungssatz> result = new ArrayList<>();
        try {
            // SAP-Connection
            sapConn = connectSAP();

            // Query
            sapConn.ap_Query(debNo, buchungskreis);
            int i = sapConn.ap_LineLength();
            int j = sapConn.ap_MaxLength();

            // Hole Daten
            Map map = new HashMap();

            for (int t = 1; t <= j; t++) {
                // Daten werden immer Feld für Feld abgeholt
                String header = sapConn.ap_Header();
                String str = sapConn.ap_Get();

                map.put(header, str);

                // Falls alle Daten für einen Datensatz gelesen wurden, erzeuge Buchungssatz
                if (t % i == 0) {
                    SAPBuchungssatz bs = getBuchungssatz(map);
                    if (bs != null) {
                        result.add(bs);
                    }
                    map = new HashMap();
                }
            }
        }
        catch (Exception e) {
            throw new SAPDataAccessException(e);
        }
        finally {
            // disconnect
            disconnectSAP(sapConn);
        }

        // Rückgabe
        return (CollectionTools.isNotEmpty(result)) ? result : null;
    }


    /**
     * @see de.augustakom.hurrican.dao.exmodules.sap.SAPDAO#findOffenePosten(java.lang.String)
     */
    public List<SAPBuchungssatz> findOffenePosten(String debNo) {
        IMnetSapConn sapConn = null;
        List<SAPBuchungssatz> result = new ArrayList<>();
        try {
            // SAP-Connection
            sapConn = connectSAP();

            // Query
            sapConn.op_Query(debNo, buchungskreis);
            int i = sapConn.op_LineLength();
            int j = sapConn.op_MaxLength();

            // Hole Daten
            Map map = new HashMap();

            for (int t = 1; t <= j; t++) {
                // Daten werden immer Feld für Feld abgeholt
                String header = sapConn.op_Header();
                String str = sapConn.op_Get();

                map.put(header, str);

                // Falls alle Daten für einen Datensatz gelesen wurden, erzeuge Buchungssatz
                if (t % i == 0) {
                    SAPBuchungssatz bs = getBuchungssatz(map);
                    if (bs != null) {
                        result.add(bs);
                    }
                    map = new HashMap();
                }
            }
        }
        catch (Exception e) {
            throw new SAPDataAccessException(e);
        }
        finally {
            // disconnect
            disconnectSAP(sapConn);
        }

        // Rückgabe
        return (CollectionTools.isNotEmpty(result)) ? result : null;
    }


    /**
     * @see de.augustakom.hurrican.dao.exmodules.sap.SAPDAO#findBuchungssaetze(java.lang.String)
     */
    public List<SAPBuchungssatz> findBuchungssaetze(String debNo) {
        IMnetSapConn sapConn = null;
        List<SAPBuchungssatz> result = new ArrayList<>();
        try {
            // SAP-Connection
            sapConn = connectSAP();

            // Query
            sapConn.bs_Query(debNo, buchungskreis);
            int i = sapConn.bs_LineLength();
            int j = sapConn.bs_MaxLength();

            // Hole Daten
            Map map = new HashMap();

            for (int t = 1; t <= j; t++) {
                // Daten werden immer Feld für Feld abgeholt
                String header = sapConn.bs_Header();
                String str = sapConn.bs_Get();

                map.put(header, str);

                // Falls alle Daten für einen Datensatz gelesen wurden, erzeuge Buchungssatz
                if (t % i == 0) {
                    SAPBuchungssatz bs = getBuchungssatz(map);
                    if (bs != null) {
                        result.add(bs);
                    }
                    map = new HashMap();
                }
            }
        }
        catch (Exception e) {
            throw new SAPDataAccessException(e);
        }
        finally {
            // disconnect
            disconnectSAP(sapConn);
        }

        // Rückgabe
        return (CollectionTools.isNotEmpty(result)) ? result : null;
    }


    /**
     * @see de.augustakom.hurrican.dao.exmodules.sap.SAPDAO#findAktSaldo(java.lang.String)
     */
    public SAPSaldo findAktSaldo(String debNo) {
        if (debNo == null) {
            return null;
        }
        IMnetSapConn sapConn = null;
        SAPSaldo result = new SAPSaldo();
        try {
            // SAP-Connection
            sapConn = connectSAP();

            // Query
            sapConn.aktSaldo_Query(debNo, buchungskreis);
            int i = sapConn.aktSaldo_LineLength();

            // Hole Daten
            for (int h = 0; h < i; h++) {
                String header = sapConn.aktSaldo_Header();
                String str = sapConn.aktSaldo_Get();

                // Setze Daten
                if (StringUtils.equals(header, SAPSaldo.SALDO)) {
                    str = StringUtils.replace(str, ",", ".");
                    result.setSaldo(NumberUtils.createFloat(str));
                }
                else if (StringUtils.equals(header, SAPSaldo.WAEHRUNG)) {
                    result.setWaehrung(str);
                }
            }
        }
        catch (Exception e) {
            throw new SAPDataAccessException(e);
        }
        finally {
            // disconnect
            disconnectSAP(sapConn);
        }
        return result;
    }


    /**
     * @see de.augustakom.hurrican.dao.exmodules.sap.SAPDAO#findBankverbindung(java.lang.String)
     */
    public SAPBankverbindung findBankverbindung(String debNo) {
        IMnetSapConn sapConn = null;
        SAPBankverbindung result = new SAPBankverbindung();
        try {
            // SAP-Connection
            sapConn = connectSAP();

            sapConn.bank_Query(debNo, buchungskreis);
            int i = sapConn.bank_LineLength();

            for (int h = 0; h < i; h++) {
                String header = sapConn.bank_Header();
                String str = sapConn.bank_Get();

                // Setze Daten
                if (StringUtils.equals(header, SAPBankverbindung.ACCOUNT)) {
                    result.setAccount(NumberUtils.createLong(str));
                }
                else if (StringUtils.equals(header, SAPBankverbindung.BANK_LAND)) {
                    result.setBankLand(str);
                }
                else if (StringUtils.equals(header, SAPBankverbindung.BLZ)) {
                    result.setBlz(NumberUtils.createLong(str));
                }
                else if (StringUtils.equals(header, SAPBankverbindung.COLL_AUTH)) {
                    result.setCollAuth(str);
                }
            }
        }
        catch (Exception e) {
            throw new SAPDataAccessException(e);
        }
        finally {
            // disconnect
            disconnectSAP(sapConn);
        }
        return result;
    }


    /**
     * @see de.augustakom.hurrican.dao.exmodules.sap.SAPDAO#findMahnstufe(java.lang.String)
     */
    public Integer findMahnstufe(String debNo) {
        if (StringUtils.isBlank(debNo)) {
            return null;
        }
        IMnetSapConn sapConn = null;
        Integer result = null;
        try {
            // SAP-Connection
            sapConn = connectSAP();
            result = NumberUtils.createInteger(sapConn.getMahnstufe(debNo, buchungskreis));
        }
        catch (Exception e) {
            throw new SAPDataAccessException(e);
        }
        finally {
            // disconnect
            disconnectSAP(sapConn);
        }
        return result;
    }


    /* Funktion zerlegt String und erzeugt daraus ein Date-Object */
    private Date getSAPDate(String datum) {
        // String hat Format "YYYYMMDD"
        if (StringUtils.isBlank(datum) || StringUtils.equals(datum, "00000000") || (datum.length() < 8)) {
            return null;
        }

        Date date = DateTools.createDate(Integer.parseInt(StringUtils.substring(datum, 0, 4)),
                Integer.parseInt(StringUtils.substring(datum, 4, 6)) - 1,
                Integer.parseInt(StringUtils.substring(datum, 6, 8)));

        return date;
    }


    /* Funktion setzt die Attribute der Klasse SAPBuchungssatz mit den Werten aus einer HashMap */
    private SAPBuchungssatz getBuchungssatz(Map map) {
        if (map == null) {
            return null;
        }
        SAPBuchungssatz bs = new SAPBuchungssatz();

        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.ALLOC_NMBR))) {
            bs.setAllocNo((String) map.get(SAPBuchungssatz.ALLOC_NMBR));
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.BLINE_DATE))) {
            Date date = getSAPDate((String) map.get(SAPBuchungssatz.BLINE_DATE));
            bs.setBlineDate(date);
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.CLEAR_DATE))) {
            bs.setClearDate(getSAPDate((String) map.get(SAPBuchungssatz.CLEAR_DATE)));
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.DB_CR_IND))) {
            bs.setDbCrInd((String) map.get(SAPBuchungssatz.DB_CR_IND));
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.DOC_DATE))) {
            bs.setDocDate(getSAPDate((String) map.get(SAPBuchungssatz.DOC_DATE)));
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.DOC_NO))) {
            bs.setDocNo((String) map.get(SAPBuchungssatz.DOC_NO));
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.DOC_TYPE))) {
            bs.setDocType((String) map.get(SAPBuchungssatz.DOC_TYPE));
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.DUNN_AREA))) {
            bs.setDunnArea((String) map.get(SAPBuchungssatz.DUNN_AREA));
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.DUNN_LEVEL))) {
            bs.setDunnLevel(NumberUtils.createInteger((String) map.get(SAPBuchungssatz.DUNN_LEVEL)));
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.ITEM_TEXT))) {
            bs.setItemText((String) map.get(SAPBuchungssatz.ITEM_TEXT));
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.LAST_DUNN))) {
            bs.setLastDunn(getSAPDate((String) map.get(SAPBuchungssatz.LAST_DUNN)));
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.LC_AMOUNT))) {
            String str = StringUtils.replace((String) map.get(SAPBuchungssatz.LC_AMOUNT), ",", ".");
            Float betrag = NumberUtils.createFloat(str);
            bs.setLcAmount(betrag);
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.PMNTTRMS))) {
            bs.setPmntTrms((String) map.get(SAPBuchungssatz.PMNTTRMS));
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.PSTNG_DATE))) {
            bs.setPstngDate(getSAPDate((String) map.get(SAPBuchungssatz.PSTNG_DATE)));
        }
        if (StringUtils.isNotBlank((String) map.get(SAPBuchungssatz.REF_DOC_NO))) {
            bs.setRefDocNo((String) map.get(SAPBuchungssatz.REF_DOC_NO));
        }

        return bs;
    }


    /* Funktion stellt eine SAP-Connection her */
    private IMnetSapConn connectSAP() {
        IMnetSapConn sapConn = null;
        try {
            // Ermittle Betriebssystem, da SAP-Zugriff nur unter Windows möglich ist
            if (StringUtils.contains(System.getProperty("os.name"), "Windows")) {
                // SAP-Connection
                Ole32.CoInitialize();
                sapConn = new IMnetSapConn(MnetSapConnControl.CLSID);
                sapConn.connect_PR4(sapUser, sapPasswort);
            }
            else {
                throw new SAPDataAccessException("SAP-Zugriff nur unter Windows möglich");
            }
            return sapConn;
        }
        catch (Exception e) {
            if (sapConn != null) {
                // disconnect
                try {
                    sapConn.disconnect();
                    Ole32.CoUninitialize();
                }
                catch (Exception ex) {
                    throw new SAPDataAccessException(ex);
                }
            }
            throw new SAPDataAccessException(e);
        }
    }


    /* Funktion stellt eine SAP-Connection her */
    private void disconnectSAP(IMnetSapConn sapConn) {
        if (sapConn == null) {
            return;
        }
        try {
            // SAP-Connection schließen
            sapConn.disconnect();
            Ole32.CoUninitialize();
        }
        catch (Exception e) {
            throw new SAPDataAccessException(e);
        }
    }

}


