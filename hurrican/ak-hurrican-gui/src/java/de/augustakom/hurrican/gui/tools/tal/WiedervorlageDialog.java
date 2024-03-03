/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.03.2012 11:28:46
 */
package de.augustakom.hurrican.gui.tools.tal;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import javax.swing.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;

/**
 * Dialog zur Spezifizierung von Wiedervorlage-Zeitraeumen fuer Usertasks
 */
public class WiedervorlageDialog extends AbstractServiceOptionDialog implements ItemListener {

    private static final long serialVersionUID = 7286891124549326314L;

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/resources/WiedervorlageDialog.xml";

    private static final String WIEDERVORLAGE_TYP = "wiedervorlage.typ";
    private static final String WIEDERVORLAGE_DATUM = "wiedervorlage.datum";
    private static final String WIEDERVORLAGE_UHRZEIT = "wiedervorlage.uhrzeit";
    private static final String WIEDERVORLAGE_ZEITSPANNE = "wiedervorlage.zeitspanne";
    private static final String WIEDERVORLAGE_ZEITEINHEIT = "wiedervorlage.zeiteinheit";
    private static final String WIEDERVORLAGE_STATUSBEMERKUNG = "wiedervorlage.statusbemerkung";

    private static final String WIEDERVORLAGE_TYP_DATUM = "Datum";
    private static final String WIEDERVORLAGE_TYP_ZEITSPANNE = "Zeitspanne";

    private static final String ZEITEINHEIT_TAG = "Tag(e)";
    private static final String ZEITEINHEIT_STUNDE = "Stunde(n)";
    private static final String ZEITEINHEIT_MINUTE = "Minute(n)";

    private static final String DEFAULT_UHRZEIT = "12:00";
    private static final String DEFAULT_ZEITEINHET = "1";

    private AKJComboBox cbWiedervorlageTyp;
    private AKJDateComponent dcWiedervorlageDatum;
    private AKJTextField taUhrzeit;
    private AKJTextField taZeitspanne;
    private AKJComboBox cbZeiteinheit;
    private AKJTextArea taStatusBemerkung;

    private LocalDateTime wiedervorlageDatum;
    private String statusBemerkung;
    private final Boolean showBemerkung;

    public WiedervorlageDialog(LocalDateTime wiedervorlageDatum, String statusBemerkung, Boolean showBemerkung) {
        super(RESOURCE);
        this.wiedervorlageDatum = wiedervorlageDatum;
        this.statusBemerkung = statusBemerkung;
        this.showBemerkung = showBemerkung;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, "Wiedervorlage speichern", "Speichert die Wiedervorlage und Task wird ausgeblendet",
                true, true);

        AKJPanel details = new AKJPanel(new GridBagLayout());

        AKJLabel lblWiedervorlageTyp = getSwingFactory().createLabel(WIEDERVORLAGE_TYP);
        cbWiedervorlageTyp = getSwingFactory().createComboBox(WIEDERVORLAGE_TYP);
        cbWiedervorlageTyp.addItemListener(this);

        AKJLabel lblWiedervorlageDatum = getSwingFactory().createLabel(WIEDERVORLAGE_DATUM);
        dcWiedervorlageDatum = getSwingFactory().createDateComponent(WIEDERVORLAGE_DATUM);

        AKJLabel lblUhrzeit = getSwingFactory().createLabel(WIEDERVORLAGE_UHRZEIT);
        taUhrzeit = getSwingFactory().createTextField(WIEDERVORLAGE_UHRZEIT);

        AKJLabel lblWiedervorlageZeitspanne = getSwingFactory().createLabel(WIEDERVORLAGE_ZEITSPANNE);
        taZeitspanne = getSwingFactory().createTextField(WIEDERVORLAGE_ZEITSPANNE);

        AKJLabel lblZeiteinheit = getSwingFactory().createLabel(WIEDERVORLAGE_ZEITEINHEIT);
        cbZeiteinheit = getSwingFactory().createComboBox(WIEDERVORLAGE_ZEITEINHEIT);

        taStatusBemerkung = getSwingFactory().createTextArea(WIEDERVORLAGE_STATUSBEMERKUNG, true);
        AKJLabel lblStatusBem = getSwingFactory().createLabel(WIEDERVORLAGE_STATUSBEMERKUNG);
        AKJScrollPane spStatusBem = new AKJScrollPane(taStatusBemerkung);

        int yCorrdinate = 0;
        // @formatter:off
        details.add(new AKJPanel()             , GBCFactory.createGBC(  0,  0, 0, yCorrdinate  , 1, 1, GridBagConstraints.NONE));
        details.add(lblWiedervorlageTyp        , GBCFactory.createGBC(  0,  0, 1, yCorrdinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(cbWiedervorlageTyp         , GBCFactory.createGBC(100,  0, 2, yCorrdinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(lblWiedervorlageDatum      , GBCFactory.createGBC(  0,  0, 1, ++yCorrdinate, 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(dcWiedervorlageDatum       , GBCFactory.createGBC(100,  0, 2, yCorrdinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(lblUhrzeit                 , GBCFactory.createGBC(  0,  0, 3, yCorrdinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(taUhrzeit                  , GBCFactory.createGBC(100,  0, 4, yCorrdinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(lblWiedervorlageZeitspanne , GBCFactory.createGBC(  0,  0, 1, ++yCorrdinate, 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(taZeitspanne               , GBCFactory.createGBC(100,  0, 2, yCorrdinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(lblZeiteinheit             , GBCFactory.createGBC(  0,  0, 3, yCorrdinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(cbZeiteinheit              , GBCFactory.createGBC(100,  0, 4, yCorrdinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(new AKJPanel()             , GBCFactory.createGBC(  0,  0, 5, yCorrdinate  , 1, 1, GridBagConstraints.NONE));

        if (showBemerkung) {
            yCorrdinate = yCorrdinate + 4;
            details.add(lblStatusBem               , GBCFactory.createGBC(  0,  0, 1, ++yCorrdinate, 1, 1, GridBagConstraints.HORIZONTAL));
            details.add(spStatusBem                , GBCFactory.createGBC(  0,  0, 2, yCorrdinate  , 3, 5, GridBagConstraints.HORIZONTAL));
            details.add(new AKJPanel()             , GBCFactory.createGBC(  0,  0, 1, ++yCorrdinate, 1, 1, GridBagConstraints.NONE));
        }
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(details, BorderLayout.CENTER);
    }

    @Override
    protected void validateSaveButton() {
        // nothing to do - save button should not be managed!
    }

    public final void loadData() {
        cbWiedervorlageTyp.addItem(WIEDERVORLAGE_TYP_DATUM, true);
        cbWiedervorlageTyp.addItem(WIEDERVORLAGE_TYP_ZEITSPANNE, true);
        cbZeiteinheit.addItem(ZEITEINHEIT_TAG);
        cbZeiteinheit.addItem(ZEITEINHEIT_STUNDE);
        cbZeiteinheit.addItem(ZEITEINHEIT_MINUTE);
        taZeitspanne.setText(DEFAULT_ZEITEINHET);
        taStatusBemerkung.setText(statusBemerkung);
        if (wiedervorlageDatum != null) {
            dcWiedervorlageDatum.setDate(Date.from(wiedervorlageDatum.atZone(ZoneId.systemDefault()).toInstant()));
            taUhrzeit.setText(getHourAsFormattedString());
        }
        else {
            dcWiedervorlageDatum.setDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            taUhrzeit.setText(DEFAULT_UHRZEIT);
        }
    }

    private String getHourAsFormattedString() {
        Integer hour = wiedervorlageDatum.getHour();
        Integer minutes = wiedervorlageDatum.getMinute();
        String hourStr = "00";
        String minutesStr = "00";
        if (hour.toString().length() == 1) {
            hourStr = "0" + hour.toString();
        }
        else {
            hourStr = hour.toString();
        }
        if (minutes.toString().length() == 1) {
            minutesStr = "0" + minutes.toString();
        }
        else {
            minutesStr = minutes.toString();
        }
        return hourStr + ":" + minutesStr;
    }

    @Override
    protected void doSave() {
        Boolean validationOk = false;
        statusBemerkung = taStatusBemerkung.getText();
        if (taZeitspanne.isEnabled()) {
            validationOk = validateZeitspanne();
        }
        else {
            validationOk = validateWiedervorlageDatum();
        }
        if (validationOk) {
            if (wiedervorlageDatum.isAfter(LocalDateTime.now())) {
                prepare4Close();
                setValue(JOptionPane.OK_OPTION);
            }
            else {
                MessageHelper.showInfoDialog(this, "Das Wiedervorlagedatum muss in der Zukunft liegen (aktuell: "
                        + wiedervorlageDatum.format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")) + ")");
            }
        }
    }

    private Boolean validateZeitspanne() {
        try {
            Integer zeitspanne = Integer.valueOf(taZeitspanne.getText());
            if (zeitspanne != null) {
                String zeitspanneEinheit = (String) cbZeiteinheit.getSelectedItem();
                if (zeitspanneEinheit.equals(ZEITEINHEIT_TAG)) {
                    wiedervorlageDatum = LocalDateTime.now().plusDays(zeitspanne);
                }
                if (zeitspanneEinheit.equals(ZEITEINHEIT_STUNDE)) {
                    wiedervorlageDatum = LocalDateTime.now().plusHours(zeitspanne);
                }
                if (zeitspanneEinheit.equals(ZEITEINHEIT_MINUTE)) {
                    wiedervorlageDatum = LocalDateTime.now().plusMinutes(zeitspanne);
                }
            }
            else {
                MessageHelper.showInfoDialog(this,
                        "Zeitspanne ist nicht angegeben! Bitte füllen Sie das Feld ein.");
                return false;
            }
        }
        catch (Exception e) {
            MessageHelper.showInfoDialog(this,
                    "Zeitspanne nicht korrekt angegeben! Es sind ausschließlich ganze Zahlen zulässig!");
            return false;
        }
        return true;
    }

    private Boolean validateWiedervorlageDatum() {
        String uhrzeit = taUhrzeit.getText();
        String[] uhrzeitSubStrings = uhrzeit.split(":");
        if (!validateUhrzeit(uhrzeitSubStrings)) {
            return false;
        }
        Integer stunden = Integer.valueOf(uhrzeitSubStrings[0]);
        Integer minuten = Integer.valueOf(uhrzeitSubStrings[1]);
        wiedervorlageDatum = wiedervorlageDatum != null ? Instant.ofEpochMilli(dcWiedervorlageDatum.getDate(null).getTime()).atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay() : null;
        if ((stunden != null) && (minuten != null) && (stunden <= 23) && (minuten <= 59)) {
            wiedervorlageDatum = wiedervorlageDatum.withHour(stunden);
            wiedervorlageDatum = wiedervorlageDatum.withMinute(minuten);
        }
        else {
            MessageHelper.showInfoDialog(this, "Bitte korrekte Uhrzeit angeben! Format: HH:MM (Beispiel: 15:00");
            return false;
        }
        return true;
    }

    private Boolean validateUhrzeit(String[] uhrzeitSubStrings) {
        if ((uhrzeitSubStrings.length != 2) || (uhrzeitSubStrings[0].length() != 2)
                || (uhrzeitSubStrings[1].length() != 2)) {
            MessageHelper.showInfoDialog(this, "Bitte korrekte Uhrzeit angeben! Format: HH:MM (Beispiel: 15:00)");
            return false;
        }
        return true;
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem().equals(WIEDERVORLAGE_TYP_DATUM)) {
            enableDatum();
        }
        else if (e.getItem().equals(WIEDERVORLAGE_TYP_ZEITSPANNE)) {
            enableZeitspanne();
        }
    }

    private void enableZeitspanne() {
        cbZeiteinheit.setEnabled(true);
        taZeitspanne.setEnabled(true);
        dcWiedervorlageDatum.setEnabled(false);
        taUhrzeit.setEnabled(false);
    }

    private void enableDatum() {
        cbZeiteinheit.setEnabled(false);
        taZeitspanne.setEnabled(false);
        dcWiedervorlageDatum.setEnabled(true);
        taUhrzeit.setEnabled(true);
    }

    public LocalDateTime getWiedervorlageDatum() {
        return wiedervorlageDatum;
    }

    public String getStatusBemerkung() {
        return statusBemerkung;
    }
}
