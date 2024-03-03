/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.2014 13:08:33
 */
package de.augustakom.hurrican.gui.tools.dn;

import static de.augustakom.common.gui.utils.Layout.*;

import java.awt.*;
import java.awt.datatransfer.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import com.google.common.base.Strings;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.swing.AKJAbstractOptionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.MessageHelper;


/**
 * Dialog fuer die Erfassung der Parameterwerte fuer Rufnummernleistungen
 */
public class DnRufnummern2LeistungDialog extends AKJAbstractOptionDialog {

    final static String SINGLE_DEFAULT = "";

    final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    private final String oldValue;
    private AKJFormattedTextField cc, lac, dn;
    private AKJButton btnDel;
    private DefaultListModel lsMdlWerte = null;
    private AKJList lsWerte = null;
    private final int leistungParameterMehrfach;

    public DnRufnummern2LeistungDialog(Integer leistungParameterMehrfach, String oldValue) {
        super("de/augustakom/hurrican/gui/tools/dn/resources/DnRufnummern2LeistungDialog.xml");
        this.leistungParameterMehrfach = leistungParameterMehrfach != null ? leistungParameterMehrfach : 1;
        this.oldValue = oldValue;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        setTitle("Rufnummern eingeben");

        AKJLabel lblCc = getSwingFactory().createLabel("cc");
        AKJLabel lblLac = getSwingFactory().createLabel("lac");
        AKJLabel lblDn = getSwingFactory().createLabel("dn");
        cc = getSwingFactory().createFormattedTextField("cc");
        lac = getSwingFactory().createFormattedTextField("lac");
        dn = getSwingFactory().createFormattedTextField("dn");
        AKJButton btnOk = getSwingFactory().createButton("ok", getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton("cancel", getActionListener());
        AKJButton btnAdd = getSwingFactory().createButton("add", getActionListener());
        AKJButton btnAddFromClipboard = getSwingFactory().createButton("addFromClipboard", getActionListener());
        btnDel = getSwingFactory().createButton("del", getActionListener());
        btnDel.setEnabled(false);

        lsMdlWerte = new DefaultListModel();
        lsWerte = getSwingFactory().createList("ls.parameter", lsMdlWerte);
        lsWerte.setEnabled(true);
        lsWerte.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AKJScrollPane spWerte = new AKJScrollPane(lsWerte);
        spWerte.setMinimumSize(new Dimension(300, 130));

        JComponent panel =
                vbox(
                        hbox(
                                vbox(lblCc, cc),
                                vbox(lblLac, lac),
                                vbox(lblDn, dn)
                        ),
                        vspace(1),
                        hbox(
                                btnAdd,
                                btnAddFromClipboard,
                                btnDel
                        ),
                        spWerte,
                        hbox(
                                btnOk,
                                btnCancel
                        )
                );

        getChildPanel().setBorder(new EmptyBorder(new Insets(2, 2, 2, 2)));
        getChildPanel().add(panel);

        lsWerte.addListSelectionListener(new ListSelectionListener() {
            boolean changePending = false;

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() || changePending) {
                    return;
                }
                changePending = true;
                try {
                    int index = lsWerte.getSelectedIndex();
                    if (index < 0) {
                        // keine Mouse-Selektion - nur update
                        return;
                    }
                    btnDel.setEnabled(true);
                }
                finally {
                    changePending = false;
                }
            }
        });

        parseMulti(oldValue);
    }

    private void parseMulti(String text) {
        String text1 = StringUtils.trimToNull(text);
        if (text1 == null) {
            return;
        }
        if (!text1.matches("[0-9]{2}-[0-9]+(?:-[0-9]+){0,2}(?:&[0-9]{2}-[0-9]+(?:-[0-9]+){0,2})*")) {
            MessageHelper.showWarningDialog(this, "Eingabeformat entspricht nicht dem Pattern \"lfdNr-CC[-LAC][-DN]\" (Mehrfachangaben durch \"&\" getrennt)", true);
            return;
        }
        String rufnummern[] = text1.trim().split("&");
        for (String rufnummer : rufnummern) {
            final Telephonenumber number;
            // rufnummer format: <lfd-nr>-<cc>-<lac>-<dn> oder <lfd-nr>-<cc>-<lac> oder <lfd-nr>-<cc>
            String parts[] = rufnummer.split("-");
            switch (parts.length) {
                case 2:
                    number = Telephonenumber.parse(parts[1], null, null);
                    break;
                case 3:
                    number = Telephonenumber.parse(parts[1], parts[2], null);
                    break;
                case 4:
                    number = Telephonenumber.parse(parts[1], parts[2], parts[3]);
                    break;
                default:
                    number = null;
                    continue;
            }
            if (number != null) {
                if (maxNumbersAdded()) {
                    showMaxNumbersAddedInfoDialog();
                    return;
                }
                addNumber(number);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(String)
     */
    @Override
    protected void execute(String command) {
        if ("cancel".equals(command)) {
            prepare4Close();
            setValue(Integer.valueOf(CANCEL_OPTION));
        }
        else if ("add".equals(command)) {
            Telephonenumber telephonenumber = parseComposite();
            if (telephonenumber != null) {
                addComposite(telephonenumber);
            }
        }
        else if ("addFromClipboard".equals(command)) {
            Clipboard systemClip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = systemClip.getContents(null);
            if (contents != null) {
                try {
                    String text = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    parseMulti(text);
                }
                catch (Exception e) {
                    MessageHelper.showErrorDialog(this, e);
                    return;
                }
            }
        }
        else if ("del".equals(command)) {
            delComposite();
        }
        else if ("ok".equals(command)) {
            prepare4Close();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lsMdlWerte.getSize(); i++) {
                sb.append(String.format("%02d", i + 1));
                sb.append('-');
                sb.append(lsMdlWerte.getElementAt(i).toString().replace(' ', '-'));
                if (i < lsMdlWerte.getSize() - 1) {
                    sb.append('&');
                }
            }
            setValue(sb.toString());
        }
    }

    public void update(Observable arg0, Object arg1) {
        // intentionally left blank
    }

    private Telephonenumber parseComposite() {
        if (!Strings.isNullOrEmpty(cc.getText()) && "ZZ".equals(phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(cc.getText())))) {
            MessageHelper.showInfoDialog(this, "Der eingegebene CountryCode: \"" + cc.getText() + "\" existiert nicht");
            return null;
        }
        return Telephonenumber.parse(cc.getText(), lac.getText(), dn.getText());
    }

    private void clearComposite() {
        cc.setText("");
        lac.setText("");
        dn.setText("");
    }

    private void addComposite(Telephonenumber telephonenumber) {
        if (maxNumbersAdded()) {
            showMaxNumbersAddedInfoDialog();
            return;
        }
        if(addNumber(telephonenumber)) {
            clearComposite();
        }
    }

    /**
     *
     * @param number
     * @return true wenn die Rufnr zur Liste hinzugefuegt wurde
     */
    private boolean addNumber(Telephonenumber number) {
        if(!number.isPublicLenghtOk()) {
            MessageHelper.showInfoDialog(this, String.format("Nummer zu lang (insgesamt max. %d Stellen erlaubt)", Telephonenumber.PUBLIC_MAX_LENGTH));
            return false;
        }
        String numberAsString = number.toString();
        if (lsMdlWerte.contains(numberAsString)) {
            MessageHelper.showInfoDialog(this, "Nummer bereits in Liste vorhanden");
            return false;
        }
        else {
            lsMdlWerte.addElement(numberAsString);
        }
        return true;
    }

    private void delComposite() {
        int index = lsWerte.getSelectedIndex();
        lsMdlWerte.remove(index);
        lsWerte.clearSelection();
        btnDel.setEnabled(false);
        parseComposite();
    }

    private boolean maxNumbersAdded() {
        return lsMdlWerte.getSize() >= leistungParameterMehrfach;
    }

    private void showMaxNumbersAddedInfoDialog() {
        MessageHelper.showInfoDialog(this,
                "Maximal " + leistungParameterMehrfach + " Rufnummern erlaubt");
    }
}
