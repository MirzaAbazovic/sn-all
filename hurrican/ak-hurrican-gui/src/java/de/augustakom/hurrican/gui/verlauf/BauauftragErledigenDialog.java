/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.01.2005 15:26:00
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Dialog zur Abfrage der Abschluss-Daten fuer einen Bauauftrag. <br> <br> Wird der Dialog ueber 'OK' geschlossen,
 * koennen ueber die Methoden getBearbeiter() und getRealDate() die eingegebenen Daten abgefragt werden.
 *
 *
 */
public class BauauftragErledigenDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent,
        ActionListener {

    private static final Logger LOGGER = Logger.getLogger(BauauftragErledigenDialog.class);

    private static final String RESOURCE =
            "de/augustakom/hurrican/gui/verlauf/resources/BauauftragErledigenDialog.xml";
    private static final long serialVersionUID = -6800577186426733829L;

    private final VerlaufAbteilung verlaufAbteilung;
    private Verlauf verlauf = null;
    private final AKUser akUser;
    private final Date realDate;
    private final boolean showBemerkung;

    private AKJDateComponent dcRealDate = null;
    private AKJComboBox cbBearbeiter = null;
    private AKJCheckBox chbNotPossible = null;
    private AKReferenceField rfNotPossibleReason = null;
    private AKJTextArea taBemerkung = null;  // Bug-ID 11462
    private PhysikRueckmeldungPanel zusatzPanel = null;

    /**
     * Konstruktor mit Angabe eines Realisierung-Termins und der Abteilungs-ID.
     *
     * @param verlaufAbteilung Verlauf-Abteilungs-Datensatzes, der abgeschlossen werden soll (never {@code null})
     * @param akUser           aktueller User
     * @param realDate         Realisierungsdatum zur Vorgabe
     * @param showBemerkung    Angabe, ob das Bemerkungsfeld angezeigt werden soll.
     * @throws IllegalArgumentException
     */
    public BauauftragErledigenDialog(VerlaufAbteilung verlaufAbteilung, AKUser akUser, Date realDate,
            boolean showBemerkung) {
        super(RESOURCE);

        if (verlaufAbteilung == null) {
            throw new IllegalArgumentException("Ungueltige Parameter fuer Dialog " + this.getClass().getName());
        }

        this.verlaufAbteilung = verlaufAbteilung;
        this.akUser = akUser;
        this.realDate = (realDate != null) ? new Date(realDate.getTime()) : null;
        this.showBemerkung = showBemerkung;

        createGUI();
        loadData();
    }

    /**
     * Konstruktor mit Angabe eines Realisierung-Termins und der Abteilungs-ID.
     *
     * @param verlaufAbteilung Verlauf-Abteilungs-Datensatzes, der abgeschlossen werden soll (never {@code null})
     * @param akUser           aktueller User
     * @param realDate         Realisierungsdatum zur Vorgabe
     * @throws IllegalArgumentException
     */
    public BauauftragErledigenDialog(VerlaufAbteilung verlaufAbteilung, AKUser akUser, Date realDate) {
        this(verlaufAbteilung, akUser, realDate, true);
    }

    @Override
    protected final void createGUI() {
        setTitle("Abschlussdaten");

        AKJLabel lblBearbeiter = getSwingFactory().createLabel("bearbeiter");
        AKJLabel lblRealDate = getSwingFactory().createLabel("realisierungstermin");
        AKJLabel lblNotPossible = getSwingFactory().createLabel("not.possible");
        AKJLabel lblNotPossibleReason = getSwingFactory().createLabel("not.possible.reason");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");

        dcRealDate = getSwingFactory().createDateComponent("realisierungstermin");
        dcRealDate.setDate(realDate);
        cbBearbeiter = getSwingFactory().createComboBox("bearbeiter");
        cbBearbeiter.setEditable(true);
        chbNotPossible = getSwingFactory().createCheckBox("not.possible", this, false);
        rfNotPossibleReason = getSwingFactory().createReferenceField("not.possible.reason");
        rfNotPossibleReason.setEnabled(false);
        taBemerkung = getSwingFactory().createTextArea("bemerkung");
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung);

        zusatzPanel = getZusatzPanel();

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblBearbeiter, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        child.add(cbBearbeiter, GBCFactory.createGBC(20, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblRealDate, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(dcRealDate, GBCFactory.createGBC(20, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblNotPossible, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(chbNotPossible, GBCFactory.createGBC(20, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblNotPossibleReason, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(rfNotPossibleReason, GBCFactory.createGBC(20, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        int y = 4;
        if (showBemerkung) {
            child.add(lblBemerkung, GBCFactory.createGBC(0, 0, 1, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
            child.add(spBemerkung, GBCFactory.createGBC(20, 20, 3, y, 1, 2, GridBagConstraints.BOTH));
            y++;
        }
        if (zusatzPanel != null) {
            child.add(zusatzPanel, GBCFactory.createGBC(100, 0, 0, ++y, 4, 1, GridBagConstraints.HORIZONTAL));
        }
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, ++y, 1, 1, GridBagConstraints.VERTICAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, ++y, 1, 1, GridBagConstraints.BOTH));
    }

    /* Ermittelt abhaengig vom Anschlusstyp, ob ein Zusatz-Panel benoetigt wird */
    private PhysikRueckmeldungPanel getZusatzPanel() {
        try {
            if (verlaufAbteilung == null) {
                return null;
            }
            // Ermittle Produkt
            BAService baService = getCCService(BAService.class);
            Verlauf verlauf = baService.findVerlauf(verlaufAbteilung.getVerlaufId());
            if ((verlauf != null) && (verlauf.getAuftragId() != null)) {
                EndstellenService endstellenService = getCCService(EndstellenService.class);
                Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(verlauf.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);

                if (NumberTools.isIn(verlauf.getAnlass(), new Number[] { BAVerlaufAnlass.NEUSCHALTUNG, BAVerlaufAnlass.ABW_TKG46_NEUSCHALTUNG })
                        && (NumberTools.equal(verlaufAbteilung.getAbteilungId(), Abteilung.EXTERN)
                        || NumberTools.equal(verlaufAbteilung.getAbteilungId(), Abteilung.FIELD_SERVICE))
                        && (endstelleB != null)
                        && (endstelleB.getHvtIdStandort() != null)) {
                    HVTService hvtService = getCCService(HVTService.class);
                    HVTStandort hvt = hvtService.findHVTStandort(endstelleB.getHvtIdStandort());
                    if (hvt != null && hvt.isFtthOrFttb() && NumberTools.isIn(hvt.getStandortTypRefId(),
                            new Long[] { HVTStandort.HVT_STANDORT_TYP_FTTH, HVTStandort.HVT_STANDORT_TYP_FTTB,
                                    HVTStandort.HVT_STANDORT_TYP_FTTB_H
                            })) {
                        return new PhysikRueckmeldungPanel(hvt.getStandortTypRefId());

                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            AKUserService userService = getAuthenticationService(
                    AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
            List<AKUser> users = userService.findByHurricanAbteilungId(verlaufAbteilung.getAbteilungId());
            List<String> users2Add = new ArrayList<>();
            boolean bearbeiterFound = false;
            if (users != null) {
                for (AKUser user : users) {
                    users2Add.add(user.getLoginName());
                    if (!bearbeiterFound) {
                        bearbeiterFound = StringUtils.equalsIgnoreCase(user.getLoginName(), akUser.getLoginName());
                    }
                }
            }

            if (!bearbeiterFound && (akUser.getLoginName() != null)) {
                users2Add.add(0, akUser.getLoginName());
            }

            cbBearbeiter.addItems(users2Add, true, String.class);
            cbBearbeiter.selectItem("toString", String.class, akUser.getLoginName());
            taBemerkung.setText(verlaufAbteilung.getBemerkung());

            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfNotPossibleReason.setFindService(sfs);

            ReferenceService refService = getCCService(ReferenceService.class);
            List<Reference> reasonRefs = refService.findReferencesByType(Reference.REF_TYPE_VERLAUF_REASONS, Boolean.TRUE);
            rfNotPossibleReason.setReferenceList(reasonRefs);

            BAService bas = getCCService(BAService.class);
            verlauf = bas.findVerlauf(verlaufAbteilung.getVerlaufId());
            if (BooleanTools.nullToFalse(verlauf.getProjektierung())) {
                // bei Projektierungen werden die Felder Bearbeiter u. RealDate deaktiviert
                cbBearbeiter.setEnabled(false);
                dcRealDate.setEnabled(false);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * Gibt den Benutzer-Namen zurueck.
     */
    protected String getBearbeiter() {
        return (cbBearbeiter.getSelectedItem() instanceof String) ? (String) cbBearbeiter.getSelectedItem() : null;
    }

    /**
     * Gibt das Realisierungsdatum zurueck.
     */
    protected Date getRealisierungstermin() {
        return dcRealDate.getDate(null);
    }

    /**
     * Gibt die Bemerkung zu dem BA zurueck.
     *
     * @return
     */
    protected String getBemerkung() {
        return (showBemerkung) ? taBemerkung.getText() : null;
    }

    /**
     * Gibt an, ob der Verlauf als 'nicht realisierbar' markiert wurde.
     *
     * @return
     */
    protected Boolean isNotPossible() {
        return chbNotPossible.isSelectedBoolean();
    }

    /**
     * Gibt den definierten Grund fuer eine Nicht-Realisierbarkeit zurueck. <br> Dies geschieht jedoch nur, wenn das
     * Flag 'NotPossible' gesetzt wurde.
     *
     * @return
     */
    protected Long getNotPossibleReason() {
        if (BooleanTools.nullToFalse(isNotPossible())) {
            return rfNotPossibleReason.getReferenceIdAs(Long.class);
        }
        return null;
    }

    /**
     * Gibt einen Zusatzaufwand zum BA zurueck.
     *
     * @return
     */
    protected Long getZusatzAufwand4Fttx() {
        if (zusatzPanel != null) {
            return zusatzPanel.getZusatzAufwand();
        }
        return null;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        if (!BooleanTools.nullToFalse(verlauf.getProjektierung())) {
            if (StringUtils.isBlank(getBearbeiter())) {
                MessageHelper.showInfoDialog(this, "Bitte den Bearbeiter eintragen.", null, true);
                return;
            }

            Date date = dcRealDate.getDate(null);
            if ((date == null) || DateTools.isDateAfter(date, new Date())) {
                MessageHelper.showInfoDialog(this, "Bitte ein gültiges Realisierungsdatum eintragen.", null, true);
                return;
            }
        }

        prepare4Close();
        setValue(OK_OPTION);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chbNotPossible) {
            rfNotPossibleReason.setEnabled(chbNotPossible.isSelected());
        }
    }

}
