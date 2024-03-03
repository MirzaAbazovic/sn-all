/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 14:19:33
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.view.IPAddressSearchView;

/**
 * TableModel zur Darstellung der Suchergebnisse bei der IP-Adress Suche
 */
public class IPAddressSearchResultTableModel extends AKTableModel<IPAddressSearchView> {

    private static enum IPSearchResultColumn {
        CUSTOMER_NO("Kunde__No"),
        BILLING_ORDER_NO("Taifun-Nr."),
        IP_ADDRESS("IP-Adresse/-Netz"),
        TDN("Verbindungsbezeichnung"),
        CC_AUFTRAG_NO("Auftrag-Nr."),
        PURPOSE("Verwendungszweck"),
        GUELTIG_VON("Gültig von"),
        GUELTIG_BIS("Gültig bis"),
        ADDRESS_TYPE("Typ");

        private IPSearchResultColumn(String name) {
            this.name = name;
        }

        private final String name;

        String getName() {
            return name;
        }

        static IPSearchResultColumn forOrdinal(int ordinal) {
            return values()[ordinal];
        }

        static int columnCount() {
            return values().length;
        }
    }

    @Override
    public int getColumnCount() {
        return IPSearchResultColumn.columnCount();
    }

    @Override
    public String getColumnName(int column) {
        return IPSearchResultColumn.forOrdinal(column).getName();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (IPSearchResultColumn.forOrdinal(columnIndex)) {
            case BILLING_ORDER_NO:
                return Long.class;
            case CUSTOMER_NO:
                return Long.class;
            case CC_AUFTRAG_NO:
                return Long.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        IPAddressSearchView ipView = getDataAtRow(row);
        switch (IPSearchResultColumn.forOrdinal(column)) {
            case CUSTOMER_NO:
                return ipView.getKundeNo();
            case BILLING_ORDER_NO:
                return ipView.getBillingOrderNo();
            case IP_ADDRESS:
                return (ipView.getIpAddress() != null) ? ipView.getIpAddress().getAbsoluteAddress() : null;
            case TDN:
                return ipView.getTdn();
            case CC_AUFTRAG_NO:
                return ipView.getAuftragId();
            case PURPOSE:
                return ipView.getPurpose();
            case GUELTIG_VON:
                return DateTools.formatDate(ipView.getGueltigVon(), DateTools.PATTERN_DAY_MONTH_YEAR);
            case GUELTIG_BIS:
                return DateTools.formatDate(ipView.getGueltigBis(), DateTools.PATTERN_DAY_MONTH_YEAR);
            case ADDRESS_TYPE:
                return ipView.getIpType();
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
