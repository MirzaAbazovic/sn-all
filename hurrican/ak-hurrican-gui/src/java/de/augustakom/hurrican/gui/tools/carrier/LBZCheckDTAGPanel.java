/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2005 09:03:06
 */
package de.augustakom.hurrican.gui.tools.carrier;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Panel, um die LBZs (Leitungsbezeichnungen) der DTAG-Bestellungen auf sytaktische(!) Richtigkeit zu pruefen.
 *
 *
 */
public class LBZCheckDTAGPanel extends AbstractServicePanel {

    private static final Logger LOGGER = Logger.getLogger(LBZCheckDTAGPanel.class);

    private static final int STEP_COUNT = 50;

    private static final String EMPTY = " ";
    private static final String LBZ_INVALID = "LBZ ungültig;";
    private static final String BEREITSTELLUNG_BUT_NOT_LBZ = "Bereitstellung eingetragen, aber LBZ nicht gefüllt;";
    private static final String LBZ_BUT_NOT_BEREITSTELLUNG = "LBZ ist eingetragen, aber Bereitstellungsdatum fehlt;";
    private static final String CB_NICHT_GEKUENDIGT = "CuDA nicht gekuendigt;";
    private static final String VTR_IS_BLANK = "VtrNr nicht eingetragen;";
    private static final String ERROR_WHILE_CHECK = "unbekannter Fehler waehrend Check";
    private static final long serialVersionUID = -7061787171754226126L;

    private AKJCheckBox chbCheckVtrNr = null;
    private AKJCheckBox chbCheckKuendBest = null;
    private AKJCheckBox chbIgnoreKuend = null;
    private AKJButton btnStart = null;
    private AKJButton btnStop = null;
    private AKReflectionTableModel tbMdlResult = null;

    private SwingWorker<Void, CarrierLbzCheckModel> swingWorker;

    /**
     * Default-Konstruktor.
     */
    public LBZCheckDTAGPanel() {
        super("de/augustakom/hurrican/gui/tools/carrier/resources/LBZCheckDTAGPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        chbCheckVtrNr = getSwingFactory().createCheckBox("check.vtr.nr");
        chbCheckKuendBest = getSwingFactory().createCheckBox("check.kuend.bestaetigung");
        chbIgnoreKuend = getSwingFactory().createCheckBox("ignore.gekuendigt");
        btnStart = getSwingFactory().createButton("start.check", getActionListener());
        btnStop = getSwingFactory().createButton("stop.check", getActionListener());

        AKJPanel top = new AKJPanel(new GridBagLayout());
        Insets insets = new Insets(5, 5, 5, 5);
        top.setBorder(BorderFactory.createTitledBorder("Optionen"));
        top.add(chbCheckVtrNr, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE, insets));
        top.add(chbCheckKuendBest, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE, insets));
        top.add(chbIgnoreKuend, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE, insets));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnStart, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnStop, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlResult = new AKReflectionTableModel<CarrierLbzCheckModel>(
                new String[] { "CB-ID", "LBZ", "Vtr-Nr", "Auftrag-IDs", VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Bemerkungen" },
                new String[] { "cbId", "lbz", "vtrNr", "auftragIds", "vbzs", "bemerkung" },
                new Class[] { Long.class, String.class, String.class, String.class, String.class, String.class });
        AKJTable tbResult = new AKJTable(tbMdlResult, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbResult.attachSorter();
        tbResult.fitTable(new int[] { 80, 150, 100, 100, 150, 400 });

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.NORTH);
        this.add(new AKJScrollPane(tbResult), BorderLayout.CENTER);
        this.add(btnPnl, BorderLayout.SOUTH);

        enableGuiComponentsForSwingWorkerInProgress(false);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("start.check".equals(command)) {
            startLbzCheck();
        }
        else if ("stop.check".equals(command)) {
            stopLbzCheck();
        }
    }

    /* Ueberprueft, ob die LBZ der DTAG-Bestellungen syntaktisch richtig sind. */
    private void startLbzCheck() {
        swingWorker = new SwingWorker<Void, CarrierLbzCheckModel>() {

            // Initialize required data from panel in the constructor since this should be done in the EDT thread.
            final boolean checkVtrNr = chbCheckVtrNr.isSelected();
            final boolean checkKuendBest = chbCheckKuendBest.isSelected();
            final boolean ignoreKuend = chbIgnoreKuend.isSelected();

            @Override
            public Void doInBackground() throws Exception {
                int begin = 0;
                boolean doCheck = true;

                CarrierService cs = getCCService(CarrierService.class);
                List<Carrierbestellung> result = null;
                do {
                    result = cs.findCBs4Carrier(Carrier.ID_DTAG, STEP_COUNT, begin);
                    begin += STEP_COUNT;
                    for (Carrierbestellung cb : result) {
                        if (isCancelled()) {
                            return null;
                        }

                        doCheck = (!(ignoreKuend && (cb.getKuendBestaetigungCarrier() != null)));
                        if (doCheck) {
                            publish(check(cb, cs, checkVtrNr, checkKuendBest));
                        }
                    }
                }
                while ((!result.isEmpty()) && !isCancelled());

                return null;
            }

            @Override
            protected void process(List<CarrierLbzCheckModel> chunks) {
                for (CarrierLbzCheckModel carrierLbzCheckModel : chunks) {
                    if (carrierLbzCheckModel != null) {
                        tbMdlResult.addObject(carrierLbzCheckModel);
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                }
                catch (CancellationException e) {
                    LOGGER.info(e.getMessage(), e);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    enableGuiComponentsForSwingWorkerInProgress(false);
                    stopProgressBar();
                    setDefaultCursor();
                }
            }
        };

        setWaitCursor();
        showProgressBar("LBZ prüfen...");
        enableGuiComponentsForSwingWorkerInProgress(true);
        tbMdlResult.setData(new LinkedList<CarrierLbzCheckModel>());
        swingWorker.execute();
    }

    /**
     * Prueft die Carrierbestellung auf syntaktische Richtigkeit.
     */
    private CarrierLbzCheckModel check(Carrierbestellung cb, CarrierService service, boolean checkVtrNr, boolean checkKuendBest) {
        try {
            StringBuilder msg = new StringBuilder();
            if (StringUtils.isNotBlank(cb.getLbz())) {
                try {
                    service.validateLbz(cb.getCarrier(), cb.getLbz());
                }
                catch (ValidationException e) {
                    msg.append(LBZ_INVALID);
                }
            }

            List<AuftragDaten> auftragDaten = service.findAuftragDaten4CB(cb.getId());
            boolean containsActive = containsActiveAuftrag(auftragDaten);

            if (containsActive) {
                if (cb.getBereitstellungAm() != null) {
                    if (StringUtils.isBlank(cb.getLbz())) {
                        if (msg.length() > 0) { msg.append(EMPTY); }
                        msg.append(BEREITSTELLUNG_BUT_NOT_LBZ);
                    }

                    if (checkVtrNr && StringUtils.isBlank(cb.getVtrNr())) {
                        if (msg.length() > 0) { msg.append(EMPTY); }
                        msg.append(VTR_IS_BLANK);
                    }
                }
                else if (StringUtils.isNotBlank(cb.getLbz()) &&
                        (cb.getBereitstellungAm() == null) && (cb.getKuendBestaetigungCarrier() == null)) {
                    if (msg.length() > 0) { msg.append(EMPTY); }
                    msg.append(LBZ_BUT_NOT_BEREITSTELLUNG);
                }
            }

            if (checkKuendBest && !containsActive && (cb.getKuendBestaetigungCarrier() == null)) {
                if (msg.length() > 0) { msg.append(EMPTY); }
                msg.append(CB_NICHT_GEKUENDIGT);
            }

            if (msg.length() > 0) {
                return createError(cb, msg.toString(), auftragDaten);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return createError(cb, ERROR_WHILE_CHECK, null);
        }
    }

    /**
     * Erzeugt ein Modell mit den benoetigten Informationen zur ungueltigen Carrierbestellung.
     */
    private CarrierLbzCheckModel createError(Carrierbestellung cb, String msg, List<AuftragDaten> auftragDaten) {
        CarrierLbzCheckModel mdl = new CarrierLbzCheckModel();
        mdl.setCbId(cb.getId());
        mdl.setLbz(cb.getLbz());
        mdl.setVtrNr(cb.getVtrNr());
        mdl.setBemerkung(msg);

        try {
            if (auftragDaten != null) {
                PhysikService ps = getCCService(PhysikService.class);

                StringBuilder ids = new StringBuilder();
                StringBuilder vbzs = new StringBuilder();
                boolean seperate = false;
                for (AuftragDaten ad : auftragDaten) {
                    if (seperate) {
                        ids.append("; ");
                        vbzs.append("; ");
                    }
                    else {
                        seperate = true;
                    }
                    ids.append(ad.getAuftragId());

                    // VerbindungsBezeichnung laden
                    VerbindungsBezeichnung verbindungsBezeichnung = ps.findVerbindungsBezeichnungByAuftragId(ad.getAuftragId());
                    vbzs.append((verbindungsBezeichnung != null) ? verbindungsBezeichnung.getVbz() : "");
                }
                mdl.setAuftragIds(ids.toString());
                mdl.setVbzs(vbzs.toString());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return mdl;
    }

    /* Ueberprueft, ob in der Liste <code>auftragDaten</code> ein aktiver Auftrag
     * enthalten ist. Ist dies der Fall, wird <code>true</code> zurueck gegeben - ansonsten
     * <code>false</code>.
     * @param auftragDaten
     * @return true wenn min. ein aktiver Auftrag in der Liste enthalten ist.
     */
    private boolean containsActiveAuftrag(List<AuftragDaten> auftragDaten) {
        if (auftragDaten != null) {
            for (AuftragDaten ad : auftragDaten) {
                if (ad.isAuftragActive()) {
                    return true;
                }
            }
        }
        return false;
    }

    /* Stoppt den LBZ-Check. */
    private void stopLbzCheck() {
        if (swingWorker != null) {
            swingWorker.cancel(true);
        }
    }

    /*
     * Schaltet die Buttons fuer den Check disabled/enabled.
     */
    private void enableGuiComponentsForSwingWorkerInProgress(boolean inProgess) {
        chbCheckVtrNr.setEnabled(!inProgess);
        chbCheckKuendBest.setEnabled(!inProgess);
        chbIgnoreKuend.setEnabled(!inProgess);
        btnStart.setEnabled(!inProgess);
        btnStop.setEnabled(inProgess);
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


