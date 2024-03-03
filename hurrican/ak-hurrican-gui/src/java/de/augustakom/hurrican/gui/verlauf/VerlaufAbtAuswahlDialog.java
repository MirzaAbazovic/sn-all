/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.01.2005 09:23:20
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.BAVerlaufConfig;
import de.augustakom.hurrican.model.cc.ExtServiceProvider;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.temp.SelectAbteilung4BAModel;
import de.augustakom.hurrican.model.cc.view.AbstractBauauftragView;
import de.augustakom.hurrican.model.cc.view.VerlaufAbteilungView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AuftragInternService;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog, um die Abteilungen auszuwaehlen, die einen Verlauf erhalten sollen. <br> Die Abteilungen AM und DISPO werden
 * NICHT angezeigt! <br> Die IDs der ausgewaehlten Abteilungen werden beim Schliessen des Dialogs ueber die Methode
 * setValue gespeichert.
 *
 *
 */
public class VerlaufAbtAuswahlDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(VerlaufAbtAuswahlDialog.class);
    private static final long serialVersionUID = -2776412441034253811L;

    private boolean show4Bauauftrag = true;
    private Date realDate = null;
    private Long verlaufId = null;
    private Long auftragId = null;

    private List<Abteilung> abteilungen = null;
    private AKReferenceField rfExternPartner = null;
    private AKJCheckBox chbFfm = null;
    private List<AbtComponents> abtComps = null;

    /**
     * Default-Konstruktor.
     *
     * @param show4Bauauftrag Flag, ob die Auswahl fuer einen Bauauftrag (true) oder fuer eine Projektierung (false)
     *                        erfolgt.
     */
    public VerlaufAbtAuswahlDialog(boolean show4Bauauftrag, Long verlaufId) throws HurricanGUIException {
        super("de/augustakom/hurrican/gui/verlauf/resources/VerlaufAbtAuswahlDialog.xml", false);
        this.show4Bauauftrag = show4Bauauftrag;
        this.verlaufId = verlaufId;
        loadData();
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        try {
            setTitle(getSwingFactory().getText((show4Bauauftrag) ? "ba.title" : "proj.title"));
            configureButton(CMD_SAVE, getSwingFactory().getText("btn.save.text"),
                    getSwingFactory().getText("btn.save.tooltip"), true, true);
            configureButton(CMD_CANCEL, getSwingFactory().getText("btn.cancel.text"),
                    getSwingFactory().getText("btn.cancel.tooltip"), true, true);

            // Abteilungs-Panel erzeugen
            abtComps = new ArrayList<>();
            AKJPanel abtPnl = new AKJPanel(new GridBagLayout());
            int y = 0;

            for (Abteilung abt : abteilungen) {
                if (!NumberTools.isIn(abt.getId(), new Number[] { Abteilung.AM, Abteilung.DISPO, Abteilung.NP })) {
                    NiederlassungService ns = getCCService(NiederlassungService.class);
                    List<Niederlassung> nls = ns.findNL4Abteilung(abt.getId());

                    if (CollectionTools.isEmpty(nls)) {
                        continue;
                    }

                    AKJLabel abtName = new AKJLabel(abt.getName());
                    abtName.setFontStyle(Font.BOLD);
                    abtPnl.add(abtName, GBCFactory.createGBC(50, 0, 0, y, 1, 1, GridBagConstraints.HORIZONTAL));

                    // CheckBoxes erzeugen
                    AbtComponents abtComp = new AbtComponents();
                    abtComp.setNiederlassungen(nls);
                    abtComp.setAbteilungId(abt.getId());

                    List<AKJCheckBox> cb = new ArrayList<>();
                    int x = 0;
                    AKJPanel nlPnl = new AKJPanel(new GridBagLayout());
                    for (Niederlassung nl : abtComp.getNiederlassungen()) {
                        if (NumberTools.isNotIn(abt.getId(), new Number[] { Abteilung.EXTERN, Abteilung.FFM })) {
                            AKJCheckBox cbNL = new AKJCheckBox();
                            cbNL.setToolTipText(getSwingFactory().getText("cb.nl.tooltip", abt.getName()));
                            cbNL.setName(nl.getId().toString());
                            cbNL.setText(nl.getName());
                            cb.add(cbNL);

                            nlPnl.add(cbNL, GBCFactory.createGBC(50, 0, x++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
                        }
                    }

                    abtPnl.add(nlPnl, GBCFactory.createGBC(100, 0, 0, y + 1, 4, 1, GridBagConstraints.HORIZONTAL));
                    abtComp.setCbNl(cb);

                    // ComboBox fuer externen Partner erzeugen
                    if (Abteilung.EXTERN.equals(abt.getId())) {
                        rfExternPartner = getSwingFactory().createReferenceField("ext.service.provider");
                        ISimpleFindService sfs = getCCService(QueryCCService.class);
                        rfExternPartner.setFindService(sfs);

                        abtPnl.add(rfExternPartner, GBCFactory.createGBC(50, 0, 0, y + 1, 1, 1, GridBagConstraints.HORIZONTAL));
                    }
                    else if (Abteilung.FFM.equals(abt.getId())) {
                        chbFfm = getSwingFactory().createCheckBox("ffm.system");
                        abtPnl.add(chbFfm, GBCFactory.createGBC(50, 0, 0, y + 1, 1, 1, GridBagConstraints.HORIZONTAL));
                    }
                    else {
                        abtPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, y, 2, 1, GridBagConstraints.HORIZONTAL));
                    }

                    // DateComponent fuer Realisierungsdatum einfuegen
                    AKJDateComponent dcRealDate4Abt = new AKJDateComponent();
                    dcRealDate4Abt.setToolTipText(getSwingFactory().getText("real.date.tooltip", abt.getName()));
                    dcRealDate4Abt.setColumns(12);
                    if (show4Bauauftrag) {
                        dcRealDate4Abt.setDate(realDate);  // Datum nur bei Bauauftraegen vorbelegen
                    }
                    abtComp.setRealDate(dcRealDate4Abt);
                    abtPnl.add(dcRealDate4Abt, GBCFactory.createGBC(50, 0, 4, y + 1, 1, 1, GridBagConstraints.HORIZONTAL));

                    y = y + 2;
                    abtComps.add(abtComp);
                }
            }

            AKJScrollPane sp = new AKJScrollPane(abtPnl);
            sp.setBorder(BorderFactory.createEmptyBorder());

            getChildPanel().setLayout(new GridBagLayout());
            getChildPanel().add(sp, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 2, 1, 1, GridBagConstraints.BOTH));

            revalidate();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /* Laedt die benoetigten Daten. */
    private void loadData() throws HurricanGUIException {
        try {
            // Verlauf laden
            BAService bas = getCCService(BAService.class);
            NiederlassungService ns = getCCService(NiederlassungService.class);

            if (verlaufId != null) {
                Verlauf ba = bas.findVerlauf(verlaufId);
                if (ba != null) {
                    realDate = ba.getRealisierungstermin();
                    if ((realDate == null) && !show4Bauauftrag) {
                        realDate = DateTools.getHurricanEndDate();
                    }

                    auftragId = ba.getAuftragId();
                }
                else {
                    throw new HurricanGUIException("Konnte Verlauf nicht ermitteln.");
                }
            }

            abteilungen = (show4Bauauftrag) ? ns.findAbteilungen4Ba() : ns.findAbteilungen4Proj();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException(e.getMessage(), e);
        }

        if ((abteilungen == null) || (abteilungen.isEmpty())) {
            throw new HurricanGUIException("Die Abteilungen konnten nicht ermittelt werden.");
        }
    }

    /**
     * Laedt die Daten, die fuer eine Nachverteilung eines Bauauftrags bzw. einer Projektierung benoetigt werden. <br>
     * Es werden die CheckBoxen der Abteilungen markiert, die den Bauauftrag bereits erhalten haben - ausserdem werden
     * diese CheckBoxen disabled.
     *
     * @param verlaufId ID des betroffenen Verlaufs.
     * @throws HurricanGUIException
     */
    protected void loadData4Nachverteilung(Long verlaufId) throws HurricanGUIException {
        try {
            BAService bas = getCCService(BAService.class);
            Verlauf verlauf = bas.findVerlauf(verlaufId);
            if ((verlauf == null) || !BooleanTools.nullToFalse(verlauf.getAkt()) ||
                    (((verlauf.getVerlaufStatusId().intValue() > VerlaufStatus.RUECKLAEUFER_DISPO) &&
                            (verlauf.getVerlaufStatusId().intValue() < VerlaufStatus.KUENDIGUNG_BEI_DISPO)) ||
                            (verlauf.getVerlaufStatusId().intValue() > VerlaufStatus.KUENDIGUNG_RL_DISPO))) {
                throw new HurricanGUIException("Nachverteilung ist in diesem Fall nicht mehr moeglich!");
            }

            List<VerlaufAbteilung> result = bas.findVerlaufAbteilungen(verlaufId);
            if (result != null) {
                for (VerlaufAbteilung va : result) {
                    for (int i = 0; i < abtComps.size(); i++) {
                        if (NumberTools.equal(abtComps.get(i).getAbteilungId(), va.getAbteilungId())) {
                            List<AKJCheckBox> list = abtComps.get(i).getCbNl();
                            for (AKJCheckBox cb : list) {
                                if (StringUtils.equals(cb.getName(), va.getNiederlassungId().toString())) {
                                    cb.setSelected(true);
                                    cb.setEnabled(false);
                                }
                            }

                            if (!BooleanTools.nullToFalse(verlauf.getProjektierung())) {
                                abtComps.get(i).getRealDate().setDate(va.getRealisierungsdatum());
                                abtComps.get(i).getRealDate().setEnabled(false);
                            }

                            if (NumberTools.equal(va.getAbteilungId(), Abteilung.EXTERN)) {
                                rfExternPartner.setReferenceId(va.getExtServiceProviderId());
                                rfExternPartner.setEnabled(false);
                            }

                            if (Abteilung.FFM.equals(va.getAbteilungId())) {
                                chbFfm.setSelected(true);
                                chbFfm.setEnabled(false);
                            }
                        }
                    }
                }
            }
        }
        catch (HurricanGUIException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    /**
     * Laedt die Daten, die fuer eine Terminverschiebung eines Bauauftrags bzw. einer Projektierung benoetigt werden.
     * <br>
     *
     * @param verlaufId ID des betroffenen Verlaufs.
     * @param realDate  Neues Realisierungsdatum
     * @throws HurricanGUIException
     *
     */
    protected void loadData4Terminverschiebung(Long verlaufId, Date realDate) throws HurricanGUIException {
        try {
            BAService bas = getCCService(BAService.class);
            Verlauf verlauf = bas.findVerlauf(verlaufId);
            if ((verlauf == null) || !BooleanTools.nullToFalse(verlauf.getAkt()) ||
                    (((verlauf.getVerlaufStatusId().intValue() > VerlaufStatus.RUECKLAEUFER_DISPO) &&
                            (verlauf.getVerlaufStatusId().intValue() < VerlaufStatus.KUENDIGUNG_BEI_DISPO)) ||
                            (verlauf.getVerlaufStatusId().intValue() > VerlaufStatus.KUENDIGUNG_RL_DISPO))) {
                throw new HurricanGUIException("Terminverschiebung ist in diesem Fall nicht mehr moeglich!");
            }

            // Beschriftung Button und Titel aendern
            setTitle(getSwingFactory().getText("termin.title"));
            configureButton(CMD_SAVE, getSwingFactory().getText("btn.termin.text"),
                    getSwingFactory().getText("btn.termin.tooltip"), true, true);

            // Realisierungsdatum setzen
            this.realDate = realDate;

            rfExternPartner.setEnabled(false);
            chbFfm.setEnabled(false);

            List<VerlaufAbteilung> result = bas.findVerlaufAbteilungen(verlaufId);
            if (CollectionTools.isNotEmpty(result)) {
                for (int i = 0; i < abtComps.size(); i++) {
                    // Deaktiviere Komponenten
                    abtComps.get(i).getRealDate().setEnabled(false);
                    for (AKJCheckBox cb : abtComps.get(i).getCbNl()) {
                        cb.setEnabled(false);
                    }

                    // Suche nach VerlaufAbteilung
                    for (VerlaufAbteilung va : result) {
                        if (NumberTools.equal(abtComps.get(i).getAbteilungId(), va.getAbteilungId())
                                && (va.getDatumErledigt() == null)) {
                            // Aktiviere DateComponent
                            abtComps.get(i).getRealDate().setEnabled(true);
                            abtComps.get(i).getRealDate().setDate(realDate);

                            // Setze Niederlassung
                            List<AKJCheckBox> list = abtComps.get(i).getCbNl();
                            for (AKJCheckBox cb : list) {
                                if (StringUtils.equals(cb.getName(), va.getNiederlassungId().toString())) {
                                    cb.setSelected(true);
                                    cb.setEnabled(true);
                                }
                            }

                            if (Abteilung.EXTERN.equals(va.getAbteilungId())) {
                                rfExternPartner.setReferenceId(va.getExtServiceProviderId());
                                rfExternPartner.setEnabled(true);
                            }

                            if (Abteilung.FFM.equals(va.getAbteilungId())) {
                                chbFfm.setSelected(true);
                                chbFfm.setEnabled(true);
                            }
                        }
                    }
                }
            }
        }
        catch (HurricanGUIException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    /**
     * Ermittelt zu dem aktuellen Verlauf die Verteilungskonfiguration. <br> Sollte fuer die Kombination aus Produkt und
     * Anlass keine Konfiguration gefunden werden, findet keine Vorbelegung statt. Ausnahme: beim Anlass 'Kuendigung'
     * werden alle Abteilungen ermittelt, die zu dem angegebenen Auftrag bereits einen Bauauftrag erhalten hatten.
     *
     * @param view View-Modell mit den wichtigsten Daten des zu verteilenden Verlaufs.
     *
     */
    protected void selectAbteilungen(AbstractBauauftragView view) {
        if (view == null) { return; }
        try {
            BAConfigService bcs = getCCService(BAConfigService.class);
            BAVerlaufConfig config = bcs.findBAVerlaufConfig(view.getAnlassId(), view.getProduktId(), false);
            NiederlassungService ns = getCCService(NiederlassungService.class);

            // Niederlassung zum Auftrag ermitteln
            Niederlassung nl = ns.findNiederlassung4Auftrag(auftragId);
            if (nl == null) {
                return;
            }
            List<VerlaufAbteilungView> abtIds = new ArrayList<>();

            if (config != null) {
                // Abteilungen ueber die Konfiguration ermitteln
                abtIds.addAll(bcs.findAbteilungView4BAVerlaufConfig(config.getAbtConfigId(), nl.getId()));
            }
            else if (NumberTools.equal(view.getAnlassId(), BAVerlaufAnlass.KUENDIGUNG)) {
                try {
                    // alle Abteilungen ermitteln, die an dem Auftrag bereits einmal beteiligt waren.
                    BAService bas = getCCService(BAService.class);
                    List<VerlaufAbteilungView> list = bas.findAffectedAbteilungViews(view.getAuftragId(), true, true, true, true);
                    if (CollectionTools.isNotEmpty(list)) {
                        abtIds.addAll(list);
                    }
                }
                catch (FindException e) {
                    /* nothing to do! Suppress all errors/warnings! */
                    if (LOGGER.isDebugEnabled()) { LOGGER.debug(e.getMessage()); }
                }
            }

            if (auftragId != null) {
                // Pruefe interne Auftraege
                AuftragInternService ais = getCCService(AuftragInternService.class);
                AuftragIntern ai = ais.findByAuftragId(auftragId);

                if ((ai != null) && (ai.getExtServiceProviderId() != null)) {
                    VerlaufAbteilungView abtView = new VerlaufAbteilungView();
                    abtView.setAbtId(Abteilung.EXTERN);
                    abtView.setNlId(nl.getId());
                    abtIds.add(abtView);

                    rfExternPartner.setReferenceId(ai.getExtServiceProviderId());
                }

                // CheckBoxes markieren
                if (CollectionTools.isNotEmpty(abtIds)) {
                    for (VerlaufAbteilungView abtId : abtIds) {
                        for (int i = 0; i < abtComps.size(); i++) {
                            if (NumberTools.equal(abtId.getAbtId(), abtComps.get(i).getAbteilungId())) {
                                for (AKJCheckBox cb : abtComps.get(i).getCbNl()) {
                                    if (StringUtils.equals(cb.getName(), abtId.getNlId().toString())) {
                                        cb.setSelected(true);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        List<SelectAbteilung4BAModel> result = new ArrayList<>();
        for (int i = 0; i < abtComps.size(); i++) {
            AbtComponents comp = abtComps.get(i);
            if ((comp != null) && CollectionTools.isNotEmpty(comp.getCbNl())) {
                for (AKJCheckBox cb : comp.getCbNl()) {
                    if (cb.isEnabled() && cb.isSelected()) {
                        // Rueckgabe-Wert erzeugen
                        SelectAbteilung4BAModel res = new SelectAbteilung4BAModel();

                        // Abteilungs-ID
                        res.setAbteilungId(comp.getAbteilungId());
                        if (res.getAbteilungId() == null) {
                            MessageHelper.showInfoDialog(getParent(), "Abteilung kann nicht ermittelt werden.");
                            return;
                        }

                        checkAndSetRealDate(comp, res);

                        //  Niederlassung setzen
                        res.setNiederlassungId(Long.valueOf(cb.getName()));
                        if (res.getNiederlassungId() == null) {
                            MessageHelper.showInfoDialog(getParent(), "Niederlassung ist nicht angegeben.");
                            return;
                        }

                        result.add(res);
                    }
                }
            }
            else if (comp != null) {
                // Rueckgabe-Wert erzeugen
                SelectAbteilung4BAModel res = new SelectAbteilung4BAModel();
                res.setAbteilungId(comp.abteilungId);
                checkAndSetRealDate(comp, res);

                loadNiederlassungFromAuftrag(res);

                if (Abteilung.EXTERN.equals(comp.abteilungId) && rfExternPartner.isEnabled()) {
                    // ID des externen Partner
                    Object obj = rfExternPartner.getReferenceObject();
                    if ((obj != null) && (obj instanceof ExtServiceProvider)) {
                        ExtServiceProvider extPartner = (ExtServiceProvider) obj;
                        res.setExtServiceProviderId(extPartner.getId());
                        result.add(res);
                    }
                }
                else if (Abteilung.FFM.equals(comp.abteilungId) && chbFfm.isEnabled() && chbFfm.isSelected()) {
                    result.add(res);
                }
            }
        }

        prepare4Close();
        setValue((!result.isEmpty()) ? result : null);
    }

    private void loadNiederlassungFromAuftrag(SelectAbteilung4BAModel res) {
        try {
            NiederlassungService ns = getCCService(NiederlassungService.class);
            Niederlassung nl = ns.findNiederlassung4Auftrag(auftragId);
            if (nl != null) {
                res.setNiederlassungId(nl.getMappedFallbackOrId());
            }
            else {
                MessageHelper.showInfoDialog(getParent(), "Niederlassung des Auftrags kann nicht ermittelt werden.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getParent(), e);
        }
    }

    /* Setzt das Realisierungsdatum fuer die Abteilung */
    private void checkAndSetRealDate(AbtComponents comp, SelectAbteilung4BAModel res) {
        // Realisierungsdatum
        Date date = comp.getRealDate().getDate(null);
        if (date != null) {
            // Check
            if (DateTools.isDateBefore(date, new Date()) || DateTools.isDateAfter(date, realDate)) {
                MessageHelper.showInfoDialog(getParent(),
                        "Realisierungsdatum muss groesser/gleich heute und kleiner/gleich dem Gesamt-Realisierungsdatum sein.", null, true);
            }
            else {
                res.setRealDate(date);
            }
        }
}

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * Hilfsklasse *
     */
    static class AbtComponents {
        private List<Niederlassung> niederlassungen = null;
        private List<AKJCheckBox> cbNl = null;
        private AKJDateComponent realDate = null;
        private Long abteilungId = null;

        public List<Niederlassung> getNiederlassungen() {
            return niederlassungen;
        }

        public void setNiederlassungen(List<Niederlassung> niederlassungen) {
            this.niederlassungen = niederlassungen;
        }

        public List<AKJCheckBox> getCbNl() {
            return cbNl;
        }

        public void setCbNl(List<AKJCheckBox> cbNl) {
            this.cbNl = cbNl;
        }

        public AKJDateComponent getRealDate() {
            return realDate;
        }

        public void setRealDate(AKJDateComponent realDate) {
            this.realDate = realDate;
        }

        public Long getAbteilungId() {
            return abteilungId;
        }

        public void setAbteilungId(Long abteilungId) {
            this.abteilungId = abteilungId;
        }

    }
}


