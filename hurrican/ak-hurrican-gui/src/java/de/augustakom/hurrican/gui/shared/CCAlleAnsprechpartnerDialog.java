/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH All rights reserved. -------------------------------------------------------
 * File created: 16.11.2015
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.view.CCAnsprechpartnerView;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 * Dialog fuer die Anzeige aller Ansprechpartner ({@link CCAnsprechpartnerView}) die zum aktuellen Kunden zugeordnet
 * sind.
 */
public class CCAlleAnsprechpartnerDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(CCAlleAnsprechpartnerDialog.class);

    private final Long auftragId;
    private Long kundeNo = null;
    private Integer buendelNo = null;
    private Long auftragNoOrig = null;
    private String buendelNrHerkunft = null;

    private AnsprechpartnerService ansprechpartnerService = null;
    private CCAnsprechpartnerViewTableModel tbMdlAnsprechpartner = null;
    private AKJCheckBox chbSelectForOtherAuftraege = null;

    /**
     * Konstruktor.
     *
     * @param auftragId ID des aktuell gew&auml;hlten Auftrags.
     */
    public CCAlleAnsprechpartnerDialog(Long auftragId) {
        super(null);
        this.auftragId = auftragId;

        init();
        createGUI();
        loadData();
    }

    private void init() {
        try {
            ansprechpartnerService = getCCService(AnsprechpartnerService.class);
            CCAuftragService ccAuftragService = getCCService(CCAuftragService.class);
            Auftrag auftrag = ccAuftragService.findAuftragById(auftragId);
            AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(auftragId);
            kundeNo = auftrag.getKundeNo();
            buendelNo = auftragDaten.getBuendelNr();
            buendelNrHerkunft = auftragDaten.getBuendelNrHerkunft();
            auftragNoOrig = auftragDaten.getAuftragNoOrig();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle((kundeNo == null) ? "" : "Liste aller Ansprechpartner zu der Kundennummer " + kundeNo);
        configureButton(CMD_SAVE, "Übernehmen", "Übernimmt die Auswahl und schliesst den Dialog", true, true);
        configureButton(CMD_CANCEL, "Schließen", "Schliesst den Dialog", true, true);

        tbMdlAnsprechpartner = new CCAnsprechpartnerViewTableModel();
        AKJTable tbAnsprechpartner = new AKJTable(tbMdlAnsprechpartner, AKJTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        tbAnsprechpartner.fitTable(new int[] { 100, 100, 100, 70, 100, 100, 100, 100, 100 });
        tbAnsprechpartner.setAutoCreateRowSorter(true);

        AKJScrollPane spTable = new AKJScrollPane(tbAnsprechpartner, new Dimension(900, 300));
        AKJPanel top = new AKJPanel(new BorderLayout());
        top.setBorder(BorderFactory.createTitledBorder("Ansprechpartner"));
        top.add(spTable, BorderLayout.CENTER);

        chbSelectForOtherAuftraege = new AKJCheckBox("Für alle Aufträge mit gleicher Taifun-Auftragsnr. und Bündel-Nr. übernehmen");
        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(top, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        getChildPanel().add(chbSelectForOtherAuftraege, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    @Override
    protected void validateSaveButton() {
        // Save-Button nicht validieren!
    }

    /**
     * Laedt saemtliche Ansprechparter zu einer bestimmten Kundennummer / Buendelnummer.
     */
    private void loadData() {
        tbMdlAnsprechpartner.setData(null);
        if (kundeNo == null) {
            MessageHelper.showInfoDialog(this, "Keine Daten zu laden, da keine gültige customerId übergeben wurde.",
                    "Warnung", null, true);
            return;
        }
        setWaitCursor();
        List<CCAnsprechpartnerView> ansprechpartner = getAnsprechpartnerAndFilterDoubles();
        enableAnsprechpartner(ansprechpartner);
        tbMdlAnsprechpartner.setData(ansprechpartner);
    }

    /**
     * L&auml;dt alle Ansprechpartner eines Kunden. Filtert anschlie&szlig;end die raus, die die gleiche Adresse und den
     * gleichen Typ besitzen.
     *
     * @return Liste mit Ansprechpartnern eines Kunden.
     */
    private List<CCAnsprechpartnerView> getAnsprechpartnerAndFilterDoubles() {
        try {
            List<CCAnsprechpartnerView> available = ansprechpartnerService.findAnsprechpartnerByKundeNoAndBuendelNo(kundeNo, null);
            return available.stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<CCAnsprechpartnerView>(
                            Comparator.comparingLong(CCAnsprechpartnerView::getAddressId)
                                    .thenComparingLong(CCAnsprechpartnerView::getTypeRefId))), ArrayList::new));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
        return new ArrayList<>();
    }

    private void enableAnsprechpartner(List<CCAnsprechpartnerView> available) {
        try {
            List<Ansprechpartner> currentAnsprechpartner = ansprechpartnerService.findAnsprechpartner(null, auftragId);
            if (currentAnsprechpartner == null || currentAnsprechpartner.isEmpty()) {
                return;
            }
            for (Ansprechpartner current : currentAnsprechpartner) {
                for (CCAnsprechpartnerView ap : available) {
                    if (ap.getTypeRefId().equals(current.getTypeRefId())) {
                        ap.setEditable(Boolean.FALSE);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void doSave() {
        AKWarnings warnings = new AKWarnings();
        boolean selected = false;
        setWaitCursor();
        for (CCAnsprechpartnerView ap : tbMdlAnsprechpartner.getData()) {
            if (!BooleanTools.nullToFalse(ap.getTakeOver())) {
                continue;
            }
            if (chbSelectForOtherAuftraege.isSelected()) {
                warnings.addAKWarnings(copyAnsprechpartner4otherAuftraege(ap));
            }
            else {
                warnings.addAKWarnings(copyAnsprechpartner(ap.getAnsprechpartnerId()));
            }
            ap.setTakeOver(Boolean.FALSE);
            ap.setEditable(Boolean.FALSE);
            selected = true;
        }
        setDefaultCursor();

        if (!selected) {
            MessageHelper.showInfoDialog(getMainFrame(), "Es wurden keine Ansprechpartner gewählt!");
        }
        else if (warnings.isNotEmpty()) {
            MessageHelper.showInfoDialog(getMainFrame(), String.format("Ansprechpartner wurden kopiert. Folgende "
                    + "Warnungen traten auf:\n\n%s", warnings.getMessagesAsText()), true);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), "Ansprechparnter wurden erfolgreich kopiert.", true);
        }
        tbMdlAnsprechpartner.fireTableDataChanged();
    }

    /**
     * Kopiert Ansprechpartner f&uuml;r Auftr&auml;ge mit der gleichen AuftragNoOrig und B&uuml;ndelnummer (beide aus
     * dem aktuell gew&auml;hlten Projekt). Pr&uuml;ft dabei auch, ob dem Auftrag bereits Ansprechpartner mit dem
     * gleichen Typ zugeordnet sind.
     *
     * @param ansprechpartnerToCopy Ansprechpartner, der die AuftragNoOrig und B&uuml;ndelnummer enth&auml;lt und
     *                              kopiert werden soll.
     */
    private AKWarnings copyAnsprechpartner4otherAuftraege(CCAnsprechpartnerView ansprechpartnerToCopy) {
        AKWarnings warnings = new AKWarnings();
        try {
            Collection<CCAuftragIDsView> buendel = getCCService(CCAuftragService.class)
                    .findAufragIdAndVbz4AuftragNoOrigAndBuendel(auftragNoOrig, buendelNo, buendelNrHerkunft);
            if (buendel == null || buendel.isEmpty()) {
                warnings.addAKWarning(null, String.format("Zu dem Billing Auftrag %s und der Bündelnummer %s sind keine "
                        + "Aufträge verfügbar.", auftragNoOrig, buendelNo));
                return warnings;
            }
            for (CCAuftragIDsView auftragIdsView : buendel) {
                List<Ansprechpartner> ansprechpartner4auftrag = ansprechpartnerService.findAnsprechpartner(
                        Ansprechpartner.Typ.forRefId(ansprechpartnerToCopy.getTypeRefId()), auftragIdsView.getAuftragId());
                if (ansprechpartner4auftrag != null && !ansprechpartner4auftrag.isEmpty()) {
                    warnings.addAKWarning(null, String.format("Zu dem Auftrag %s existiert bereits ein Ansprechpartner "
                            + "des Typs %s", auftragIdsView.getAuftragId(), ansprechpartnerToCopy.getAnsprechpartnerType()));
                    continue;
                }
                warnings.addAKWarnings(copyAnsprechpartner(ansprechpartnerToCopy.getAnsprechpartnerId(),
                        auftragIdsView.getAuftragId()));
            }
        }
        catch (Exception e) {
            setDefaultCursor();
            LOGGER.error(e.getMessage(), e);
            warnings.addAKWarning(null, String.format("Beim kopieren des Ansprechpartner Typs %s ist ein Fehler "
                    + "aufgetreten. Grund: %s", ansprechpartnerToCopy.getAnsprechpartnerType(), e.getMessage()));
        }
        return warnings;
    }

    private AKWarnings copyAnsprechpartner(Long ansprechpartnerId) {
        return copyAnsprechpartner(ansprechpartnerId, auftragId);
    }

    private AKWarnings copyAnsprechpartner(Long ansprechpartnerId, Long auftragId) {
        AKWarnings warnings = new AKWarnings();
        try {
            ansprechpartnerService.copyAnsprechpartner(ansprechpartnerId, auftragId);
        }
        catch (Exception e) {
            setDefaultCursor();
            LOGGER.error(e.getMessage(), e);
            warnings.addAKWarning(null, String.format("Beim kopieren des Ansprechpartners %s zum Auftrag %s ist ein "
                    + "Fehler aufgetreten. Grund: %s", ansprechpartnerId, auftragId, e.getMessage()));
        }
        return warnings;
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        // intentionally left blank
    }

    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }
}
