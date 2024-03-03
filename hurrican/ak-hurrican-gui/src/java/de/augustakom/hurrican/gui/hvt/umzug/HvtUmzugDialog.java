/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2015 14:28
 */
package de.augustakom.hurrican.gui.hvt.umzug;

import java.awt.*;
import java.time.*;
import java.util.*;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.hvt.HVTSelectionDialog;
import de.augustakom.hurrican.gui.hvt.umzug.actions.ImportHvtUmzugAction;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HvtUmzugService;

/**
 * Dialog, um einen neuen {@link HvtUmzug} anzulegen und das entsprechende XLS File der DTAG zu importieren.
 */
class HvtUmzugDialog extends AbstractServiceOptionDialog {

    public static final String BTN_HVT_AUSWAEHLEN = "btn.hvt.auswaehlen";
    public static final String BTN_HVT_DEST_AUSWAEHLEN = "btn.hvt.dest.auswaehlen";
    public static final String DC_SCHALTTAG = "dc.schalttag";
    public static final String TF_KVZ_NR = "tf.kvz.nr";
    public static final String TF_HVT_STANDORT = "tf.hvt.standort";
    public static final String TF_HVT_DEST_STANDORT = "tf.hvt.dest.standort";
    private static final long serialVersionUID = 9089741322868438029L;
    private static final Logger LOGGER = Logger.getLogger(HvtUmzug.class);
    /**
     * Represents HVT 'MUC-SATTL-001_089-26'
     */
    private static final long HVT_STD_ID_SATTLER_STR = 761L;
    /**
     * Represents HVT 'MUC-FRAUN-032_089-26'
     */
    private static final long HVT_STD_ID_FRAUENHOFER_STR = 33079;
    private AKJTextField tfKvzNr;
    private AKJDateComponent dcSchalttag;
    private AKJTextField tfHvtStandort;
    private AKJTextField tfHvtDestStandort;
    private Pair<Long, String> standortIdWithOrtsteil;
    private Pair<Long, String> standortIdDestWithOrtsteil;
    private HvtUmzug hvtUmzug = new HvtUmzug();

    public HvtUmzugDialog() {
        super("de/augustakom/hurrican/gui/hvt/umzug/resources/HvtUmzugDialog.xml");
        createGUI();
    }

    @Override
    protected void doSave() {
        try {
            setWaitCursor();

            hvtUmzug.setKvzNr(tfKvzNr.getText() == null ? null : tfKvzNr.getText().trim());
            hvtUmzug.setHvtStandort(standortIdWithOrtsteil == null ? null : standortIdWithOrtsteil.getFirst());
            hvtUmzug.setHvtStandortDestination(standortIdDestWithOrtsteil == null ? null : standortIdDestWithOrtsteil.getFirst());
            hvtUmzug.setSchalttag(dcSchalttag.getDate(null) == null ? null : Instant.ofEpochMilli(dcSchalttag.getDate(null).getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
            hvtUmzug.setImportAm(new Date());
            hvtUmzug.setBearbeiter(HurricanSystemRegistry.instance().getCurrentLoginName());
            //excelBlob und details werden über ImportHvtUmzugAction gesetzt

            final StringBuilder validationMsg = new StringBuilder();
            if (Strings.isNullOrEmpty(hvtUmzug.getKvzNr())) {
                validationMsg.append("Bitte Kvz-Nr angeben!\n");
            }
            if (hvtUmzug.getHvtStandort() == null) {
                validationMsg.append("Bitte einen HVT auswählen!\n");
            }
            if (hvtUmzug.getHvtStandortDestination() == null) {
                validationMsg.append("Bitte einen Ziel-HVT auswählen!\n");
            }
            if (hvtUmzug.getSchalttag() == null) {
                validationMsg.append("Bitte einen Schalttag angeben!\n");
            }
            else if (hvtUmzug.getSchalttag().isBefore(LocalDate.now())) {
                validationMsg.append("Der Schalttag darf nicht in der Vergangenheit liegen!\n");
            }
            if (hvtUmzug.getExcelBlob() == null || hvtUmzug.getExcelBlob().length == 0) {
                validationMsg.append("Bitte Excel-Datei auswählen!\n");
            }

            if (validationMsg.length() != 0) {
                MessageHelper.showWarningDialog(this, validationMsg.toString(), true);
            }
            else {
                HvtUmzug savedHvtUmzug = null;
                try {
                    HvtUmzugService hvtUmzugService = getCCService(HvtUmzugService.class);

                    savedHvtUmzug = hvtUmzugService.createHvtUmzug(hvtUmzug);
                    hvtUmzugService.matchHurricanOrders4HvtUmzug(savedHvtUmzug);
                }
                catch (Exception e) {
                    LOGGER.error(e);
                    MessageHelper.showErrorDialog(this, e);
                }

                prepare4Close();
                setValue(savedHvtUmzug);
            }
        }
        finally {
            setDefaultCursor();
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        final AKJPanel panel = new AKJPanel(new GridBagLayout());
        final AKJLabel lblSchalttag = getSwingFactory().createLabel(DC_SCHALTTAG);
        dcSchalttag = getSwingFactory().createDateComponent(DC_SCHALTTAG);
        AKJButton btnHvtDialog = getSwingFactory().createButton(BTN_HVT_AUSWAEHLEN, getActionListener());
        final AKJLabel lblHvtStandort = getSwingFactory().createLabel(TF_HVT_STANDORT);
        tfHvtStandort = getSwingFactory().createTextField(TF_HVT_STANDORT, false);

        AKJButton btnHvtDestDialog = getSwingFactory().createButton(BTN_HVT_DEST_AUSWAEHLEN, getActionListener());
        final AKJLabel lblHvtDestStandort = getSwingFactory().createLabel(TF_HVT_DEST_STANDORT);
        tfHvtDestStandort = getSwingFactory().createTextField(TF_HVT_DEST_STANDORT, false);

        final AKJLabel lblKvzNr = getSwingFactory().createLabel(TF_KVZ_NR);
        tfKvzNr = getSwingFactory().createTextField(TF_KVZ_NR);

        final AKJButton btnImportXls = new AKJButton(new ImportHvtUmzugAction(hvtUmzug));
        btnImportXls.setText("XLS auswählen");
        btnImportXls.setEnabled(true);

        //@formatter:off
        panel.add(lblHvtStandort    , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfHvtStandort     , GBCFactory.createGBC(100,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(btnHvtDialog      , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        panel.add(lblHvtDestStandort, GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfHvtDestStandort , GBCFactory.createGBC(100,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(btnHvtDestDialog  , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        panel.add(lblKvzNr          , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfKvzNr           , GBCFactory.createGBC(100,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        panel.add(lblSchalttag      , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(dcSchalttag       , GBCFactory.createGBC(100,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        panel.add(btnImportXls      , GBCFactory.createGBC(100,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel()    , GBCFactory.createGBC(100,100, 2, 5, 1,1, GridBagConstraints.BOTH));
        //@formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(panel, BorderLayout.CENTER);

        //set default standort data
        standortIdWithOrtsteil = getHvtDataPair(HVT_STD_ID_SATTLER_STR);
        standortIdDestWithOrtsteil = getHvtDataPair(HVT_STD_ID_FRAUENHOFER_STR);
        setHvtStandortText();
    }

    private Pair<Long, String> getHvtDataPair(final Long hvtStandortId) {
        try {
            final HVTStandort hvtStandort = getCCService(HVTService.class).findHVTStandort(hvtStandortId);
            final HVTGruppe hvtGruppe = getCCService(HVTService.class).findHVTGruppe4Standort(hvtStandortId);
            if (hvtStandort != null && hvtGruppe != null) {
                return Pair.create(hvtStandort.getHvtIdStandort(), hvtGruppe.getOrtsteil());
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        if (BTN_HVT_AUSWAEHLEN.equals(command) || BTN_HVT_DEST_AUSWAEHLEN.equals(command)) {
            final HVTSelectionDialog hvtSelectionDialog = new HVTSelectionDialog();
            final Object choice = DialogHelper.showDialog(this, hvtSelectionDialog, true, true);
            if (choice instanceof HVTGruppeStdView) {
                final HVTGruppeStdView view = (HVTGruppeStdView) choice;
                final Pair<Long, String> pair = Pair.create(view.getHvtIdStandort(), view.getOrtsteil());
                if (BTN_HVT_AUSWAEHLEN.equals(command)) {
                    standortIdWithOrtsteil = pair;
                }
                else {
                    standortIdDestWithOrtsteil = pair;
                }
                setHvtStandortText();
            }
        }
    }

    private void setHvtStandortText() {
        if (standortIdWithOrtsteil != null) {
            tfHvtStandort.setText("" + standortIdWithOrtsteil.getFirst() + " " + standortIdWithOrtsteil.getSecond());
        }
        if (standortIdDestWithOrtsteil != null) {
            tfHvtDestStandort.setText("" + standortIdDestWithOrtsteil.getFirst() + " " + standortIdDestWithOrtsteil.getSecond());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        //nothing to do
    }
}
