/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.2016
 */
package de.augustakom.hurrican.gui.auftrag.wizards.tablemodels;

import java.text.*;
import java.util.*;
import javax.swing.table.*;

import de.augustakom.hurrican.model.cc.ProfileAuftrag;

public class ProfileAuftragTableModel extends AbstractTableModel {
    private static final String BEARBEITER = "Bearbeiter";
    private static final String GRUND = "Änderungsgrund";
    private static final String BEMERKUNG = "Bemerkung";
    private static final String VON = "gültig von";
    private static final String BIS = "gültig bis";
    private List<ProfileAuftrag> profiles;
    private static final String[] columns = new String[] {
            BEARBEITER, GRUND, BEMERKUNG, VON, BIS };

    public ProfileAuftragTableModel(List<ProfileAuftrag> profiles) {
        super();
        this.profiles = profiles;
        sort();
    }

    private void sort() {
        profiles.sort(Comparator.comparing(ProfileAuftrag::getGueltigBis).reversed());
    }

    public ProfileAuftrag getProfileAuftrag(int row) {
        return profiles.get(row);
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public int getRowCount() {
        return profiles.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        ProfileAuftrag profileAuftrag = getProfileAuftrag(row);
        switch (columns[col]) {
            case BEARBEITER:
                return profileAuftrag.getUserW();
            case GRUND:
                return profileAuftrag.getChangeReason().getName();
            case BEMERKUNG:
                return profileAuftrag.getBemerkung();
            case VON:
                return formatDate(profileAuftrag.getGueltigVon());
            case BIS:
                return formatDate(profileAuftrag.getGueltigBis());
            default:
                return null;
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(date);
    }
}